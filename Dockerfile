FROM androidsdk/android-30@sha256:257d036509bd698753ca80f21aa8bb8f998017e03b248d9ce7c2522a086f381c AS ANDROID

COPY . .
COPY --from=ANDROID /opt/android-sdk-linux/platforms/android-30/android.jar ${HOME}/Android/Sdk/platforms/android-30/android.jar
COPY --from=ANDROID /opt/android-sdk-linux/build-tools/30.0.2/lib/dx.jar ${HOME}/Android/Sdk/build-tools/30.0.2/lib/dx.jar

