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
package ch.netcetera.eclipse.common.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.SSLHandshakeException;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Base HTTP client for the different ETE subprojects.
 */
public abstract class AbstractHttpClient {

  /** The default buffer size for stream operations. */
  protected static final int DEFAULT_BUFFER_SIZE = 1024 * 8;

  /**
   * Call-back interface for handling HTTP responses.
   *
   * @param <R> the type of result of handling the response.
   */
  public interface IResponseHandler<R> {

    /**
     * Handle a HTTP response.
     *
     * @param response the method that was executed
     * @param monitor the monitor to report progress on
     * @return the result of handling the response
     * @throws IOException if reading the response fails
     */
    R handleResponse(HttpResponse response, IProgressMonitor monitor) throws IOException;

  }

  /**
   * An {@link InputStream} that wraps a {@link IProgressMonitor}. Reading from
   * the stream will report progress.
   */
  protected static final class ProgressReportingInputStream extends InputStream {
    private final InputStream stream;
    private final IProgressMonitor monitor;

    /**
     * Constructor.
     *
     * @param stream the stream to wrap, not {@literal null}
     * @param monitor the monitor to use, not {@literal null}
     */
    ProgressReportingInputStream(InputStream stream, IProgressMonitor monitor) {
      this.stream = stream;
      this.monitor = monitor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int available() throws IOException {
      return this.stream.available();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
      try {
        this.stream.close();
      } finally {
        this.monitor.done();
      }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mark(int readlimit) {
      this.stream.mark(readlimit);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean markSupported() {
      return this.stream.markSupported();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read() throws IOException {
      int data = this.stream.read();
      this.monitor.worked(1);
      return data;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
      int read = this.stream.read(b, off, len);
      this.monitor.worked(read);
      return read;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read(byte[] b) throws IOException {
      int read = this.stream.read(b);
      this.monitor.worked(read);
      return read;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset() throws IOException {
      // screws the monitor
      this.stream.reset();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long skip(long n) throws IOException {
      long skipped = this.stream.skip(n);
      this.monitor.worked((int) skipped);
      return skipped;
    }
  }

  private HttpResponse executeMethod(HttpClient client, HttpRequestBase method) throws IOException,
  HttpException, CoreException {

    HttpHost httpHost = new HttpHost(method.getURI().getHost());
    HttpRequest request = new BasicHttpRequest(method.getRequestLine());
    HttpContext context = new BasicHttpContext();
    HttpResponse response = client.execute(httpHost, request, context);
    return response;
  }

  private <R> R executePreparedMethod(IResponseHandler<R> handler, IProgressMonitor monitor,
      HttpRequestBase request,
      HttpClient client) throws CoreException {
    try {
      HttpResponse response = this.executeMethod(client, request);
      int statusCode = response.getStatusLine().getStatusCode();
      if (statusCode == HttpStatus.SC_OK) {
        return handler.handleResponse(response, monitor);
      } else {
        throw convertHttpStatusToException(statusCode);
      }
    } catch (HttpException e) {
      throw wrapHttpException(e);
    } catch (SSLHandshakeException e) {
      throw wrapSslHandshakeException(e);
    } catch (IOException e) {
      throw wrapIoException(e);
    }
  }

  /**
   * Executes a HTTP get request.
   *
   * @param <R> the return type
   * @param url the url
   * @param handler the response handler
   * @param monitor the progress monitor
   * @return the response
   * @throws CoreException on error
   */
  protected <R> R executeGetRequest(String url, IResponseHandler<R> handler, IProgressMonitor monitor)
  throws CoreException {
    HttpClient client = new DefaultHttpClient();
    HttpGet get = new HttpGet(url);
    get.addHeader("Accept-Encoding", "gzip");
    return executePreparedMethod(handler, monitor, get, client);
  }

  private CoreException wrapSslHandshakeException(SSLHandshakeException e) {
    IStatus status = new Status(IStatus.ERROR, getBundleSymbolicName(), e.getLocalizedMessage(), e);
    return new CoreException(status);
  }

  private CoreException wrapIoException(IOException e) {
    return this.wrapGenericException(e);
  }

  private CoreException wrapHttpException(HttpException e) {
    return this.wrapGenericException(e);
  }

  private CoreException wrapGenericException(Exception e) {
    IStatus status = new Status(IStatus.ERROR, getBundleSymbolicName(), e.getLocalizedMessage(), e);
    return new CoreException(status);
  }

  /**
   * Gets the bundle symbolic name.
   *
   * @return the bundle symbolic name
   */
  protected abstract String getBundleSymbolicName();

  private CoreException convertHttpStatusToException(int httpStatusCode) {
    IStatus status = new Status(IStatus.ERROR, getBundleSymbolicName(), "unexpected HTTP status code "
        + httpStatusCode);
    return new CoreException(status);
  }

  /**
   * Copy bytes from an {@link InputStream} to an {@link OutputStream}.
   *
   * @param input  the {@link InputStream} to read from
   * @param output  the {@link OutputStream} to write to
   * @throws NullPointerException if the input or output is null
   * @throws IOException if an I/O error occurs
   */
  protected static void copy(InputStream input, OutputStream output) throws IOException, NullPointerException {
    // taken from Commons IO 1.3
    byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
    int n = 0;
    while (-1 != (n = input.read(buffer))) { // NOPMD pellaton 2010-11-20 ok
      output.write(buffer, 0, n);
    }
  }

  private IProxyService proxyService;

  /**
   * Wraps the response stream.
   *
   * @param response the HTTP response
   * @param stream the input stream
   * @param monitor the progress monitor
   * @return the wrapped input stream
   * @throws IOException on error
   */
  protected InputStream wrapResponseStream(HttpResponse response, InputStream stream, IProgressMonitor monitor)
      throws IOException {
    InputStream input = stream;
    Header contentEncoding = response.getFirstHeader("Content-Encoding");
    long contentLength = response.getEntity().getContentLength();
    if (contentLength != -1) {
      monitor.beginTask("Parsing Response", (int) contentLength);
      input = new ProgressReportingInputStream(input, monitor);
    }
    if (contentEncoding != null && "gzip".equalsIgnoreCase(contentEncoding.getValue())) {
      input = new GZIPInputStream(input);
    }
    return input;
  }

  /**
   * Binds the {@link IProxyService} service reference.
   *
   * @param proxyService the {@link IProxyService} service reference to bind
   */
  public void bindProxyService(IProxyService proxyService) {
    this.proxyService = proxyService;
  }

  /**
   * Unbinds the {@link IProxyService} service reference.
   *
   * @param proxyService the {@link IProxyService} service reference to unbind
   */
  public void unbindProxyService(@SuppressWarnings("unused") IProxyService proxyService) {
    this.proxyService = null;
  }

  /**
   * Gets the {@link IProxyService} instance.
   *
   * @return the {@link IProxyService} instance
   */
  protected IProxyService getProxyService() {
    return proxyService;
  }
}
