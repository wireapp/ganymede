FROM adoptopenjdk/openjdk11:jdk-11.0.6_10-alpine AS build
LABEL description="Wire Digital Signatures Backend"
LABEL project="wire-digitail-signatures:backend"

ENV PROJECT_ROOT /src
WORKDIR $PROJECT_ROOT

# Copy gradle settings
COPY build.gradle.kts settings.gradle.kts gradle.properties gradlew $PROJECT_ROOT/
# Make sure gradlew is executable
RUN chmod +x gradlew
# Copy gradle specification
COPY gradle $PROJECT_ROOT/gradle
# Download gradle
RUN ./gradlew --version
# download and cache dependencies
RUN ./gradlew resolveDependencies

# Copy project and build
COPY . $PROJECT_ROOT
RUN ./gradlew fatJar

# Runtime
FROM adoptopenjdk/openjdk11:jre-11.0.6_10-alpine

ENV APP_ROOT /app
WORKDIR $APP_ROOT

# Obtain built from the base
COPY --from=build /src/build/libs/app.tar $APP_ROOT/

# Extract executables
RUN mkdir $APP_ROOT/run
RUN tar -xvf app.tar --strip-components=1 -C $APP_ROOT/run

# create version file
ARG release_version=development
ENV RELEASE_FILE_PATH=$APP_ROOT/run/release.txt
RUN echo $release_version > $RELEASE_FILE_PATH

EXPOSE 8080
ENTRYPOINT ["/bin/sh", "-c", "/app/run/bin/digital-signatures"]
