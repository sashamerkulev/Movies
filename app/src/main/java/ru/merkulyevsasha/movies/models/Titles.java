package ru.merkulyevsasha.movies.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class Titles {

    @SerializedName("id")
    @Expose
    public int id;

    @SerializedName("titles")
    @Expose
    public List<Title> titles;

}


