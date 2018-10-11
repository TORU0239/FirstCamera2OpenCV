package my.com.toru.firstcamera2opencv.ui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import java.util.Collections;

import my.com.toru.firstcamera2opencv.R;

public class NewCameraActivity extends AppCompatActivity {


    //region preview related
    private TextureView textureview;
    private CaptureRequest.Builder captureReqBuilder;
    private TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            initCamara(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };

    private CameraCaptureSession.StateCallback captureSessionCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            try {
                session.setRepeatingRequest(captureReqBuilder.build(), null, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
        }
    };

    private void createCameraPreviewSession() {
        SurfaceTexture texture = textureview.getSurfaceTexture();
        texture.setDefaultBufferSize(size.getWidth(), size.getHeight());
        Surface surface = new Surface(texture);

        try {
            captureReqBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureReqBuilder.addTarget(surface);

            cameraDevice.createCaptureSession(Collections.singletonList(surface), captureSessionCallback, null);
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

    private void initCamara(int width, int height) {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        if (cameraManager == null) return;

        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
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
        } else {
            // call from the listener callback
            textureview.setSurfaceTextureListener(surfaceTextureListener);
        }
    }
}
