package com.example.newsq.ui.browse;

import static android.R.layout.simple_spinner_dropdown_item;
import static android.R.layout.simple_spinner_item;

import android.app.Activity;
import android.content.Context;
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
    showHideKeyboard(requireContext(), container, false); // Hide keyboard
    binding = FragmentBrowseBinding.inflate(inflater, container, false);
    progressBar = binding.progressCircular;
    defaultView = binding.textBrowseDefault;
    recyclerView = binding.listNewsStories;
    createBrowseSpinner();
    progressBar.setVisibility(View.INVISIBLE);
    defaultView.setText(R.string.browse_default_message);
    return binding.getRoot();
  }

  /**
   * Uses the {@link InputMethodManager} to hide the keyboard when it isn't required, or hide it
   * completely.
   *
   * @param context  The {@link Context} from the current {@link Activity}.
   * @param view     A {@link View} instance.
   * @param isNeeded A {@link Boolean} of true if the keyboard should be visible, and false if it
   *                 should be invisible.
   */
  public void showHideKeyboard(@NonNull Context context, @NonNull View view, boolean isNeeded) {
    InputMethodManager manager = (InputMethodManager) context
        .getSystemService(Activity.INPUT_METHOD_SERVICE);
    if (!isNeeded) {
      manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    } else {
      manager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
    }
  }

  /**
   * Creates a {@link Spinner} configured to display news categories.
   */
  private void createBrowseSpinner() {
    Spinner spinner = binding.storySectionsSpinner;
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
  public void onDestroy() {
    super.onDestroy();
    binding = null;
  }

  @Override
  public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    if (position > 0) { // Skips the default/non-functional spinner array item
      defaultView.setVisibility(View.INVISIBLE);
      recyclerView.setVisibility(View.INVISIBLE);
      progressBar.setVisibility(View.VISIBLE);
      checkConfigureLoader();
    }
  }

  /**
   * Checks for an Internet connection before initializing or restarting the loader and contacting
   * the API.
   */
  private void checkConfigureLoader() {
    final int BROWSE_LOADER_ID = 2;
    if (QueryUtils.isDeviceConnected(getContext())) {
      /*
      Checks storyAdapter to determine if restartLoader or initLoader is called; storyAdapter is
      only null before the first news query is made.
       */
      if (storyAdapter != null) {
        LoaderManager.getInstance(this).restartLoader(BROWSE_LOADER_ID, null, this);
      } else {
        LoaderManager.getInstance(this).initLoader(BROWSE_LOADER_ID, null, this);
      }
    } else {
      progressBar.setVisibility(View.INVISIBLE);
      defaultView.setText(getString(R.string.no_network_connection));
    }
  }

  @Override
  public void onNothingSelected(AdapterView<?> parent) {
    // DO NOTHING ON SELECTED
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
    String section = getSpinnerMap().get(binding.storySectionsSpinner.getSelectedItem().toString());
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
    return QueryUtils.createUri(uriSegments);
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
  public void onLoaderReset(@NonNull Loader<ArrayList<Story>> storyLoader) {
    storyAdapter.updateStories(new ArrayList<>());
  }
}