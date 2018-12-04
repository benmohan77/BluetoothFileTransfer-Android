package com.mohan.gaffaney.bluetoothfiletransfer.Objects;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.mohan.gaffaney.bluetoothfiletransfer.Constants;
import com.mohan.gaffaney.bluetoothfiletransfer.MainActivity;

import java.io.UnsupportedEncodingException;

public class PeripheralManager {
    BluetoothManager mBluetoothManager;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothGattServer mBluetoothGattServer;
    private String name = "BEN";
    private static final String TAG = MainActivity.class.getSimpleName();
    private Bitmap imageToSend;

    public PeripheralManager(Context context, BluetoothManager manager, BluetoothAdapter bluetoothAdapter){
        mBluetoothManager = manager;
        mBluetoothAdapter = bluetoothAdapter;
        mBluetoothGattServer = mBluetoothManager.openGattServer(context, mGattServerCallback);
    }

    public void startService(){
        initGattServer();
        startAdvertising();
    }

    private void initGattServer(){
        BluetoothGattService service =new BluetoothGattService(Constants.Service_UUID.getUuid(), BluetoothGattService.SERVICE_TYPE_PRIMARY);

        BluetoothGattCharacteristic nameCharacteristic = new BluetoothGattCharacteristic(
                Constants.Name_Characteristic.getUuid(),
                BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PERMISSION_READ);

        BluetoothGattCharacteristic transferCharacteristic = new BluetoothGattCharacteristic(
                Constants.Transfer_Characteristic.getUuid(),
                BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PERMISSION_READ);
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


    private BluetoothGattServerCallback mGattServerCallback = new BluetoothGattServerCallback(){
        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            super.onConnectionStateChange(device, status, newState);
        }




        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device,
                                                int requestId,
                                                int offset,
                                                BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
            Log.i(TAG, "onCharacteristicReadRequest " + characteristic.getUuid().toString());

            if (Constants.Name_Characteristic.getUuid().equals(characteristic.getUuid())) {
                try {
                    mBluetoothGattServer.sendResponse(device,
                            requestId,
                            BluetoothGatt.GATT_SUCCESS,
                            0,
                            name.getBytes("UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            if (Constants.Transfer_Characteristic.getUuid().equals(characteristic.getUuid())) {
                if(imageToSend != null){
                    mBluetoothGattServer.sendResponse(device,
                            requestId,
                            BluetoothGatt.GATT_SUCCESS,
                            0,
                            new byte[]{});
                }

            }

            /*
             * Unless the characteristic supports WRITE_NO_RESPONSE,
             * always send a response back for any request.
             */
            mBluetoothGattServer.sendResponse(device,
                    requestId,
                    BluetoothGatt.GATT_FAILURE,
                    0,
                    null);
        }

    };
}
