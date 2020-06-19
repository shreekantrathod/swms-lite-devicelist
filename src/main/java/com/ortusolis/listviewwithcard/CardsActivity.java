package com.ortusolis.listviewwithcard;

import android.annotation.SuppressLint;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.os.StrictMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.OkHttpClient;
import okhttp3.WebSocket;


public class CardsActivity extends AppCompatActivity {
    WebSocket webSocket;
    String response, clientId, connection, description, icons;

    String deviceId, temps;

    ProgressBar progressBar;
    // List<String> arrPackageData;
    List<String> arrDeviceId;
    List<String> arrDeviceName;

    private OkHttpClient client;
    SharedPreferences sharedPreferences;
    View view;
    private static final int NORMAL_CLOSURE_STATUS = 10000;


    /*******************weather*****************/
    TextView view_city;
    TextView view_temp;
    TextView view_desc;

    ImageView view_weather;
    String City = "Vijayapura";
    EditText search;
    FloatingActionButton search_floating;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);
        sharedPreferences = getSharedPreferences("clientPreferences", MODE_PRIVATE);
        clientId = sharedPreferences.getString("clientId", "");
        Intent intent = getIntent();
        connection = intent.getStringExtra("connection");
        //   WebSocketWifi wifi= new WebSocketWifi(this);

        arrDeviceId = new ArrayList<>();
        arrDeviceName = new ArrayList<>();
        launchRingDialog(view);
        ListView lvCards = (ListView) findViewById(R.id.list_cards);
        Gson gson = new Gson();
        String jsonDeviceId = sharedPreferences.getString("deviceIdList", "");
        String jsonDeviceName = sharedPreferences.getString("deviceNameList", "");
        if (jsonDeviceId.isEmpty() && jsonDeviceName.isEmpty()) {
            Toast.makeText(CardsActivity.this, "There is something error", Toast.LENGTH_LONG).show();
        } else {
            Type type = new TypeToken<List<String>>() {
            }.getType();
            arrDeviceId  = gson.fromJson(jsonDeviceId, type);
            arrDeviceName=gson.fromJson(jsonDeviceName,type);
            CardsAdapter adapter = new CardsAdapter(this);
            lvCards.setAdapter(adapter);
            for (String data : arrDeviceName) {
                adapter.addAll(new CardModel(R.drawable.image1, data));
            }
        }

        /**************device List Iterator ****************************/
       /* Gson gson = new Gson();
        // String d=deviceList;
        Type type = new TypeToken<List<String>>() {
        }.getType();*/

      /*  mImageView=(ImageView) findViewById(R.id.waterdrop);
        mImageView.setImageResource(R.drawable.reddrop);*/

        response = WebSocketWifi.getInstance(getApplicationContext()).deviceresponse;
        if (response.equalsIgnoreCase("200")) {

            lvCards.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    CardModel item = (CardModel) adapterView.getItemAtPosition(position);
                    String ListViewClickedValue = arrDeviceId.get(position);

                    Intent intent = new Intent(CardsActivity.this, DeviceInformation.class);
                    intent.putExtra("ListViewClickedValue", ListViewClickedValue);
                    intent.putExtra("temps", temps);
                    intent.putExtra("description", description);
                    intent.putExtra("icons", icons);
                    intent.putExtra("city", City);
                    startActivity(intent);
                }
            });
        } else {
            Toast.makeText(this, "" + response, Toast.LENGTH_SHORT).show();
        }


        /*weather forecast************/
        view_city = findViewById(R.id.town);
        view_city.setText("");
        view_temp = findViewById(R.id.temp);
        view_temp.setText("");
        view_desc = findViewById(R.id.desc);
        view_desc.setText("");

        view_weather = findViewById(R.id.wheather_image);
        /*search=findViewById(R.id.search_edit);
        search_floating=findViewById(R.id.floating_search);*/

      /*  search_floating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //hide Keyboard
                InputMethodManager imm=(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getRootView().getWindowToken(),0);
                api_key(String.valueOf(City));
            }
        });
*/
        api_key(String.valueOf(City));

    }
    /************************progress bar ******************/
    public void launchRingDialog(View view) {

        final ProgressDialog ringProgressDialog = ProgressDialog.show(CardsActivity.this, "Processing ...", "Please Wait ...", false);
        ringProgressDialog.setCancelable(false);
        //ringProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ringProgressDialog.getWindow().setGravity(Gravity.BOTTOM);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Thread.sleep(2000);
                } catch (Exception e) {

                }
                ringProgressDialog.dismiss();
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
if(connection.equalsIgnoreCase("1")) {
    getMenuInflater().inflate(R.menu.main, menu);
}else
{
    getMenuInflater().inflate(R.menu.disconnect,menu);
}
        return super.onCreateOptionsMenu(menu);
    }




















    private void api_key(final String City) {
        OkHttpClient client=new OkHttpClient();

        Request request=new Request.Builder()
                .url("https://api.openweathermap.org/data/2.5/weather?q="+City+"&appid=a6f41d947e0542a26580bcd5c3fb90ef&units=metric")
                .get()
                .build();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            Response response= client.newCall(request).execute();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {

                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String responseData= response.body().string();
                    try {
                        JSONObject json=new JSONObject(responseData);
                        JSONArray array=json.getJSONArray("weather");
                        JSONObject object=array.getJSONObject(0);

                         description=object.getString("description");
                         icons = object.getString("icon");

                        JSONObject temp1= json.getJSONObject("main");
                        Double Temperature=temp1.getDouble("temp");

                        setText(view_city,City);

                        temps=Math.round(Temperature)+" °C";
                        setText(view_temp,temps);
                        setText(view_desc,description);
                        setImage(view_weather,icons);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            });
        }catch (IOException e){
            e.printStackTrace();
        }


    }
    private void setText(final TextView text, final String value){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setText(value);
            }
        });
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


