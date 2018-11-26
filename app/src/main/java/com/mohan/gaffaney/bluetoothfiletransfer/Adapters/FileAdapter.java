package com.mohan.gaffaney.bluetoothfiletransfer.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mohan.gaffaney.bluetoothfiletransfer.Objects.FileItem;
import com.mohan.gaffaney.bluetoothfiletransfer.R;

import java.util.List;

public class FileAdapter extends ArrayAdapter<FileItem> {

    public FileAdapter(Context context, int resource, List<FileItem> items){
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        FileItem item = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.file_item, parent,false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.row_filename);
        ImageView img = (ImageView) convertView.findViewById(R.id.row_img);

        name.setText(item.filename);
        img.setImageBitmap(item.image);

        return convertView;
    }
}
