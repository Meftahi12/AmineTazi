package com.navigation.drawer.activity.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.navigation.drawer.activity.Activity.Alls.AllCliniquesActivity;
import com.navigation.drawer.activity.Activity.Alls.AllMedecinsActivity;
import com.navigation.drawer.activity.Activity.Alls.AllPharmaciesActivity;
import com.navigation.drawer.activity.Activity.Profiles.CliniqueProfil;
import com.navigation.drawer.activity.Activity.Profiles.MedecinProfil;
import com.navigation.drawer.activity.Activity.Profiles.PharmacieProfil;
import com.navigation.drawer.activity.Classes.Clinique;
import com.navigation.drawer.activity.Classes.ListGarde;
import com.navigation.drawer.activity.Classes.Medecin;
import com.navigation.drawer.activity.Classes.MyGPS;
import com.navigation.drawer.activity.Classes.Pharmacie;
import com.navigation.drawer.activity.Classes.Speciality;
import com.navigation.drawer.activity.HttpManager;
import com.navigation.drawer.activity.JSONParser;
import com.navigation.drawer.activity.R;

import java.util.List;


public class SearchActivity extends BaseActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    CheckBox cPha = null, cCli = null, cMed = null;
    EditText searchField = null;
    Button search = null;
    ImageView addMedecin = null, addClinique = null, addPharmacie = null;
    public SQLiteDatabase myDB = null;

    public boolean loginVerifed ;
    ProgressDialog pDialog;
    private TrackGPS gps;
    public boolean firstTime = true ;
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

        Main2Activity.historique.add("search");

        super.onCreate(savedInstanceState);

        myDB = this.openOrCreateDatabase("DatabaseName", MODE_PRIVATE, null);


        getLayoutInflater().inflate(R.layout.activity_search, frameLayout);

        mDrawerList.setItemChecked(position, true);
        setTitle(listArray[position]);

        searchField = (EditText) findViewById(R.id.editText3);

        cPha = (CheckBox) findViewById(R.id.CheckPharmacies);
        cMed = (CheckBox) findViewById(R.id.CheckMedecins);
        cCli = (CheckBox) findViewById(R.id.CheckCliniques);

        search = (Button) findViewById(R.id.Search);
        search.setOnClickListener(this);

        ImageView Closer = (ImageView) findViewById(R.id.proche);
        Closer.setOnClickListener(this);

        addMedecin = (ImageView) findViewById(R.id.getMedecins);
        addMedecin.setOnClickListener(this);
        addClinique = (ImageView) findViewById(R.id.getCliniques);
        addClinique.setOnClickListener(this);
        addPharmacie = (ImageView) findViewById(R.id.getPharmacies);
        addPharmacie.setOnClickListener(this);

        //new GetGPS(SearchActivity.this);
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position - 1) {
            case 0:
                openActivity(2);
                break;
            case 1:
                openActivity(1);
                break;
            case 2:
                openActivity(3);
                break;
            case 3:
                openActivity(4);
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        if (v.getClass().getSimpleName().equals("Button")) {
            Button b = (Button) v;
            if (b.getText().equals("Search")) {
                if (searchField.getText().toString().isEmpty())
                    Toast.makeText(getApplicationContext(), "Entrer quelque chose svp", Toast.LENGTH_SHORT).show();
                else {
                    ResultActivity.setVar(searchField.getText().toString(), cPha.isChecked(), cMed.isChecked(), cCli.isChecked(),true);
                    Intent intent = new Intent(this, ResultActivity.class);
                    startActivity(intent);
                }
            }
        }
        if (v.getClass().getSimpleName().equals("ImageView")) {
            ImageView b = (ImageView) v;
            if (b.getId() == R.id.proche) {
                showSettingsAlert();
            }
            if (b.getId() == R.id.getCliniques) {
                AllCliniquesActivity.currentCateg = null ;
                openActivity(4);
            }
            if(b.getId() == R.id.getMedecins){
                AllMedecinsActivity.currentSpeciality = null ;
                openActivity(3);
            }
            if(b.getId() == R.id.getPharmacies){
                AllPharmaciesActivity.currentSecteur = null ;
                openActivityFrom(2);
            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public class MyTask extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SearchActivity.this);
            pDialog.setMessage("Please Wait");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {


            if (firstTime && isNetworkAvailable()) {
                firstTime = false;
                try {

                    int numberOfTry = 0 ;
                    while(ListGarde.pharmacieList == null && numberOfTry <5) {
                        try {
                            numberOfTry++ ;
                            ListGarde.pharmacieList = JSONParser.JSONPharmacieParser(HttpManager.getData("http://www.pfesmi.tk/getGardes.php"));
                        } catch (Exception e) {
                        }
                    }

                    if (ListGarde.pharmacieList != null) {
                        for (int i = 0; i < ListGarde.pharmacieList.size(); i++) {
                            Pharmacie currentPharmacie = ListGarde.pharmacieList.get(i);
                            boolean drap = false;

                            MyGPS t = JSONParser.getGPS(currentPharmacie.getPharmacie() + " PHARMACIE ");
                            if (t != null) {
                                currentPharmacie.setLocalisation(t);
                                drap = true;
                            }
                            if (!drap) {
                                t = JSONParser.getGPS(currentPharmacie.getAdresse() + " PHARMACIE");
                                if (t != null) {
                                    currentPharmacie.setLocalisation(t);
                                    drap = true;
                                }
                            }
                            if (!drap) {
                                t = JSONParser.getGPS(currentPharmacie.getSecteur() + " PHARMACIE");
                                if (t != null) {
                                    currentPharmacie.setLocalisation(t);
                                }
                            }
                        }
                    }
                }
                catch (Exception e) {

                }
            }
            publishProgress();
            gps = new TrackGPS(SearchActivity.this);
            int nbOfTry = 0;
            Log.d("ha", gps.canGetLocation() + "," + userlocatLa);
            while (gps.canGetLocation() && userlocatLa == -1 && nbOfTry < 20) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                nbOfTry++;
                Log.d("ha", gps.canGetLocation() + "," + userlocatLa + "," + nbOfTry);
            }
            loginVerifed = true;
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (loginVerifed) {
                if(isNetworkAvailable()) {
                    try {
                        if (ListGarde.pharmacieList != null) {
                            if (gps.canGetLocation()) {
                                if (userlocatLa == -1) {
                                    pDialog.dismiss();
                                    Toast toast = Toast.makeText(getApplicationContext(), "nous avons rencontré une erreur en détectant ta position courante ... reéssayer plus tard", Toast.LENGTH_SHORT);
                                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                                    if (v != null) v.setGravity(Gravity.CENTER);
                                    toast.show();
                                } else {
                                    MapActivity.currentLocation = new Location("");
                                    MapActivity.currentLocation.setLatitude(userlocatLa);
                                    MapActivity.currentLocation.setLongitude(userlocatLn);
                                        double dis = 1000000000;
                                        Pharmacie closer = null;
                                        Location currentLocation = MapActivity.currentLocation;

                                        Location closerLoc = null;
                                        for (int i = 0; i < ListGarde.pharmacieList.size(); i++) {

                                            Pharmacie currentPharmacie = ListGarde.pharmacieList.get(i);
                                            Location crLoc = new Location("");

                                            crLoc.setLongitude(currentPharmacie.getLocalisation().getLongitude());
                                            crLoc.setLatitude(currentPharmacie.getLocalisation().getLaltitude());

                                            double currDistance = currentLocation.distanceTo(crLoc);


                                            if (dis > currDistance) {
                                                dis = currDistance;
                                                closerLoc = crLoc;
                                                closer = currentPharmacie;
                                            }
                                        }

                                        MapActivity.searchedLocation = closerLoc;
                                        MapActivity.currentObject = closer;
                                        pDialog.dismiss();
                                        startActivity(new Intent(SearchActivity.this, MapActivity.class));
                                        finish();
                                    }
                                } else {
                                    pDialog.dismiss();
                                    gps.showSettingsAlert();
                                }
                            } else {
                                pDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Votre Connexion est faible .. réessayer ulterieurement", Toast.LENGTH_SHORT).show();
                            }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else {
                    pDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Connectez vous a l'internet et reéssayer", Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            pDialog.setMessage("Détection de ta position courante");
        }
    }
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Localiser un service medical");

        alertDialog.setMessage("Les localisations sont faites a l'aide du service google maps qui ne peux pas bien localiser parfois l'adresse demandée ... ce qui fait que les localisations ne sont pas bien précises");

        alertDialog.setPositiveButton("Continuez quand même", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                new MyTask().execute();
            }
        });

        alertDialog.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }
}

