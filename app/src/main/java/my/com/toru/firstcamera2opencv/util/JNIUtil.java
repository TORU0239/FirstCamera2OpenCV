package my.com.toru.firstcamera2opencv.util;

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
}