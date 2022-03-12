package com.example.activemind;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

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

import java.util.ArrayList;

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

    public void getScore(){
        barChartData.add(new BarChartData("07-Mar",10));
        barChartData.add(new BarChartData("06-Mar",30));
        barChartData.add(new BarChartData("05-Mar",50));
        barChartData.add(new BarChartData("04-Mar",70));
        barChartData.add(new BarChartData("03-Mar",100));
        barChartData.add(new BarChartData("02-Mar",95));
        barChartData.add(new BarChartData("01-Mar",100));
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
        getScore();

        for (int i = 0; i < 7; i++){
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
}