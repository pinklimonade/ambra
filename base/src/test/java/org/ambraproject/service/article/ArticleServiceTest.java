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
 *   distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.ambraproject.service.article;

import org.ambraproject.action.BaseTest;
import org.ambraproject.views.SearchHit;
import org.ambraproject.views.article.ArticleInfo;
import org.ambraproject.models.Article;
import org.ambraproject.models.ArticleAsset;
import org.ambraproject.models.ArticleAuthor;
import org.ambraproject.models.ArticleEditor;
import org.ambraproject.models.ArticleRelationship;
import org.ambraproject.models.Category;
import org.ambraproject.models.CitedArticle;
import org.ambraproject.models.CitedArticleAuthor;
import org.ambraproject.models.CitedArticleEditor;
import org.ambraproject.models.Issue;
import org.ambraproject.models.Journal;
import org.ambraproject.models.UserProfile;
import org.ambraproject.models.Volume;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * TODO: Test method: isResearchArticle(...)
 */
public class ArticleServiceTest extends BaseTest {
  private static final Logger log = LoggerFactory.getLogger(ArticleServiceTest.class);
  @Autowired
  protected ArticleService articleService;

  private static Article getArticle1() {
    Article article1 = new Article();
    article1.setDoi("info:doi/10.1371/Fake-Doi-For-article1");
    article1.setTitle("Fake Title for Article 1");
    article1.seteIssn("Fake EIssn for Article 1");
    article1.setState(Article.STATE_ACTIVE);
    article1.setArchiveName("Fake ArchiveName for Article 1");
    article1.setDescription("Fake Description for Article 1");
    article1.setRights("Fake Rights for Article 1");
    article1.setLanguage("Fake Language for Article 1");
    article1.setFormat("Fake Format for Article 1");
    article1.setVolume("Fake Volume for Article 1");
    article1.setIssue("Fake Issue for Article 1");
    article1.setPublisherLocation("Fake PublisherLocation for Article 1");
    article1.setPublisherName("Fake PublisherName for Article 1");
    article1.setStrkImgURI("Article 1 fake strkImgURI");
    article1.setJournal("Fake Journal for Article 1");

    List<String> collaborativeAuthorsForArticle1 = new LinkedList<String>();
    collaborativeAuthorsForArticle1.add("Fake CollaborativeAuthor ONE for Article 1");
    collaborativeAuthorsForArticle1.add("Fake CollaborativeAuthor TWO for Article 1");
    collaborativeAuthorsForArticle1.add("Fake CollaborativeAuthor THREE for Article 1");
    collaborativeAuthorsForArticle1.add("Fake CollaborativeAuthor FOUR for Article 1");
    article1.setCollaborativeAuthors(collaborativeAuthorsForArticle1);

    Set<Category> categoriesForArticle1 = new HashSet<Category>();
    Category category1ForArticle1 = new Category();
    category1ForArticle1.setPath("/Fake Main Category/for/category1ForArticle1");
    category1ForArticle1.setCreated(new Date());
    category1ForArticle1.setLastModified(new Date());
    categoriesForArticle1.add(category1ForArticle1);
    Category category2ForArticle1 = new Category();
    category2ForArticle1.setPath("/Fake Main Category/for/category2ForArticle1");
    category2ForArticle1.setCreated(new Date());
    category2ForArticle1.setLastModified(new Date());
    categoriesForArticle1.add(category2ForArticle1);
    Category category3ForArticle1 = new Category();
    category3ForArticle1.setPath("/Fake Main Category/for/category3ForArticle1");
    categoriesForArticle1.add(category3ForArticle1);
    article1.setCategories(categoriesForArticle1);

    List<ArticleAsset> assetsForArticle1 = new LinkedList<ArticleAsset>();
    ArticleAsset asset1ForArticle1 = new ArticleAsset();
    asset1ForArticle1.setContentType("Fake ContentType for asset1ForArticle1");
    asset1ForArticle1.setContextElement("Fake ContextElement for asset1ForArticle1");
    asset1ForArticle1.setDoi("info:doi/10.1371/Fake-Doi-For-asset1ForArticle1");
    asset1ForArticle1.setExtension("Fake Name for asset1ForArticle1");
    asset1ForArticle1.setSize(1000001l);
    asset1ForArticle1.setCreated(new Date());
    asset1ForArticle1.setLastModified(new Date());
    assetsForArticle1.add(asset1ForArticle1);
    ArticleAsset asset2ForArticle1 = new ArticleAsset();
    asset2ForArticle1.setContentType("Fake ContentType for asset2ForArticle1");
    asset2ForArticle1.setContextElement("Fake ContextElement for asset2ForArticle1");
    asset2ForArticle1.setDoi("info:doi/10.1371/Fake-Doi-For-asset2ForArticle1");
    asset2ForArticle1.setExtension("Fake Name for asset2ForArticle1");
    asset2ForArticle1.setSize(1000002l);
    assetsForArticle1.add(asset2ForArticle1);
    ArticleAsset asset3ForArticle1 = new ArticleAsset();
    asset3ForArticle1.setContentType("Fake ContentType for asset3ForArticle1");
    asset3ForArticle1.setContextElement("Fake ContextElement for asset3ForArticle1");
    asset3ForArticle1.setDoi("info:doi/10.1371/Fake-Doi-For-asset3ForArticle1");
    asset3ForArticle1.setExtension("Fake Name for asset3ForArticle1");
    asset3ForArticle1.setSize(1000003l);
    assetsForArticle1.add(asset3ForArticle1);
    article1.setAssets(assetsForArticle1);

    List<CitedArticle> citedArticlesForArticle1 = new LinkedList<CitedArticle>();
    CitedArticle citedArticle1ForArticle1 = new CitedArticle();

    List<CitedArticleAuthor> citedArticleAuthorsForCitedArticle1ForArticle1 = new LinkedList<CitedArticleAuthor>();
    CitedArticleAuthor citedArticleAuthor1ForCitedArticle1ForArticle1 = new CitedArticleAuthor();
    citedArticleAuthor1ForCitedArticle1ForArticle1.setFullName("Fake FullName for citedArticleAuthor1ForCitedArticle1ForArticle1");
    citedArticleAuthor1ForCitedArticle1ForArticle1.setGivenNames("Fake GivenNames for citedArticleAuthor1ForCitedArticle1ForArticle1");
    citedArticleAuthor1ForCitedArticle1ForArticle1.setSuffix("Fake Suffix for citedArticleAuthor1ForCitedArticle1ForArticle1");
    citedArticleAuthor1ForCitedArticle1ForArticle1.setSurnames("Fake Surnames for citedArticleAuthor1ForCitedArticle1ForArticle1");
    citedArticleAuthorsForCitedArticle1ForArticle1.add(citedArticleAuthor1ForCitedArticle1ForArticle1);
    CitedArticleAuthor citedArticleAuthor2ForCitedArticle1ForArticle1 = new CitedArticleAuthor();
    citedArticleAuthor2ForCitedArticle1ForArticle1.setFullName("Fake FullName for citedArticleAuthor2ForCitedArticle1ForArticle1");
    citedArticleAuthor2ForCitedArticle1ForArticle1.setGivenNames("Fake GivenNames for citedArticleAuthor2ForCitedArticle1ForArticle1");
    citedArticleAuthor2ForCitedArticle1ForArticle1.setSuffix("Fake Suffix for citedArticleAuthor2ForCitedArticle1ForArticle1");
    citedArticleAuthor2ForCitedArticle1ForArticle1.setSurnames("Fake Surnames for citedArticleAuthor2ForCitedArticle1ForArticle1");
    citedArticleAuthorsForCitedArticle1ForArticle1.add(citedArticleAuthor2ForCitedArticle1ForArticle1);
    citedArticle1ForArticle1.setAuthors(citedArticleAuthorsForCitedArticle1ForArticle1);
    citedArticle1ForArticle1.setCitationType("Fake CitationType for citedArticle1ForArticle1");

    List<String> collaborativeAuthorsForCitedArticle1ForArticle1 = new LinkedList<String>();
    collaborativeAuthorsForCitedArticle1ForArticle1.add("Fake CollaborativeAuthor ONE for collaborativeAuthorsForCitedArticle1ForArticle1");
    collaborativeAuthorsForCitedArticle1ForArticle1.add("Fake CollaborativeAuthor TWO for collaborativeAuthorsForCitedArticle1ForArticle1");
    collaborativeAuthorsForCitedArticle1ForArticle1.add("Fake CollaborativeAuthor THREE for collaborativeAuthorsForCitedArticle1ForArticle1");
    collaborativeAuthorsForCitedArticle1ForArticle1.add("Fake CollaborativeAuthor FOUR for collaborativeAuthorsForCitedArticle1ForArticle1");
    citedArticle1ForArticle1.setCollaborativeAuthors(collaborativeAuthorsForCitedArticle1ForArticle1);

    citedArticle1ForArticle1.setDay("Fake Day for citedArticle1ForArticle1");
    citedArticle1ForArticle1.setDisplayYear("Fake DisplayYear for citedArticle1ForArticle1");
    citedArticle1ForArticle1.setDoi("Fake Doi for citedArticle1ForArticle1");

    List<CitedArticleEditor> citedArticleEditorsForArticle1 = new LinkedList<CitedArticleEditor>();
    CitedArticleEditor citedArticleEditor1ForCitedArticle1ForArticle1 = new CitedArticleEditor();
    citedArticleEditor1ForCitedArticle1ForArticle1.setFullName("Fake FullName for citedArticleEditor1ForCitedArticle1ForArticle1");
    citedArticleEditor1ForCitedArticle1ForArticle1.setGivenNames("Fake GivenNames for citedArticleEditor1ForCitedArticle1ForArticle1");
    citedArticleEditor1ForCitedArticle1ForArticle1.setSuffix("Fake Suffix for citedArticleEditor1ForCitedArticle1ForArticle1");
    citedArticleEditor1ForCitedArticle1ForArticle1.setSurnames("Fake Surnames for citedArticleEditor1ForCitedArticle1ForArticle1");
    citedArticleEditorsForArticle1.add(citedArticleEditor1ForCitedArticle1ForArticle1);
    CitedArticleEditor citedArticleEditor2ForCitedArticle1ForArticle1 = new CitedArticleEditor();
    citedArticleEditor2ForCitedArticle1ForArticle1.setFullName("Fake FullName for citedArticleEditor2ForCitedArticle1ForArticle1");
    citedArticleEditor2ForCitedArticle1ForArticle1.setGivenNames("Fake GivenNames for citedArticleEditor2ForCitedArticle1ForArticle1");
    citedArticleEditor2ForCitedArticle1ForArticle1.setSuffix("Fake Suffix for citedArticleEditor2ForCitedArticle1ForArticle1");
    citedArticleEditor2ForCitedArticle1ForArticle1.setSurnames("Fake Surnames for citedArticleEditor2ForCitedArticle1ForArticle1");
    citedArticleEditorsForArticle1.add(citedArticleEditor2ForCitedArticle1ForArticle1);
    citedArticle1ForArticle1.setEditors(citedArticleEditorsForArticle1);

    citedArticle1ForArticle1.seteLocationID("Fake eLocationID for citedArticle1ForArticle1");
    citedArticle1ForArticle1.setIssue("Fake Issue for citedArticle1ForArticle1");
    citedArticle1ForArticle1.setJournal("Fake Journal for citedArticle1ForArticle1");
    citedArticle1ForArticle1.setKey("Fake Key for citedArticle1ForArticle1");
    citedArticle1ForArticle1.setMonth("Fake Month for citedArticle1ForArticle1");
    citedArticle1ForArticle1.setNote("Fake Note for citedArticle1ForArticle1");
    citedArticle1ForArticle1.setPages("Fake Pages for citedArticle1ForArticle1");
    citedArticle1ForArticle1.setPublisherLocation("Fake PublisherLocation for citedArticle1ForArticle1");
    citedArticle1ForArticle1.setSummary("Fake Summary for citedArticle1ForArticle1");
    citedArticle1ForArticle1.setTitle("Fake Title for citedArticle1ForArticle1");
    citedArticle1ForArticle1.setUrl("Fake Url for citedArticle1ForArticle1");
    citedArticle1ForArticle1.setVolume("Fake Volume for citedArticle1ForArticle1");
    citedArticle1ForArticle1.setVolumeNumber(new Integer(1000004));
    citedArticle1ForArticle1.setYear(new Integer(1000005));
    article1.setCitedArticles(citedArticlesForArticle1);

    List<ArticleAuthor> authorsForArticle1 = new LinkedList<ArticleAuthor>();
    ArticleAuthor articleAuthor1ForArticle1 = new ArticleAuthor();
    articleAuthor1ForArticle1.setFullName("Fake FullName for articleAuthor1ForArticle1");
    articleAuthor1ForArticle1.setGivenNames("Fake GivenNames for articleAuthor1ForArticle1");
    articleAuthor1ForArticle1.setSuffix("Fake Suffix for articleAuthor1ForArticle1");
    articleAuthor1ForArticle1.setSurnames("Fake Surnames for articleAuthor1ForArticle1");
    authorsForArticle1.add(articleAuthor1ForArticle1);
    ArticleAuthor articleAuthor2ForArticle1 = new ArticleAuthor();
    articleAuthor2ForArticle1.setFullName("Fake FullName for articleAuthor2ForArticle1");
    articleAuthor2ForArticle1.setGivenNames("Fake GivenNames for articleAuthor2ForArticle1");
    articleAuthor2ForArticle1.setSuffix("Fake Suffix for articleAuthor2ForArticle1");
    articleAuthor2ForArticle1.setSurnames("Fake Surnames for articleAuthor2ForArticle1");
    authorsForArticle1.add(articleAuthor2ForArticle1);
    ArticleAuthor articleAuthor3ForArticle1 = new ArticleAuthor();
    articleAuthor3ForArticle1.setFullName("Fake FullName for articleAuthor3ForArticle1");
    articleAuthor3ForArticle1.setGivenNames("Fake GivenNames for articleAuthor3ForArticle1");
    articleAuthor3ForArticle1.setSuffix("Fake Suffix for articleAuthor3ForArticle1");
    articleAuthor3ForArticle1.setSurnames("Fake Surnames for articleAuthor3ForArticle1");
    authorsForArticle1.add(articleAuthor3ForArticle1);
    article1.setAuthors(authorsForArticle1);

    List<ArticleEditor> editorsForArticle1 = new LinkedList<ArticleEditor>();
    ArticleEditor articleEditor1ForArticle1 = new ArticleEditor();
    articleEditor1ForArticle1.setFullName("Fake FullName for articleEditor1ForArticle1");
    articleEditor1ForArticle1.setGivenNames("Fake GivenNames for articleEditor1ForArticle1");
    articleEditor1ForArticle1.setSuffix("Fake Suffix for articleEditor1ForArticle1");
    articleEditor1ForArticle1.setSurnames("Fake Surnames for articleEditor1ForArticle1");
    editorsForArticle1.add(articleEditor1ForArticle1);
    ArticleEditor articleEditor2ForArticle1 = new ArticleEditor();
    articleEditor2ForArticle1.setFullName("Fake FullName for articleEditor2ForArticle1");
    articleEditor2ForArticle1.setGivenNames("Fake GivenNames for articleEditor2ForArticle1");
    articleEditor2ForArticle1.setSuffix("Fake Suffix for articleEditor2ForArticle1");
    articleEditor2ForArticle1.setSurnames("Fake Surnames for articleEditor2ForArticle1");
    editorsForArticle1.add(articleEditor2ForArticle1);
    ArticleEditor articleEditor3ForArticle1 = new ArticleEditor();
    articleEditor3ForArticle1.setFullName("Fake FullName for articleEditor3ForArticle1");
    articleEditor3ForArticle1.setGivenNames("Fake GivenNames for articleEditor3ForArticle1");
    articleEditor3ForArticle1.setSuffix("Fake Suffix for articleEditor3ForArticle1");
    articleEditor3ForArticle1.setSurnames("Fake Surnames for articleEditor3ForArticle1");
    editorsForArticle1.add(articleEditor3ForArticle1);
    article1.setEditors(editorsForArticle1);

    article1.setDate(new Date());

    List<ArticleRelationship> relatedArticlesForArticle1 = new LinkedList<ArticleRelationship>();
    ArticleRelationship relatedArticle1ForArticle1 = new ArticleRelationship();
    relatedArticle1ForArticle1.setOtherArticleDoi("Fake OtherArticleDoi for relatedArticle1ForArticle1");
    relatedArticle1ForArticle1.setOtherArticleID(new Long(1000006l));
    relatedArticle1ForArticle1.setParentArticle(new Article());     //  TODO: Make this a "real" dummy Article object!
    relatedArticle1ForArticle1.setType("Fake%20Type%20for%20relatedArticle1ForArticle1");
    relatedArticlesForArticle1.add(relatedArticle1ForArticle1);
    ArticleRelationship relatedArticle2ForArticle1 = new ArticleRelationship();
    relatedArticle2ForArticle1.setOtherArticleDoi("Fake OtherArticleDoi for relatedArticle2ForArticle1");
    relatedArticle2ForArticle1.setOtherArticleID(new Long(1000007l));
    relatedArticle2ForArticle1.setParentArticle(new Article());     //  TODO: Make this a "real" dummy Article object!
    relatedArticle2ForArticle1.setType("Fake%20Type%20for%20relatedArticle2ForArticle1");
    relatedArticlesForArticle1.add(relatedArticle2ForArticle1);

    //TODO: Can't save related articles because they aren't 'real' yet.
    //article1.setRelatedArticles(relatedArticlesForArticle1);

    Set<String> typesForArticle1 = new HashSet<String>();
    typesForArticle1.add("Fake%20Type%20ONE%20for%20Article1");
    typesForArticle1.add("Fake%20Type%20TWO%20for%20Article1");
    typesForArticle1.add("Fake%20Type%20THREE%20for%20Article1");
    typesForArticle1.add("Fake%20Type%20FOUR%20for%20Article1");
    article1.setTypes(typesForArticle1);

    return article1;
  }

