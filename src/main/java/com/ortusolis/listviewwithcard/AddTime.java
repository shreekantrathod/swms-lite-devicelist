package com.ortusolis.listviewwithcard;


import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.WebSocket;

import static java.lang.String.valueOf;

public class AddTime extends AppCompatActivity  {
    Button repeatButton,durationButton,setButton;
    TextView repeatText,durationText;
    String weekdays;
    SharedPreferences sharedPreferences;
    String clientId,repeat;
    int hours,minute,durationTime;
    NumberPicker duration;
    int timerOnOff=0;
    String deviceId="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_time);
        sharedPreferences= getSharedPreferences("clientPreferences",MODE_PRIVATE);
        clientId = sharedPreferences.getString("clientId","");
        repeatButton=(Button) findViewById(R.id.next_repeat);
        setButton = (Button) findViewById(R.id.settimer);
        durationButton=(Button) findViewById(R.id.next_duration);
        repeatText=findViewById(R.id.repeattext);
        durationText=findViewById(R.id.durationtext);
        deviceId = getIntent().getStringExtra("deviceId");
        TimePicker timePicker  = (TimePicker) findViewById(R.id.timePicker1);
      //  new TimePicker(new ContextThemeWrapper(getApplication(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar));
        // initiate a time
        timePicker.setMinimumWidth(500);
        hours = timePicker.getCurrentHour();
         minute=timePicker.getCurrentMinute();

        Boolean mode=timePicker.is24HourView(); // check the current mode of the time picker
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hour, int minute)
            {
                Toast.makeText(AddTime.this,+hour+":"+minute, Toast.LENGTH_SHORT).show();

            }
        });

    /**********if we click repeat button from addtime it will transfer to weekdays activity ************/
    repeatButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(AddTime.this,MultiWeekDaysSelect.class);
        startActivityForResult(intent,1);
        }
    });
    /* set the duration of timer mode */
    durationButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        durationSet();
    }
    });
    }

    /***********************for getting week days from next activity **************/
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                weekdays = data.getStringExtra("weekdays");
              //  for (int i=0;i<weekdays.length;i++) {
                    repeatText.append(weekdays.toString());
                    repeat=data.getStringExtra("repeat");
               // }
            }
        }
    }

/******************* set the timer duration **********************/
    public void durationSet()
    {
        final Dialog dialog = new Dialog(AddTime.this);
        dialog.setTitle("NumberPicker");
        dialog.setContentView(R.layout.duration_dialog);
       /*Button cancle = (Button) d.findViewById(R.id.button2);*/
        duration = (NumberPicker) dialog.findViewById(R.id.numberPicker1);
        duration.setMaxValue(99); // max value 100
        duration.setMinValue(1);   // min value 0
        duration.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format("%02d", i);
            }
        });
        duration.setWrapSelectorWheel(false);
        duration.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker,int oldVal, int newVal) {
                durationText.setText(duration.getValue()+" "+ "Min");
                duration.setFormatter(new NumberPicker.Formatter() {
                    @Override
                    public String format(int i) {
                        durationTime=duration.getValue();
                        return String.format("%02d", i);
                    }
                });

                //Toast.makeText(getApplicationContext(), "duration" +newVal, Toast.LENGTH_SHORT).show();
            }
        });

      setButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                sendTimeSheetToWebSocket(WebSocketWifi.getInstance(getApplicationContext()).webSocket);
        //sendTimesheetToWebSocket(WebSocketWifi.getInstance(getApplicationContext()).webSocket);
                Intent intent = new Intent(AddTime.this, DeviceInformation.class);
                intent.putExtra("hours",hours);
                intent.putExtra("minute",minute);
                intent.putExtra("duration",durationTime);
                intent.putExtra("weekdays", weekdays);
                intent.putExtra("repeat",repeat);
                startActivity(intent);

            }
        });
      /*  cancle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                d.dismiss(); // dismiss the dialog
            }
        });*/
        dialog.show();
    }

    /*************** send Time sheet to web socket
                    its time ,duration , on/off, repeat or non repeat ************************/

    public void sendTimeSheetToWebSocket(WebSocket webSocket)
    {
        int  timeSheetdata= Integer.parseInt(hours+""+minute+""+durationTime+""+repeat+timerOnOff);

        JSONObject timeSheet= new JSONObject();
        JSONObject object= new JSONObject();
        JSONObject jObject = new JSONObject();
        try {
            jObject.put("action", "SET_TIMER");
            jObject.put("deviceId",deviceId);
            jObject.put("clientId", clientId);
            timeSheet.put("SU","");
            timeSheet.put("MO","");
            timeSheet.put("TU","");
            timeSheet.put("WE","");
            timeSheet.put("TH","");
            timeSheet.put("FR","");
            timeSheet.put("SA","");
            if(weekdays.equalsIgnoreCase("SU")) {
                timeSheet.put("SU", timeSheetdata);
            }else if(weekdays.equalsIgnoreCase("MO"))
            {
                timeSheet.put("MO", timeSheetdata);
            }else if(weekdays.equalsIgnoreCase("TU"))
            {
                timeSheet.put("TU", timeSheetdata);
            }else if(weekdays.equalsIgnoreCase("WE"))
            {
                timeSheet.put("WE", timeSheetdata);
            }else if(weekdays.equalsIgnoreCase("TH"))
            {
                timeSheet.put("TH", timeSheetdata);
            }else if(weekdays.equalsIgnoreCase("FR"))
            {
                timeSheet.put("FR", timeSheetdata);
            }else if(weekdays.equalsIgnoreCase("SA"))
            {
                timeSheet.put("SA", timeSheetdata);
            }

            object.put("timeSheet",timeSheet);
            jObject.put(",",object);
            webSocket.send(jObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}


