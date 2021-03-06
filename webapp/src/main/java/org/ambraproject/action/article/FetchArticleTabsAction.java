/*
 * Copyright (c) 2006-2013 by Public Library of Science
 *
 * http://plos.org
 * http://ambraproject.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ambraproject.action.article;

import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import org.ambraproject.ApplicationException;
import org.ambraproject.action.BaseSessionAwareActionSupport;
import org.ambraproject.models.Article;
import org.ambraproject.service.article.NoSuchObjectIdException;
import org.ambraproject.views.CitationView;
import org.ambraproject.service.captcha.CaptchaService;
import org.ambraproject.web.Cookies;
import org.ambraproject.freemarker.AmbraFreemarkerConfig;
import org.ambraproject.models.AnnotationType;
import org.ambraproject.models.ArticleView;
import org.ambraproject.models.UserProfile;
import org.ambraproject.service.annotation.AnnotationService;
import org.ambraproject.service.article.ArticleAssetService;
import org.ambraproject.service.article.ArticleAssetWrapper;
import org.ambraproject.service.article.ArticleService;
import org.ambraproject.service.article.FetchArticleService;
import org.ambraproject.service.article.NoSuchArticleIdException;
import org.ambraproject.service.trackback.TrackbackService;
import org.ambraproject.service.user.UserService;
import org.ambraproject.util.TextUtils;
import org.ambraproject.util.UriUtil;
import org.ambraproject.views.AnnotationView;
import org.ambraproject.views.ArticleCategory;
import org.ambraproject.views.AuthorView;
import org.ambraproject.views.CitationReference;
import org.ambraproject.views.JournalView;
import org.ambraproject.views.article.ArticleInfo;
import org.ambraproject.views.article.ArticleType;
import org.ambraproject.views.article.RelatedArticleInfo;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.ambraproject.service.annotation.AnnotationService.AnnotationOrder;

/**
 * This class fetches the information from the service tier for the article Tabs.  Common data is defined in the
 * setCommonData.  One method is defined for each tab.
 * <p/>
 * Freemarker builds rest like URLs, inbound and outbound as defined in the /WEB-INF/urlrewrite.xml file. These URLS
 * then map to the methods are referenced in the struts.xml file.
 * <p/>
 * ex: http://localhost/article/related/info%3Adoi%2F10.1371%2Fjournal.pone.0299
 * <p/>
 * Gets rewritten to:
 * <p/>
 * http://localhost/fetchArticleRelated.action&amp;articleURI=info%3Adoi%2F10.1371%2Fjournal.pone.0299
 * <p/>
 * Struts picks this up and translates it call the FetchArticleRelated method ex: &lt;action name="fetchRelatedArticle"
 * class="org.ambraproject.article.action.FetchArticleTabsAction" method="FetchArticleRelated"&gt;
 */
public class FetchArticleTabsAction extends BaseSessionAwareActionSupport implements ArticleHeaderAction {
  private static final Logger log = LoggerFactory.getLogger(FetchArticleTabsAction.class);
  private final ArrayList<String> messages = new ArrayList<String>();

  private static final int RELATED_AUTHOR_SEARCH_QUERY_SIZE = 4;

  /**
   * Returned by fetchArticle() when the given DOI is not in the repository.
   */
  public static final String ARTICLE_NOT_FOUND = "articleNotFound";
  public static final String EXPRESSION_OF_CONCERN_RELATION = "expressed-concern";
  public static final String CORRECTION_RELATION = "correction-forward";
  public static final String RETRACTION_RELATION = "retraction";

  private String articleURI;
  private String transformedArticle;
  private String annotationId = "";
  private String retraction = "";
  private String expressionOfConcern = "";
  private CitationView retractionCitation;
  private CitationView  eocCitation;
  private List<CitationView> articleCorrection = new ArrayList<CitationView>();

  private List<String> correspondingAuthor;
  private List<String> authorContributions;
  private List<String> competingInterest;

  private int pageCount = 0;
  private int numComments = 0;

