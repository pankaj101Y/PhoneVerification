package com.example.ks.mobileverificationsystem;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Telephony;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class OTPActivity extends AppCompatActivity implements OTPVerifier.OnVerificationResponse,OTPSender.OnSendResponse{
    private Button button_verify;
    private EditText edit_otp;
    private BroadcastReceiver OTPBroadcastReceiver;
    private String phoneNumber;
    private ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_otp);

        if (!isInternetAvailable())showInternetDialog();

        phoneNumber=getIntent().getStringExtra("phoneNumber");
        sendOTP();

        button_verify=(Button)findViewById(R.id.button_verify);
        edit_otp=(EditText)findViewById(R.id.edit_otp);

        button_verify.setEnabled(false);
    }


    public void onOTPEntered(View view) {
        String OTP=edit_otp.getText().toString();
        if (OTP.length()==6&&OTP.matches("[0-9]+"))
            verifyOTP(OTP);
        else Toast.makeText(this,"please provide valid OTP",Toast.LENGTH_LONG).show();
    }

    private void sendOTP(){
        OTPSender otpSender=new OTPSender(OTPActivity.this);
        otpSender.execute(phoneNumber);
        Toast.makeText(getApplicationContext(),getString(R.string.sendingOTP),Toast.LENGTH_LONG).show();
    }

    public void showProgressDialog(String title,String message){
        progressBar =new ProgressDialog(OTPActivity.this);
        progressBar.setCancelable(false);
        progressBar.setMessage(title);
        progressBar.setMessage(message);
        progressBar.setProgressDrawable(ContextCompat.getDrawable(OTPActivity.this,R.drawable.progress));
        progressBar.show();
    }




    private  void registerOTPReceiver(){
        IntentFilter OTP_filter=new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        OTPBroadcastReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                verifyOTP(intent);
            }
        };
        registerReceiver(OTPBroadcastReceiver,OTP_filter);
    }

    private void verifyOTP(Intent intent) {
        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null&& Build.VERSION.SDK_INT>=19) {
                SmsMessage smsMessage;
                SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
                smsMessage = messages[0];
                String body=smsMessage.getDisplayMessageBody();
                String OTP= body.substring(0,6);
                verifyOTP(OTP);
            }else
                Toast.makeText(getApplicationContext(),"Please Enter OTP",Toast.LENGTH_LONG).show();
        } catch (Exception ignored) {
            Toast.makeText(getApplicationContext(),"Please Enter OTP",Toast.LENGTH_LONG).show();
        }
        button_verify.setEnabled(true);
    }

    private void verifyOTP(String OTP) {
        showProgressDialog(getString(R.string.verifyingOTPTitle),getString(R.string.verifyingOTPMessage));
        OTPVerifier verifier=new OTPVerifier(this);
        verifier.execute(phoneNumber,OTP);
    }



    @Override
    public void onOTPSent() {
        Toast.makeText(getApplicationContext(),"OTP Sent",Toast.LENGTH_LONG).show();
        registerOTPReceiver();
    }

    @Override
    public void onOTPSendError() {
        if (progressBar !=null){
            progressBar.dismiss();
            progressBar=null;
        }

        showBackDialog(getString(R.string.errorTitle),getString(R.string.errorMessage),
                getString(R.string.OTPSendErrPos));
    }

    @Override
    public void onOTPVerified() {
        if (progressBar !=null){
            progressBar.dismiss();
            progressBar =null;
        }
        showInfoDialog(getString(R.string.OTPVerifiedTitle),getString(R.string.OTPVerifiedMessage),
                getString(R.string.OTPVerifiedPos));
    }

    @Override
    public void onOTPInvalid() {
        if (progressBar !=null){
            progressBar.dismiss();
            progressBar =null;
        }
        showInfoDialog(getString(R.string.OTPInvalidTitle),getString(R.string.OTPInvalidMessage),
                getString(R.string.OTPInvalidPos));
    }

    @Override
    public void onOTPExpired() {
        if (progressBar !=null){
            progressBar.dismiss();
            progressBar =null;
        }
        showResendDialog(getString(R.string.OTPExpiredTitle),getString(R.string.OTPExpiredMessage),
                getString(R.string.OTPSExpiredPos));
    }

    @Override
    public void onOTPError() {
        if (progressBar !=null){
            progressBar.dismiss();
            progressBar =null;
        }
        showBackDialog(getString(R.string.errorTitle),getString(R.string.errorMessage),
                getString(R.string.OTPErrPos));
    }




    private void showInternetDialog() {
        try {
            dialog_internet().show();
        }catch (Exception ignored){}
    }

    private AlertDialog dialog_internet(){
        AlertDialog.Builder builder=new AlertDialog.Builder(OTPActivity.this);

        builder.setTitle("No Internet!");
        builder.setMessage("No internet connection detected.Please enable internet.");
        builder.setCancelable(true);
        builder.setPositiveButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!isInternetAvailable())
                    finish();
                dialog.dismiss();
            }
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (!isInternetAvailable())
                    finish();
            }
        });

        return builder.create();
    }

    private boolean isInternetAvailable(){
        ConnectivityManager connectivityManager= (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        return networkInfo!=null&&networkInfo.isConnected();
    }

    private AlertDialog.Builder getDialogBuilder(String title, String message){
        final AlertDialog.Builder builder=new AlertDialog.Builder(OTPActivity.this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(true);
        return builder;
    }

    private void showInfoDialog(String title, String message, String positive) {
        AlertDialog.Builder builder=getDialogBuilder(title,message);
        builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void showBackDialog(String title, String message, String positive) {
        AlertDialog.Builder builder=getDialogBuilder(title,message);

        builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });

        builder.show();
    }

    private void showResendDialog(String title, String message, String positive) {
        AlertDialog.Builder builder=getDialogBuilder(title,message);
        builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendOTP();
            }
        });
        builder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }


    @Override
    protected void onStop() {
        if (OTPBroadcastReceiver!=null){
            unregisterReceiver(OTPBroadcastReceiver);
            OTPBroadcastReceiver=null;
        }
        super.onStop();
    }
}