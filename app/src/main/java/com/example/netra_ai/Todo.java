package com.example.netra_ai;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class Todo extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    FloatingActionButton btn_add;
    private TaskDb databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_todo);

        recyclerView = findViewById(R.id.recyclerViewTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        btn_add = findViewById(R.id.fabAddTask);
        databaseHelper = new TaskDb(this);




        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),AddTask.class);
                startActivity(intent);
            }
        });

        taskList = new ArrayList<>();


        // Load tasks from database
        loadTasksFromDatabase();



        taskAdapter = new TaskAdapter(taskList,getApplicationContext());
        recyclerView.setAdapter(taskAdapter);


    }

    private void loadTasksFromDatabase() {
        taskList.clear();
        taskList.addAll(databaseHelper.getAllTasks());

        if (taskAdapter != null) {
            taskAdapter.notifyDataSetChanged(); // Refresh RecyclerView
        } else {
            //Toast.makeText(this, "No tasks found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasksFromDatabase(); // Refresh list when returning from AddTaskActivity

    }
}