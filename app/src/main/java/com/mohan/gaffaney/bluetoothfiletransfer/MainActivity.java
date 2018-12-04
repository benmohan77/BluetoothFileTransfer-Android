package com.mohan.gaffaney.bluetoothfiletransfer;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mohan.gaffaney.bluetoothfiletransfer.Fragments.ShareFragment;
import com.mohan.gaffaney.bluetoothfiletransfer.Objects.PeripheralManager;

public class MainActivity extends AppCompatActivity {
    FragmentManager fm;
    BluetoothAdapter mBluetoothAdapter;

    private BluetoothManager mBluetoothManager;
    private static final String TAG = MainActivity.class.getSimpleName();
    private String name = "BEN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fm = getSupportFragmentManager();

        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }
        setContentView(R.layout.activity_main);

        ShareFragment shareFragment = new ShareFragment();
        shareFragment.setBluetoothServices(mBluetoothManager, mBluetoothAdapter);

        fm.beginTransaction().replace(R.id.fragment_container, shareFragment).commit();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
                return;
            }

        }
    }

}
