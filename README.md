## Build Projects

```
mvn clean install
```

## Go to app folder

```
cd ~/app
```

## Run Hello

java -Dport=8080 -Dserver.codebase="file:hello-1.0-SNAPSHOT.jar" -jar hello-1.0-SNAPSHOT.jar

## Run Client

java -Djava.security.manager -Djava.security.policy=server.policy -jar client-1.0-SNAPSHOT.jar 


