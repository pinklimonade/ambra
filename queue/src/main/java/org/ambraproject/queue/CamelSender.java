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

package org.ambraproject.queue;

import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Required;
import org.w3c.dom.Document;
import java.util.Map;

/**
 * Apache Camel sender.
 * @author Dragisa Krsmanovic
 * @author Joe Osowski
 */
public class CamelSender implements MessageSender {

  private ProducerTemplate producerTemplate;

  /**
   * @inheritDoc
   */
  @Required
  public void setProducerTemplate(ProducerTemplate producerTemplate) {
    this.producerTemplate = producerTemplate;
  }

  /**
   * @inheritDoc
   */
  public void sendMessage(String destination, String body) {
    producerTemplate.sendBody(destination, body);
  }

  /**
   * @inheritDoc
   */
  public void sendMessage(String destination, Document body) {
    producerTemplate.sendBody(destination, body);
  }

  /**
   * @inheritDoc
   */
  public void sendMessage(String destination, Object body, Map<String,Object> headers) {
    producerTemplate.sendBodyAndHeaders(destination, body, headers);
  }
}
