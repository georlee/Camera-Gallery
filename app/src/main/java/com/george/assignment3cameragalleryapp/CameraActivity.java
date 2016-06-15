package com.george.assignment3cameragalleryapp;




import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Created by George on 2016-01-29.
 */
public class CameraActivity extends Activity{
    private gps mgps;
    private gpstag mgpstag;
    private SensorManager sensorM;
    private Sensor accele;
    private accelerometer shakeD;
    tools prev;
    Camera cam;
    Activity activity;
    Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mgps =new gps(CameraActivity.this);
        mgpstag = new gpstag();
        context = this;
        activity = this;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.layout_camera);

        prev = new tools(this, (SurfaceView)findViewById(R.id.surfaceView));
        prev.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ((FrameLayout) findViewById(R.id.framelayout_camera)).addView(prev);
        prev.setKeepScreenOn(true);

        sensorM = (SensorManager)getSystemService(SENSOR_SERVICE);
        accele = sensorM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        shakeD = new accelerometer(new accelerometer.OnShakeListener() {
            @Override
            public void onShake() {
              try{
                  CountDownTimer countdowntimerTAKEPIC = new CountDownTimer(1000, 1000) {
                      public void onTick(long millisUntilFinished) {
                      }
                      public void onFinish() {
                          try {
                              cam.setDisplayOrientation(getScreenRotationOnPhone());
                              prev.setCamera(cam, getScreenRotationOnPhone());
                              cam.takePicture(shutterCallback, rawCallback, jpegCallback);
                              Toast.makeText(context, getString(R.string.saved), Toast.LENGTH_SHORT).show();
                          }
                          catch(Exception e){
                          }
                      }
                  }.start();
                }
                catch(Exception e){
                }
            }
        });
        Toast.makeText(context, "Shake to take Picture", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorM.registerListener(shakeD, accele, SensorManager.SENSOR_DELAY_UI);
        int numCams = Camera.getNumberOfCameras();
        if(numCams > 0){
            try{
                cam = Camera.open(0);
                cam.setDisplayOrientation(getScreenRotationOnPhone());
                cam.startPreview();
                prev.setCamera(cam, getScreenRotationOnPhone());
            } catch (RuntimeException ex){
                Toast.makeText(context, "No camera found", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onPause() {
        sensorM.unregisterListener(shakeD);
        if(cam != null) {
            cam.stopPreview();
            prev.setCamera(null, -1);
            cam.release();
            cam = null;
        }
        super.onPause();
    }

    private void resetCam() {
        cam.setDisplayOrientation(getScreenRotationOnPhone());
        cam.startPreview();
        prev.setCamera(cam, getScreenRotationOnPhone());
    }

    private int getScreenRotationOnPhone() {

        final Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int rotation=0;

            switch (display.getRotation()) {
                case Surface.ROTATION_0:
                    rotation = 90;
                    break;
                case Surface.ROTATION_90:
                    rotation = 0;
                    break;
                case Surface.ROTATION_180:
                    rotation = 270;
                    break;
                case Surface.ROTATION_270:
                    rotation = 180;
                    break;
            }
        return rotation;
    }

    private void refreshGallery(File file) {
        Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        sendBroadcast(mediaScanIntent);
    }

    Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
        }
    };

    Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera cam) {
        }
    };

    Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera cam) {
            new SaveImageTask().execute(data);
            resetCam();
        }
    };

        private class SaveImageTask extends AsyncTask<byte[], Void, Void> {

            @Override
            protected Void doInBackground(byte[]... data) {
                FileOutputStream outStream = null;

                try {
                    File sdCard = Environment.getExternalStorageDirectory();
                    File dir = new File(sdCard.getAbsolutePath() + "/george998579943camtest123");
                    dir.mkdirs();

                    String fileName = String.format("%d.jpg", System.currentTimeMillis());
                    File outFile = new File(dir, fileName);
                    outStream = new FileOutputStream(outFile);
                    outStream.write(data[0]);
                    outStream.flush();
                    outStream.close();
                    refreshGallery(outFile);
                        ExifInterface exif=null;
                        try {
                            exif = new ExifInterface(outFile.getAbsolutePath());
                            String gpslat = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                            String gpslong = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, gpslat);
                            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, gpslong);
                            exif.saveAttributes();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_UNDEFINED);

                    Bitmap bitmap = BitmapFactory.decodeFile(outFile.getAbsolutePath());
                        Bitmap bmRotated = rotateBitmap(bitmap, orientation);

                    try {
                        FileOutputStream fos = new FileOutputStream(outFile);
                        bmRotated.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        fos.flush();
                        fos.close();

                    } catch (FileNotFoundException e) {
                    } catch (IOException e) {
                    }

                        File dir_TB = new File(sdCard.getAbsolutePath() + "/george998579943camtest1");
                        dir_TB.mkdirs();

                        File outFile_TB = new File(dir_TB, fileName);
                        FileOutputStream outStream_TB = new FileOutputStream(outFile_TB);

                        Bitmap bmp;
                        bmp = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(outFile.getAbsolutePath()), 200, 200);

                        bmp.compress(Bitmap.CompressFormat.JPEG, 100, outStream_TB);

                        outStream_TB.flush();
                        outStream_TB.close();
                        refreshGallery(outFile_TB);

                    mgpstag.MarkGeoTagImage(outFile.getAbsolutePath(), mgps.getLocation());
                    mgpstag.MarkGeoTagImage( outFile_TB.getAbsolutePath() ,mgps.getLocation());

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                }
                return null;
            }
        }

    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }
}
