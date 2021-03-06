/*
* Copyright (c) 2006-2014 by Public Library of Science
*
* http://plos.org
* http://ambraproject.org
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.ambraproject.action.user;

import org.ambraproject.models.UserProfile;
import org.ambraproject.service.user.UserService;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import java.util.Map;

import static org.ambraproject.Constants.AMBRA_USER_KEY;

/**
 * Remove the user's orcid information
 */
public class OrcidRemoveAction extends UserActionSupport {
  private static final Logger log = LoggerFactory.getLogger(OrcidRemoveAction.class);

  private UserService userService;

  /**
   * If access has been denied, orcid will return these values
   */
  @Override
  public String execute() throws Exception {
    Map<String, Object> session = ServletActionContext.getContext().getSession();
    UserProfile user = (UserProfile)session.get(AMBRA_USER_KEY);

    if(user == null) {
      //User not authenticated, some how the user got here with out this?  URL Hacking?
      return LOGIN;
    } else {
      this.userService.removeUserOrcid(user.getID());

      return SUCCESS;
    }
  }

  @Required
  public void setUserService(UserService userService) { this.userService = userService; }
}
