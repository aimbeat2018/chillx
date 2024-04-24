#include <jni.h>
#include <string>


//std::string SERVER_URL = "https://primeplay.co.in/webworld_backoffice/rest-api/";
std::string SERVER_URL = "https://hitprime.in/hitprime_backoffice/rest-api/";
std::string API_KEY = "2b14kpa3k18nyqbu1sdre1cy4kpa3k18";
std::string PURCHASE_CODE = "xxxxxxxxxxxxxxx";


//WARNING: ==>> Don't change anything below.
extern "C" JNIEXPORT jstring JNICALL
Java_ott_spices_AppConfig_getApiServerUrl(
        JNIEnv *env,
        jclass clazz) {
    return env->NewStringUTF(SERVER_URL.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_ott_spices_AppConfig_getApiKey(
        JNIEnv *env,
        jclass clazz) {
    return env->NewStringUTF(API_KEY.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_ott_spices_AppConfig_getPurchaseCode(
        JNIEnv *env,
        jclass clazz) {
    return env->NewStringUTF(PURCHASE_CODE.c_str());
}

