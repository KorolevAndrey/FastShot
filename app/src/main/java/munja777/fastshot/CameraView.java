package munja777.fastshot;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class CameraView extends Activity implements SurfaceHolder.Callback,
        View.OnClickListener {


    Camera mCamera;
    boolean mPreviewRunning = false;
    String time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    String image = "JPEG_" + time + ".jpg";
    String base = Environment.getExternalStorageDirectory() + File.separator + "DCIM/Camera/" + image;



    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.cameraview);
        SurfaceView mSurfaceView = (SurfaceView) findViewById(R.id.surface_camera);
        SurfaceHolder mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceHolder.setKeepScreenOn(true);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    protected void onStop() {
        super.onStop();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

        if (mPreviewRunning) mCamera.stopPreview();
        Camera.Parameters p = mCamera.getParameters();
        mCamera.setParameters(p);
        mCamera.startPreview();
        mPreviewRunning = true;
        mCamera.takePicture(null, null, mPictureCallback);
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        stopCamera();
    }

    public void onClick(View v) {
        mCamera.takePicture(null, mPictureCallback, mPictureCallback);
    }

    public void surfaceCreated(SurfaceHolder holder) {

        while (true) {

            try {
                this.mCamera = Camera.open(0);
                try {
                    this.mCamera.setPreviewDisplay(holder);
                    return;
                } catch (IOException localIOException2) {
                    stopCamera();
                    return;
                }
            } catch (RuntimeException localRuntimeException) {
                localRuntimeException.printStackTrace();
                if (this.mCamera == null)
                    continue;
                stopCamera();
                this.mCamera = Camera.open(0);
                try {
                    this.mCamera.setPreviewDisplay(holder);
                    return;
                } catch (IOException localIOException1) {
                    stopCamera();
                    localIOException1.printStackTrace();
                    return;
                }
            } catch (Exception localException) {
                if (this.mCamera != null)
                    stopCamera();
                localException.printStackTrace();
                return;
            }
        }
    }

    private void stopCamera() {
        if (this.mCamera != null) this.mPreviewRunning = false;
    }


    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {

        public void onPictureTaken(byte[] data, Camera camera) {

            if (data != null) {
                mCamera.stopPreview();
                mPreviewRunning = false;
                mCamera.release();

                try {
                    File f = new File(base);
                    FileOutputStream fo = new FileOutputStream(f);
                    fo.write(data);
                    galleryAddPic();
                    fo.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                setResult(585);
                finish();
            }
        }
    };

    private void galleryAddPic() {

        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(base);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

}