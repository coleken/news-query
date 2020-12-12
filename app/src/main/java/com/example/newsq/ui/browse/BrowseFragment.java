package com.example.newsq.ui.browse;

import static android.R.layout.simple_spinner_dropdown_item;
import static android.R.layout.simple_spinner_item;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.newsq.R;
import com.example.newsq.databinding.FragmentBrowseBinding;

/**
 * TODO: description
 */
public class BrowseFragment extends Fragment implements AdapterView.OnItemSelectedListener {

  FragmentBrowseBinding binding;

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
    binding = FragmentBrowseBinding.inflate(inflater, container, false);
    getBrowseSpinner();
    return binding.getRoot();
  }

  /**
   * Returns a spinner configured to display news categories.
   *
   * @return A spinner configured to use the sections array adapter.
   */
  private Spinner getBrowseSpinner() {
    Spinner spinner = binding.spinnerSections;
    spinner.setAdapter(createSelectionAdapter());
    spinner.setOnItemSelectedListener(this);
    return spinner;
  }

  /**
   * Returns a string adapter for the browse fragment spinner.
   *
   * @return A string array adapter populated with the news sections string array.
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
    // TODO: onItemSelected
  }

  @Override
  public void onNothingSelected(AdapterView<?> parent) {
    // TODO: onNothingSelected
  }
}