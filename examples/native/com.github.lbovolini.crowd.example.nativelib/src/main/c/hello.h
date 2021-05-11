#include <jni.h>

#ifndef _Included_com_github_lbovolini_example_nativeLib_Native
#define _Included_com_github_lbovolini_example_nativeLib_Native
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_com_github_lbovolini_example_nativeLib_Native_say
  (JNIEnv *, jobject, jstring);

#ifdef __cplusplus
}
#endif
#endif
