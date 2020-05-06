package com.example.the_calories_tracking_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.File;

// CITATION: Parts of code copied and edited from
// Google's Firebase documentation https://firebase.google.com/docs/ml-kit/android/recognize-text#java_103
public class ProgramActivity extends AppCompatActivity {

    private String recognizedText;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program);

        String path = getIntent().getStringExtra("imagePath");

        Bitmap bitmap = null;
        try {
            bitmap = rotateBitmap(BitmapFactory.decodeFile(path), 90);
        } catch (Exception e) {
            System.out.println("no file found");
        }

        File file = new File(path, "tempFile.jpeg");
        file.delete();

        FirebaseVisionImage image = null;
        try {
            image = FirebaseVisionImage.fromBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Firebase image fail");
        }

        ImageView imageView = findViewById(R.id.imageView3);
        imageView.setImageBitmap(bitmap);

        if (image != null) {
            FirebaseVisionTextRecognizer recognizer = FirebaseVision.getInstance()
                    .getOnDeviceTextRecognizer();
            recognizer.processImage(image)
                    .addOnSuccessListener(
                            new OnSuccessListener<FirebaseVisionText>() {
                                @Override
                                public void onSuccess(FirebaseVisionText texts) {
                                    TextView resultTextView = findViewById(R.id.calories);
                                    recognizedText = texts.getText();
                                    resultTextView.setText(recognizedText);
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Task failed with an exception
                                    e.printStackTrace();
                                }
                            });
        }
    }

    private Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
                source.getHeight(), matrix, true);
    }
}