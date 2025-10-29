package com.example.netra_ai;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class Chat extends AppCompatActivity {

    ArrayList array_recieve,array_sent;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        array_recieve = new ArrayList();
        array_sent = new ArrayList();
        listView =  findViewById(R.id.list_chat);

        array_sent.add("Hi");
        array_sent.add("how are you");
        array_recieve.add("Fine you can create one with empty brackets kjkjasfguif ffiguigig   iewgf ssddsvydyv");
        array_recieve.add("ok bye");
        array_sent.add("Hi");
        array_sent.add("how are you");
        array_recieve.add("Fine");
        array_recieve.add("ok bye");
        array_sent.add("Hi");
        array_sent.add("how are you");
        array_recieve.add("Fine");
        array_recieve.add("ok bye");
        array_sent.add("Hi");
        array_sent.add("how are you");
        array_recieve.add("Fine");
        array_recieve.add("ok bye");
        array_sent.add("Hi");
        array_sent.add("how are you");
        array_recieve.add("Fine");
        array_recieve.add("ok bye");

        Chat_adapter adapter = new Chat_adapter(array_recieve,array_sent,Chat.this);

        listView.setAdapter(adapter);



    }
}