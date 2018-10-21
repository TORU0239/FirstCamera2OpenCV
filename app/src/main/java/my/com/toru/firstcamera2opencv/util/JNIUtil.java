package my.com.toru.firstcamera2opencv.util;

import android.view.Surface;

import java.nio.ByteBuffer;

public class JNIUtil {
    static {
        System.loadLibrary("native-lib");
        System.loadLibrary("opencv_java3");
    }

    public static native void convertRGBtoGray(long matAddrInput, long matAddrResult);

    public static native void detectThreshold(long matAddrInput,
                                       long matAddrResult,
                                       int Threshold,
                                       int ThresholdMax,
                                       int type);

    public static native void GrayscaleDisplay(int srcWidth, int srcHeight, int rowStride, ByteBuffer srcBuffer, Surface surface);

    public static native boolean imageProcessing(int width, int height,
                                          byte[] NV21FrameData, int [] pixels);
}