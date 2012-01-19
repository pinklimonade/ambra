/*
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 *
 * The Original Code is the Kowari Metadata Store.
 *
 * The Initial Developer of the Original Code is Plugged In Software Pty
 * Ltd (http://www.pisoftware.com, mailto:info@pisoftware.com). Portions
 * created by Plugged In Software Pty Ltd are Copyright (C) 2001,2002
 * Plugged In Software Pty Ltd. All Rights Reserved.
 *
 * Contributor(s): N/A.
 *
 * [NOTE: The text of this Exhibit A may differ slightly from the text
 * of the notices in the Source Code files of the Original Code. You
 * should use the text of this Exhibit A rather than the text found in the
 * Original Code Source Code for Your Modifications.]
 *
 */

package org.mulgara.server.rmi;

// Java 2 standard packages
import java.io.Serializable;
import java.net.URI;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;
import java.io.*;

// Third party packages
import org.apache.log4j.Logger;
import org.jrdf.graph.*;

// Locally written packages
import org.mulgara.query.Answer;
import org.mulgara.query.ModelExpression;
import org.mulgara.query.Query;
import org.mulgara.query.QueryException;
import org.mulgara.rules.RulesException;
import org.mulgara.rules.RulesRef;
import org.mulgara.server.NonRemoteSessionException;
import org.mulgara.server.Session;

import javax.naming.*;

/**
 * Wrapper around a {@link RemoteSession} to make it look like a {@link
 * Session}. The only real functionality this wrapper implements is to nest any
 * {@link RemoteException}s inside {@link QueryException}s.
 *
 * @author <a href="http://staff.pisoftware.com/raboczi">Simon Raboczi</a>
 *
 * @created 2002-01-03
 *
 * @version $Revision$
 *
 * @modified $Date: 2005/06/26 12:48:16 $
 *
 * @maintenanceAuthor $Author: pgearon $
 *
 * @company <A href="mailto:info@PIsoftware.com">Plugged In Software</A>
 *
 * @copyright &copy; 2002-2003 <A href="http://www.PIsoftware.com/">Plugged In
 *      Software Pty Ltd</A>
 *
 * @licence <a href="{@docRoot}/../../LICENCE">Mozilla Public License v1.1</a>
 */
class RemoteSessionWrapperSession implements Serializable, Session {

  /** Logger.  */
  private static final Logger logger = Logger.getLogger(RemoteSessionWrapperSession.class.getName());

  /**
   * Allow newer compiled version of the stub to operate when changes
   * have not occurred with the class.
   * NOTE : update this serialVersionUID when a method or a public member is
   * deleted.
   */
  static final long serialVersionUID = -2647357071965350751L;

  /**
   * The number of times to retry a call.
   */
  protected static final int RETRY_COUNT = 2;

  /**
   * The number of times remaining to retry the current call.
   */
  protected int retryCount;

  /**
   * The wrapped {@link RemoteSession}
   */
  private RemoteSession remoteSession;

  /**
   * The serverURI of the remoteSessionFactory
   * Used to reconnect sessions.
   */
  protected URI serverURI = null;

  /**
   * Maintain the state of autcommit to determine if an exception
   * needs to be throw if a reconnection is attempted when a transaction
   * was previously in process when the server was bounced (a rollback has
   * occured).
   */
  protected boolean autoCommit = true;

  //
  // Constructor
  //

  /**
   *
   * @param remoteSession the wrapped remote session
   * @throws IllegalArgumentException if <var>remoteSession</var> is <code>null</code>
   */
  protected RemoteSessionWrapperSession(RemoteSession remoteSession, URI serverURI) {

    // Validate "remoteSession" parameter
    if (remoteSession == null) {

      throw new IllegalArgumentException("Null \"remoteSession\" parameter");
    }

    // Initialize fields
    this.remoteSession = remoteSession;
    this.serverURI = serverURI;

    resetRetries();
  }

