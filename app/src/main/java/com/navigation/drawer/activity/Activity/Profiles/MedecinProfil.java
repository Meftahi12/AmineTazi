package com.navigation.drawer.activity.Activity.Profiles;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.navigation.drawer.activity.Activity.Alls.AllCliniquesActivity;
import com.navigation.drawer.activity.Activity.Alls.AllMedecinsActivity;
import com.navigation.drawer.activity.Activity.BaseActivity;
import com.navigation.drawer.activity.Activity.FavorisActivity;
import com.navigation.drawer.activity.Activity.SearchActivity;
import com.navigation.drawer.activity.Activity.TrackGPS;
import com.navigation.drawer.activity.Activity.UploadData;
import com.navigation.drawer.activity.JSONParser;
import com.navigation.drawer.activity.Activity.Main2Activity;
import com.navigation.drawer.activity.Activity.MapActivity;
import com.navigation.drawer.activity.Classes.Medecin;
import com.navigation.drawer.activity.Classes.MyGPS;
import com.navigation.drawer.activity.R;

public class MedecinProfil extends BaseActivity implements View.OnClickListener {

    public boolean loginVerifed = false;
    public String currentTel = null;
    public static MyGPS result = null;
    public static boolean isFavoris = false;
    public static Medecin currentMedecin = null;
    ImageView fav = null;
    ProgressDialog pDialog;

    private TrackGPS gps;

    double userlocatLa = -1;
    double userlocatLn = -1;

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
        getLayoutInflater().inflate(R.layout.activity_medecin_profil, frameLayout);

        mDrawerList.setItemChecked(position, true);
        setTitle(listArray[position]);

        Main2Activity.historique.add("profMedecin");

        TextView speciality = (TextView) findViewById(R.id.SpecialityM);
        ImageView tel = (ImageView) findViewById(R.id.telM);
        TextView name = (TextView) findViewById(R.id.MedecinName);
        TextView Adresse = (TextView) findViewById(R.id.AdresseM);

        ImageView back = (ImageView) findViewById(R.id.retourmp);
        back.setOnClickListener(this);

        name.setText("Dr. " + currentMedecin.getName());
        speciality.setText(currentMedecin.getSpeciality());
        TextView teleph = (TextView) findViewById(R.id.telMM);
        currentTel = currentMedecin.getTel();
        teleph.setHint(currentTel);
        tel.setOnClickListener(this);


        String mainString = currentMedecin.getAdresse();
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

        fav = (ImageView) findViewById(R.id.fav_med);
        if (Main2Activity.isFavoris(currentMedecin)) {
            isFavoris = true;
            fav.setImageDrawable(getResources().getDrawable(R.drawable.fav));
        } else {
            isFavoris = false;
            fav.setImageDrawable(getResources().getDrawable(R.drawable.notfav));
        }
        fav.setOnClickListener(this);


