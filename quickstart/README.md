---
page_type: sample
languages:
- java
name: Quickstart in Java
description: |
  Learn how to create, load, and query an Azure AI Search index using the Azure SDK for Java.
products:
- azure
- azure-cognitive-search
urlFragment: java-quickstart
---

# Java quickstart for Azure AI Search

![Flask sample MIT license badge](https://img.shields.io/badge/license-MIT-green.svg)

Learn how to create, load, and query a search index on Azure AI Search using Java and the [com.azure:azure-search-documents](https://search.maven.org/artifact/com.azure/azure-search-documents) package.

This readme explains how to configure and run the sample. For details about the code, see [Quickstart: Create search index in Java](https://learn.microsoft.com/azure/search/search-get-started-java).

## Prerequisites

- Azure AI Search. You can [create a search service in the portal](https://docs.microsoft.com/azure/search/search-create-service-portal).

- Install a Java SDK. This sample was tested on the [Microsoft Build of OpenJDK](https://learn.microsoft.com/java/openjdk/ins).

- Choose a strategy for building and running the project using [Maven](https://maven.apache.org/). This sample was tested using [Visual Studio Code](https://code.visualstudio.com/) with the [Java extension](https://vscode.trafficmanager.net/docs/java/extensions).

## Set up the sample

1. Clone or download this sample repository.

1. Extract contents if the download is a zip file. Make sure the files are read-write.

1. Get the [service name and admin API key](https://learn.microsoft.com/azure/search/search-get-started-java#get-a-key-and-url) of your service. You can find this information in the Azure portal.

1. In Visual Studio Code or another IDE, create a Maven project.

1. Configure access to the search service by editing the variables for `searchServiceEndpoint` and `adminKey` in the **App.java** class.

## Run the sample

1. In Visual Studio Code, press F5 to rebuild the app and run the program in its entirety.

The console should show the Maven build and testing process and the output of program execution:

- Deletes an index of the same name, if one already exists.
- Creates an index.
- Loads the index with four hotel documents.
- Executes several  queries.

Finally, the Maven process should exit with a success message. 

## Next steps

You can learn more about Azure AI Search on the [official documentation site](https://docs.microsoft.com/azure/search).
