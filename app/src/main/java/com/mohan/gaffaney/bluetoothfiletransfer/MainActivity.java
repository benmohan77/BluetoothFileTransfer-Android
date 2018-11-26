package com.mohan.gaffaney.bluetoothfiletransfer;

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
        fm.beginTransaction().replace(R.id.fragment_container, new ShareFragment()).commit();
    }
}
