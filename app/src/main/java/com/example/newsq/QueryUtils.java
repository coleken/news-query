package com.example.newsq;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import androidx.annotation.NonNull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A utility class that contains the {@link HttpConnectionClient HttpConnectionClient} class and
 * helper methods to build {@link Uri Uris}, {@link URL URLs}, and parse API responses.
 */
public final class QueryUtils {

  private static final String LOG_TAG = QueryUtils.class.getSimpleName();
  public static String httpResponseMessage;

  private static final String JSON_RESPONSE = "response";
  private static final String JSON_RESULTS = "results";
  private static final String TAGS = "tags";
  private static final String ID = "id";
  private static final String TYPE = "type";
  private static final String SECTION_NAME = "sectionName";
  private static final String SECTION_ID = "sectionId";
  private static final String WEB_PUBLICATION_DATE = "webPublicationDate";
  private static final String WEB_TITLE = "webTitle";
  private static final String WEB_URL = "webUrl";
  private static final String API_URL = "apiUrl";

  /**
   * Private constructor
   */
  private QueryUtils() {
    // Private Constructor
  }

  /**
   * Requests news data from the API and returns it in an array list of custom {@link Story}
   * objects.
   * <p>
   * Calls to: {@link #delayNewsFetch()}, {@link HttpConnectionClient#getHttpResponse(URL)}, and
   * {@link #extractNewsStories(String)}
   *
   * @param urlString A {@link String} that contains a url for a specific API request.
   * @return An {@link ArrayList} of {@link Story} objects obtained from the API request.
   */
  public static ArrayList<Story> fetchNews(String urlString) {
    // Pause thread for loading indicator
    delayNewsFetch();
    // Create URL
    URL url = createUrl(urlString);
    // Retrieve API response
    String response = HttpConnectionClient.getHttpResponse(url);
    if (HttpConnectionClient.responseOk) {
      // Return ArrayList of news stories
      return extractNewsStories(response);
    } else {
      // Assign a bad response for reference
      httpResponseMessage = response;
      return null;
    }
  }

