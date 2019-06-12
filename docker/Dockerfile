FROM openjdk:8

RUN mkdir /usr/src/microstatemachine

COPY micro-statemachine.jar /usr/src/microstatemachine

WORKDIR /usr/src/microstatemachine

CMD ["java", "-jar", "micro-statemachine.jar"]