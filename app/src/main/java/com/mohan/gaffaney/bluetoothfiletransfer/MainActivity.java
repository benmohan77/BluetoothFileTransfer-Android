package com.mohan.gaffaney.bluetoothfiletransfer;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mohan.gaffaney.bluetoothfiletransfer.Fragments.ShareFragment;

public class MainActivity extends AppCompatActivity {
    FragmentManager fm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fm = getSupportFragmentManager();
        setContentView(R.layout.activity_main);

        ShareFragment shareFragment = new ShareFragment();
        shareFragment.setArguments(getIntent().getExtras());
        fm.beginTransaction().replace(R.id.fragment_container, shareFragment).commit();
    }
}
