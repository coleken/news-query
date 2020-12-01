package com.example.newsq;

import static com.example.newsq.R.layout.story_item;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * TODO: description
 */
public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder> {

  //TODO: ArrayList

  public StoryAdapter() {
    //TODO: constructor
  }

  @NonNull
  @Override
  public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(story_item, parent, false);
    return new StoryViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull final StoryViewHolder holder, int position) {
    //TODO: onBindViewHolder
  }

  @Override
  public int getItemCount() {
    //TODO: getItemCount
    return 0;
  }

  public static class StoryViewHolder extends RecyclerView.ViewHolder {

    //TODO: StoryViewHolder fields

    public StoryViewHolder(View view) {
      super(view);
      //TODO: StoryViewHolder
    }
  }
}