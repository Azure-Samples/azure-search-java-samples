---
services: search
platforms: java
author: liamca
---

# Getting Started with Azure Search using Java

This is a sample of how to interact with Azure Search using Java.  Not only does it execute most of the common API requests against Azure Search, but it also implements some of the best practices such as handling retries, etc.  

## Running this sample

To run this sample, you will need to have an Azure Search service and add your Search Service name as well as API key for your Search service tot he App.java file located under \src\main\java\com\microsoft\azure\search\samples\demo.  After that, you should be able to simply load it and run it.  One thing to note is that you will need to have JDK 8 or higher.   

When you run the sample, it will do the following:
* Create an Index - createIndex(indexClient, true);
* Upload documents to an Index - indexData(indexClient);
* Perform a simple search query - searchSimple(indexClient);
* Do a more complex search - searchAllFeatures(indexClient);
* Do an item lookup - lookup(indexClient);
* Perform a suggest query (used for type ahead) - suggest(indexClient);

---

This project has adopted the [Microsoft Open Source Code of Conduct](https://opensource.microsoft.com/codeofconduct/). For more information see the [Code of Conduct FAQ](https://opensource.microsoft.com/codeofconduct/faq/) or contact [opencode@microsoft.com](mailto:opencode@microsoft.com) with any additional questions or comments.