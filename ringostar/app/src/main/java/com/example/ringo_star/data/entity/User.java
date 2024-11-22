package com.example.ringo_star.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.ringo_star.enumeration.BloodGroup;
import com.example.ringo_star.enumeration.Gender;

import java.time.LocalDate;

@Entity
public class User {
    //////////////////
    /// ATTRIBUTES ///
    //////////////////

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String firstname;
    private String lastname;
    private Gender gender;
    private LocalDate birthday;

    private byte[] salt;
    private String hashedPin;

    private int height;
    private int weight;

    private BloodGroup bloodGroup;

    private boolean smoke;

    ///////////////////////
    /// GETTER & SETTER ///
    ///////////////////////

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    public String getHashedPin() {
        return hashedPin;
    }

    public void setHashedPin(String hashedPin) {
        this.hashedPin = hashedPin;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public BloodGroup getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(BloodGroup bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public boolean isSmoke() {
        return smoke;
    }

    public void setSmoke(boolean smoke) {
        this.smoke = smoke;
    }
}