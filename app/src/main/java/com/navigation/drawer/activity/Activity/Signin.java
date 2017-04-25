package com.navigation.drawer.activity.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.navigation.drawer.activity.R;

import java.io.IOException;


public class Signin extends Activity {
    public static String ipAdresse = "192.168.159.1";
    boolean isEdited = false ;

    String nom,prenom,sexe,email,tel,num_permi,date_permi,password,confirm,id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);


    }

    public void enregistrer(View view) {
        id = ((EditText) findViewById(R.id.id)).getText().toString();
        nom = ((EditText) findViewById(R.id.nom)).getText().toString();
        prenom = ((EditText) findViewById(R.id.prenom)).getText().toString();
        sexe = ((EditText) findViewById(R.id.sexe)).getText().toString();
        email = ((EditText) findViewById(R.id.email)).getText().toString();
        tel = ((EditText) findViewById(R.id.tel)).getText().toString();
        num_permi = ((EditText) findViewById(R.id.num_permi)).getText().toString();
        date_permi = ((EditText) findViewById(R.id.date_permi)).getText().toString();
        password = ((EditText) findViewById(R.id.password)).getText().toString();
        confirm = ((EditText) findViewById(R.id.confirm)).getText().toString();
        new MyTask().execute();

    }
    public class MyTask extends AsyncTask<Void,String,Void>{
        @Override
        protected Void doInBackground(Void... params) {
            if (!password.equals(confirm))
            {
                publishProgress("mot de passe incorect");
            }
            else
            {
                String url = "http://"+ipAdresse+"/covoiturage/Ajouter.php?id="+id+"&password="+password+"&nom="+nom+"&prenom="+prenom+"&sexe="+sexe+"&email="+email+"&tel="+tel+"&num_permi="+num_permi+"&date_permi="+date_permi ;
                try {
                    String reponse = HttpManager.getDatas(url) ;
                    if(reponse.equals("done"))
                        isEdited = true ;
                    publishProgress(reponse);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(isEdited)
                startActivity(new Intent(Signin.this,MainActivity.class));
        }
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Toast.makeText(getApplicationContext(),values[0],Toast.LENGTH_LONG).show();
        }
    }
}
