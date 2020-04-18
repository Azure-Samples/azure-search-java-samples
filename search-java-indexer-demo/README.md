---
page_type: sample
languages:
  - java
name: Index Cosmos DB data in Azure Cognitive Search
description: |
  Automate indexing of Cosmos DB data using an Azure Cognitive Search indexer and REST APIs. This example runs as a Java console application.
products:
  - azure
  - azure-cognitive-search
urlFragment: search-java-indexer-demo
---

# Index data from Cosmos DB using Java and Azure Cognitive Search indexers

![Flask sample MIT license badge](https://img.shields.io/badge/license-MIT-green.svg)

This sample shows you how to write a Java client that creates an Azure Cognitive Search indexer, applies that indexer to documents, and runs queries. The sample uses the Search Service REST APIs.

## Contents

| File/folder | Description |
|-------------|-------------|
| `src/`       | Sample source code. |
| `pom.xml` | Maven 3 project file. |
| `README.md`   | This file. |

## Prerequisites

- Install a Java 11 SDK. An option that does not incur support costs is [Zulu](https://docs.microsoft.com/java/azure/jdk/?view=azure-java-stable).
- Choose a strategy for building and running the project using Maven 3:
    - To build and run from the command-line, install [Maven 3](https://maven.apache.org/download.cgi).
    - To use VSCode, install [VSCode](https://code.visualstudio.com/) and the [Maven for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-maven) extension.
    - To use IDEA, install [IntelliJ IDEA](https://www.jetbrains.com/idea/) with default Java options.

## Setup

1. Clone or download this sample repository
1. Follow the steps in the article [Create a search service in the portal](https://docs.microsoft.com/azure/search/search-create-service-portal) to create and configure a search service that uses the "hotels" sample data.
1. Open project in IDE:
    * In VSCode: 
        1. Open folder containing `pom.xml` 
    * In IDEA: 
        1. Use the "Import project" dialog to open `pom.xml`
        1. Select the Java 11 JDK to use with the project
1. Configure access to the search service by editing the `SearchServiceName` and `SearchServiceKey` values in the file [config.properties](src/main/resources/com/microsoft/azure/search/samples/app/config.properties). Note that the `SearchServiceName` should only be the specific name of the service. For instance, if the URL to your service was `https://myservice.search.windows.net`, the `SearchServiceName` would be `myservice`.

## Running the sample

1. Execute Maven goal `verify exec:java`:
    * Command line: run `mvn verify exec:java`.
    * In VSCode: 
        1. Open Command Palette and run "Maven: Execute commands".
        2. Execute the custom goal `verify exec:java`.
    * In IDEA:
        1. Open Maven Panel and Execute Maven Goal `verify exec:java`.

The console should show the Maven build process, the output of the program as it creates an indexer, indexes documents, and executes queries, and should complete with a "BUILD SUCCESS" message.

## Key concepts

The app specifies a number of domain classes whose structure mirrors the index defined in `index.json`. This index shows the use of complex types in Azure Cognitive Search, such as the `Address` and `Room` types. 

The `SearchServiceHelper` class defines methods that help querying a search index. The `SearchServiceClient` uses these methods to accomplish the various tasks -- creating the index and indexer, indexing the documents and blocking until the indexing is complete, and querying the resulting index. 
    
## Next steps

You can learn more about Azure Cognitive Search on the [official documentation site](https://docs.microsoft.com/azure/search).