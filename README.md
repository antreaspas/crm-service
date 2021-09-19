# CRM API Service

A Spring Boot API service which allows management of users and customers of a CRM system. Persists customer and user
data to MariaDB, and stores customers' photos in AWS S3.

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

## Improvements

List of additional tasks that can be done to improve the project:

- Provision AWS infrastructure automatically using [Terraform](https://www.terraform.io/) or
  the [AWS CDK](https://docs.aws.amazon.com/cdk/latest/guide/getting_started.html). Will automate provision of the AWS
  RDS MariaDB instance, the ECR image repository, and the ECS cluster, as well as an IAM user for the application to
  have S3 permissions, and an IAM user for CI/CD.
- Cache customer photo IDs in the PhotoService. Currently, the application generates a pre-signed URL for each request
  to the Customers resource which returns CustomerResponse DTOs. This can be optimised by caching the URL against the
  photo ID to avoid unnecessary calls to AWS S3. Care should be taken to evict cached URLs when a customer updates their
  photo or is deleted. An in-memory cache (e.g. using [Caffeine](https://github.com/ben-manes/caffeine)) should be
  sufficient.
- Migrate from basic auth to token authentication. Currently, the application supports HTTP Basic authentication which
  means the user needs to send their credentials for every request they make. Instead, a better approach will be to
  generate tokens for a login request (could be JWT or opaque depending on implementation), and the user can then use
  the token to authenticate further requests. Two possible approaches to implement this are leveraging AWS Cognito for
  user management, or implementing a login endpoint which generates the token. The latter could also be implemented in a
  separate auth microservice.
- AwsS3Client integration tests with [testcontainers + Localstack](https://www.testcontainers.org/modules/localstack/)