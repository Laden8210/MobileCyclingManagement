package com.example.mobilecyclingmanagement.model;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;


public class Route implements Parcelable {
    private String description;
    private Timestamp datePosted;

    private String startingName;
    private String endingName;
    private Coordination startingCoordination;

    private Coordination endingCoordination;

    private String userId;
    private String routeId;

    public Route() {
    }

    protected Route(Parcel in) {
        description = in.readString();
        datePosted = in.readParcelable(Timestamp.class.getClassLoader());
        startingName = in.readString();
        endingName = in.readString();
        startingCoordination = in.readParcelable(Coordination.class.getClassLoader());
        endingCoordination = in.readParcelable(Coordination.class.getClassLoader());
        userId = in.readString();
        routeId = in.readString();
    }

    public static final Creator<Route> CREATOR = new Creator<Route>() {
        @Override
        public Route createFromParcel(Parcel in) {
            return new Route(in);
        }

        @Override
        public Route[] newArray(int size) {
            return new Route[size];
        }
    };

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(Timestamp datePosted) {
        this.datePosted = datePosted;
    }

    public String getStartingName() {
        return startingName;
    }

    public void setStartingName(String startingName) {
        this.startingName = startingName;
    }

    public String getEndingName() {
        return endingName;
    }

    public void setEndingName(String endingName) {
        this.endingName = endingName;
    }

    public Coordination getStartingCoordination() {
        return startingCoordination;
    }

    public void setStartingCoordination(Coordination startingCoordination) {
        this.startingCoordination = startingCoordination;
    }

    public Coordination getEndingCoordination() {
        return endingCoordination;
    }

    public void setEndingCoordination(Coordination endingCoordination) {
        this.endingCoordination = endingCoordination;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(description);
        dest.writeParcelable(datePosted, flags);
        dest.writeString(startingName);
        dest.writeString(endingName);
        dest.writeParcelable(startingCoordination, flags);
        dest.writeParcelable(endingCoordination, flags);
        dest.writeString(userId);
        dest.writeString(routeId);
    }
}
