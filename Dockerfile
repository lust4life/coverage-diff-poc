ARG OPENJDK_TAG=11
FROM openjdk:${OPENJDK_TAG} AS build

ARG SCALA_VERSION=2.13.3
# install scala
RUN \
 curl -L https://downloads.lightbend.com/scala/$SCALA_VERSION/scala-$SCALA_VERSION.tgz | tar xfz - -C /root/ && \
 echo >> /root/.bashrc && \
 echo "export PATH=~/scala-$SCALA_VERSION/bin:$PATH" >> /root/.bashrc

ENV APPROOT="/app" \
   JAR_DIR="/app-jar"

WORKDIR $APPROOT
COPY . $APPROOT

# build
RUN \
 cd ./spring-boot-service-demo/aService/ && \
 ./mvnw clean package && \
 cp target/*.jar ${JAR_DIR}


# result image
FROM openjdk:${OPENJDK_TAG}-jre
COPY --from=build $JAR_DIR $APPROOT
RUN addgroup --system spring && adduser --system --group spring spring
USER spring:spring

WORKDIR $APPROOT
#ENTRYPOINT ["java","-jar","/app.jar"]
