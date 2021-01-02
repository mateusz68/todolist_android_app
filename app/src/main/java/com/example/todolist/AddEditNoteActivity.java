package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.todolist.noteDatabase.Note;
import com.example.todolist.noteDatabase.NoteViewModel;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddEditNoteActivity extends AppCompatActivity {
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 105;
    public static final String EXTRA_ID = "com.example.todolist.EXTRA_NOTE_ID";
    public static final int STORAGE_REQUEST_CODE = 106;
    public static final int WRITE_STORAGE_REQUEST_CODE = 107;
    private EditText editNoteTitle;
    private EditText editNoteContent;
    private ImageView noteImage;
    private Button takePictureBtn;
    private Button addPictureBtn;
    private String currentPhotoPath;
    private Note selectedNote;
    private int id;
    private static String TAG = "AddNoteActivity";


    private NoteViewModel noteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        editNoteTitle = findViewById(R.id.note_title);
        editNoteContent = findViewById(R.id.note_text);
        noteImage = findViewById(R.id.add_note_image);

        takePictureBtn = findViewById(R.id.take_photo_btn);
        addPictureBtn = findViewById(R.id.add_photo_btn);

        takePictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askCameraPermissions();
                askReadStoragePerm();
            }
        });

        addPictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askReadStoragePerm();
                Intent gallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery, GALLERY_REQUEST_CODE);
            }
        });
        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);
        Intent intent = getIntent();

        if(intent.hasExtra(EXTRA_ID)){
            setTitle(getString(R.string.edit_note_titile));
            int noteId = intent.getIntExtra(EXTRA_ID,-1);
            if(noteId != -1){
                id = noteId;
                new getNote().execute();
            }
        }else{
            setTitle(getString(R.string.add_note_title));
        }

    }


    private void saveNote(){
        String title = editNoteTitle.getText().toString();
        String content = editNoteContent.getText().toString();

        // Sprawdzam czy pola nie są puste
        if(title.trim().isEmpty()){
            Toast.makeText(this, getString(R.string.error_empty_title),Toast.LENGTH_SHORT).show();
            return;
        }
        if(content.trim().isEmpty()){
            Toast.makeText(this, getString(R.string.error_empty_description_task),Toast.LENGTH_SHORT).show();
            return;
        }

        Intent data = new Intent();
        if(id != -1 && selectedNote != null){
            selectedNote.setTitle(editNoteTitle.getText().toString());
            selectedNote.setContent(editNoteContent.getText().toString());
            selectedNote.setImagePath(currentPhotoPath);
            noteViewModel.update(selectedNote);
        }else {
            Date date = new Date();
            Note note = new Note(title,content,date);
            if(currentPhotoPath != null){
                note.setImagePath(currentPhotoPath);
            }
            noteViewModel.insert(note);
        }

        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_task_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_task:
                saveNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void askCameraPermissions() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},CAMERA_PERM_CODE);
        }else{
//            dispatchTakePictureIntent();
            askWriteStoragePerm();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CAMERA_PERM_CODE){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                dispatchTakePictureIntent();
                askWriteStoragePerm();
            }else{
                Toast.makeText(this, getString(R.string.camera_permission_requried_error),Toast.LENGTH_SHORT).show();
            }
        }
        if(requestCode == STORAGE_REQUEST_CODE){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                setImage();
            }else{
                Toast.makeText(this, getString(R.string.storage_permission_requried_error),Toast.LENGTH_SHORT).show();
            }
        }
        if(requestCode == WRITE_STORAGE_REQUEST_CODE){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                setImage();
                dispatchTakePictureIntent();
            }else{
                Toast.makeText(this,getString(R.string.storage_permission_requried_error),Toast.LENGTH_SHORT).show();
            }
        }
    }

//    private void openCamera(){
//        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(camera, CAMERA_REQUEST_CODE);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            File f = new File(currentPhotoPath);
            Uri photoUri = Uri.fromFile(f);
            noteImage.setImageURI(photoUri);
            noteImage.setVisibility(View.VISIBLE);
            currentPhotoPath = getPathFromUri(photoUri);
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            //dodaj zdjęcie do galerii
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);
        }
        if(requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Uri contentUri = data.getData();
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "." + getFileExt(contentUri);
            noteImage.setImageURI(contentUri);
            Log.i(TAG, "MyClass.getView() — get item number " +contentUri);
            noteImage.setVisibility(View.VISIBLE);
            currentPhotoPath = getPathFromUri(contentUri);
//            currentPhotoPath = contentUri.toString();
            Log.i(TAG, "path by function " + getPathFromUri(contentUri));
            Log.i(TAG, "path by uri " + contentUri.getPath());

        }
    }

    private String getFileExt(Uri contentUri) {
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }

    private String getPathFromUri(Uri contentUri){
        String filePath;
        Cursor cursor = getContentResolver()
                .query(contentUri,null,null,null,null);
        if(cursor == null){
            filePath = contentUri.getPath();
        }else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            filePath = cursor.getString(index);
            cursor.close();
        }
        return filePath;
    }

    //https://developer.android.com/training/camera/photobasics

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //Zmieniam ścieżkę żeby zdjęcia były widoczne w galerii
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.i(TAG, "error permision");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                Log.i(TAG, "create request");
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    private void setImage(){
        if(selectedNote.getImagePath() != null){
            Log.i(TAG, "image path " + selectedNote.getImagePath());
            File image = new File(selectedNote.getImagePath());
            if(image.exists())
            {
                noteImage.setImageURI(Uri.fromFile(image));
                noteImage.setVisibility(View.VISIBLE);
            }
        }
    }

    private void askWriteStoragePerm(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_STORAGE_REQUEST_CODE);
        }else{
//            setImage();
            dispatchTakePictureIntent();
        }
    }

    private void askReadStoragePerm(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_REQUEST_CODE);
        }else{
//            setImage();
        }
    }

    private class getNote extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids){
            selectedNote = noteViewModel.getNoteById(id);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(selectedNote != null){
                editNoteTitle.setText(selectedNote.getTitle());
                editNoteContent.setText(selectedNote.getContent());
                if(selectedNote.getImagePath() != null){
                    askReadStoragePerm();
                    setImage();
                    currentPhotoPath = selectedNote.getImagePath();
                }
            }

        }
    }
}