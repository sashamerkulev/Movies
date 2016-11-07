package ru.merkulyevsasha.movies.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Details {

    @SerializedName("adult")
    @Expose
    public boolean adult;
    @SerializedName("backdrop_path")
    @Expose
    public String backdropPath;
    @SerializedName("belongs_to_collection")
    @Expose
    public Series belongsToCollection;
    @SerializedName("budget")
    @Expose
    public double budget;
    @SerializedName("genres")
    @Expose
    public List<Dict> genres;
    @SerializedName("homepage")
    @Expose
    public String homepage;
    @SerializedName("id")
    @Expose
    public int id;
    @SerializedName("imdb_id")
    @Expose
    public String imdbId;
    @SerializedName("original_language")
    @Expose
    public String originalLanguage;
    @SerializedName("original_title")
    @Expose
    public String originalTitle;
    @SerializedName("overview")
    @Expose
    public String overview;
    @SerializedName("popularity")
    @Expose
    public double popularity;
    @SerializedName("poster_path")
    @Expose
    public String posterPath;
    @SerializedName("production_companies")
    @Expose
    public List<Dict> productionCompanies;
    @SerializedName("production_countries")
    @Expose
    public List<Dict> productionCountries;
    @SerializedName("release_date")
    @Expose
    public String releaseDate;
    @SerializedName("revenue")
    @Expose
    public double revenue;
    @SerializedName("runtime")
    @Expose
    public int runtime;
    @SerializedName("spoken_languages")
    @Expose
    public List<Dict> spokenLanguages;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("tagline")
    @Expose
    public String tagline;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("video")
    @Expose
    public boolean video;
    @SerializedName("vote_average")
    @Expose
    public String voteAverage;
    @SerializedName("vote_count")
    @Expose
    public int voteCount;

}
