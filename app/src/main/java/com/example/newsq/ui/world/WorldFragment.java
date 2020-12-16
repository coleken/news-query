package com.example.newsq.ui.world;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
  private TextView defaultView;

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
    hideKeyboard();
    binding = FragmentWorldBinding.inflate(inflater, container, false);
    defaultView = binding.textWorldDefault;
    progressBar = binding.progressCircular;
    recyclerView = binding.listNewsStories;
    checkConfigureLoader();
    return binding.getRoot();
  }

  /**
   * Uses {@link InputMethodManager} to toggle the software keyboard's appearance.
   */
  private void hideKeyboard() {
    InputMethodManager manager = (InputMethodManager) requireContext()
        .getSystemService(Activity.INPUT_METHOD_SERVICE);
    manager.hideSoftInputFromWindow(requireActivity().getCurrentFocus().getWindowToken(),
        InputMethodManager.RESULT_UNCHANGED_SHOWN);
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
    // Base URL
    uriSegments.put(getString(R.string.uri_scheme_key), getString(R.string.uri_scheme_value));
    uriSegments.put(getString(R.string.uri_authority_key), getString(R.string.uri_authority_value));
    // Section
    uriSegments.put(getString(R.string.uri_path_key), getString(R.string.section_world));
    // Parameters
    uriSegments.put(getString(R.string.param_key_show_fields),
        getString(R.string.param_value_multiple_fields));
    uriSegments
        .put(getString(R.string.param_key_page_size), getString(R.string.param_value_page_size_30));
    uriSegments.put(getString(R.string.param_key_use_date),
        getString(R.string.param_value_use_date_last_modified));
    uriSegments.put(getString(R.string.param_key_order_by),
        getString(R.string.param_value_order_by_newest));
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
      recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
      storyAdapter = new StoryAdapter(getContext(), storyData);
      recyclerView.setAdapter(storyAdapter);
    } else {                                                         // For null/empty cases
      defaultView.setVisibility(View.VISIBLE);
      defaultView.setText(getString(R.string.problem_with_request));
    }
  }

  @Override
  public void onLoaderReset(@NonNull Loader<ArrayList<Story>> storyLoader) {
    storyAdapter.updateStories(new ArrayList<>());
  }
}