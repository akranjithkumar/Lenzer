package com.example.netra_ai;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.impl.utils.futures.FutureCallback;
import androidx.camera.core.impl.utils.futures.Futures;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class CareTaker extends AppCompatActivity {

    RecognitionListener listener;
    SpeechRecognizer recognizer;
    ImageButton img;
    TextView text_res,txt_care_ans,txt_lisening;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference ref;
    LottieAnimationView lot;
    String mode = "assistant";

    GenerativeModel gm;
    String selectedLanguageCode;
    GenerativeModelFutures model;
    private TextToSpeech textToSpeech;
    ImageView img_todo,img_lang,img_cancel;

    Intent intent;

    HashMap languageMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_care_taker);

        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference();

        lot = findViewById(R.id.listening_anim);
        txt_care_ans = findViewById(R.id.txt_care_ans);
        txt_lisening = findViewById(R.id.txt_care_listening);
        img_lang = findViewById(R.id.img_care_lang);

        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, selectedLanguageCode);


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



        img_todo = findViewById(R.id.img_todo);

        img_lang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLanguagePopup(v);
            }
        });

        Handler handler = new Handler(Looper.getMainLooper());

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // Get current time
                Calendar calendar = Calendar.getInstance();
                int selectedHour = calendar.get(Calendar.HOUR_OF_DAY);
                int selectedMinute = calendar.get(Calendar.MINUTE);

                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm", Locale.getDefault());
                 String s = sdf.format(calendar.getTime());
                 TaskDb db = new TaskDb(getApplicationContext());
                 Cursor cursor = db.getTaskAtCurrentTime(s);
                if (cursor.moveToFirst()) {
                    String title = cursor.getString(0);
                    String description  = cursor.getString(1);
                    String tas = "Task to do now!! \nTitle : " + title +"\nDescription : "+description;
                    txt_care_ans.setText(tas);
                    textToSpeech.speak(tas  ,TextToSpeech.QUEUE_FLUSH,null,"utteranceId");
                }
                handler.postDelayed(this, 30000); // 60,000 ms = 1 minute
            }
        };

// Start the loop
        handler.post(runnable);

        ref.child("threat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String updatedValue = snapshot.getValue().toString();
                    if(updatedValue.contains("1")){
                        Intent intent = new Intent(getApplicationContext(),Add.class);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        img_todo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Todo.class);
                startActivity(intent);
            }
        });

        gm = new GenerativeModel(/* modelName */ "gemini-2.0-flash", "AIzaSyDxJjh4exlYMctl-JUhXKCWmfNl64wyqUE");
        model = GenerativeModelFutures.from(gm);

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS){
                    textToSpeech.setLanguage(Locale.ENGLISH);
                }
            }
        });


        recognizer = SpeechRecognizer.createSpeechRecognizer(this);
        img = findViewById(R.id.care_mic);
        text_res = findViewById(R.id.text_get_res);



        ref.child("ans").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Retrieve the updated data
                    String updatedValue = snapshot.getValue().toString();
                    txt_care_ans.setText(updatedValue);
                    txt_lisening.setText("Speak now");
                    textToSpeech.speak(updatedValue,TextToSpeech.QUEUE_FLUSH,null,"utteranceId");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
            listener = new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

                txt_lisening.setText("Listening...");
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

                txt_lisening.setText("Listening...");
                callrecognize();

            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onResults(Bundle results) {

                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                String main = matches.get(0).toString();
                text_res.setText(main);

                ref.child("hi").setValue("Assume you are explaining to the blind people. If the text is in a newspaper read out the entire article else read out only the 2 lines. And the question is - "+matches.get(0).toString());
                txt_lisening.setText("Speak now");
                recognizer.stopListening();
                recognizer.cancel();
                recognizer.destroy();
                lot.setVisibility(View.INVISIBLE);
                img.setVisibility(View.VISIBLE);

            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                callrecognize();

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        };


        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, selectedLanguageCode);

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recognizer.setRecognitionListener(listener);
                recognizer.startListening(intent);
                lot.playAnimation();
                lot.setVisibility(View.VISIBLE);
                img.setVisibility(View.INVISIBLE);
            }
        });


    }

    private void callrecognize() {

                recognizer.setRecognitionListener(listener);
                recognizer.startListening(intent);

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
}