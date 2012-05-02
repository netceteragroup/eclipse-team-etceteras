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
package ch.netcetera.eclipse.workspaceconfig.core;

import java.util.List;

import org.eclipse.core.runtime.IStatus;

/**
 * Interface to the remote preferences import service.
 */
public interface IPreferencesImportService {
  
  /**
   * Imports the configuration file (EPF) located at the URL passed into the workspace. The protocols file:// http:// 
   * and https:// are supported.
   *   
   * @param url the URL of the preference file to import
   * @param systemPropertyReplacementList the system property replacements to do during the import 
   * @return the status of the import operation
   */
  IStatus importConfigFile(String url, List<String> systemPropertyReplacementList); 
}
