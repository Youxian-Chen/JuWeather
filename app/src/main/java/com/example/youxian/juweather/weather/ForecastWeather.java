package com.example.youxian.juweather.weather;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Youxian on 12/20/15.
 */
public class ForecastWeather implements Serializable {

    private City city;

    @SerializedName("cnt")
    private String number;

    private List[] list;


    public City getCity() {
        return city;
    }

    public String getNumber() {
        return number;
    }

    public List[] getList() {
        return list;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setList(List[] list) {
        this.list = list;
    }

    public class City {
        private String id;
        private String name;
        private String country;

        public String getCountry() {
            return country;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public class List {

        @SerializedName("dt")
        private String timeStamp;

        private Temp temp;
        private Weather[] weather;

        public String getTimeStamp() {
            return timeStamp;
        }

        public Temp getTemp() {
            return temp;
        }

        public void setTemp(Temp temp) {
            this.temp = temp;
        }

        public void setTimeStamp(String timeStamp) {
            this.timeStamp = timeStamp;
        }

        public void setWeather(Weather[] weather) {
            this.weather = weather;
        }

        public Weather[] getWeather() {
            return weather;
        }
    }

    public class Temp {
        private String min;
        private String max;

        public String getMax() {
            return max;
        }

        public String getMin() {
            return min;
        }

        public void setMax(String max) {
            this.max = max;
        }

        public void setMin(String min) {
            this.min = min;
        }
    }

    public class Weather {
        private String main;
        private String description;

        public String getDescription() {
            return description;
        }

        public String getMain() {
            return main;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setMain(String main) {
            this.main = main;
        }
    }
}


