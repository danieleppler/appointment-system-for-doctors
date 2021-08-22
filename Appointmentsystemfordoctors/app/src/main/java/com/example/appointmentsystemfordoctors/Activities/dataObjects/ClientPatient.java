package com.example.appointmentsystemfordoctors.Activities.dataObjects;

import java.io.Serializable;

/**
 * This class represent Costumer Details of Patient
 */
public class ClientPatient extends Client implements Serializable  {
    private String first_name;
    private String second_name;
    private String age;
    private String patientID;
    private long Arrival_Hour, Arrival_Minute,currPlaecInWl;



    public ClientPatient(){
        super();
    }

    //for Sign Up purpose
    public ClientPatient(String email, String phone_number, String password, Address address,
                         LockedAccount lockedAccount, String first_name,
                         String second_name, String age, String patientID) {
        super(email, phone_number, password, address, lockedAccount);
        this.first_name = first_name;
        this.second_name = second_name;
        this.age = age;
        this.patientID = patientID;
    }

    //for invoking data purpose - doctor view
    public ClientPatient(String phone_number, String first_name,
                         String second_name, String age, String patientID, long h, long m) {
        this.first_name = first_name;
        this.second_name = second_name;
        this.age = age;
        this.patientID = patientID;
        this.setPhone_number(phone_number);
        this.Arrival_Hour = h;
        this.Arrival_Minute = m;
    }

    //for invoking data purpose - patient waiting list view
    public ClientPatient(String first_name,
                         String second_name, long currPlaecInWl) {
        this.first_name = first_name;
        this.second_name = second_name;

        this.currPlaecInWl = currPlaecInWl;
    }





    public String getFirst_name() {
        return first_name;
    }

    public String getSecond_name() {
        return second_name;
    }

    public String getAge() {
        return age;
    }

    public long getCurrPlaecInWl()
    {
        return this.currPlaecInWl;
    }

    public String getPatientID() {
        return patientID;
    }

    public long getArrivalHour()
    {
        return this.Arrival_Hour;
    }

    public long getArrivalMinute()
    {
        return this.Arrival_Minute;
    }


    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setSecond_name(String second_name) {
        this.second_name = second_name;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }

    public String TostringWithArrival() {
        String minute;
        if (this.Arrival_Minute < 10)
             minute= "0"+ this.Arrival_Minute;
        else minute = String.valueOf(this.Arrival_Minute);
        return this.Arrival_Hour + ":"+minute + "            " + this.first_name + " " + this.second_name + "  ," + this.age ;
    }

    public String TostringWithcurrPlaceInWl() {
        return  this.currPlaecInWl + "............." + this.first_name + " " + this.second_name + " " ;
    }

}