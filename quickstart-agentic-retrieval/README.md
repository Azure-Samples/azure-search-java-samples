---
page_type: sample
languages:
  - java
name: "Quickstart: Agentic retrieval in Azure AI Search using Java"
description: |
  Learn how to set up an agentic retrieval pipeline in Azure AI Search using Java.
products:
  - azure
  - azure-cognitive-search
urlFragment: java-agentic-retrieval-quickstart
---

# Quickstart: Agentic retrieval in Azure AI Search using Java

![Flask sample MIT license badge](https://img.shields.io/badge/license-MIT-green.svg)

This sample demonstrates the fundamentals of agentic retrieval using Azure AI Search. You create a search index, a knowledge source that targets the index, and a knowledge base that integrates an LLM for query planning and answer synthesis.

## What's in this sample

| File | Description |
|------|-------------|
| `pom.xml` | Project file that defines dependencies and build settings |
| `AgenticRetrievalQuickstart.java` | Creates an index, uploads documents, configures knowledge source and knowledge base, and runs agentic retrieval queries |
| `sample.env` | Environment variable template for configuration |

## Documentation

This sample accompanies [Quickstart: Agentic retrieval using Java](https://learn.microsoft.com/azure/search/search-get-started-agentic-retrieval?pivots=java). Follow the documentation for prerequisites, setup instructions, and detailed explanations.

## Next step

You can learn more about Azure AI Search on the [official documentation site](https://learn.microsoft.com/azure/search).
