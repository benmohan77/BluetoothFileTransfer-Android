package com.mohan.gaffaney.bluetoothfiletransfer;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mohan.gaffaney.bluetoothfiletransfer.Fragments.ShareFragment;

/**
 * This App is really only partially functional.
 * It can successfully broadcast image files to a receiving device, but due
 * to time constraints the android version of this app cannot successfully request.
 * As such it can run and scan, but nothing else without the iOS version running as well.
 *
 * Of Primary note is the ShareFragment and ScannerFragment.
 *
 * Code is not particularly well compartmentalized as this was developed in roughly a week by people who knew little about BLE.
 */
public class MainActivity extends AppCompatActivity {
    FragmentManager fm;
    BluetoothAdapter mBluetoothAdapter;

    private BluetoothManager mBluetoothManager;
    private static final String TAG = MainActivity.class.getSimpleName();
    private String name = "BEN";
    private static final int REQUEST_ENABLE_BT = 13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fm = getSupportFragmentManager();

        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            loadAfterPermissions();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ENABLE_BT: {
                if(resultCode == Activity.RESULT_OK){
                    loadAfterPermissions();
                }
            }
        }
    }


    private void loadAfterPermissions(){
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

}
