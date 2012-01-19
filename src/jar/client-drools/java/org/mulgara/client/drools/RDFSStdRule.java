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

package org.mulgara.client.drools;

import org.mulgara.itql.ItqlInterpreterBean;
import org.mulgara.itql.ItqlInterpreterException;
import org.mulgara.query.Answer;
import org.mulgara.query.TuplesException;


/**
 * Rule object for implementing RDFS in Drools configuration.
 *
 * @created 2004-07-09
 *
 * @author Paul Gearon
 *
 * @version $Revision$
 *
 * @modified $Date: 2005/01/05 04:57:33 $ by $Author: newmana $
 *
 * @maintenanceAuthor $Author: newmana $
 *
 * @copyright &copy;2001-2004
 *   <a href="http://www.pisoftware.com/">Plugged In Software Pty Ltd</a>
 *
 * @licence <a href="{@docRoot}/../../LICENCE">Mozilla Public License v1.1</a>
 */
public class RDFSStdRule extends RDFSRule {


  /**
   * Main constructor.  This is called in the bootstrap rule of a Drools configuration.
   *
   * @param init The bootstrap object, contains all info needed for a session.
   * @param name The name of this rule object.
   * @param query The iTQL query which generates the rules.
   */
  public RDFSStdRule(Bootstrap init, String name, String query) {
    super(init, name, query);
    System.out.println("Initializing Rule \"" + name + "\" with query: " + query);
  }


  /**
   * Insert the data for the query into the inference model.
   */
  public void insertData() throws ItqlInterpreterException {
    System.out.println("Insertion triggered for rule: " + name);

    String selQuery = queryString.trim();
    if (selQuery.endsWith(";")) {
      selQuery = selQuery.substring(0, selQuery.lastIndexOf(';'));
    }

    String cmdQuery = "insert " + selQuery + " into <" + inferenceModel + "> ;";

    System.out.println("Executing: " + cmdQuery);
    try {
      itql.executeUpdate(cmdQuery);  // catch
    } catch (ItqlInterpreterException e) {
      System.err.println("Error while inserting: " + cmdQuery);
      System.err.println(e.getMessage());
      e.printStackTrace();
      throw e;
    }

    // update the row count
    updateRowCache();
  }

}
