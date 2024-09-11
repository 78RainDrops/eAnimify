package com.rcm.eanimify.Account;

public class User {
    public String firstName;
    public String lastName;
    public String email;

    public User() {} // Empty constructor for Firestore

    public User(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
}
