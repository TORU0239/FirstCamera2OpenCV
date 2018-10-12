package my.com.toru.firstcamera2opencv.ui.camera;

import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Size;
import android.view.TextureView;

import my.com.toru.firstcamera2opencv.R;

public class CameraActivity extends AppCompatActivity {
    private static final String TAG = CameraActivity.class.getSimpleName();

    //region textureView
    private TextureView textureView;
    private TextureView.SurfaceTextureListener surfaceTextureListener;
    // endregion

    //region CameraDevice
    private CameraDevice cameraDevice;
    private CameraDevice.StateCallback cameraStateCallback;
    //endregion

    //region Capture
    private CameraCaptureSession captureSession;
    private CameraCaptureSession.CaptureCallback captureCallback;
    private CameraCaptureSession.StateCallback captureStateCallback;
    private CaptureRequest captureRequest;
    private CaptureRequest.Builder captureRequestBuilder;
    //endregion

    //region Image and Size
    private Size size;
    private ImageReader imageReader;
    private ImageReader.OnImageAvailableListener imageAvailableListener;
    //endregion

    //region in charge of Background Task
    private Handler backgroundHander;
    private HandlerThread backgroundThread;
    // endregion


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        initCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initCamera(){
        textureView = findViewById(R.id.camera_texture);
    }

    //region Background Work
    private void initializeBackgroundThread(){
        backgroundThread = new HandlerThread("Camera Background Thread");
        backgroundThread.start();
        backgroundHander = new Handler(backgroundThread.getLooper());
    }

    private void stopBackgroundThread(){
        backgroundThread.quitSafely();
        try {
            backgroundThread.join();
            backgroundThread = null;
            backgroundHander = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    //endofregion
}