# dwbh-api

This is the backend of the DWBH project

## Fixtures

There're a couple of Gradle tasks to load/clean project's data fixtures:

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
file. In your *build.gradle* you can use the fixtures extension:

```groovy
fixtures {
  cleanDir '...'
  loadDir '...'
  configFile '...'
}