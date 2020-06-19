package com.ortusolis.listviewwithcard;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class OTPMapActivity extends AppCompatActivity {
    private Button otpsubmit;
    WebSocket webSocketotp;
    private EditText editTextOtp;

    SharedPreferences sharedPreferences;
    String clientId;
    String userId;
    private static final int NORMAL_CLOSURE_STATUS = 10000;
    private Handler mMessageHandler;
    public static final String mypreference = "VTS";
    String response;
   String otp;
   String otptxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpmap);
        /*getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("OTP");*/

        sharedPreferences= getSharedPreferences("vt", MODE_PRIVATE);
        userId=sharedPreferences.getString("phoneNumber","");

        Log.e("phoneNumberrrrrrr",userId);


       final EditText editTextOtp = (EditText) findViewById(R.id.editTextOTP);


       if (TextUtils.isEmpty(otptxt)) {
            editTextOtp.setError("Please enter OTP");
            editTextOtp.requestFocus();
            return;
        }
        otpsubmit=findViewById(R.id.otpSubmit);

        otpsubmit.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View v)
            {
                otptxt= editTextOtp.getText().toString();
                Log.d("************",otptxt);
                if(otp.equalsIgnoreCase(otptxt))
                {
                    Intent intent = new Intent(OTPMapActivity.this, CardsActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Toast.makeText(OTPMapActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                }
            }


        });
        findViewById(R.id.resendOtp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                otpgenerate();

            }
        });

    }


    public void otpgenerate()
    {
        Random random= new Random();
        otp = String.format("%04d", random.nextInt(10000));
        JSONObject jObjectotp = new JSONObject();
        try {
            // jObject.put("clientId", output);
            jObjectotp.put("action","SEND_OTP");
            jObjectotp.put("phoneNum", userId);
            jObjectotp.put("message",otp+" is your one time password for Vehicle Tracking System");
            webSocketotp.send(jObjectotp.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
    }

}
