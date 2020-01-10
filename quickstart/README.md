---
page_type: sample
languages:
  - java
  - rest
name: Quickstart in Java
description: "Learn basic steps for creating, loading, and querying an Azure Cognitive Search index in a Java console application."
products:
  - azure
  - azure-cognitive-search
urlFragment: java-sample-quickstart
---
# Quickstart sample for Azure Cognitive Search using Java

![Flask sample MIT license badge](https://img.shields.io/badge/license-MIT-green.svg)

This sample shows you how to write a Java client that accesses Azure Cognitive Search, creates an index, loads documents, and runs queries. This code sample uses the Search Service REST APIs.

## Contents

| File/folder | Description |
|-------------|-------------|
| `src`       | Sample source code. |
| `.gitignore` | Define what to ignore at commit time. |
| `CONTRIBUTING.md` | Guidelines for contributing to the sample. |
| `pom.xml` | Maven 3 project file. |
| `LICENSE`   | The license for the sample. |

## Prerequisites

- Install a Java 11 SDK. An option that does not incur support costs is [Zulu](https://docs.microsoft.com/java/azure/jdk/?view=azure-java-stable).
- Choose a strategy for building and running the project using Maven 3:
    - To build and run from the command-line, install [Maven 3](https://maven.apache.org/download.cgi).
    - To use VSCode, install [VSCode](https://code.visualstudio.com/) and the [Maven for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-maven) extension.
    - To use IDEA, install [IntelliJ IDEA](https://www.jetbrains.com/idea/) with default Java options.

## Setup

1. Clone or download this sample repository.
1. Follow the steps in the article [Create a search service in the portal](https://docs.microsoft.com/azure/search/search-create-service-portal) to create and configure a search service that uses the "hotels" sample data.
1. Open project in IDE:
    * In VSCode: 
        1. Open folder containing `pom.xml` 
    * In IDEA: 
        1. Use the "Import project" dialog to open `pom.xml`
        1. Select the Java 11 JDK to use with the project
1. Configure access to the search service by editing the `ServiceName` and `ApiKey` values in the file [config.properties](src/main/resources/com/microsoft/azure/search/samples/app/config.properties). Note that the `ServiceName` should only be the specific name of the service. For instance, if the URL to your service was `https://myservice.search.windows.net`, the `ServiceName` would be `myservice`.

## Running the sample

1. Execute Maven goal `verify exec:java`:
    * Command line: run `mvn verify exec:java`.
    * In VSCode: 
        1. Open Command Palette and run "Maven: Execute commands".
        2. Execute the custom goal `verify exec:java`.
    * In IDEA:
        1. Open Maven Panel and Execute Maven Goal `verify exec:java`.

The console should show the Maven build and testing process and the output of the program as it:
 
* Creates an index, possibly deleting the existing index.
* Runs the indexing task.
* Loads documents with hotel data.
* Executes simple queries.

Finally, the Maven process should exit with a success message. 

## Key concepts

Communication with the search service is handled by the [SearchIndexClient](src/main/java/com/microsoft/azure/search/samples/service/SearchIndexClient.java) class.

The index definition is specified in [index.json](src/main/resources/com/microsoft/azure/search/samples/service/index.json) and uploaded document data is defined in [hotels.json](src/main/resources/com/microsoft/azure/search/samples/service/hotels.json)

The [App class](src/main/java/com/microsoft/azure/search/samples/app/App.java) contains the main method that makes calls to the SearchIndexClient to create the index, upload the documents and perform queries.

## Next steps

You can learn more about Azure Cognitive Search on the [official documentation site](https://docs.microsoft.com/azure/search).