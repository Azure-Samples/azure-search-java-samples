# Java samples for Azure AI Search

This repository contains Java code samples used in Azure AI Search documentation. Unless noted otherwise, all samples run on the shared (free) pricing tier of an [Azure AI Search service](https://learn.microsoft.com/azure/search/search-create-service-portal).  

+ Use the **main** branch for code samples that call the [Azure AI Search client library for Java](https://docs.microsoft.com/java/api/overview/azure/search-documents-readme).

+ Use the [**java-rest-api**](https://github.com/Azure-Samples/azure-search-java-samples/tree/java-rest-api) branch for older code samples that call the AI Search REST APIs.

| Sample | Description |
|--------|-------------|
| [quickstart-keyword-search](./quickstart-keyword-search/README.md) | Introduces the fundamental tasks of working with a classic search index: create, load, and query. The index is modeled on a subset of the hotels dataset, which is widely used in Azure AI Search samples but reduced in this sample for readability and comprehension. |
| [quickstart-semantic-ranking](./quickstart-semantic-ranking/README.md) | Extends the quickstart through modifications that invoke semantic ranking. This samples adds a semantic configuration to the index and semantic query options that formulate the query and response. |
| [quickstart-vector-search](./quickstart-vector-search/README.md) | Introduces vector search in Azure AI Search. This sample demonstrates how to create, load, and query a vector index. |