  /**
   * Sets the Model attribute of the RemoteSessionWrapperSession object
   *
   * @param uri The new Model value
   * @param modelExpression The new Model value
   * @return RETURNED VALUE TO DO
   * @throws QueryException EXCEPTION TO DO
   */
  public long setModel(URI uri,
      ModelExpression modelExpression) throws QueryException {

    try {

      long r = remoteSession.setModel(uri, modelExpression);
      resetRetries();
      return r;
    }
    catch (RemoteException e) {

      testRetry(e);
      return setModel(uri, modelExpression);
    }
  }

  /**
   * Sets the Model attribute of the RemoteSessionWrapperSession object
   *
   * @param inputStream a remote inputstream
   * @param uri The new Model value
   * @param modelExpression The new Model value
   * @return RETURNED VALUE TO DO
   * @throws QueryException EXCEPTION TO DO
   */
  public long setModel(InputStream inputStream, URI uri,
      ModelExpression modelExpression) throws QueryException {

    try {

      long r = remoteSession.setModel(inputStream, uri, modelExpression);
      resetRetries();
      return r;
    }
    catch (RemoteException e) {

      testRetry(e);
      return setModel(inputStream, uri, modelExpression);
    }
  }

  /**
   * Sets the AutoCommit attribute of the RemoteSessionWrapperSession object
   *
   * @param autoCommit The new AutoCommit value
   * @throws QueryException EXCEPTION TO DO
   */
  public void setAutoCommit(boolean autoCommit) throws QueryException {

    try {

      remoteSession.setAutoCommit(autoCommit);

      // autoCommit has been successfully issued on the
      // first attempt.
      this.autoCommit = autoCommit;

      resetRetries();
    }
    catch (RemoteException e) {

      // if autocommit was set the off/false then an
      // exception would be thrown by testRetry informing
      // the user of a rollback

      testRetry(e);

      // a successful retry to re-establish server
      // connectivity has been made.  Autocommit will
      // now default to true (a new session).

      // set the requested value of autocommit
      setAutoCommit(autoCommit);

    }
  }

  //
  // Methods implementing the Session interface
  //

  public void insert(URI modelURI, Set statements) throws QueryException {

    try {

      remoteSession.insert(modelURI, statements);
      resetRetries();
    }
    catch (RemoteException e) {

      testRetry(e);
      insert(modelURI, statements);
    }
  }

  public void insert(URI modelURI, Query query) throws QueryException {

    try {

      remoteSession.insert(modelURI, query);
      resetRetries();
    }
    catch (RemoteException e) {

      testRetry(e);
      insert(modelURI, query);
    }
  }

  public void delete(URI modelURI, Set statements) throws QueryException {

    try {

      remoteSession.delete(modelURI, statements);
      resetRetries();
    }
    catch (RemoteException e) {

      testRetry(e);
      delete(modelURI, statements);
    }
  }

  public void delete(URI modelURI, Query query) throws QueryException {

    try {

      remoteSession.delete(modelURI, query);
      resetRetries();
    }
    catch (RemoteException e) {

      testRetry(e);
      delete(modelURI, query);
    }
  }

 /**
  * Backup all the data on the specified server. The database is not changed by
  * this method.
  *
  * @param sourceURI The URI of the server or model to backup.
  * @param destinationURI The URI of the file to backup into.
  * @throws QueryException if the backup cannot be completed.
  */
  public void backup(URI sourceURI, URI destinationURI) throws QueryException {

    try {

      remoteSession.backup(sourceURI, destinationURI);
      resetRetries();
    }
    catch (RemoteException e) {

      testRetry(e);
      backup(sourceURI, destinationURI);
    }
  }

  /**
   * Backup all the data on the specified server to an output stream.
   * The database is not changed by this method.
   *
   * @param sourceURI The URI of the server or model to backup.
   * @param outputStream The stream to receive the contents
   * @throws QueryException if the backup cannot be completed.
   */
  public void backup(URI sourceURI, OutputStream outputStream)
    throws QueryException {

    try {

      remoteSession.backup(sourceURI, outputStream);
      resetRetries();
    }
    catch (RemoteException e) {

      testRetry(e);
      backup(sourceURI, outputStream);
    }
  }


