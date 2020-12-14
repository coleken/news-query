package com.example.newsq;

import static com.example.newsq.R.layout.story_item;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

/**
 * A {@link RecyclerView.Adapter} subclass that formats and displays {@link Story} objects.
 */
public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder> {

  private final Context CONTEXT;
  private final ArrayList<Story> STORIES;

  /**
   * {@link ArrayList} constructor.
   *
   * @param context The {@link Context} from the current {@link android.app.Activity}.
   * @param stories An {@link ArrayList} of {@link Story} objects.
   */
  public StoryAdapter(Context context, ArrayList<Story> stories) {
    this.CONTEXT = context;
    this.STORIES = stories;
  }

  @NonNull
  @Override
  public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(story_item, parent, false);
    return new StoryViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull final StoryViewHolder holder, int position) {
    Story story = STORIES.get(position);
    holder.headline.setText(formatText(story.getHeadline()));
    holder.trailText.setText(formatText(story.getTrailText()));
    holder.sectionName.setText(formatTitleCase(story.getSectionName()));
    holder.webPublicationDate.setText(formatDate(story.getWebPublicationDate()));
    holder.contributors.setText(story.getByline());
    holder.storyCard.setOnClickListener(view -> CONTEXT
        .startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(story.getWebUrl()))));
  }

  /**
   * Returns styled text if HTML tags are present in the attribute.
   *
   * @param storyAttribute A {@link String} that contains a {@link Story} attribute.
   * @return A {@link String} of the formatted attribute.
   */
  private String formatText(String storyAttribute) {
    final String breakOpen = "<br>";
    // Prevents interference with spacing in the user interface.
    String cleanText = storyAttribute.replace(breakOpen, "");
    return Html.fromHtml(cleanText).toString();
  }

  /**
   * Returns a {@link String} formatted to title case.
   *
   * @param storyAttribute A {@link String} that contains a story attribute.
   * @return A {@link String} that contains a story attribute formatted to title case.
   */
  private String formatTitleCase(String storyAttribute) {
    StringBuilder builder = new StringBuilder(storyAttribute.length());
    char[] attributeChars = storyAttribute.toLowerCase().toCharArray();
    boolean toTitleCase = true;
    for (char character : attributeChars) {
      if (toTitleCase) {
        character = Character.toTitleCase(character);
        toTitleCase = false;
      } else if (Character.isSpaceChar(character)) {
        toTitleCase = true;
      }
      builder.append(character);
    }
    return builder.toString();
  }

  /**
   * Returns a {@link String} with the web publication date for the news story in the relative time
   * format.
   *
   * @param publicationDateTime A {@link String} that contains a publication date.
   * @return A {@link String} that contains a formatted date.
   */
  private String formatDate(String publicationDateTime) {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'", Locale.getDefault());
    format.setTimeZone(TimeZone.getTimeZone(String.valueOf(TimeZone.getDefault())));
    CharSequence timeAgo = "";
    try {
      long postedTime = Objects.requireNonNull(format.parse(publicationDateTime)).getTime();
      long currentTime = System.currentTimeMillis();
      timeAgo = DateUtils
          .getRelativeTimeSpanString(postedTime, currentTime, DateUtils.MINUTE_IN_MILLIS);
    } catch (ParseException e) {
      Log.e(StoryAdapter.class.getSimpleName(), "There was a problem parsing the date.", e);
    }
    return timeAgo.toString();
  }

  @Override
  public int getItemCount() {
    return STORIES.size();
  }

  /**
   * Updates the {@link Story} {@link ArrayList}.
   *
   * @param stories An {@link ArrayList} of {@link Story} objects.
   */
  public void updateStories(ArrayList<Story> stories) {
    STORIES.addAll(stories);
    notifyDataSetChanged();
  }

  /**
   * A {@link androidx.recyclerview.widget.RecyclerView.ViewHolder ViewHolder} subclass for the
   * {@link StoryAdapter} class.
   */
  public static class StoryViewHolder extends RecyclerView.ViewHolder {

    final public TextView sectionName;
    final public TextView webPublicationDate;
    final public TextView headline;
    final public TextView contributors;
    final public CardView storyCard;
    final public TextView trailText;

    /**
     * Maps the {@link androidx.recyclerview.widget.RecyclerView.ViewHolder ViewHolder} items to
     * their corresponding views.
     *
     * @param storyItem A {@link View} instance.
     */
    public StoryViewHolder(View storyItem) {
      super(storyItem);
      storyCard = storyItem.findViewById(R.id.layout_story_cardview);
      sectionName = storyItem.findViewById(R.id.text_story_section_name);
      webPublicationDate = storyItem.findViewById(R.id.text_story_publication_date);
      headline = storyItem.findViewById(R.id.text_story_headline);
      contributors = storyItem.findViewById(R.id.text_story_byline);
      trailText = storyItem.findViewById(R.id.text_story_trail_text);
    }
  }
}