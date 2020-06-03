package com.example.the_calories_tracking_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
    private String recognizedTextSubstring;
    private int recognizedTextStringLength;

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

    private TextView loadingStatus;
    private EditText caloriesValue;
    private EditText fatValue;
    private EditText carbohydrateValue;
    private EditText proteinValue;
    private EditText cholesterolValue;
    private EditText sodiumValue;
    private EditText sugarValue;

    private TextView servings;
    private EditText numberServings;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program);

        loadingStatus = findViewById(R.id.loadingStatus);
        caloriesValue = findViewById(R.id.caloriesText);
        fatValue = findViewById(R.id.fatText);
        carbohydrateValue = findViewById(R.id.carbohydrateText);
        proteinValue = findViewById(R.id.proteinText);
        cholesterolValue = findViewById(R.id.cholesterolText);
        sodiumValue = findViewById(R.id.sodiumText);
        sugarValue = findViewById(R.id.sugarText);

        servings = findViewById(R.id.servings);
        numberServings = findViewById(R.id.servingText);
        numberServings.setText("1");

        String path = getIntent().getStringExtra("imagePath");

        Bitmap bitmap = null;
        try {
            bitmap = rotateBitmap(BitmapFactory.decodeFile(path), 90);
        } catch (Exception e) {
            System.out.println("no file found");
            String manualEntry = "MANUAL ENTRY";
            loadingStatus.setText(manualEntry);
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
//                                    TextView resultTextView = findViewById(R.id.calories);
                                    recognizedText = texts.getText();
                                    updateTable();
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
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
        recognizedTextStringLength = recognizedText.length();
        for (int i = 1; i < recognizedTextStringLength; i++) {
            recognizedTextSubstring = recognizedText.substring(0, i);
            if (recognizedTextSubstring.contains("/ calories") || bruteForceCheckKeyWord("calories")) {
                if (!caloriesIndexUpdated) {
                    caloriesIndex = i - 1;
                    caloriesIndexUpdated = true;
                }
            }
            if (recognizedTextSubstring.contains("lipides") || bruteForceCheckKeyWord("fat")) {
                if (!fatIndexUpdated) {
                    fatIndex = i - 1;
                    fatIndexUpdated = true;
                }
            }
            if (recognizedTextSubstring.contains("glucides") || bruteForceCheckKeyWord("carbohydrate")) {
                if (!carbohydrateIndexUpdated) {
                    carbohydrateIndex = i - 1;
                    carbohydrateIndexUpdated = true;
                }
            }
            if (recognizedTextSubstring.contains("protéines") || bruteForceCheckKeyWord("protein")) {
                if (!proteinIndexUpdated) {
                    proteinIndex = i - 1;
                    proteinIndexUpdated = true;
                }
            }
            if (recognizedTextSubstring.contains("cholestérol") || bruteForceCheckKeyWord("cholesterol")) {
                if (!cholesterolIndexUpdated) {
                    cholesterolIndex = i - 1;
                    cholesterolIndexUpdated = true;
                }
            }
            if (recognizedTextSubstring.contains("/ sodium") || bruteForceCheckKeyWord("sodium")) {
                if (!sodiumIndexUpdated) {
                    sodiumIndex = i - 1;
                    sodiumIndexUpdated = true;
                }
            }
            if (recognizedTextSubstring.contains("sucres") || bruteForceCheckKeyWord("sugars")) {
                if (!sugarIndexUpdated) {
                    sugarIndex = i - 1;
                    sugarIndexUpdated = true;
                }
            }
        }

        updateCaloriesValue();
        updateFatValue();
        updateCarbohydrateValue();
        updateProteinValue();
        updateSugarValue();
        updateSodiumValue();
        updateCholesterolValue();

        String loadedData = "LOADED DATA";
        loadingStatus.setText(loadedData);
    }

    private void updateCaloriesValue() {
        if (caloriesIndex != 0) {
            if (caloriesIndex + 5 <= recognizedTextStringLength) {
                String localSubstring = recognizedText.substring(caloriesIndex, caloriesIndex + 5);
                if (localSubstring.matches("[^0-9]*[0-9]+([^0-9]*|[.][0-9]+[^0-9]*)")) {
                    caloriesValue.setText(localSubstring.replaceAll("[^.0-9]", ""));
                } else {
                    caloriesValue.setHint("unrecognized");
                }
            } else {
                String localSubstring = recognizedText.substring(caloriesIndex, recognizedTextStringLength);
                if (localSubstring.matches("[^0-9]*[0-9]+([^0-9]*|[.][0-9]+[^0-9]*)")) {
                    caloriesValue.setText(localSubstring.replaceAll("[^.0-9]", ""));
                } else {
                    caloriesValue.setHint("unrecognized");
                }
            }
        } else {
            caloriesValue.setHint("unrecognized");
        }
    }

    private void updateFatValue() {
        if (fatIndex != 0) {
            if (fatIndex + 5 <= recognizedTextStringLength) {
                String localSubstring = recognizedText.substring(fatIndex, fatIndex + 5);
                if (localSubstring.matches("[^0-9]*[0-9]+([^0-9]*|[.][0-9]+[^0-9]*)")) {
                    fatValue.setText(localSubstring.replaceAll("[^.0-9]", ""));
                } else {
                    fatValue.setHint("unrecognized");
                }
            } else {
                String localSubstring = recognizedText.substring(fatIndex, recognizedTextStringLength);
                if (localSubstring.matches("[^0-9]*[0-9]+([^0-9]*|[.][0-9]+[^0-9]*)")) {
                    fatValue.setText(localSubstring.replaceAll("[^.0-9]", ""));
                } else {
                    fatValue.setHint("unrecognized");
                }
            }
        } else {
            fatValue.setHint("unrecognized");
        }
    }

    private void updateCarbohydrateValue() {
        if (carbohydrateIndex != 0) {
            if (carbohydrateIndex + 5 <= recognizedTextStringLength) {
                String localSubstring = recognizedText.substring(carbohydrateIndex, carbohydrateIndex + 5);
                if (localSubstring.matches("[^0-9]*[0-9]+([^0-9]*|[.][0-9]+[^0-9]*)")) {
                    carbohydrateValue.setText(localSubstring.replaceAll("[^.0-9]", ""));
                } else {
                    carbohydrateValue.setHint("unrecognized");
                }
            } else {
                String localSubstring = recognizedText.substring(carbohydrateIndex, recognizedTextStringLength);
                if (localSubstring.matches("[^0-9]*[0-9]+([^0-9]*|[.][0-9]+[^0-9]*)")) {
                    carbohydrateValue.setText(localSubstring.replaceAll("[^.0-9]", ""));
                } else {
                    carbohydrateValue.setHint("unrecognized");
                }
            }
        } else {
            carbohydrateValue.setHint("unrecognized");
        }
    }

    private void updateProteinValue() {
        if (proteinIndex != 0) {
            if (proteinIndex + 5 <= recognizedTextStringLength) {
                String localSubstring = recognizedText.substring(proteinIndex, proteinIndex + 5);
                if (localSubstring.matches("[^0-9]*[0-9]+([^0-9]*|[.][0-9]+[^0-9]*)")) {
                    proteinValue.setText(localSubstring.replaceAll("[^.0-9]", ""));
                } else {
                    proteinValue.setHint("unrecognized");
                }
            } else {
                String localSubstring = recognizedText.substring(proteinIndex, recognizedTextStringLength);
                if (localSubstring.matches("[^0-9]*[0-9]+([^0-9]*|[.][0-9]+[^0-9]*)")) {
                    proteinValue.setText(localSubstring.replaceAll("[^.0-9]", ""));
                } else {
                    proteinValue.setHint("unrecognized");
                }
            }
        } else {
            proteinValue.setHint("unrecognized");
        }
    }

    private void updateCholesterolValue() {
        if (cholesterolIndex != 0) {
            if (cholesterolIndex + 5 <= recognizedTextStringLength) {
                String localSubstring = recognizedText.substring(cholesterolIndex, cholesterolIndex + 5);
                if (localSubstring.matches("[^0-9]*[0-9]+([^0-9]*|[.][0-9]+[^0-9]*)")) {
                    cholesterolValue.setText(localSubstring.replaceAll("[^.0-9]", ""));
                } else {
                    cholesterolValue.setHint("unrecognized");
                }
            } else {
                String localSubstring = recognizedText.substring(cholesterolIndex, recognizedTextStringLength);
                if (localSubstring.matches("[^0-9]*[0-9]+([^0-9]*|[.][0-9]+[^0-9]*)")) {
                    cholesterolValue.setText(localSubstring.replaceAll("[^.0-9]", ""));
                } else {
                    cholesterolValue.setHint("unrecognized");
                }
            }
        } else {
            cholesterolValue.setHint("unrecognized");
        }
    }

    private void updateSodiumValue() {
        if (sodiumIndex != 0) {
            if (sodiumIndex + 5 <= recognizedTextStringLength) {
                String localSubstring = recognizedText.substring(sodiumIndex, sodiumIndex + 5);
                if (localSubstring.matches("[^0-9]*[0-9]+([^0-9]*|[.][0-9]+[^0-9]*)")) {
                    sodiumValue.setText(localSubstring.replaceAll("[^.0-9]", ""));
                } else {
                    sodiumValue.setHint("unrecognized");
                }
            } else {
                String localSubstring = recognizedText.substring(sodiumIndex, recognizedTextStringLength);
                if (localSubstring.matches("[^0-9]*[0-9]+([^0-9]*|[.][0-9]+[^0-9]*)")) {
                    sodiumValue.setText(localSubstring.replaceAll("[^.0-9]", ""));
                } else {
                    sodiumValue.setHint("unrecognized");
                }
            }
        } else {
            sodiumValue.setHint("unrecognized");
        }
    }

    private void updateSugarValue() {
        if (sugarIndex != 0) {
            if (sugarIndex + 5 <= recognizedTextStringLength) {
                String localSubstring = recognizedText.substring(sugarIndex, sugarIndex + 5);
                if (localSubstring.matches("[^0-9]*[0-9]+([^0-9]*|[.][0-9]+[^0-9]*)")) {
                    sugarValue.setText(localSubstring.replaceAll("[^.0-9]", ""));
                } else {
                    sugarValue.setHint("unrecognized");
                }
            } else {
                String localSubstring = recognizedText.substring(sugarIndex, recognizedTextStringLength);
                if (localSubstring.matches("[^0-9]*[0-9]+([^0-9]*|[.][0-9]+[^0-9]*)")) {
                    sugarValue.setText(localSubstring.replaceAll("[^.0-9]", ""));
                } else {
                    sugarValue.setHint("unrecognized");
                }
            }
        } else {
            sugarValue.setHint("unrecognized");
        }
    }

    private boolean bruteForceCheckKeyWord(String nutrient) {
        return (recognizedTextSubstring.contains(nutrient + " 0") ||
                recognizedTextSubstring.contains(nutrient + " 1") ||
                recognizedTextSubstring.contains(nutrient + " 2") ||
                recognizedTextSubstring.contains(nutrient + " 3") ||
                recognizedTextSubstring.contains(nutrient + " 4") ||
                recognizedTextSubstring.contains(nutrient + " 5") ||
                recognizedTextSubstring.contains(nutrient + " 6") ||
                recognizedTextSubstring.contains(nutrient + " 7") ||
                recognizedTextSubstring.contains(nutrient + " 8") ||
                recognizedTextSubstring.contains(nutrient + " 9"));
    }

}

