package org.mekonecampus.mekonecapstone;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.mime.HttpMultipartMode;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;

public class AddActivity extends AppCompatActivity {

    private static Context mContext;
    private static Activity activity;
    public static final int PICK_IMAGE = 1;
    static String category = "sokika";
    static Article arto = new Article();
    static ImageView img;
    static EditText notes;
    static EditText website;
    static File finalFile;
    static String picName;
    static String myUri;
    static String myAddress;
    static Double longi;
    static Double lati;
    static String zipcode;
    static String realZipCode;
    static String myState;
    static String myCountry;
    static String mySecondLineAddr;
    static String myHome;
    static String myApartment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        mContext = getApplicationContext();
        activity = AddActivity.this;
        img = findViewById(R.id.imageV);
        notes = findViewById(R.id.editNotes);
        website = findViewById(R.id.editWebsite);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        String uploadSuccess = "";

        assert b != null;
        if (b != null) {
            uploadSuccess = b.getString("myUpload");
        }
        if(!(uploadSuccess == null || uploadSuccess.length() == 0)) {
            Toast.makeText(this, uploadSuccess, Toast.LENGTH_SHORT).show();
        }

        try {
            LocationDetector myloc = new LocationDetector(
                    AddActivity.this);
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
            try {
                addresses = gCoder.getFromLocation(lati, longi, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addresses != null && addresses.size() > 0) {
                myAddress = addresses.get(0).getAddressLine(0);
                String[] adds = myAddress.split(" ");
                myHome = adds[0];
                myApartment = addresses.get(0).getAddressLine(1);
                myState = adds[adds.length - 3];
                myCountry = adds[adds.length - 1];
                zipcode = adds[adds.length - 2].replace(',', ' ');
                realZipCode = adds[adds.length - 2].replace(',', ' ');
                Toast.makeText(mContext, myAddress, Toast.LENGTH_LONG).show();
            }
        }catch (Exception ex){
            ex.printStackTrace();
            Toast.makeText(this, "Geocoder error, please reload!", Toast.LENGTH_SHORT).show();
        }

        Intent intenta = getIntent();
        Bundle boo = intenta.getExtras();
        assert boo != null;
        if (boo != null) {
            if (boo.getString("myZipo") != null) {
                if (boo.getString("myZipo").length() != 0) {
                    zipcode = boo.getString("myZipo");
                    //Toast.makeText(mContext, "settings zip : " + zipcode, Toast.LENGTH_LONG).show();
                }
            }
        }

        findViewById(R.id.imageV).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, PICK_IMAGE);
            }
        });

        findViewById(R.id.cancelBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AddActivity.class);
                intent.putExtra("myZipo", zipcode);
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
                intent.putExtra("myZipo", zipcode);
                startActivity(intent);
            }
        });

        findViewById(R.id.saveBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
                Calendar calobj = Calendar.getInstance();
                String dt = df.format(calobj.getTime()).toString().replace('/', '-').replace(' ', '-').replace(':', '-');
                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                //call api 2
                if(finalFile != null) {
                    try {
                        new UploadImage(AddActivity.this).execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    arto.Id = "mekonecampus";
                    arto.Title = notes.getText().toString();
                    if (arto.Title == null || arto.Title.length() == 0) {
                        arto.Title = "WONDER, SEEK, DISCOVER";
                    }
                    arto.Body = myAddress;
                    arto.Custom3 = date;
                    arto.Category = category;
                    arto.LikesNumber = 0;
                    arto.ViewsNumber = 0;
                    arto.PicUrl = "https://mekonecampusapistorage.blob.core.windows.net/campusimages/" + picName;
                    arto.PartitionKey = "sokika";
                    arto.RowKey = dt;
                    arto.DateCreated = df.format(calobj.getTime());
                    arto.Status = "active";
                    arto.Custom5 = "mekonecampus";
                    arto.Custom2 = realZipCode.trim();
                    arto.Custom1 = myCountry;
                    arto.Custom5 = myState;
                    arto.Custom4 = website.getText().toString();
                    if (arto.Custom4 == null || arto.Custom4.length() == 0) {
                        arto.Custom4 = "ok";
                    }

                    //call api
                    try {
                        new CreateArticle(AddActivity.this).execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(v.getContext(), AddActivity.class);
                    intent.putExtra("myUpload", "Image upload successful!");
                    intent.putExtra("myZipo", zipcode);
                    //finalFile = null;
                    startActivity(intent);
                }else{
                    Toast.makeText(mContext, "Please pick an image to upload.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                finalFile = new File(getRealPathFromURI(imageUri));
                myUri = imageUri.toString();
                img.setImageBitmap(selectedImage);
                img.setVisibility(View.VISIBLE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    public static void Transformer(String fileName) throws FileNotFoundException {
        InputStream inputStream = new FileInputStream(fileName);//You can get an inputStream using any IO API
        byte[] bytes;
        byte[] buffer = new byte[8192];
        int bytesRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        bytes = output.toByteArray();
        //encodedString = Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public static Article APICall() throws IOException {
        try {
            arto.PicUrl = "https://mekonecampusapistorage.blob.core.windows.net/campusimages/" + picName;
            URL url = new URL("http://mekonecampusapi.azurewebsites.net/api/Articles");
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setRequestMethod("POST");

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

    public static Article APICall2(){
        try {
            boolean permissionGranted = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
            if(permissionGranted) {
                HttpClient client = HttpClientBuilder.create().build();
                HttpPost post = new HttpPost("http://mekonecampusapi.azurewebsites.net/api/pictures");
                File file = finalFile; //new File(imageFileName);
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                builder.addBinaryBody
                        ("upfile", file, ContentType.DEFAULT_BINARY, finalFile.getName());
                HttpEntity entity = builder.build();
                post.setEntity(entity);
                HttpResponse response = client.execute(post);
                picName = finalFile.getName();
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
                picName = finalFile.getName();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arto;
    }

    private static class CreateArticle extends AsyncTask<Void, Void, Article> {
        private WeakReference<Activity> weakActivity;
        public CreateArticle(Activity activity) {
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

    private static class UploadImage extends AsyncTask<Void, Void, Article> {
        private WeakReference<Activity> weakActivity;
        public UploadImage(Activity activity) {
            weakActivity = new WeakReference<>(activity);
        }
        @Override
        protected Article doInBackground(Void... voids) {
            Activity activity = weakActivity.get();
            if(activity == null) {
                return null;
            }
            try {
                APICall2();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //if(articles.size() == 0) {
            //  return null;
            //}
            return null;
        }
    }
}
