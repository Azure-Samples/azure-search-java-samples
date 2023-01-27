# Java samples for Azure Cognitive Search

This repository contains Java code samples used in Azure Cognitive Search documentation. Unless noted otherwise, all samples run on the shared (free) pricing tier of an [Azure Cognitive Search service](https://learn.microsoft.com/azure/search/search-create-service-portal).  

+ Use the **main** branch for code samples that call the [Azure Cognitive Search client library for Java](https://docs.microsoft.com/java/api/overview/azure/search-documents-readme).

+ Use the [**java-rest-api**](https://github.com/Azure-Samples/azure-search-java-samples/tree/java-rest-api)  branch for older code samples that call the Cognitive Search REST APIs.

| Sample | Description |
|--------|-------------|
| quickstart | "Day One" introduction to the fundamental tasks of working with a search index: create, load, and query. This Java console app uses a subset of the hotels demo data set, using just 4 documents as a test case. This sample is built on the [Microsoft Build of OpenJDK](https://learn.microsoft.com/java/openjdk/ins) using the [Maven](https://maven.apache.org/) build system. This sample has a dependency on the [com.azure:azure-search-documents](https://search.maven.org/artifact/com.azure/azure-search-documents) package.|