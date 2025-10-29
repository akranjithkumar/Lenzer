package com.example.netra_ai;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;
    Context context;
    TaskDb databaseHelper;

    public TaskAdapter(List<Task> taskList,Context context) {
        this.context = context;
        this.taskList = taskList;
        this.databaseHelper = new TaskDb(context);
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.todolist, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.tvTaskTitle.setText(task.getTitle());
        holder.tvTaskDescription.setText(task.getDescription());
        holder.tvTaskDate.setText("Date: " + task.getDate());
        holder.tvTaskTime.setText("Time: " + task.getTime());
        holder.img_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog(taskList.get(position),position);
            }
        });
    }

    private void showDeleteConfirmationDialog(Task task, int position) {
        databaseHelper.deleteTask(task.getTime()); // Delete from DB
        taskList.remove(position); // Remove from list
        notifyItemRemoved(position); // Update RecyclerView
        
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTaskTitle, tvTaskDescription, tvTaskDate, tvTaskTime;
        ImageView img_del;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTaskTitle = itemView.findViewById(R.id.tvTaskTitle);
            tvTaskDescription = itemView.findViewById(R.id.tvTaskDescription);
            tvTaskDate = itemView.findViewById(R.id.tvTaskDate);
            tvTaskTime = itemView.findViewById(R.id.tvTaskTime);
            img_del = itemView.findViewById(R.id.img_del_todo);
        }
    }
}
