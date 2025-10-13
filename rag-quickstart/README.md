---
page_type: sample
languages:
- java
name: Java RAG quickstart for Azure AI Search
description: |
  Demonstrates the Retrieval-Augmented Generation (RAG) pattern using Azure AI Search for retrieval and Azure OpenAI for generation.
products:
- azure
- azure-cognitive-search
- azure-openai
urlFragment: java-rag-quickstart
---

# Java RAG quickstart for Azure AI Search

![Flask sample MIT license badge](https://img.shields.io/badge/license-MIT-green.svg)

Retrieval-Augmented Generation (RAG) pattern using Azure AI Search for retrieval and Azure OpenAI for generation. This Java sample demonstrates how to query a search index using semantic search and use retrieved documents to generate natural language responses via a chat completion model. Requires Azure OpenAI Service with a chat completion model (such as gpt-4o or gpt-4o-mini). This sample is built on Java 21 (LTS) from the [Microsoft Build of OpenJDK](https://learn.microsoft.com/java/openjdk/install) using the [Maven](https://maven.apache.org/) build system. This sample has dependencies on the [Azure AI Search](https://search.maven.org/artifact/com.azure/azure-search-documents), [Azure OpenAI](https://search.maven.org/artifact/com.azure/azure-ai-openai), and [Azure Identity](https://search.maven.org/artifact/com.azure/azure-identity) client libraries.

**To run this sample:** Follow the step-by-step instructions in [Quickstart: Generative search (RAG) using grounding data from Azure AI Search](https://learn.microsoft.com/azure/search/search-get-started-rag?pivots=java).

## Next steps

You can learn more about Azure AI Search and RAG patterns on the [official documentation site](https://learn.microsoft.com/azure/search) and [RAG overview](https://learn.microsoft.com/azure/search/retrieval-augmented-generation-overview).
