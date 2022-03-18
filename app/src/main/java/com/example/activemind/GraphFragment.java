package com.example.activemind;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.example.activemind.databinding.FragmentGraphBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
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

    BarChart barChart;
    ArrayList<BarEntry> barEntryArrayList = new ArrayList<>();
    ArrayList<String> labelNames;

    ArrayList<BarChartData> barChartData = new ArrayList<>();

    /**
     * Gets and sets the highest score for the last 7 days for game
     * @param gameName name of the game to pull data for
     */
    private void getNumberMemoryScore(String gameName) {
        //check if user is logged in
        FirebaseUser fbU = FirebaseAuth.getInstance().getCurrentUser();
        if (fbU != null) {
            System.out.println("Game name: " + gameName);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference colRef = db.collection("Users").document(fbU.getUid().toString()).collection(gameName);

            ZonedDateTime now = ZonedDateTime.now();
            ZonedDateTime sgDateTime = now.withZoneSameInstant(ZoneId.of("Asia/Shanghai"));

            barChartData.clear();

            for (short days = 0; days < 7; days ++) {
                final ZonedDateTime offsetSgDateTime = sgDateTime.minusDays(days);
                final short id = days;

                Query queryColRef = colRef.whereEqualTo("Year", offsetSgDateTime.getYear());;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentGraphBinding.inflate(inflater, container, false);
        numberMemoryGameBtn = binding.numberMemoryGameBtn;
        sequenceMemoryGameBtn = binding.sequenceMemoryGameBtn;
        wordMemoryGameBtn = binding.wordMemoryGameBtn;
        barChart = binding.barChart.findViewById(R.id.barChart);
        // Inflate the layout for this fragment

        barEntryArrayList = new ArrayList<>();
        labelNames = new ArrayList<>();

        getNumberMemoryScore("NumberMemory");

        numberMemoryGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        sequenceMemoryGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        wordMemoryGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return binding.getRoot();
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
        Description description = new Description();
        description.setText("");
        barChart.setDescription(description);
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        barChart.getAxisLeft().setStartAtZero(true);

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