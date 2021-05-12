#include <stdio.h>
#include <stdlib.h>
#include <jni.h>

#include "hello.h"

#ifdef __ANDROID__
#include <android/log.h>
#endif

void say(const char *str) {
    printf("Hello %s.\n", str);
    
    #ifdef __ANDROID__
    char *hello = (char *) malloc (14 * sizeof(char));
    sprintf(hello, "Hello %s.\n", str);
    __android_log_print(ANDROID_LOG_DEBUG, "CROWD_TAG", "%s", hello);
    #endif
}

JNIEXPORT void JNICALL Java_com_github_lbovolini_crowd_example_nativelib_Native_say (JNIEnv *env, jobject obj, jstring str) {

    const char *cstr = (*env)->GetStringUTFChars(env, str, 0);

    say(cstr);
}