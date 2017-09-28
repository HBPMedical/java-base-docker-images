FROM hbpmip/java-base-build:3.5.0-jdk-8-7 as build-java-env

COPY pom.xml /project/pom.xml
COPY src/ /project/src

# Repeating the file copy works better. I dunno why.
RUN cp /usr/share/maven/ref/settings-docker.xml /root/.m2/settings.xml \
    && mvn package site

FROM hbpmip/java-mip:0.4.1

MAINTAINER arnaud@ahead-solutions.ch

ENV JAVA_CLASSPATH=${JAVA_CLASSPATH}:/usr/share/jars/mip-weka-deps.jar
ENV JAVA_MAINCLASS=eu.humanbrainproject.mip.algorithms.weka.Main

COPY --from=build-java-env /usr/share/maven/ref/repository/org/postgresql/postgresql/9.4.1212.jre7/postgresql-9.4.1212.jre7.jar /usr/share/jars/postgres-9.4.jar
COPY --from=build-java-env /project/target/weka.jar /usr/share/jars/mip-weka.jar
COPY --from=build-java-env /project/target/weka-deps.jar /usr/share/jars/mip-weka-deps.jar
COPY --from=build-java-env /project/target/site/ /mip/java-weka/docs/
COPY src/ /mip/java-weka/src/

RUN chown -R compute:compute /mip/

LABEL org.label-schema.build-date=$BUILD_DATE \
      org.label-schema.name="hbpmip/java-weka" \
      org.label-schema.description="Execute RapidMiner algorithms in MIP Woken environment" \
      org.label-schema.url="https://github.com/LREN-CHUV/java-base-docker-images" \
      org.label-schema.vcs-type="git" \
      org.label-schema.vcs-url="https://github.com/LREN-CHUV/java-base-docker-images.git" \
      org.label-schema.vcs-ref=$VCS_REF \
      org.label-schema.version="$VERSION" \
      org.label-schema.vendor="LREN CHUV" \
      org.label-schema.license="AGPLv3" \
      org.label-schema.docker.dockerfile="Dockerfile" \
      org.label-schema.schema-version="1.0"