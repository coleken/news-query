package com.example.newsq.ui.world;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.newsq.databinding.FragmentWorldBinding;

/**
 * TODO: description
 */
public class WorldFragment extends Fragment {

  FragmentWorldBinding binding;

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
    return binding.getRoot();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    binding = null;
  }

  /**
   * TODO
   */
  private void displayNewsStories() {
    // TODO: displayNewsStories
  }
}