  //commentary holds the comments that are being listed
  private AnnotationView[] commentary = new AnnotationView[0];
  private boolean isResearchArticle;
  private String publishedJournal = "";
  private ArticleInfo articleInfoX;
  private Document doc;
  private ArticleType articleType;
  private List<List<String>> articleIssues;
  private int trackbackCount;
  private List<AuthorView> authors;
  private List<CitationReference> references;
  private String journalAbbrev;
  private String relatedAuthorSearchQuery;
  private Set<JournalView> journalList;
  private ArticleAssetWrapper[] articleAssetWrapper;
  private AmbraFreemarkerConfig ambraFreemarkerConfig;
  private FetchArticleService fetchArticleService;
  private AnnotationService annotationService;
  private ArticleService articleService;
  private TrackbackService trackbackService;
  private UserService userService;
  private ArticleAssetService articleAssetService;
  private Set<ArticleCategory> categories;
  private CaptchaService captchaService;
  private UserProfile user;
  private String reCaptchaPublicKey;
  private boolean hasPDF;


  /**
   * Fetch the data for Article Tab
   *
   * @return "success" on success, "error" on error
   */
  public String fetchArticle() throws Exception {
    try {
      setCommonData();
      articleAssetWrapper = articleAssetService.listFiguresTables(articleInfoX.getDoi(), getAuthId());
      fetchAmendment();
      transformedArticle = fetchArticleService.getArticleAsHTML(articleInfoX);
    } catch (NoSuchArticleIdException e) {
      messages.add("No article found for id: " + articleURI);
      log.info("Could not find article: " + articleURI, e);
      return ARTICLE_NOT_FOUND;
    }

    recordArticleView();
    return SUCCESS;
  }

  /**
   * check if the article has any amendments; if the article has eoc or retraction, fetch the body and citation.
   * If the article has only corrections fetch the citations.
   *
   * @return String
   */
  private String fetchAmendment() {
    try {
      if (articleInfoX.getRelatedArticles() != null
              && !articleService.isCorrectionArticle(articleInfoX)
              && !articleService.isRetractionArticle(articleInfoX)
              && !articleService.isEocArticle(articleInfoX)) {

        for (RelatedArticleInfo relatedArticleInfo : articleInfoX.getRelatedArticles()) {
          if ((relatedArticleInfo.getArticleTypes() != null)) {
            // currently, we don't have many related articles; therefore, these lines
            // shouldn't cause performance overhead.
            Article article = articleService.getArticle(relatedArticleInfo.getDoi(), getAuthId());
            ArticleInfo articleInfo = articleService.getArticleInfo(relatedArticleInfo.getDoi(), getAuthId());
            Document document = this.fetchArticleService.getArticleDocument(articleInfo);

            if (RETRACTION_RELATION.equalsIgnoreCase(relatedArticleInfo.getRelationType()) &&
                    articleService.isRetractionArticle(relatedArticleInfo)) {

              retraction = this.fetchArticleService.getAmendmentBody(document);
              retractionCitation = buildCitationFromArticle(article);

              break;
            }

            if (EXPRESSION_OF_CONCERN_RELATION.equalsIgnoreCase(relatedArticleInfo.getRelationType()) &&
                    articleService.isEocArticle(relatedArticleInfo)) {

              expressionOfConcern = this.fetchArticleService.getAmendmentBody(document);
              eocCitation = buildCitationFromArticle(article);
              break;
            }

            if (CORRECTION_RELATION.equalsIgnoreCase(relatedArticleInfo.getRelationType()) &&
                    articleService.isCorrectionArticle(relatedArticleInfo)) {

              CitationView citation = buildCitationFromArticle(article);
              articleCorrection.add(citation);

            }
          }
        }
      }
    } catch (Exception e) {
      populateErrorMessages(e);
      return ERROR;
    }
    return SUCCESS;
  }

