package com.example.newsq;

/**
 * A custom object class for news stories.
 */
public class Story {

  private String id;
  private String type;
  private String sectionId;
  private String sectionName;
  private String webPublicationDate;
  private String webTitle;
  private String webUrl;
  private String apiUrl;

  /**
   * Default constructor
   */
  public Story() {
    // Default public constructor
  }

  /**
   * ArrayList constructor
   *
   * @param id                 A string that contains the id.
   * @param type               A string that contains the type.
   * @param sectionId          A string that contains the section id.
   * @param sectionName        A string that contains the section name.
   * @param webPublicationDate A string that contains the web publication date.
   * @param webTitle           A string that contains the web title.
   * @param webUrl             A string that contains the web url.
   * @param apiUrl             A string that contains the api url.
   */
  public Story(String id, String type, String sectionId, String sectionName,
      String webPublicationDate, String webTitle, String webUrl, String apiUrl) {
    this.id = id;
    this.type = type;
    this.sectionId = sectionId;
    this.sectionName = sectionName;
    this.webPublicationDate = webPublicationDate;
    this.webTitle = webTitle;
    this.webUrl = webUrl;
    this.apiUrl = apiUrl;
  }

  /**
   * Returns the story's id.
   *
   * @return A string that contains the id.
   */
  public String getId() {
    return id;
  }

  /**
   * Assigns the story's id.
   *
   * @param id A string that contains the id.
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Returns the story's section id.
   *
   * @return A string that contains the section id.
   */
  public String getSectionId() {
    return sectionId;
  }

  /**
   * Assigns the story's section id.
   *
   * @param sectionId A string that contains the section id.
   */
  public void setSectionId(String sectionId) {
    this.sectionId = sectionId;
  }

  /**
   * Returns the story's section name.
   *
   * @return A string that contains the section name.
   */
  public String getSectionName() {
    return sectionName;
  }

  /**
   * Assigns the story's section name.
   *
   * @param sectionName A string that contains the section name.
   */
  public void setSectionName(String sectionName) {
    this.sectionName = sectionName;
  }

  /**
   * Assigns the story's web publication date.
   *
   * @return A string that contains the web publication date.
   */
  public String getWebPublicationDate() {
    return webPublicationDate;
  }

  /**
   * Assigns the story's web publication date.
   *
   * @param webPublicationDate A string that contains the web publication date.
   */
  public void setWebPublicationDate(String webPublicationDate) {
    this.webPublicationDate = webPublicationDate;
  }

  /**
   * Returns the story's web title.
   *
   * @return A string that contains the web title.
   */
  public String getWebTitle() {
    return webTitle;
  }

  /**
   * Assigns the story's web title.
   *
   * @param webTitle A string that contains the web title.
   */
  public void setWebTitle(String webTitle) {
    this.webTitle = webTitle;
  }

  /**
   * Returns the story's web url.
   *
   * @return A string that contains the web url.
   */
  public String getWebUrl() {
    return webUrl;
  }

  /**
   * Assigns the story's web url.
   *
   * @param webUrl A string that contains the web url.
   */
  public void setWebUrl(String webUrl) {
    this.webUrl = webUrl;
  }

  /**
   * Returns the story's api url.
   *
   * @return A string that contains the api url.
   */
  public String getApiUrl() {
    return apiUrl;
  }

  /**
   * Assigns the story's api url.
   *
   * @param apiUrl A string that contains the api url.
   */
  public void setApiUrl(String apiUrl) {
    this.apiUrl = apiUrl;
  }

  /**
   * Assigns the story's type (e.g. an article)
   *
   * @return A string that contains the story's type.
   */
  public String getType() {
    return type;
  }

  /**
   * Returns a the story's type (e.g. an article).
   *
   * @param type A string that contains the story's type.
   */
  public void setType(String type) {
    this.type = type;
  }
}

