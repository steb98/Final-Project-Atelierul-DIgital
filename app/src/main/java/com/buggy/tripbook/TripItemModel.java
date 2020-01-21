package com.buggy.tripbook;

import android.net.Uri;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
@Entity(tableName = "tripsTable")
public class TripItemModel {
    @SerializedName("id")
    @Expose
    @PrimaryKey
    private int id;

    @SerializedName("title")
    @Expose
    @ColumnInfo(name="title")
    private String tripTitle;

    @SerializedName("destination")
    @Expose
    @ColumnInfo(name="destination")
    private String tripDestination;

    private Uri tripImage;

    @SerializedName("fav")
    @Expose
    @ColumnInfo(name="fav")
    private int tripFav;

    @SerializedName("type")
    @Expose
    @ColumnInfo(name="type")
    private String tripType;

    @SerializedName("startYear")
    @Expose
    @ColumnInfo(name="startYear")
    private int tripStartYear;

    @SerializedName("startMonth")
    @Expose
    @ColumnInfo(name="startMonth")
    private int tripStartMonth;

    @SerializedName("startDay")
    @Expose
    @ColumnInfo(name="startDay")
    private int tripStartDay;

    @SerializedName("endYear")
    @Expose
    @ColumnInfo(name="endYear")
    private int tripEndYear;

    @SerializedName("endMonth")
    @Expose
    @ColumnInfo(name="endMonth")
    private int tripEndMonth;

    @SerializedName("endDay")
    @Expose
    @ColumnInfo(name="endDay")
    private int tripEndDay;

    @SerializedName("price")
    @Expose
    @ColumnInfo(name="price")
    private int tripPrice;

    @SerializedName("rating")
    @Expose
    @ColumnInfo(name="rating")
    private float tripRating;

    public TripItemModel(int id,Uri tripImage,String tripTitle, String tripDestination,int tripPrice, float tripRating, int tripFav, String tripType, int tripStartYear, int tripStartMonth, int tripStartDay, int tripEndYear, int tripEndMonth, int tripEndDay) {
        this.id=id;
        this.tripTitle = tripTitle;
        this.tripDestination = tripDestination;
        this.tripImage = tripImage;
        this.tripPrice = tripPrice;
        this.tripRating = tripRating;
        this.tripFav = tripFav;
        this.tripType = tripType;
        this.tripStartYear = tripStartYear;
        this.tripStartMonth = tripStartMonth;
        this.tripStartDay = tripStartDay;
        this.tripEndYear = tripEndYear;
        this.tripEndMonth = tripEndMonth;
        this.tripEndDay = tripEndDay;


    }




    //Getters and setters down below
    //Nothing else


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTripTitle(String tripTitle) {
        this.tripTitle = tripTitle;
    }

    public void setTripDestination(String tripDestination) {
        this.tripDestination = tripDestination;
    }

    public void setTripImage(Uri tripImage) {
        this.tripImage = tripImage;
    }

    public void setTripFav(int tripFav) {
        this.tripFav = tripFav;
    }

    public void setTripType(String tripType) {
        this.tripType = tripType;
    }

    public void setTripStartYear(int tripStartYear) {
        this.tripStartYear = tripStartYear;
    }

    public void setTripStartMonth(int tripStartMonth) {
        this.tripStartMonth = tripStartMonth;
    }

    public void setTripStartDay(int tripStartDay) {
        this.tripStartDay = tripStartDay;
    }

    public void setTripEndYear(int tripEndYear) {
        this.tripEndYear = tripEndYear;
    }

    public void setTripEndMonth(int tripEndMonth) {
        this.tripEndMonth = tripEndMonth;
    }

    public void setTripEndDay(int tripEndDay) {
        this.tripEndDay = tripEndDay;
    }

    public void setTripPrice(int tripPrice) {
        this.tripPrice = tripPrice;
    }

    public void setTripRating(float tripRating) {
        this.tripRating = tripRating;
    }

    public String getTripTitle() {
        return tripTitle;
    }

    public String getTripDestination() {
        return tripDestination;
    }

    public Uri getTripImage() {
        return tripImage;
    }

    public int getTripFav() {
        return tripFav;
    }

    public String getTripType() {
        return tripType;
    }

    public int getTripStartYear() {
        return tripStartYear;
    }

    public int getTripStartMonth() {
        return tripStartMonth;
    }

    public int getTripStartDay() {
        return tripStartDay;
    }

    public int getTripEndYear() {
        return tripEndYear;
    }

    public int getTripEndMonth() {
        return tripEndMonth;
    }

    public int getTripEndDay() {
        return tripEndDay;
    }

    public int getTripPrice() {
        return tripPrice;
    }

    public float getTripRating() {
        return tripRating;
    }
}
