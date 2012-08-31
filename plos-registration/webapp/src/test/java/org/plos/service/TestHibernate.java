/* $HeadURL::                                                                            $
 * $Id$
 *
 */
package org.plos.service;

import org.plos.BasePlosoneRegistrationTestCase;
import org.plos.registration.User;
import org.plos.registration.UserImpl;

/**
 *
 */
public class TestHibernate extends BasePlosoneRegistrationTestCase {
  private UserDAO userDao;

  public void testHibernate() {
    userDao.saveOrUpdate(new UserImpl("steve@home.com", "stevec"));
  }

  public void setUserDAO(final UserDAO userDao) {
      this.userDao = userDao;
  }

  public void testDeleteUser() {
    User user = new UserImpl("deleteUser@home.com", "delete");
    userDao.saveOrUpdate(user);
    user = userDao.findUserWithLoginName("deleteUser@home.com");
    assertNotNull(user);
    userDao.delete(user);
    user = userDao.findUserWithLoginName("deleteUser@home.com");
    assertNull(user);
  }

  public void testDeleteUserWithCaseInsensitiveEmailAddressCheck() {
    User user = new UserImpl("deleteUser@home.com", "delete");
    userDao.saveOrUpdate(user);
    user = userDao.findUserWithLoginName("DELETEUSER@HOME.COM");
    assertNotNull(user);
    userDao.delete(user);
    user = userDao.findUserWithLoginName("deleteuser@home.com");
    assertNull(user);
  }

}