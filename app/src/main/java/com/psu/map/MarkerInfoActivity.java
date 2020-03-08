package com.psu.map;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.psu.map.Dao.App;
import com.psu.map.Dao.AppDatabase;
import com.psu.map.Entity.MapMarker;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MarkerInfoActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    static final int REQUEST_TAKE_PHOTO = 1;

    String currentPhotoPath;

    private AppDatabase db;

    MapMarker mapMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_info);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        Button btSave = findViewById(R.id.btSave);


        db = App.getInstance().getDatabase();
        if(mapMarker == null)
            mapMarker = db.markerDao().findByLOngitudeAndLatitude(getIntent().getDoubleExtra("latitude",0.00 )
                , getIntent().getDoubleExtra("longitude", 0.00));

        setFields();


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPermissions();
                dispatchTakePictureIntent();
            }
        });

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText editTextComment = (EditText)findViewById(R.id.textViewComment);
                mapMarker.comment = editTextComment.getText().toString();

                db.markerDao().update(mapMarker);
                closeActivity();
            }
        });
    }

    private void closeActivity(){
        this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            ImageView imageView = findViewById(R.id.imageView);
            imageView.setImageURI(Uri.parse(mapMarker.filePath));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    /*
        Права на создание файла
     */
    private void getPermissions(){
        int MY_WRITE_EXTERNAL_REQUEST  = 1;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_WRITE_EXTERNAL_REQUEST);
            }
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        mapMarker.filePath = currentPhotoPath;
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
                System.out.println(ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.psu.map.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /*
        Заполняем форму при открытии
     */
    private void setFields(){

        EditText editTextPos = (EditText) findViewById(R.id.textViewPosition);
        DecimalFormat df = new DecimalFormat("###.###");

        editTextPos.setText("Широта = "  +df.format(mapMarker.latitude)
                + "\nДолгота = " + df.format(mapMarker.longitude));

        EditText editTextComment = (EditText)findViewById(R.id.textViewComment);
        ImageView imageView = findViewById(R.id.imageView);

        editTextComment.setText(mapMarker.comment);
        if (mapMarker.filePath != null)
            imageView.setImageURI(Uri.parse(mapMarker.filePath));
    }

    /*
        Сохранение состояния
     */
    protected void onSaveInstanceState(Bundle outState) {

        EditText editTextComment = (EditText)findViewById(R.id.textViewComment);

        super.onSaveInstanceState(outState);
        outState.putString("path",mapMarker.filePath);
        outState.putString("comment",editTextComment.getText().toString());
    }
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mapMarker.comment = savedInstanceState.getString("comment");
        mapMarker.filePath = savedInstanceState.getString("path");

        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageURI(Uri.parse(savedInstanceState.getString("path")));
    }
}