  private static Article getArticle2() {
    Article article2 = new Article();
    article2.setDoi("info:doi/10.1371/Fake-Doi-For-article2");
    article2.setTitle("Fake Title for Article 1");
    article2.seteIssn("Fake EIssn for Article 1");
    article2.setState(Article.STATE_ACTIVE);
    article2.setArchiveName("Fake ArchiveName for Article 1");
    article2.setDescription("Fake Description for Article 1");
    article2.setRights("Fake Rights for Article 1");
    article2.setLanguage("Fake Language for Article 1");
    article2.setFormat("Fake Format for Article 1");
    article2.setVolume("Fake Volume for Article 1");
    article2.setIssue("Fake Issue for Article 1");
    article2.setPublisherLocation("Fake PublisherLocation for Article 1");
    article2.setPublisherName("Fake PublisherName for Article 1");
    article2.setStrkImgURI("Article 2 fake strkImgURI");
    article2.setJournal("Fake Journal for Article 1");

    List<String> collaborativeAuthorsForArticle2 = new LinkedList<String>();
    collaborativeAuthorsForArticle2.add("Fake CollaborativeAuthor ONE for Article 1");
    collaborativeAuthorsForArticle2.add("Fake CollaborativeAuthor TWO for Article 1");
    collaborativeAuthorsForArticle2.add("Fake CollaborativeAuthor THREE for Article 1");
    collaborativeAuthorsForArticle2.add("Fake CollaborativeAuthor FOUR for Article 1");
    article2.setCollaborativeAuthors(collaborativeAuthorsForArticle2);

    Set<Category> categoriesForArticle2 = new HashSet<Category>();
    Category category1ForArticle2 = new Category();
    category1ForArticle2.setPath("/Fake Main Category/for/category1ForArticle2");
    category1ForArticle2.setCreated(new Date());
    category1ForArticle2.setLastModified(new Date());
    categoriesForArticle2.add(category1ForArticle2);
    Category category2ForArticle2 = new Category();
    category2ForArticle2.setPath("/Fake Main Category/for/category2ForArticle2");
    category2ForArticle2.setCreated(new Date());
    category2ForArticle2.setLastModified(new Date());
    categoriesForArticle2.add(category2ForArticle2);
    Category category3ForArticle2 = new Category();
    category3ForArticle2.setPath("/Fake Main Category/for/category3ForArticle2");
    categoriesForArticle2.add(category3ForArticle2);
    article2.setCategories(categoriesForArticle2);

    List<ArticleAsset> assetsForArticle2 = new LinkedList<ArticleAsset>();
    ArticleAsset asset1ForArticle2 = new ArticleAsset();
    asset1ForArticle2.setContentType("Fake ContentType for asset1ForArticle2");
    asset1ForArticle2.setContextElement("Fake ContextElement for asset1ForArticle2");
    asset1ForArticle2.setDoi("info:doi/10.1371/Fake-Doi-For-asset1ForArticle2");
    asset1ForArticle2.setExtension("Fake Name for asset1ForArticle2");
    asset1ForArticle2.setSize(1000101l);
    asset1ForArticle2.setCreated(new Date());
    asset1ForArticle2.setLastModified(new Date());
    assetsForArticle2.add(asset1ForArticle2);
    ArticleAsset asset2ForArticle2 = new ArticleAsset();
    asset2ForArticle2.setContentType("Fake ContentType for asset2ForArticle2");
    asset2ForArticle2.setContextElement("Fake ContextElement for asset2ForArticle2");
    asset2ForArticle2.setDoi("info:doi/10.1371/Fake-Doi-For-asset2ForArticle2");
    asset2ForArticle2.setExtension("Fake Name for asset2ForArticle2");
    asset2ForArticle2.setSize(1000102l);
    assetsForArticle2.add(asset2ForArticle2);
    ArticleAsset asset3ForArticle2 = new ArticleAsset();
    asset3ForArticle2.setContentType("Fake ContentType for asset3ForArticle2");
    asset3ForArticle2.setContextElement("Fake ContextElement for asset3ForArticle2");
    asset3ForArticle2.setDoi("info:doi/10.1371/Fake-Doi-For-asset3ForArticle2");
    asset3ForArticle2.setExtension("Fake Name for asset3ForArticle2");
    asset3ForArticle2.setSize(1000103l);
    assetsForArticle2.add(asset3ForArticle2);
    article2.setAssets(assetsForArticle2);

    List<CitedArticle> citedArticlesForArticle2 = new LinkedList<CitedArticle>();
    CitedArticle citedArticle2ForArticle2 = new CitedArticle();

    List<CitedArticleAuthor> citedArticleAuthorsForCitedArticle2ForArticle2 = new LinkedList<CitedArticleAuthor>();
    CitedArticleAuthor citedArticleAuthor1ForCitedArticle2ForArticle2 = new CitedArticleAuthor();
    citedArticleAuthor1ForCitedArticle2ForArticle2.setFullName("Fake FullName for citedArticleAuthor1ForCitedArticle2ForArticle2");
    citedArticleAuthor1ForCitedArticle2ForArticle2.setGivenNames("Fake GivenNames for citedArticleAuthor1ForCitedArticle2ForArticle2");
    citedArticleAuthor1ForCitedArticle2ForArticle2.setSuffix("Fake Suffix for citedArticleAuthor1ForCitedArticle2ForArticle2");
    citedArticleAuthor1ForCitedArticle2ForArticle2.setSurnames("Fake Surnames for citedArticleAuthor1ForCitedArticle2ForArticle2");
    citedArticleAuthorsForCitedArticle2ForArticle2.add(citedArticleAuthor1ForCitedArticle2ForArticle2);
    CitedArticleAuthor citedArticleAuthor2ForCitedArticle2ForArticle2 = new CitedArticleAuthor();
    citedArticleAuthor2ForCitedArticle2ForArticle2.setFullName("Fake FullName for citedArticleAuthor2ForCitedArticle2ForArticle2");
    citedArticleAuthor2ForCitedArticle2ForArticle2.setGivenNames("Fake GivenNames for citedArticleAuthor2ForCitedArticle2ForArticle2");
    citedArticleAuthor2ForCitedArticle2ForArticle2.setSuffix("Fake Suffix for citedArticleAuthor2ForCitedArticle2ForArticle2");
    citedArticleAuthor2ForCitedArticle2ForArticle2.setSurnames("Fake Surnames for citedArticleAuthor2ForCitedArticle2ForArticle2");
    citedArticleAuthorsForCitedArticle2ForArticle2.add(citedArticleAuthor2ForCitedArticle2ForArticle2);
    citedArticle2ForArticle2.setAuthors(citedArticleAuthorsForCitedArticle2ForArticle2);
    citedArticle2ForArticle2.setCitationType("Fake CitationType for citedArticle2ForArticle2");

    List<String> collaborativeAuthorsForCitedArticle2ForArticle2 = new LinkedList<String>();
    collaborativeAuthorsForCitedArticle2ForArticle2.add("Fake CollaborativeAuthor ONE for collaborativeAuthorsForCitedArticle2ForArticle2");
    collaborativeAuthorsForCitedArticle2ForArticle2.add("Fake CollaborativeAuthor TWO for collaborativeAuthorsForCitedArticle2ForArticle2");
    collaborativeAuthorsForCitedArticle2ForArticle2.add("Fake CollaborativeAuthor THREE for collaborativeAuthorsForCitedArticle2ForArticle2");
    collaborativeAuthorsForCitedArticle2ForArticle2.add("Fake CollaborativeAuthor FOUR for collaborativeAuthorsForCitedArticle2ForArticle2");
    citedArticle2ForArticle2.setCollaborativeAuthors(collaborativeAuthorsForCitedArticle2ForArticle2);

    citedArticle2ForArticle2.setDay("Fake Day for citedArticle2ForArticle2");
    citedArticle2ForArticle2.setDisplayYear("Fake DisplayYear for citedArticle2ForArticle2");
    citedArticle2ForArticle2.setDoi("Fake Doi for citedArticle2ForArticle2");

    List<CitedArticleEditor> citedArticleEditorsForArticle2 = new LinkedList<CitedArticleEditor>();
    CitedArticleEditor citedArticleEditor1ForCitedArticle2ForArticle2 = new CitedArticleEditor();
    citedArticleEditor1ForCitedArticle2ForArticle2.setFullName("Fake FullName for citedArticleEditor1ForCitedArticle2ForArticle2");
    citedArticleEditor1ForCitedArticle2ForArticle2.setGivenNames("Fake GivenNames for citedArticleEditor1ForCitedArticle2ForArticle2");
    citedArticleEditor1ForCitedArticle2ForArticle2.setSuffix("Fake Suffix for citedArticleEditor1ForCitedArticle2ForArticle2");
    citedArticleEditor1ForCitedArticle2ForArticle2.setSurnames("Fake Surnames for citedArticleEditor1ForCitedArticle2ForArticle2");
    citedArticleEditorsForArticle2.add(citedArticleEditor1ForCitedArticle2ForArticle2);
    CitedArticleEditor citedArticleEditor2ForCitedArticle2ForArticle2 = new CitedArticleEditor();
    citedArticleEditor2ForCitedArticle2ForArticle2.setFullName("Fake FullName for citedArticleEditor2ForCitedArticle2ForArticle2");
    citedArticleEditor2ForCitedArticle2ForArticle2.setGivenNames("Fake GivenNames for citedArticleEditor2ForCitedArticle2ForArticle2");
    citedArticleEditor2ForCitedArticle2ForArticle2.setSuffix("Fake Suffix for citedArticleEditor2ForCitedArticle2ForArticle2");
    citedArticleEditor2ForCitedArticle2ForArticle2.setSurnames("Fake Surnames for citedArticleEditor2ForCitedArticle2ForArticle2");
    citedArticleEditorsForArticle2.add(citedArticleEditor2ForCitedArticle2ForArticle2);
    citedArticle2ForArticle2.setEditors(citedArticleEditorsForArticle2);

    citedArticle2ForArticle2.seteLocationID("Fake eLocationID for citedArticle2ForArticle2");
    citedArticle2ForArticle2.setIssue("Fake Issue for citedArticle2ForArticle2");
    citedArticle2ForArticle2.setJournal("Fake Journal for citedArticle2ForArticle2");
    citedArticle2ForArticle2.setKey("Fake Key for citedArticle2ForArticle2");
    citedArticle2ForArticle2.setMonth("Fake Month for citedArticle2ForArticle2");
    citedArticle2ForArticle2.setNote("Fake Note for citedArticle2ForArticle2");
    citedArticle2ForArticle2.setPages("Fake Pages for citedArticle2ForArticle2");
    citedArticle2ForArticle2.setPublisherLocation("Fake PublisherLocation for citedArticle2ForArticle2");
    citedArticle2ForArticle2.setSummary("Fake Summary for citedArticle2ForArticle2");
    citedArticle2ForArticle2.setTitle("Fake Title for citedArticle2ForArticle2");
    citedArticle2ForArticle2.setUrl("Fake Url for citedArticle2ForArticle2");
    citedArticle2ForArticle2.setVolume("Fake Volume for citedArticle2ForArticle2");
    citedArticle2ForArticle2.setVolumeNumber(new Integer(1000104));
    citedArticle2ForArticle2.setYear(new Integer(1000105));
    article2.setCitedArticles(citedArticlesForArticle2);

    List<ArticleAuthor> authorsForArticle2 = new LinkedList<ArticleAuthor>();
    ArticleAuthor articleAuthor1ForArticle2 = new ArticleAuthor();
    articleAuthor1ForArticle2.setFullName("Fake FullName for articleAuthor1ForArticle2");
    articleAuthor1ForArticle2.setGivenNames("Fake GivenNames for articleAuthor1ForArticle2");
    articleAuthor1ForArticle2.setSuffix("Fake Suffix for articleAuthor1ForArticle2");
    articleAuthor1ForArticle2.setSurnames("Fake Surnames for articleAuthor1ForArticle2");
    authorsForArticle2.add(articleAuthor1ForArticle2);
    ArticleAuthor articleAuthor2ForArticle2 = new ArticleAuthor();
    articleAuthor2ForArticle2.setFullName("Fake FullName for articleAuthor2ForArticle2");
    articleAuthor2ForArticle2.setGivenNames("Fake GivenNames for articleAuthor2ForArticle2");
    articleAuthor2ForArticle2.setSuffix("Fake Suffix for articleAuthor2ForArticle2");
    articleAuthor2ForArticle2.setSurnames("Fake Surnames for articleAuthor2ForArticle2");
    authorsForArticle2.add(articleAuthor2ForArticle2);
    ArticleAuthor articleAuthor3ForArticle2 = new ArticleAuthor();
    articleAuthor3ForArticle2.setFullName("Fake FullName for articleAuthor3ForArticle2");
    articleAuthor3ForArticle2.setGivenNames("Fake GivenNames for articleAuthor3ForArticle2");
    articleAuthor3ForArticle2.setSuffix("Fake Suffix for articleAuthor3ForArticle2");
    articleAuthor3ForArticle2.setSurnames("Fake Surnames for articleAuthor3ForArticle2");
    authorsForArticle2.add(articleAuthor3ForArticle2);
    article2.setAuthors(authorsForArticle2);

    List<ArticleEditor> editorsForArticle2 = new LinkedList<ArticleEditor>();
    ArticleEditor articleEditor1ForArticle2 = new ArticleEditor();
    articleEditor1ForArticle2.setFullName("Fake FullName for articleEditor1ForArticle2");
    articleEditor1ForArticle2.setGivenNames("Fake GivenNames for articleEditor1ForArticle2");
    articleEditor1ForArticle2.setSuffix("Fake Suffix for articleEditor1ForArticle2");
    articleEditor1ForArticle2.setSurnames("Fake Surnames for articleEditor1ForArticle2");
    editorsForArticle2.add(articleEditor1ForArticle2);
    ArticleEditor articleEditor2ForArticle2 = new ArticleEditor();
    articleEditor2ForArticle2.setFullName("Fake FullName for articleEditor2ForArticle2");
    articleEditor2ForArticle2.setGivenNames("Fake GivenNames for articleEditor2ForArticle2");
    articleEditor2ForArticle2.setSuffix("Fake Suffix for articleEditor2ForArticle2");
    articleEditor2ForArticle2.setSurnames("Fake Surnames for articleEditor2ForArticle2");
    editorsForArticle2.add(articleEditor2ForArticle2);
    ArticleEditor articleEditor3ForArticle2 = new ArticleEditor();
    articleEditor3ForArticle2.setFullName("Fake FullName for articleEditor3ForArticle2");
    articleEditor3ForArticle2.setGivenNames("Fake GivenNames for articleEditor3ForArticle2");
    articleEditor3ForArticle2.setSuffix("Fake Suffix for articleEditor3ForArticle2");
    articleEditor3ForArticle2.setSurnames("Fake Surnames for articleEditor3ForArticle2");
    editorsForArticle2.add(articleEditor3ForArticle2);
    article2.setEditors(editorsForArticle2);

    article2.setDate(new Date());

    List<ArticleRelationship> relatedArticlesForArticle2 = new LinkedList<ArticleRelationship>();
    ArticleRelationship relatedArticle1ForArticle2 = new ArticleRelationship();
    relatedArticle1ForArticle2.setOtherArticleDoi("Fake OtherArticleDoi for relatedArticle2ForArticle2");
    relatedArticle1ForArticle2.setOtherArticleID(new Long(1000106l));
    relatedArticle1ForArticle2.setParentArticle(new Article());     //  TODO: Make this a "real" dummy Article object!
    relatedArticle1ForArticle2.setType("Fake%20Type%20for%20relatedArticle2ForArticle2");
    relatedArticlesForArticle2.add(relatedArticle1ForArticle2);
    ArticleRelationship relatedArticle2ForArticle2 = new ArticleRelationship();
    relatedArticle2ForArticle2.setOtherArticleDoi("Fake OtherArticleDoi for relatedArticle2ForArticle2");
    relatedArticle2ForArticle2.setOtherArticleID(new Long(1000107l));
    relatedArticle2ForArticle2.setParentArticle(new Article());     //  TODO: Make this a "real" dummy Article object!
    relatedArticle2ForArticle2.setType("Fake%20Type%20for%20relatedArticle2ForArticle2");
    relatedArticlesForArticle2.add(relatedArticle2ForArticle2);

    //TODO: Can't save related articles because they aren't 'real' yet.
    //article2.setRelatedArticles(relatedArticlesForArticle2);

    Set<String> typesForArticle2 = new HashSet<String>();
    typesForArticle2.add("Fake%20Type%20ONE%20for%20Article2");
    typesForArticle2.add("Fake%20Type%20TWO%20for%20Article2");
    typesForArticle2.add("Fake%20Type%20THREE%20for%20Article2");
    typesForArticle2.add("Fake%20Type%20FOUR%20for%20Article2");
    article2.setTypes(typesForArticle2);

    return article2;
  }

