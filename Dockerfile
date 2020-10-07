FROM openjdk:8-jdk-alpine

RUN mkdir /opt/E2E-test/
WORKDIR /opt/E2E-test/


# ----
# Install Maven
RUN apk add --no-cache curl tar bash
ARG MAVEN_VERSION=3.3.9
ARG USER_HOME_DIR="/root"
RUN mkdir -p /usr/share/maven && \
curl -fsSL http://apache.osuosl.org/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz | tar -xzC /usr/share/maven --strip-components=1 && \
ln -s /usr/share/maven/bin/mvn /usr/bin/mvn
ENV MAVEN_HOME /usr/share/maven
ENV MAVEN_CONFIG "$USER_HOME_DIR/.m2"
# speed up Maven JVM a bit
ENV MAVEN_OPTS="-XX:+TieredCompilation -XX:TieredStopAtLevel=1"
#ENTRYPOINT ["/usr/bin/mvn"]



# ----
# Install project dependencies and keep sources
# make source folder
RUN mkdir -p /opt/E2E-test/
WORKDIR /opt/E2E-test/
# install maven dependency packages (keep in image)
COPY pom.xml /opt/E2E-test/
RUN mvn -T 1C install && rm -rf target
# copy other source files (keep in image)
COPY src /opt/E2E-test/src


CMD  mvn clean install

