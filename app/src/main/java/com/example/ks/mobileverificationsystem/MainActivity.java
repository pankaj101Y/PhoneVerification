package com.example.ks.mobileverificationsystem;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity{
    private EditText edit_phone;
    private Adapter_spinner_country adapter_spinner_country;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!isInternetAvailable()){
            AlertDialog dialog=dialog_internet();
            if (dialog!=null&&MainActivity.this!=null)
                dialog.show();
        }

        Spinner spinner_code = (Spinner) findViewById(R.id.spinner_code);
        edit_phone=(EditText)findViewById(R.id.edit_phone);
        Button button_sendOTP = (Button) findViewById(R.id.button_sendOTP);

        adapter_spinner_country=new Adapter_spinner_country(this,R.layout.row_spinner_code);
        initializeCountryList();
        spinner_code.setAdapter(adapter_spinner_country);

        button_sendOTP.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isInternetAvailable()){
                    dialog_internet().show();
                    return;
                }
                String phoneNumber=edit_phone.getText().toString();
                if (phoneNumber.matches("[0-9]+")){
                    Intent intent_OTPActivity=new Intent(MainActivity.this,OTPActivity.class);
                    intent_OTPActivity.putExtra("phoneNumber",edit_phone.getText().toString());
                    startActivity(intent_OTPActivity);
                }else
                    Toast.makeText(MainActivity.this,getResources().getString(R.string.valid_phone_request),
                            Toast.LENGTH_LONG).show();
            }
        });
    }


    private void initializeCountryList(){
        try {
            String file=getString(R.string.countriesCodes);
            String jsonArray=getString(R.string.cc_array);
            String jsonLocation = loadCountryCodes(file,this);
            JSONObject jsonobject = new JSONObject(jsonLocation);
            JSONArray jArray = jsonobject.getJSONArray(jsonArray);
            for(int i=0;i<jArray.length();i++) {
                JSONObject jb =(JSONObject) jArray.get(i);
                String name = jb.getString("name");
                String dial_code = jb.getString("dial_code");
                String code=jb.getString("code");
                adapter_spinner_country.add(new Country(name,dial_code,code));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String loadCountryCodes(String file, Context context) {
        String json;
        try {
            InputStream is = context.getAssets().open(file);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private AlertDialog dialog_internet(){
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);

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
}
