---
page_type: sample
languages:
  - java
name: "Quickstart: Semantic ranking in Azure AI Search using Java"
description: |
  Demonstrates semantic ranking capabilities to improve search relevance using Azure AI Search.
products:
  - azure
  - azure-cognitive-search
urlFragment: java-semantic-ranking-quickstart
---

# Quickstart: Semantic ranking in Azure AI Search using Java

![Flask sample MIT license badge](https://img.shields.io/badge/license-MIT-green.svg)

This sample demonstrates how to set up semantic ranking. You add a semantic configuration to a search index, and then you add semantic parameters to a query.

## What's in this sample

| File | Description |
|------|-------------|
| `pom.xml` | Project file that defines dependencies and build settings |
| `application.properties` | Configuration file for search service endpoint |
| `SearchConfig.java` | Configuration class for search service connection |
| `GetIndexSettings.java` | Retrieves index schema and semantic configuration |
| `UpdateIndexSettings.java` | Adds semantic configuration to an index |
| `SemanticQuery.java` | Runs basic semantic ranking queries |
| `SemanticQueryWithCaptions.java` | Runs semantic queries with captions and highlights |
| `SemanticAnswer.java` | Returns semantic answers from query results |

## Documentation

This sample accompanies [Quickstart: Semantic ranking using Java](https://learn.microsoft.com/azure/search/search-get-started-semantic?pivots=java). Follow the documentation for prerequisites, setup instructions, and detailed explanations.

## Next step

You can learn more about Azure AI Search on the [official documentation site](https://learn.microsoft.com/azure/search).
