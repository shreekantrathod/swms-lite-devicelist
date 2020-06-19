package com.ortusolis.listviewwithcard;



import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import okhttp3.WebSocket;



public class DeviceInformation extends AppCompatActivity {
    RadioGroup radioGroup;
    RadioButton manual, timer;
    Switch ManualSwitch, TimerSwitch;
    LinearLayout manualEnableDisble, timerEnableDisable;
    TextView manualText, timerText, title,timedisplay, addNew;

    String clientId, weekday, hour, minute, duration, repeat, enableOrDisableTimer;
    String currentDevice = " ";
    String state;
    String mode;
    ProgressDialog barProgressDialog;
    Handler updateBarHandler;
    View view;

    /*weather 27-5-2020*/
    TextView view_city;
    TextView view_temp;
    TextView view_desc;
    ImageView view_weather;
    /**************************/
    ImageButton deleteTimer;
    SharedPreferences sharedPreferences;
    String strStatus = "";
   String sheet;
    ArrayList<String> currentRes= new ArrayList<String>();
    String[] time = new String[]{
            "1 PM",
            "5 PM",
            "8 AM",
            "2 AM"

    };

    // Array of booleans to store toggle button status
    public int[] status = {
            0,
            0
    };
    public String[] weekDaysText={
           "MO",
            "TU",
            "SA",
            "SU"
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_information);


        sharedPreferences = getSharedPreferences("clientPreferences", MODE_PRIVATE);
        updateBarHandler = new Handler();
        clientId = sharedPreferences.getString("clientId", "");
        ImageView imageView = (ImageView) findViewById(R.id.infoimage);
        title = (TextView) findViewById(R.id.text_title);
        String temps = getIntent().getStringExtra("temps");
        String city= getIntent().getStringExtra("city");
        String icons = getIntent().getStringExtra("icons");
        String description = getIntent().getStringExtra("description");
        /****************weater*/
        view_city=findViewById(R.id.town);
        view_city.setText(city);
        view_temp=findViewById(R.id.temp);
        view_temp.setText(temps);
        view_desc=findViewById(R.id.desc);
        view_desc.setText(description);

        view_weather=findViewById(R.id.wheather_image);
        setImage(view_weather,icons);
        //view_weather.set;

        view_weather=findViewById(R.id.wheather_image);
        /*************************/
        deleteTimer = (ImageButton) findViewById(R.id.deleteTimer);
        timedisplay=findViewById(R.id.time);
       // currentDevice = WebSocketWifi.getInstance(getApplicationContext()).currentDevice;
        currentDevice = getIntent().getStringExtra("ListViewClickedValue");
        title.setText(currentDevice);
        deviceCurrentInfo(WebSocketWifi.getInstance(getApplicationContext()).webSocket);
        manualEnableDisble = findViewById(R.id.onofflayout);
        timerEnableDisable = findViewById(R.id.timerEnDis);
        manual = (RadioButton) findViewById(R.id.manual);
        timer = (RadioButton) findViewById(R.id.timer);
        addNew = findViewById(R.id.addNew);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        ManualSwitch = (Switch) findViewById(R.id.manualswitch);
        TimerSwitch = (Switch) findViewById(R.id.timerswitch);
        if (TimerSwitch != null) {
            TimerSwitch.setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener) this);
        }
        manualText = findViewById(R.id.manualtext);
        timerText = findViewById(R.id.timertext);
       // weekdaysText.setText(weekday);
        //timer mode list


