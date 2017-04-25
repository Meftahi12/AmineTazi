package com.navigation.drawer.activity.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import com.navigation.drawer.activity.R;

public class Login extends Activity {
    String Password,id;
    public static boolean isLogin = false ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        if(isLogin)
            startActivity(new Intent(this,Profil.class));
    }
    public void login(View view) {
        EditText pseudo =(EditText) findViewById(R.id.pseudo);
        EditText password=(EditText)findViewById(R.id.password);
        id = pseudo.getText().toString();
        Password  = password.getText().toString();
        new MyTask().execute();
    }
    public class MyTask extends AsyncTask<Void,Void,Void> {
        String console = "" ;
        @Override
        protected Void doInBackground(Void... params) {
                String url = "http://"+Signin.ipAdresse+"/covoiturage/login.php?id="+id+"&password="+Password ;
                try {
                    String content = HttpManager.getDatas(url);
                    if(content.equals("done")) {
                        isLogin = true ;
                        Profil.id = id;
                        startActivity(new Intent(Login.this, Acceuil.class));
                    }

                    else {
                        console = content;
                        publishProgress();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            Toast.makeText(getApplicationContext(),console,Toast.LENGTH_LONG).show();
        }
    }
}