  /**
   * Restore all the data on the specified server. If the database is not
   * currently empty then the database will contain the union of its current
   * content and the content of the backup file when this method returns.
   *
   * @param serverURI The URI of the server to restore.
   * @param sourceURI The URI of the backup file to restore from.
   * @throws QueryException if the restore cannot be completed.
   */
  public void restore(URI serverURI, URI sourceURI) throws QueryException {

    try {

      remoteSession.restore(serverURI, sourceURI);
      resetRetries();
    }
    catch (RemoteException e) {

      testRetry(e);
      restore(serverURI, sourceURI);
    }
  }

  /**
   * Restore all the data on the specified server. If the database is not
   * currently empty then the database will contain the union of its current
   * content and the content of the backup file when this method returns.
   *
   * @param inputStream a client supplied inputStream to obtain the restore
   *        content from. If null assume the sourceURI has been supplied.
   * @param serverURI The URI of the server to restore.
   * @param sourceURI The URI of the backup file to restore from.
   * @throws QueryException if the restore cannot be completed.
   */
  public void restore(InputStream inputStream, URI serverURI, URI sourceURI)
      throws QueryException {

    try {

      remoteSession.restore(inputStream, serverURI, sourceURI);
      resetRetries();
    }
    catch (RemoteException e) {

      testRetry(e);
      restore(inputStream, serverURI, sourceURI);
    }
  }


  /**
   * METHOD TO DO
   *
   * @param modelURI PARAMETER TO DO
   * @param modelTypeURI PARAMETER TO DO
   * @throws QueryException EXCEPTION TO DO
   */
  public void createModel(URI modelURI, URI modelTypeURI) throws QueryException {

    try {

      remoteSession.createModel(modelURI, modelTypeURI);
      resetRetries();
    }
    catch (RemoteException e) {

      testRetry(e);
      createModel(modelURI, modelTypeURI);
    }
  }

  /**
   * METHOD TO DO
   *
   * @param uri PARAMETER TO DO
   * @throws QueryException EXCEPTION TO DO
   */
  public void removeModel(URI uri) throws QueryException {

    try {
      remoteSession.removeModel(uri);
      resetRetries();
    }
    catch (RemoteException e) {
      testRetry(e);
      removeModel(uri);
    }
  }

  public boolean modelExists(URI uri) throws QueryException {
    try {
      boolean modelExists = remoteSession.modelExists(uri);
      resetRetries();
      return modelExists;
    }
    catch (RemoteException e) {
      testRetry(e);
      return modelExists(uri);
    }
  }

  /**
   * METHOD TO DO
   *
   * @throws QueryException EXCEPTION TO DO
   */
  public void commit() throws QueryException {

    try {

      remoteSession.commit();
      resetRetries();
    }
    catch (RemoteException e) {

      testRetry(e);
      commit();
    }
  }

  /**
   * METHOD TO DO
   *
   * @throws QueryException EXCEPTION TO DO
   */
  public void rollback() throws QueryException {

    try {

      remoteSession.rollback();
      resetRetries();
    }
    catch (RemoteException e) {

      testRetry(e);
      rollback();
    }
  }

  /**
   * METHOD TO DO
   *
   * @param queries PARAMETER TO DO
   * @return RETURNED VALUE TO DO
   * @throws QueryException EXCEPTION TO DO
   */
  public List query(List queries) throws QueryException {

    try {

      List remoteAnswers = remoteSession.query(queries);
      resetRetries();
      List localAnswers = new ArrayList(remoteAnswers.size());

      Iterator i = remoteAnswers.iterator();
      while (i.hasNext()) {
        Object ans = i.next();
        if (!(ans instanceof RemoteAnswer)) {
          throw new QueryException("Non-answer returned from query.");
        }
        localAnswers.add(new RemoteAnswerWrapperAnswer((RemoteAnswer)ans));
      }

      return localAnswers;
    }
    catch (RemoteException e) {

      testRetry(e);
      return query(queries);
    }
  }

