[![Travis](https://travis-ci.org/dont-worry-be-happy/dwbh-api.svg?branch=master)](https://travis-ci.org/dont-worry-be-happy/dwbh-api)

# Don't Worry Be Happy

![dwbh](etc/site/imgs/dwbh.png)

**Don't Worry Be Happy** is a web application that tries to measure the happiness of a given team periodically by
asking for a level of happiness between 1 and 5 (being 1 the saddest scenario and 5 the happiest). This repository
hosts the backend of the DWBH project. Cool!

## Getting Started

If you'd like to start working on the project. The following sections show you how to
do it.

### Requirements

What things you need to run the software:

* [Docker](https://docs.docker.com/)
* [Docker Compose](https://docs.docker.com/compose/)
* [JDK 11](https://openjdk.java.net/)

To install JDK11 you can also use [SDKMAN](https://sdkman.io/) a great tool to manage JVM related SDKs.

### Building

To build the code just execute the task **build**:

```shell
./gradlew build
```

Building the code implies running:

- static analysis
- code style checking
- test execution
- code coverage generation


If you'd like to enable continuous build execute:

```shell
./gradlew build -t
```

Continuous build means that Gradle does not exit and will re-execute tasks
when task file inputs change

### Running the tests

To execute only tests do:

```shell
./gradlew test
```

If you'd like to test a specific test or set of tests you can use the **--tests** option to
match the name of the test class/es you want to execute:

```shell
./gradlew test --tests *Repository*
```

There are more ways of executing tests, you can
take a look at this [blog entry](https://blog.jdriven.com/2017/10/run-one-or-exclude-one-test-with-gradle/)

### Running style checking

The project uses [Spotless](https://github.com/diffplug/spotless) for code style checking.
To make sure contributors follow the style rules the project uses :

```shell
./gradlew spotlessJavaCheck
```

If you find any error you can format the conflicted files automatically by
executing:

```shell
./gradlew spotlessJavaApply
```

### Running static analysis

The project uses [PMD](https://pmd.github.io/) as static analysis checker. To execute **PMD** for main classes do:

```shell
./gradlew pmdMain
```

And for test classes:

```shell
./gradlew pmdTest
```

### Running bug finding

This project uses [Spotbugs](https://github.com/spotbugs) as tool for spotting possible new bugs. To execute
**Spotbugs** just execute:

```shell
./gradlew spotbugsMain
```

### Running dev environment

When working on development we'll need a PostgreSQL database and sometimes the front end. There's a
`docker-compose` file to bootstrap both systems and make them work with the current back development.

Go  to **your project's** `etc/docker` (dwbh-api/etc/docker) folder and execute:

`docker-compose up -d`

Both the database and the front end will be installed and bootstrapped. Now you can execute:

```shell
./gradlew run
```

And the api will use the database register in the docker-compose.yml.

**IMPORTANT**: Remember that the database is using a docker volume, if you would like to get rid of database data
you can do it by executing:

```shell
docker volume rm NAME-OF-THE-VOLUME
```

### Loading Fixtures

If it's the first time you are running the application, you may want to load some fixtures
to the database first. There're a couple of Gradle tasks to
load/clean project's data fixtures:

- fixtures-load
- fixtures-clean

`fixtures-load` loops over the sql files in 'fixtures/load' and executes them
against the database configured in `fixtures/fixtures.yaml`. The order of
execution depends on the file name.

```shell
./gradlew fixtures-load
```

`fixtures-clean` loops over the sql files in 'fixtures/clean' and executes them
against the database configured in `fixtures/fixtures.yaml`. The order of
execution also depends on the file name.

```shell
./gradlew fixtures-clean
```

You can change where the load/clean files are as well as the database configuration
file. In your **build.gradle** you can use the fixtures extension:

```groovy
fixtures {
  cleanDir '...'
  loadDir '...'
  configFile '...'
}
```

## Technologies

The most important technologies you need to be aware of to work
on this project are:

### Runtime

* [Micronaut](https://micronaut.io/) - A JVM-based micro-framework
* [Jooq](https://www.jooq.org/) - Persistence framework
* [AWS API](https://aws.amazon.com/sdk-for-java/) - For AWS services (e.g. mailing)

### Testing

* [JUnit 5](https://junit.org/junit5/) - Java testing library
* [Test containers](https://www.testcontainers.org/) For testing integrations

### Building

* [Gradle](https://gradle.org/) - Building tool

## How to contribute

Please see [CONTRIBUTING.md](CONTRIBUTING.md) for contribution guidelines.

## Versioning

TODO

## License

TODO

## Acknowledgments

Thanks to [Kaleidos Open Source](https://kaleidos.net/) to make this project possible.