---
page_type: sample
languages:
- java
name: "Quickstart: Keyword search in Azure AI Search using Java"
description: |
  Learn how to create, load, and query an Azure AI Search index using the Azure SDK for Java.
products:
- azure
- azure-cognitive-search
urlFragment: java-quickstart-keyword
---

# Quickstart: Keyword search in Azure AI Search using Java

![Flask sample MIT license badge](https://img.shields.io/badge/license-MIT-green.svg)

Learn how to create, load, and query a search index on Azure AI Search using Java and the [com.azure:azure-search-documents](https://search.maven.org/artifact/com.azure/azure-search-documents) package.

To run this sample, follow the step-by-step instructions in [Quickstart: Full-text search](https://learn.microsoft.com/azure/search/search-get-started-text?pivots=java).

## Prerequisites

- Azure AI Search. You can [create a search service in the portal](https://docs.microsoft.com/azure/search/search-create-service-portal).

- Install a Java SDK. This sample was tested on the [Microsoft Build of OpenJDK](https://learn.microsoft.com/java/openjdk/install).

- Choose a strategy for building and running the project using [Maven](https://maven.apache.org/). This sample was tested using [Visual Studio Code](https://code.visualstudio.com/) with the [Java extension](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack).

## Set up the sample

1. Clone or download this sample repository.

1. Extract contents if the download is a ZIP file. Make sure the files are read-write.

1. Get the name of your search service. You can find the URL on the search service **Overview** page in the Azure portal.

1. Make sure you have permissions to create, load, and query an index: **Search Service Contributor**, **Search Index Data Contributor**, and **Search Index Data Reader**.

1. Run `az login` to sign in to your Azure account using the Azure CLI.

1. In Visual Studio Code or another IDE, create a Java project.

   - Press Ctrl-Shift-P to open the command palette.
   - Search for and then select **Java: Create Java Project**.
   - Select **No Build**.

1. Open **App.java** and configure access to the search service by editing the variable for `searchServiceEndpoint`.

## Run the sample

1. In Visual Studio Code, press F5 to rebuild the app and run the program in its entirety.

The console should show the Maven build and testing process and the output of program execution:

- Deletes an index of the same name, if one already exists.
- Creates an index.
- Loads the index with four hotel documents.
- Executes several  queries.

Finally, the Maven process should exit with a success message.

## Next step

You can learn more about Azure AI Search on the [official documentation site](https://learn.microsoft.com/azure/search).