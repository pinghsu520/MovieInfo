/*
 * @author: Mario Verdugo. Ping Hsu, Nathon Smith
 * @description: This is a wrapper class for each movie object.
 * It holds all the information that is significant to the app.
 */

package com.example.myapplication;

import android.graphics.Bitmap;

public class Movie {
    private String title = "";
    private double popularity;
    private String releaseDate;
    private String overview = "";
    private  int voteAverage;
    private Bitmap poster;
    private String id = "";


    /**
     * gets the id of the movie
     * @return the id of the moive
     */
    public String getId(){return id;}

    /**
     * sets the id of the movie.
     * @param newId the id to set it to.
     */
    public void setId(String newId) {this.id = newId;}

    /**
     * gets the title of the movie
     * @return the title of the movie
     */
    public String getTitle() {
        return title;
    }

    /**
     * sets the title of the movie.
     * @param title the title to set it to.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * gets the popularity of the movie.
     * @return the popularity.
     */
    public double getPopularity() {
        return popularity;
    }


    /**
     * gets the average of the movie.
     * @return the average.
     */
    public int getVoteAverage() {
        return voteAverage;
    }

    /**
     * sets the average of the movie.
     * @param voteAverage the new average to set it to.
     */
    public void setVoteAverage(int voteAverage) {
        this.voteAverage = voteAverage;
    }

    /**
     * sets the popularity of the movie.
     * @param popularity the popularity to set it to.
     */
    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    /**
     * sets the poster of the movie
     * @param image the bitmap to set it to.
     */
    public void setPoster(Bitmap image){
        poster = image;
    }

    /**
     * gets the poster of the movie.
     * @return the bitmap of the poster.
     */
    public Bitmap getPoster(){
        return poster;
    }

    /**
     * gets the overview of the movie.
     * @return the string of the overview.
     */

    public String getOverview() {
        return overview;
    }

    /**
     * sets the overview of the movie.
     * @param overview the new overview.
     */
    public void setOverview(String overview) {
        this.overview = overview;
    }

    /**
     * gets the release date of the movie.
     * @return the string of the release date.
     */
    public String getReleaseDate() {
        return releaseDate;
    }

    /**
     * sets the release date of the movie.
     * @param releaseDate the release date to set it to.
     */
    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
}