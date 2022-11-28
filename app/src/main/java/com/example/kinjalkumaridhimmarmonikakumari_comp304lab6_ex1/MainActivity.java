package com.example.kinjalkumaridhimmarmonikakumari_comp304lab6_ex1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.kinjalkumaridhimmarmonikakumari_comp304lab6_ex1.constants.Constants;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String[] contacts;
    private ListView contactsListView;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contactsListView = findViewById(R.id.android_list);

        ActivityCompat.requestPermissions(this, new String[] {
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.SEND_SMS,
                Manifest.permission.READ_PHONE_STATE
        }, Constants.SMS_RECEIVE_PERMISSION_REQUEST);

        this.getSupportActionBar().setTitle("My Messaging App");
        TextView contactTextViewTitle = new TextView(getApplicationContext());
        contactTextViewTitle.setText("My Contacts");

        contactsListView.addHeaderView(contactTextViewTitle);
        contactsListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
        contactsListView.setTextFilterEnabled(true);

        contacts = getResources().getStringArray(R.array.contacts);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, contacts);
        contactsListView.setAdapter(adapter);

        intent = new Intent(this, MessageActivity.class);
        contactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                int itemPosition = position;

                String item = (String) contactsListView.getItemAtPosition(position);
                intent.putExtra("contactName", item);
                startActivity(intent);
            }
        });
    }
}