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
package ch.netcetera.eclipse.workspaceconfig.net.internal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import ch.netcetera.eclipse.common.io.IOUtil;
import ch.netcetera.eclipse.common.net.AbstractHttpClient;
import ch.netcetera.eclipse.workspaceconfig.net.IPreferenceFileData;
import ch.netcetera.eclipse.workspaceconfig.net.IWorkspacePreferenceClient;

/**
 * HTTP client to fetch workspace preference files.
 */
public class WorkspacePreferenceClient extends AbstractHttpClient implements
    IWorkspacePreferenceClient {

  /**
   * {@inheritDoc}
   */
  @Override
  protected String getBundleSymbolicName() {
    final Bundle bundle = FrameworkUtil.getBundle(this.getClass());
    if (bundle == null) {
      return this.getClass().getPackage().getName();
    } else {
      return bundle.getSymbolicName();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IPreferenceFileData getPreferenceFileData(String url, IProgressMonitor monitor)
      throws CoreException {
    return this.executeGetRequest(url, new PreferenceFileResponseHandler(), monitor);
  }

  /**
   * A response handler that parses the response.
   */
  final class PreferenceFileResponseHandler implements IResponseHandler<IPreferenceFileData> {

    /**
     * {@inheritDoc}
     */
    @Override
    public IPreferenceFileData handleResponse(HttpResponse response, IProgressMonitor monitor)
        throws IOException {
      return WorkspacePreferenceClient.this.handleResponse(response, monitor);
    }
  }

  /**
   * Handles the HTTP response.
   *
   * @param response the HTTP response
   * @param monitor the progress monitor
   * @return the preference file date
   * @throws IOException on error
   */
  protected IPreferenceFileData handleResponse(HttpResponse response, IProgressMonitor monitor) throws IOException {
    InputStream input = response.getEntity().getContent();
    try {
      monitor.subTask("Transfering data from server...");
      input = wrapResponseStream(response, input, monitor);

      monitor.subTask("Parsing data..");

      ByteArrayOutputStream output = new ByteArrayOutputStream(DEFAULT_BUFFER_SIZE);
      copy(input, output);
      return new PreferenceFileData(output.toByteArray());
    } finally {
      IOUtil.closeSilently(input);
    }
  }
}
