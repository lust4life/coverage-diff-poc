# syntax=docker/dockerfile:experimental

ARG OPENJDK_TAG=11
FROM openjdk:${OPENJDK_TAG} AS build

ARG APPROOT="/app"
ARG SCALA_VERSION=2.13.3

# for mill couriser dependency resolver
ENV COURSIER_REPOSITORIES="ivy2Local|https://maven.aliyun.com/repository/public"

# install scala
RUN \
   curl -L https://downloads.lightbend.com/scala/$SCALA_VERSION/scala-$SCALA_VERSION.tgz | tar xfz - -C /root/ && \
   echo >> /root/.bashrc && \
   echo "export PATH=~/scala-$SCALA_VERSION/bin:$PATH" >> /root/.bashrc

COPY . $APPROOT

# using aliyun mirror in maven
COPY ./docker-compose/settings.xml /root/.m2/

# build detector example
WORKDIR $APPROOT
# preload mill into a layer
RUN --mount=type=cache,target=/root/.cache ./mill --version
RUN --mount=type=cache,target=/root/.cache ./mill example.assembly

# build aService (the spring boot example)
WORKDIR $APPROOT/spring-boot-service-demo/aService
RUN --mount=type=cache,target=/root/.m2/repository ./mvnw clean package


# result image
FROM openjdk:${OPENJDK_TAG}-jre

ARG APPROOT="/app/"

COPY --from=build ["$APPROOT/poc/test/resources/jacocoagent.jar","$APPROOT/out/example/assembly/dest/out.jar","$APPROOT/spring-boot-service-demo/aService/target/aService-0.0.1-SNAPSHOT.jar","$APPROOT"] 
RUN adduser --system spring --group
USER spring:spring

WORKDIR $APPROOT
