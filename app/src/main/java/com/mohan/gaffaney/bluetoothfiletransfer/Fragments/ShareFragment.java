package com.mohan.gaffaney.bluetoothfiletransfer.Fragments;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.mohan.gaffaney.bluetoothfiletransfer.Adapters.FileAdapter;
import com.mohan.gaffaney.bluetoothfiletransfer.Objects.FileItem;
import com.mohan.gaffaney.bluetoothfiletransfer.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShareFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    private static final int READ_REQUEST_CODE = 42;
    private ImageView imageView;
   // private ListView list;
    private BluetoothAdapter mBluetoothAdapter;
    private final ScannerFragment scannerFragment = new ScannerFragment();

    public void setBluetoothAdapter(BluetoothAdapter bluetoothAdapter){
        this.mBluetoothAdapter = bluetoothAdapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.share_fragment, container, false);
        Button button = rootView.findViewById(R.id.select_files_btn);
       // list = rootView.findViewById(R.id.image_list);
        imageView = rootView.findViewById(R.id.share_image);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
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
//        List<FileItem> fileItems = new ArrayList<>();
//        if(resultData.getClipData() != null){
//            int count = resultData.getClipData().getItemCount();
//            for(int i = 0; i < count; i++){
//                uri = resultData.getClipData().getItemAt(i).getUri();
//                try {
//                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), uri);
//                    fileItems.add(new FileItem(bitmap, uri.getPath()));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        } else
//            if(resultData.getData() != null){
//            uri = resultData.getData();
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), uri);
//                fileItems.add(new FileItem(bitmap, uri.getPath()));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        list.setAdapter(new FileAdapter(getContext(), R.id.image_list, fileItems));
        if (resultData.getData() != null) {
            uri = resultData.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), uri);
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
