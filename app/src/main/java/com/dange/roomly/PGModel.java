package com.dange.roomly;


import java.io.Serializable;

public class PGModel implements Serializable {
    public int id;
    public String pg_name,pg_rent, pg_type, city, description, owner_name, owner_phone, image1, image2, image3;
    public double latitude, longitude;

    public PGModel(int id, String pg_name, String pg_type, String city,
                   String description, String owner_name, String owner_phone,
                   double latitude, double longitude, String image1,
                   String image2, String image3, String pg_rent) {
        this.id = id;
        this.pg_name = pg_name;
        this.pg_rent = pg_rent;
        this.pg_type = pg_type;
        this.city = city;
        this.description = description;
        this.owner_name = owner_name;
        this.owner_phone = owner_phone;
        this.latitude = latitude;
        this.longitude = longitude;
        this.image1 = image1;
        this.image2 = image2;
        this.image3 = image3;
    }
}