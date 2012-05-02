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
package ch.netcetera.eclipse.workspaceconfig.ui.fragment;

import ch.netcetera.eclipse.workspaceconfig.ui.preferences.api.IEpfUrlProvider;

/**
 * Example implementation of an {@link IEpfUrlProvider} that dynamically calculates the
 * EPF URL depending on the Java system property {@code os.arch}.
 */
public class OSArchEpfUrlProvider implements IEpfUrlProvider {

  /**
   * {@inheritDoc}
   */
  @Override
  public String getEpfUrl() {
    String osArch = System.getProperty("os.arch");
    if (osArch == null) {
      return "";
    }
    return "http://whatever.some.tld/config-" +  osArch + ".epf";
  }
}