  @DataProvider(name = "savedArticlesURI")
  public Object[][] savedArticlesURI() {
    log.debug("data-savedArticles");

    dummyDataStore.store(getArticle1());
    dummyDataStore.store(getArticle2());

    return new Object[][]{
      { getArticle1().getDoi(), getArticle1() },
      { getArticle2().getDoi(), getArticle2() }
    };
  }

  @DataProvider(name = "savedArticlesID")
  public Object[][] savedArticles() {
    log.debug("data-savedArticles");

    Article article1 = getArticle1();
    Article article2 = getArticle2();

    dummyDataStore.store(article1);
    dummyDataStore.store(article2);

    return new Object[][]{
        { article1.getID(), getArticle1() },
        { article2.getID(), getArticle2() }
    };
  }

  @Test(dataProvider = "savedArticlesURI")
  public void testGetArticle(String articleDoi, Article expectedArticle) throws NoSuchArticleIdException {
    log.debug("test-testGetArticle");

    Article article = articleService.getArticle(articleDoi, DEFAULT_ADMIN_AUTHID);

    assertNotNull(article, "returned null article");
    assertEquals(article.getDoi(), articleDoi, "returned article with incorrect DOI.  Expected "
        + articleDoi + " but returned " + article.getDoi());

    compareArticles(article, expectedArticle);
  }

