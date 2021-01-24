---
page_type: sample
languages:
- java
name: "Get started with Azure Cognitive Search in Java"
description: "Demonstrates the com.azure:azure-search-documents package to create, load, and query a search index in a Java console app."
products:
- azure
- azure-cognitive-search
urlFragment: search-java-getting-started
---

# Get started with Azure Cognitive Search using Java

![Flask sample MIT license badge](https://img.shields.io/badge/license-MIT-green.svg)

This is a sample of how to interact with Azure Cognitive Search using Java and the [com.azure:azure-search-documents](https://search.maven.org/artifact/com.azure/azure-search-documents).

## Contents

| File/folder | Description |
|-------------|-------------|
| `src/main`       | Sample source code. |
| `pom.xml` | Maven 3 project file. |
| `CONTRIBUTING.md` | Guidelines for contributing to this project. |
| `LICENSE` | MIT License. | 
| `README.md`   | This file. |
| 

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
1. Configure access to the search service by editing the `ServiceName` and `ApiKey` values in the file [azure_search_config](src/main/resources/azure_search_config). Note that the `ServiceName` should only be the specific name of the service. For instance, if the URL to your service was `https://myservice.search.windows.net`, the `ServiceName` would be `myservice`.

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
* Executes simple and advanced queries.
* Looks up a specific document.
* Performs a suggest query, which is used in type-ahead use-cases.

Finally, the Maven process should exit with a success message. 

## Key concepts

This sample uses the [com.azure:azure-search-documents](https://search.maven.org/artifact/com.azure/azure-search-documents) package. The app specifies a number of domain classes whose structure mirrors the index of the Hotels dataset. This index shows the use of complex types in Azure Cognitive Search, such as the `Address` and `Room` types. 

You can review [package readme](https://docs.microsoft.com/java/api/overview/azure/search-documents-readme) for an overview and links to [source code](https://github.com/Azure/azure-sdk-for-java/tree/azure-search-documents_11.1.2/sdk/search/azure-search-documents/src), [API reference](https://azure.github.io/azure-sdk-for-java/), and [API samples](https://github.com/Azure/azure-sdk-for-java/tree/master/sdk/search/azure-search-documents/src/samples/java/com/azure/search/documents).

The Java client library provides **SearchIndexAsyncClient** to create the index, and **SearchAsyncClient** to load and query the index. 

Besides search index creation and queries, the sample demonstrates suggesters, which offers a completed hotel name based on partial term input. 

## Next steps

You can learn more about Azure Cognitive Search on the [official documentation site](https://docs.microsoft.com/azure/search).
