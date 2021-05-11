#include <stdio.h>
#include <jni.h>
#include "hello.h"

void say(const char *str) {
    printf("Hello %s.\n", str);
}

JNIEXPORT void JNICALL Java_com_github_lbovolini_example_nativeLib_Native_say (JNIEnv *env, jobject obj, jstring str) {

    const char *cstr = (*env)->GetStringUTFChars(env, str, 0);

    say(cstr);
}