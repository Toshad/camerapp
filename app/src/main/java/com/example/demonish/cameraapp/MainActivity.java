package com.example.demonish.cameraapp;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    //private static final attr R = ;
    private static String logtag = "CameraApp8";
    private static int TAKE_PICTURE = 1;
    private static double gravity = 9.81;
    private static double cos_80 = 0.1736;
    private static double sin_80 = 0.984;
    private static double cos_70 = 0.342;
    private static double sin_70 = 0.94;
    private Uri imageUri;
    private SensorManager sensorM;
    private SensorEventListener SEL;
    private Sensor Sen;
    private static String str="Click Now.";
    private TextToSpeech txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorM = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sen = sensorM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SEL = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    //  Log.i("Tag", "gravity");
                    float para = Math.sqrt(event.values[0] * event.values[0] + event.values[1] * event.values[1]);
                    float perp = event.values[2];
                    if (Math.abs(para-(sin_80*gravity))<0.2 && Math.abs(perp-cos_80*gravity)<0.1) {
                        //sensorM.unregisterListener(this);
                        txt = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                            @Override
                            public void onInit(int status) {
                                txt.speak(str, TextToSpeech.QUEUE_FLUSH, null, null);
                            }
                        });
                        takePhoto("80");        // Call your method here
                    }
                    else if (Math.abs(para-(sin_70*gravity))<0.2 && Math.abs(perp-cos_70*gravity)<0.1) {
                        //sensorM.unregisterListener(this);
                        txt = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                            @Override
                            public void onInit(int status) {
                                txt.speak(str, TextToSpeech.QUEUE_FLUSH, null, null);
                            }
                        });
                        takePhoto("70");        // Call your method here
                    }
                    else (Math.abs(para-(gravity))<0.2 && Math.abs(perp)<0.1) {
                        //sensorM.unregisterListener(this);
                        txt = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                            @Override
                            public void onInit(int status) {
                                txt.speak(str, TextToSpeech.QUEUE_FLUSH, null, null);
                            }
                        });
                        takePhoto("90");        // Call your method here
                    }
                }
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
        sensorM.registerListener(SEL,Sen, SensorManager.SENSOR_DELAY_NORMAL);

    }

    private View.OnClickListener cameraListener = new View.OnClickListener() {
        public void onClick(View v){
            takePhoto();
        }

    };


    private void takePhoto(String a) {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        File photo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "picture"+a+".jpg");
        imageUri = Uri.fromFile(photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == Activity.RESULT_OK) {
            Uri selectedImage = imageUri;
            getContentResolver().notifyChange(selectedImage, null);
            ImageView imageView = (ImageView) findViewById(R.id.image_camera);
            ContentResolver cr = getContentResolver();
            Bitmap bitmap;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(cr, selectedImage);
                imageView.setImageBitmap(bitmap);
                Toast.makeText(MainActivity.this, selectedImage.toString(), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Log.e(logtag, e.toString());
            }
        }
    }

}