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

This sample demonstrates how to create, load, and query a search index on Azure Cognitive Search using Java and the [com.azure:azure-search-documents](https://search.maven.org/artifact/com.azure/azure-search-documents) package.

For detailed instructions, see [Quickstart: Create an Azure Cognitive Search index in Java](https://learn.microsoft.com/azure/search/search-get-started-java).

## Contents

| File/folder | Description |
|-------------|-------------|
| `src/main`       | Sample source code. |
| `pom.xml` | Maven 3 project file. |
| `CONTRIBUTING.md` | Guidelines for contributing to this project. |
| `LICENSE` | MIT License. | 
| `README.md`   | This file. |

## Prerequisites

- Azure Cognitive Search. You can [create a search service in the portal](https://docs.microsoft.com/azure/search/search-create-service-portal).

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

You can learn more about Azure Cognitive Search on the [official documentation site](https://docs.microsoft.com/azure/search).
