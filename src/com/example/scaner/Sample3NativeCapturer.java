package com.example.scaner;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.opencv.android.JavaCameraView;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Toast;
import android.hardware.Camera.AutoFocusCallback;
public class Sample3NativeCapturer extends JavaCameraView implements PictureCallback {

    public Sample3NativeCapturer(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public Sample3NativeCapturer(Context context, int camID){
        super(context,camID);
    }

    private static final String TAG = "Sample::Tutorial3View";
    private static int MEDIA_TYPE_IMAGE = 1;
    private int interpBy = 1;
    private Mat theImage;




    public List<String> getEffectList() {

        return mCamera.getParameters().getSupportedColorEffects();
    }

    public boolean isEffectSupported() {
        return (mCamera.getParameters().getColorEffect() != null);
    }

    public String getEffect() {
        return mCamera.getParameters().getColorEffect();
    }

    public void setEffect(String effect) {
        Camera.Parameters params = mCamera.getParameters();
        params.setColorEffect(effect);
        mCamera.setParameters(params);
    }

    public List<Size> getResolutionList() {
        return mCamera.getParameters().getSupportedPreviewSizes();
        //mCamera.getParameters().getSupportedPictureSizes()
    }

    public void setResolution(Size resolution) {
        disconnectCamera();
        mMaxHeight = resolution.height;
        mMaxWidth = resolution.width;
        connectCamera(getWidth(), getHeight());
    }
    //Set CAPTURE resolution (currently only setting preview resolution)
    public void setCaptureResolution(Size resolution){
        Parameters params = mCamera.getParameters();
        params.setPictureSize(resolution.width,resolution.height);
        mCamera.setParameters(params);
    }

    public Size getResolution() {
        return mCamera.getParameters().getPreviewSize();
    }

    public void takePicture() {
        Log.i(TAG, "Taking picture");
        Parameters params = mCamera.getParameters();
        try{
            params.setJpegQuality(100);
            params.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }finally{}
        mCamera.setParameters(params);
        // Postview and jpeg are sent in the same buffers if the queue is not empty when performing a capture.
        // Clear up buffers to avoid mCamera.takePicture to be stuck because of a memory issue
        mCamera.setPreviewCallback(null);

        // PictureCallback is implemented by the current class

        mCamera.takePicture(null, null, this);
    }

    //@Override
    public void onPictureTaken(byte[] data, Camera camera) {
        Log.i(TAG, "Saving a bitmap to file");
        // The camera preview was automatically stopped. Start it again.
        mCamera.startPreview();
        mCamera.setPreviewCallback(this);

        // Write the image in a file (in jpeg format)
        //File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
        try {
            //Now try to convert and save.
            Parameters params = camera.getParameters();
            Size sz = params.getPictureSize();
            Mat raw = new Mat(1, data.length, CvType.CV_8UC1);
            raw.put(0, 0, data);
            theImage = Highgui.imdecode(raw, 0);

        } finally {
            Log.e("PictureDemo", "Exception in photoCallback");
        }

    }

    @SuppressLint("SimpleDateFormat")
    public static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                  Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
            "IMG_"+ timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }


       public Mat getCapturedImage(int interpVal){
           interpBy = interpVal;
           takePicture();
           return theImage;
       }

}