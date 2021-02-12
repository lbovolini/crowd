#FROM androidsdk/android-29 AS ANDROID

FROM ubuntu

RUN apt-get update && apt-get -y install openjdk-11-jdk && apt-get -y install maven && apt-get -y install build-essential

RUN mvn --version
RUN gcc --version

COPY . .
#COPY --from=ANDROID /opt/android-sdk-linux/platforms/android-29/android.jar /root/Android/Sdk/platforms/android-29/android.jar
#COPY --from=ANDROID /opt/android-sdk-linux/build-tools/30.0.2/lib/dx.jar /root/Android/Sdk/build-tools/30.0.2/lib/dx.jar

#RUN mvn -Dmaven.repo.local=dependencies dependency:go-offline
RUN mvn -Dmaven.repo.local=./.m2/repository package
#RUN mvn -Dmaven.repo.local=./.m2/repository -o package