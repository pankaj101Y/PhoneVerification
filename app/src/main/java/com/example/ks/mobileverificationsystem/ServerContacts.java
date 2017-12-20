package com.example.ks.mobileverificationsystem;

class ServerContacts {

    static String getSendOTPURL(){
        return "https://fast-sea-53512.herokuapp.com/sendOTP";
    }

    static String getVerifyOTPURL(){
        return "https://fast-sea-53512.herokuapp.com/verifyOTP";
    }
}
