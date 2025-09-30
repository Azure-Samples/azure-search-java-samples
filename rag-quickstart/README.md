---
page_type: sample
languages:
- java
name: Java RAG quickstart for Azure AI Search
description: |
  Learn how to implement Retrieval-Augmented Generation (RAG) patterns using Azure AI Search and Azure OpenAI with the Azure SDK for Java.
products:
- azure
- azure-cognitive-search
- azure-openai
urlFragment: java-rag-quickstart
---

# Java RAG quickstart for Azure AI Search

![Flask sample MIT license badge](https://img.shields.io/badge/license-MIT-green.svg)

Learn how to implement Retrieval-Augmented Generation (RAG) patterns using Azure AI Search for retrieval and Azure OpenAI for generation with Java and the [com.azure:azure-search-documents](https://search.maven.org/artifact/com.azure/azure-search-documents) package.

This readme explains how to configure and run the sample. For details about the code, see [Quickstart: RAG with Azure AI Search](https://learn.microsoft.com/azure/search/search-get-started-rag?pivots=java).

## Prerequisites

- Azure AI Search with a Basic tier or higher (required for vector search in RAG patterns). You can [create a search service in the portal](https://docs.microsoft.com/azure/search/search-create-service-portal).

- Azure OpenAI Service with access to embedding models (text-embedding-ada-002 or text-embedding-3-large) and chat completion models (gpt-35-turbo or gpt-4). You can [request access to Azure OpenAI](https://learn.microsoft.com/azure/ai-services/openai/overview#how-do-i-get-access-to-azure-openai).

- An available region that supports both Azure AI Search vector search and Azure OpenAI. See [region availability](https://learn.microsoft.com/azure/search/vector-search-overview#availability-and-pricing) for current list.

- Install a Java SDK. This sample was tested on the [Microsoft Build of OpenJDK](https://learn.microsoft.com/java/openjdk/install).

- Choose a strategy for building and running the project using [Maven](https://maven.apache.org/). This sample was tested using [Visual Studio Code](https://code.visualstudio.com/) with the [Java extension](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack).

## Set up the sample

1. Clone or download this sample repository.

1. Extract contents if the download is a zip file. Make sure the files are read-write.

1. Get the [service name and admin API key](https://learn.microsoft.com/azure/search/search-get-started-java#get-a-key-and-url) of your search service. You can find this information in the Azure portal.

1. Get your Azure OpenAI endpoint and API key from the Azure portal. Make note of the deployment names for your embedding and chat completion models.

1. In Visual Studio Code or another IDE, create a Java project.

   - Press Ctrl-Shift-P to open the command palette.
   - Search for and then select **Java: Create Java Project**.
   - Select **No Build**.

1. Open **App.java** and configure access to the services by editing the variables for `searchServiceEndpoint`, `adminKey`, `openaiEndpoint`, `openaiApiKey`, and model deployment names.

## Run the sample

1. In Visual Studio Code, press F5 to rebuild the app and run the program in its entirety.

The console should show the Maven build and testing process and the output of program execution:

- Deletes an index of the same name, if one already exists.
- Creates an index with vector field configurations for embeddings using HNSW algorithm.
- Generates embeddings for sample documents using Azure OpenAI embedding models.
- Loads the index with documents and their vector embeddings.
- Demonstrates RAG pattern by performing vector similarity search to retrieve relevant documents and generating natural language responses using Azure OpenAI chat completion models.

Finally, the Maven process should exit with a success message. 

## Next steps

You can learn more about Azure AI Search and RAG patterns on the [official documentation site](https://learn.microsoft.com/azure/search) and [RAG solution overview](https://learn.microsoft.com/azure/search/retrieval-augmented-generation-overview).
