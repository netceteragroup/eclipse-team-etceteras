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
package ch.netcetera.eclipse.projectconfig.net;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Interface to clients implementing the http communication to fetch project configuration script files.
 */
public interface IProjectConfigurationClient {
  
  /**
   * Gets the project configuration script file data. 
   * 
   * @param url the URL of the project configuration script file to get
   * @param monitor the progress monitor to use
   * @return the project configuration script file data
   * @throws CoreException on errors
   */
  IProjectConfigurationScriptData getProjectConfiguationScriptFileData(String url, IProgressMonitor monitor) 
      throws CoreException;
}
