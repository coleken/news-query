package com.example.newsq.ui.browse;

import static android.R.layout.simple_spinner_dropdown_item;
import static android.R.layout.simple_spinner_item;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import com.example.newsq.databinding.FragmentBrowseBinding;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A {@link Fragment} subclass that implements {@link LoaderCallbacks} and is responsible for
 * displaying the selected news category provided by the API.
 */
public class BrowseFragment extends Fragment implements AdapterView.OnItemSelectedListener,
    LoaderCallbacks<ArrayList<Story>> {

  private String url;
  private boolean hasLoaderInit = false;
  private FragmentBrowseBinding binding;
  private RecyclerView recyclerView;
  private StoryAdapter storyAdapter;
  private ProgressBar progressBar;
  private TextView defaultView;

  public BrowseFragment() {
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
    binding = FragmentBrowseBinding.inflate(inflater, container, false);
    progressBar = binding.progressCircular;
    defaultView = binding.textBrowseDefault;
    recyclerView = binding.listNewsStories;
    createBrowseSpinner();
    defaultView.setText(R.string.browse_default_message);
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
   * Creates a {@link Spinner} configured to display news categories.
   */
  private void createBrowseSpinner() {
    Spinner spinner = binding.spinnerStorySections;
    spinner.setAdapter(createSelectionAdapter());
    spinner.setOnItemSelectedListener(this);
  }

  /**
   * Returns an {@link ArrayAdapter} for the {@link BrowseFragment} {@link Spinner}.
   *
   * @return An {@link ArrayAdapter} populated with the news sections {@link String} {@link
   * java.lang.reflect.Array Array}.
   */
  private ArrayAdapter<String> createSelectionAdapter() {
    String[] selections = getResources().getStringArray(R.array.spinner_array);
    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), simple_spinner_item,
        selections);
    adapter.setDropDownViewResource(simple_spinner_dropdown_item);
    return adapter;
  }

  @Override
  public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    if (position > 0) { // Skips the default/non-functional spinner array item
      String selection = parent.getSelectedItem().toString();
      createUrlString(selection);
      checkConfigureLoader();
    } else {
      binding.textNowReadingSection.setVisibility(View.INVISIBLE);
    }
  }

  /**
   * Creates a {@link String} formatted for an API request.
   *
   * @param spinnerSelection A {@link String} that contains the user's selection from the spinner.
   */
  private void createUrlString(String spinnerSelection) {
    Map<String, String> uriSegments = new HashMap<>();
    String section = getSpinnerMap().get(spinnerSelection);
    // Base URL
    uriSegments.put(getString(R.string.uri_scheme_key), getString(R.string.uri_scheme_value));
    uriSegments.put(getString(R.string.uri_authority_key), getString(R.string.uri_authority_value));
    // Section
    uriSegments.put(getString(R.string.uri_path_key), section); // Add selection to query
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
   * Returns a {@link Map} of each category/selection and its corresponding API section.
   *
   * @return A {@link Map} that contains selections as keys and sections as values.
   */
  private Map<String, String> getSpinnerMap() {
    Map<String, String> spinnerMap = new HashMap<>();
    String[] sections = getResources().getStringArray(R.array.section_array);
    String[] selections = getResources().getStringArray(R.array.spinner_array);
    for (int i = 0; i < sections.length; i++) {
      spinnerMap.put(selections[i], sections[i]);
    }
    return spinnerMap;
  }

  /**
   * Checks for an Internet connection before initializing or restarting the loader and contacting
   * the API.
   */
  private void checkConfigureLoader() {
    final int BROWSE_LOADER_ID = 2;
    if (QueryUtils.isDeviceConnected(getContext())) {
      defaultView.setVisibility(View.INVISIBLE);
      recyclerView.setVisibility(View.INVISIBLE);
      progressBar.setVisibility(View.VISIBLE);
      /*
      Checks hasLoaderInit to determine if restartLoader or initLoader is called; hasLoaderInit is
      only false before the first news query is made.
       */
      if (!hasLoaderInit) {
        LoaderManager.getInstance(this).initLoader(BROWSE_LOADER_ID, null, this);
        hasLoaderInit = true;
      } else {
        LoaderManager.getInstance(this).restartLoader(BROWSE_LOADER_ID, null, this);
      }
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
    if (QueryUtils.isNullOrEmpty(storyData) && !validResponse) {     // Message for bad responses
      defaultView.setVisibility(View.VISIBLE);
      defaultView.setText(QueryUtils.httpResponseMessage);
    }
    if (validResponse) {                                             // Update UI for good responses
      recyclerView.setVisibility(View.VISIBLE);
      recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
      storyAdapter = new StoryAdapter(getContext(), storyData);
      recyclerView.setAdapter(storyAdapter);
      binding.textNowReadingSection.setVisibility(View.VISIBLE);
    } else {                                                         // For null/empty cases
      defaultView.setVisibility(View.VISIBLE);
      defaultView.setText(getString(R.string.problem_with_request));
    }
  }

  @Override
  public void onNothingSelected(AdapterView<?> parent) {
    // DO NOTHING ON SELECTED
  }

  @Override
  public void onLoaderReset(@NonNull Loader<ArrayList<Story>> storyLoader) {
    storyAdapter.updateStories(new ArrayList<>());
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    binding = null;
  }
}