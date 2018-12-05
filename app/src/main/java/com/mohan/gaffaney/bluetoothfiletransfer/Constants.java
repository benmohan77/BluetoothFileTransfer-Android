package com.mohan.gaffaney.bluetoothfiletransfer;

import android.os.ParcelUuid;

import java.util.UUID;

public class Constants {
    public static final ParcelUuid Service_UUID = ParcelUuid
            .fromString("E71EE188-279F-4ED6-8055-12D77BFD900C");
    public static final ParcelUuid Transfer_Characteristic = ParcelUuid.fromString("2F016955-E675-49A6-9176-111E2A1CF333");
    public static final ParcelUuid Name_Characteristic = ParcelUuid.fromString("2F016955-E675-49A6-9176-111E2A1CF331");
    public static final String EOM = "{{{EOM}}}";
}
