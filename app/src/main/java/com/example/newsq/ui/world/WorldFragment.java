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

  private String url;
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
    hideKeyboard(container);
    binding = FragmentWorldBinding.inflate(inflater, container, false);
    defaultView = binding.textWorldDefault;
    progressBar = binding.progressCircular;
    recyclerView = binding.listNewsStories;
    createUrlString();
    checkConfigureLoader();
    return binding.getRoot();
  }

  /**
   * Uses {@link InputMethodManager} to hide the software keyboard; this method prevents the
   * keyboard from remaining on the screen if it was visible when switching from the {@link
   * com.example.newsq.ui.search.SearchFragment SearchFragment}.
   *
   * @param view An instance of {@link View}.
   */
  private void hideKeyboard(View view) {
    /*
     * Method: hideKeyboard (and its preceding versions)
     * Adopted Content: Procedure for obtaining a window token in fragments.
     * Date Created: 12/15/2020
     * Original Source Code Location: https://stackoverflow.com/questions/1109022/
     * Asked by: Ak23; https://stackoverflow.com/users/11945183
     * Answered by: mirabelle; https://stackoverflow.com/users/680583
     */
    InputMethodManager manager = (InputMethodManager) requireContext()
        .getSystemService(Activity.INPUT_METHOD_SERVICE);
    manager.hideSoftInputFromWindow(view.getWindowToken(),
        InputMethodManager.RESULT_UNCHANGED_SHOWN);
  }

  /**
   * Creates a {@link String} formatted for an API request.
   */
  private void createUrlString() {
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
    url = QueryUtils.createUri(uriSegments);
  }

  /**
   * Checks for an Internet connection before initializing the loader and contacting the API.
   */
  private void checkConfigureLoader() {
    if (QueryUtils.isDeviceConnected(getContext())) {
      final int WORLD_LOADER_ID = 1;
      defaultView.setVisibility(View.INVISIBLE);
      recyclerView.setVisibility(View.INVISIBLE);
      progressBar.setVisibility(View.VISIBLE);
      LoaderManager.getInstance(this).initLoader(WORLD_LOADER_ID, null, this);
    } else {
      defaultView.setText(getString(R.string.no_network_connection));
    }
  }

  @NonNull
  @Override
  public Loader<ArrayList<Story>> onCreateLoader(int id, @Nullable Bundle args) {
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
    if (!validResponse) {
      if (QueryUtils.isConnectionError()) {
        defaultView.setText(getString(R.string.problem_with_request));
      } else {
        defaultView.setText(QueryUtils.httpResponseMessage);
        return;
      }
    }
    if (validResponse) {
      if (QueryUtils.isParseError) {
        defaultView.setText(R.string.problem_with_response);
      } else if (QueryUtils.isNoDataResponse) {
        defaultView.setText(R.string.no_data_available);
      } else {
        defaultView.setVisibility(View.INVISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        storyAdapter = new StoryAdapter(getContext(), storyData);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setAdapter(storyAdapter);
      }
    } else {
      defaultView.setVisibility(View.VISIBLE);
      defaultView.setText(getString(R.string.problem_with_request));
    }
  }

  @Override
  public void onLoaderReset(@NonNull Loader<ArrayList<Story>> storyLoader) {
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