package com.example.kinjalkumaridhimmarmonikakumari_comp304lab6_ex1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kinjalkumaridhimmarmonikakumari_comp304lab6_ex1.constants.Constants;

public class MessageActivity extends AppCompatActivity {

    //UI Related
    EditText eText;
    EditText txtPhone;
    TextView textMessage;
    Button sensSMSButton;

    public static final String SENT = "SMS_SENT";
    public static final String DELIVERED = "SMS_DELIVERED";

    PendingIntent sentPI, deliveredPI;
    BroadcastReceiver smsSentReceiver, smsDeliveredReceiver;
    IntentFilter intentFilter;

    BroadcastReceiver intentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            textMessage = findViewById(R.id.textMessage);
            textMessage.setText(textMessage.getText() + "\n" +
                    intent.getExtras().getString("sms"));
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        sensSMSButton = findViewById(R.id.send_btn);

        //Request Permission
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.SEND_SMS,
                Manifest.permission.READ_PHONE_STATE
        }, Constants.SMS_RECEIVE_PERMISSION_REQUEST);

        Bundle extras = getIntent().getExtras();
        String contactName = "";
        if(extras != null){
            contactName = extras.getString("contactName");
        }
        textMessage = findViewById(R.id.textMessage);
        textMessage.setMovementMethod(ScrollingMovementMethod.getInstance());
        TextView tView = findViewById(R.id.person_textView);
        tView.setText(contactName);

        eText = findViewById(R.id.edit_text_message);
        txtPhone = findViewById(R.id.edit_text_phone_num);

        sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), PendingIntent.FLAG_IMMUTABLE);
        deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED),  PendingIntent.FLAG_IMMUTABLE);

        intentFilter = new IntentFilter();
        intentFilter.addAction("SMS_RECEIVED_ACTION");

        sensSMSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = txtPhone.getText().toString();
                String message = eText.getText().toString();
                sendSMS(phoneNumber, message);
                textMessage.setText(textMessage.getText() + "\n" + eText.getText());
            }
        });

        registerReceiver(intentReceiver, intentFilter);
    }

    private void sendSMS(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }

    @Override
    protected void onResume() {
        super.onResume();

        smsSentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS sent", Toast.LENGTH_LONG).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        smsDeliveredReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK: Toast.makeText(getBaseContext(), "SMS delivered",
                            Toast.LENGTH_LONG).show();
                        break;
                    case Activity.RESULT_CANCELED: Toast.makeText(getBaseContext(), "SMS not delivered",
                            Toast.LENGTH_LONG).show();
                        break;
                }
            }
        };

        registerReceiver(smsDeliveredReceiver, new IntentFilter(DELIVERED));
        registerReceiver(smsSentReceiver, new IntentFilter(SENT));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(smsSentReceiver);
        unregisterReceiver(smsDeliveredReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(intentReceiver);
    }


}