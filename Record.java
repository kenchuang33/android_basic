package com.example.bmrcalculator;

import java.io.Serializable;

public class Record implements Serializable {
    private String name;
    private String gender;
    private int bmr;
    private int age;
    private int height;
    private int weight;
    private int bmi;

    public Record(String name, String gender, int bmr, int age, int height, int weight, int bmi) {
        this.name = name;
        this.bmr = bmr;
        this.gender = gender;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.bmi = bmi;
    }

    public String getName() {
        return name;
    }
    public String getGender() {
        return gender;
    }
    public int getAge() {
        return age;
    }
    public int getWeight() {
        return weight;
    }
    public int getHeight() {
        return height;
    }

    public int getBmr(){
        if (gender.equals("Male")) {
            bmr = (int) (66 + (13.7 * weight + 5 * height - 6.8 * age));
        }
        else {
            bmr = (int) (655 + (9.6 * weight + 1.8 * height - 4.7 * age));
        }
        return bmr;
    }
    public int getBmi(){return (int) ( weight / ((height / 100.0) * (height / 100.0)));}







    @Override
    public String toString() {
        return name + "                       |                        " + bmr;
    }
}

