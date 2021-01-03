package com.example.todolist;

import android.content.Context;
import android.graphics.Paint;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.api.services.tasks.model.Task;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GoogleTaskAdapter extends RecyclerView.Adapter<GoogleTaskAdapter.TaskHolder> {
    private List<Task> tasks = new ArrayList<>();
    private  OnItemClickListener listener;
    Context myContext;

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        myContext = recyclerView.getContext();
    }

    @NonNull
    @Override
    public TaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item, parent, false);
        return new TaskHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskHolder holder, int position) {
        if(tasks != null){
            Task currentTask = tasks.get(position);
            holder.textViewTitle.setText(currentTask.getTitle());
            if(currentTask.getStatus().equals("completed"))
                holder.textViewTitle.setPaintFlags(holder.textViewTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            else
                holder.textViewTitle.setPaintFlags(holder.textViewTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            if(currentTask.getDue()!=null){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
                Date taskDate = null;
                try {
                    taskDate = sdf.parse(currentTask.getDue().toStringRfc3339());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                PrettyTime prettyTime = new PrettyTime();
                holder.textViewDate.setText(prettyTime.format(taskDate));
            }else if(currentTask.getNotes()!=null){
                String descriptionShort = "";
                if(currentTask.getNotes().length() > 15){
                    descriptionShort = currentTask.getNotes().substring(0,14);
                }else {
                    descriptionShort = currentTask.getNotes();
                }
                holder.textViewDate.setText(descriptionShort);
            }else{
                holder.textViewDate.setText("");
            }
        }
    }

    @Override
    public int getItemCount() {
        if(tasks == null){
            return 0;
        }else {
            return tasks.size();
        }
    }

    public void setTasks(List<Task> tasks){
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    public void removeAt(int position){
        if(position<tasks.size() && position >= 0){
            tasks.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Task getTaskAt(int position){
        return tasks.get(position);
    }

    class TaskHolder extends RecyclerView.ViewHolder{
        private TextView textViewTitle;
        private TextView textViewDate;

        public TaskHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewDate = itemView.findViewById(R.id.text_view_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(listener != null && position != RecyclerView.NO_POSITION){
                        listener.onItemClick(tasks.get(position));
                    }
                }
            });
        }
    }

    public interface  OnItemClickListener{
        void onItemClick(Task task);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
}