  private CitationView buildCitationFromArticle(Article article)  {
    CitationView citation =  CitationView.builder()
            .setDoi(article.getDoi())
            .seteLocationId(article.geteLocationId())
            .setUrl(article.getUrl())
            .setTitle(article.getTitle())
            .setJournal(article.getJournal())
            .setVolume(article.getVolume())
            .setIssue(article.getIssue())
            .setSummary(article.getDescription())
            .setPublisherName(article.getPublisherName())
            .setPublishedDate(article.getDate())
            .setAuthorList(article.getAuthors())
            .setCollaborativeAuthors(article.getCollaborativeAuthors())
            .build();
    return citation;
  }

  /**
   * Fetch data for Comments Tab
   *
   * @return "success" on success, "error" on error
   */
  public String fetchArticleComments() {
    try {
      setCommonData();

      numComments = annotationService.countAnnotations(articleInfoX.getId(),
          EnumSet.of(AnnotationType.COMMENT));

    } catch (Exception e) {
      populateErrorMessages(e);
      return ERROR;
    }
    return SUCCESS;
  }

  /**
   * Fetches data for Authors Tab
   *
   * @return "success" on success, "error" on error
   */
  public String fetchArticleAuthors() {
    try {
      setCommonData();      
    } catch (Exception e) {
      populateErrorMessages(e);
    }
    return SUCCESS;
  }

  /**
   * Fetches data for Metrics Tab
   *
   * @return "success" on success, "error" on error
   */
  public String fetchArticleMetrics() {
    try {
      setCommonData();
      trackbackCount = trackbackService.countTrackbacksForArticle(articleURI);
      //count all the comments
      numComments = annotationService.countAnnotations(articleInfoX.getId(),
          EnumSet.of(AnnotationType.COMMENT));
    } catch (Exception e) {
      populateErrorMessages(e);
      return ERROR;
    }
    return SUCCESS;
  }

  /**
   * Fetches data for Related Content Tab
   *
   * @return "success" on success, "error" on error
   */
  public String fetchArticleRelated() {
    try {
      setCommonData();      
      populateRelatedAuthorSearchQuery();
      user = getCurrentUser();
      reCaptchaPublicKey = captchaService.getPublicKey();
    } catch (Exception e) {
     populateErrorMessages(e);
    }
    return SUCCESS;
  }

  /** This method gets called when user click on crossref tile inside metrics tab
   * Fetches common data and nothing else
   *
   * @return "success" on succes, "error" on error
   */
  public String fetchArticleCrossRef() {
    try {
      setCommonData();
    } catch (NoSuchArticleIdException e) {
      messages.add("No article found for id: " + articleURI);
      log.info("Could not find article: " + articleURI, e);
      return ERROR;
    } catch (Exception e) {
      messages.add(e.getMessage());
      log.error("Error retrieving article: " + articleURI, e);
      return ERROR;
    }
    return SUCCESS;
  }

