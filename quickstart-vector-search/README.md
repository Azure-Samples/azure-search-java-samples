---
page_type: sample
languages:
- java
name: "Quickstart: Vector search in Azure AI Search using Java"
description: |
  Demonstrates vector search capabilities using Azure AI Search with HNSW algorithm.
products:
- azure
- azure-cognitive-search
urlFragment: java-vector-quickstart
---

# Quickstart: Vector search in Azure AI Search using Java

![Flask sample MIT license badge](https://img.shields.io/badge/license-MIT-green.svg)

Vector search capabilities using Azure AI Search with the HNSW algorithm. This Java sample demonstrates how to create an index with vector field configurations, upload documents with pre-computed embeddings to the index, and execute vector similarity searches and hybrid queries. Requires a search service on any pricing tier, though Basic or higher is recommended for larger data files.

This sample is built on Java 21 (LTS) from the [Microsoft Build of OpenJDK](https://learn.microsoft.com/java/openjdk/install) using the [Maven](https://maven.apache.org/) build system. This sample has dependencies on the [Azure AI Search](https://search.maven.org/artifact/com.azure/azure-search-documents) and [Azure Identity](https://search.maven.org/artifact/com.azure/azure-identity) client libraries.

To run this sample, follow the step-by-step instructions in [Quickstart: Vector search](https://learn.microsoft.com/azure/search/search-get-started-vector?tabs=keyless&pivots=java).

## Next step

You can learn more about Azure AI Search and vector search on the [official documentation site](https://learn.microsoft.com/azure/search) and [vector search overview](https://learn.microsoft.com/azure/search/vector-search-overview).
