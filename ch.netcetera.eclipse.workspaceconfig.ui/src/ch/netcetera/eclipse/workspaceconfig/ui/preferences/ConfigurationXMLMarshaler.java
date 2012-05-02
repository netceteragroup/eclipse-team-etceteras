/*
 * Copyright (c) 2011 Netcetera AG and others.
 * All rights reserved.
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * - Netcetera AG: initial implementation
 */
package ch.netcetera.eclipse.workspaceconfig.ui.preferences;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import static ch.netcetera.eclipse.workspaceconfig.ui.preferences.ConfigurationXMLConstants.XML_ENVVAR;
import static ch.netcetera.eclipse.workspaceconfig.ui.preferences.ConfigurationXMLConstants.XML_ENVVARS;
import static ch.netcetera.eclipse.workspaceconfig.ui.preferences.ConfigurationXMLConstants.XML_EPFURL;
import static ch.netcetera.eclipse.workspaceconfig.ui.preferences.ConfigurationXMLConstants.XML_EPFURLS;

/**
 * Marshaler that converts the values to be stored in the Eclipse preferences into XML.
 */
public class ConfigurationXMLMarshaler {

  private static final String ENCODING = "ISO-8859-1";
  
  private final XMLOutputFactory factory;
  

  /**
   * Default constructor.
   */
  ConfigurationXMLMarshaler() {
    this.factory = XMLOutputFactory.newInstance();
  }

  /**
   * Marshals a EPF URL List into XML.
   * 
   * @param epfUrls the EPF URLs to marshal
   * @return the marshaled XMLs representation
   * @throws IOException on error
   */
  public String marshalEpfUrlList(Collection<String> epfUrls) throws IOException {
    return marshalList(XML_EPFURLS, XML_EPFURL, epfUrls);
  }
  
  /**
   * Marshals a environment variable List into XML.
   * 
   * @param envVarList the environment variables to marshal
   * @return the marshaled XMLs representation
   * @throws IOException on error
   */
  public String marshalEnvVarList(Collection<String> envVarList) throws IOException {
    return marshalList(XML_ENVVARS, XML_ENVVAR, envVarList);
  }
 
  
  private String marshalList(String rootElement, String itemElement, Collection<String> envVarList) throws IOException {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    try {
      XMLStreamWriter writer = this.factory.createXMLStreamWriter(output, ENCODING);
      writer.writeStartDocument(ENCODING, "1.0");
      writer.writeStartElement(rootElement);
      
      for (String envVar : envVarList) {
        writer.writeStartElement(itemElement);
        writer.writeCharacters(envVar);
        writer.writeEndElement();
      }
      
      writer.writeEndElement();
      writer.writeEndDocument();

      writer.flush();
      return output.toString(ENCODING);
    } catch (XMLStreamException e) {
      throw new IOException("Could not export list to xml.", e);
    }
  }
}
