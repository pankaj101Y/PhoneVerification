package com.example.ks.mobileverificationsystem;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


class OTPVerifier extends AsyncTask<String,Void,String> {
    private OnVerificationResponse onResponse;

    OTPVerifier(OTPActivity otpActivity){
        if (otpActivity!=null && otpActivity instanceof OTPActivity){
            onResponse =otpActivity;
            Log.e("debug","madefdfd");
        }else
            Log.e("debug","not okogf");
    }

    @Override
    protected String doInBackground(String... params) {
        Log.e("debug","state=");
        String state="";
        try {
            URL sendOTPURL=new URL(ServerContacts.getVerifyOTPURL());
            HttpURLConnection sendOTPConnection= (HttpURLConnection) sendOTPURL.openConnection();

            sendOTPConnection.setDoOutput(true);
            sendOTPConnection.setDoInput(true);
            sendOTPConnection.setRequestMethod("POST");

            sendOTPConnection.setRequestProperty("Content-Type", "application/json");
            sendOTPConnection.setRequestProperty("Accept", "application/json");

            OutputStream phoneOutputStream=sendOTPConnection.getOutputStream();
            BufferedWriter writePhone=new BufferedWriter(new OutputStreamWriter(phoneOutputStream,"UTF-8"));

            writePhone.write(getJsonObjectOTP(params[0],params[1]).toString());
            writePhone.close();
            phoneOutputStream.close();

            InputStream in = sendOTPConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(in,"iso-8859-1");
            BufferedReader verificationResponse=new BufferedReader(inputStreamReader);

            String data="";
            String temp;
            while ((temp=verificationResponse.readLine())!=null)
                data+=temp;

            inputStreamReader.close();
            in.close();

            JSONObject verificationObject=new JSONObject(data);
            state=verificationObject.getString("State");
        } catch (IOException ignored) {
            return state;
        } catch (JSONException e) {
            return state;
        }
        return state;
    }

    @Override
    protected void onPostExecute(String state) {
        Log.e("debug","state="+state);
        try {
            switch (state){
                case State.OTP.verified:
                    onResponse.onOTPVerified();
                    break;
                case State.OTP.expired:
                    onResponse.onOTPExpired();
                    break;
                case State.OTP.invalid:
                    onResponse.onOTPInvalid();
                    break;
                default:
                    onResponse.onOTPError();
            }
        }catch (Exception e){
            Log.e("debug",e.toString());
        }
    }

    private JSONObject getJsonObjectOTP(String phoneNumber, String otp){
        JSONObject OTPObject=new JSONObject();
        try {
            OTPObject.put("phoneNumber",phoneNumber);
            OTPObject.put("otp",otp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return OTPObject;
    }

    interface OnVerificationResponse{
        void onOTPVerified();
        void onOTPInvalid();
        void onOTPExpired();
        void onOTPError();
    }
}
