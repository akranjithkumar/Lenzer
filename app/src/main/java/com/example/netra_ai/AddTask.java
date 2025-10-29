package com.example.netra_ai;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AddTask extends AppCompatActivity {

    private EditText editTaskTitle, editTaskDescription;
    private TextView tvSelectedDate, tvSelectedTime;
    private CalendarView calendarView;
    private Button btnAddTask;
    private int selectedHour, selectedMinute;
    private String selectedDate = "", selectedTime = "";

    private TaskDb databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        // Initialize Views
        editTaskTitle = findViewById(R.id.editTaskTitle);
        editTaskDescription = findViewById(R.id.editTaskDescription);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvSelectedTime = findViewById(R.id.tvSelectedTime);
        calendarView = findViewById(R.id.calendarView);
        btnAddTask = findViewById(R.id.btnAddTask);
        TimePicker timePicker = findViewById(R.id.timePicker);

        // Initialize Database Helper
        databaseHelper = new TaskDb(this);


        // Calendar View Listener to Get Selected Date
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = (month + 1) + "/" + dayOfMonth + "/" + year;
            tvSelectedDate.setText("Selected Date: " + selectedDate);
        });

        // Time Picker Listener to Get Selected Time
        timePicker.setOnTimeChangedListener((view, hourOfDay, minute) -> {
            // Convert 24-hour time to 12-hour format
            int hour = (hourOfDay % 12 == 0) ? 12 : hourOfDay % 12;
            selectedTime = String.format("%02d:%02d", hour, minute);
        });
        // Add Task Button Click Listener
        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTaskTitle.getText().toString().trim();
                String description = editTaskDescription.getText().toString().trim();

                if (title.isEmpty() || selectedDate.isEmpty() || selectedTime.isEmpty()) {
                    Toast.makeText(AddTask.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Save Task to Database
                databaseHelper.addTask(new Task(title, description, selectedDate, selectedTime));

                // Show Confirmation & Close Activity
                Toast.makeText(AddTask.this, "Task Added!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
