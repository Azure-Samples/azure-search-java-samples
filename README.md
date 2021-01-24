# Azure Cognitive Search Java Samples repository

The **java-rest-api** branch of the **azure-search-java-samples** repository contains Java samples that call the [Azure Cognitive Search REST APIs](https://docs.microsoft.com/rest/api/searchservice/). 

Prior the release of the [Azure Cognitive Search client library for Java](https://docs.microsoft.com/java/api/overview/azure/search-documents-readme), REST was the only option for building Cognitive Search solutions in Java. This branch preserves older Java-REST samples so that the master branch can move forward with the new client library. 

If you are using the REST APIs in a Java search solution, we encourage you to upgrade to the client library and the Java SDK. In the future, all new sample work and maintenance in the master branch will target the SDK.

## About this branch

In the **java-rest-api** branch, code samples are built on [Java 11](http://openjdk.java.net/projects/jdk/11/) using the [Maven 3+](https://maven.apache.org/) build system. There are three code samples in this branch:

| Sample | Description |
|--------|-------------|
| quickstart | Demonstrates how to create an index, load documents, and run queries using a Java console app. This sample uses a modified version of the hotels demo data set, using just 4 documents and a simplified index.  |
| search-java-getting-started | Demonstrates how to push JSON documents into a search index. This example is similar to the quickstart, but includes a complex type collection (a Rooms collection for each hotel), adds retry logic, and handles errors.  |
| search-java-indexer-demo  | Demonstrates how to call a search indexer in Java code. This example ingests data from a hosted data source in Azure Cosmos DB. As with the other examples, it creates an index, loads documents, and runs queries using a Java console app. |