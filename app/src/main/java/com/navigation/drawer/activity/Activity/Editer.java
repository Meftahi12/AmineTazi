package com.navigation.drawer.activity.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.navigation.drawer.activity.R;

import java.io.IOException;

public class Editer extends BaseActivity {
    String nom,prenom,sexe,email,tel,num_permi,date_permi,password,confirm;
    boolean isEdited = false ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_editer, frameLayout);

        mDrawerList.setItemChecked(position, true);
        setTitle(listArray[position]);
        ((EditText) findViewById(R.id.nome)).setText(Profil.currentUser.getNom());
        ((EditText) findViewById(R.id.prenome)).setText(Profil.currentUser.getPrenom());
        ((EditText) findViewById(R.id.sexee)).setText(Profil.currentUser.getSexe());
        ((EditText) findViewById(R.id.emaile)).setText(Profil.currentUser.getEmail());
        ((EditText) findViewById(R.id.tele)).setText(Profil.currentUser.getTel());
        ((EditText) findViewById(R.id.num_permie)).setText(Profil.currentUser.getNum_permi());
        ((EditText) findViewById(R.id.date_permie)).setText(Profil.currentUser.getDate_permi());
        ((EditText) findViewById(R.id.passworde)).setText(Profil.currentUser.getPassword());
        ((EditText) findViewById(R.id.confirme)).setText(Profil.currentUser.getPassword());
    }

    public void edit(View view) {
        nom = ((EditText) findViewById(R.id.nome)).getText().toString();
        prenom = ((EditText) findViewById(R.id.prenome)).getText().toString();
        sexe = ((EditText) findViewById(R.id.sexee)).getText().toString();
        email = ((EditText) findViewById(R.id.emaile)).getText().toString();
        tel = ((EditText) findViewById(R.id.tele)).getText().toString();
        num_permi = ((EditText) findViewById(R.id.num_permie)).getText().toString();
        date_permi = ((EditText) findViewById(R.id.date_permie)).getText().toString();
        password = ((EditText) findViewById(R.id.passworde)).getText().toString();
        confirm = ((EditText) findViewById(R.id.confirme)).getText().toString();
        new MyTask().execute();
    }

    public class MyTask extends AsyncTask<Void,String,Void> {
        @Override
        protected Void doInBackground(Void... params) {
            if (!password.equals(confirm))
            {
                publishProgress("mot de passe incorect");
            }
            else{
                String url = "http://"+Signin.ipAdresse+"/covoiturage/Editer.php?id="+Profil.id+"&password="+password+"&nom="+nom+"&prenom="+prenom+"&sexe="+sexe+"&email="+email+"&tel="+tel+"&num_permi="+num_permi+"&date_permi="+date_permi ;
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
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Toast.makeText(getApplicationContext(),values[0],Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(isEdited)
                startActivity(new Intent(Editer.this,Profil.class));
        }
    }
}
