package com.navigation.drawer.activity.Activity.Alls;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.navigation.drawer.activity.Activity.BaseActivity;
import com.navigation.drawer.activity.Activity.Main2Activity;
import com.navigation.drawer.activity.Activity.Profiles.MedecinProfil;
import com.navigation.drawer.activity.Blocs.medecinBloc;
import com.navigation.drawer.activity.Classes.Medecin;
import com.navigation.drawer.activity.Classes.MyGPS;
import com.navigation.drawer.activity.R;

import java.util.HashMap;
import java.util.Vector;

public class AllMedecinsActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    public static SQLiteDatabase myDB= null;
    public static String currentSpeciality = null;
    Spinner s = null ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Main2Activity.historique.add("medecin");
        getLayoutInflater().inflate(R.layout.activity_all_medecins, frameLayout);

        mDrawerList.setItemChecked(position, true);
        setTitle(listArray[position]);
        ImageView b = (ImageView) findViewById(R.id.backM);
        b.setOnClickListener(this);

        myDB = this.openOrCreateDatabase("DatabaseName", MODE_PRIVATE, null);

        Cursor c = myDB.rawQuery("SELECT * FROM Medecin",null);
        int indSpec = c.getColumnIndex("speciality");

        Vector<String> Specialities = new Vector<String>();
        c.moveToFirst();
        if (c != null && c.getCount()!=0) {
            do {
                String spec = c.getString(indSpec).replaceAll("&","'");
                if(!Specialities.contains(spec.toUpperCase())){
                    Specialities.add(spec.toUpperCase());
                }
            }while(c.moveToNext());
        }

        int n = Specialities.size() ;

        String[] arraySpinner = new String[n+1];
        arraySpinner[0] = "Choisissez une specialité";
        for(int i=0;i<n;i++){
            arraySpinner[i+1] = Specialities.get(i);
        }


        s = (Spinner) findViewById(R.id.spinnerM);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner) {

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                convertView = super.getDropDownView(position, convertView,
                        parent);

                convertView.setVisibility(View.VISIBLE);
                ViewGroup.LayoutParams p = convertView.getLayoutParams();
                p.height = ViewGroup.LayoutParams.WRAP_CONTENT; // set the height
                convertView.setLayoutParams(p);

                return convertView;
            }
        };



        adapter.setDropDownViewResource(R.layout.my_spinner_textview);
        s.setAdapter(adapter);
        s.setOnItemSelectedListener(this);

        if(currentSpeciality != null){
            selectItem(currentSpeciality);
            setSpeciality(currentSpeciality);
        }

    }

    @Override
    public void onClick(View v) {
        if(v.getClass().getSimpleName().equals("ImageView")) {
            openActivity(0);
        }
        else {
            medecinBloc gb = (medecinBloc) v;
            MedecinProfil.currentMedecin = gb.medecin ;
            Intent intent = new Intent(this,MedecinProfil.class);
            startActivity(intent);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        TextView selectedText = (TextView) parent.getChildAt(0);
        if (selectedText != null) {
            selectedText.setTextColor(Color.WHITE);
        }
        if(position != 0) {

            String spec = parent.getSelectedItem().toString();

            setSpeciality(spec);
        }
        else {
            ScrollView scroll = (ScrollView) findViewById(R.id.scrollMedecin);
            scroll.setVisibility(View.INVISIBLE);
        }
    }

    private void setSpeciality(String spec) {
        GridLayout myMedecins = (GridLayout) findViewById(R.id.MedecinAll);

        myMedecins.removeAllViews();

        Cursor c = myDB.rawQuery("SELECT * FROM Medecin",null);
        c.moveToFirst();

        Vector<Medecin> speciality = new Vector<Medecin>();

        int indSpec = c.getColumnIndex("speciality");
        if (c != null && c.getCount()!=0) {
            do {
                String speci = c.getString(indSpec).replaceAll("&","'");
                if(speci.toUpperCase().equals(spec)){
                    int nameInd = c.getColumnIndex("name");
                    int AdresseInd = c.getColumnIndex("Adresse");
                    int telInd = c.getColumnIndex("tel");
                    speciality.add(new Medecin(c.getString(nameInd).replaceAll("&","'"), c.getString(AdresseInd).replaceAll("&","'"), c.getString(telInd).replaceAll("&","'"), speci, new MyGPS(0,0)));
                }
            }while(c.moveToNext());
        }

        myMedecins.setRowCount(speciality.size()/2+1);
        myMedecins.setColumnCount(2);


        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;
        int halfScreenWidth = (int)(screenWidth *0.5);
        int quarterScreenWidth = (int)(halfScreenWidth * 0.5);

        for(int i=0;i<speciality.size();i++){
            Medecin medecin = speciality.get(i);
            GridLayout.LayoutParams param =new GridLayout.LayoutParams();
            param.height = GridLayout.LayoutParams.WRAP_CONTENT;
            param.width = halfScreenWidth - 120;
            param.setMargins(10,10,10,10);
            param.setGravity(Gravity.CENTER_HORIZONTAL);
            medecinBloc gb = new medecinBloc(getApplicationContext(),medecin,param.width);
            gb.setOnClickListener(this);
            gb.setLayoutParams (param);

            myMedecins.addView(gb);
        }

        ScrollView scroll = (ScrollView) findViewById(R.id.scrollMedecin);
        scroll.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    public void selectItem(String name){
        for(int i=0;i<s.getAdapter().getCount();i++){
            String q =(String) s.getItemAtPosition(i);
            if(q.equals(name)){
                s.setSelection(i);
                return ;
            }
        }
    }
}
