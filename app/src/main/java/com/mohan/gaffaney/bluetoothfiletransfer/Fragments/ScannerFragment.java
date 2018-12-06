package com.mohan.gaffaney.bluetoothfiletransfer.Fragments;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.mohan.gaffaney.bluetoothfiletransfer.Adapters.ScanResultAdapter;
import com.mohan.gaffaney.bluetoothfiletransfer.Constants;
import com.mohan.gaffaney.bluetoothfiletransfer.MainActivity;
import com.mohan.gaffaney.bluetoothfiletransfer.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;
import static android.graphics.Bitmap.CompressFormat.PNG;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class ScannerFragment extends ListFragment {

    private ScanCallback mScanCallback;
    private BluetoothAdapter mBluetoothAdapter;
    private ScanResultAdapter mAdapter;
    private Handler mHandler;
    private static final long SCAN_PERIOD = 5000;
    private BluetoothLeScanner mBluetoothLeScanner;
    private String selectedName;
    private BluetoothGatt mBluetoothGatt;
    private int lastPosition;
    private ByteArrayOutputStream outputStream;
    private ImageView imageView;
    private String NAME = "BEN";

    public ScannerFragment() {
        // Required empty public constructor
    }

    public void setBluetoothAdapter(BluetoothAdapter btAdapter){
        this.mBluetoothAdapter = btAdapter;
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new ScanResultAdapter(getActivity().getApplicationContext(), LayoutInflater.from(getActivity()));
        mHandler = new Handler();
        outputStream = new ByteArrayOutputStream();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_scanner, container, false);
        Button button = view.findViewById(R.id.scan_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startScanning();
            }
        });
        setListAdapter(mAdapter);
        imageView = view.findViewById(R.id.receive_image);
        // Inflate the layout for this fragment
        return view;

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getListView().setDivider(null);
        getListView().setDividerHeight(0);
        // Trigger refresh on app's 1st load
    }


    /**
     * Start scanning for BLE Advertisements (& set it up to stop after a set period of time).
     */
    public void startScanning() {
        if (mScanCallback == null) {
            Log.d(TAG, "Starting Scanning");

            // Will stop the scanning after a set time.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopScanning();
                }
            }, SCAN_PERIOD);

            // Kick off a new scan.
            mScanCallback = new SampleScanCallback();
            mBluetoothLeScanner.startScan(buildScanFilters(), buildScanSettings(), mScanCallback);

            String toastText = "TOAST" + " "
                    + TimeUnit.SECONDS.convert(SCAN_PERIOD, TimeUnit.MILLISECONDS) + " "
                    + "SECONDS";
            Toast.makeText(getActivity(), toastText, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity(), "Already Scanning", Toast.LENGTH_SHORT);
        }
    }


    /**
     * Stop scanning for BLE Advertisements.
     */
    public void stopScanning() {
        Log.d(TAG, "Stopping Scanning");

        // Stop the scan, wipe the callback.
        mBluetoothLeScanner.stopScan(mScanCallback);
        mScanCallback = null;

        // Even if no new results, update 'last seen' times.
        mAdapter.notifyDataSetChanged();
    }



    /**
     * Return a List of {@link ScanFilter} objects to filter by Service UUID.
     */
    private List<ScanFilter> buildScanFilters() {
        List<ScanFilter> scanFilters = new ArrayList<>();

        ScanFilter.Builder builder = new ScanFilter.Builder();
        // Comment out the below line to see all BLE devices around you
        builder.setServiceUuid(Constants.Service_UUID);
        scanFilters.add(builder.build());

        return scanFilters;
    }

    /**
     * Return a {@link ScanSettings} object set to use low power (to preserve battery life).
     */
    private ScanSettings buildScanSettings() {
        ScanSettings.Builder builder = new ScanSettings.Builder();
        builder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER);
        return builder.build();
    }
    /**
     * Custom ScanCallback object - adds to adapter on success, displays error on failure.
     */
    private class SampleScanCallback extends ScanCallback {

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);

            for (ScanResult result : results) {
                mAdapter.add(result);
            }
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            mAdapter.add(result);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Toast.makeText(getActivity(), "Scan failed with error: " + errorCode, Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        lastPosition = position;
        ScanResult scanResult = (ScanResult) mAdapter.getItem(position);
        mBluetoothGatt = scanResult.getDevice().connectGatt(getActivity(), false, mGattCallback);



    }

    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        private int byteCount;
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if(newState == BluetoothProfile.STATE_CONNECTED){
                gatt.discoverServices();
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status){
            if(Constants.Name_Characteristic.getUuid().equals(characteristic.getUuid())){
                try {
                    selectedName = new String(characteristic.getValue(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic){
            if(characteristic.getUuid().equals(Constants.Transfer_Characteristic.getUuid())){
                try{
                    String comp = new String(characteristic.getValue(), "UTF-8");
                    if(comp.equals(Constants.EOM)){
                        byte[] result = outputStream.toByteArray();
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        Bitmap img = BitmapFactory.decodeByteArray(result, 0, result.length);
                        final Bitmap outBmp = img.copy(Bitmap.Config.ARGB_8888, true);
                        gatt.disconnect();
                        byteCount = outputStream.size();
                        Log.i("BLE", "Size of file: " + byteCount);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageBitmap(outBmp);
                            }
                        });
                    } else{
                        byteCount = outputStream.size();
                        Log.i("BLE", "Size of file: " + byteCount);
                        outputStream.write(characteristic.getValue());
                    }
                }catch (UnsupportedEncodingException e){
                    e.printStackTrace();
                } catch (IOException e){
                    e.printStackTrace();
                }

            }
        }

        @Override
        // New services discovered
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                BluetoothGattService service = gatt.getService(Constants.Service_UUID.getUuid());
                if (service != null) {
//                    BluetoothGattCharacteristic nameCharacteristic = service.getCharacteristic(Constants.Name_Characteristic.getUuid());
//                    nameCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
//                    BluetoothGattCharacteristic transferCharacteristic = service.getCharacteristic(Constants.Transfer_Characteristic.getUuid());
//                    gatt.setCharacteristicNotification(transferCharacteristic, true);
//                    BluetoothGattDescriptor descriptor = transferCharacteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805F9B34FB"));
//                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
//                    gatt.writeDescriptor(descriptor);
//                    nameCharacteristic.setValue(NAME.getBytes());
//                    gatt.writeCharacteristic(nameCharacteristic);
//                    gatt.readCharacteristic(nameCharacteristic);
                    BluetoothGattCharacteristic nameCharacteristic = service.getCharacteristic(Constants.Name_Characteristic.getUuid());
                    nameCharacteristic.setValue(NAME.getBytes());

                    BluetoothGattCharacteristic transferCharacteristic = service.getCharacteristic(Constants.Transfer_Characteristic.getUuid());

                    gatt.setCharacteristicNotification(transferCharacteristic, true);
                    BluetoothGattDescriptor descriptor = transferCharacteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805F9B34FB"));
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);

                    gatt.writeCharacteristic(nameCharacteristic);

                    Log.i(TAG, "Service characteristic UUID found: " + service.getUuid().toString());
                } else {
                    Log.i(TAG, "Service characteristic not found for UUID: " + Constants.Service_UUID.getUuid());
                }
            }
        }
    };
}
