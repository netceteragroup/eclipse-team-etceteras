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
package ch.netcetera.eclipse.workspaceconfig.core.internal;

import java.util.List;


/**
 * Replaces values in String based on system properties.
 */
public class SystemPropertyReplacer implements IReplacer {

  private static final String PREFIX_PATTERN = "\\$";

  private final List<String> replacementList;

  /**
   * Constructor.
   *
   * @param replacementList the replacement list
   * 
   */
  public SystemPropertyReplacer(List<String> replacementList) {
    this.replacementList = replacementList;
  }
  
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String replace(String in) {
    String out = in;
    for (String placeholder : this.replacementList) {
      String systemProperty = System.getProperty(placeholder);
      if (systemProperty != null) {
        out = out.replaceAll(PREFIX_PATTERN + placeholder, systemProperty);
      }
    }
    return out;
  }
}