/* $HeadURL::                                                                                      $
 * $Id$
 *
 * Copyright (c) 2007-2010 by Public Library of Science
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
package org.topazproject.ambra.it.pages;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.topazproject.ambra.it.jwebunit.AmbraWebTester;

/**
 * A base class for pages that contain the most common page elements.
 *
 * @author Pradeep Krishnan
 */
public abstract class CommonBasePage extends AbstractPage {
  private static final Log    log         = LogFactory.getLog(CommonBasePage.class);

  public static final String LOGIN_LINK          = "Login";
  public static final String CREATE_ACCOUNT_LINK = "Create Account";
  public static final String FEEDBACK_LINK       = "Feedback";
  public static final String LOGOUT_LINK         = "Logout";
  public static final String PREFERENCES_LINK    = "Preferences";

  private static final String commonLinks[] = new String[] {
    "Browse", "RSS", "Home", "Browse Articles", "About", "For Readers",
      "Journals", "Hubs", "PLoS.org"
  };
  
  

  private static final String plosOneLinks[] = new String[] {"PLoS ONE", 
    "For Authors and Reviewers"};
  private static final String ctLinks[] = new String[] {"PLoS Hub - Clinical Trials",
    "For Authors"   // XXX: Is this a bug? Should this be same as plosOne?
  };

  public CommonBasePage(AmbraWebTester tester, String journal, String url) {
    super(tester, journal, url);
  }

  public void verifyPage() {
    if (!tester.isLoggedIn()) {
      tester.assertLinkPresentWithText(LOGIN_LINK);
      tester.assertLinkPresentWithText(CREATE_ACCOUNT_LINK);
      tester.assertLinkPresentWithText(FEEDBACK_LINK);
    } else {
      tester.assertLinkPresentWithText(LOGOUT_LINK);
      tester.assertLinkPresentWithText(PREFERENCES_LINK);
    }

    if (HomePage.J_PONE.equals(journal)) {
      for (String link : commonLinks)
        tester.assertLinkPresentWithText(link);
    }
    
    if (J_PONE.equals(getJournal())) {
      for (String link : plosOneLinks)
        tester.assertLinkPresentWithText(link);
    }

    if (J_CT.equals(getJournal())) {
      for (String link : ctLinks)
        tester.assertLinkPresentWithText(link);
    }

    // TODO : Add more verification tests
  }


  public void loginAs(String authId, String email) {
    boolean onLoginPage = isLoginPage();
    if (!onLoginPage) {
      if (tester.isLoggedIn())
        logOut();

      log.debug("Going to login page ...");
      tester.clickLinkWithText(LOGIN_LINK);
    }

    log.debug("Logging in as (" + authId + ", " + email + ") ...");
    tester.setFormElement("sso.auth.id", authId);
    tester.setFormElement("sso.email", email);
    tester.submit();
    tester.setLoggedIn(true);
    if (onLoginPage)
      gotoPage();
  }

  public void logOut() {
    if (tester.isLoggedIn()) {
      log.debug("Logging out ...");
      tester.clickLinkWithText(LOGOUT_LINK);
      tester.setLoggedIn(false);
      gotoPage();  // logout takes you to home page. so come-back here again
    }
  }

  public SearchResultsPage search(String query, String expected[]) {
    tester.setWorkingForm("searchForm");
    tester.setFormElement("query", query);
    tester.submit();
    return new SearchResultsPage(tester, getJournal(), query, expected);
  }

}
