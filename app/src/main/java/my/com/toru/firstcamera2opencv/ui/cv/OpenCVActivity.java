package my.com.toru.firstcamera2opencv.ui.cv;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import my.com.toru.firstcamera2opencv.R;
import my.com.toru.firstcamera2opencv.ui.camera.CameraActivity;
import my.com.toru.firstcamera2opencv.util.JNIUtil;

public class OpenCVActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String TAG = OpenCVActivity.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_CODE = 1000;

    private String[] PERMISSIONS  = {"android.permission.CAMERA"};

    private CameraBridgeViewBase mOpenCvCameraView;
    private Mat source;
    private Mat result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        findViewById(R.id.img_test).setOnClickListener(v -> {
           startActivity(new Intent(OpenCVActivity.this, CameraActivity.class));
        });

        mOpenCvCameraView = findViewById(R.id.activity_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setCameraIndex(0); // front-camera(1),  back-camera(0)

        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermissions(PERMISSIONS)) {
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
            else{
                mOpenCvCameraView.enableView();
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "onResume :: Internal OpenCV library not found.");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallback);
        }
        else {
            Log.d(TAG, "onResum :: OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onPause() {
        if (mOpenCvCameraView != null){
            mOpenCvCameraView.disableView();
        }
        super.onPause();
    }

    public void onDestroy() {
        if (mOpenCvCameraView != null){
            mOpenCvCameraView.disableView();
        }
        super.onDestroy();
    }


    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    mOpenCvCameraView.enableView();
                    break;

                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    //region Camera Permission
    private boolean hasPermissions(String[] permissions) {
        int result;
        for (String perms : permissions){
            result = ContextCompat.checkSelfPermission(this, perms);
            if (result == PackageManager.PERMISSION_DENIED){
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraPermissionAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (!cameraPermissionAccepted){
                        showDialogForPermission("App is only available after granting permission");
                    }
                }
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder( OpenCVActivity.this);
        builder.setTitle("NOTICE!!");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("YES", (dialog, id) -> requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE));
        builder.setNegativeButton("NO", (dialog, id) -> finish());
        builder.create().show();
    }
    //endregion

    @Override
    public void onCameraViewStarted(int width, int height) {}

    @Override
    public void onCameraViewStopped() {
        if(source != null){
            source.release();
        }

        if(result != null){
            result.release();
        }
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        source = inputFrame.rgba();
        if(result == null ){
            result = new Mat(source.rows(), source.cols(), source.type());
        }

        JNIUtil.detectThreshold(source.getNativeObjAddr(),
                        result.getNativeObjAddr(),
                        120,
                        255,
                        Imgproc.THRESH_BINARY);
        return result;
    }
    //endregion
}