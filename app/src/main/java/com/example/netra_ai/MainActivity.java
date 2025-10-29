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
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.MenuInflater;
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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity{
    ProcessCameraProvider processCameraProvider;
    PreviewView previewView;
    LottieAnimationView lottieAnimationView;
    ImageCapture imageCapture;
    ImageButton imageButton;
    ImageView img_menu,img_speaker,img_bulb,img_lang;
    TextView textView;
    RecognitionListener listener;
    TextToSpeech textToSpeech;
    SpeechRecognizer recognizer;
    boolean bool_speaker = false;
    boolean bool_heart = false;
    GenerativeModelFutures model;
    GenerativeModel gm;
    HashMap languageMap;
    String selectedLanguageCode;
    String fileContent;


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
        img_lang = findViewById(R.id.img_lang);

        gm = new GenerativeModel(/* modelName */ "gemini-2.0-flash", "AIzaSyDdEODqW527Zzv81bsAvCIb0QptJyDb240");
        model = GenerativeModelFutures.from(gm);

        sqlite = new Database(getApplicationContext());

        img_lang.setOnClickListener(view -> showLanguagePopup(view));
        fileContent = readFileFromAssets(this, R.raw.instructions);
        languageMap = new HashMap<>();

        // Indian Regional Languages
        languageMap.put("Assamese (অসমীয়া)", "as-IN");
        languageMap.put("Bengali (বাংলা)", "bn-IN");
        languageMap.put("Gujarati (ગુજરાતી)", "gu-IN");
        languageMap.put("Hindi (हिन्दी)", "hi-IN");
        languageMap.put("Kannada (ಕನ್ನಡ)", "kn-IN");
        languageMap.put("Malayalam (മലയാളം)", "ml-IN");
        languageMap.put("Marathi (मराठी)", "mr-IN");
        languageMap.put("Nepali (नेपाली)", "ne-NP");
        languageMap.put("Odia (ଓଡ଼ିଆ)", "or-IN");
        languageMap.put("Punjabi (ਪੰਜਾਬੀ)", "pa-IN");
        languageMap.put("Tamil (தமிழ்)", "ta-IN");
        languageMap.put("Telugu (తెలుగు)", "te-IN");
        languageMap.put("Urdu (اردو)", "ur-IN");

        // English Variants
        languageMap.put("English (US)", "en-US");
        languageMap.put("English (UK)", "en-GB");
        languageMap.put("English (India)", "en-IN");

        // Other Popular Languages
        languageMap.put("French (Français)", "fr-FR");
        languageMap.put("German (Deutsch)", "de-DE");
        languageMap.put("Spanish (Español - Spain)", "es-ES");
        languageMap.put("Italian (Italiano)", "it-IT");
        languageMap.put("Portuguese (Português)", "pt-BR");
        languageMap.put("Chinese (中文)", "zh-CN");
        languageMap.put("Japanese (日本語)", "ja-JP");
        languageMap.put("Korean (한국어)", "ko-KR");
        languageMap.put("Arabic (العربية)", "ar-SA");





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
                state = " and Assume you are giving answer to a tourist in a new place, so give the answer by analysing the tourist places data from the online which is trained without any * symbol.";
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
                Toast.makeText(MainActivity.this, "Switched to Normal mode", Toast.LENGTH_SHORT).show();
                bool_speaker = true;

                state = " Give the formated accurate answer in reading format without any * symbol and allwed for long answer if required.";
            }
        });

        lshop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Switched to Shopping mode", Toast.LENGTH_SHORT).show();
                state = " and Assume you are giving answer to the buyers ask about the product,give the description of the product with approx price in Rupees and give the product usea and other details also without any * symbol";
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
                    img_speaker.setImageDrawable(getResources().getDrawable(R.drawable.speaker));
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
                    textToSpeech.stop();
                    textToSpeech.shutdown();
                    Toast.makeText(MainActivity.this, " Muted", Toast.LENGTH_SHORT).show();
                    bool_speaker = false;
                    img_speaker.setImageDrawable(getResources().getDrawable(R.drawable.mute));
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
                            textToSpeech.stop();
                            textToSpeech.shutdown();
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

    private void showLanguagePopup(View view) {

        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        for (Object language : languageMap.keySet()) {
            popup.getMenu().add(language.toString());
        }
        popup.setOnMenuItemClickListener(item -> {
            selectedLanguageCode = languageMap.get(item.getTitle().toString()).toString();
            Toast.makeText(this, selectedLanguageCode, Toast.LENGTH_SHORT).show();
            return true;
        });
        popup.show();

    }

    private void listnercall() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, selectedLanguageCode);

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



                Content content = new Content.Builder()
                        .addText(fileContent + input + ",Give the answer only in the questioned language")
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

                        //long res = sqlite.addrun(str_get_user_data,resultText);


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

    public static String readFileFromAssets(Context context, int resourceId) {
        StringBuilder content = new StringBuilder();
        try {
            InputStream inputStream = context.getResources().openRawResource(resourceId);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            reader.close();
            inputStream.close();
        } catch (IOException e) {
            return "Error reading file: " + e.getMessage();
        }
        return content.toString();
    }
}