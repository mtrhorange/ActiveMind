package com.example.activemind;

public class BarChartData {
    String date;
    int score;

    public BarChartData(String date, int score) {
        this.date = date;
        this.score = score;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String data) {
        this.date = date;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
