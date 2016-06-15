package com.george.assignment3cameragalleryapp;


import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import java.io.IOException;
import java.util.List;

/**
 * Created by George on 2016-01-31.
 */
public class tools extends ViewGroup implements SurfaceHolder.Callback{
    SurfaceView surfview;
    SurfaceHolder surfholder;
    Camera.Size prevSize;
    List<Camera.Size> supPrevSize;
    Camera cam;

    tools(Context context, SurfaceView sv) {
        super(context);

        surfview = sv;
        surfholder = surfview.getHolder();
        surfholder.addCallback(this);
        surfholder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void setCamera(Camera camera, int orientation) {
        cam = camera;

        if (cam != null) {
            supPrevSize = cam.getParameters().getSupportedPreviewSizes();
            requestLayout();
            Camera.Parameters params = cam.getParameters();
            List<String> focusModes = params.getSupportedFocusModes();
            if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            }
            params.setRotation(orientation);
            params.setGpsLongitude(50);
            params.setGpsLatitude(40);
            //params.setJpegQuality(100);
            params.setPictureSize(1280,720);
            cam.setParameters(params);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);
        if (supPrevSize != null) {
            prevSize = getOptimalPreviewSize(supPrevSize, width, height);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed && getChildCount() > 0) {
            final View child = getChildAt(0);
            final int width = r - l;
            final int height = b - t;

            int previewWidth = width;
            int previewHeight = height;
            if (prevSize != null) {
                previewWidth = prevSize.width;
                previewHeight = prevSize.height;
            }

            if (width * previewHeight > height * previewWidth) {
                final int scaledChildWidth = previewWidth * height / previewHeight;
                child.layout((width - scaledChildWidth) / 2, 0,
                        (width + scaledChildWidth) / 2, height);
            } else {
                final int scaledChildHeight = previewHeight * width / previewWidth;
                child.layout(0, (height - scaledChildHeight) / 2,
                        width, (height + scaledChildHeight) / 2);
            }
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (cam != null) {
                cam.setPreviewDisplay(holder);
            }
        } catch (IOException exception) {

        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        if (cam != null) {
            cam.stopPreview();
        }
    }


    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if(cam != null) {
            Camera.Parameters parameters = cam.getParameters();
            parameters.setPreviewSize(prevSize.width, prevSize.height);
            requestLayout();
            cam.setParameters(parameters);
            cam.startPreview();
        }
    }

}
