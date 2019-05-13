package org.mekonecampus.mekonecapstone;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipeDirectionalView;
import com.mindorks.placeholderview.listeners.ItemRemovedListener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.mekonecampus.mekonecapstone.AddActivity.lati;
import static org.mekonecampus.mekonecapstone.AddActivity.longi;
import static org.mekonecampus.mekonecapstone.AddActivity.myAddress;

public class MainActivity extends AppCompatActivity implements TinderCard.Callback {

    private SwipeDirectionalView mSwipeView;
    private static Activity activity;
    private Context mContext;
    private int mAnimationDuration = 300;
    private boolean isToUndo = false;
    static String category = "sokika";
    //static int count = 0;
    static List<Article> articles = new ArrayList<>();
    static Article arto = new Article();
    static Article fArto = new Article();
    static Visitor visito = new Visitor();
    static String myAddress;
    static Double longi = 0.0;
    static Double lati = 0.0;
    static String zipcode;
    static String myState;
    static String myCountry;
    static String mySecondLineAddr;
    static String myHome;
    static String myApartment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();
        activity = MainActivity.this;
        mSwipeView = (SwipeDirectionalView) findViewById(R.id.swipeView);

        int bottomMargin = Utils.dpToPx(160);
        Point windowSize = Utils.getDisplaySize(getWindowManager());
        mSwipeView.getBuilder()
                .setDisplayViewCount(3)
                .setIsUndoEnabled(true)
                .setSwipeVerticalThreshold(Utils.dpToPx(50))
                .setSwipeHorizontalThreshold(Utils.dpToPx(50))
                .setHeightSwipeDistFactor(10)
                .setWidthSwipeDistFactor(5)
                .setSwipeDecor(new SwipeDecor()
                        .setViewWidth(windowSize.x)
                        .setViewHeight(windowSize.y - bottomMargin)
                        .setViewGravity(Gravity.TOP)
                        .setPaddingTop(20)
                        .setSwipeAnimTime(mAnimationDuration)
                        .setRelativeScale(0.01f)
                        .setSwipeInMsgLayoutId(R.layout.tinder_swipe_in_msg_view)
                        .setSwipeOutMsgLayoutId(R.layout.tinder_swipe_out_msg_view));


        Point cardViewHolderSize = new Point(windowSize.x, windowSize.y - bottomMargin);