  /**
   * Sets up data used by the right hand column in the freemarker templates
   * <p/>
   * TODO: Review the data fetched by this; it's called on every tab and fetches more than is necessary (e.g.
   * articleInfo)
   *
   * @throws ApplicationException     when there is an error talking to the OTM
   * @throws NoSuchArticleIdException when the article can not be found
   */
  private void setCommonData() throws ApplicationException, NoSuchArticleIdException, NoSuchObjectIdException {
    validateArticleURI();
    articleInfoX = articleService.getArticleInfo(articleURI, getAuthId());
    hasPDF = true;
    if (articleAssetService.getArticleAsset(articleURI, "PDF", getAuthId()) == null) {
      hasPDF = false;
    }
    // sort the related articles by date
    Collections.sort(articleInfoX.getRelatedArticles());
    journalList = articleInfoX.getJournals();
    isResearchArticle = articleService.isResearchArticle(articleInfoX);
    articleIssues = articleService.getArticleIssues(articleURI);
    articleType = articleInfoX.getKnownArticleType();

    String pages = this.articleInfoX.getPages();

    if (pages != null && pages.indexOf("-") > 0 && pages.split("-").length > 1) {
      String t = pages.split("-")[1];

      try {
        pageCount = Integer.parseInt(t);
      } catch (NumberFormatException ex) {
        log.warn("Not able to parse page count from citation pages property with value of (" + t + ")");
        pageCount = 0;
      }
    }

    //TODO: Refactor this to not be spaghetti, all these properties should be made
    //to be part of articleInfo.  Rename articleInfo to articleView and populate articleView
    //In the service tier in whatever way is appropriate
    doc = this.fetchArticleService.getArticleDocument(articleInfoX);
    authors = this.fetchArticleService.getAuthors(doc);
    correspondingAuthor = this.fetchArticleService.getCorrespondingAuthors(doc);
    authorContributions = this.fetchArticleService.getAuthorContributions(doc);
    competingInterest = this.fetchArticleService.getAuthorCompetingInterests(doc);
    references = this.fetchArticleService.getReferences(doc);
    journalAbbrev = this.fetchArticleService.getJournalAbbreviation(doc);
    commentary = this.annotationService.listAnnotations(articleInfoX.getId(),
        EnumSet.of(AnnotationType.COMMENT), 
        AnnotationOrder.MOST_RECENT_REPLY);
    /**
     An article can be cross published, but we want the source journal.
     If in this collection an article eIssn matches the article's eIssn keep that value.
     freemarker_config.getDisplayName(journalContext)}">
     **/
    for (JournalView j : journalList) {
      if (articleInfoX.geteIssn().equals(j.geteIssn())) {
        publishedJournal = ambraFreemarkerConfig.getDisplayName(j.getJournalKey());
      }
    }

    this.categories = Cookies.setAdditionalCategoryFlags(articleInfoX.getCategories(), articleInfoX.getId());
  }

  @Override
  public boolean getHasAboutAuthorContent() {
    return authors != null ? AuthorView.anyHasAffiliation(authors)
        || CollectionUtils.isNotEmpty(correspondingAuthor)
        || CollectionUtils.isNotEmpty(authorContributions)
        || CollectionUtils.isNotEmpty(competingInterest) : false;
  }

  /**
   * This method is called only when request has x-pjax in its header. Rule is defined
   * in urlrewrite.xml
   *
   * Fetch data for Article tab
   *
   * @return "success" on success, "error" on error
   */
  public String fetchArticleContent() throws Exception {
    try {
      validateArticleURI();
      articleInfoX = articleService.getArticleInfo(articleURI, getAuthId());
      articleAssetWrapper = articleAssetService.listFiguresTables(articleInfoX.getDoi(), getAuthId());
      commentary = annotationService.listAnnotations(
          articleInfoX.getId(),
          EnumSet.of(AnnotationType.COMMENT),
          AnnotationOrder.MOST_RECENT_REPLY);
      fetchAmendment();
      transformedArticle = fetchArticleService.getArticleAsHTML(articleInfoX);
    } catch (Exception e) {
      populateErrorMessages(e);
      return (e instanceof  NoSuchArticleIdException ? ARTICLE_NOT_FOUND : ERROR);
    }
    //If the user is logged in, record this as an article view
    recordArticleView();
    return SUCCESS;
  }

  /**
   * This method is called only when request has x-pjax in its header. Rule is defined
   * in urlrewrite.xml
   *
   * Fetch data for Comments Tab
   *
   * @return "success" on success, "error" on error
   */
  public String fetchArticleCommentsContent() {
    try {
      validateArticleURI();
      articleInfoX = articleService.getArticleInfo(articleURI, getAuthId());
      commentary = annotationService.listAnnotations(
              articleInfoX.getId(),
              EnumSet.of(AnnotationType.COMMENT),
              AnnotationOrder.MOST_RECENT_REPLY);
    } catch (Exception e) {
      populateErrorMessages(e);
      return ERROR;
    }
    return SUCCESS;
  }

