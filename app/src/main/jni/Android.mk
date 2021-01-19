LOCAL_PATH:=$(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE:=sample-ffmpeg
LOCAL_SRC_FILES:=sample-ffmpeg.c
LOCAL_LDLIBS:=-llog
LOCAL_SHARED_LIBRARIES:=libavformat libavcodec libswscale libavutil libswresample libavfilter
include $(BUILD_SHARED_LIBRARY)
$(call import-add-path, /Users/hangyojeong/Dev/AVideoEditor/app)
$(call import-module, libs)

# $(call import-module, libs) -- 기존에 있는 라이브러리의 경로를 지정, 경로의 기준은 $NDK_MODULE_PATH임 ndk가 설치된 위치를 가르킴
# $(call import-add-path, /Users/hangyojeong/Dev/AVideoEditor/app) - 프로젝트의 app 폴더 위치를 추가함
# LOCAL_LDLIBS:=-llog -- NDK에서 제공하는 라이브러리