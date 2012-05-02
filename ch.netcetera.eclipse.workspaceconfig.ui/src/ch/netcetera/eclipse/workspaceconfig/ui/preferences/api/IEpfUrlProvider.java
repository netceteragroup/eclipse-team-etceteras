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
package ch.netcetera.eclipse.workspaceconfig.ui.preferences.api;

/**
 * Interface to classes providing a URL to a EPF file.
 */
public interface IEpfUrlProvider {

  /**
   * Gets an single URL of an EPF file. The protocols {@code http://}, {@code https://} and {@code file://}
   * are acceptable.
   * 
   * @return the URL of the RPF file
   */
  String getEpfUrl();
}
