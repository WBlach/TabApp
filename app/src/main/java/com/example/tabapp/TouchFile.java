package com.example.tabapp;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TouchFile {

    private final File dir;
    public TouchFile(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/TabApp");
        }
        else{
            dir = new File(Environment.getExternalStorageDirectory() + "/Tabapp");
        }
    }

    public void saveToCSV(Context context, String data, String fileName){
        if(!dir.exists()) dir.mkdir();
        File file = new File(dir.getAbsolutePath(), fileName+".csv");
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
            writer.write(data);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }


}