/**********************  shreekant rathod  10-4-2020
                                time sheet list  ************************************************/

        if(savedInstanceState!=null){
          //  status = savedInstanceState.getIntArray("status");
        }

        final List<String> timeSheet = new ArrayList<String>(Arrays.asList(time));
       /* final List<String> days = new ArrayList<String>(Arrays.asList(weekDaysText));
        //timeSheet.add(DeviceInformation.sheet.toString());
        days.add("TU");*/
        TimeSheetListView adapter=new TimeSheetListView(this, time,weekDaysText);
         ListView listView = (ListView) findViewById(R.id.timesheetlist);
         listView.setAdapter(adapter);
        //timeSheet.add(String.valueOf(sheet));
        adapter.notifyDataSetChanged();



        /********************transfer to add activity for adding time repeat and duration  *******************/

        addNew.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(DeviceInformation.this, AddTime.class);
                intent.putExtra("deviceId",currentDevice);
                startActivity(intent);
            }
        });
        state = "0";
        changeMode(WebSocketWifi.getInstance(getApplicationContext()).webSocket);
        manualOnOff(WebSocketWifi.getInstance(getApplicationContext()).webSocket);
        // deviceCurrentInformation(WebSocketWifi.getInstance(getApplicationContext()).webSocket);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (timer.isChecked()) {
                    mode = "T";
                    timerEnableDisable.setVisibility(LinearLayout.VISIBLE);
                    manualEnableDisble.setVisibility(LinearLayout.GONE);
                    changeMode(WebSocketWifi.getInstance(getApplicationContext()).webSocket);
                    getTimeSheetList(WebSocketWifi.getInstance(getApplicationContext()).webSocket);
                String days=sharedPreferences.getString("timeshetList","");
                    Gson weekdays = new Gson();
                    weekdays.toJson(days);


                    try {
                        JSONObject jsonObject=new JSONObject(days);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ArrayList<ArrayList<String>> sheet= new ArrayList<>();
                  sheet.add(WebSocketWifi.getInstance(getApplicationContext()).res);
                  for (int j = 0; j<sheet.size(); j++)
                  {
                      Log.d("*****", sheet.get(j).toString());
                  }
                 /*   try {
                        sheet.getJSONObject("timeSheet");
                        if(sheet.has("SU")) {
                            String sunday = sheet.getString("SU");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/

/*TimerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked==true){
                                enableOrDisableTimer = "1";
                                launchRingDialog(view);
                                sendTimeSheetToWebSocket(WebSocketWifi.getInstance(getApplicationContext()).webSocket);
                                Toast.makeText(getApplicationContext(), getText(R.string.on), Toast.LENGTH_LONG).show();
                            }else {
                                enableOrDisableTimer = "0";
                                launchRingDialog(view);
                                sendTimeSheetToWebSocket(WebSocketWifi.getInstance(getApplicationContext()).webSocket);
                                Toast.makeText(getApplicationContext(), getText(R.string.off), Toast.LENGTH_LONG).show();
                            }

                        }
                    });*/
                } else if (manual.isChecked()) {
                    mode = "M";
                    manualEnableDisble.setVisibility(LinearLayout.VISIBLE);
                    timerEnableDisable.setVisibility(LinearLayout.GONE);
                    changeMode(WebSocketWifi.getInstance(getApplicationContext()).webSocket);
                    ManualSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            // state 1 for timer mode else by default manual mode
                            if (isChecked == true) {
                                state = "1";
                                launchRingDialog(view);
                                manualOnOff(WebSocketWifi.getInstance(getApplicationContext()).webSocket);
                                Toast.makeText(getApplicationContext(), getText(R.string.on), Toast.LENGTH_LONG).show();
                            } else {
                                state = "0";
                                launchRingDialog(view);
                                manualOnOff(WebSocketWifi.getInstance(getApplicationContext()).webSocket);
                                Toast.makeText(getApplicationContext(), getText(R.string.off), Toast.LENGTH_LONG).show();
                            }

                            manualOnOff(WebSocketWifi.getInstance(getApplicationContext()).webSocket);


                        }
                    });
                   /* deleteTimer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            deleteTimer(WebSocketWifi.getInstance(getApplicationContext()).webSocket);
                        }
                    });*/


                    /*if (ManualOFFNO.isChecked()) {
                        state = ManualOFFNO.getTextOn().toString();
                        Log.d("onnnnnnnn",state);
                      //  manualOnOff(webSocket);
                    }
                    else {
                        state = ManualOFFNO.getTextOff().toString();
                        Log.d("onnnnnnnn",state);
                       // manualOnOff(webSocket);
                    }*/
                    //changeMode(WebSocketWifi.getInstance(getApplicationContext()).webSocket);
                    //manualOnOff(WebSocketWifi.getInstance(getApplicationContext()).webSocket);
                }
            }
        });
        ManualSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // state 1 for timer mode else by default manual mode
                if (isChecked == true) {
                    state = "1";
                    launchRingDialog(view);
                    manualOnOff(WebSocketWifi.getInstance(getApplicationContext()).webSocket);
                   // Toast.makeText(getApplicationContext(), getText(R.string.on), Toast.LENGTH_LONG).show();
                }
                else {
                    state = "0";
                    launchRingDialog(view);
                    manualOnOff(WebSocketWifi.getInstance(getApplicationContext()).webSocket);

                }
                manualOnOff(WebSocketWifi.getInstance(getApplicationContext()).webSocket);
            }

        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /*************************** websocket Request for delete timer ****************************/
    public void deleteTimer(WebSocket webSocket) {
        JSONObject timeSheet= new JSONObject();
        JSONObject object= new JSONObject();
        JSONObject jObject = new JSONObject();
        try {
            jObject.put("action", "SET_TIMER");
            jObject.put("deviceId",currentDevice);
            jObject.put("clientId", clientId);
            timeSheet.put("SU","");
            timeSheet.put("MO","");
            timeSheet.put("TU","");
            timeSheet.put("WE","");
            timeSheet.put("TH","");
            timeSheet.put("FR","");
            timeSheet.put("SA","");

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "****" + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }



    /************************progress bar ******************/
    public void launchRingDialog(View view) {
        final ProgressDialog ringProgressDialog = ProgressDialog.show(DeviceInformation.this, "Processing ...", "Please Wait ...", true);
        ringProgressDialog.setCancelable(false);
        ringProgressDialog.getWindow().setGravity(Gravity.BOTTOM);
        //ringProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Here you should write your time consuming task...

                    Thread.sleep(3000);
                } catch (Exception e) {

                }
                ringProgressDialog.dismiss();
            }
        }).start();
    }


 /*   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }*/

    /*************************** websocket request for on or off of manual mode*****************************/

    public void manualOnOff(WebSocket webSocket) {
        JSONObject jObject = new JSONObject();
        try {
            jObject.put("action", "MOTOR_ON_OFF_REQ");
            //jObject.putOpt("deviceId", deviceId.indexOf(1));
            jObject.put("deviceId", currentDevice);
            jObject.put("state", state);
            jObject.put("clientId", clientId);
            webSocket.send(jObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /*******************************web socket request for changing mode *************************************/

    public void changeMode(WebSocket webSocket) {
        JSONObject jObject = new JSONObject();
        try {
            jObject.put("action", "MODE_CHANGE_REQ");
            //  jObject.putOpt("deviceId", deviceId.indexOf(1));
            jObject.put("mode", mode);
            jObject.put("deviceId", currentDevice);
            jObject.put("clientId", clientId);
            webSocket.send(jObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "****" + e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    /********************* method to communicate with websocket to get time sheet  *********************/
    public void getTimeSheetList(WebSocket webSocket) {
        JSONObject jObject = new JSONObject();
        try {
            jObject.put("action", "GET_DEVICE_INFO");
            jObject.put("deviceId", currentDevice);
            jObject.put("clientId", clientId);
            webSocket.send(jObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "****" + e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    /*************** get the current device information ******************/
    public void deviceCurrentInfo(WebSocket webSocket){
        JSONObject jObject = new JSONObject();
        try {
            jObject.put("action", "GET_DEVICE_CURRENT_INFO");
            jObject.put("deviceId", currentDevice);
            jObject.put("clientId", clientId);
            webSocket.send(jObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "****" + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
    /***************receive data from addtime page *************************/

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                weekday = data.getStringExtra("weekdays");
                hour = data.getStringExtra("hour");
                minute = data.getStringExtra("minute");
                duration = data.getStringExtra("duration");
                repeat = data.getStringExtra("repeat");
            }
        }
    }
    public  static void responceTimeSheetValue(ArrayList<String> res)throws IOException {

     /*   DeviceInformation.sheet.add(String.valueOf(res));
       sheet.add(res.toString());*/
      /* for(int i=0;i<res.size();i++) {
           String days = res.get(i);
           String Hour = days.substring(0, 2);
           String min = "";
           min = days.substring(2, 4);
           String duration = "";
       }*/

    }
    public static void responceDeviceCurrentInfo(ArrayList<String> currentRes) {
        for (int i = 0; i < currentRes.size(); i++)
        {
          String responseCurrent =currentRes.get(i);
    }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }
    private void setImage(final ImageView imageView, final String value){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //paste switch
                switch (value){
                    case "01d": imageView.setImageDrawable(getResources().getDrawable(R.drawable.d01d));
                        break;
                    case "01n": imageView.setImageDrawable(getResources().getDrawable(R.drawable.d01d));
                        break;
                    case "02d": imageView.setImageDrawable(getResources().getDrawable(R.drawable.d02d));
                        break;
                    case "02n": imageView.setImageDrawable(getResources().getDrawable(R.drawable.d02d));
                        break;
                    case "03d": imageView.setImageDrawable(getResources().getDrawable(R.drawable.d03d));
                        break;
                    case "03n": imageView.setImageDrawable(getResources().getDrawable(R.drawable.d03d));
                        break;
                    case "04d": imageView.setImageDrawable(getResources().getDrawable(R.drawable.d04d));
                        break;
                    case "04n": imageView.setImageDrawable(getResources().getDrawable(R.drawable.d04d));
                        break;
                    case "09d": imageView.setImageDrawable(getResources().getDrawable(R.drawable.d09d));
                        break;
                    case "09n": imageView.setImageDrawable(getResources().getDrawable(R.drawable.d09d));
                        break;
                    case "10d": imageView.setImageDrawable(getResources().getDrawable(R.drawable.d10d));
                        break;
                    case "10n": imageView.setImageDrawable(getResources().getDrawable(R.drawable.d10d));
                        break;
                    case "11d": imageView.setImageDrawable(getResources().getDrawable(R.drawable.d11d));
                        break;
                    case "11n": imageView.setImageDrawable(getResources().getDrawable(R.drawable.d11d));
                        break;
                    case "13d": imageView.setImageDrawable(getResources().getDrawable(R.drawable.d13d));
                        break;
                    case "13n": imageView.setImageDrawable(getResources().getDrawable(R.drawable.d13d));
                        break;
                    default:
                        imageView.setImageDrawable(getResources().getDrawable(R.drawable.wheather));

                }
            }
        });
    }

}