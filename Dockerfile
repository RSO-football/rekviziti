FROM adoptopenjdk:15-jre-hotspot

RUN mkdir /app

WORKDIR /app

ADD ./api/target/rekviziti-api-1.0-SNAPSHOT.jar /app

EXPOSE 8085

CMD ["java", "-jar", "rekviziti-api-1.0-SNAPSHOT.jar"]
#ENTRYPOINT ["java", "-jar", "image-catalog-api-1.0.0-SNAPSHOT.jar"]
#CMD java -jar image-catalog-api-1.0.0-SNAPSHOT.jar