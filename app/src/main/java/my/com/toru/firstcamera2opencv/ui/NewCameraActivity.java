package my.com.toru.firstcamera2opencv.ui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import my.com.toru.firstcamera2opencv.R;

public class NewCameraActivity extends AppCompatActivity {
    private static final String TAG = NewCameraActivity.class.getSimpleName();

    //region preview related
    private TextureView textureview;
    private CaptureRequest.Builder captureReqBuilder;
    private TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            initCamara(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {}

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };

    private CameraCaptureSession captureSession;

    private CameraCaptureSession.StateCallback captureSessionCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            captureSession = session;
            try {
                captureSession.setRepeatingRequest(captureReqBuilder.build(), null, null);
            }
            catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {}
    };

    private ImageReader imageReader;

    private ImageReader.OnImageAvailableListener imageAvailableListener = reader -> {
        Log.w(TAG, "reader");
//        Image image =  imageReader.acquireNextImage();
//        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
//        byte[] bytes = new byte[buffer.remaining()];
//        buffer.get(bytes);
//        image.close();
    };

    private void createCameraPreviewSession() {
        SurfaceTexture texture = textureview.getSurfaceTexture();
        texture.setDefaultBufferSize(size.getWidth(), size.getHeight());
        Surface surface = new Surface(texture);
        Surface imageSurface = imageReader.getSurface();

        HandlerThread thread = new HandlerThread("CameraPreview");
        thread.start();
        Handler backgroundHandler = new Handler(thread.getLooper());

        try {
            captureReqBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureReqBuilder.addTarget(surface);
//            captureReqBuilder.addTarget(imageSurface);
            cameraDevice.createCaptureSession(Arrays.asList(surface/*, imageSurface*/), captureSessionCallback, backgroundHandler);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }

    //endregion

    //region camera related
    private CameraDevice cameraDevice;
    private Size size;
    private CameraDevice.StateCallback cameraStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            cameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            cameraDevice = null;
        }
    };
    //endregion

    /**
     * Compares two {@code Size}s based on their areas.
     */
    static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }

    }


    private void setupCamera(){
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);

                StreamConfigurationMap map = characteristics.get(
                        CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (map == null) {
                    continue;
                }

                HandlerThread thread = new HandlerThread("CameraPreview2");
                thread.start();
                Handler backgroundHandler = new Handler(thread.getLooper());

                // For still image captures, we use the largest available size.
                Size largest = Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                        new CompareSizesByArea());
                imageReader = ImageReader.newInstance(largest.getWidth() / 16, largest.getHeight() / 16, ImageFormat.JPEG, /*maxImages*/2);
                imageReader.setOnImageAvailableListener(imageAvailableListener, backgroundHandler);
                return;
            }
        } catch (CameraAccessException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void initCamara(int width, int height) {
        setupCamera();
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        if (cameraManager == null) return;
        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);

            Size[] jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                                                .getOutputSizes(ImageFormat.JPEG);
            StreamConfigurationMap configurationMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            if (configurationMap != null) {
                size = configurationMap.getOutputSizes(SurfaceTexture.class)[0];
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    cameraManager.openCamera(cameraId, cameraStateCallback, null);
                }
            }

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_camera);
        textureview = findViewById(R.id.textureview);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (textureview.isAvailable()) {
            // call now
            int width = textureview.getWidth();
            int height = textureview.getHeight();
            initCamara(width, height);
        }
        else {
            // call from the listener callback
            textureview.setSurfaceTextureListener(surfaceTextureListener);
        }
    }
}
