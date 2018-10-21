#include <jni.h>
#include <string>
#include <opencv2/opencv.hpp>

// from internet
#include <android/log.h>
#include <android/native_window.h>
#include <android/native_window_jni.h>

using namespace cv;

extern "C" JNIEXPORT jstring JNICALL
Java_my_com_toru_firstcamera2opencv_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT void JNICALL
Java_my_com_toru_firstcamera2opencv_util_JNIUtil_convertRGBtoGray(JNIEnv *env, jclass type,
                                                                  jlong matAddrInput,
                                                                  jlong matAddrResult) {

    Mat &matInput = *(Mat *)matAddrInput;
    Mat &matResult = *(Mat *)matAddrResult;
    cvtColor(matInput, matResult, CV_RGBA2GRAY);
}

extern "C"
JNIEXPORT void JNICALL
Java_my_com_toru_firstcamera2opencv_util_JNIUtil_detectThreshold(JNIEnv *env, jclass clazz,
                                                                 jlong matAddrInput,
                                                                 jlong matAddrResult,
                                                                 jint Threshold, jint ThresholdMax,
                                                                 jint type) {

    Mat &matInput = *(Mat *)matAddrInput;
    Mat &matResult = *(Mat *)matAddrResult;
    threshold(matInput, matResult, Threshold, ThresholdMax, type);
}

extern "C"
JNIEXPORT void JNICALL
Java_my_com_toru_firstcamera2opencv_util_JNIUtil_GrayscaleDisplay(JNIEnv *env, jclass type,
                                                                  jint srcWidth, jint srcHeight,
                                                                  jint rowStride, jobject srcBuffer,
                                                                  jobject surface) {

    uint8_t *srcLumaPtr = reinterpret_cast<uint8_t *>(env->GetDirectBufferAddress(srcBuffer));

    ANativeWindow * window = ANativeWindow_fromSurface(env, surface);
    ANativeWindow_acquire(window);
    ANativeWindow_Buffer buffer;
    //set output size and format
    //only 3 formats are available:
    //WINDOW_FORMAT_RGBA_8888(DEFAULT), WINDOW_FORMAT_RGBX_8888, WINDOW_FORMAT_RGB_565
    ANativeWindow_setBuffersGeometry(window, 0, 0, WINDOW_FORMAT_RGBA_8888);
    if (int32_t err = ANativeWindow_lock(window, &buffer, NULL)) {
        ANativeWindow_release(window);
    }

    //to display grayscale, first convert the Y plane from YUV_420_888 to RGBA
    //ANativeWindow_Buffer buffer;
    uint8_t * outPtr = reinterpret_cast<uint8_t *>(buffer.bits);
    for (size_t y = 0; y < srcHeight; y++)
    {
        uint8_t * rowPtr = srcLumaPtr + y * rowStride;
        for (size_t x = 0; x < srcWidth; x++)
        {
            //for grayscale output, just duplicate the Y channel into R, G, B channels
            *(outPtr++) = *rowPtr; //R
            *(outPtr++) = *rowPtr; //G
            *(outPtr++) = *rowPtr; //B
//            *(outPtr++) = 255; // gamma for RGBA_8888
            ++rowPtr;
        }
    }

    ANativeWindow_unlockAndPost(window);
    ANativeWindow_release(window);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_my_com_toru_firstcamera2opencv_util_JNIUtil_imageProcessing(JNIEnv *env, jclass type,
                                                                 jint width, jint height,
                                                                 jbyteArray NV21FrameData_,
                                                                 jintArray pixels_) {
    jbyte *NV21FrameData = env->GetByteArrayElements(NV21FrameData_, NULL);
    jint *pixels = env->GetIntArrayElements(pixels_, NULL);

    // TODO:

    env->ReleaseByteArrayElements(NV21FrameData_, NV21FrameData, 0);
    env->ReleaseIntArrayElements(pixels_, pixels, 0);
}