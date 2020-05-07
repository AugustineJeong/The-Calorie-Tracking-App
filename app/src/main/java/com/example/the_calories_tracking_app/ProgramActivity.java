package com.example.the_calories_tracking_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.widget.EditText;
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
    private int caloriesIndex;
    private int fatIndex;
    private int carbohydrateIndex;
    private int proteinIndex;
    private int cholesterolIndex;
    private int sodiumIndex;
    private int sugarIndex;

    private TextView calories;
    private TextView fat;
    private TextView carbohydrate;
    private TextView protein;
    private TextView cholesterol;
    private TextView sodium;
    private TextView sugar;

    private EditText caloriesValue;
    private EditText fatValue;
    private EditText carbohydrateValue;
    private EditText proteinValue;
    private EditText cholesterolValue;
    private EditText sodiumValue;
    private EditText sugarValue;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program);

        calories = findViewById(R.id.calories);
        fat = findViewById(R.id.fat);
        carbohydrate = findViewById(R.id.carbohydrate);
        protein = findViewById(R.id.protein);
        cholesterol = findViewById(R.id.cholesterol);
        sodium = findViewById(R.id.sodium);
        sugar = findViewById(R.id.sugar);

        caloriesValue = findViewById(R.id.caloriesText);
        fatValue = findViewById(R.id.fatText);
        carbohydrateValue = findViewById(R.id.carbohydrateText);
        proteinValue = findViewById(R.id.proteinText);
        cholesterolValue = findViewById(R.id.cholesterolText);
        sodiumValue = findViewById(R.id.sodiumText);
        sugarValue = findViewById(R.id.sugarText);

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

        updateTable();
    }

    private Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
                source.getHeight(), matrix, true);
    }

    private void updateTable() {
        recognizedText.toLowerCase();
        int length = recognizedText.length();
        String substring;
        for (int i = 1; i < length; i++) {
            substring = recognizedText.substring(0, i);
            if (substring.contains("calories")) {
                caloriesIndex = i;
            }
            if (substring.contains("fat")) {
                fatIndex = i;
            }
            if (substring.contains("carbohydrate")) {
                carbohydrateIndex = i;
            }
            if (substring.contains("protein")) {
                proteinIndex = i;
            }
            if (substring.contains("cholesterol")) {
                cholesterolIndex = i;
            }
            if (substring.contains("sodium")) {
                sodiumIndex = i;
            }
            if (substring.contains("sugar")) {
                sugarIndex = i;
            }
        }
    }
}