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

    private int caloriesIndex = 0;
    private int fatIndex = 0;
    private int carbohydrateIndex = 0;
    private int proteinIndex = 0;
    private int cholesterolIndex = 0;
    private int sodiumIndex = 0;
    private int sugarIndex = 0;

    private boolean caloriesIndexUpdated = false;
    private boolean fatIndexUpdated = false;
    private boolean carbohydrateIndexUpdated = false;
    private boolean proteinIndexUpdated = false;
    private boolean cholesterolIndexUpdated = false;
    private boolean sodiumIndexUpdated = false;
    private boolean sugarIndexUpdated = false;


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
                                    updateTable();
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

    private void updateTable() {
        recognizedText = recognizedText.toLowerCase();
        int length = recognizedText.length();
        String substring;
        for (int i = 1; i < length; i++) {
            substring = recognizedText.substring(0, i);
            if (substring.contains("/ calories")) {
                if (!caloriesIndexUpdated) {
                    caloriesIndex = i;
                    caloriesIndexUpdated = true;
                }
            }
            if (substring.contains("lipides")) {
                if (!fatIndexUpdated) {
                    fatIndex = i;
                    fatIndexUpdated = true;
                }
            }
            if (substring.contains("glucides")) {
                if (!carbohydrateIndexUpdated) {
                    carbohydrateIndex = i;
                    carbohydrateIndexUpdated = true;
                }
            }
            if (substring.contains("protéines")) {
                if (!proteinIndexUpdated) {
                    proteinIndex = i;
                    proteinIndexUpdated = true;
                }
            }
            if (substring.contains("cholestérol")) {
                if (!cholesterolIndexUpdated) {
                    cholesterolIndex = i;
                    cholesterolIndexUpdated = true;
                }
            }
            if (substring.contains("/ sodium")) {
                if (!sodiumIndexUpdated) {
                    sodiumIndex = i;
                    sodiumIndexUpdated = true;
                }
            }
            if (substring.contains("sucres")) {
                if (!sugarIndexUpdated) {
                    sugarIndex = i;
                    sugarIndexUpdated = true;
                }
            }
        }

        System.out.println(recognizedText);

        System.out.println("___________");
        System.out.println(recognizedText.substring(caloriesIndex, caloriesIndex + 5));

        if (caloriesIndex != 0) {
            if (caloriesIndex + 5 <= length) {
                String localSubstring = recognizedText.substring(caloriesIndex, caloriesIndex + 5);
                if (localSubstring.matches("[^0-9]*[0-9]+[^0-9]*")) {
                    caloriesValue.setText(localSubstring.replaceAll("[^0-9]", ""));
                }
            } else {
                String localSubstring = recognizedText.substring(caloriesIndex, length);
                if (localSubstring.matches("[^0-9]*[0-9]+[^0-9]*")) {
                    caloriesValue.setText(localSubstring.replaceAll("[^0-9]", ""));
                }
            }
        }
//        if (fatIndex != 0) {
//            if (fatIndex + 5 <= length) {
//                fatValue.setText(Integer.parseInt(recognizedText.substring(fatIndex, fatIndex + 5).replaceAll("[^0-9]+", "")));
//            } else {
//                fatValue.setText(Integer.parseInt(recognizedText.substring(fatIndex, length).replaceAll("[^0-9]+", "")));
//            }
//        }
//        if (carbohydrateIndex != 0) {
//            if (carbohydrateIndex + 5 <= length) {
//                carbohydrateValue.setText(Integer.parseInt(recognizedText.substring(carbohydrateIndex, carbohydrateIndex + 5).replaceAll("[^0-9]+", "")));
//            } else {
//                carbohydrateValue.setText(Integer.parseInt(recognizedText.substring(carbohydrateIndex, length).replaceAll("[^0-9]+", "")));
//            }
//        }
//        if (proteinIndex + 5 <= length) {
//            proteinValue.setText(Integer.parseInt(recognizedText.substring(proteinIndex, proteinIndex + 5).replaceAll("[^0-9]", "")));
//        } else {
//            proteinValue.setText(Integer.parseInt(recognizedText.substring(proteinIndex, length).replaceAll("[^0-9]", "")));
//        }
//        if (cholesterolIndex + 5 <= length) {
//            cholesterolValue.setText(Integer.parseInt(recognizedText.substring(cholesterolIndex, cholesterolIndex + 5).replaceAll("[^0-9]", "")));
//        } else {
//            cholesterolValue.setText(Integer.parseInt(recognizedText.substring(cholesterolIndex, length).replaceAll("[^0-9]", "")));
//        }
//        if (sodiumIndex + 5 <= length) {
//            sodiumValue.setText(Integer.parseInt(recognizedText.substring(sodiumIndex, sodiumIndex + 5).replaceAll("[^0-9]", "")));
//        } else {
//            sodiumValue.setText(Integer.parseInt(recognizedText.substring(sodiumIndex, length).replaceAll("[^0-9]", "")));
//        }
//        if (sugarIndex + 5 <= length) {
//            sugarValue.setText(Integer.parseInt(recognizedText.substring(sugarIndex, sugarIndex + 5).replaceAll("[^0-9]", "")));
//        } else {
//            sugarValue.setText(Integer.parseInt(recognizedText.substring(sugarIndex, length).replaceAll("[^0-9]", "")));
//        }
    }
}

