package com.ortusolis.listviewwithcard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketWifi extends WebSocketListener {


    public WebSocket webSocket;
    OkHttpClient client;
    SharedPreferences sharedPreferences;
    private static final int NORMAL_CLOSURE_STATUS = 100000;
    String clientId,response,initresponse,deviceresponse,status,mode,state,deviceId,action,deviceStatus,deviceName;
    public  String  currentDevice="";
    private static WebSocketWifi mInstance;
    List deviceIdList;
    List deviceNameList;
    private static Context mCtx;

    ArrayList<String> res= new ArrayList<String>();
    ArrayList<String> curentRes= new ArrayList<String>();
    static  JSONObject days;
    WebSocketWifi(Context context) {
        mCtx = context;
        deviceIdList = new ArrayList<>();
        deviceNameList = new ArrayList<>();
        sharedPreferences = context.getSharedPreferences("clientPreferences", Activity.MODE_PRIVATE);
    }



    public static synchronized WebSocketWifi getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new WebSocketWifi(context);
        }
        return mInstance;
    }

public  void start() {
        try {
            client = new OkHttpClient();
            /*Main server*/
            Request request = new Request.Builder().url("ws://194.59.165.40:2500").build();
            /*test server*/
            //  Request request = new Request.Builder().url("ws://35.157.227.54:1500").build();
            EcoWebSocketListerner listerner = new EcoWebSocketListerner();
            webSocket = client.newWebSocket(request, listerner);
            client.dispatcher().executorService().shutdown();
        }catch (Exception e)
        {
            e.printStackTrace();

        }
    }

    private final class EcoWebSocketListerner extends WebSocketListener {

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
           /* JSONObject jObject = new JSONObject();
            try {
                jObject.put("action", "APP_INIT");
                jObject.put("userId", "8971492516");
                webSocket.send(jObject.toString());
            }
            catch(JSONException e)
            {
                e.printStackTrace();
            }*/
           /* JSONObject jObject = new JSONObject();
            try {
                jObject.put("action", "APP_INIT");
                jObject.put("userId", "xyz");
                webSocket.send(jObject.toString());*/
             /*   if(clientId!=null) {
                    //  String jsonText = editor.toString();
                    webSocket.send(jObject.toString());
                }
                else
                {

                }*/

          /*  } catch (JSONException e) {
                e.printStackTrace();
            }*/
        }

        @SuppressLint("LongLogTag")
        @Override
        public void onMessage(WebSocket webSocket, String text) {
            try {
                JSONObject ob = new JSONObject(text);
                 action =ob.getString("resp");
                }catch (JSONException e)
            {
                e.printStackTrace();
            }
            switch (action) {
                /****************************** Response for Initial Handshake *************************/
                    case "APP_INIT":
                        try {
                            JSONObject obj = new JSONObject(text);
                            Log.d("RespoOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO", text);
                            clientId = obj.getString("clientId");
                           initresponse = obj.getString("statusCode");
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor = sharedPreferences.edit();
                            editor.putString("clientId", clientId);
                            editor.commit();
                            editor.apply();
              /*  if(initresponse.equalsIgnoreCase("200"))
                {
                    getDeviceList(webSocket);
                }*/
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        break;
                        /**********************Response DeviceList**************************/
                    case "GET_DEVICE_LIST":
                        try
                        {
                        JSONObject obj = new JSONObject(text);
                        Log.d("RespoOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO", text);
                        deviceresponse = obj.getString("statusCode");
                        deviceId=obj.getString("deviceList");
                           JSONArray jsonarray = new JSONArray(obj.getJSONArray("deviceList").toString());
                            for (int i = 0; i < jsonarray.length(); i++) {
                                JSONObject jsonobject = jsonarray.getJSONObject(i);
                                String id = jsonobject.getString("deviceId");
                                String name = jsonobject.getString("deviceName");
                                deviceIdList.add(id);
                               deviceNameList.add(name);
                              /*  HashMap<String, String> contact = new HashMap<>();
                                contact.put("name", name);
                                contact.put("id", id);
                                arrPackageData.add(contact);*/
                            }
                            Gson gson = new Gson();
                            String dId = gson.toJson(deviceIdList);
                            String dName = gson.toJson(deviceNameList);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("deviceIdList",dId );
                            editor.putString("deviceNameList",dName);
                            editor.commit();
                       // deviceStatus=obj.getString("deviceStatus");
                        }catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                        break;
                        /****************Response Device Current Info***************/
                case "GET_DEVICE_CURRENT_INFO":
                    try
                    {
                        JSONObject obj = new JSONObject(text);
                        Log.d("RespoOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO", text);
                        response = obj.getString("statusCode");
                        currentDevice=obj.getString("deviceId");
                        status=obj.getString("status");
                       mode=obj.getString("mode");
                        /*curentRes.add(state);
                        curentRes.add(mode);
                        DeviceInformation.responceDeviceCurrentInfo(curentRes);*/
                    }catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                    break;

                    /*********************** Mode Change Require ******************/
                case "MODE_CHANGE_REQ":
                    try
                    {
                        JSONObject obj = new JSONObject(text);
                        Log.d("RespoOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO", text);
                        response = obj.getString("statusCode");
                        status=obj.getString("status");
                        deviceId=obj.getString("deviceId");
                        mode=obj.getString("mode");
                    }catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                    break;
                case "MOTOR_ON_OFF_REQ":
                    try
                    {
                        JSONObject obj = new JSONObject(text);
                        Log.d("RespoOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO", text);
                        response = obj.getString("statusCode");
                        deviceId=obj.getString("deviceId");
                        state=obj.getString("state");
                    }catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                    break;
                    /**************** get device info *******************************/
                case "GET_DEVICE_INFO":
                    try
                    {
                        JSONObject obj = new JSONObject(text);
                        Log.d("RespoOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO", text);
                        response=obj.getString("resp");
                        deviceId=obj.getString("deviceId");
                        status=obj.getString("status");
                        String deviceOn=obj.getString("deviceOn");
                        String deviceOff=obj.getString("deviceOff");
                         days = obj.getJSONObject("timeSheet");
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor = sharedPreferences.edit();
                        editor.putString("timeshetList", days.toString());
                        editor.commit();
                        editor.apply();
                    if(days.has("SU")) {
                        String sunday = days.getString("SU");
                        String Hour="";
                        Hour=days.getString("SU").substring(0,2);
                        String min="";
                        min=days.getString("SU").substring(2,4);
                        String duration="";
                     /*  duration=days.getString("SU").substring(4,6);
                        char Repeat= '\0';
                        Repeat=days.getString("SU").charAt(6);
                        char EnableOrDisable= '\0';
                        EnableOrDisable=days.getString("SU").charAt(7);
                        sundayValue=Hour+min+duration+Repeat+EnableOrDisable;
                        res.add(days.toString());
                        res.add(sunday);
*/
                        //res.add()
           //             Log.e("days",Hour+min+duration+Repeat+EnableOrDisable);
                    }
                    if(days.has("MO")) {
                        String monday = days.getString("MO");
                        res.add(monday);
                    }
                    if(days.has("TU")) {
                        String tuesday = days.getString("TU");
                        res.add(tuesday);
                    }
                    if(days.has("WE")) {
                        String wensday = days.getString("WE");
                        res.add(wensday);
                    }
                    if(days.has("TH")) {
                        String thursday = days.getString("TH");
                        res.add(thursday);
                    }
                    if(days.has("FR")) {
                        String friday = days.getString("FR");
                        res.add(friday);
                    }
                    if (days.has("SA")) {
                       String Saturday = days.getString("SA");
                       res.add(Saturday);
                        }
                        //String timesheet=obj.getString("timeSheet");
                       // DeviceInformation.responceTimeSheetValue(res);
                    }catch (JSONException e)
                    {
                        e.printStackTrace();
                    } /*catch (IOException e) {
                        e.printStackTrace();
                    }*/
                    break;
/*****************************Response of Set Timer *************************************/
                case "SET_TIMER":
                    try
                    {
                        JSONObject obj = new JSONObject(text);
                        Log.d("RespoOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO", text);
                        response = obj.getString("statusCode");
                        deviceId=obj.getString("deviceId");
                     //   DeviceInformation.responceTimeSheetValue(res);
                    }catch (JSONException e)
                    {
                        e.printStackTrace();
                    }

                    break;
            }

    }
            }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null);
            //output("closing " + code + "/" + reason);

        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            //output("Error :" + t.getMessage());
           // Toast.makeText(getInstance(co),"Hello Javatpoint",Toast.LENGTH_SHORT).show();
        }



    }
