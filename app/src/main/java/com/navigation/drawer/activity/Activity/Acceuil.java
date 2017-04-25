package com.navigation.drawer.activity.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.navigation.drawer.activity.R;

public class Acceuil extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_acceuil, frameLayout);

        mDrawerList.setItemChecked(position, true);
        setTitle(listArray[position]);
    }
}
