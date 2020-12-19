package com.example.newsq.ui.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SearchView.OnQueryTextListener;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.app.LoaderManager.LoaderCallbacks;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.newsq.QueryUtils;
import com.example.newsq.R;
import com.example.newsq.Story;
import com.example.newsq.StoryAdapter;
import com.example.newsq.StoryLoader;
import com.example.newsq.databinding.FragmentSearchBinding;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A {@link Fragment} subclass that implements {@link LoaderCallbacks} and is responsible for
 * displaying the news search results provided by the API.
 */
public class SearchFragment extends Fragment implements LoaderCallbacks<ArrayList<Story>> {

  private final String SEARCH_STRING = "searchQuery";
  private boolean hasLoaderInit = false;
  private String url;
  private FragmentSearchBinding binding;
  private RecyclerView recyclerView;
  private StoryAdapter storyAdapter;
  private ProgressBar progressBar;
  private TextView defaultView;

  public SearchFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString(SEARCH_STRING, binding.storySearchBar.getQuery().toString());
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    binding = FragmentSearchBinding.inflate(inflater, container, false);
    recyclerView = binding.listNewsStories;
    progressBar = binding.progressCircular;
    defaultView = binding.textSearchDefault;
    defaultView.setText(R.string.search_default_message);
    configureSearchField();
    if (savedInstanceState != null) {
      restoreQuery(savedInstanceState);
    }
    return binding.getRoot();
  }

  /**
   * Configures the {@link SearchView} for news queries.
   */
  private void configureSearchField() {
    SearchView searchView = binding.storySearchBar;
    searchView.setIconifiedByDefault(false);
    searchView.onActionViewExpanded();
    searchView.clearFocus();
    searchView.setOnQueryTextListener(new OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        createUrlString(query);
        checkConfigureLoader();
        searchView.clearFocus();
        return false;
      }

      @Override
      public boolean onQueryTextChange(String newText) {
        return false;
      }
    });
  }

  /**
   * Creates a {@link String} formatted for an API request.
   *
   * @param searchTerm A {@link} that contains the user input.
   */
  private void createUrlString(String searchTerm) {
    Map<String, String> uriSegments = new HashMap<>();
    // Base URL
    uriSegments.put(getString(R.string.uri_scheme_key), getString(R.string.uri_scheme_value));
    uriSegments.put(getString(R.string.uri_authority_key), getString(R.string.uri_authority_value));
    // Section
    uriSegments.put(getString(R.string.uri_path_key), getString(R.string.endpoint_content));
    // Parameters
    uriSegments.put(getString(R.string.param_query), searchTerm); // Add search term
    uriSegments.put(getString(R.string.param_key_show_fields),
        getString(R.string.param_value_multiple_fields));
    uriSegments
        .put(getString(R.string.param_key_page_size), getString(R.string.param_value_page_size_30));
    url = QueryUtils.createUri(uriSegments);
  }

  /**
   * Checks for an Internet connection before initializing or restarting the loader and contacting
   * the API.
   */
  private void checkConfigureLoader() {
    recyclerView.setVisibility(View.INVISIBLE);
    final int SEARCH_LOADER_ID = 3;
    if (QueryUtils.isDeviceConnected(getContext())) {
      defaultView.setVisibility(View.INVISIBLE);
      progressBar.setVisibility(View.VISIBLE);
      /*
      Checks hasLoaderInit to determine if restartLoader or initLoader is called; hasLoaderInit is
      only false before the first news query is made.
       */
      if (!hasLoaderInit) {
        LoaderManager.getInstance(this).initLoader(SEARCH_LOADER_ID, null, this);
        hasLoaderInit = true;
      } else {
        LoaderManager.getInstance(this).restartLoader(SEARCH_LOADER_ID, null, this);
      }
    } else {
      defaultView.setVisibility(View.VISIBLE);
      defaultView.setText(getString(R.string.no_network_connection));
    }
  }

  @NonNull
  @Override
  public Loader<ArrayList<Story>> onCreateLoader(int id, Bundle args) {
    return new StoryLoader(requireContext(), url);
  }

  @Override
  public void onLoadFinished(@NonNull Loader<ArrayList<Story>> loader, ArrayList<Story> storyData) {
    updateUserInterface(QueryUtils.isResponseValid(), storyData);
  }

  /**
   * Helper method for {@link #onLoadFinished} that updates the user interface based on
   * valid/invalid responses and empty/null cases.
   *
   * @param validResponse A {@link Boolean} that indicates if the HTTP response is valid.
   * @param storyData     An {@link ArrayList} of {@link Story} objects.
   */
  private void updateUserInterface(boolean validResponse, ArrayList<Story> storyData) {
    progressBar.setVisibility(View.INVISIBLE);
    defaultView.setVisibility(View.VISIBLE);
    if (storyData == null) {
      if (!QueryUtils.isDeviceConnected(getContext())) {
        recyclerView.setVisibility(View.INVISIBLE);
        defaultView.setText(R.string.no_network_connection);
      } else {
        defaultView.setText(R.string.no_data_available);
      }
    } else if (!validResponse) {
      updateInvalidResponse();
    } else {
      updateValidResponse(storyData);
    }
  }

  /**
   * Updates the interface based on invalid responses; this method will show response codes and
   * messages or a general error for connection-specific errors that yield no response codes.
   */
  private void updateInvalidResponse() {
    if (QueryUtils.isConnectionError()) {
      defaultView.setText(getString(R.string.problem_with_request));
    } else {
      defaultView.setText(QueryUtils.httpResponseMessage);
    }
  }

  /**
   * Updates the user interface based on valid responses; this method displays messages for parsing
   * errors, displays news, and uses a general error where necessary.
   *
   * @param stories An {@link ArrayList} of {@link Story} objects.
   */
  private void updateValidResponse(ArrayList<Story> stories) {
    if (QueryUtils.isParseError) {
      defaultView.setText(R.string.problem_with_response);
    } else if (QueryUtils.isNoDataResponse) {
      defaultView.setText(R.string.no_data_available);
    } else if (!QueryUtils.isNullOrEmpty(stories)) {
      defaultView.setVisibility(View.INVISIBLE);
      recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
      storyAdapter = new StoryAdapter(getContext(), stories);
      recyclerView.setVisibility(View.VISIBLE);
      recyclerView.setAdapter(storyAdapter);
    } else {
      defaultView.setVisibility(View.VISIBLE);
      defaultView.setText(getString(R.string.problem_with_request));
    }
  }

  /**
   * Resubmits the query sent prior to configuration changes.
   *
   * @param savedState A {@link Bundle} instance that contains the previous search string.
   */
  private void restoreQuery(Bundle savedState) {
    binding.storySearchBar.setQuery(savedState.getString(SEARCH_STRING), true);
    binding.storySearchBar.clearFocus();
  }

  @Override
  public void onLoaderReset(@NonNull Loader<ArrayList<Story>> loader) {
    if (storyAdapter != null) {
      storyAdapter.updateStories(new ArrayList<>());
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    binding = null;
  }
}