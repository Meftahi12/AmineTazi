package com.navigation.drawer.activity.Activity.Profiles;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.navigation.drawer.activity.Activity.Alls.AllCliniquesActivity;
import com.navigation.drawer.activity.Activity.BaseActivity;
import com.navigation.drawer.activity.Activity.FavorisActivity;
import com.navigation.drawer.activity.Activity.SearchActivity;
import com.navigation.drawer.activity.Activity.TrackGPS;
import com.navigation.drawer.activity.JSONParser;
import com.navigation.drawer.activity.Activity.Main2Activity;
import com.navigation.drawer.activity.Activity.MapActivity;
import com.navigation.drawer.activity.Blocs.InfoBloc;
import com.navigation.drawer.activity.Classes.Clinique;
import com.navigation.drawer.activity.Classes.MyGPS;
import com.navigation.drawer.activity.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class CliniqueProfil extends BaseActivity implements View.OnClickListener {

    public static Clinique currentClinique = null;
    public String currentTel = null;
    ProgressDialog pDialog;
    public boolean loginVerifed = false;
    public static boolean isFavoris = false;
    LinearLayout ll =  null ;
    private TrackGPS gps;


    ImageView fav = null;

    double userlocatLa = -1 ;
    double userlocatLn = -1 ;

    LocationManager locationManager;
    LocationListener locationListener;





    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_clinique_profil, frameLayout);

        mDrawerList.setItemChecked(position, true);
        setTitle(listArray[position]);

        Main2Activity.historique.add("profClinique");

        TextView telC = (TextView) findViewById(R.id.telCM);
        TextView categorie = (TextView) findViewById(R.id.CategorieC);
        ImageView tel = (ImageView) findViewById(R.id.telC);
        TextView name = (TextView) findViewById(R.id.CliniqueName);
        TextView Adresse = (TextView) findViewById(R.id.AdresseC);

        ImageView back = (ImageView) findViewById(R.id.retourcp);
        back.setOnClickListener(this);

        name.setText(currentClinique.getName());
        categorie.setText(currentClinique.getCategorie());

        currentTel = currentClinique.getTel();

        tel.setOnClickListener(this);

        String mainString = currentClinique.getAdresse();
        String[] stringArray = mainString.split("\\s+");
        String tmpString = "";
        StringBuffer  finalString = new StringBuffer();
        for (String singleWord : stringArray) {
            if ((tmpString + singleWord + " ").length() > 28) {
                finalString.append(tmpString + "\n");

                tmpString = singleWord + " ";
            } else {
                tmpString = tmpString + singleWord + " ";
            }
        }

        if (tmpString.length() > 0) {
            finalString.append(tmpString);
        }
        Adresse.setText(finalString);


        telC.setText(currentTel);

        fav = (ImageView) findViewById(R.id.fav_cli);
        if (Main2Activity.isFavoris(currentClinique)) {
            isFavoris = true;
            fav.setImageDrawable(getResources().getDrawable(R.drawable.fav));
        } else {
            isFavoris = false;
            fav.setImageDrawable(getResources().getDrawable(R.drawable.notfav));
        }

        fav.setOnClickListener(this);

        ll = (LinearLayout) findViewById(R.id.cliniqueInformations);

        for(Map.Entry<String, Vector<String>> entry : currentClinique.getInformations().entrySet()) {
            String key = entry.getKey();
            Vector<String> value = entry.getValue();
            InfoBloc il = new InfoBloc(getApplicationContext(),key,value);

            ll.addView(il);

        }
        ImageView localisation = (ImageView) findViewById(R.id.localClinique);
        localisation.setOnClickListener(this);

   //     new GetGPS(CliniqueProfil.this);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {

                userlocatLn=location.getLongitude();
                userlocatLa=location.getLatitude();

                //   Log.i("wiliba",location.getLongitude() + " " + location.getLatitude());

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }

        };

        // If device is running SDK < 23

        if (Build.VERSION.SDK_INT < 23) {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        } else {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                // ask for permission

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);


            } else {

                // we have permission!

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            }

        }
    }


    @Override
    public void onClick(View v) {
        if (v.getClass().getSimpleName().equals("ImageView")) {
            ImageView curr = (ImageView) v;

            if (curr.getId() == R.id.telC || curr.getId() == R.id.retourcp || curr.getId() == R.id.localClinique) {
                if(curr.getId() == R.id.telC) {

                    Intent callIntent = new Intent(Intent.ACTION_CALL); //use ACTION_CALL class
                    callIntent.setData(Uri.parse("tel:"+currentTel));    //this is the phone number calling
                    //check permission
                    //If the device is running Android 6.0 (API level 23) and the app's targetSdkVersion is 23 or higher,
                    //the system asks the user to grant approval.
                    if (ActivityCompat.checkSelfPermission(CliniqueProfil.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        //request permission from user if the app hasn't got the required permission
                        ActivityCompat.requestPermissions(CliniqueProfil.this,
                                new String[]{Manifest.permission.CALL_PHONE},   //request specific permission from user
                                10);
                        return;
                    }else {     //have got permission
                        try{
                            startActivity(callIntent);  //call activity and make phone call
                        }
                        catch (android.content.ActivityNotFoundException ex){
                            Toast.makeText(getApplicationContext(),"yourActivity is not founded",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                if (curr.getId() == R.id.retourcp) {

                    int cliniques = Main2Activity.historique.lastIndexOf("clinique");
                    int search = Main2Activity.historique.lastIndexOf("search");
                    int favoris = Main2Activity.historique.lastIndexOf("favoris");


                    int m = Math.max(search,Math.max(favoris,cliniques));

                    if(m==search)
                        openActivity(0);
                    if(m==cliniques)
                        AllCliniquesActivity.currentCateg = currentClinique.getCategorie() ;
                        openActivityFrom(4);
                    if(m==favoris)
                        openActivity(5);

                }
                if (curr.getId() == R.id.localClinique) {
                    if(isNetworkAvailable())
                        new MyTask().execute();
                    else
                        Toast.makeText(getApplicationContext(),"connectez vous a internet et activez gps et reessayer",Toast.LENGTH_LONG).show();
                }
            }
            else{
                if(isFavoris){
                    isFavoris = false ;
                    fav.setImageDrawable(getResources().getDrawable(R.drawable.notfav));
                    Main2Activity.removeFav("Clinique",currentClinique.getName());
                }
                else{
                    isFavoris = true ;
                    fav.setImageDrawable(getResources().getDrawable(R.drawable.fav));
                    Main2Activity.insertFav("Clinique",currentClinique.getName());
                }
            }
        }
    }
    public class MyTask extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CliniqueProfil.this);
            pDialog.setMessage("Please Wait");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {


            MyGPS t = JSONParser.getGPS(currentClinique.getName());
            if(t != null){

                Location loc = new Location("");
                loc.setLatitude(t.getLaltitude());
                loc.setLongitude(t.getLongitude());

                MapActivity.searchedLocation = loc;
                MapActivity.currentObject = currentClinique;
                publishProgress();
                gps = new TrackGPS(CliniqueProfil.this);
                int nbOfTry = 0 ;
                Log.d("ha",gps.canGetLocation()+","+userlocatLa);
                while(gps.canGetLocation() && userlocatLa == -1 && nbOfTry <5) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    nbOfTry++;
                    Log.d("ha",gps.canGetLocation()+","+userlocatLa+","+nbOfTry);
                }
                loginVerifed = true;

                return null ;
            }
            t = JSONParser.getGPS(currentClinique.getName()+" "+currentClinique.getAdresse());
            if(t != null){

                Location loc = new Location("");
                loc.setLatitude(t.getLaltitude());
                loc.setLongitude(t.getLongitude());

                MapActivity.searchedLocation = loc;
                MapActivity.currentObject = currentClinique;
                publishProgress();
                gps = new TrackGPS(CliniqueProfil.this);

                int nbOfTry = 0 ;
                Log.d("ha",gps.canGetLocation()+","+userlocatLa);
                while(gps.canGetLocation() && userlocatLa == -1 && nbOfTry <5) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    nbOfTry++;
                    Log.d("ha",gps.canGetLocation()+","+userlocatLa+","+nbOfTry);
                }
                loginVerifed = true;
                return null ;
            }
            t = JSONParser.getGPS(currentClinique.getAdresse() );
            if(t != null){
                Location loc = new Location("");
                loc.setLatitude(t.getLaltitude());
                loc.setLongitude(t.getLongitude());

                MapActivity.searchedLocation = loc;
                MapActivity.currentObject = currentClinique;
                gps = new TrackGPS(CliniqueProfil.this);
                publishProgress();
                int nbOfTry = 0 ;
                Log.d("ha",gps.canGetLocation()+","+userlocatLa);
                while(gps.canGetLocation() && userlocatLa == -1 && nbOfTry <5) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    nbOfTry++;
                    Log.d("ha",gps.canGetLocation()+","+userlocatLa+","+nbOfTry);
                }
                loginVerifed = true;
                return null ;
            }
            loginVerifed = false;
            return  null ;
        }

        @Override
        protected void onPostExecute(String s) {
            if(loginVerifed == true)
            {
                pDialog.dismiss();
                gps = new TrackGPS(CliniqueProfil.this);


                if(gps.canGetLocation()){

                    if (userlocatLa == -1) {
                        Toast toast = Toast.makeText(getApplicationContext(), "nous avons rencontré une erreur en détectant ta position courante ... reéssayer plus tard", Toast.LENGTH_SHORT);
                        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                        if( v != null) v.setGravity(Gravity.CENTER);
                        toast.show();
                    }
                    else {
                        MapActivity.currentLocation = new Location("");
                        MapActivity.currentLocation.setLatitude(userlocatLa);
                        MapActivity.currentLocation.setLongitude(userlocatLn);
                        startActivity(new Intent(CliniqueProfil.this, MapActivity.class));
                        finish();
                    }
                }
                else
                {
                    gps.showSettingsAlert();
                }

            }
            else{
                pDialog.dismiss();
                Toast.makeText(CliniqueProfil.this, "not available", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            pDialog.setMessage("Détection de ta position courante");
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}