  /**
   * METHOD TO DO
   *
   * @param query PARAMETER TO DO
   * @return RETURNED VALUE TO DO
   * @throws QueryException EXCEPTION TO DO
   */
  public Answer query(Query query) throws QueryException {

    try {

      RemoteAnswer ans = remoteSession.query(query);
      resetRetries();
      return new RemoteAnswerWrapperAnswer(ans);
    }
    catch (RemoteException e) {

      testRetry(e);
      return query(query);
    }
  }

  /**
   * METHOD TO DO
   */
  public void close() throws QueryException {

    try {

      remoteSession.close();
      resetRetries();
    }
    catch (java.rmi.NoSuchObjectException e) {
      // do nothing as the RMI server has removed
      // the reference
    }
    catch (RemoteException e) {

      // no need to retry, since the session is gone
      throw new QueryException("Java RMI failure", e);
    }
  }

  /**
   * METHOD TO DO
   *
   * @param securityDomain PARAMETER TO DO
   * @param username PARAMETER TO DO
   * @param password PARAMETER TO DO
   */
  public void login(URI securityDomain, String username, char[] password) {

    try {

      remoteSession.login(securityDomain, username, password);
    }
    catch (RemoteException e) {

      try {
        // test if this should be retried
        testRetry(e);
        try {
          // successfully got new session.  Try to log in.
          remoteSession.login(securityDomain, username, password);
        } catch (RemoteException re) {
          // unable to log in to a new session
          re.printStackTrace();
        } finally {
          resetRetries();
        }
      } catch (QueryException qe) {
        // retry not possible
        qe.printStackTrace();
      }
    }
  }


  /**
   * Tests if an RMIException was caused by a retryable condition.
   * If so, then obtains a new session for retrying.
   *
   * @throws QueryException if remote method can't be retried.
   */
  protected void testRetry(RemoteException e) throws QueryException {

    // determine if a retry should be attempted.
    if (!(e instanceof java.rmi.ConnectException) || retryCount == 0) {
      resetRetries();
      throw new QueryException("Java RMI failure", e);
    }

    try {

      RmiSessionFactory rmiSessionFactory = null;

      // create a new RMI session factory
      try {
        rmiSessionFactory = new RmiSessionFactory(serverURI);
      } catch (NamingException ex) {
        throw new QueryException("Java RMI reconnection failure", ex);
      } catch (NonRemoteSessionException nrse) {
        throw new QueryException("Server name modification during a query reconnection");
      }

      // obtain a new remoteSession and replace the current one
      remoteSession = rmiSessionFactory.
          getRemoteSessionFactory().newRemoteSession();

      // was a transaction in progress before the server connectivity
      // was lost?
      if ( ! this.autoCommit ) {

        // all new sessions will result in automcommit set to on;
        this.autoCommit = true;

        // since a transaction was in progress when server connectivity
        // was lost we must notify the user a possible rollback
        throw new QueryException("Connectivity to server "+
                                 this.serverURI + " was lost during a "+
                                "transaction, which has resulted in a "+
                                "transaction rollback.  "+
                                "Connectivity has now been re-established.");
      }


    } catch (RemoteException re) {
      throw new QueryException("Java RMI reconnection failure", re);
    }
    retryCount--;
  }

  /**
   * Resets the retry count.
   */
  protected void resetRetries() {
    retryCount = RETRY_COUNT;
  }

  public boolean isLocal() {
    return false;
  }

  /**
   * {@inheritDoc}
   */
  public RulesRef buildRules(URI ruleModel, URI baseModel, URI destModel) throws QueryException, org.mulgara.rules.InitializerException {
    try {
      RulesRef ref = remoteSession.buildRules(ruleModel, baseModel, destModel);
      logger.warn("got rules from RMI");
      return ref;
    } catch (RemoteException re) {
      throw new org.mulgara.rules.InitializerException("Java RMI reconnection failure", re);
    }
  }

  /**
   * {@inheritDoc}
   */
  public void applyRules(RulesRef rules) throws RulesException {
    try {
      remoteSession.applyRules(rules);
    } catch (RemoteException re) {
      throw new RulesException("Java RMI reconnection failure", re);
    }
  }

}