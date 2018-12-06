package com.mohan.gaffaney.bluetoothfiletransfer.Fragments;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;


import com.mohan.gaffaney.bluetoothfiletransfer.Constants;
import com.mohan.gaffaney.bluetoothfiletransfer.MainActivity;
import com.mohan.gaffaney.bluetoothfiletransfer.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.UUID;

public class ShareFragment extends Fragment {
    boolean startTransmissionOnResult =false;
    private static final int READ_REQUEST_CODE = 42;
    private ImageView imageView;
    BluetoothGattServer mBluetoothGattServer;

    // private ListView list;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private final ScannerFragment scannerFragment = new ScannerFragment();
    Handler mHandler;

    public void setBluetoothServices(BluetoothManager bluetoothManager, BluetoothAdapter bluetoothAdapter){
        this.mBluetoothAdapter = bluetoothAdapter;
        this.mBluetoothManager = bluetoothManager;
    }

    public ShareFragment(){
    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mBluetoothAdapter.setName(name);
        mHandler = new Handler();
        mBluetoothGattServer = mBluetoothManager.openGattServer(getContext(), mGattServerCallback);
        initGattServer();
        startAdvertising();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.share_fragment, container, false);
        Button button = rootView.findViewById(R.id.select_files_btn);
        imageView = rootView.findViewById(R.id.share_image);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
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

    public void selectImage(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");

        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        Uri uri;
        if (resultData.getData() != null) {
            uri = resultData.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), uri);
                setImageToSend(bitmap);
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

    private String name = "Ben";
    private static final String TAG = MainActivity.class.getSimpleName();
    private Bitmap imageToSend;
    private BluetoothGattCharacteristic transferCharacteristic;
    private BluetoothGattCharacteristic nameCharacteristic;



    private void initGattServer(){
        BluetoothGattService service =new BluetoothGattService(Constants.Service_UUID.getUuid(), BluetoothGattService.SERVICE_TYPE_PRIMARY);

        nameCharacteristic = new BluetoothGattCharacteristic(
                Constants.Name_Characteristic.getUuid(),
                BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_INDICATE |  BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE,
                BluetoothGattCharacteristic.PERMISSION_READ | BluetoothGattCharacteristic.PERMISSION_WRITE);

        transferCharacteristic = new BluetoothGattCharacteristic(
                Constants.Transfer_Characteristic.getUuid(),
                BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_INDICATE,
                BluetoothGattCharacteristic.PERMISSION_READ);

        BluetoothGattDescriptor gD = new BluetoothGattDescriptor(UUID.fromString("00002902-0000-1000-8000-00805F9B34FB"), BluetoothGattDescriptor.PERMISSION_WRITE | BluetoothGattDescriptor.PERMISSION_READ);
        transferCharacteristic.addDescriptor(gD);
        service.addCharacteristic(nameCharacteristic);

        service.addCharacteristic(transferCharacteristic);

        mBluetoothGattServer.addService(service);
    }

    /**
     * Starts BLE Advertising by starting {@code AdvertiserService}.
     */
    private void startAdvertising() {
        BluetoothLeAdvertiser advertiser =
                mBluetoothAdapter.getBluetoothLeAdvertiser();

        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                .setConnectable(true)
                .build();

        AdvertiseData data = new AdvertiseData.Builder()
                .setIncludeDeviceName(false)
                .addServiceUuid(Constants.Service_UUID)
                .build();


        AdvertiseCallback advertisingCallback = new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                Log.e( "BLE", "Advertising onStartSuccess: ");
                super.onStartSuccess(settingsInEffect);
            }

