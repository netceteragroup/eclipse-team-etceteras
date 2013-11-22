/*
 * Copyright (c) 2009 Netcetera AG and others.
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import static ch.netcetera.eclipse.workspaceconfig.ui.preferences.ConfigurationXMLConstants.XML_ENVVAR;
import static ch.netcetera.eclipse.workspaceconfig.ui.preferences.ConfigurationXMLConstants.XML_ENVVARS;
import static ch.netcetera.eclipse.workspaceconfig.ui.preferences.ConfigurationXMLConstants.XML_EPFURL;
import static ch.netcetera.eclipse.workspaceconfig.ui.preferences.ConfigurationXMLConstants.XML_EPFURLS;

/**
 * Parser that parses the XML stored in the Eclipse preferences.
 */
public class ConfigurationXmlParser {

  private static final String ENCODING = "ISO-8859-1";

  private final XMLInputFactory factory;

  /**
   * No argument constructor.
   */
  ConfigurationXmlParser() {
    this.factory = XMLInputFactory.newInstance();
    // disable various features that we don't need and just cost performance
    this.factory.setProperty("javax.xml.stream.isValidating", Boolean.FALSE);
    this.factory.setProperty("javax.xml.stream.isNamespaceAware", Boolean.FALSE);
    this.factory.setProperty("javax.xml.stream.isReplacingEntityReferences", Boolean.FALSE);
    this.factory.setProperty("javax.xml.stream.isSupportingExternalEntities", Boolean.FALSE);
    this.factory.setProperty("javax.xml.stream.supportDTD", Boolean.FALSE);
  }

  /**
   * Parses the XML representation of the EPF URL list.
   *
   * @param xml the XML to parse
   * @return the EPF URL list
   * @throws IOException on error
   */
  public List<String> parseEpfUrlList(String xml) throws IOException {
    return parseList(XML_EPFURLS, XML_EPFURL, xml);
  }

  /**
   * Parses the XML representation of the environment variable list.
   *
   * @param xml the XML to parse
   * @return the environment variable list
   * @throws IOException on error
   */
  public List<String> parseEnvVarList(String xml) throws IOException {
    return parseList(XML_ENVVARS, XML_ENVVAR, xml);
  }


  private List<String> parseList(String rootElement, String listElement, String xml) throws IOException {
    try {
      InputStream inputStream = new ByteArrayInputStream(xml.getBytes(ENCODING));
      XMLStreamReader reader = this.factory.createXMLStreamReader(inputStream, ENCODING);

      List<String> envVarList = new ArrayList<>();

      while (reader.hasNext()) {
        int event = reader.next();

        if (event == XMLStreamConstants.START_ELEMENT) {
          String localName = reader.getLocalName();
          if (rootElement.equals(localName)) {
            while (reader.hasNext()) {
              event = reader.next();
              if (event == XMLStreamConstants.START_ELEMENT) {
                localName = reader.getLocalName();
                if (listElement.equals(localName)) {
                  envVarList.add(getContent(reader));
                }
              }
            }
          }
        }
      }
      return envVarList;
    } catch (XMLStreamException e) {
      throw new IOException("could not parse XML", e);
    }
  }

  private String getContent(XMLStreamReader reader) throws XMLStreamException {
    StringBuilder builder = new StringBuilder();
    while (reader.hasNext()) {
      int event = reader.next();
      if (event == XMLStreamConstants.END_ELEMENT) {
        return builder.toString().trim();
      } else if (event == XMLStreamConstants.CHARACTERS || event == XMLStreamConstants.CDATA) {
        builder.append(reader.getText());
      }
    }
    return builder.toString().trim();
  }

}
