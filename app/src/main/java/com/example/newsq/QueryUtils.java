package com.example.newsq;

import android.content.Context;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;


/**
 * A utility class that contains helper methods to construct and send API queries.
 */
public final class QueryUtils {

  private static final String LOG_TAG = QueryUtils.class.getSimpleName();
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
  public static String HTTP_RESPONSE_MESSAGE;

  /**
   * Private constructor
   */
  private QueryUtils() {
    // Private Constructor
  }

  /**
   * TODO: Comments
   */
  public static ArrayList<Story> fetchNews(URL url) {
    return null;
  }

  /**
   * TODO: Comments
   */
  private static void delayNewsFetch() {
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * TODO: Comments
   */
  private static ArrayList<Story> extractNewsStories(String response) {
    ArrayList<Story> stories = new ArrayList<>();
    return stories;
  }

  /**
   * A utility class that checks and manages network connections for API requests.
   */
  private final static class HttpConnectionClient {

    private static final String LOG_TAG = HttpURLConnection.class.getSimpleName();
    private static final int STATUS_OK = 200;
    private static boolean RESPONSE_OK;

    /**
     * Default constructor
     */
    private HttpConnectionClient() {
      //Default private constructor
    }

    /**
     * TODO: Comments
     */
    private static boolean isDeviceConnected(Context context) {
      boolean isConnected = false;
      return isConnected;
    }

    /**
     * TODO: Comments
     */
    private static String getHttpResponse(URL url) {
      String response = "";
      return response;
    }

    /**
     * TODO: Comments
     */
    private static String readInputStream(HttpURLConnection connection) {
      StringBuilder out = new StringBuilder();
      String lines;
      return out.toString();
    }

    /**
     * TODO: Comments
     */
    private static URL createUrl(String uri) {
      URL url = null;
      return url;
    }

    /**
     * TODO: Comments
     */
    public static String createUri(Map<String, String> uriSegments) {
      return null;
    }

    /**
     * TODO: Comments
     */
    private static String getApiKey() {
      return null;
    }
  }
}

