/*
 * Copyright (c) 2010 Netcetera AG and others.
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
package ch.netcetera.eclipse.workspaceconfig.net;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Interface to clients implementing the http communication to fetch preference files.
 */
public interface IWorkspacePreferenceClient {
  
  /**
   * Gets the preference file data. 
   * 
   * @param url the URL of the preference file to get
   * @param monitor the progress monitor to use
   * @return the preference file data
   * @throws CoreException on errors
   */
  IPreferenceFileData getPreferenceFileData(String url, IProgressMonitor monitor) throws CoreException;
}
