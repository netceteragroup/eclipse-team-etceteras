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

/**
 * Class containing the XML related constants used when dealing with the configuration.
 */
final class ConfigurationXMLConstants {

  /*
   * The XML representation of the EPF URL list:
   *
   * <epfurls>
   *   <epfurl>http://foo.bar.example/...</epfurl>
   *   <epfurl>http://foo.bar.example/...</epfurl>
   * <epfurls/>
   */

  /** XML tag for the EPF URL list. */
  static final String XML_EPFURLS = "epfurls";

  /** XML tag for a single EPF URL. */
  static final String XML_EPFURL = "epfurl";

  
  /*
   * The XML representation of the EPF URL list:
   *
   * <envvars>
   *   <envvar>user.name</envvar>
   *   <envvar></envvar>
   * <envvars/>
   */

  /** XML tag for the environment variables list. */
  static final String XML_ENVVARS = "envvars";

  /** XML tag for a single environment variable. */
  static final String XML_ENVVAR = "envvar";
  
  
  /**
   * Not instantiable.
   */
  private ConfigurationXMLConstants() {
    throw new AssertionError("Not instantiable.");
  }
}