  @DataProvider(name = "savedArticlesStrikingImageURI")
  public Object[][] strikingImageURI() {
    log.debug("data-savedArticles");

    dummyDataStore.store(getArticle1());
    dummyDataStore.store(getArticle2());

    return new Object[][]{
        { getArticle1().getStrkImgURI(), getArticle1() },
        { getArticle2().getStrkImgURI(), getArticle2() }
    };
  }


  @Test(dataProvider = "savedArticlesStrikingImageURI")
  public void testGetStrikingImage(String imageURI, Article expectedArticle) throws NoSuchArticleIdException {
    log.debug("test-testGetArticle");

    Article article = articleService.getArticle(expectedArticle.getDoi(), DEFAULT_ADMIN_AUTHID);

    assertEquals(article.getStrkImgURI(), imageURI, "returned article with incorrect striking image URI.  Expected "
        + imageURI + " but returned " + article.getStrkImgURI());
  }

  @Test(dataProvider = "savedArticlesID")
  public void testGetArticle(Long articleID, Article expectedArticle) throws NoSuchArticleIdException {
    log.debug("test-testGetArticle");

    Article article = articleService.getArticle(articleID, DEFAULT_ADMIN_AUTHID);

    assertNotNull(article, "returned null article");
    assertEquals(article.getID(), articleID, "returned article with incorrect ID");

    compareArticles(article, expectedArticle);
  }

