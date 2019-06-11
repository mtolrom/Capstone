package org.mekonecampus.mekonecapstone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
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
import java.util.List;
import java.util.stream.Collectors;

public class FarmActivity extends AppCompatActivity {

    static EditText customzip;
    static String category = "sokika";
    static List<Article> mData = new ArrayList<>();
    static Visitor visito = new Visitor();
    static String myAddress;
    static Double longi = 0.0;
    static Double lati = 0.0;
    static String zipcode;
    static String SettingZipcode;
    static String myState;
    static String myCountry;
    static String mySecondLineAddr;
    static String myHome;
    static String myApartment;
    //static EditText customzip;
    private static Activity activity;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farm);

        mContext = getApplicationContext();
        activity = FarmActivity.this;

        try {
            LocationDetector myloc = new LocationDetector(
                    FarmActivity.this);
            if (myloc.canGetLocation) {
                lati = myloc.getLatitude();
                longi = myloc.getLongitude();
            }
        }catch (Exception ex){
            Toast.makeText(this, "Location error, please reload!", Toast.LENGTH_SHORT).show();
        }

        Geocoder gCoder = new Geocoder(mContext);
        try {
            List<Address> addresses = null;
            if (lati != null) {
                if (longi != null) {
                    try {
                        addresses = gCoder.getFromLocation(lati, longi, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (addresses != null && addresses.size() > 0) {
                myAddress = addresses.get(0).getAddressLine(0);
                String[] adds = myAddress.split(" ");
                myHome = adds[0];
                myApartment = addresses.get(0).getAddressLine(1);
                myState = adds[adds.length - 3];
                myCountry = adds[adds.length - 1];
                zipcode = adds[adds.length - 2].replace(',', ' ');
                SettingZipcode = adds[adds.length - 2].replace(',', ' ');
                //Toast.makeText(mContext, zipcode, Toast.LENGTH_LONG).show();
            }
        }catch (Exception ex){
            ex.printStackTrace();
            Toast.makeText(this, "Geocoder error, please reload!", Toast.LENGTH_SHORT).show();
        }

        Intent intent = getIntent();
        Bundle boo = intent.getExtras();

        assert boo != null;
        if (boo != null) {
            if (boo.getString("myZipo") != null) {
                if (boo.getString("myZipo").length() != 0) {
                    zipcode = boo.getString("myZipo");
                }
            }
            Toast.makeText(mContext, zipcode, Toast.LENGTH_LONG).show();
        }

        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Calendar calobj = Calendar.getInstance();
        String dt = df.format(calobj.getTime()).toString().replace('/', '-').replace(' ', '-').replace(':', '-');

        visito.Browser = myAddress;
        visito.BrowserDetails = myHome;
        visito.Category = category;
        visito.Custom1 = myState;
        visito.Custom2 = myCountry;
        visito.Custom3 = myApartment;
        visito.Custom4 = mySecondLineAddr;
        if(zipcode.trim().equals(SettingZipcode.trim())) {
            visito.Custom5 = "same";
        }else{
            visito.Custom5 = SettingZipcode;
        }
        visito.Hostname = longi.toString();
        visito.Id = "mekonecampus";
        visito.Mobile = lati.toString();
        visito.Status = "active";
        visito.VisitDate = df.format(calobj.getTime());
        visito.NetworkIP = android.os.Build.DEVICE;
        visito.UrlReferer = android.os.Build.MODEL;
        visito.RawUrlPage = Build.MANUFACTURER;
        visito.PartitionKey = "sokika";
        visito.RowKey = dt;
        //visito.Key = "";

        //call api
        try {
            new FarmActivity.CreateVisitor(this).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //call api
        try {
            new GetArticles(this).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Article a = new Article();
        a.PicUrl = "https://mekonecampusapistorage.blob.core.windows.net/campusimages/diasporaLogoBig.JPG";
        a.Title = "MékonéCampus";
        a.Custom2 = "98007";
        a.Category = category;
        a.Custom3 = "Centre Polyvalent";
        a.Custom4 = "www.mekonecampus.org";
        a.Custom5 = "WA";
        a.DateCreated = "09/15/2011";
        a.Body = "Bellevue WA, United States";
        mData.add(a);

        Article b = new Article();
        b.PicUrl = "https://mekonecampusapistorage.blob.core.windows.net/campusimages/boule.jpeg";
        b.Title = "Boule de manioc";
        b.Custom2 = "98007";
        b.Category = category;
        b.Custom3 = "Au pays Ngambaye";
        b.Custom4 = "www.mekonetolrom.com";
        b.Custom5 = "WA";
        b.DateCreated = "12/15/2015";
        b.Body = "Moundou Chad (Central Africa)";
        mData.add(b);

        RecyclerView myRecyclerview = (RecyclerView) findViewById(R.id.recyclerView_id);
        RecyclerViewAdapter myAdapter = new RecyclerViewAdapter(this,mData);
        myRecyclerview.setLayoutManager(new GridLayoutManager(this,1));
        myRecyclerview.setAdapter(myAdapter);

        findViewById(R.id.houseBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inta = new Intent(v.getContext(), MainActivity.class);
                inta.putExtra("myZipo", zipcode);
                startActivity(inta);
            }
        });

        findViewById(R.id.addBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AddActivity.class);
                intent.putExtra("myZipo", zipcode);
                startActivity(intent);
            }
        });

        findViewById(R.id.flagBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SettingsActivity.class);
                intent.putExtra("myZipo", zipcode);
                startActivity(intent);
            }
        });

        findViewById(R.id.refreshBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), FarmActivity.class);
                intent.putExtra("myZipo", zipcode);
                startActivity(intent);
            }
        });
    }

    private static class CreateVisitor extends AsyncTask<Void, Void, Visitor> {
        private WeakReference<Activity> weakActivity;
        public CreateVisitor(Activity activity) {
            weakActivity = new WeakReference<>(activity);
        }
        @Override
        protected Visitor doInBackground(Void... voids) {
            Activity activity = weakActivity.get();
            if(activity == null) {
                return null;
            }
            try {
                APICallVisitor();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static void APICallVisitor() throws IOException {
        try {
            URL url = new URL("http://mekonecampusapi.azurewebsites.net/api/Visitors");
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setRequestMethod("POST");

            httpCon.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            ObjectMapper mapper = new ObjectMapper();
            String input = mapper.writeValueAsString(visito);
            OutputStream os = httpCon.getOutputStream();
            os.write(input.getBytes());
            os.write(input.getBytes("UTF-8"));
            os.flush();
            os.close();
            httpCon.connect();

            int rCode = httpCon.getResponseCode();
            /*if (httpCon.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + httpCon.getResponseCode());
            }*/

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Article> CallMekone(String category) throws IOException {
        try {
            URL url = new URL("http://mekonecampusapi.azurewebsites.net/api/Articles/mekonecampus?category=" + category + "&status=active");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));
            String output;
            //System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                ObjectMapper mapper = new ObjectMapper();
                mData = mapper.readValue(output, new TypeReference<List<Article>>() {});
            }
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Article> artamis = new ArrayList<>();
        for(int i = 0; i < mData.size(); i++){
            if(mData.get(i).Custom4.startsWith("www")){
                if(zipcode != null) {
                    String strZip = mData.get(i).Custom2.trim();
                    if(strZip != null) {
                        String strHere = zipcode.trim();
                        if (strZip.equals(strHere)) {
                            artamis.add(mData.get(i));
                        }
                    }
                }
            }
        }
        mData = artamis;
        return mData;
    }

    private static class GetArticles extends AsyncTask<Void, Void, List<Article>> {
        private WeakReference<Activity> weakActivity;
        public GetArticles(Activity activity) {
            weakActivity = new WeakReference<>(activity);
        }
        @Override
        protected List<Article> doInBackground(Void... voids) {
            Activity activity = weakActivity.get();
            if(activity == null) {
                return null;
            }
            try {
                mData = CallMekone(category);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(mData.size() == 0) {
                return null;
            }
            return mData;
        }
    }
}
