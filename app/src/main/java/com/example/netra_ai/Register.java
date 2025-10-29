package com.example.netra_ai;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends AppCompatActivity {
    Button button;
    EditText edit_name,edit_phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edit_name = findViewById(R.id.edit_name);
        edit_phone = findViewById(R.id.edit_phone_no);

        button = findViewById(R.id.btn_register);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String name = edit_name.getText().toString();
                String phone = edit_phone.getText().toString();

                if(name.isEmpty() || phone.isEmpty() || name.trim().isEmpty() || phone.trim().isEmpty())
                    {
                        Toast.makeText(Register.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                    }
                else {
                    TinyDB tinyDB = new TinyDB(getApplicationContext());
                    tinyDB.putString("name",name);
                    tinyDB.putString("phone",phone);
                    Intent intent = new Intent(Register.this, Care_taker_main.class);
                    startActivity(intent);
                }

            }
        });

    }
}