        ImageView localisation = (ImageView) findViewById(R.id.localMedecin);
        localisation.setOnClickListener(this);


        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {

                userlocatLn=location.getLongitude();
                userlocatLa=location.getLatitude();


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

            if (curr.getId() == R.id.telM || curr.getId() == R.id.retourmp || curr.getId() == R.id.localMedecin) {
                if(curr.getId() == R.id.telM) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL); //use ACTION_CALL class
                    callIntent.setData(Uri.parse("tel:"+currentTel));    //this is the phone number calling
                    //check permission
                    //If the device is running Android 6.0 (API level 23) and the app's targetSdkVersion is 23 or higher,
                    //the system asks the user to grant approval.
                    if (ActivityCompat.checkSelfPermission(MedecinProfil.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        //request permission from user if the app hasn't got the required permission
                        ActivityCompat.requestPermissions(MedecinProfil.this,
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
                if (curr.getId() == R.id.retourmp) {
                    int search = Main2Activity.historique.lastIndexOf("search");
                    int favoris = Main2Activity.historique.lastIndexOf("favoris");
                    int medecins = Main2Activity.historique.lastIndexOf("medecin");

                    int m = Math.max(search,Math.max(favoris,medecins));

                    if(m==search)
                        openActivity(0);
                    if(m==medecins) {
                        AllMedecinsActivity.currentSpeciality = currentMedecin.getSpeciality().toUpperCase();
                        openActivityFrom(3);
                    }
                    if(m==favoris)
                        openActivity(5);
                }
                if (curr.getId() == R.id.localMedecin) {
                    if(isNetworkAvailable()) {
                        if(SearchActivity.show)
                            showSettingsAlert();
                        else
                            new MyTask().execute();
                    }
                    else
                        Toast.makeText(getApplicationContext(),"connectez vous a internet et activez gps et reessayer",Toast.LENGTH_LONG).show();

                }
            }
            else{
                if(isFavoris){
                    isFavoris = false ;
                    fav.setImageDrawable(getResources().getDrawable(R.drawable.notfav));
                    Main2Activity.removeFav("Medecin",currentMedecin.getName());
                }
                else{
                    isFavoris = true ;
                    fav.setImageDrawable(getResources().getDrawable(R.drawable.fav));
                    Main2Activity.insertFav("Medecin",currentMedecin.getName());
                }
            }
         }
    }
    public class MyTask extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MedecinProfil.this);
            pDialog.setMessage("Please Wait");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            if(currentMedecin.getLocalisation().getLongitude()!=0){

                Location loc = new Location("");
                loc.setLatitude(currentMedecin.getLocalisation().getLaltitude());
                loc.setLongitude(currentMedecin.getLocalisation().getLongitude());

                MapActivity.searchedLocation = loc;
                MapActivity.currentObject = currentMedecin;
                publishProgress();
                gps = new TrackGPS(MedecinProfil.this);

                int nbOfTry = 0 ;
                Log.d("ha",gps.canGetLocation()+","+userlocatLa);
                while(gps.canGetLocation() && userlocatLa == -1 && nbOfTry <20) {
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

            MyGPS t = JSONParser.getGPS(currentMedecin.getName());
            if(t != null){
                currentMedecin.setLocalisation(t);

                Location loc = new Location("");
                loc.setLatitude(currentMedecin.getLocalisation().getLaltitude());
                loc.setLongitude(currentMedecin.getLocalisation().getLongitude());

                MapActivity.searchedLocation = loc;
                MapActivity.currentObject = currentMedecin;
                publishProgress();
                gps = new TrackGPS(MedecinProfil.this);

                int nbOfTry = 0 ;
                Log.d("ha",gps.canGetLocation()+","+userlocatLa);
                while(gps.canGetLocation() && userlocatLa == -1 && nbOfTry <20) {
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
            t = JSONParser.getGPS(currentMedecin.getName()+" "+currentMedecin.getAdresse());
            if(t != null){
                currentMedecin.setLocalisation(t);

                Location loc = new Location("");
                loc.setLatitude(currentMedecin.getLocalisation().getLaltitude());
                loc.setLongitude(currentMedecin.getLocalisation().getLongitude());

                MapActivity.searchedLocation = loc;
                MapActivity.currentObject = currentMedecin;
                publishProgress();
                gps = new TrackGPS(MedecinProfil.this);

                int nbOfTry = 0 ;
                Log.d("ha",gps.canGetLocation()+","+userlocatLa);
                while(gps.canGetLocation() && userlocatLa == -1 && nbOfTry <20) {
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
            t = JSONParser.getGPS(currentMedecin.getAdresse() );
            if(t != null){
                currentMedecin.setLocalisation(t);
                Location loc = new Location("");
                loc.setLatitude(currentMedecin.getLocalisation().getLaltitude());
                loc.setLongitude(currentMedecin.getLocalisation().getLongitude());

                MapActivity.searchedLocation = loc;
                MapActivity.currentObject = currentMedecin;
                publishProgress();
                gps = new TrackGPS(MedecinProfil.this);

                int nbOfTry = 0 ;
                Log.d("ha",gps.canGetLocation()+","+userlocatLa);
                while(gps.canGetLocation() && userlocatLa == -1 && nbOfTry <20) {
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
                gps = new TrackGPS(MedecinProfil.this);
                if(gps.canGetLocation()) {
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
                        startActivity(new Intent(MedecinProfil.this, MapActivity.class));
                        finish();
                    }

                }
                else
                {
                    gps.showSettingsAlert();
                }
            }
            else {
                pDialog.dismiss();
                Toast.makeText(MedecinProfil.this, "not available", Toast.LENGTH_SHORT).show();
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
    public void showSettingsAlert() {
        final CheckBox dontShowAgain;
        LayoutInflater adbInflater = LayoutInflater.from(this);
        View eulaLayout = adbInflater.inflate(R.layout.checkbox, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        dontShowAgain = (CheckBox) eulaLayout.findViewById(R.id.skip);
        alertDialog.setView(eulaLayout);
        alertDialog.setTitle("Localiser un service medical");

        alertDialog.setMessage("Les localisations sont faites a l'aide du service google maps qui ne peux pas bien localiser parfois l'adresse demandée ... ce qui fait que les localisations ne sont pas bien précises");

        alertDialog.setPositiveButton("Continuez quand même", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                if (dontShowAgain.isChecked()) {
                    SearchActivity.show = false ;
                }
                new MyTask().execute();
            }

        });

        alertDialog.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (dontShowAgain.isChecked()) {
                    SearchActivity.show = false ;
                }
                dialog.cancel();
            }
        });

        alertDialog.show();
    }
}