  /**
   * Puts the current thread to sleep for showing the circular progress bar.
   */
  private static void delayNewsFetch() {
    final int time = 2000; // in milliseconds
    try {
      Thread.sleep(time);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * Returns a new {@link URL} created from the given {@link Uri}.
   *
   * @param uri A {@link String} that contains a {@link Uri} for an API request.
   * @return A {@link URL} object formatted for an API request.
   */
  private static URL createUrl(String uri) {
    if (isNullOrEmpty(uri)) {
      return null;
    }
    URL url;
    try {
      url = new URL(uri);
    } catch (MalformedURLException e) {
      Log.e(LOG_TAG, "An error occurred while creating URL.", e);
      return null;
    }
    return url;
  }

  /**
   * Extracts news stories from the HTTP response retrieved by {@link
   * HttpConnectionClient#getHttpResponse(URL) getHttpResponse}.
   *
   * @param response A {@link String} that contains the response data.
   * @return An {@link ArrayList} of news objects obtained from the API request.
   */
  @NonNull
  private static ArrayList<Story> extractNewsStories(String response) {
    ArrayList<Story> stories = new ArrayList<>();
    if (!isNullOrEmpty(response) && HttpConnectionClient.responseOk) {
      try {
        // Complete response object
        JSONObject responseData = new JSONObject(response).getJSONObject(JSON_RESPONSE);
        // Extract results array
        JSONArray newsArray = responseData.getJSONArray(JSON_RESULTS);
        for (int i = 0; i < newsArray.length(); i++) {
          // Extract news story object
          JSONObject storyAttribute = newsArray.getJSONObject(i);
          // Initialize story attributes
          String id = storyAttribute.getString(ID);
          String type = storyAttribute.getString(TYPE);
          String sectionName = storyAttribute.getString(SECTION_NAME);
          String sectionId = storyAttribute.getString(SECTION_ID);
          String webPublicationDate = storyAttribute.getString(WEB_PUBLICATION_DATE);
          String webTitle = storyAttribute.getString(WEB_TITLE);
          String webUrl = storyAttribute.getString(WEB_URL);
          String apiUrl = storyAttribute.getString(API_URL);
          // Extract story tags
          JSONArray tagsArray = storyAttribute.getJSONArray(TAGS);
          List<String> contributors = new ArrayList<>();
          for (int j = 0; j < tagsArray.length(); j++) {
            // Tags object that contains the story contributors
            JSONObject storyContributor = tagsArray.getJSONObject(j);
            contributors.add(storyContributor.getString(WEB_TITLE));
          }
          // Add all attributes to a new story object
          stories.add(
              new Story(id, type, sectionId, sectionName, webPublicationDate, webTitle, webUrl,
                  apiUrl, contributors));
        }
      } catch (JSONException e) {
        Log.e(LOG_TAG, "Problem parsing JSON response", e);
      }
    }
    return stories;
  }

  /**
   * Returns a {@link Uri} reference constructed with the given parameters for an API request.
   *
   * @param uriSegments A {@link Map} of keys and values used to build a {@link Uri}.
   * @return A {@link String} that contains a {@link Uri} built with the given parameters.
   */
  public static String createUri(@NonNull Map<String, String> uriSegments) {
    Uri.Builder builder = new Uri.Builder();
    final String scheme = "scheme";
    final String authority = "authority";
    final String path = "path";
    final String apiKey = "api-key";
    if (isNullOrEmpty(uriSegments)) {
      return null;
    } else {
      for (Map.Entry<String, String> parametersEntry : uriSegments.entrySet()) {
        String key = parametersEntry.getKey();
        switch (key) {
          case scheme:
            builder.scheme(parametersEntry.getValue());
            break;
          case authority:
            builder.authority(parametersEntry.getValue());
            break;
          case path:
            builder.appendPath(parametersEntry.getValue());
            break;
          default:
            builder.appendQueryParameter(parametersEntry.getKey(), parametersEntry.getValue());
            builder.appendQueryParameter(apiKey, HttpConnectionClient.getApiKey());
            break;
        }
      }
    }
    return builder.build().toString();
  }

  /**
   * Checks if the given {@link ArrayList} is null or empty.
   *
   * @param stories An {@link ArrayList} of {@link Story} objects.
   * @return A {@link Boolean} true if the {@link ArrayList} is empty or null, and false if it's
   * not.
   */
  public static boolean isNullOrEmpty(ArrayList<Story> stories) {
    boolean isNullOrEmpty = false;
    if (stories == null || stories.isEmpty()) {
      isNullOrEmpty = true;
    }
    return isNullOrEmpty;
  }

  /**
   * Checks if the given {@link String} is null or empty.
   *
   * @param string A {@link String} object.
   * @return True if the string is empty or null, and false if it's not.
   */
  public static boolean isNullOrEmpty(String string) {
    boolean isNullOrEmpty = false;
    if (string == null || string.isEmpty()) {
      isNullOrEmpty = true;
    }
    return isNullOrEmpty;
  }

  /**
   * Checks if the given {@link Map} is empty, or has empty/null keys or values.
   *
   * @param map A {@link Map} of keys and values used to build a {@link Uri}.
   * @return True if the map is empty or contains empty/null keys or values, and false if it
   * doesn't.
   */
  private static boolean isNullOrEmpty(Map<String, String> map) {
    boolean isNullOrEmpty = false;
    if (map == null || map.isEmpty()) {
      isNullOrEmpty = true;
    } else if (map.containsKey(null) || map.containsValue(null)) {
      isNullOrEmpty = true;
    } else {
      for (Entry<String, String> mapEntry : map.entrySet()) {
        if (mapEntry.getKey().isEmpty() || mapEntry.getValue().isEmpty()) {
          isNullOrEmpty = true;
        }
      }
    }
    return isNullOrEmpty;
  }

  /**
   * Returns a boolean to indicate if the HTTP response didn't include a status code of 200.
   *
   * @return True if the HTTP response was valid, False if the response wasn't valid.
   */
  public static boolean isIsResponseValid() {
    return HttpConnectionClient.responseOk;
  }

  /**
   * Returns a boolean to indicate if the device is currently connected to the network.
   *
   * @see HttpConnectionClient#isDeviceConnected(Context)
   */
  public static boolean isDeviceConnected(Context context) {
    return HttpConnectionClient.isDeviceConnected(context);
  }

  /**
   * A utility class that checks and manages network connections for API requests.
   */
  private final static class HttpConnectionClient {

    private static final String LOG_TAG = HttpURLConnection.class.getSimpleName();
    private static boolean responseOk;

    /**
     * Default constructor
     */
    private HttpConnectionClient() {
      //Default private constructor
    }

    /**
     * Returns a {@link Boolean} to indicate if the device is currently connected to the network.
     *
     * @param context The {@link Context} from the current {@link android.app.Activity}.
     * @return A {@link Boolean} that indicates if the device is connected to the network.
     */
    private static boolean isDeviceConnected(@NonNull Context context) {
      boolean isConnected = false;
      ConnectivityManager manager = (ConnectivityManager) context
          .getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo networkState = manager.getActiveNetworkInfo();
      if (networkState != null && networkState.isConnected()) {
        isConnected = true;
      }
      return isConnected;
    }

    /**
     * Connects to the API and returns the response in a string.
     * <p>
     * Calls: {@link #readInputStream(HttpURLConnection)}
     *
     * @param url A {@link URL} object formatted for an API request.
     * @return A {@link String} that contains the API response data.
     */
    @NonNull
    private static String getHttpResponse(URL url) {
      HttpURLConnection connection = null;
      responseOk = false;
      String response = "";
      final int STATUS_OK = 200;
      final int connectionTimeoutLimit = 10000;
      final int readTimeoutLimit = 15000;
      final String requestGet = "GET";
      if (url != null) {
        try {
          connection = (HttpURLConnection) url.openConnection();
          connection.setRequestMethod(requestGet);
          connection.setConnectTimeout(connectionTimeoutLimit);
          connection.setReadTimeout(readTimeoutLimit);
          connection.connect();
          if (connection.getResponseCode() == STATUS_OK) {
            responseOk = true;
            response = readInputStream(connection);
          } else {
            response = connection.getResponseCode() + " " + connection.getResponseMessage();
          }
        } catch (IOException e) {
          Log.e(LOG_TAG, "There was a problem connecting to the server." + response, e);
        } finally {
          if (connection != null) {
            connection.disconnect();
          }
        }
      }
      return response;
    }

    /**
     * A helper method that reads from the input stream and converts it into a string.
     * <p>
     * Called by: {@link #getHttpResponse(URL)}
     *
     * @param connection An {@link HttpURLConnection} instance configured for an API request.
     * @return A {@link String} that contains the API response data.
     */
    @NonNull
    private static String readInputStream(HttpURLConnection connection) {
      StringBuilder out = new StringBuilder();
      String lines;
      if (connection != null) {
        try (BufferedReader in = new BufferedReader(
            new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
          while ((lines = in.readLine()) != null) {
            out.append(lines);
          }
        } catch (IOException e) {
          Log.e(LOG_TAG, "Problem reading input stream.", e);
        }
      }
      return out.toString();
    }

    /**
     * Retrieves, decodes, and returns the API key.
     *
     * @return A string that contains the api key.
     */
    @NonNull
    private static String getApiKey() {
      // Retrieve API key from build config
      String encodedKey = com.example.newsq.BuildConfig.API_KEY;
      // Decode the API key
      byte[] bytes = Base64.decode(encodedKey, Base64.DEFAULT);
      // Return the converted key
      return new String(bytes, StandardCharsets.UTF_8);
    }
  }
}
