package ru.merkulyevsasha.movies.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Cast {

    @SerializedName("cast_id")
    @Expose
    public int castId;

    @SerializedName("character")
    @Expose
    public String character;

    @SerializedName("credit_id")
    @Expose
    public String creditId;

    @SerializedName("id")
    @Expose
    public int id;

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("order")
    @Expose
    public int order;

    @SerializedName("profile_path")
    @Expose
    public String profilePath;

}
