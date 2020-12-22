package com.example.todolist;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.todolist.noteDatabase.Note;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteHolder> {
    private List<Note> notes = new ArrayList<>();
    private  OnItemClickListener listener;


    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_item, parent, false);
        return new NoteHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {
        Note currentNote = notes.get(position);
        holder.textViewTitle.setText(currentNote.getTitle());
        if(currentNote.getContent().length() > 20){
            String text = currentNote.getContent().substring(0,20) + "...";
            holder.textViewSubtitle.setText(text);
        }else {
            holder.textViewSubtitle.setText(currentNote.getContent());
        }

//        holder.textViewDate.setText(currentNote.getCreateDate().toString());
        PrettyTime prettyTime = new PrettyTime();
        holder.textViewDate.setText(prettyTime.format(currentNote.getCreateDate()));
        if(currentNote.getImagePath() != null){
            holder.imageViewNoteType.setImageResource(R.drawable.ic_baseline_image_24);
        }else {
            holder.imageViewNoteType.setImageResource(R.drawable.ic_baseline_text_snippet_24);
        }
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void setNotes(List<Note> notes){
        this.notes = notes;
        notifyDataSetChanged();
    }

    public Note getNoteAt(int position){
        return notes.get(position);
    }

    class NoteHolder extends RecyclerView.ViewHolder{
        private TextView textViewTitle;
        private TextView textViewSubtitle;
        private TextView textViewDate;
        private ImageView imageViewNoteType;

        public NoteHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.noteTitleText);
            textViewSubtitle = itemView.findViewById(R.id.noteSubtitleText);
            textViewDate = itemView.findViewById(R.id.noteDateText);
            imageViewNoteType = itemView.findViewById(R.id.note_list_type_icon);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(listener != null && position != RecyclerView.NO_POSITION){
                        listener.onItemClick(notes.get(position));
                    }
                }
            });
        }
    }

    public interface  OnItemClickListener{
        void onItemClick(Note note);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;

    }
}