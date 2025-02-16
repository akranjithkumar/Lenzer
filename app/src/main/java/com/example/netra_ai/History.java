package com.example.netra_ai;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class History extends AppCompatActivity {

    ListView listView;

    Database dbHelper;
    ArrayList array_user,array_gemini;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        listView=findViewById(R.id.list_show_history);
        dbHelper = new Database(getApplicationContext());

        array_user = new ArrayList();
        array_gemini = new ArrayList();



        // Retrieve Data
        Cursor cursor = dbHelper.getAllUsers();

        // Display Data
        TextView textView = findViewById(R.id.txt_show_db);
        StringBuilder stringBuilder = new StringBuilder();

        if (cursor.moveToFirst()) {
            do {
                String answer = cursor.getString(cursor.getColumnIndexOrThrow("answer"));
                String question = cursor.getString(cursor.getColumnIndexOrThrow("userquery"));

                array_user.add(question);
                array_gemini.add(answer);

            } while (cursor.moveToNext());
        }
        textView.setText(stringBuilder);
        cursor.close();

        Chat_adapter adapter = new Chat_adapter(array_gemini,array_user,History.this);

        listView.setAdapter(adapter);


    }
}