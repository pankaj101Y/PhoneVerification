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


class OTPSender extends AsyncTask<String,Void,String> {
    private OnSendResponse sendResponse;

    OTPSender(OTPActivity otpActivity){
        if (otpActivity!=null&& otpActivity instanceof OTPActivity)
            sendResponse=otpActivity;
    }

    @Override
    protected String doInBackground(String... params) {
        String state="";
        try {
            URL sendOTPURL=new URL(ServerContacts.getSendOTPURL());
            HttpURLConnection sendOTPConnection= (HttpURLConnection) sendOTPURL.openConnection();

            sendOTPConnection.setConnectTimeout(4000);

            sendOTPConnection.setDoOutput(true);
            sendOTPConnection.setDoInput(true);
            sendOTPConnection.setRequestMethod("POST");

            sendOTPConnection.setRequestProperty("Content-Type", "application/json");
            sendOTPConnection.setRequestProperty("Accept", "application/json");

            OutputStream phoneOutputStream=sendOTPConnection.getOutputStream();
            BufferedWriter writePhone=new BufferedWriter(new OutputStreamWriter(phoneOutputStream,"UTF-8"));

            writePhone.write(getJsonObjectPhone(params[0]).toString());
            writePhone.close();
            phoneOutputStream.close();

            InputStream in = sendOTPConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(in,"iso-8859-1");
            BufferedReader OTPSendResponse=new BufferedReader(inputStreamReader);

            String data="";
            String temp;
            while ((temp=OTPSendResponse.readLine())!=null)
                data+=temp;

            inputStreamReader.close();
            in.close();

            JSONObject verificationObject=new JSONObject(data);
            state=verificationObject.getString("State");
        }  catch (IOException e) {
            return state;
        } catch (JSONException e) {
            return state;
        }

        return state;
    }

    @Override
    protected void onPostExecute(String state) {
        try {
            switch (state){
                case State.OTP.sent:
                    sendResponse.onOTPSent();
                    break;
                default:
                    sendResponse.onOTPSendError();
            }
        }catch (Exception e){
            Log.e("debug",e.toString());
        }
    }

    private JSONObject getJsonObjectPhone(String phoneNumber){
        JSONObject phoneObject=new JSONObject();
        try {
            phoneObject.put("phoneNumber",phoneNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return phoneObject;
    }

    interface OnSendResponse{
        void onOTPSent();
        void onOTPSendError();
    }
}
