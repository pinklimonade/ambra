/*
 * $HeadURL$
 * $Id$
 *
 * Copyright (c) 2006-2010 by Public Library of Science
 * http://plos.org
 * http://ambraproject.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ambraproject.action.search;

import org.ambraproject.action.BaseSessionAwareActionSupport;
import org.ambraproject.service.search.SearchParameters;
import org.ambraproject.service.search.SearchService;
import org.ambraproject.util.DateParser;
import org.ambraproject.util.InvalidDateException;
import org.ambraproject.views.SearchResultSinglePage;
import org.springframework.beans.factory.annotation.Required;

import java.util.Date;
import java.util.List;

public abstract class BaseSearchAction extends BaseSessionAwareActionSupport {
  // All of the parameters used to execute any search.
  private SearchParameters searchParameters;

  protected static final String SEARCH_PAGE_SIZE = "ambra.services.search.pageSize";
  protected SearchService searchService;
  protected SearchResultSinglePage resultsSinglePage;

  /**
   * Return the object used to store all of the parameters used to create a query.
   * If no searchParameter exists, then a new one is created.
   *
   * @return The object used to store all of the parameters used to create a query
   */
  public SearchParameters getSearchParameters() {
    if (searchParameters == null) {
      searchParameters = new SearchParameters();
    }
    return searchParameters;
  }

  /**
   * Set values used for processing and/or display by all searches.
   */
  protected void setDefaultSearchParams() {
    // Set default for "pageSize".
    if (getPageSize() == 0) {
      setPageSize(configuration.getInt(SEARCH_PAGE_SIZE, 10));
    }
  }


  /**
   * Converts a String array whose first element may be a comma delimited String
   * into a new String array whose elements are the split comma delimited elements.
   *
   * @param arr String array that may contain one or more elements having a comma delimited String.
   * @return Rectified String[] array or
   *         <code>null</code> when the given String array is <code>null</code>.
   */
  private String[] rectify(String[] arr) {
    if (arr != null && arr.length == 1 && arr[0].length() > 0) {
      arr = arr[0].split(",");
      for (int i = 0; i < arr.length; i++) {
        arr[i] = arr[i] == null ? null : arr[i].trim();
      }
    }
    return arr;
  }

  //  Getters and Setters that belong to SearchParameters class

  /**
   * Set the simple query
   *
   * @param query query
   */
  public void setQuery(final String query) {
    getSearchParameters().setQuery(query);
  }

  /**
   * Getter for property 'query'.
   *
   * @return Value for property 'query'.
   */
  public String getQuery() {
    return getSearchParameters().getQuery();
  }

  /**
   * Set the <code>unformattedQuery</code> parameter.
   * Not associated to Simple Search.
   *
   * @param unformattedQuery
   *   The <code>unformattedQuery</code> which will be run against the search engine
   */
  public void setUnformattedQuery(final String unformattedQuery) {
    getSearchParameters().setUnformattedQuery(unformattedQuery);
  }

  /**
   * Get the <code>unformattedQuery</code> parameter.
   * Not associated to Simple Search.
   *
   * @return Value for property <code>unformattedQuery</code>,
   *   the unformattedQuery which will be run against the search engine
   */
  public String getUnformattedQuery() {
    return getSearchParameters().getUnformattedQuery();
  }

  /**
   * Set the startPage
   *
   * @param startPage startPage
   */
  public void setStartPage(final int startPage) {
    getSearchParameters().setStartPage(startPage);
  }

  /**
   * Getter for property 'startPage'.
   *
   * @return Value for property 'startPage'.
   */
  public int getStartPage() {
    return getSearchParameters().getStartPage();
  }

  /**
   * Set the pageSize
   *
   * @param pageSize pageSize
   */
  public void setPageSize(final int pageSize) {
    getSearchParameters().setPageSize(pageSize);
  }

  /**
   * Getter for property 'pageSize'.
   *
   * @return Value for property 'pageSize'.
   */
  public int getPageSize() {
    return getSearchParameters().getPageSize();
  }

  /**
   * Setter for 'sort', the clause which orders the query results.
   * 'sort' is passed in the query string when the user selects the type of sort.
   * It is the name of the sort field in the form.
   * @param sort The sort order for the search results
   */
  public void setSort(final String sort) {
    getSearchParameters().setSortKey(sort);
  }

  /**
   * Getter for 'sort', the clause which orders the query results
   * @return The sort order for the search results
   */
  public String getSort() {
    return getSearchParameters().getSortKey();
  }

  /**
   * Setter for 'sortKey', the clause which orders the query results.
   * 'sortKey' is passed in the query string when the user navigates through the
   * pages after choosing the sort type.
   * In pagination macro, the search parameters are passed as the query String;
   * therefore the query string contains 'sortKey' rather than 'sort'.
   * @param sortKey The sort order for the search results
   */
  public void setSortKey(final String sortKey) {
    getSearchParameters().setSortKey(sortKey);
  }

  /**
   * Getter for 'sortKey', the clause which orders the query results.
   * @return The sort order for the search results
   */
  public String getSortKey() {
    return getSearchParameters().getSortKey();
  }


  public void setVolume(final String volume) {
    getSearchParameters().setVolume(volume);
  }

  public String getVolume() {
    return getSearchParameters().getVolume();
  }

  public void setELocationId(final String eLocationId) {
    getSearchParameters().setELocationId(eLocationId);
  }

  public String getELocationId() {
    return getSearchParameters().getELocationId();
  }

  public void setId(final String id) {
    getSearchParameters().setId(id);
  }
  public String getId() {
    return getSearchParameters().getId();
  }

  public void setFilterJournals(String[] filterJournals) {
    getSearchParameters().setFilterJournals(rectify(filterJournals));
  }

  public String[] getFilterJournals() {
    return getSearchParameters().getFilterJournals();
  }

  public void setFilterSubjects(String[] category) {
    getSearchParameters().setFilterSubjects(rectify(category));
  }

  public String[] getFilterSubjects() {
    return getSearchParameters().getFilterSubjects();
  }

  public void setFilterArticleTypes(String[] type) {
    getSearchParameters().setFilterArticleTypes(rectify(type));
  }

  public String[] getFilterArticleTypes() {
    return getSearchParameters().getFilterArticleTypes();
  }

  public void setFilterKeyword(String keyword) {
    getSearchParameters().setFilterKeyword(keyword);
  }

  public String getFilterKeyword() {
    return getSearchParameters().getFilterKeyword();
  }

  public String[] getFilterAuthors() {
    return getSearchParameters().getFilterAuthors();
  }

  public void setFilterAuthors(String[] authors) {
    getSearchParameters().setFilterAuthors(rectify(authors));
  }

  public String getResultView() {
    return getSearchParameters().getResultView();
  }

  public void setResultView(String resultView) {
    getSearchParameters().setResultView(resultView);
  }

  public void setFilterEndDate(String filterEndDate) {
    Date eDate = null;

    if(filterEndDate != null && filterEndDate.length() > 0) {
      try {
        //If no time is specified, append end of day
        if(!filterEndDate.contains("T")) {
          filterEndDate = filterEndDate + "T23:59:59Z";
        }

        eDate = DateParser.parse(filterEndDate);
      } catch(InvalidDateException ex) {
        //If the user enters a wacky date, die silently?
      }
    }

    getSearchParameters().setFilterEndDate(eDate);
  }

  public Date getFilterEndDate() {
    return getSearchParameters().getFilterEndDate();
  }

  public void setFilterStartDate(String filterStartDate) {
    Date sDate = null;

    if(filterStartDate != null && filterStartDate.length() > 0) {
      //If no time is specified, append beginning of day
      if(!filterStartDate.contains("T")) {
        filterStartDate = filterStartDate + "T00:00:00Z";
      }

      try {
        sDate = DateParser.parse(filterStartDate);
      } catch(InvalidDateException ex) {
        //If the user enters a wacky date, die silently?
      }
    }

    getSearchParameters().setFilterStartDate(sDate);
  }

  public Date getFilterStartDate() {
    return getSearchParameters().getFilterStartDate();
  }

  /**
   * The total number of search results
   *
   * @return Value for property 'totalNoOfResults'.
   */
  public int getTotalNoOfResults() {
    return resultsSinglePage.getTotalNoOfResults();
  }

  public List getPageSizes()
  {
    return searchService.getPageSizes();
  }

  public SearchResultSinglePage getResultsSinglePage() {
    return resultsSinglePage;
  }

  /**
   * Set the searchService
   *
   * @param searchService searchService
   */
  @Required
  public void setSearchService(final SearchService searchService) {
    this.searchService = searchService;
  }
}
