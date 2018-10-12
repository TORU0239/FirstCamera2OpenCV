package my.com.toru.firstcamera2opencv.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import my.com.toru.firstcamera2opencv.R;
import my.com.toru.firstcamera2opencv.ui.camera.CameraActivity;

public class RouterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_router);

        findViewById(R.id.opencv).setOnClickListener(v -> {
            startActivity(new Intent(RouterActivity.this, MainActivity.class));
        });

        findViewById(R.id.open_camera2).setOnClickListener(v -> {
            startActivity(new Intent(RouterActivity.this, CameraActivity.class));
        });
    }
}