            @Override
            public void onStartFailure(int errorCode) {
                String description = "";
                if (errorCode == AdvertiseCallback.ADVERTISE_FAILED_FEATURE_UNSUPPORTED)
                    description = "ADVERTISE_FAILED_FEATURE_UNSUPPORTED";
                else if (errorCode == AdvertiseCallback.ADVERTISE_FAILED_TOO_MANY_ADVERTISERS)
                    description = "ADVERTISE_FAILED_TOO_MANY_ADVERTISERS";
                else if (errorCode == AdvertiseCallback.ADVERTISE_FAILED_ALREADY_STARTED)
                    description = "ADVERTISE_FAILED_ALREADY_STARTED";
                else if (errorCode == AdvertiseCallback.ADVERTISE_FAILED_DATA_TOO_LARGE)
                    description = "ADVERTISE_FAILED_DATA_TOO_LARGE";
                else if (errorCode == AdvertiseCallback.ADVERTISE_FAILED_INTERNAL_ERROR)
                    description = "ADVERTISE_FAILED_INTERNAL_ERROR";
                else description = "unknown";
                Log.e( "BLE", "Advertising onStartFailure: " + description );
                super.onStartFailure(errorCode);
            }
        };

        advertiser.startAdvertising(settings, data, advertisingCallback);
    }

    public void setImageToSend(Bitmap bitmap){
        this.imageToSend = bitmap;
    }

    private BluetoothGattServerCallback mGattServerCallback = new BluetoothGattServerCallback() {
        int currentIndex;
        byte[] bytesToSend;
        ByteArrayOutputStream byteArrayOutputStream;
        byte[] b;
        int finalIndex;
        boolean finalSent = false;
        boolean needToSendEOM = false;

        @Override
        public void onServiceAdded(int status, BluetoothGattService service){
            super.onServiceAdded(status, service);
        }

        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            super.onConnectionStateChange(device, status, newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i("BLE", "Device Connected");
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i("BLE", "Device Disconnected");
            }
        }

        public void startTransmission(BluetoothDevice device){
            finalIndex = 0;
            currentIndex = 0;
            finalSent = false;
            needToSendEOM = false;
            byteArrayOutputStream = new ByteArrayOutputStream();
            imageToSend.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream );
            b = byteArrayOutputStream.toByteArray();
            bytesToSend = new byte[20];
            finalIndex = b.length - 1;
            currentIndex = 0;

            bytesToSend = Arrays.copyOfRange(b, currentIndex, currentIndex + 20);

            transferCharacteristic.setValue(bytesToSend);
            mBluetoothGattServer.notifyCharacteristicChanged(device, transferCharacteristic, true);
            currentIndex += 20;
        }

        @Override
        public void onCharacteristicWriteRequest(final BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, final byte[] value) {
            Log.e("BLE", "WRITE REQUESTED FROM CENTRAL");
            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);
            if (Constants.Name_Characteristic.getUuid().equals(characteristic.getUuid())) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Send Image");
                        try {
                            builder.setMessage("Do you want to send an image to " + new String(value, "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(imageToSend == null){
                                    selectImage();
                                    startTransmission(device);
                                }else {
                                    startTransmission(device);
                                }
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                transferCharacteristic.setValue(Constants.EOM.getBytes());
                                mBluetoothGattServer.notifyCharacteristicChanged(device, transferCharacteristic, false);
                            }
                        });
                        builder.show();
                    }
                });
//                if(imageToSend != null){
//                    finalIndex = 0;
//                    currentIndex = 0;
//                    finalSent = false;
//                    needToSendEOM = false;
//                    byteArrayOutputStream = new ByteArrayOutputStream();
//                    imageToSend.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream );
//                    b = byteArrayOutputStream.toByteArray();
//                    bytesToSend = new byte[20];
//                    finalIndex = b.length - 1;
//                    currentIndex = 0;
//
//                    bytesToSend = Arrays.copyOfRange(b, currentIndex, currentIndex + 20);
//
//                    transferCharacteristic.setValue(bytesToSend);
//                    mBluetoothGattServer.notifyCharacteristicChanged(device, transferCharacteristic, true);
//                    currentIndex += 20;
//                } else{
//                    transferCharacteristic.setValue(Constants.EOM.getBytes());
//                    mBluetoothGattServer.notifyCharacteristicChanged(device, transferCharacteristic, false);
//                }
            }
        }


        @Override
        public void onNotificationSent(BluetoothDevice device, int status){
            super.onNotificationSent(device, status);
            if(currentIndex + 20 < finalIndex){
                bytesToSend = Arrays.copyOfRange(b, currentIndex, currentIndex + 20);
                currentIndex += 20;
                transferCharacteristic.setValue(bytesToSend);
                mBluetoothGattServer.notifyCharacteristicChanged(device, transferCharacteristic, true);
            } else if(!finalSent && needToSendEOM){
                transferCharacteristic.setValue(Constants.EOM.getBytes());
                mBluetoothGattServer.notifyCharacteristicChanged(device, transferCharacteristic, false);
                finalSent = true;
            } else if(currentIndex + 20 >= finalIndex && !needToSendEOM){
                byte[] finalBytes = Arrays.copyOfRange(b, currentIndex, finalIndex);
                Log.i("BLE", "Current Index: " + currentIndex);
                transferCharacteristic.setValue(finalBytes);
                mBluetoothGattServer.notifyCharacteristicChanged(device, transferCharacteristic, true);
                needToSendEOM = true;
            }
        }

        @Override
        public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {

            // now tell the connected device that this was all successfull
            mBluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value);

        }
    };
}
