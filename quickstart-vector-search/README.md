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

This sample demonstrates the fundamentals of vector search, including creating a vector index, loading documents with embeddings, and running vector and hybrid queries.

## What's in this sample

| File | Description |
|------|-------------|
| `pom.xml` | Project file that defines dependencies and build settings |
| `application.properties` | Configuration file for search service endpoint |
| `CreateIndex.java` | Creates a search index with vector field configurations |
| `DeleteIndex.java` | Deletes an existing search index |
| `UploadDocuments.java` | Uploads documents with precomputed embeddings |
| `QueryVector.java` | Precomputed sample query vector |
| `Search*.java` | Runs vector, hybrid, and semantic hybrid queries |

## Documentation

This sample accompanies [Quickstart: Vector search using Java](https://learn.microsoft.com/azure/search/search-get-started-vector?pivots=java). Follow the documentation for prerequisites, setup instructions, and detailed explanations.

## Next step

You can learn more about Azure AI Search on the [official documentation site](https://learn.microsoft.com/azure/search).
