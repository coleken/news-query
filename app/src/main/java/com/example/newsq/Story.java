package com.example.newsq;

/**
 * A custom object class for news stories.
 */
public class Story {

  private final String id;
  private final String sectionName;
  private final String webPublicationDate;
  private final String webUrl;
  private final String headline;
  private final String byline;
  private final String trailText;

  /**
   * ArrayList constructor
   *
   * @param id                 A string that contains the id.
   * @param sectionName        A string that contains the section name.
   * @param webPublicationDate A string that contains the web publication date.
   * @param webUrl             A string that contains the web url.
   * @param headline           A string that contains the headline.
   * @param byline             A string that contains the byline.
   * @param trailText          A string that contains the trail text.
   */
  public Story(String id, String sectionName,
      String webPublicationDate, String webUrl, String headline, String byline, String trailText) {

    this.id = id;
    this.sectionName = sectionName;
    this.webPublicationDate = webPublicationDate;
    this.webUrl = webUrl;
    this.headline = headline;
    this.byline = byline;
    this.trailText = trailText;
  }

  /**
   * Returns a {@link String} with the story's id.
   *
   * @return A string that contains the id.
   */
  public String getId() {
    return id;
  }

  /**
   * Returns a {@link String} with the story's section name.
   *
   * @return A string that contains the section name.
   */
  public String getSectionName() {
    return sectionName;
  }

  /**
   * Returns a {@link String} with the story's publication date.
   *
   * @return A string that contains the web publication date.
   */
  public String getWebPublicationDate() {
    return webPublicationDate;
  }

  /**
   * Returns a {@link String} with the story's web url.
   *
   * @return A string that contains the web url.
   */
  public String getWebUrl() {
    return webUrl;
  }

  /**
   * Returns a {@link String} with the story's headline.
   *
   * @return A {@link String} that contains the headline.
   */
  public String getHeadline() {
    return headline;
  }

  /**
   * Returns a {@link String} with the story's byline.
   *
   * @return A {@link String} that contains the byline.
   */
  public String getByline() {
    return byline;
  }

  /**
   * Returns a {@link String} with the story's trail text.
   *
   * @return A {@link String} that contains the trail text.
   */
  public String getTrailText() {
    return trailText;
  }
}

