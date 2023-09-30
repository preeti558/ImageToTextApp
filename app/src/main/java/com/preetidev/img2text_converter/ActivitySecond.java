package com.preetidev.img2text_converter;
import android.Manifest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.Image;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;


public class ActivitySecond extends AppCompatActivity {
private ImageView captureTV;
private TextView resultTV;
private Button snapbtn, detectBtn;
private Bitmap imageBitmap;

static final int REQUEST_IMAGE_CAPTURE=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        captureTV=findViewById(R.id.imageViewSecond);
        resultTV=findViewById(R.id.textViewSecond);
        snapbtn=findViewById(R.id.BtnSecond);
        detectBtn=findViewById(R.id.BtnDetectSecond);

        detectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DetectText();
            }
        });
        snapbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkPermission()){
                    CaptureImage();
                }else{
                    RequestPermission();
                }
            }
        });

    }

    private void RequestPermission() {
      int PERMISSION_CODE=200;
        ActivityCompat.requestPermissions(this,new String[]{
                Manifest.permission.CAMERA
        },PERMISSION_CODE);
    }

    private void CaptureImage() {

        Intent takePicture=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePicture.resolveActivity(getPackageManager())!=null){
            startActivityForResult(takePicture,REQUEST_IMAGE_CAPTURE);
        }

    }

    private void DetectText() {
        InputImage image=InputImage.fromBitmap(imageBitmap,0);
        TextRecognizer recognizer= TextRecognition.getClient(
                TextRecognizerOptions.DEFAULT_OPTIONS);

        Task<Text> result=recognizer.process(image).addOnSuccessListener(new OnSuccessListener<Text>() {
            @Override
            public void onSuccess(Text text) {
                StringBuilder result=new StringBuilder();
                for(Text.TextBlock block:text.getTextBlocks()){
                    String blockText=block.getText();
                    Point[] blockCornerPoint= block.getCornerPoints();
                    Rect blockFrame=block.getBoundingBox();

                    for(Text.Line line:block.getLines()){
                        String lineText= line.getText();
                        Point[] lineCornerPoint= line.getCornerPoints();
                        Rect lineRect=line.getBoundingBox();

                        for(Text.Element element:line.getElements()){
                            String elementText=element.getText();
                            result.append(elementText);
                        }
                        //Displaying the results
                        resultTV.setText(blockText);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),
                        "Failed to Detect Text from Image...",Toast.LENGTH_SHORT).show();
            }
        });


    }

    //permission method
    private boolean checkPermission(){
        int cameraPermission= ContextCompat.checkSelfPermission(getApplicationContext(),CAMERA_SERVICE);
        return cameraPermission== PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length>0){
            boolean cameraPermission=grantResults[0]==PackageManager.PERMISSION_GRANTED;
            if(cameraPermission){
                Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
                CaptureImage();
            }else{
                Toast.makeText(getApplicationContext(),"Permission Denied!",Toast.LENGTH_SHORT).show();
            }
        }



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQUEST_IMAGE_CAPTURE && resultCode==RESULT_OK){
            Bundle extras=data.getExtras();
            imageBitmap=(Bitmap) extras.get("data");
            captureTV.setImageBitmap((imageBitmap));
        }

    }
}