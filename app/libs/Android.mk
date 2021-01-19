# LOCAL_PATH에 현재 Android.mk 파일이 있는 위치를 저장
LOCAL_PATH:=$(call my-dir)

# include $(CLEAR_VARS) -- 변수 초기화
# LOCAL_MODULE -- 라이브러리 이름설정
# LOCAL_SRC_FILES -- 소스파일 경로설정
# LOCAL_EXPORT_C_INCLUDES -- include 경로 설정
# PREBUILT_SHARED_LIBRARY -- 미리 만들어진 공유 라이브러리

# AVCODEC
include $(CLEAR_VARS)
LOCAL_MODULE:=libavcodec
LOCAL_SRC_FILES:=lib/libavcodec.so
LOCAL_EXPORT_C_INCLUDES:=$(LOCAL_PATH)/include
include $(PREBUILT_SHARED_LIBRARY)

# AVFORMAT
include $(CLEAR_VARS)
LOCAL_MODULE:=libavformat
LOCAL_SRC_FILES:=lib/libavformat.so
LOCAL_EXPORT_C_INCLUDES:=$(LOCAL_PATH)/include
include $(PREBUILT_SHARED_LIBRARY)

# SWSCALE
include $(CLEAR_VARS)
LOCAL_MODULE:=libswscale
LOCAL_SRC_FILES:=lib/libswscale.so
LOCAL_EXPORT_C_INCLUDES:=$(LOCAL_PATH)/include
include $(PREBUILT_SHARED_LIBRARY)

# AVUTIL
include $(CLEAR_VARS)
LOCAL_MODULE:=libavutil
LOCAL_SRC_FILES:=lib/libavutil.so
LOCAL_EXPORT_C_INCLUDES:=$(LOCAL_PATH)/include
include $(PREBUILT_SHARED_LIBRARY)

# AVFILTER
include $(CLEAR_VARS)
LOCAL_MODULE:=libavfilter
LOCAL_SRC_FILES:=lib/libavfilter.so
LOCAL_EXPORT_C_INCLUDES:=$(LOCAL_PATH)/include
include $(PREBUILT_SHARED_LIBRARY)

# SWRESAMPLE
include $(CLEAR_VARS)
LOCAL_MODULE:=libswresample
LOCAL_SRC_FILES:=lib/libswresample.so
LOCAL_EXPORT_C_INCLUDES:=$(LOCAL_PATH)/include
include $(PREBUILT_SHARED_LIBRARY)