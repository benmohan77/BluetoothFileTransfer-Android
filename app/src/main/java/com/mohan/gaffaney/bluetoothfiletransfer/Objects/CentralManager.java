//package com.mohan.gaffaney.bluetoothfiletransfer.Objects;
//
//import android.bluetooth.le.BluetoothLeScanner;
//import android.bluetooth.le.ScanCallback;
//import android.bluetooth.le.ScanFilter;
//import android.bluetooth.le.ScanResult;
//import android.bluetooth.le.ScanSettings;
//import android.content.Context;
//import android.os.Handler;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.mohan.gaffaney.bluetoothfiletransfer.Constants;
//import com.mohan.gaffaney.bluetoothfiletransfer.Fragments.ScannerFragment;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//import static android.content.ContentValues.TAG;
//
//public class CentralManager {
//
//    private ScanCallback mScanCallback;
//    private Handler mHandler;
//    private BluetoothLeScanner mBluetoothLeScanner;
//    private static final long SCAN_PERIOD = 5000;
//
//
//    /**
//     * Start scanning for BLE Advertisements (& set it up to stop after a set period of time).
//     */
//    public void startScanning(Context context) {
//        if (mScanCallback == null) {
//            Log.d(TAG, "Starting Scanning");
//
//            // Will stop the scanning after a set time.
//            mHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    stopScanning();
//                }
//            }, SCAN_PERIOD);
//
//            // Kick off a new scan.
//            mScanCallback = new SampleScanCallback();
//            mBluetoothLeScanner.startScan(buildScanFilters(), buildScanSettings(), mScanCallback);
//
//            String toastText = "TOAST" + " "
//                    + TimeUnit.SECONDS.convert(SCAN_PERIOD, TimeUnit.MILLISECONDS) + " "
//                    + "SECONDS";
//            Toast.makeText(context, toastText, Toast.LENGTH_LONG).show();
//        } else {
//            Toast.makeText(context, "Already Scanning", Toast.LENGTH_SHORT);
//        }
//    }
//
//    /**
//     * Stop scanning for BLE Advertisements.
//     */
//    public void stopScanning() {
//        Log.d(TAG, "Stopping Scanning");
//
//        // Stop the scan, wipe the callback.
//        mBluetoothLeScanner.stopScan(mScanCallback);
//        mScanCallback = null;
//
//        // Even if no new results, update 'last seen' times.
//        mAdapter.notifyDataSetChanged();
//    }
//
//
//
//    /**
//     * Return a List of {@link ScanFilter} objects to filter by Service UUID.
//     */
//    private List<ScanFilter> buildScanFilters() {
//        List<ScanFilter> scanFilters = new ArrayList<>();
//
//        ScanFilter.Builder builder = new ScanFilter.Builder();
//        // Comment out the below line to see all BLE devices around you
//        builder.setServiceUuid(Constants.Service_UUID);
//        scanFilters.add(builder.build());
//
//        return scanFilters;
//    }
//
//    /**
//     * Return a {@link ScanSettings} object set to use low power (to preserve battery life).
//     */
//    private ScanSettings buildScanSettings() {
//        ScanSettings.Builder builder = new ScanSettings.Builder();
//        builder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER);
//        return builder.build();
//    }
//    /**
//     * Custom ScanCallback object - adds to adapter on success, displays error on failure.
//     */
//    private class SampleScanCallback extends ScanCallback {
//
//        @Override
//        public void onBatchScanResults(List<ScanResult> results) {
//            super.onBatchScanResults(results);
//
//            for (ScanResult result : results) {
//                mAdapter.add(result);
//            }
//            mAdapter.notifyDataSetChanged();
//        }
//
//        @Override
//        public void onScanResult(int callbackType, ScanResult result) {
//            super.onScanResult(callbackType, result);
//
//            mAdapter.add(result);
//            mAdapter.notifyDataSetChanged();
//        }
//
//        @Override
//        public void onScanFailed(int errorCode) {
//            super.onScanFailed(errorCode);
//            Toast.makeText(getActivity(), "Scan failed with error: " + errorCode, Toast.LENGTH_LONG)
//                    .show();
//        }
//    }
//
//
//}
