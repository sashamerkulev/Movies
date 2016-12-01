package ru.merkulyevsasha.movies.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Crew {

    @SerializedName("credit_id")
    @Expose
    public String creditId;

    @SerializedName("department")
    @Expose
    public String department;

    @SerializedName("id")
    @Expose
    public int id;

    @SerializedName("job")
    @Expose
    public String job;

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("profile_path")
    @Expose
    public String profilePath;

}

