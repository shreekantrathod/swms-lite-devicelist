package com.ortusolis.listviewwithcard;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;

public class MultiWeekDaysSelect extends Activity implements
        View.OnClickListener {
    Button button;
    ListView listView;
    ArrayAdapter<String> adapter;
    CheckBox repeatCheckBox;
    String repeat="";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_week_days_select);
        repeatCheckBox=(CheckBox) findViewById(R.id.repeatCheckBox);
        listView = (ListView) findViewById(R.id.weekDayList);
        button=(Button) findViewById(R.id.btnGetSelected);
        String[] week = getResources().getStringArray(R.array.week_days);
        adapter = new ArrayAdapter<String>(this, R.layout.check_list, week);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(adapter);

        button.setOnClickListener(this);
        repeatCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(repeatCheckBox.isChecked())
                {
                    repeat="1";
                }else
                {
                    repeat="0";
                }
            }
    });
    }

    public void onClick(View v) {

       String[] week=getResources().getStringArray(R.array.week_days);
        SparseBooleanArray checked = listView.getCheckedItemPositions();
        ArrayList<String> selectedItems = new ArrayList<String>();
        for (int i = 0; i < week.length; i++)
        {
            // Item position in adapter
            int position = checked.keyAt(i);
            // Add sport if it is checked i.e.) == TRUE!
            if (checked.valueAt(i)) {
                selectedItems.add(adapter.getItem(position));
            }
        }

        String[] outputStrArr = new String[selectedItems.size()];

        for (int i = 0; i < selectedItems.size(); i++) {
            outputStrArr[i] = selectedItems.get(i).substring(0,2).toUpperCase();


        Intent intent = new Intent();
        intent.putExtra("weekdays", outputStrArr[i].toString());
        intent.putExtra("repeat",repeat.toString());
        setResult(RESULT_OK, intent);
        finish();

        }
       /* intent.putExtra("selectedItems", outputStrArr);
        startActivity(intent);*/
    }
}