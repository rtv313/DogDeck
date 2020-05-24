package com.example.dogdeck;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;
import org.tensorflow.lite.support.model.Model;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;

public class DogAnalysis extends AppCompatActivity {

    ImageView dogPhoto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog_analysis);
        dogPhoto = findViewById(R.id.dogPhoto);
        setDogPhoto();

        ImageProcessor imageProcessor = new ImageProcessor.Builder()
                .add(new ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
                .build();

        Bitmap bitmap = ((BitmapDrawable)dogPhoto.getDrawable()).getBitmap();
        TensorImage tImage = new TensorImage(DataType.FLOAT32);
        tImage.load(bitmap);
        tImage = imageProcessor.process(tImage);

        //TensorBuffer probabilityBuffer = TensorBuffer.createFixedSize(new int[]{120}, DataType.UINT8);
        float[][] probabilityBuffer = new float[1][120];
        Interpreter tflite = null;

        try{
            MappedByteBuffer tfliteModel = FileUtil.loadMappedFile(this, "tf_model.tflite");
            tflite = new Interpreter(tfliteModel);
        } catch (IOException e){
            Log.e("tfliteSupport", "Error reading model", e);
        }

        // Running inference
        if(null != tflite) {
           // tflite.run(tImage.getBuffer(), probabilityBuffer.getBuffer());
            ByteBuffer buffer =  tImage.getBuffer();
            tflite.run(buffer, probabilityBuffer);
    }

        //float [] results = probabilityBuffer.getFloatArray();
        float [] inferredValue = probabilityBuffer[0];
        int x = 0;

    }

    private void setDogPhoto(){
        Bundle bundle = getIntent().getExtras();
        String uri = bundle.getString("imageUri");
        Bitmap imageCameraBitmap = BitmapFactory.decodeFile(uri);
        dogPhoto.setImageBitmap(imageCameraBitmap);
    }

}
