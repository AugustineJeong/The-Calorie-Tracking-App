package com.example.the_calories_tracking_app;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.widget.ImageView;

public class ProgramActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program);

        Bundle bundle = getIntent().getExtras();
        Bitmap bitmap = (Bitmap) bundle.get("image");

        ImageView imageView = findViewById(R.id.imageView3);
        imageView.setImageBitmap(bitmap);
    }
}