  /**
   * This method is called only when request has x-pjax in its header. Rule is defined
   * in urlrewrite.xml
   *
   * Fetches data for Authors Tab
   *
   * @return "success" on success, "error" on error
   */
  public String fetchArticleAuthorsContent() {
    try {
      validateArticleURI();
      articleInfoX = articleService.getArticleInfo(articleURI, getAuthId());
      doc = this.fetchArticleService.getArticleDocument(articleInfoX);
      authors = this.fetchArticleService.getAuthors(doc);
      correspondingAuthor = this.fetchArticleService.getCorrespondingAuthors(doc);
      authorContributions = this.fetchArticleService.getAuthorContributions(doc);
      competingInterest = this.fetchArticleService.getAuthorCompetingInterests(doc);
    } catch (Exception e) {
      populateErrorMessages(e);
      return ERROR;
    }
    return SUCCESS;
  }

  /**
   * This method is called only when request has x-pjax in its header. Rule is defined
   * in urlrewrite.xml
   *
   * Fetches data for Metrics Tab
   *
   * @return "success" on success, "error" on error
   */
  public String fetchArticleMetricsContent() {
    try {
      validateArticleURI();
      articleInfoX = articleService.getArticleInfo(articleURI, getAuthId());
      numComments = annotationService.countAnnotations(articleInfoX.getId(),
              EnumSet.of(AnnotationType.COMMENT));
      trackbackCount = trackbackService.countTrackbacksForArticle(articleURI);
    } catch (Exception e) {
      populateErrorMessages(e);
      return ERROR;
    }
    return SUCCESS;
  }

  /**
   * This method is called only when request has x-pjax in its header. Rule is defined
   * in urlrewrite.xml
   *
   * Fetches data for Related Content Tab
   *
   * @return "success" on success, "error" on error
   */
  public String fetchArticleRelatedContent() {
    try {
      validateArticleURI();
      articleInfoX = articleService.getArticleInfo(articleURI, getAuthId());
      populateRelatedAuthorSearchQuery();
      user = getCurrentUser();
      reCaptchaPublicKey = captchaService.getPublicKey();
    } catch (Exception e) {
      populateErrorMessages(e);
      return ERROR;
    }
    return SUCCESS;
  }

  /**
   * validate the article URI
   * @throws NoSuchArticleIdException
   */
  private void validateArticleURI() throws NoSuchArticleIdException {
    try {
      UriUtil.validateUri(articleURI, "articleURI=<" + articleURI + ">");
    } catch (Exception e) {
      throw new NoSuchArticleIdException(articleURI, e.getMessage(), e);
    }
  }

  /**
   * populate the error messages
   * @param e
   */
  private void populateErrorMessages(Exception e) {
    if (e instanceof NoSuchArticleIdException) {
      messages.add("No article found for id: " + articleURI);
      log.info("Could not find article: " + articleURI, e);
    }
    else {
      messages.add(e.getMessage());
      log.error("Error retrieving article: " + articleURI, e);
    }
  }

  /**
   * populate the author search query
   */
  private void populateRelatedAuthorSearchQuery() {
    // get the first two and the last two authors
    List<String> authors = articleInfoX.getAuthors();
    int authorSize = authors.size();
    relatedAuthorSearchQuery = "";
    if (authorSize <= RELATED_AUTHOR_SEARCH_QUERY_SIZE) {
      for (String author : authors) {
        relatedAuthorSearchQuery = relatedAuthorSearchQuery + "\"" + author + "\" OR ";
      }
      // remove the last ", OR "
      if (relatedAuthorSearchQuery.length() > 0) {
        relatedAuthorSearchQuery = relatedAuthorSearchQuery.substring(0, relatedAuthorSearchQuery.length() - 4);
      }
    } else {
      // get first 2
      relatedAuthorSearchQuery = "\"" + authors.get(0) + "\" OR ";
      relatedAuthorSearchQuery = relatedAuthorSearchQuery + "\"" + authors.get(1) + "\" OR ";
      // get last 2
      relatedAuthorSearchQuery = relatedAuthorSearchQuery + "\"" + authors.get(authorSize - 2) + "\" OR ";
      relatedAuthorSearchQuery = relatedAuthorSearchQuery + "\"" + authors.get(authorSize - 1) + "\"";
    }
  }

