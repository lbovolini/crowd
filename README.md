## Build Projects

```
mvn clean install
```

## Go to app folder

```
cd ~/app
```

## Run Hello

java -jar hello-1.0-SNAPSHOT.jar

## Run Client

java -Djava.security.manager -Djava.security.policy=server.policy -Dserver.codebase="file:hello-1.0-SNAPSHOT.jar" -jar client-1.0-SNAPSHOT.jar 


