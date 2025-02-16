package com.example.netra_ai;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.impl.utils.futures.FutureCallback;
import androidx.camera.core.impl.utils.futures.Futures;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity{
    ProcessCameraProvider processCameraProvider;
    PreviewView previewView;
    LottieAnimationView lottieAnimationView;
    ImageCapture imageCapture;
    ImageButton imageButton;
    ImageView img_menu,img_speaker,img_bulb,img_enlarge;
    TextView textView;
    RecognitionListener listener;
    TextToSpeech textToSpeech;
    SpeechRecognizer recognizer;
    boolean bool_speaker = false;
    boolean bool_heart = false;


    String state = " and Assume yo are giving answer tot the child";

    LinearLayout lchild,lshop,ltourist,ltech,ltrans;
    Database sqlite;
    String str_get_user_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lchild = findViewById(R.id.l_child);
        lshop = findViewById(R.id.l_shoping);
        ltech = findViewById(R.id.l_tech);
        ltrans = findViewById(R.id.l_trans);
        ltourist = findViewById(R.id.l_torist);

        sqlite = new Database(getApplicationContext());



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }


        previewView = findViewById(R.id.previewView);
        imageButton = findViewById(R.id.imageButton2);

        img_speaker = findViewById(R.id.img_speaker);
        img_bulb = findViewById(R.id.img_bulb);


        lottieAnimationView = findViewById(R.id.main_loading);

        recognizer = SpeechRecognizer.createSpeechRecognizer(this);
        textView = findViewById(R.id.outputtext);

        img_menu =  findViewById(R.id.menu_icon);


        lchild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Switched to children mode", Toast.LENGTH_SHORT).show();
                state = " and Assume you are giving answer to a child which make the enjoyable the learning without any * symbol";
            }
        });

        ltourist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Switched to Tourist mode", Toast.LENGTH_SHORT).show();
                state = " and Assume you are giving answer to a tourist in a new place, so give the answer by analysing the tourist places data from the online without any * symbol.";
            }
        });

        ltrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Switched to Translate mode", Toast.LENGTH_SHORT).show();
                state = "and Translate the given data to their oly the specified language without any * symbol";
            }
        });

        ltech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Switched to Technician mode", Toast.LENGTH_SHORT).show();

                state = " and Assume you are giving answer to the industry technicians and the steps to be solved for the asked question in he short manner without any * symbol";
            }
        });

        lshop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Switched to Shopping mode", Toast.LENGTH_SHORT).show();
                state = " and Assume you are giving answer to the buyers ask about the product,give the description of the product with approx price in Rupees without any * symbol";
            }
        });

        img_bulb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_bulb.setImageDrawable(getResources().getDrawable(R.drawable.light_on));

                lottieAnimationView.playAnimation();
                lottieAnimationView.setVisibility(View.VISIBLE);
                imageButton.setVisibility(View.INVISIBLE);
                textView.setText("You - Describe what you see");
                capture("Tell what you see and Answer shortly without any * symbol "+state+" and give the answer in reading format");
            }
        });

        img_speaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bool_speaker == false){
                    img_speaker.setImageDrawable(getResources().getDrawable(R.drawable.mute));
                    bool_speaker = true;
                    Toast.makeText(MainActivity.this, "Unmuted", Toast.LENGTH_SHORT).show();

                    textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            if(status == TextToSpeech.SUCCESS){
                                textToSpeech.setLanguage(Locale.ENGLISH);
                            }
                        }
                    });


                }
                else {
                    textToSpeech.shutdown();
                    Toast.makeText(MainActivity.this, " Muted", Toast.LENGTH_SHORT).show();
                    bool_speaker = false;
                    img_speaker.setImageDrawable(getResources().getDrawable(R.drawable.colour_mute));
                }
            }
        });

        img_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(),v);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        String  id = item.getTitle().toString();
                        if(id.equals("Refresh")){
                            Toast.makeText(MainActivity.this, "Refreshed", Toast.LENGTH_SHORT).show();
                            recreate();
                        }
                        return true;
                    }
                });
                popupMenu.inflate(R.menu.main_menu);
                popupMenu.show();

            }
        });

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS){

                    textToSpeech.setLanguage(Locale.ENGLISH);
                    textToSpeech.speak("Welcome!,Tap to speak",TextToSpeech.QUEUE_FLUSH,null,"utteranceId");

                }
            }
        });


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                textView.setText("Listening...");
                listener = new RecognitionListener() {
                    @Override
                    public void onReadyForSpeech(Bundle params) {

                    }

                    @Override
                    public void onBeginningOfSpeech() {
                        textView.setText("Listening...");
                    }

                    @Override
                    public void onRmsChanged(float rmsdB) {

                    }

                    @Override
                    public void onBufferReceived(byte[] buffer) {

                    }

                    @Override
                    public void onEndOfSpeech() {

                    }

                    @Override
                    public void onError(int error) {

                        textView.setText("Try again...");
                    }

                    @Override
                    public void onResults(Bundle results) {
                        lottieAnimationView.playAnimation();
                        lottieAnimationView.setVisibility(View.VISIBLE);
                        imageButton.setVisibility(View.INVISIBLE);
                        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                        textView.setText("You - "+matches.get(0).toString());
                        capture(matches.get(0).toString() + "and Answer shortly without any * symbol" + state +" and give the answer in reading format");
                        str_get_user_data = matches.get(0).toString();
                    }

                    @Override
                    public void onPartialResults(Bundle partialResults) {

                    }

                    @Override
                    public void onEvent(int eventType, Bundle params) {

                    }
                };

                listnercall();

            }
        });

        ListenableFuture<ProcessCameraProvider> providerListenableFuture= ProcessCameraProvider.getInstance(this);
        providerListenableFuture.addListener(new Runnable() {
            @Override
            public void run() {

                try {
                    processCameraProvider = providerListenableFuture.get();

                    startcamera(processCameraProvider);

                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        }, ContextCompat.getMainExecutor(this));

    }

    private void listnercall() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5);

        recognizer.setRecognitionListener(listener);
        recognizer.startListening(intent);
    }

    private void capture(String input) {

        if(imageCapture == null) return;
        imageCapture.takePicture(ContextCompat.getMainExecutor(this), new ImageCapture.OnImageCapturedCallback() {
            @Override
            public void onCaptureStarted() {
                super.onCaptureStarted();
            }
            @SuppressLint("RestrictedApi")
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy image) {
                super.onCaptureSuccess(image);
                Bitmap bitmap = image.toBitmap();
                image.close();

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,1,stream);

                byte[] compressed = stream.toByteArray();

                Bitmap comp = BitmapFactory.decodeByteArray(compressed,0,compressed.length);

                GenerativeModel gm = new GenerativeModel(/* modelName */ "gemini-1.5-flash", "AIzaSyDndvgGOFyR6c8sHnZzBjGNw5wTc77P_FM");
                GenerativeModelFutures model = GenerativeModelFutures.from(gm);

                Content content = new Content.Builder()
                        .addText(input)
                        .addImage(comp)
                        .build();

                ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
                Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                    @Override
                    public void onSuccess(GenerateContentResponse result) {
                        String resultText = result.getText();
                        lottieAnimationView.pauseAnimation();
                        lottieAnimationView.setVisibility(View.INVISIBLE);
                        imageButton.setVisibility(View.VISIBLE);
                        textView.setText("Nethra - " + resultText);
                        textToSpeech.speak(resultText,TextToSpeech.QUEUE_FLUSH,null,"utteranceId");
                        img_bulb.setImageDrawable(getResources().getDrawable(R.drawable.light_on_yellow));

                        long res = sqlite.addrun(str_get_user_data,resultText);

                        if(res==-1){
                            Toast.makeText(getApplicationContext(), "Failed to add in DB", Toast.LENGTH_SHORT).show();
                        } else{
                            Toast.makeText(getApplicationContext(), "Added in DB", Toast.LENGTH_SHORT).show();
                        }


                    }

                    @Override
                    public void onFailure(Throwable t) {
                        t.printStackTrace();
                    }
                }, MainActivity.this.getMainExecutor());

            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                super.onError(exception);
            }

            @Override
            public void onCaptureProcessProgressed(int progress) {
                super.onCaptureProcessProgressed(progress);
            }

            @Override
            public void onPostviewBitmapAvailable(@NonNull Bitmap bitmap) {
                super.onPostviewBitmapAvailable(bitmap);
            }
        });
    }

    private void startcamera(ProcessCameraProvider processCameraProvider) {
        CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        imageCapture = new ImageCapture.Builder().build();
        try {
            processCameraProvider.unbindAll();
            processCameraProvider.bindToLifecycle(this,cameraSelector,preview,imageCapture);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}