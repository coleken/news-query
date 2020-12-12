package com.example.newsq;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;
import java.util.ArrayList;

/**
 * {@link AsyncTaskLoader} subclass that fetches the news data for the {@link
 * com.example.newsq.ui.world.WorldFragment WorldFragment}, {@link com.example.newsq.ui.browse.BrowseFragment
 * BrowseFragment}, and {@link com.example.newsq.ui.search.SearchFragment SearchFragment}
 */
public class StoryLoader extends AsyncTaskLoader<ArrayList<Story>> {

  private final String url;

  /**
   * Creates a new {@link StoryLoader} object for API requests.
   *
   * @param context The {@link Context} from the current {@link android.app.Activity}.
   * @param url     A {@link String} that contains a url for API requests.
   */
  public StoryLoader(@NonNull Context context, String url) {
    super(context);
    this.url = url;
  }

  @Override
  public void onStartLoading() {
    forceLoad();
  }

  @Nullable
  @Override
  public ArrayList<Story> loadInBackground() {
    if (QueryUtils.isNullOrEmpty(url)) {
      return null;
    } else {
      return QueryUtils.fetchNews(url);
    }
  }
}