  @DataProvider(name = "journalVolumeIssue")
  public Object[][] journalVolumeIssue() {
    log.debug("data-journalVolumeIssue");

    List<String> articleDoisBoth = new LinkedList<String>();
    articleDoisBoth.add(getArticle1().getDoi());
    articleDoisBoth.add(getArticle2().getDoi());

    List<String> articleDoisOne = new LinkedList<String>();
    articleDoisOne.add(getArticle1().getDoi());

    //Issues
    Issue bothArticleIssue = new Issue(); //Contains both articles
    bothArticleIssue.setArticleDois(articleDoisBoth);
    bothArticleIssue.setDisplayName("issue with TWO articles");
    bothArticleIssue.setIssueUri("info:doi/articleServiceTest/issue/1");
    dummyDataStore.store(bothArticleIssue);

    Issue oneArticleIssue = new Issue(); //contains only the first article
    oneArticleIssue.setArticleDois(articleDoisOne);
    oneArticleIssue.setDisplayName("issue with ONE article");
    oneArticleIssue.setIssueUri("info:doi/articleServiceTest/issue/2");
    dummyDataStore.store(oneArticleIssue);

    List<Issue> issueListOne = new LinkedList<Issue>();
    issueListOne.add(bothArticleIssue);

    List<Issue> issueListTwo = new LinkedList<Issue>();
    issueListTwo.add(oneArticleIssue);

    //Volumes - each issue can only be in one volume
    Volume volume1 = new Volume();
    volume1.setIssues(issueListOne);
    volume1.setDisplayName("volume with both issues");
    volume1.setVolumeUri("info:doi/articleServiceTest/volume/1");
    dummyDataStore.store(volume1);

    Volume volume2 = new Volume();
    volume2.setIssues(issueListTwo);
    volume2.setDisplayName("volume with one issue");
    volume2.setVolumeUri("info:doi/articleServiceTest/volume/2");
    dummyDataStore.store(volume2);

    List<Volume> volumeList = new LinkedList<Volume>();
    volumeList.add(volume1);
    volumeList.add(volume2);

    Journal journal = new Journal();
    journal.setVolumes(volumeList);
    journal.setJournalKey("articleServiceTest-journal-key");
    dummyDataStore.store(journal);

    List<String> possibleJournalIds = new ArrayList<String>(1);
    possibleJournalIds.add(journal.getID().toString());
    List<String> possibleJournalKeys = new ArrayList<String>(1);
    possibleJournalKeys.add(journal.getJournalKey());

    List<String> possibleVolumeIds = new ArrayList<String>(2);
    possibleVolumeIds.add(volume1.getVolumeUri());
    possibleVolumeIds.add(volume2.getVolumeUri());
    List<String> possibleVolumeNames = new ArrayList<String>(2);
    possibleVolumeNames.add(volume1.getDisplayName());
    possibleVolumeNames.add(volume2.getDisplayName());

    List<String> possibleIssueIds = new ArrayList<String>(2);
    possibleIssueIds.add(bothArticleIssue.getIssueUri());
    possibleIssueIds.add(oneArticleIssue.getIssueUri());
    List<String> possibleIssueNames = new ArrayList<String>(2);
    possibleIssueNames.add(bothArticleIssue.getDisplayName());
    possibleIssueNames.add(oneArticleIssue.getDisplayName());

    return new Object[][]{
        {articleDoisBoth.get(0).toString(), 2, //The first article is in both issues
            possibleJournalIds, possibleJournalKeys,
            possibleVolumeIds, possibleVolumeNames,
            possibleIssueIds, possibleIssueNames},
        {articleDoisBoth.get(1).toString(), 1, //The second article is only in the first issue
            possibleJournalIds, possibleJournalKeys,
            possibleVolumeIds.subList(0,1), possibleVolumeNames.subList(0,1),
            possibleIssueIds.subList(0,1), possibleIssueNames.subList(0,1)}
    };
  }