        try {
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 200);
            } else {
                Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                longi = location.getLongitude();
                lati = location.getLatitude();
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
                Toast.makeText(mContext, zipcode, Toast.LENGTH_LONG).show();
            }
        }catch (Exception ex){
            Toast.makeText(this, "Geocoder error, please reload!", Toast.LENGTH_SHORT).show();
        }

        Article a = new Article();
        a.PicUrl = "https://mekonecampusapistorage.blob.core.windows.net/campusimages/diasporaLogoBig.JPG";
        a.Title = "MékonéCampus";
        a.Custom2 = "98007";
        a.Category = category;
        a.Custom3 = "Centre Polyvalent";
        a.Custom4 = "ok";
        a.Custom5 = "WA";
        a.DateCreated = "09/15/2011";
        a.Body = "Bellevue WA";
        articles.add(a);

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
        visito.Custom5 = zipcode;
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
            new CreateVisitor(this).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //call api
        try {
            new GetArticles(this).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //get most recent articles
        try {
            for (Article article : articles) {
                if (article.Custom2 != null) {
                    if (article.Custom4 != null) {
                        if (!article.Custom4.equals("flagged")) {
                            if (article.Custom2.equals(zipcode) || article.Custom3.equals("Centre Polyvalent")) {
                                arto = article;
                                String[] tp = arto.Body.split(",");
                                if(tp.length > 2) {
                                    arto.Body = tp[0] + tp[1];
                                }
                                arto.ViewsNumber += 1;
                                //call api
                                if (!a.Custom4.equals("ok")) {
                                    try {
                                        new IncreaseView(this).execute();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                mSwipeView.addView(new TinderCard(mContext, article, cardViewHolderSize, this));
                            }
                        }
                    }
                }
            }
        }catch (Exception ex){
            Toast.makeText(this, "Query error, please reload!", Toast.LENGTH_SHORT).show();
        }

        findViewById(R.id.rejectBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fArto = null;
                mSwipeView.doSwipe(false);
            }
        });

        findViewById(R.id.acceptBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mSwipeView.doSwipe(true);
                Intent intent = new Intent(v.getContext(), AddActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.flagBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arto = fArto;
                if(arto != null) {
                    //call api
                    try {
                        new EditArticle(MainActivity.this).execute();
                        Toast.makeText(MainActivity.this, "Thanks for cleaning up!", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        findViewById(R.id.undoBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mSwipeView.undoLastSwipe();
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        mSwipeView.addItemRemoveListener(new ItemRemovedListener() {
            @Override
            public void onItemRemoved(int count) {
                if (isToUndo) {
                    isToUndo = false;
                    mSwipeView.undoLastSwipe();
                }
            }
        });
    }

    @Override
    public void onSwipeUp() {
        //Toast.makeText(this, "Awesome!!!", Toast.LENGTH_SHORT).show();
        isToUndo = true;
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

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Article APICall() throws IOException {
        try {
            arto.Custom4 = "flagged";
            URL url = new URL("http://mekonecampusapi.azurewebsites.net/api/Articles");
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setRequestMethod("PUT");

            httpCon.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            ObjectMapper mapper = new ObjectMapper();
            String input = mapper.writeValueAsString(arto);
            OutputStream os = httpCon.getOutputStream();
            os.write(input.getBytes());
            os.write(input.getBytes("UTF-8"));
            os.flush();
            os.close();
            httpCon.connect();

            if (httpCon.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + httpCon.getResponseCode());
            }

            //read the inputstream and print it
            String result;
            BufferedInputStream bis = new BufferedInputStream(httpCon.getInputStream());
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            int result2 = bis.read();
            while(result2 != -1) {
                buf.write((byte) result2);
                result2 = bis.read();
            }
            result = buf.toString();
            //System.out.println(result);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return arto;
    }

    public static Article APICallView() throws IOException {
        try {
            arto.ViewsNumber += 1;
            URL url = new URL("http://mekonecampusapi.azurewebsites.net/api/Articles");
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setRequestMethod("PUT");

            httpCon.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            ObjectMapper mapper = new ObjectMapper();
            String input = mapper.writeValueAsString(arto);
            OutputStream os = httpCon.getOutputStream();
            os.write(input.getBytes());
            os.write(input.getBytes("UTF-8"));
            os.flush();
            os.close();
            httpCon.connect();

            if (httpCon.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + httpCon.getResponseCode());
            }

            //read the inputstream and print it
            String result;
            BufferedInputStream bis = new BufferedInputStream(httpCon.getInputStream());
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            int result2 = bis.read();
            while(result2 != -1) {
                buf.write((byte) result2);
                result2 = bis.read();
            }
            result = buf.toString();
            //System.out.println(result);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return arto;
    }

    private static class EditArticle extends AsyncTask<Void, Void, Article> {
        private WeakReference<Activity> weakActivity;
        public EditArticle(Activity activity) {
            weakActivity = new WeakReference<>(activity);
        }
        @Override
        protected Article doInBackground(Void... voids) {
            Activity activity = weakActivity.get();
            if(activity == null) {
                return null;
            }
            try {
                APICall();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private static class IncreaseView extends AsyncTask<Void, Void, Article> {
        private WeakReference<Activity> weakActivity;
        public IncreaseView(Activity activity) {
            weakActivity = new WeakReference<>(activity);
        }
        @Override
        protected Article doInBackground(Void... voids) {
            Activity activity = weakActivity.get();
            if(activity == null) {
                return null;
            }
            try {
                APICallView();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    //make api call
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
                articles = mapper.readValue(output, new TypeReference<List<Article>>() {});
            }
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return articles;
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
                articles = CallMekone(category);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(articles.size() == 0) {
                return null;
            }
            return articles;
        }
    }
}

