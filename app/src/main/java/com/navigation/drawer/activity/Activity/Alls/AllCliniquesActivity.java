package com.navigation.drawer.activity.Activity.Alls;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.navigation.drawer.activity.Activity.BaseActivity;
import com.navigation.drawer.activity.Activity.Main2Activity;
import com.navigation.drawer.activity.Activity.Profiles.CliniqueProfil;
import com.navigation.drawer.activity.Activity.SearchActivity;
import com.navigation.drawer.activity.Blocs.CliniqueBloc;
import com.navigation.drawer.activity.Classes.Clinique;
import com.navigation.drawer.activity.R;

import java.util.Vector;

public class AllCliniquesActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    public static SQLiteDatabase myDB= null;
    public static String currentCateg = null ;

    Spinner s = null ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Main2Activity.historique.add("clinique");
        getLayoutInflater().inflate(R.layout.activity_all_cliniques, frameLayout);

        mDrawerList.setItemChecked(position, true);
        setTitle(listArray[position]);
        ImageView b = (ImageView) findViewById(R.id.backC);
        b.setOnClickListener(this);

        myDB = this.openOrCreateDatabase("DatabaseName", MODE_PRIVATE, null);


        String[] arraySpinner = new String[3];
        arraySpinner[0] = "Choisissez une categorie";
        arraySpinner[1] = "Hopitaux";
        arraySpinner[2] = "Cliniques";

        s = (Spinner) findViewById(R.id.spinnerC);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner) {

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                convertView = super.getDropDownView(position, convertView,
                        parent);

                convertView.setVisibility(View.VISIBLE);
                ViewGroup.LayoutParams p = convertView.getLayoutParams();
                p.height = 100; // set the height
                convertView.setLayoutParams(p);

                return convertView;
            }
        };
        adapter.setDropDownViewResource(R.layout.my_spinner_textview);
        s.setAdapter(adapter);
        s.setOnItemSelectedListener(this);

        if(currentCateg != null ){
            setCateg(currentCateg);
            selectItem(currentCateg);
        }


    }

    @Override
    public void onClick(View v) {
        if(v.getClass().getSimpleName().equals("ImageView")) {
            openActivity(0);
        }
        else {
            CliniqueBloc gb = (CliniqueBloc) v;
            CliniqueProfil.currentClinique = gb.clinique ;
            Intent intent = new Intent(this,CliniqueProfil.class);
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

            String categ = parent.getSelectedItem().toString();


            setCateg(categ);
        }
        else {
            ScrollView scroll = (ScrollView) findViewById(R.id.scrollClinique);
            scroll.setVisibility(View.INVISIBLE);
        }
    }

    private void setCateg(String categ) {
        GridLayout myCliniques = (GridLayout) findViewById(R.id.CliniqueAll);

        myCliniques.removeAllViews();

        Vector<Clinique> list = new Vector<Clinique>();


        Cursor c = myDB.rawQuery("SELECT * FROM Clinique",null);

        int indCateg = c.getColumnIndex("categorie");
        int nameInd = c.getColumnIndex("name");
        int adresseInd = c.getColumnIndex("adresse");
        int telInd = c.getColumnIndex("tel");
        int infoInd = c.getColumnIndex("info");

        c.moveToFirst();
        if (c != null && c.getCount()!=0) {
            do {
                String categorie = c.getString(indCateg).replaceAll("&","'");
                if(categorie.equals(categ)){
                    Clinique cl = new Clinique(c.getString(nameInd).replaceAll("&","'"),categorie,c.getString(adresseInd).replaceAll("&","'"),c.getString(telInd).replaceAll("&","'"), null);
                    cl.setInfo(c.getString(infoInd).replaceAll("&","'"));
                    list.add(cl);
                }
            }while(c.moveToNext());
        }


        myCliniques.setRowCount((list.size()/2)+1);
        myCliniques.setColumnCount(2);


        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);

        double scale = getApplicationContext().getResources().getDisplayMetrics().density;
        float nb =  getResources().getDimension(R.dimen.activity_horizontal_margin)*2 ;
        float width = (float) (40 * scale + 0.5f + nb);
        float screenWidth = size.x - width;
        int halfScreenWidth = (int)(screenWidth *0.5);

        for(int i=0;i<list.size();i++){
            Clinique clinique = list.get(i);
            GridLayout.LayoutParams param =new GridLayout.LayoutParams();
            param.height = GridLayout.LayoutParams.WRAP_CONTENT;
            param.width = halfScreenWidth;
            param.setMargins(5,5,5,5);
            param.setGravity(Gravity.CENTER_HORIZONTAL);
            CliniqueBloc gb = new CliniqueBloc(getApplicationContext(),clinique , param.width);
            gb.setOnClickListener(this);
            gb.setLayoutParams (param);

            myCliniques.addView(gb);
        }

        ScrollView scroll = (ScrollView) findViewById(R.id.scrollClinique);
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