  /**
   * Get a List of all of the Journal/Volume/Issue combinations that contain the <code>articleURI</code> which was
   * passed in. Each primary List element contains a secondary List of six Strings which are, in order: <ul>
   * <li><strong>Element 0: </strong> Journal URI</li> <li><strong>Element 1: </strong> Journal key</li>
   * <li><strong>Element 2: </strong> Volume URI</li> <li><strong>Element 3: </strong> Volume name</li>
   * <li><strong>Element 4: </strong> Issue URI</li> <li><strong>Element 5: </strong> Issue name</li> </ul> A Journal
   * might have multiple Volumes, any of which might have multiple Issues that contain the <code>articleURI</code>.
   * The primary List will always contain one element for each Issue that contains the <code>articleURI</code>.
   *
   * @param articleDoi Article DOI that is contained in the Journal/Volume/Issue combinations which will be returned
   * @return All of the Journal/Volume/Issue combinations which contain the articleURI passed in
   */


  @Test(dataProvider = "journalVolumeIssue")
  public void testGetArticleIssues(String articleDoi, int expectedNumberOfResults,
                                   List<String> journalIds, List<String> journalKeys,
                                   List<String> volumeIds, List<String> volumeNames,
                                   List<String> issueIds, List<String> issueNames) {

    log.debug("test-testGetArticleIssues");

    List<List<String>> articleIssues = articleService.getArticleIssues(articleDoi);

    assertNotNull(articleIssues, "returned null list of article issues");
    assertEquals(articleIssues.size(), expectedNumberOfResults, "returned incorrect number of article issues");

    for (List<String> row : articleIssues) {
      assertEquals(row.size(), 6, "Returned issue row with incorrect number of entries");

      assertTrue(journalIds.contains(row.get(0)), "returned incorrect first entry (journal id); expected one of "
          + Arrays.toString(journalIds.toArray()) + ", but got " + row.get(0));

      assertTrue(journalKeys.contains(row.get(1)), "returned incorrect second entry (journal key); expected one of "
          + Arrays.toString(journalKeys.toArray()) + ", but got " + row.get(1));

      assertTrue(volumeIds.contains(row.get(2)), "returned incorrect third entry (volume id); expected one of "
          + Arrays.toString(volumeIds.toArray()) + ", but got " + row.get(2));

      assertTrue(volumeNames.contains(row.get(3)), "returned incorrect fourth entry (volume name); expected one of "
          + Arrays.toString(volumeNames.toArray()) + ", but got " + row.get(3));

      assertTrue(issueIds.contains(row.get(4)), "returned incorrect fifth entry (issue id); expected one of "
          + Arrays.toString(issueIds.toArray()) + ", but got " + row.get(4));

      assertTrue(issueNames.contains(row.get(5)), "returned incorrect sixth entry (issue name); expected one of "
          + Arrays.toString(issueNames.toArray()) + ", but got " + row.get(5));

    }
  }

