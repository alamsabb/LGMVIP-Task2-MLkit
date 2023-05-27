package com.example.detector;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final int CAMERA_CODE=100;
    private final int G_CODE=200;
    FirebaseVisionImage res_img;
    FirebaseVisionFaceDetector detect;
    Button btn ,btn2;
    ImageView img;
    String result_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
//        Dialog da=new Dialog(this);
//        da.setContentView(R.layout.result_dialouge);
//        Button btn=da.findViewById(R.id.btnid);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                da.dismiss();
//            }
//        });
//        da.show();
        img=findViewById(R.id.img);
        btn=findViewById(R.id.mainid);
        btn2=findViewById(R.id.mainid2);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i,CAMERA_CODE);
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i2=new Intent(Intent.ACTION_PICK);
                i2.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i2,G_CODE);


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK&&requestCode==CAMERA_CODE){
            Bitmap imge=(Bitmap) data.getExtras().get("data");
            img.setImageBitmap(imge);
            facedect(imge);

        } else if (resultCode==RESULT_OK&&requestCode==G_CODE) {
            img.setImageURI(data.getData());
            facedect2(data.getData());

        }
    }

    private void facedect2(Uri data) {
        ProgressDialog progressDialog =new ProgressDialog(this);
        progressDialog.setTitle("please wait");
        progressDialog.show();
        FirebaseVisionFaceDetectorOptions options
                = new FirebaseVisionFaceDetectorOptions.Builder()
                .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                .build();
        try {
            res_img=FirebaseVisionImage.fromFilePath(this,data);
            detect= FirebaseVision.getInstance().getVisionFaceDetector(options);

        }catch (Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
        detect.detectInImage(res_img).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                result_text="";
                String smile_te;
                String Left;
                String Right;
                int i = 1;
                for (FirebaseVisionFace face :
                        firebaseVisionFaces) {
                    smile_te="";
                    Left="";
                    Right="";
                    float smile= face.getSmilingProbability()
                            * 100;
                    float lefteye=face.getLeftEyeOpenProbability()
                            * 100;
                    float righteye=face.getRightEyeOpenProbability()
                            * 100;

                    if(smile>50&&smile<70){
                        smile_te="Smile Properly";

                    } else if (smile>71&&smile<90) {
                        smile_te="Smile Openly";

                    }else if(smile>=90){
                        smile_te="Perfect Smile";
                    }else
                        smile_te="No Smile";


                    if (lefteye>70&&lefteye<80){
                        Left="Open properly";
                    } else if (lefteye>=80&&lefteye<90) {
                        Left="Open a bit more";
                    } else if (lefteye>=90) {
                        Left="Perfect";

                    }else
                        Left="Eye not Opened";
                    if (righteye>70&&righteye<80){
                        Right="Open properly";
                    } else if (righteye>=80&&righteye<90) {
                        Right="Open a bit more";
                    } else if (righteye>=90) {
                        Right="Perfect";

                    }else
                        Right="Eye not Opened";
                    result_text
                            = result_text
                            .concat("\nFACE NUMBER. "
                                    + i + ":\n ")
                            .concat(
                                    "\nSMILE: \n"
                                            +  String.format("%.2f",face.getSmilingProbability()
                                            * 100)
                                            + "%\n\t"
                                            + smile_te
                                            +"\n"
                            )
                            .concat(
                                    "\nLeft eye open:\n "
                                            +  String.format("%.2f",face.getLeftEyeOpenProbability()
                                            * 100)
                                            + "%\n"
                                            + Left
                                            + "\n")
                            .concat(
                                    "\nRight eye open\n"
                                            + String.format("%.2f",face.getRightEyeOpenProbability()
                                            * 100)
                                            + "%\n"
                                            + Right
                                            + "\n");
                    i++;
                }
                progressDialog.dismiss();
                if (firebaseVisionFaces.size() == 0) {
                    Toast
                            .makeText(MainActivity.this,
                                    "NO FACE DETECT",
                                    Toast.LENGTH_SHORT)
                            .show();
                }else {
                    Bundle bundle = new Bundle();
                    bundle.putString(
                            result_fragment.FINAL_TEXT,
                            result_text);
                    DialogFragment resultDialog
                            = new result_fragment();
                    resultDialog.setArguments(bundle);
                    resultDialog.setCancelable(false);
                    resultDialog.show(
                            getSupportFragmentManager(),
                            "Result");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void facedect(Bitmap imge) {
        FirebaseVisionFaceDetectorOptions options
                = new FirebaseVisionFaceDetectorOptions.Builder()
                .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                .build();
        try {
            res_img=FirebaseVisionImage.fromBitmap(imge);
            detect= FirebaseVision.getInstance().getVisionFaceDetector(options);

        }catch (Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
        detect.detectInImage(res_img).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                result_text="";
                String smile_te;
                String Left;
                String Right;
                int i = 1;
                for (FirebaseVisionFace face :
                        firebaseVisionFaces) {
                    smile_te="";
                    Left="";
                    Right="";
                        float smile= face.getSmilingProbability()
                                * 100;
                        float lefteye=face.getLeftEyeOpenProbability()
                                * 100;
                        float righteye=face.getRightEyeOpenProbability()
                                * 100;

                        if(smile>50&&smile<70){
                            smile_te="Smile Properly";

                        } else if (smile>71&&smile<90) {
                            smile_te="Smile Openly";

                        }else if(smile>=90){
                            smile_te="Perfect Smile";
                        }else
                            smile_te="No Smile";


                        if (lefteye>70&&lefteye<80){
                            Left="Open properly";
                        } else if (lefteye>=80&&lefteye<90) {
                            Left="Open a bit more";
                        } else if (lefteye>=90) {
                            Left="Perfect";

                        }else
                            Left="Eye not Opened";
                    if (righteye>70&&righteye<80){
                        Right="Open properly";
                    } else if (righteye>=80&&righteye<90) {
                        Right="Open a bit more";
                    } else if (righteye>=90) {
                        Right="Perfect";

                    }else
                        Right="Eye not Opened";
                    result_text
                            = result_text
                            .concat("\nFACE NUMBER. "
                                    + i + ":\n ")
                            .concat(
                                    "\nSMILE: \n"
                                            +  String.format("%.2f",face.getSmilingProbability()
                                            * 100)
                                            + "%\n\t"
                                            + smile_te
                                            +"\n"
                            )
                            .concat(
                                    "\nLeft eye open:\n "
                                            +  String.format("%.2f",face.getLeftEyeOpenProbability()
                                            * 100)
                                            + "%\n"
                                            + Left
                                            + "\n")
                            .concat(
                                    "\nRight eye open\n"
                                            + String.format("%.2f",face.getRightEyeOpenProbability()
                                            * 100)
                                            + "%\n"
                                            + Right
                                            + "\n");
                    i++;
                }
                if (firebaseVisionFaces.size() == 0) {
                    Toast
                            .makeText(MainActivity.this,
                                    "NO FACE DETECT",
                                    Toast.LENGTH_SHORT)
                            .show();
                }else {
                    Bundle bundle = new Bundle();
                    bundle.putString(
                            result_fragment.FINAL_TEXT,
                            result_text);
                    DialogFragment resultDialog
                            = new result_fragment();
                    resultDialog.setArguments(bundle);
                    resultDialog.setCancelable(false);
                    resultDialog.show(
                            getSupportFragmentManager(),
                            "Result");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}