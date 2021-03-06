package com.example.appointmentsystemfordoctors.Activities.dataObjects;

import java.io.Serializable;

/**
 * This JAVA Class represent a Costumer Details
 * aka Imaging Institutes and patients
 */
public class Client implements Serializable {
    private String email;
    private String phone_number;
    private String password;
    private Address address;
    private LockedAccount lockedAccount;

    public Client() {
    }

    public Client(String email, String phone_number, String password, Address address,
                  LockedAccount lockedAccount) {
        this.email = email;
        this.phone_number = phone_number;
        this.password = password;
        this.address = address;
        this.lockedAccount = lockedAccount;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public String getPassword() {
        return password;
    }

    public Address getAddress() {
        return address;
    }

    public LockedAccount getLockedAccount() {
        return lockedAccount;
    }

    public void setLockedAccount(LockedAccount lockedAccount) {
        this.lockedAccount = lockedAccount;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
