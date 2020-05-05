package com.example.the_calories_tracking_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

// CITATION: saveImageFile() and takePictureIntent() methods based on
// Google's Android Developers Documentation.
// https://developer.android.com/training/camera/photobasics#java

public class MainActivity extends AppCompatActivity {

    private String photoSaveLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private File saveImageFile() throws IOException {
        String name = "JPG_scan_image";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageSaveFile = File.createTempFile(name, ".jpg", storageDir);
        photoSaveLocation = imageSaveFile.getAbsolutePath();
        return imageSaveFile;
    }


    public void takePhotoIntent(View view) {
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePhotoIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = saveImageFile();
            } catch (IOException e) {
                System.out.println("error creating file!");
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider", photoFile);
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePhotoIntent, 1);
            }
        }
    }
}
