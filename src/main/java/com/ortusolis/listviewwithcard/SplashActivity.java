package com.ortusolis.listviewwithcard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.bumptech.glide.Glide;
import com.ortusolis.listviewwithcard.R;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.WebSocket;

public class SplashActivity extends AppCompatActivity {
    Handler handler;
    String connect;
    SharedPreferences sharedPreferences;
    String clientId="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences= getSharedPreferences("clientPreferences", MODE_PRIVATE);
        setContentView(R.layout.activity_splash);
        ImageView imageView = findViewById(R.id.logo_id);

try {
    WebSocketWifi.getInstance(getApplicationContext()).start();
    connect="1";
}catch (Exception e)
{
    connect="0";
    e.printStackTrace();

}
        Glide.with(this).load(R.drawable.tenor).into(imageView);
        clientId=sharedPreferences.getString("clientId","");
        if (clientId.equals("") )
                {
                    initialHandShake(WebSocketWifi.getInstance(getApplicationContext()).webSocket);
                    getDeviceList(WebSocketWifi.getInstance(getApplicationContext()).webSocket);
        }
        else
        {
            getDeviceList(WebSocketWifi.getInstance(getApplicationContext()).webSocket);
        }

            handler=new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    String initResponse = WebSocketWifi.getInstance(getApplicationContext()).deviceresponse;
                    if (initResponse.equalsIgnoreCase("200")) {
                        Intent intent = new Intent(SplashActivity.this, CardsActivity.class);
                        intent.putExtra("connection", connect);
                        startActivity(intent);
                        finish();
                    }else
                        {
                            Toast.makeText(getApplicationContext(), ""+initResponse, Toast.LENGTH_SHORT).show();
                    }
                }
                    
                
            },5000);

        }
    /************************** Initial HandShake ******************************/
    public void initialHandShake(WebSocket webSocket)
    {
        JSONObject jObject = new JSONObject();
        try {
            jObject.put("action", "APP_INIT");
            jObject.put("userId", "8971492516");
            webSocket.send(jObject.toString());
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
    }
    /********************** Get Device List *****************************/
    public void getDeviceList(WebSocket webSocket)
    {

        JSONObject jObject = new JSONObject();
        try {
            jObject.put("action", "GET_DEVICE_LIST");
            jObject.put("userId", "8971492516");
            jObject.put("clientId", clientId);
            webSocket.send(jObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    }
