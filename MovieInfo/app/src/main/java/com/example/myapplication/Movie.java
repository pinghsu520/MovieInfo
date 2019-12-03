package com.example.myapplication;

import android.graphics.Bitmap;

public class Movie {


    private String title;
    private double popularity;
    private String releaseDate;
    private String overview;
    private  int voteAverage;
    private Bitmap poster;
    private String id;


    public String getId(){return id;}

    public void setId(String newId) {this.id = newId;}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPopularity() {
        return popularity;
    }


    public int getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(int voteAverage) {
        this.voteAverage = voteAverage;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public void setPoster(Bitmap image){
        poster = image;
    }

    public Bitmap getPoster(){
        return poster;
    }


    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }




}