[![CHUV](https://img.shields.io/badge/CHUV-LREN-AF4C64.svg)](https://www.unil.ch/lren/en/home.html) [![License](https://img.shields.io/badge/license-Apache--2.0-blue.svg)](https://github.com/LREN-CHUV/java-base-docker-images/blob/master/LICENSE) [![DockerHub](https://img.shields.io/badge/docker-hbpmip%2Fjava--base-008bb8.svg)](https://hub.docker.com/r/hbpmip/java-base/) [![ImageVersion](https://images.microbadger.com/badges/version/hbpmip/java-base.svg)](https://hub.docker.com/r/hbpmip/java-base/tags "hbpmip/java-base image tags") [![ImageLayers](https://images.microbadger.com/badges/image/hbpmip/java-base.svg)](https://microbadger.com/#/images/hbpmip/java-base "hbpmip/java-base on microbadger")

# hbpmip/java-base: Base image for Java programs

This image contains the base Java Runtime (from [OpenJDK image](https://hub.docker.com/r/_/openjdk/))

## Usage

Dockerfile
```
  FROM hbpmip/java-base:11.0.1-1

```

To start Java inside the container:

```
  java ${JAVA_OPTIONS} -cp ${JAVA_CLASSPATH} ${JAVA_MAINCLASS} ${JAVA_ARGS}
```

Use the pre-defined Java options as they ensure that Java memory management will adapt to Docker.
