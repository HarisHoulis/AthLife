package com.example.xoulis.xaris.athLife.data.models;

public class Weather {

    private String description;
    private String timeOfDay;

    public Weather() {
    }

    public Weather(String description, String timeOfDay) {
        this.description = description;
        this.timeOfDay = timeOfDay;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTimeOfDay() {
        return timeOfDay;
    }

    public void setTimeOfDay(String timeOfDay) {
        this.timeOfDay = timeOfDay;
    }
}