  /**
   * record the article view
   */
  private void recordArticleView() {
    //If the user is logged in, record this as an article view
    UserProfile user = getCurrentUser();
    if (user != null) {
      try {
        userService.recordArticleView(user.getID(), articleInfoX.getId(), ArticleView.Type.ARTICLE_VIEW);
      } catch (Exception e) {
        log.error("Error recording an article view for user: {} and article: {}", user.getID(), articleInfoX.getId());
        log.error(e.getMessage(), e);
      }
    }
  }

  /**
   * Set the fetch article service
   *
   * @param articleService articleService
   */
  @Required
  public void setArticleService(ArticleService articleService) {
    this.articleService = articleService;
  }

  /**
   * Set the fetch article service
   *
   * @param fetchArticleService fetchArticleService
   */
  @Required
  public void setFetchArticleService(final FetchArticleService fetchArticleService) {
    this.fetchArticleService = fetchArticleService;
  }

  /**
   * @param trackBackservice The trackBackService to set.
   */
  @Required
  public void setTrackBackService(TrackbackService trackBackservice) {
    this.trackbackService = trackBackservice;
  }

  /**
   * @param annotationService The annotationService to set.
   */
  @Required
  public void setAnnotationService(AnnotationService annotationService) {
    this.annotationService = annotationService;
  }

  /**
   * @param articleAssetService the articleAssetService
   */
  public void setArticleAssetService(ArticleAssetService articleAssetService) {
    this.articleAssetService = articleAssetService;
  }

  /**
   * @return articleURI
   */
  @RequiredStringValidator(message = "Article URI is required.")
  public String getArticleURI() {
    return articleURI;
  }

  /**
   * Set articleURI to fetch the article for.
   *
   * @param articleURI articleURI
   */
  public void setArticleURI(final String articleURI) {
    this.articleURI = articleURI;
  }

  /**
   * @return transformed output
   */
  public String getTransformedArticle() {
    return transformedArticle;
  }

  /**
   * Return the ArticleInfo from the Browse cache.
   * <p/>
   *
   * @return Returns the articleInfoX.
   */
  public ArticleInfo getArticleInfoX() {
    return articleInfoX;
  }

  /**
   * @return the articleAssetWrapper
   */
  public ArticleAssetWrapper[] getArticleAssetWrapper() {
    return articleAssetWrapper;
  }

  /**
   * @return Returns the annotationId.
   */
  public String getAnnotationId() {
    return annotationId;
  }

  /**
   * @param annotationId The annotationId to set.
   */
  public void setAnnotationId(String annotationId) {
    this.annotationId = annotationId;
  }

  /**
   * Gets the article Type
   *
   * @return Returns articleType
   */
  public ArticleType getArticleType() {
    return articleType;
  }

  public ArrayList<String> getMessages() {
    return messages;
  }

  /**
   * @return Returns the journalList.
   */
  public Set<JournalView> getJournalList() {
    return journalList;
  }

  /**
   * @return Returns the names and URIs of all of the Journals, Volumes, and Issues to which this Article has been
   *         attached.  This includes "collections", but does not include the
   */
  public List<List<String>> getArticleIssues() {
    return articleIssues;
  }

  /**
   * @return the isResearchArticle
   */
  public boolean getIsResearchArticle() {
    return isResearchArticle;
  }

  public int getTrackbackCount() {
    return trackbackCount;
  }

  public String getPublishedJournal() {
    return publishedJournal;
  }

  /**
   * If available, return the current count of pages.
   *
   * @return the current article's page count
   */
  public int getPageCount() {
    return pageCount;
  }

  /**
   * Return a comma delimited string of authors.
   *
   * @return Comma delimited string of authors
   */
  public String getAuthorNames() {
    return AuthorView.buildNameList(authors);
  }

  /**
   * Get the corresponding author
   *
   * @return
   */
  public List<String> getCorrespondingAuthor() {
    return this.correspondingAuthor;
  }

  /**
   * Get the author contributions
   *
   * @return
   */
  public List<String> getAuthorContributions() {
    return this.authorContributions;
  }

