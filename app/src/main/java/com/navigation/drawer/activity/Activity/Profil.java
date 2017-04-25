package com.navigation.drawer.activity.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.navigation.drawer.activity.R;

import org.json.JSONException;

import java.io.IOException;




/*

FIRST ONE

        Daba ay activity bghiti tdirha jdida  Atcreyeha 3adi empty
        atlqaha extends shi haja .. hyed dik l haja w dir blastha dik BaseActivity

 */




public class Profil extends BaseActivity {
    public static String id = "" ;

    ProgressDialog pDialog = null ;
    public static User currentUser = null ;
    boolean loginVerified = false ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*

        SECOND

        Atlqa fiha wahd setContentView hiedha o dir blastha had 3 d stor
         hi blast dik activity_profil nta atdir smit l xml dl activity
         Apres sir l BaseActivity bl kheft

        */

        ////////////////

        getLayoutInflater().inflate(R.layout.activity_profil, frameLayout);
        mDrawerList.setItemChecked(position, true);
        setTitle(listArray[position]);

        ////////////////

        new getS().execute();


    }

    public void goDisconnect(View view) {
        Login.isLogin = false ;
        startActivity(new Intent(Profil.this,Login.class));

    }

    public class getS extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Profil.this);
            pDialog.setMessage("Please Wait");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(loginVerified){
                pDialog.dismiss();
                ((TextView) findViewById(R.id.nomt)).setText(Profil.currentUser.getNom()+"\n"+Profil.currentUser.getPrenom());
                ((TextView) findViewById(R.id.sexet)).setText(Profil.currentUser.getSexe());
                ((TextView) findViewById(R.id.emailt)).setText(Profil.currentUser.getEmail());
                ((TextView) findViewById(R.id.telt)).setText(Profil.currentUser.getTel());
                ((TextView) findViewById(R.id.num_permit)).setText(Profil.currentUser.getNum_permi());
                ((TextView) findViewById(R.id.date_permit)).setText(Profil.currentUser.getDate_permi());
            }
        }




        @Override
        protected Void doInBackground(Void... params) {

            String url = "http://" + Signin.ipAdresse + "/covoiturage/getprofile.php?id=" + Profil.id;
            try {
                String content = HttpManager.getDatas(url);
                currentUser = HttpManager.JSONUser(content);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            loginVerified = true ;
            return null;
        }

    }

    public void goEdit(View view) {
        startActivity(new Intent(this,Editer.class));
    }
}
