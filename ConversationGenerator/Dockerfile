FROM gradle:8.11.1-jdk17-corretto AS BUILD_STAGE
COPY --chown=gradle:gradle . /home/gradle
RUN gradle installDist --info

FROM openjdk:17-jdk-slim
ENV APP_NAME=ConversationGenerator
ENV APP_HOME=/app
WORKDIR $APP_HOME
COPY --from=BUILD_STAGE /home/gradle/build/install/$APP_NAME .
CMD bin/${APP_NAME}