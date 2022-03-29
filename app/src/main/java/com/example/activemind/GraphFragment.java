package com.example.activemind;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.activemind.databinding.FragmentGraphBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class GraphFragment extends Fragment {

    public GraphFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    FragmentGraphBinding binding;
    private Button numberMemoryGameBtn;
    private Button sequenceMemoryGameBtn;
    private Button wordMemoryGameBtn;
    private Button leftBtn, rightBtn;
    private TextView gameName;
    private ConstraintLayout loginLayout, logoutLayout;

    private FirebaseUser fbU;

    private BarChart barChart;
    private ArrayList<BarEntry> barEntryArrayList = new ArrayList<>();
    private ArrayList<String> labelNames;

    private ArrayList<BarChartData> barChartData = new ArrayList<>();

    private ArrayList<String> gameList = new ArrayList<String>();
    private int currentGame = 0;
    private int totalgame = gameList.size();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentGraphBinding.inflate(inflater, container, false);
        loginLayout = binding.LoginLayout;
        logoutLayout = binding.LogoutLayout;
        leftBtn = binding.leftBtn;
        rightBtn = binding.rightBtn;
        gameName = binding.GameName;
        numberMemoryGameBtn = binding.numberMemoryGameBtn;
        sequenceMemoryGameBtn = binding.sequenceMemoryGameBtn;
        wordMemoryGameBtn = binding.wordMemoryGameBtn;
        barChart = binding.barChart.findViewById(R.id.barChart);
        // Inflate the layout for this fragment

        //check if user is logged in
        fbU = FirebaseAuth.getInstance().getCurrentUser();

        gameList.add("NumberMemory");
        gameList.add("SequenceMemory");
        gameList.add("WordMemory");

        if (fbU != null) {
            logoutLayout.setVisibility(View.GONE);
            loginLayout.setVisibility(View.VISIBLE);
            barEntryArrayList = new ArrayList<>();
            labelNames = new ArrayList<>();
            gameName.setText(gameList.get(currentGame));
            getGameScores(gameList.get(currentGame));

            leftBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(currentGame == 0){
                        currentGame = totalgame;
                    }else{
                        currentGame --;
                    }
                    gameName.setText(gameList.get(currentGame));
                    getGameScores(gameList.get(currentGame));
                }
            });

            rightBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(currentGame == totalgame-1){
                        currentGame = 0;
                    }else{
                        currentGame ++;
                    }
                    gameName.setText(gameList.get(currentGame));
                    getGameScores(gameList.get(currentGame));
                }
            });

            numberMemoryGameBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getGameScores("NumberMemory");
                }
            });

            sequenceMemoryGameBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getGameScores("SequenceMemory");
                }
            });

            wordMemoryGameBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getGameScores("WordMemory");
                }
            });
        }
        //prompt user to log in to be able to view
        else {
            logoutLayout.setVisibility(View.VISIBLE);
            loginLayout.setVisibility(View.GONE);
//            Toast.makeText(getContext(), "Log in to view scores", Toast.LENGTH_SHORT).show();
        }

        return binding.getRoot();
    }

    /**
     * Gets and sets the highest score for the last 7 days for game
     * @param gameName name of the game to pull data for
     */
    private void getGameScores(String gameName) {
        if (fbU != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference colRef = db.collection("Users").document(fbU.getUid().toString()).collection(gameName);

            ZonedDateTime now = ZonedDateTime.now();
            ZonedDateTime sgDateTime = now.withZoneSameInstant(ZoneId.of("Asia/Shanghai"));

            barChartData.clear();

            for (short days = 0; days < 7; days++) {
                final ZonedDateTime offsetSgDateTime = sgDateTime.minusDays(days);
                final short id = days;

                Query queryColRef = colRef.whereEqualTo("Year", offsetSgDateTime.getYear());
                queryColRef = queryColRef.whereEqualTo("Month", offsetSgDateTime.getMonthValue());
                queryColRef = queryColRef.whereEqualTo("Day", offsetSgDateTime.getDayOfMonth());

                queryColRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        String dateStr = (offsetSgDateTime.getDayOfMonth() + "-" + offsetSgDateTime.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
                        int score = 0;
                        // if there is a record present for the date
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                score = Math.toIntExact((task.getResult().getDocuments()).get(0).getLong("HighScore"));
                            }
                            // no record present for the date, set 0
                        }
                        // can't read database, set 0

                        //add barchart data
                        barChartData.add(new BarChartData(dateStr, score, id));
                        refreshBarChart();
                    }
                });
            }
        }
    }

    /**
     * Updates UI of the BarChart
     */
    private void refreshBarChart() {

        barEntryArrayList.clear();
        labelNames.clear();
        Collections.sort(barChartData);

        for (int i = 0; i < barChartData.size(); i++) {
            String date = barChartData.get(i).getDate();
            int score = barChartData.get(i).getScore();
            barEntryArrayList.add(new BarEntry(i,score));
            labelNames.add(date);
        }

        BarDataSet barDataSet = new BarDataSet(barEntryArrayList, "Game Scores");
        barDataSet.setColors(ColorTemplate.rgb("673AB7"));
        barDataSet.setValueTextSize(12);
        barDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) Math.floor(value));
            }
        });
        Description description = new Description();
        description.setText("");
        barChart.setDescription(description);
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        barChart.getAxisLeft().setStartAtZero(true);
//        barChart.getAxisLeft().setAxisMaximum(100);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labelNames));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(labelNames.size());
        xAxis.setTextSize(12);

        barChart.animateY(1000);
        barChart.invalidate();
    }
}