  @DataProvider(name = "articleInfoDataProvider")
  public Object[][] getArticle() {
    Article article = new Article();
    article.setDoi("id://test-article-47");
    article.setTitle("test title for article info");
    article.setDate(new Date());
    article.setRights("article rights");
    article.setJournal("testJournal");
    article.setIssue("testIssue");
    article.setVolume("testVolume");
    article.setDescription("test, test, test, this is a test");

    Set<Category> categories = new HashSet<Category>(2);
    Category cat1 = new Category();
    cat1.setPath("/maincat1");

    Category cat2 = new Category();
    cat2.setPath("/maincat2");

    categories.add(cat1);
    categories.add(cat2);

    article.setCategories(categories);

    List<ArticleAuthor> authors = new ArrayList<ArticleAuthor>(2);
    ArticleAuthor author1 = new ArticleAuthor();
    author1.setFullName("Some fake author");
    dummyDataStore.store(author1);
    authors.add(author1);

    ArticleAuthor author2 = new ArticleAuthor();
    author2.setFullName("Michael Eisen");
    dummyDataStore.store(author2);
    authors.add(author2);

    article.setAuthors(authors);

    Set<String> typesForResArticle = new HashSet<String>();
    typesForResArticle.add("http://rdf.plos.org/RDF/articleType/Research%20Article");

    List<ArticleRelationship> articleRelationships = new ArrayList<ArticleRelationship>(1);
    Article unpubbedArticle = new Article();
    unpubbedArticle.setDoi("id:doi-unpubbed-related-article");
    unpubbedArticle.setState(Article.STATE_UNPUBLISHED);
    unpubbedArticle.setRights("article rights");
    unpubbedArticle.setTitle("foo");
    unpubbedArticle.setTypes(typesForResArticle);

    ArticleRelationship unpubbedRelationship = new ArticleRelationship();
    unpubbedRelationship.setParentArticle(article);
    unpubbedRelationship.setOtherArticleID(Long.valueOf(dummyDataStore.store(unpubbedArticle)));
    unpubbedRelationship.setType("foo");
    unpubbedRelationship.setOtherArticleDoi(unpubbedArticle.getDoi());

    articleRelationships.add(unpubbedRelationship);

    Article pubbedArticle = new Article();
    pubbedArticle.setDoi("id:doi-pubbed-related-article");
    pubbedArticle.setState(Article.STATE_ACTIVE);
    pubbedArticle.setTitle("foo");
    pubbedArticle.setTypes(typesForResArticle);

    ArticleRelationship pubbedRelationship = new ArticleRelationship();
    pubbedRelationship.setParentArticle(article);
    pubbedRelationship.setOtherArticleID(Long.valueOf(dummyDataStore.store(pubbedArticle)));
    pubbedRelationship.setType("foo2");
    pubbedRelationship.setOtherArticleDoi(pubbedArticle.getDoi());
    articleRelationships.add(pubbedRelationship);

    //regression test for PDEV-215: if you have multiple relationships to same 'other' article (different types,
    //e.g. correction and companion) the other article will show up twice on the article page
    ArticleRelationship duplicatePubbedRelationship = new ArticleRelationship();
    duplicatePubbedRelationship.setParentArticle(article);
    duplicatePubbedRelationship.setOtherArticleID(pubbedRelationship.getOtherArticleID());
    duplicatePubbedRelationship.setType("foo 49 thousand");
    duplicatePubbedRelationship.setOtherArticleDoi(pubbedArticle.getDoi());
    articleRelationships.add(duplicatePubbedRelationship);

    //even admins shouldn't see this related article
    Article disabledArticle = new Article();
    disabledArticle.setDoi("id:doi-disabled-related-article");
    disabledArticle.setTitle("disabled article");
    disabledArticle.setState(Article.STATE_DISABLED);
    ArticleRelationship disableRelationship = new ArticleRelationship();
    disableRelationship.setParentArticle(article);
    disableRelationship.setOtherArticleID(Long.valueOf(dummyDataStore.store(disabledArticle)));
    disableRelationship.setType("foo");
    articleRelationships.add(disableRelationship);


    Article eocArticle = new Article();
    eocArticle.setDoi("id:doi-object-of-concern-relationship-article");
    eocArticle.setState(Article.STATE_ACTIVE);
    eocArticle.setTitle("foo");
    Set<String> typesForEocArticle = new HashSet<String>();
    typesForEocArticle.add("http://rdf.plos.org/RDF/articleType/Expression%20of%20Concern");
    eocArticle.setTypes(typesForEocArticle);

    //Expression of Concern relationship
    ArticleRelationship eocRelationship = new ArticleRelationship();
    eocRelationship.setParentArticle(article);
    eocRelationship.setOtherArticleID(Long.valueOf(dummyDataStore.store(eocArticle)));
    eocRelationship.setType("expressed-concern");

    eocRelationship.setOtherArticleDoi(eocArticle.getDoi());
    articleRelationships.add(eocRelationship);

    article.setRelatedArticles(articleRelationships);

    dummyDataStore.store(article);

    UserProfile annotationCreator = new UserProfile(
        "email@articleServiceTest.org",
        "displayNameForArticleServiceTest",
        "pass");
    dummyDataStore.store(annotationCreator);

    return new Object[][]{
        //admins should see unpubbed article
        {article.getDoi(), article, DEFAULT_ADMIN_AUTHID, new Article[]{unpubbedArticle, pubbedArticle, eocArticle}},
        //users should not
        {article.getDoi(), article, DEFAULT_USER_AUTHID, new Article[]{pubbedArticle, eocArticle}}
    };
  }

  @Test(dataProvider = "articleInfoDataProvider", dependsOnMethods = {"testGetArticle"})
  public void testGetArticleInfo(String id, Article expectedArticle, String authId,
                                 Article[] expectedRelatedArticles) throws NoSuchArticleIdException {
    ArticleInfo result = articleService.getArticleInfo(id, authId);
    assertNotNull(result, "returned null article info");

    checkArticleInfo(result,
        expectedArticle,
        expectedRelatedArticles);
  }

  @Test(dataProvider = "savedArticlesURI")
  public void testGetRandomRecentArticles(String DOI, Article article) throws Exception {
    String eIssn = article.geteIssn();
    List<URI>articleTypes = new ArrayList<URI>();
    int numDaysInPast = 30;
    int articleCount = 100;

    for(String type : article.getTypes())
    {
      articleTypes.add(new URI(type));
    }

    //Being the list can be random, I'm just checking that it executes properly
    List<SearchHit> hits = articleService.getRandomRecentArticles(eIssn, articleTypes, numDaysInPast, articleCount);

    assertEquals(hits.size(), 1);
    assertMatchingDates(hits.get(0).getDate(), article.getDate());
    assertEquals(hits.get(0).getUri(), article.getDoi());
    assertEquals(hits.get(0).getTitle(), article.getTitle());
  }