  /**
   * Get the authors competing interest
   *
   * @return
   */
  public List<String> getCompetingInterest() {
    return competingInterest;
  }

  /**
   *
   * @return body test of the article's retraction
   */
  public String getRetraction() {
    return this.retraction;
  }

  /**
   *
   * @return the citation for the article's retraction
   */
  public CitationView getRetractionCitation() {
    return retractionCitation;
  }

  /**
   *
   * @return the body text of Expression Of Concern
   */
  public String getExpressionOfConcern() {
    return expressionOfConcern;
  }

  public void setExpressionOfConcern(String expressionOfConcern) {
    this.expressionOfConcern = expressionOfConcern;
  }

  /**
   *
   * @return the citation of the article's expression of concern
   */
  public CitationView getEocCitation() {
    return eocCitation;
  }

  /**
   *
   * @return article corrections
   */
  public List<CitationView> getArticleCorrection() {
    return articleCorrection;
  }

  public void setArticleCorrection(List<CitationView> articleCorrection) {
    this.articleCorrection = articleCorrection;
  }


  public int getNumComments() {
    return numComments;
  }

  public AnnotationView[] getCommentary() {
    return commentary;
  }

  /**
   * Return a list of this article's categories.
   *
   * Note: These values may be different pending the user's cookies then the values stored in the database.
   *
   * If a user is logged in, a list is built of categories(and if they have been flagged) for the article
   * from the database
   *
   * If a user is not logged in, a list is built of categories for the article.  Then we append (from a cookie)
   * flagged categories for this article
   *
   * @return Return a list of this article's categories
   */
  public Set<ArticleCategory> getCategories() {
    return categories;
  }

  /**
   * Set the config class containing all of the properties used by the Freemarker templates so those values can be used
   * within this Action class.
   *
   * @param ambraFreemarkerConfig All of the configuration properties used by the Freemarker templates
   */
  @Required
  public void setAmbraFreemarkerConfig(final AmbraFreemarkerConfig ambraFreemarkerConfig) {
    this.ambraFreemarkerConfig = ambraFreemarkerConfig;
  }

  @Required
  public void setUserService(UserService userService) {
    this.userService = userService;
  }

  /**
   * Returns a list of author affiliations
   *
   * @return author affiliations
   */
  public List<AuthorView> getAuthors() {
    return this.authors;
  }

  /**
   * Get a list of contributing authors
   *
   * @return
   */
  public String getContributingAuthors() {
    return AuthorView.buildContributingAuthorsList(authors);
  }

  /**
   * Generate a list of authors grouped by affiliation
   *
   * @return a list of authors grouped by affiliation and sorted according to the order of the institutions in the xml
   *
   */
  public Set<Map.Entry<String, List<AuthorView>>> getAuthorsByAffiliation() throws RuntimeException{

    return fetchArticleService.getAuthorsByAffiliation(doc, authors).entrySet();

  }

  /**
   * Returns a list of citation references
   *
   * @return citation references
   */
  public List<CitationReference> getReferences() {
    return this.references;
  }

  /**
   * Returns abbreviated journal name
   *
   * @return abbreviated journal name
   */
  public String getJournalAbbrev() {
    return this.journalAbbrev;
  }

  /**
   * Returns article description
   * <p/>
   * //TODO: This is a pretty heavy weight function that gets called for every article request to get a value that
   * rarely changes.  Should we just make the value in the database correct on ingest?
   *
   * @return
   */
  public String getArticleDescription() {
    return TextUtils.transformXMLtoHtmlText(articleInfoX.getDescription());
  }

  /**
   * Returns related article author search query
   *
   * @return author name query
   */
  public String getRelatedAuthorSearchQuery() {
    return relatedAuthorSearchQuery;
  }

  public UserProfile getUser() {
    return user;
  }

  public String getReCaptchaPublicKey() {
    return reCaptchaPublicKey;
  }

  @Required
  public void setCaptchaService(CaptchaService captchaService) {
    this.captchaService = captchaService;
  }

  public boolean isHasPDF() {
    return hasPDF;
  }
}
