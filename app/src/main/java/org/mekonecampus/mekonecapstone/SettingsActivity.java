package org.mekonecampus.mekonecapstone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private static Context mContext;
    private static Activity activity;
    static String category = "settings";
    static Article arto = new Article();
    static List<Article> articles = new ArrayList<>();

    static EditText editZipcode;
    static EditText editEmail;

    static RadioGroup radiogroupAddress;
    static RadioGroup radiogroupNotifications;
    static RadioGroup radiogroupOnlineStatus;

    static RadioButton radioAddressYes;
    static RadioButton radioAddressNo;

    static RadioButton radioNotificationsYes;
    static RadioButton radioNotificationsNo;

    static RadioButton radioOnlineStatusOn;
    static RadioButton radioOnlineStatusOff;

    static String addrOption;
    static String notifOption;
    static String statOption;

    static String myAddress;
    static Double longi;
    static Double lati;
    static String zipcode;
    static String myState;
    static String myCountry;
    static String mySecondLineAddr;
    static String myHome;
    static String myApartment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mContext = getApplicationContext();
        activity = SettingsActivity.this;

        editZipcode = findViewById(R.id.editAddress);
        editEmail = findViewById(R.id.editEmail);

        Intent intent = getIntent();
        Bundle boo = intent.getExtras();

        assert boo != null;
        if (boo != null) {
            if (boo.getString("myZipo") != null) {
                if (boo.getString("myZipo").length() != 0) {
                    zipcode = boo.getString("myZipo");
                    //Toast.makeText(mContext, "current zip : " + zipcode, Toast.LENGTH_LONG).show();
                }
            }
        }

        editZipcode.setText(zipcode);

        //one
        radiogroupAddress =(RadioGroup)findViewById(R.id.rgAddress);
        radioAddressYes =(RadioButton)findViewById(R.id.radio_pirates);
        radioAddressNo =(RadioButton)findViewById(R.id.radio_ninjas);

        if("ok".equals("ok")){
            radioAddressNo.setChecked(true);
        }else{
            //radioAddressNo.setChecked(true);
        }

        radiogroupAddress.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.radio_pirates){
                    addrOption = "Yes";
                }
                if(checkedId == R.id.radio_ninjas){
                    addrOption = "No";
                }
            }
        });

        //two
        radiogroupNotifications =(RadioGroup)findViewById(R.id.rgNotifications);
        radioNotificationsYes =(RadioButton)findViewById(R.id.radio_pirates2);
        radioNotificationsNo =(RadioButton)findViewById(R.id.radio_ninjas2);

        if("ok".equals("ok")){
            radioNotificationsYes.setChecked(true);
        }else{
            radioNotificationsNo.setChecked(true);
        }

        radiogroupNotifications.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.radio_pirates2){
                    notifOption = "Yes";
                }
                if(checkedId == R.id.radio_ninjas2){
                    notifOption = "No";
                }
            }
        });

        //three
        radiogroupOnlineStatus =(RadioGroup)findViewById(R.id.rgOnlineStatus);
        radioOnlineStatusOn =(RadioButton)findViewById(R.id.radio_pirates3);
        radioOnlineStatusOff =(RadioButton)findViewById(R.id.radio_ninjas3);

        if("ok".equals("ok")){
            radioOnlineStatusOn.setChecked(true);
        }else{
            radioOnlineStatusOff.setChecked(true);
        }

        radiogroupOnlineStatus.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.radio_pirates3){
                    statOption = "On";
                }
                if(checkedId == R.id.radio_ninjas3){
                    statOption = "Off";
                }
            }
        });

        findViewById(R.id.cancelBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AddActivity.class);
                intent.putExtra("myZipo", editZipcode.getText().toString());
                startActivity(intent);
            }
        });

        findViewById(R.id.webBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://www.mekonecampus.org";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        findViewById(R.id.homeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                intent.putExtra("myZipo", editZipcode.getText().toString());
                startActivity(intent);
            }
        });

        findViewById(R.id.saveBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                intent.putExtra("myZipo", editZipcode.getText().toString());
                startActivity(intent);
            }
        });
    }
}
