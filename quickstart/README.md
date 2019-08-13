---
page_type: sample
languages:
  - java
  - rest
name: Quickstart in Java
description: |
  Learn basic steps for creating, loading, and querying an Azure Search index in a Java console application.
products:
  - azure
  - azure-search
urlFragment: java-sample-quickstart
---
# Quickstart sample for Azure Search using Java

![Flask sample MIT license badge](https://img.shields.io/badge/license-MIT-green.svg)

This sample shows you how to write a Java client that accesses Azure Search, creates an index, loads documents, and runs queries.  

## Contents

| File/folder | Description |
|-------------|-------------|
| `src`       | Sample source code. |
| `.gitignore` | Define what to ignore at commit time. |
| `CONTRIBUTING.md` | Guidelines for contributing to the sample. |
| `pom.xml` | Maven 3 project file. |
| `LICENSE`   | The license for the sample. |

## Prerequisites

- [IntelliJ IDEA](https://www.jetbrains.com/idea/)
- Java 11 SDK. An option that does not incur support costs is [Zulu](https://docs.microsoft.com/java/azure/jdk/?view=azure-java-stable).
- [Azure Search service](https://docs.microsoft.com/azure/search/search-create-service-portal)
- Optional: If you want the option of building outside of IDEA, install [Maven 3](https://maven.apache.org/download.cgi) for the command-line.

## Setup

1. Clone or download this sample repository.
1. If the download is a zip file, unzip the file. Make sure the files are read-write.

## Running the sample

1. Use IDEA's "Import Project..." option to open the `pom.xml` file.
1. Make sure that the values in [azure_search_config](src/main/resources/com/microsoft/azure/search/samples/app/azure_search_config) are set to those in your Azure Search service.
1. From IDEA's Maven panel, execute the Maven goal `verify exec:java`.
1. IDEA's Run console should show the Maven build process, the output of the program as it creates an index, uploads documents, and executes queries, and should complete with a "BUILD SUCCESS" message.

## Key concepts

Communication with the Azure Search service is handled by the [SearchIndexClient](src/main/java/com/microsoft/azure/search/samples/client/SearchIndexClient.java) class.

The index definition is specified in [index.json](src/main/resources/com/microsoft/azure/search/samples/service/index.json) and uploaded document data is defined in [hotels.json](src/main/resources/com/microsoft/azure/search/samples/service/hotels.json)

The [App class](src/main/java/com/microsoft/azure/search/samples/app/App.java) contains the main method that makes calls to the SearchIndexClient to create the index, upload the documents and perform queries.

## Next steps

You can learn more about Azure Search on the [official documentation site](https://docs.microsoft.com/azure/search).