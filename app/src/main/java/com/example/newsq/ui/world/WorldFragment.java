package com.example.newsq.ui.world;

import static com.example.newsq.R.string.no_network_connection;
import static com.example.newsq.R.string.param_key_order_by;
import static com.example.newsq.R.string.param_key_page_size;
import static com.example.newsq.R.string.param_key_show_fields;
import static com.example.newsq.R.string.param_value_multiple_fields;
import static com.example.newsq.R.string.param_value_order_by_newest;
import static com.example.newsq.R.string.param_value_page_size_30;
import static com.example.newsq.R.string.problem_with_request;
import static com.example.newsq.R.string.section_world;
import static com.example.newsq.R.string.uri_authority_key;
import static com.example.newsq.R.string.uri_authority_value;
import static com.example.newsq.R.string.uri_path_key;
import static com.example.newsq.R.string.uri_scheme_key;
import static com.example.newsq.R.string.uri_scheme_value;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.app.LoaderManager.LoaderCallbacks;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.newsq.QueryUtils;
import com.example.newsq.Story;
import com.example.newsq.StoryAdapter;
import com.example.newsq.StoryLoader;
import com.example.newsq.databinding.FragmentWorldBinding;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A {@link Fragment} subclass that implements {@link LoaderCallbacks} and is responsible for
 * displaying the World News provided by the API.
 */
public class WorldFragment extends Fragment implements LoaderCallbacks<ArrayList<Story>> {

  private FragmentWorldBinding binding;
  private RecyclerView recyclerView;
  private StoryAdapter storyAdapter;
  private ProgressBar progressBar;

  /**
   * Default constructor
   */
  public WorldFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    binding = FragmentWorldBinding.inflate(inflater, container, false);
    recyclerView = binding.listNewsStories;
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    progressBar = binding.progressCircular;
    checkConfigureLoader();
    return binding.getRoot();
  }

  /**
   * Checks for an Internet connection before initializing the loader and contacting the API.
   */
  private void checkConfigureLoader() {
    if (QueryUtils.isDeviceConnected(getContext())) {
      final int WORLD_LOADER_ID = 1;
      LoaderManager.getInstance(this).initLoader(WORLD_LOADER_ID, null, this);
    } else {
      progressBar.setVisibility(View.INVISIBLE);
      binding.textWorldDefault.setText(getString(no_network_connection));
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    binding = null;
  }

  @NonNull
  @Override
  public Loader<ArrayList<Story>> onCreateLoader(int id, @Nullable Bundle args) {
    return new StoryLoader(requireContext(), createUrlString());
  }

  /**
   * Creates a {@link String} formatted for an API request.
   *
   * @return A {@link String} that contains a url for API requests.
   */
  private String createUrlString() {
    Map<String, String> uriSegments = new HashMap<>();
    uriSegments.put(getString(uri_scheme_key), getString(uri_scheme_value));
    uriSegments.put(getString(uri_authority_key), getString(uri_authority_value));
    uriSegments.put(getString(uri_path_key), getString(section_world));
    uriSegments.put(getString(param_key_show_fields), getString(param_value_multiple_fields));
    uriSegments.put(getString(param_key_page_size), getString(param_value_page_size_30));
    uriSegments.put(getString(param_key_order_by), getString(param_value_order_by_newest));
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
    if (QueryUtils.isNullOrEmpty(storyData) && !validResponse) {     // Message for bad responses
      binding.textWorldDefault.setText(QueryUtils.httpResponseMessage);
    }
    if (validResponse) {                                             // Update UI for good responses
      binding.textWorldDefault.setVisibility(View.INVISIBLE);
      storyAdapter = new StoryAdapter(getContext(), storyData);
      recyclerView.setAdapter(storyAdapter);
    } else {                                                         // For null/empty cases
      binding.textWorldDefault.setText(problem_with_request);
    }
  }

  @Override
  public void onLoaderReset(@NonNull Loader<ArrayList<Story>> storyLoader) {
    storyAdapter.updateStories(new ArrayList<>());
  }
}