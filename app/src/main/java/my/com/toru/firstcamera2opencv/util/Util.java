package my.com.toru.firstcamera2opencv.util;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;

public class Util {
    //region Check Permission
    @TargetApi(Build.VERSION_CODES.M)
    public static boolean isPermissionGranted(Context ctx){
        return (ContextCompat.checkSelfPermission(ctx, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED);
    }
    //endregion
}
