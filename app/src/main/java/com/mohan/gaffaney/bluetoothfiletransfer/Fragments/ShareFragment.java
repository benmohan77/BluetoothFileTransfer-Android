package com.mohan.gaffaney.bluetoothfiletransfer.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.mohan.gaffaney.bluetoothfiletransfer.Adapters.FileAdapter;
import com.mohan.gaffaney.bluetoothfiletransfer.Objects.FileItem;
import com.mohan.gaffaney.bluetoothfiletransfer.Objects.PeripheralManager;
import com.mohan.gaffaney.bluetoothfiletransfer.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShareFragment extends Fragment {

    private static final int READ_REQUEST_CODE = 42;
    private ImageView imageView;
    BluetoothGattServer mBluetoothGattServer;

    // private ListView list;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private final ScannerFragment scannerFragment = new ScannerFragment();
    private  PeripheralManager peripheralManager;

    public void setBluetoothServices(BluetoothManager bluetoothManager, BluetoothAdapter bluetoothAdapter){
        this.mBluetoothAdapter = bluetoothAdapter;
        this.mBluetoothManager = bluetoothManager;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        peripheralManager = new PeripheralManager(getContext(), mBluetoothManager, mBluetoothAdapter);
        peripheralManager.startService();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.share_fragment, container, false);
        Button button = rootView.findViewById(R.id.select_files_btn);
        imageView = rootView.findViewById(R.id.share_image);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");

                startActivityForResult(intent, READ_REQUEST_CODE);
            }
        });

        Button shareButton = rootView.findViewById(R.id.share_files_btn);
        scannerFragment.setBluetoothAdapter(((BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter());
        shareButton.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, scannerFragment).commit();
              }
          });
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        Uri uri;
        if (resultData.getData() != null) {
            uri = resultData.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), uri);
                peripheralManager.setImageToSend(bitmap);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onPause(){
        super.onPause();
    }
}
