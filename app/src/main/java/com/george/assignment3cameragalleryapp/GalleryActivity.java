package com.george.assignment3cameragalleryapp;


import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import java.io.File;


/**
 * Created by George on 2016-01-30.
 */
public class GalleryActivity extends AppCompatActivity {


    private String[] filePath;
    private String[] filenameArray;
    private File[] fileListArray;
    GridView gridviewer;
    GridViewAdapter gridVAdapter;
    File file;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gridview_main);

        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "no sd card", Toast.LENGTH_LONG)
                    .show();
        } else {
            file = new File(Environment.getExternalStorageDirectory()
                    + "/george998579943camtest1");
            file.mkdirs();
        }

        if (file.isDirectory()) {
            fileListArray = file.listFiles();
            filePath = new String[fileListArray.length];
            filenameArray = new String[fileListArray.length];
            for (int i = 0; i < fileListArray.length; i++) {
                filePath[i] = fileListArray[i].getAbsolutePath();
                filenameArray[i] = fileListArray[i].getName();
            }
        }

        gridviewer = (GridView) findViewById(R.id.gridview);
        gridVAdapter = new GridViewAdapter(this, filePath, filenameArray);
        gridviewer.setAdapter(gridVAdapter);

        gridviewer.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent i = new Intent(GalleryActivity.this, ViewImage.class);
                i.putExtra("filepath", filePath);
                i.putExtra("filename", filenameArray);
                i.putExtra("position", position);
                startActivity(i);
            }

        });
    }

  @Override
    protected void onResume(){
        super.onResume();
      if (file.isDirectory()) {
          fileListArray = file.listFiles();

          filePath = new String[fileListArray.length];
          filenameArray = new String[fileListArray.length];

          for (int i = 0; i < fileListArray.length; i++) {
              filePath[i] = fileListArray[i].getAbsolutePath();
              filenameArray[i] = fileListArray[i].getName();
          }
      }

      gridviewer = (GridView) findViewById(R.id.gridview);
      gridVAdapter = new GridViewAdapter(this, filePath, filenameArray);
      gridviewer.setAdapter(gridVAdapter);
  }


}