  @Test(dataProvider = "savedArticlesURI")
  public void testGetRandomRecentArticlesWithoutArticleTypes(String DOI, Article article) throws Exception {
    String eIssn = article.geteIssn();
    List<URI>articleTypes = new ArrayList<URI>();
    int numDaysInPast = 30;
    int articleCount = 100;

    //Being the list can be random, I'm just checking that it executes properly
    List<SearchHit> hits = articleService.getRandomRecentArticles(eIssn, articleTypes, numDaysInPast, articleCount);

    assertEquals(hits.size(), 2);
  }

  @Test
  public void testGetBasicArticleView() throws NoSuchArticleIdException {
    // TODO add check for article type

    Article article = new Article("id:doi-for-get-basic-article-view");
    article.setTitle("test title for get article view");

    List<ArticleAuthor> authors = new LinkedList<ArticleAuthor>();
    ArticleAuthor author1 = new ArticleAuthor();
    author1.setFullName("FullName");
    author1.setGivenNames("GivenNames");
    author1.setSuffix("Suffix");
    author1.setSurnames("Surnames");
    authors.add(author1);
    article.setAuthors(authors);

    List<String> collaborativeAuthors = new LinkedList<String>();
    collaborativeAuthors.add("collab author 1");
    article.setCollaborativeAuthors(collaborativeAuthors);

    dummyDataStore.store(article);

    List<String> authors2 = new ArrayList<String>(authors.size());
    for (ArticleAuthor ac : authors) {
      authors2.add(ac.getFullName());
    }

    ArticleInfo result = articleService.getBasicArticleView(article.getID());
    assertNotNull(result, "returned null result when fetching by id");
    assertEquals(result.getDoi(), article.getDoi(), "result had incorrect doi when fetching by id");
    assertEquals(result.getTitle(), article.getTitle(), "result had incorrect title when fetching by id");
    assertEquals(result.getAuthors(),authors2, "result had incorrect authors when fetching by id");
    assertEquals(result.getCollaborativeAuthors(), article.getCollaborativeAuthors(),
        "result had incorrect collaborative authors when fetching by id");

    result = articleService.getBasicArticleView(article.getDoi());
    assertNotNull(result, "returned null result when fetching by doi");
    assertEquals(result.getDoi(), article.getDoi(), "result had incorrect doi when fetching by doi");
    assertEquals(result.getTitle(), article.getTitle(), "result had incorrect title when fetching by doi");
    assertEquals(result.getAuthors(),authors2, "result had incorrect authors when fetching by doi");
    assertEquals(result.getCollaborativeAuthors(), article.getCollaborativeAuthors(),
        "result had incorrect collaborative authors when fetching by doi");
  }

  @Test
  public void testCheckArticleState() {
    try {
      articleService.checkArticleState(null, DEFAULT_USER_AUTHID);
      fail("this article does not exist");
    } catch (NoSuchArticleIdException e) {
      // success
    }

    try {
      articleService.checkArticleState("", DEFAULT_USER_AUTHID);
      fail("this article does not exist");
    } catch (NoSuchArticleIdException e) {
      // success
    }

    try {
      articleService.checkArticleState("garbage", DEFAULT_USER_AUTHID);
      fail("this article does not exist");
    } catch (NoSuchArticleIdException e) {
      // success
    }

    Article activeArticle = new Article("id:doi-for-checkArticleState-active");
    activeArticle.setTitle("test title for get article view");
    activeArticle.setState(Article.STATE_ACTIVE);
    dummyDataStore.store(activeArticle);

    try {
      articleService.checkArticleState(activeArticle.getDoi(), DEFAULT_USER_AUTHID);
    } catch (NoSuchArticleIdException e) {
      fail("this article does exist");
    }

    Article unpublishedArticle = new Article("id:doi-for-checkArticleState-unpublished");
    unpublishedArticle.setTitle("test title for get article view");
    unpublishedArticle.setState(Article.STATE_UNPUBLISHED);
    dummyDataStore.store(unpublishedArticle);

    try {
      articleService.checkArticleState(unpublishedArticle.getDoi(), DEFAULT_USER_AUTHID);
      fail("this article should not be visible to the default user");
    } catch (NoSuchArticleIdException e) {
      // success
    }

    try {
      articleService.checkArticleState(unpublishedArticle.getDoi(), DEFAULT_ADMIN_AUTHID);
      // success
    } catch (NoSuchArticleIdException e) {
      fail("this article should be visible to the admin user");
    }
  }

  private Set<Category> addCategory(Set<Category> categories, String path) {
    Category category = new Category();
    category.setPath(path);
    categories.add(category);
    return categories;
  }

  @Test(dataProvider = "savedArticlesID")
  public void testSetArticleCategories(Long articleID, Article expectedArticle) throws Exception {
    List<String> terms = Arrays.asList(
        "/Biology and life sciences/Cell biology/Cellular types/Animal cells/Blood cells/White blood cells/T cells",
        "/Biology and life sciences",
        "/Biology and life sciences/Anatomy and physiology/Immune physiology/Spleen",
        "/Biology and life sciences/Anatomy and physiology/Immune physiology/Antigens",
        "/Research and analysis methods/Imaging techniques/Radiologic imaging/Gastrointestinal imaging/Liver and spleen scan",
        "/Medicine and health sciences/Pathology and laboratory medicine/Infectious diseases/Viral diseases/Hepatitis",
        "/Biology and life sciences/Anatomy and physiology/Liver",
        "/Biology and life sciences/Anatomy and physiology/Lymphatic system/Lymph nodes",
        "/Biology and life sciences/Cell biology/Cellular types/Animal cells/Antigen-presenting cells"
        );
    Article article = articleService.getArticle(articleID, DEFAULT_ADMIN_AUTHID);
    articleService.setArticleCategories(article, terms);

    Set<Category> expectedCategories = new HashSet<Category>(8);
    addCategory(expectedCategories,
        "/Biology and life sciences/Cell biology/Cellular types/Animal cells/Blood cells/White blood cells/T cells");
    addCategory(expectedCategories, "/Biology and life sciences");
    addCategory(expectedCategories,
        "/Biology and life sciences/Anatomy and physiology/Immune physiology/Spleen");
    addCategory(expectedCategories,
        "/Biology and life sciences/Anatomy and physiology/Immune physiology/Antigens");
    addCategory(expectedCategories,
        "/Research and analysis methods/Imaging techniques/Radiologic imaging/Gastrointestinal imaging/Liver and spleen scan");
    addCategory(expectedCategories,
        "/Medicine and health sciences/Pathology and laboratory medicine/Infectious diseases/Viral diseases/Hepatitis");
    addCategory(expectedCategories, "/Biology and life sciences/Anatomy and physiology/Liver");
    addCategory(expectedCategories,
        "/Biology and life sciences/Anatomy and physiology/Lymphatic system/Lymph nodes");
    assertEquals(article.getCategories(), expectedCategories);

    // setArticleCategories should only store the first 8 categories returned by
    // the taxonomy server.
    Category shouldntExist = new Category();
    shouldntExist.setPath(
        "/Biology and life sciences/Cell biology/Cellular types/Animal cells/Antigen-presenting cells");
    assertFalse(article.getCategories().contains(shouldntExist));
  }

  @Test(dataProvider = "savedArticlesID", expectedExceptions = IllegalArgumentException.class)
  public void testSetArticleCategoriesError(Long articleID, Article expectedArticle)
      throws Exception {
    List<String> terms = Arrays.asList("bogus", "response", "from", "server");
    Article article = articleService.getArticle(articleID, DEFAULT_ADMIN_AUTHID);
    articleService.setArticleCategories(article, terms);
  }
}
