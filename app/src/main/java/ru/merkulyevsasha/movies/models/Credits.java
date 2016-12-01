package ru.merkulyevsasha.movies.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class Credits {

    @SerializedName("id")
    @Expose
    public int id;

    @SerializedName("cast")
    @Expose
    public List<Cast> casts;

}
