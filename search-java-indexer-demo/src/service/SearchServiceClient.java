package service;

import static service.SearchServiceHelper.isSuccessResponse;
import static service.SearchServiceHelper.logMessage;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.net.ssl.HttpsURLConnection;

/**
 * This class is responsible for implementing HTTP operations for creating Index, creating indexer, creating indexer datasource, ...  
 * 
 */
public class SearchServiceClient
{
	private final String _apiKey;
	private final Properties _properties;

	public SearchServiceClient(Properties properties)
	{
		_apiKey = properties.getProperty("SearchServiceApiKey");
		_properties = properties;
	}

	public boolean createIndex() throws IOException
	{
		logMessage("\n Creating Index...");

		URL url = SearchServiceHelper.getCreateIndexURL(_properties);

		HttpsURLConnection connection = SearchServiceHelper.getHttpURLConnection(url, "PUT", _apiKey);
		connection.setDoOutput(true);

		Files.copy(Paths.get("index.json"), connection.getOutputStream());

		System.out.println(connection.getResponseMessage());
		System.out.println(connection.getResponseCode());

		return isSuccessResponse(connection);
	}

	public boolean createDatasource() throws IOException
	{
		logMessage("\n Creating Indexer Data Source...");

		URL url = SearchServiceHelper.getCreateIndexerDatasourceURL(_properties);
		HttpsURLConnection connection = SearchServiceHelper.getHttpURLConnection(url, "PUT", _apiKey);
		connection.setDoOutput(true);

		String dataSourceRequestBody = "{ 'description' : 'Hotels Dataset','type' : '" + _properties.getProperty("DataSourceType")
				+ "','credentials' : " + _properties.getProperty("DataSourceConnectionString")
				+ ",'container' : { 'name' : '" + _properties.getProperty("DataSourceTable") + "' }} ";
		
		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
		outputStreamWriter.write(dataSourceRequestBody);
		outputStreamWriter.close();

		System.out.println(connection.getResponseMessage());
		System.out.println(connection.getResponseCode());

		return isSuccessResponse(connection);
	}

	public boolean createIndexer() throws IOException
	{
		logMessage("\n Creating Indexer...");

		URL url = SearchServiceHelper.getCreateIndexerURL(_properties);
		HttpsURLConnection connection = SearchServiceHelper.getHttpURLConnection(url, "PUT", _apiKey);
		connection.setDoOutput(true);
		
		String indexerRequestBody = "{ 'description' : 'Hotels data indexer', 'dataSourceName' : '" + _properties.get("DataSourceName")
				+ "', 'targetIndexName' : '" + _properties.get("IndexName")
				+ "' ,'parameters' : { 'maxFailedItems' : 10, 'maxFailedItemsPerBatch' : 5, 'base64EncodeKeys': false }}";

		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
		outputStreamWriter.write(indexerRequestBody);
		outputStreamWriter.close();

		System.out.println(connection.getResponseMessage());
		System.out.println(connection.getResponseCode());

		return isSuccessResponse(connection);
	}

	public boolean syncIndexerData() throws IOException, InterruptedException
	{
		// Check indexer status
		logMessage("Synchronization running...");

		boolean running = true;
		URL statusURL = SearchServiceHelper.getIndexerStatusURL(_properties);
		HttpsURLConnection connection = SearchServiceHelper.getHttpURLConnection(statusURL, "GET", _apiKey);

		while (running)
		{
			try {
				if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
				{
					return false;
				}
	
				JsonReader jsonReader = Json.createReader(connection.getInputStream());
				JsonObject responseJson = jsonReader.readObject();
	
				if (responseJson != null)
				{
					JsonObject lastResultObject = responseJson.getJsonObject("lastResult");
	
					if (lastResultObject != null)
					{
						String indexerStatus = lastResultObject.getString("status");
	
						if (indexerStatus.equalsIgnoreCase("inProgress"))
						{
							logMessage("Synchronization running...");
							Thread.sleep(1000);
							statusURL = SearchServiceHelper.getIndexerStatusURL(_properties);
							connection = SearchServiceHelper.getHttpURLConnection(statusURL, "GET", _apiKey);
	
						} else
						{
							running = false;
							logMessage("Synchronized " + lastResultObject.getInt("itemsProcessed") + " rows...");
						}
					}
				}
			} catch(Exception e) {
				// Indexer status is slow to update initially, this loop will help us catch up.
				Thread.sleep(1000);
				statusURL = SearchServiceHelper.getIndexerStatusURL(_properties);
				connection = SearchServiceHelper.getHttpURLConnection(statusURL, "GET", _apiKey);
			}
		}

		return true;
	}
}
