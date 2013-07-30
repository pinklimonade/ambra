/*
 * Copyright (c) 2006-2013 by Public Library of Science
 *
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
package org.ambraproject.action;

import org.ambraproject.action.search.BaseSearchAction;
import org.ambraproject.service.taxonomy.TaxonomyService;
import org.ambraproject.util.CategoryUtils;
import org.ambraproject.views.CategoryView;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * Handle browse by category requests
 *
 * @author Joe Osowski
 */
public class BrowseAction extends BaseSearchAction {
  private static final Logger log = LoggerFactory.getLogger(BrowseAction.class);

  private TaxonomyService taxonomyService;
  private String category;

  @Override
  public String execute() throws Exception {
    CategoryView categoryView = taxonomyService.parseCategories(super.getCurrentJournal());

    setDefaultSearchParams();
    setUnformattedQuery("*:*");

    if(category != null && category.length() > 0) {
      //Recreate the category name as stored in the DB
      category = category.replace("_", " ");

      CategoryView view = CategoryUtils.findCategory(categoryView, category);

      //If the value is null, we've got a category that doesn't exist any more.  Try to format the name
      //And search for it anyway?
      if(view == null) {
        category = StringUtils.capitalize(category);
        //TODO: Handle no categories here or when search returns?
      } else {
        category = view.getName();
      }

      setFilterSubjects(new String[] { this.category } );
    } else {
      category = null;
    }

    resultsSinglePage = this.searchService.advancedSearch(getSearchParameters());

    //TODO: How to handle no search results

    return SUCCESS;
  }

  /**
   * Set the category for the search to perform
   */
  public void setCategory(String category) {
    this.category = category;
  }

  /**
   * Get the category
   */
  public String getCategory() {
    return this.category;
  }

  @Required
  public void setTaxonomyService(TaxonomyService taxonomyService) {
    this.taxonomyService = taxonomyService;
  }
}
