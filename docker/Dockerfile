FROM openjdk:8

RUN mkdir /usr/src/microstatemachine

COPY micro-statemachine.jar /usr/src/microstatemachine

WORKDIR /usr/src/microstatemachine

## THE LIFE SAVER
ADD https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh wait-for-it.sh
RUN chmod +x wait-for-it.sh

## Launch the wait tool and then your application
CMD ["./wait-for-it.sh", "--timeout=0", "mongo:27017", "--", "java", "-jar", "micro-statemachine.jar"]