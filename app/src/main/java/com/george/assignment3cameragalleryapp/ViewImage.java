package com.george.assignment3cameragalleryapp;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.File;


/**
 * Created by George on 2016-02-01.
 */
public class ViewImage extends AppCompatActivity {


    ImageView imageview;
    int position;
    String[] filename;
    String[] filepath;

    private gpstag gpstag = new gpstag();
    Location location;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_image);

        Intent i = getIntent();
        position = i.getExtras().getInt("position");
        filepath = i.getStringArrayExtra("filepath");
        filename = i.getStringArrayExtra("filename");
        imageview = (ImageView) findViewById(R.id.full_image_view);
        File file = new File(Environment.getExternalStorageDirectory()
                            + "/george998579943camtest123");
        Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath()+ "/" + filename[position]);
        imageview.setImageBitmap(bmp);
        TextView text = (TextView) findViewById(R.id.viewimage_name);
        TextView text2 = (TextView) findViewById(R.id.viewimage_caption);
        text.setText( "Latitue, Longitutde");
        location = gpstag.readGeoTagImage(file.getAbsolutePath()+ "/" + filename[position]);
        text2.setText( + location.getLatitude() + ", " + location.getLongitude());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.layout_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {

            Intent i = getIntent();
            int position1 = i.getExtras().getInt("position");
            String [] filename1 = i.getStringArrayExtra("filename");
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() + "/george998579943camtest1");
            File dir123 = new File(sdCard.getAbsolutePath() + "/george998579943camtest123");
            String filename = filename1[position1];
            File myFile = new File(dir, filename);
            File myFile2 = new File(dir123,filename);
            myFile.delete();
            myFile2.delete();
            refreshGallery(myFile);
            refreshGallery(myFile2);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshGallery(File file) {
        Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        sendBroadcast(mediaScanIntent);
    }
}
