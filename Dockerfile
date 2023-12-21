FROM tomcat:9-jdk17

ADD build/libs/clever_reflection-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/myapp.war

EXPOSE 8080

CMD ["catalina.sh", "run"]
