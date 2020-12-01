package com.example.newsq.ui.browse;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
    return binding.getRoot();
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

  /**
   * TODO
   */
  private void displayNewsStories() {
    // TODO: displayNewsStories
  }

  /**
   * TODO
   */
  private Spinner getSpinner() {
    // TODO: buildSpinner
    return null;
  }

  /**
   * TODO
   *
   * @return A string array with news sections.
   */
  private String[] getSections() {
    // TODO: getSelections
    return null;
  }
}