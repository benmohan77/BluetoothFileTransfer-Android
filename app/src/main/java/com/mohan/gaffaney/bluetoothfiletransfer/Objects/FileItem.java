package com.mohan.gaffaney.bluetoothfiletransfer.Objects;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class FileItem {

    public Bitmap image;
    public String filename;

    public FileItem(Bitmap image, String filename){
        this.image = image;
        this.filename = filename;
    }
}
