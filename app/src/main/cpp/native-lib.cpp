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
Java_my_com_toru_firstcamera2opencv_util_JNIUtil_convertRGBtoGray(JNIEnv *env, jobject instance,
                                                                  jlong matAddrInput,
                                                                  jlong matAddrResult) {

    Mat &matInput = *(Mat *)matAddrInput;
    Mat &matResult = *(Mat *)matAddrResult;
    cvtColor(matInput, matResult, CV_RGBA2GRAY);
}

extern "C"
JNIEXPORT void JNICALL
Java_my_com_toru_firstcamera2opencv_util_JNIUtil_detectThreshold(JNIEnv *env, jobject instance,
                                                                 jlong matAddrInput,
                                                                 jlong matAddrResult,
                                                                 jint Threshold, jint ThresholdMax,
                                                                 jint type) {

    Mat &matInput = *(Mat *)matAddrInput;
    Mat &matResult = *(Mat *)matAddrResult;
    threshold(matInput, matResult, Threshold, ThresholdMax, type);
}