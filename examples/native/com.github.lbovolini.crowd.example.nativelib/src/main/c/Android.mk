LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := crowdexamplehello

LOCAL_SRC_FILES := hello.c
LOCAL_LDLIBS := -llog

include $(BUILD_SHARED_LIBRARY)


