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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IExportedPreferences;
import org.eclipse.core.runtime.preferences.IPreferenceFilter;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.framework.FrameworkUtil;

import ch.netcetera.eclipse.common.io.IOUtil;
import ch.netcetera.eclipse.workspaceconfig.core.IPreferencesImportService;
import ch.netcetera.eclipse.workspaceconfig.net.IPreferenceFileData;
import ch.netcetera.eclipse.workspaceconfig.net.IWorkspacePreferenceClient;


/**
 * Service that imports the remote workspace preferences.
 */
public class PreferencesImportService implements IPreferencesImportService {

  private static final String PROTOCOL_PREFIX_FILE = "file";
  private static final String PROTOCOL_PREFIX_HTTP = "http";

  private volatile IWorkspacePreferenceClient client;

  /**
   * Binds the {@link IWorkspacePreferenceClient} service reference.
   *
   * @param client the client service reference to bind
   */
  public void bindClient(IWorkspacePreferenceClient client) {
    this.client = client;
  }

  /**
   * Unbinds the {@link IWorkspacePreferenceClient} service reference.
   *
   * @param client the client service reference to bind
   */
  public void unbindClient(IWorkspacePreferenceClient client) {
    this.client = null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IStatus importConfigFile(String url, List<String> systemPropertyReplacementList) {
    IStatus importStatus = Status.OK_STATUS;
    if (url.toLowerCase().startsWith(PROTOCOL_PREFIX_HTTP)) {
      importStatus = importConfigFileHttp(url, systemPropertyReplacementList);
    } else if (url.toLowerCase().startsWith(PROTOCOL_PREFIX_FILE)) {
      importStatus = importConfigFileFile(url, systemPropertyReplacementList);
    }
    return importStatus;
  }

  /**
   * Imports a configuration from a file:// URL.
   *
   * @param systemPropertyReplacementList the system property replacements to do duringthe import
   */
  private IStatus importConfigFileFile(String url, List<String> systemPropertyReplacementList) {
    String bundleSymbolicName = FrameworkUtil.getBundle(this.getClass()).getSymbolicName();
    IStatus importStatus = Status.OK_STATUS;
    InputStream inputStream = null;

    try {
      URI uri = new URI(url);
      if (uri.getAuthority() == null) {
        File sourceFile = new File(uri);
        if (sourceFile.canRead()) {
          try {
            inputStream = new FileInputStream(sourceFile);
            importConfigurationFromStream(inputStream, systemPropertyReplacementList);
          } catch (FileNotFoundException e) {
            importStatus = wrapExceptionInErrorStatus(e);
          } catch (SecurityException e) {
            importStatus = wrapExceptionInErrorStatus(e);
          } catch (CoreException e) {
            importStatus = wrapExceptionInErrorStatus(e);
          } finally {
            IOUtil.closeSilently(inputStream);
          }
        } else {
          importStatus = new Status(IStatus.ERROR, bundleSymbolicName, "Could not read local file.");
        }
      } else {
        importStatus = new Status(IStatus.ERROR, bundleSymbolicName, "The file url is invalid.");
      }
    } catch (IOException e) {
      importStatus = new Status(IStatus.ERROR, bundleSymbolicName, e.getLocalizedMessage(), e);
    } catch (URISyntaxException e) {
      importStatus = new Status(IStatus.ERROR, bundleSymbolicName, e.getLocalizedMessage(), e);
    }
    return importStatus;
  }

  /**
   * Imports a configuration from a http:// or https:// URL.
   *
   * @param systemPropertyReplacementList the system property replacements to do during the import
   */
  private IStatus importConfigFileHttp(String url, List<String> systemPropertyReplacementList) {
    IStatus importStatus = Status.OK_STATUS;

    if (this.client != null) {
      try {
        IPreferenceFileData file = this.client.getPreferenceFileData(url, new NullProgressMonitor());
        importConfigurationFromStream(new ByteArrayInputStream(file.getData()), systemPropertyReplacementList);
      } catch (CoreException e) {
        importStatus = wrapExceptionInErrorStatus(e);
      } catch (IOException e) {
        importStatus = wrapExceptionInErrorStatus(e);
      }
    } else {
      String bundleSymbolicName = FrameworkUtil.getBundle(this.getClass()).getSymbolicName();
      importStatus = new Status(IStatus.ERROR, bundleSymbolicName, "could not obtain client service.");
    }
    return importStatus;
  }

  /**
   * Imports a configuration from the {@link InputStream} passed.
   *
   * @param inputStream the {@link InputStream} to read the configuration from
   * @param systemPropertyReplacementList the replacement list
   * @throws CoreException on import errors
   * @throws IOException on IO errors
   */
  private void importConfigurationFromStream(InputStream inputStream, List<String> systemPropertyReplacementList)
      throws CoreException, IOException {
    IPreferenceFilter[] transfers = getPreferenceImportFilters();
    SystemPropertyReplacer replacer = new SystemPropertyReplacer(systemPropertyReplacementList);
    BufferedReplacementInputStream input = new BufferedReplacementInputStream(replacer, inputStream);
    IPreferencesService service = Platform.getPreferencesService();
    IExportedPreferences preferences = service.readPreferences(input);
    service.applyPreferences(preferences, transfers);
  }

  /**
   * Gets the preference filters. As all preferences of the remote file shall be imported, the filter
   * only limits the import to the instance and configuration preference scopes.
   *
   * @return the preference filters.
   */
  private IPreferenceFilter[] getPreferenceImportFilters() {
    IPreferenceFilter filter = new IPreferenceFilter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String[] getScopes() {
        return new String[] {InstanceScope.SCOPE, ConfigurationScope.SCOPE};
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Map getMapping(String scope) {
        return null;
      }
    };

    return new IPreferenceFilter[] {filter};
  }

  /**
   * Wraps a {@link Throwable} in a {@link IStatus} instance with the status value {@link IStatus#ERROR}.
   *
   * @param t the {@link Throwable} to wrap
   * @return the {@link IStatus} instance
   */
  private IStatus wrapExceptionInErrorStatus(Throwable t) {
    String bundleSymbolicName = FrameworkUtil.getBundle(this.getClass()).getSymbolicName();
    return new Status(IStatus.ERROR, bundleSymbolicName, t.getLocalizedMessage(), t);
  }
}