package com.example.youxian.juweather.weather;


import java.io.Serializable;

/**
 * Created by Youxian on 12/20/15.
 */
public class ForecastWeather implements Serializable {
    private City city;
    private String cnt;
    private List[] list;


    public City getCity() {
        return city;
    }

    public String getCnt() {
        return cnt;
    }

    public List[] getList() {
        return list;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public void setCnt(String cnt) {
        this.cnt = cnt;
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
        private Main main;
        private Weather[] weather;

        public Main getMain() {
            return main;
        }

        public void setMain(Main main) {
            this.main = main;
        }

        public void setWeather(Weather[] weather) {
            this.weather = weather;
        }

        public Weather[] getWeather() {
            return weather;
        }
    }

    public class Main {
        private String temp_max;
        private String temp_min;

        public String getTemp_max() {
            return temp_max;
        }

        public String getTemp_min() {
            return temp_min;
        }

        public void setTemp_max(String temp_max) {
            this.temp_max = temp_max;
        }

        public void setTemp_min(String temp_min) {
            this.temp_min = temp_min;
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


