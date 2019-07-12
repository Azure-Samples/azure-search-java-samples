package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Formatter;
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;

/**
 * Helper class that contains static helper methods. 
 * Also it contains static/constant Strings like Index Name, Indexer Name, Data source Name, ...  
 * 
 */
public class SearchServiceHelper
{
	private static final String _searchURL = "https://%s.search.windows.net/indexes/%s/docs?api-version=%s&search=%s&searchMode=all";
	private static final String _createIndexURL = "https://%s.search.windows.net/indexes/%s?api-version=%s";
	private static final String _createIndexerDatasourceURL = "https://%s.search.windows.net/datasources/%s?api-version=%s";
	private static final String _createIndexerURL = "https://%s.search.windows.net/indexers/%s?api-version=%s";
	private static final String _runIndexerURL = "https://%s.search.windows.net/indexers/%s/run?api-version=%s";
	private static final String _getIndexerStatusURL = "https://%s.search.windows.net/indexers/%s/status?api-version=%s";
	
	public static URL getSearchURL(Properties properties, String query) throws MalformedURLException
	{
		Formatter strFormatter = new Formatter();
		strFormatter.format(_searchURL, properties.get("SearchServiceName"), properties.get("IndexName"), properties.get("ApiVersion"), query);
		String url = strFormatter.out().toString();
		strFormatter.close();

		return new URL(url);
	}
	
	public static URL getCreateIndexURL(Properties properties) throws MalformedURLException
	{

		Formatter strFormatter = new Formatter();
		strFormatter.format(_createIndexURL, properties.get("SearchServiceName"), properties.get("IndexName"), properties.get("ApiVersion"));
		String url = strFormatter.out().toString();
		strFormatter.close();

		return new URL(url);
	}

	public static URL getCreateIndexerURL(Properties properties) throws MalformedURLException
	{
		Formatter strFormatter = new Formatter();
		strFormatter.format(_createIndexerURL, properties.get("SearchServiceName"), properties.get("IndexerName"), properties.get("ApiVersion"));
		String url = strFormatter.out().toString();
		strFormatter.close();

		return new URL(url);
	}

	public static URL getCreateIndexerDatasourceURL(Properties properties) throws MalformedURLException
	{
		Formatter strFormatter = new Formatter();
		strFormatter.format(_createIndexerDatasourceURL, properties.get("SearchServiceName"), properties.get("DataSourceName"), properties.get("ApiVersion"));
		String url = strFormatter.out().toString();
		strFormatter.close();

		return new URL(url);
	}

	public static URL getRunIndexerURL(Properties properties) throws MalformedURLException
	{
		Formatter strFormatter = new Formatter();
		strFormatter.format(_runIndexerURL, properties.get("SearchServiceName"), properties.get("IndexerName"), properties.get("ApiVersion"));
		String url = strFormatter.out().toString();
		strFormatter.close();

		return new URL(url);
	}

	public static URL getIndexerStatusURL(Properties properties) throws MalformedURLException
	{
		Formatter strFormatter = new Formatter();
		strFormatter.format(_getIndexerStatusURL, properties.get("SearchServiceName"), properties.get("IndexerName"), properties.get("ApiVersion"));
		String url = strFormatter.out().toString();
		strFormatter.close();

		return new URL(url);
	}

	public static HttpsURLConnection getHttpURLConnection(URL url, String method, String apiKey) throws IOException
	{
		HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
		connection.setRequestMethod(method);
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("api-key", apiKey);

		return connection;
	}

	public static void logMessage(String message)
	{
		System.out.println(message);
	}

	public static boolean isSuccessResponse(HttpsURLConnection connection)
	{
		try
		{
			int responseCode = connection.getResponseCode();

			if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_ACCEPTED
					|| responseCode == HttpURLConnection.HTTP_NO_CONTENT || responseCode == HttpsURLConnection.HTTP_CREATED)
			{
				return true;
			}

			// We got an error
			if (connection.getErrorStream() != null)
			{
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
				String inputLine;

				while ((inputLine = in.readLine()) != null)
				{
					logMessage(inputLine);
				}

				in.close();
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return false;
	}
}
