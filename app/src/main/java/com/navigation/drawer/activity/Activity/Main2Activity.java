package com.navigation.drawer.activity.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.navigation.drawer.activity.Classes.Speciality;
import com.navigation.drawer.activity.HttpManager;
import com.navigation.drawer.activity.JSONParser;
import com.navigation.drawer.activity.Classes.Clinique;
import com.navigation.drawer.activity.Classes.Medecin;
import com.navigation.drawer.activity.Classes.MyGPS;
import com.navigation.drawer.activity.Classes.Pharmacie;


import com.navigation.drawer.activity.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Main2Activity extends BaseActivity {
    boolean firstTime = false ;
    boolean isError = false ;
    public static SQLiteDatabase myDB = null;
    public boolean loginVerifed = false;
    public static Vector<String> historique = new Vector<String>();
    ProgressBar mProgress  =  null ;
    public static ArrayList<Object> lf = null ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgress = (ProgressBar) findViewById(R.id.pbHeaderProgress);
        myDB = this.openOrCreateDatabase("DatabaseName", MODE_PRIVATE, null);
        createTables();
        new MyTask().execute();
    }

    public void createTable(String TableName, String... Column) {
        String col = "";
        for (int i = 0; i < Column.length; i++) {
            col += ", " + Column[i] + " TEXT";
        }
        myDB.execSQL("CREATE TABLE IF NOT EXISTS " +
                TableName + " (ID INTEGER PRIMARY KEY AUTOINCREMENT " + col + ")"
        );
    }

    public void createTables() {
        myDB.execSQL("CREATE TABLE IF NOT EXISTS LOGIN (firstTime text)");
        Cursor c = myDB.rawQuery("select * from LOGIN",null);
        if(c.getCount()==0){
            myDB.execSQL("insert into LOGIN values(\'true\')");
        }
        createTable("Pharmacie", "pharmacien", "pharmacie", "adresse", "secteur", "tel", "longitude", "laltitude");
        createTable("Medecin", "name", "Adresse", "tel", "longitude", "laltitude", "speciality");
        createTable("Clinique", "name", "categorie", "adresse", "tel", "info");
        myDB.execSQL("CREATE TABLE IF NOT EXISTS FAVORIS (ID INTEGER PRIMARY KEY AUTOINCREMENT  ,TYPE TEXT , KEY TEXT)");
    }

    public void insertDataClinique(String... info) {

        String values = "";
        for (int i = 0; i < info.length; i++) {
            info[i] = info[i].replaceAll("'","&");
            if (i == 0)
                values += "'" + info[i] + "'";
            else
                values += ",'" + info[i] + "'";
        }

        myDB.execSQL("INSERT INTO Clinique " + "(name,categorie,adresse,tel,info)"
                    + "VALUES (" + values + ");");
    }

    public void insertDataPharmacie(String... val) {

        String values = "";
        for (int i = 0; i < val.length; i++) {
            val[i] = val[i].replaceAll("'","&");
            if (i == 0)
                values += "'" + val[i] + "'";
            else
                values += ",'" + val[i] + "'";
        }

        myDB.execSQL("INSERT INTO Pharmacie " + "(pharmacien,pharmacie,adresse,secteur,tel,longitude,laltitude)"
                    + "VALUES (" + values + ");");
    }

    public void insertDataMedecin(String... val) {

        String values = "";
        for (int i = 0; i < val.length; i++) {
            val[i] = val[i].replaceAll("'","&");
            if (i == 0)
                values += "'" + val[i] + "'";
            else
                values += ",'" + val[i] + "'";
        }
        myDB.execSQL("INSERT INTO Medecin " + "(name,Adresse,tel,longitude,laltitude,speciality)"
                    + "VALUES (" + values + ");");
    }

    public static void insertFav(String... val) {
        String values = "";
        for (int i = 0; i < val.length; i++) {
            val[i] = val[i].replaceAll("'","&");
            if (i == 0)
                values += "'" + val[i] + "'";
            else
                values += ",'" + val[i] + "'";
        }

        Cursor c = myDB.rawQuery("SELECT * FROM FAVORIS WHERE TYPE = \'" + val[0] + "\' AND KEY  = \'" + val[1] + "\'", null);
        if (c.getCount() == 0)
            myDB.execSQL("INSERT INTO FAVORIS " + "(TYPE,KEY) "
                    + "VALUES (" + values + ");");
        lf = getFavoris();
    }

    public static void removeFav(String... val) {
        String values = "";
        for (int i = 0; i < val.length; i++) {
            val[i] = val[i].replaceAll("'","&");
            if (i == 0)
                values += "'" + val[i] + "'";
            else
                values += ",'" + val[i] + "'";
        }

        myDB.execSQL("DELETE FROM FAVORIS WHERE TYPE = \'" + val[0] + "\' AND KEY  = \'" + val[1] + "\'");
        lf = getFavoris();
    }

    public static ArrayList<Object> getFavoris() {

        ArrayList<Object> myList = new ArrayList<Object>();

        Cursor c = myDB.rawQuery("SELECT * FROM FAVORIS", null);

        int typeInd = c.getColumnIndex("TYPE");

        int keyInd = c.getColumnIndex("KEY");

        c.moveToFirst();

        if (c != null && c.getCount() != 0) {
            do {
                String type = c.getString(typeInd);
                String key = c.getString(keyInd);
                if (type.equals("Pharmacie")) {
                    myList.add(getPharmacie(key));
                }
                if (type.equals("Medecin")) {
                    myList.add(getMedecin(key));
                }
                if (type.equals("Clinique")) {
                    myList.add(getClinique(key));
                }
            } while (c.moveToNext());
        }

        return myList;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static Pharmacie getPharmacie(String key) {
            Pharmacie currentPharmacie = new Pharmacie();
            Cursor c = myDB.rawQuery("SELECT * FROM Pharmacie", null);
            c.moveToFirst();


            int indName = c.getColumnIndex("pharmacie");
            if (c != null && c.getCount() != 0) {
                do {
                    String secteur = c.getString(indName);
                    if (secteur.equals(key)) {
                        currentPharmacie.setPharmacie(c.getString(c.getColumnIndex("pharmacie")));
                        currentPharmacie.setPharmacien(c.getString(c.getColumnIndex("pharmacien")));
                        currentPharmacie.setAdresse(c.getString(c.getColumnIndex("adresse")));
                        currentPharmacie.setSecteur(c.getString(c.getColumnIndex("secteur")));
                        currentPharmacie.setTel(c.getString(c.getColumnIndex("tel")));
                        return currentPharmacie;
                    }
                } while (c.moveToNext());
            }
            return currentPharmacie;
        }

    public static Medecin getMedecin(String Key) {
        Medecin currentMedecin = new Medecin();
        Cursor c = myDB.rawQuery("SELECT * FROM Medecin", null);
        c.moveToFirst();


        int indName = c.getColumnIndex("name");
        if (c != null && c.getCount() != 0) {
            do {
                String name = c.getString(indName);
                if (name.equals(Key)) {
                    int nameInd = c.getColumnIndex("name");
                    int AdresseInd = c.getColumnIndex("Adresse");
                    int telInd = c.getColumnIndex("tel");
                    currentMedecin = new Medecin(c.getString(nameInd), c.getString(AdresseInd), c.getString(telInd), c.getString(c.getColumnIndex("speciality")), new MyGPS(0,0));
                    return currentMedecin;
                }
            } while (c.moveToNext());
        }
        return currentMedecin;
    }

    public static Clinique getClinique(String Key) {
        Clinique currentClinique = new Clinique();
        Cursor c = myDB.rawQuery("SELECT * FROM Clinique", null);
        c.moveToFirst();

        int indName = c.getColumnIndex("name");
        if (c != null && c.getCount() != 0) {
            do {
                String name = c.getString(indName);
                if (name.equals(Key)) {
                    int indCateg = c.getColumnIndex("categorie");
                    int nameInd = c.getColumnIndex("name");
                    int adresseInd = c.getColumnIndex("adresse");
                    int telInd = c.getColumnIndex("tel");
                    int infoInd = c.getColumnIndex("info");
                    currentClinique = new Clinique(c.getString(nameInd),c.getString(indCateg),c.getString(adresseInd),c.getString(telInd), null);
                    currentClinique.setInfo(c.getString(infoInd));
                    return currentClinique;
                }
            } while (c.moveToNext());
        }
        return currentClinique;
    }

    public class MyTask extends AsyncTask<Void, Void, Void> {

        public int value = 0 ;

        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            mProgress.setProgress(value);
        }

        @Override
        protected Void doInBackground(Void... params) {

            Cursor c = myDB.rawQuery("SELECT * FROM LOGIN", null);
            c.moveToFirst();


            int indName = c.getColumnIndex("firstTime");
            if (c != null && c.getCount() != 0) {
                String result = c.getString(indName);
                if (result.equals("true")) {
                    myDB.execSQL("update LOGIN set firstTime = \'false\' where firstTime  = \'true\'");
                    firstTime = true ;
                    isError = true ;
                }
            }

            if (isNetworkAvailable()) {

                try {
                    isError = false ;
                    publishProgress();
                    List<Pharmacie> listPharmacie = JSONParser.JSONPharmacieParser(HttpManager.getDatas("http://www.pfesmi.tk/getPharmacies.php"));
                    value = 20;
                    publishProgress();

                    if (listPharmacie != null) {
                        if (! firstTime)
                            myDB.execSQL("DELETE FROM  Pharmacie");
                        for (int i = 0; i < listPharmacie.size(); i++) {
                            Pharmacie curr = listPharmacie.get(i);
                            insertDataPharmacie
                                    (curr.getPharmacien(),
                                            curr.getPharmacie(),
                                            curr.getAdresse(),
                                            curr.getSecteur(),
                                            curr.getTel(),
                                            " " + curr.getLocalisation().getLaltitude(),
                                            " " + curr.getLocalisation().getLongitude());
                        }
                        value = 30;
                        publishProgress();
                    }
                    List<Speciality> listMedecin = JSONParser.JSONSpecialitiesPARSER(HttpManager.getDatas("http://www.pfesmi.tk/getMedecins.php"));
                    value = 50;
                    publishProgress();
                    if (listMedecin != null) {
                        if (! firstTime)
                            myDB.execSQL("DELETE FROM  Medecin");
                        for (int i = 0; i < listMedecin.size(); i++) {
                            for (int j = 0; j < listMedecin.get(i).getMyList().size(); j++) {
                                Medecin medecin = listMedecin.get(i).get(j);
                                try {
                                    insertDataMedecin(medecin.getName()
                                            , medecin.getAdresse()
                                            , medecin.getTel()
                                            , "" + medecin.getLocalisation().getLaltitude()
                                            , "" + medecin.getLocalisation().getLongitude()
                                            , medecin.getSpeciality());
                                } catch (Exception e) {
                                }
                            }
                        }
                        value = 60;
                        publishProgress();
                    }
                    List<Clinique> listClinique = JSONParser.JSONCliniqueParser(HttpManager.getDatas("http://www.pfesmi.tk/getCliniques.php"));
                    value = 80;
                    publishProgress();
                    if (listClinique != null) {
                        if (! firstTime)
                            myDB.execSQL("DELETE FROM Clinique");
                        for (int i = 0; i < listClinique.size(); i++) {
                            Clinique clinique = listClinique.get(i);
                            try {
                                insertDataClinique(clinique.getName()
                                        , clinique.getCategorie()
                                        , clinique.getAdresse()
                                        , clinique.getTel()
                                        , clinique.getInfo());
                            } catch (Exception e) {

                            }
                        }
                        value = 90;
                        publishProgress();

                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    if(firstTime == true){
                        isError = true ;
                    }
                }
            }
            else {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(firstTime == true){
                    isError = true ;
                }
            }
            lf = getFavoris();
            loginVerifed = true;
            return null;
        }

        @Override
        protected void onPostExecute(Void s) {
            if (loginVerifed) {
                if (isError){
                    mProgress.setProgress(0);
                    Toast.makeText(getApplicationContext(), "Connectez vous a internet et ressayer", Toast.LENGTH_LONG).show();
                    myDB.execSQL("update LOGIN set firstTime = \'true\' where firstTime  = \'false\'");
                }
                else {
                    startActivity(new Intent(Main2Activity.this, SearchActivity.class));
                    finish();
                }
            }
        }
    }

    public static boolean isFavoris(Object ob) {
        if(ob.getClass().getSimpleName().equals("Pharmacie")){
            Pharmacie s = (Pharmacie) ob ;
            for(int i=0;i<lf.size();i++){
                if(lf.get(i).getClass().getSimpleName().equals("Pharmacie")){
                    Pharmacie curr = (Pharmacie) lf.get(i);
                    if(curr.getAdresse().equals(s.getAdresse()) && curr.getSecteur().equals(s.getSecteur()) && curr.getPharmacie().equals(s.getPharmacie()) && curr.getPharmacien().equals(s.getPharmacien()))
                        return true ;
                }
            }
            return false ;
        }
        if(ob.getClass().getSimpleName().equals("Medecin")){
            Medecin s = (Medecin) ob ;
            for(int i=0;i<lf.size();i++){
                if(lf.get(i).getClass().getSimpleName().equals("Medecin")){
                    Medecin curr = (Medecin) lf.get(i);
                    if(curr.getAdresse().equals(s.getAdresse()) && curr.getName().equals(s.getName()) && curr.getSpeciality().equals(s.getSpeciality()))
                        return true ;
                }
            }
            return  false ;
        }

        if(ob.getClass().getSimpleName().equals("Clinique")){
            Clinique s = (Clinique) ob ;
            for(int i=0;i<lf.size();i++){
                if(lf.get(i).getClass().getSimpleName().equals("Clinique")){
                    Clinique curr = (Clinique) lf.get(i);
                    if(curr.getAdresse().equals(s.getAdresse()) && curr.getName().equals(s.getName()) && curr.getTel().equals(s.getTel()))
                        return true ;
                }
            }
            return  false ;
        }
        return false;
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders) && lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
    }
}

