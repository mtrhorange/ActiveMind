package com.example.activemind;

public class BarChartData implements Comparable<BarChartData> {
    private String date;
    private int score;
    private short id;


    public BarChartData(String date, int score, short id) {
        this.date = date;
        this.score = score;
        this.id = id;
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

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public int compareTo(BarChartData other){
        return Short.compare(other.id, this.id);
    }
}
