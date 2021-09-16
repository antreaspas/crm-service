# CRM API Service

## Running locally

Run a maven build to test and package the application in a JAR file:

```shell
mvn clean install --file pom.xml
```

Then run the following docker compose commands:

```shell
docker-compose build
docker-compose up
```

This will:

1. Build a docker image for the Spring Boot application using the JAR file produced in the latest maven build.
2. Pull the MariaDB image.
3. Start docker containers for both services locally. The Spring Boot application may have to restart once or twice due
   to the database not properly initialising, this needs further investigating as the `depends_on` field in the compose
   file does not seem to wait for the database service to actually finish initialising.

You should now be able to consume the API service locally. The initial admin user should be printed in the console, use
its credentials to consume protected resources in the API using the HTTP Basic authentication scheme.

Use Ctrl-C in your terminal to stop the containers, and then `docker-compose up` to bring them back up. When making
development changes, re-run the initial 3 commands to rebuild the JAR and the image.

To clear the database of any data you added locally, run `docker-compose down` to tear down the containers, and then the
three initial commands.