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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

import ch.netcetera.eclipse.workspaceconfig.ui.preferences.api.IEpfUrlProvider;

/**
 * Initializer for the workspace configuration plug-in's preferences.
 */
public class WorkspaceConfigPreferenceInitializer extends AbstractPreferenceInitializer {

  private static final String EXTENSION_ID = "ch.netcetera.eclipse.workspace.defaultConfiguration";

  private static final String EPF_URL = "EpfUrl";
  private static final String URL = "url";
  
  private static final String EPF_URL_PROVIDER = "EpfUrlProvider";
  private static final String CLASS = "class";
  
  private static final String NEW_WORKSPACE_DETECTION = "NewWorkspaceDetection";
  private static final String DETECT_NEW_WORKSPACES = "detectNewWorkspaces";
  
  private static final String JAVA_SYSTEM_PROPERTY_REPLACEMENT = "JavaSystemPropertyReplacement";
  private static final String SYSTEM_PROPERTY = "systemProperty";

  
  /**
   * {@inheritDoc}
   */
  @Override
  public void initializeDefaultPreferences() {
    
    IConfigurationElement[] configElements = Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_ID);
    
    Set<String> urls = new HashSet<String>();
    Set<String> replacements = new HashSet<String>();
    boolean newWorkspaceDetection = false;
    
    for (IConfigurationElement configElement : configElements) {
      String elementName = configElement.getName();
    
      if (EPF_URL.equals(elementName)) {
        String url = configElement.getAttribute(URL);
        if (url != null && !url.isEmpty()) {
          urls.add(url);
        }
      } else if (EPF_URL_PROVIDER.equals(elementName)) {
        urls.addAll(handleEpfUrlProvider(configElement));
      } else if (NEW_WORKSPACE_DETECTION.equals(elementName)) {
        newWorkspaceDetection |= Boolean.parseBoolean(configElement.getAttribute(DETECT_NEW_WORKSPACES));
      } else if (JAVA_SYSTEM_PROPERTY_REPLACEMENT.equals(elementName)) {
        String replacement = configElement.getAttribute(SYSTEM_PROPERTY);
        if (replacement != null && !replacement.isEmpty()) {
          replacements.add(replacement);
        }
      }
    }
    
    ConfigurationUtil.saveEpfUrls(urls);
    ConfigurationUtil.saveEnvReplacements(replacements);
    ConfigurationUtil.saveNewWorkspaceDetection(newWorkspaceDetection);
  }
  
  private Collection<? extends String> handleEpfUrlProvider(IConfigurationElement configElement) {
    Set<String> urls = new HashSet<String>();
    try {
      Object executableExtension = configElement.createExecutableExtension(CLASS);
      if (executableExtension instanceof IEpfUrlProvider) {
        IEpfUrlProvider urlProvider = (IEpfUrlProvider) executableExtension;
        String epfUrl = urlProvider.getEpfUrl();
        if (epfUrl != null && !epfUrl.isEmpty()) {
          urls.add(epfUrl);
        }
      }
    } catch (CoreException e) {
      // ignore malformed extension points
    }
    return urls;
  }
}