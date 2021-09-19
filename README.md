# CRM API Service

## Running locally

To run the application in a local environment, build the Spring Boot application's image and start a local environment
using [Docker Compose](https://docs.docker.com/compose/).

### Building the CRM service image

Run the following command to run tests, package the application, and build a Docker image using
[Cloud Native Buildpacks](https://spring.io/blog/2020/01/27/creating-docker-images-with-spring-boot-2-3-0-m1).

```shell
mvn spring-boot:build-image
```

### Start the local environment

Run the following docker compose command:

```shell
docker-compose up
```

This will:

1. Start a container for the Spring Boot image built in the previous step.
2. Start a container for the local MariaDB database.
3. Start a container for the local [LocalStack](https://github.com/localstack/localstack) S3 service.

You should now be able to consume the API service locally. The initial admin user should be printed in the console, use
its credentials to consume protected resources in the API using the HTTP Basic authentication scheme.

Use Ctrl-C in your terminal to stop the containers, and then `docker-compose up` to bring them back up. When making
development changes, re-run the initial Maven command to rebuild the image before running `docker-compose up`.

### Local S3 Pre-Signed URLs

When a customer is fetched via the `/v1/customers` resource, a pre-signed URL is generated and returned by
the `photoUrl` field is returned that can be used to fetch the customer's photo without requiring authentication.
Unfortunately, it does not seem to be possible to have the same internal (within the docker compose network) and
external hostnames for the created Localstack container. This means that the host in the generated pre-signed URL is not
valid outside of the internal network. Therefore, to access the photo URL generated locally by Localstack, simply rename
the host from `localstackhost:4566` to `localhost:4566`.

### Resetting the local environment

To clear all locally stored data and reset the environment:

1. Run `docker-compose down`. This will wipe the data stored in the local database.
2. Delete the `./localstack/data` directory that LocalStack creates and uses to persist the local S3 bucket.
3. Run `docker-compose up` to start the services again.
