/* $HeadURL::                                                                            $
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
package org.topazproject.ambra.user;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.topazproject.ambra.Constants.AMBRA_USER_KEY;
import static org.topazproject.ambra.Constants.ReturnCode;
import static org.topazproject.ambra.Constants.SINGLE_SIGNON_EMAIL_KEY;
import static org.topazproject.ambra.Constants.SINGLE_SIGNON_RECEIPT;
import static org.topazproject.ambra.Constants.SINGLE_SIGNON_USER_KEY;

import org.topazproject.ambra.ApplicationException;
import org.topazproject.ambra.user.service.DisplayNameAlreadyExistsException;
import org.topazproject.ambra.user.service.UserService;
import org.topazproject.ambra.util.FileUtils;

import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Map;

/**
 * Ensures that the user has a profile if the user does something which requires membership
 * Get the user object for the logged in user or redirect the user to set up his profile.
 */
public class EnsureUserAccountInterceptor extends AbstractInterceptor {
  private UserService userService;
  private static final Logger log = LoggerFactory.getLogger(EnsureUserAccountInterceptor.class);

  public String intercept(final ActionInvocation actionInvocation) throws Exception {
    if (log.isDebugEnabled())
      log.debug("ensure user account interceptor called");

    Map sessionMap = actionInvocation.getInvocationContext().getSession();

    final String userId = (String) sessionMap.get(SINGLE_SIGNON_USER_KEY);

    if (null == userId) {
      if (log.isDebugEnabled()){
        log.debug("no single sign on user key");
        log.debug("ticket is: " + sessionMap.get(SINGLE_SIGNON_RECEIPT));
      }
      return actionInvocation.invoke();
    }

    AmbraUser ambraUser = (AmbraUser) sessionMap.get(AMBRA_USER_KEY);
    if (null != ambraUser) {
      if (log.isDebugEnabled()) {
        log.debug("Retrieved user from session with userId: " + ambraUser.getUserId());
      }
      return getReturnCodeDependingOnDisplayName(ambraUser, actionInvocation);
    } else {
      ambraUser = userService.getUserByAuthId(userId);
      if (log.isDebugEnabled()) {
        log.debug("UserService : " + userService + " hashcode = "  + userService.hashCode());
        log.debug("Session: " + ServletActionContext.getRequest().getSession().getId());
      }

      if (null == ambraUser) {
        // forward to new profile creation page
        if (log.isDebugEnabled())
          log.debug("This is a new user with id: " + userId);
        return ReturnCode.NEW_PROFILE;
      } else {
        updateUserEmailAddress(ambraUser, userId, (String) sessionMap.get(SINGLE_SIGNON_EMAIL_KEY));
        sessionMap.put(AMBRA_USER_KEY, ambraUser);
        if (log.isDebugEnabled())
          log.debug("Existing user detected: " + userId);
        return getReturnCodeDependingOnDisplayName(ambraUser, actionInvocation);
      }
    }
  }

  private String getReturnCodeDependingOnDisplayName(final AmbraUser ambraUser,
                                                     final ActionInvocation actionInvocation)
    throws Exception {
    if (StringUtils.hasText(ambraUser.getDisplayName())) {
      // forward the user to the page he was initially going to
      return actionInvocation.invoke();
    } else {
      // profile has partial details as the user might have been ported from old application
      return ReturnCode.UPDATE_PROFILE; //forward to update profile page
    }
  }

  public void setUserService(final UserService userService) {
    this.userService = userService;
  }

  private void updateUserEmailAddress(AmbraUser user, String authId, String presetEmail)
    throws ApplicationException {
    String emailAddress = fetchUserEmailAddress(authId, presetEmail);
    if (emailAddress != null) {
      if (!emailAddress.equals(user.getEmail())) {
        user.setEmail(emailAddress);
        try {
          userService.setProfile(user);
        } catch (DisplayNameAlreadyExistsException de) {
          if (log.isErrorEnabled()) {
            log.error("Username: " + user.getDisplayName() +
                      " already exists while trying to update email address for user: " +
                      user.getUserId(), de);
          }
        }
      }
    } else {
      if (log.isErrorEnabled()) {
        log.error("Retrieved a null email address from CAS for userId: " + user.getUserId());
      }
    }
  }

  private String fetchUserEmailAddress(String authId, String presetEmail)
    throws ApplicationException {
    if (presetEmail != null)
      return presetEmail;

    final String emailAddressUrl = userService.getEmailAddressUrl();
    final String url = emailAddressUrl + authId;
    try {
      return FileUtils.getTextFromUrl(url);
    } catch (IOException ex) {
      final String errorMessage = "Failed to fetch the email address using the url:" + url;
      log.error(errorMessage, ex);
      throw new ApplicationException(errorMessage, ex);
    }
  }
}
