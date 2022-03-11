package com.dinaldurak.mytrips.model;

public class User {
    public String email, nameSurname, phone;

    public User(){

    }

    public User(String email, String nameSurname, String phone) {
        this.email = email;
        this.nameSurname = nameSurname;
        this.phone = phone;
    }
}
