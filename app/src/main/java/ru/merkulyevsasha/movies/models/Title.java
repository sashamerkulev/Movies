package ru.merkulyevsasha.movies.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Title {

    @SerializedName("title")
    @Expose
    public String title;

    @SerializedName("iso_3166_1")
    @Expose
    public String iso31661;

}
