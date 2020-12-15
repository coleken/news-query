package com.example.newsq.ui.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
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
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    binding = FragmentSearchBinding.inflate(inflater, container, false);
    recyclerView = binding.listNewsStories;
    progressBar = binding.progressCircular;
    defaultView = binding.textSearchDefault;
    progressBar.setVisibility(View.INVISIBLE);
    defaultView.setText(R.string.search_default_message);
    configureSearchField();
    return binding.getRoot();
  }

  /**
   * Configures a {@link SearchView} to search the news.
   */
  private void configureSearchField() {
    SearchView searchView = binding.storySearchBar;
    searchView.onActionViewExpanded();
    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

      @Override
      public boolean onQueryTextSubmit(String query) {
        searchView.clearFocus();
        defaultView.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        checkConfigureLoader();
        return false;
      }

      @Override
      public boolean onQueryTextChange(String newText) {
        return false;
      }
    });
  }


  /**
   * Checks for an Internet connection before initializing or restarting the loader and contacting
   * the API.
   */
  private void checkConfigureLoader() {
    final int SEARCH_LOADER_ID = 3;
    if (QueryUtils.isDeviceConnected(getContext())) {
      /*
      Checks storyAdapter to determine if restartLoader or initLoader is called; storyAdapter is
      only null before the first news query is made.
       */
      if (storyAdapter != null) {
        LoaderManager.getInstance(this).restartLoader(SEARCH_LOADER_ID, null, this);
      } else {
        LoaderManager.getInstance(this).initLoader(SEARCH_LOADER_ID, null, this);
      }
    } else {
      progressBar.setVisibility(View.INVISIBLE);
      defaultView.setText(getString(R.string.no_network_connection));
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    binding = null;
  }

  @NonNull
  @Override
  public Loader<ArrayList<Story>> onCreateLoader(int id, Bundle args) {
    return new StoryLoader(requireContext(), createUrlString());
  }

  /**
   * Creates a {@link String} formatted for an API request.
   *
   * @return A {@link String} that contains a url for API requests.
   */
  private String createUrlString() {
    Map<String, String> uriSegments = new HashMap<>();
    String searchQuery = binding.storySearchBar.getQuery().toString();
    // Base URL
    uriSegments.put(getString(R.string.uri_scheme_key), getString(R.string.uri_scheme_value));
    uriSegments.put(getString(R.string.uri_authority_key), getString(R.string.uri_authority_value));
    // Section
    uriSegments.put(getString(R.string.uri_path_key), getString(R.string.endpoint_content));
    // Parameters
    uriSegments.put(getString(R.string.param_query), searchQuery); // Add search term
    uriSegments.put(getString(R.string.param_key_show_fields),
        getString(R.string.param_value_multiple_fields));
    uriSegments
        .put(getString(R.string.param_key_page_size), getString(R.string.param_value_page_size_30));
    return QueryUtils.createUri(uriSegments);
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
    if (QueryUtils.isNullOrEmpty(storyData) && !validResponse) {     // Message for bad responses
      defaultView.setText(QueryUtils.httpResponseMessage);
    }
    if (validResponse) {                                             // Update UI for good responses
      defaultView.setVisibility(View.INVISIBLE);
      recyclerView.setVisibility(View.VISIBLE);
      recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
      storyAdapter = new StoryAdapter(getContext(), storyData);
      recyclerView.setAdapter(storyAdapter);
    } else {                                                         // For null/empty cases
      defaultView.setVisibility(View.VISIBLE);
      defaultView.setText(getString(R.string.problem_with_request));
    }
  }

  @Override
  public void onLoaderReset(@NonNull Loader<ArrayList<Story>> loader) {
    storyAdapter.updateStories(new ArrayList<>());
  }
}