package com.karimtimer.sugarcontrol.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    public String name;
    public String firstName;
    public String lastName;
    public String email;
    public String type;
    public String height;
    public String bglLowerRange;
    public String bglUpperRange;
    public boolean kg;
   // public String stones;
    public boolean metres;
   // public String feet;
    public String weight;
    public String medType;
    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email, String bglLowerRange, String bglUpperRange, String firstName, String lastName, String type, String medType) {
        this.name = username;
        this.email = email;
//        this.height = height;
//        this.weight = weight;
        this.bglLowerRange = bglLowerRange;
        this.bglUpperRange = bglUpperRange;
        this.firstName = firstName;
        this.lastName = lastName;
        this.type = type;
        this.medType = medType;
        //this.kg = kg;
        //this.stones = stones;
        //this.metres = metres;
        //this.feet = feet;
    }
}
