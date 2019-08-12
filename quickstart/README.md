---
page_type: sample
languages:
  - java
  - rest
name: Quickstart in Java
description: "Learn basic steps for creating, loading, and querying an Azure Search index in a Java console application."
products:
  - azure
  - azure-search
urlFragment: java-sample-quickstart
---
# Quickstart sample for Azure Search using Java

![Flask sample MIT license badge](https://img.shields.io/badge/license-MIT-green.svg)

This sample shows you how to write a Java client that accesses Azure Search, creates an index, loads documents, and runs queries. This code sample uses the Azure Search REST APIs. 

## Contents

| File/folder | Description |
|-------------|-------------|
| `src`       | Sample source code. |
| `.gitignore` | Define what to ignore at commit time. |
| `pom.xml` | Maven 3 project file. |
| `LICENSE`   | The license for the sample. |

## Prerequisites

- Install [IntelliJ IDEA](https://www.jetbrains.com/idea/)
- Install a Java 11 SDK. An option that does not incur support costs is [Zulu](https://docs.microsoft.com/java/azure/jdk/?view=azure-java-stable).
- Optional: If you want the option of building outside of IDEA, install [Maven 3](https://maven.apache.org/download.cgi) for the command-line.

## Setup

1. Clone or download this sample repository
1. Follow the steps in the article [Create an Azure Search service in the portal](https://docs.microsoft.com/azure/search/search-create-service-portal) to create and configure an Azure Search service.
1. Use IDEA's "Import Project..." dialog to open the `pom.xml` file.
1. Configure access to the search service by editing the file [azure_search_config](src/main/resources/azure_search_config). 

## Running the sample

1. Make sure that the values in [azure_search_config](src/main/resources/azure_search_config) are set to those in your Azure Search service.
1. From IDEA's Maven panel, execute the Maven goal `verify exec:java`.
1. IDEA's Run console should show the Maven build process, the output of the program as it creates an index, indexes documents, and executes queries, and should complete with a "BUILD SUCCESS" message.

## Key concepts

The app uses [Jackson data-binding](https://github.com/FasterXML/jackson-databind) and [Google AutoValue](https://github.com/google/auto/tree/master/value) throughout the codebase to define classes such as [Hotel](src/main/java/com/microsoft/demos/azure/search/samples/demo/AzureSearchConfig.java) and [Address](src/main/java/com/microsoft/demos/azure/search/samples/demo/Address.java) whose structure corresponds to the fields in the search service. It uses the same libraries to deserialize the configuration values for [AzureSearchConfig](src/main/java/com/microsoft/demos/azure/search/samples/demo/AzureSearchConfig.java) and for helper classes such as [IndexDefinition](src/main/java/com/microsoft/demos/azure/search/samples/index/IndexDefinition.java) and [SearchResult](src/main/java/com/microsoft/demos/azure/search/samples/results/SearchResult.java).

Communication with the Azure Search service is handled by the [SearchIndexClient](src/main/java/com/microsoft/demos/azure/search/samples/client/SearchIndexClient.java) class. 
    
## Next steps

You can learn more about Azure Search on the [official documentation site](https://docs.microsoft.com/azure/search).