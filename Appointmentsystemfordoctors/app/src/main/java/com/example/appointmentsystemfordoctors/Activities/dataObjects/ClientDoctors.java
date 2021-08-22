package com.example.appointmentsystemfordoctors.Activities.dataObjects;

import java.io.Serializable;

/**
 * This class represent Costumer Details of Doctor
 */
public class ClientDoctors extends Client implements Serializable {

    private String Availability;
    private String Doctor_name;
    private String DoctorID;
    private String currPat;

    public ClientDoctors(){super();}

    //For Sign Up purpose
    public ClientDoctors(String email, String phone_number, String password,
                         Address address, LockedAccount lockedAccount,
                         String Doctor_name, String DoctorID) {
        super(email, phone_number, password, address, lockedAccount);
        this.Availability = "True";
        this.Doctor_name = Doctor_name;
        this.DoctorID = DoctorID;
    }

    //For invoking data purpose
    public ClientDoctors(String phone_number,
                         Address address,
                         String Doctor_name, String DoctorID, String Availability, String currPat) {
        this.Availability = Availability;
        this.Doctor_name = Doctor_name;
        this.DoctorID = DoctorID;
        this.setAddress(address);
        this.setPhone_number(phone_number);
        this.currPat = currPat;
    }



    public String getDoctor_name() {
        return Doctor_name;
    }
    public String getCurrPat() {
        return this.currPat;
    }

    public String getDoctorID() {
        return DoctorID;
    }
    public String getDocName() {
        return Doctor_name;
    }
    public String getAvailability() {
        return Availability;
    }

    public void setDoctor_name(String Doctor_name) {
        this.Doctor_name = Doctor_name;
    }

    public void setDoctorID(String DoctorID) {
        this.DoctorID = DoctorID;
    }


    public String ToString(){
        return this.Doctor_name + "        ," + this.getAddress().getCity_Name();
    }
}
