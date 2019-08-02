---
topic: sample
languages:
  - java
  - rest
name: Azure Search Java samples
description: |
  Find Java samples for Azure Search in this repo.
products:
  - azure
  - azure-search
---

# Azure Search Java Samples repository

This repository contains Java sample code used in Azure Search quickstarts, tutorials and examples.

All Java samples use the [Azure Search REST API](https://docs.microsoft.com/rest/api/searchservice/) (currently, there is no Java SDK) and are built on [Java 11](http://openjdk.java.net/projects/jdk/11/) using the [Maven 3+](https://maven.apache.org/) build system.

## Quickstart in Java

Demonstrates how to create an index, load documents, and run queries using a Java console app. This sample uses a modified version of the hotels demo data set, using just 4 documents and a simplified index.

## Get started with Azure Search using Java

Demonstrates how to push JSON documents into an Azure Search index. This example is similar to the quickstart, but includes a complex type collection (a Rooms collection for each hotel), adds retry logic, and handles errors.

## Cosmos DB indexer sample

Demonstrates how to call an Azure Search indexer in Java code. This example ingests data from a hosted data source in Azure Cosmos DB. As with the other examples, it creates an index, loads documents, and runs queries using a Java console app.