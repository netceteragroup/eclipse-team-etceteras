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

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.SSLHandshakeException;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.ProxyHost;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.eclipse.core.net.proxy.IProxyData;
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
   * A simple {@link IProxyData} that contains the information of a {@link Proxy}.
   * <p>
   * This class in not thread safe.
   * </p>
   */
  protected static final class NativeProxyData implements IProxyData {

    private int port;
    private String hostName;
    private String userId;
    private String password;
    private String type;

    /**
     * Constructor.
     *
     * @param proxy the proxy, not {@literal null}
     * @throws IllegalArgumentException if the proxy address is not a {@link InetSocketAddress},
     *  or the proxy type is {@link Proxy.Type#PLAIN}
     */
    NativeProxyData(Proxy proxy) throws IllegalArgumentException {
      SocketAddress address = proxy.address();
      if (address instanceof InetSocketAddress) {
        InetSocketAddress internetAddress = (InetSocketAddress) address;
        this.port = internetAddress.getPort();
        this.hostName = internetAddress.getHostName();
        switch (proxy.type()) {
          case HTTP:
            this.type = IProxyData.HTTP_PROXY_TYPE;
            break;
          case SOCKS:
            this.type = IProxyData.SOCKS_PROXY_TYPE;
            break;
          default:
            throw new IllegalArgumentException("unknown proxy type " + proxy.type());
        }
      } else {
        throw new IllegalArgumentException("unknown address type " + address.getClass().getName());
      }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disable() {
      // nop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHost() {
      return this.hostName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPassword() {
      return this.password;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPort() {
      return this.port;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
      return this.type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserId() {
      return this.userId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRequiresAuthentication() {
      /*
       * That's a good question. I don't really know. And if we did, I wouldn't
       * know the username or password.
       */
      return false;
    }

    @Override
    public void setHost(String host) {
      this.hostName = host;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPassword(String password) {
      this.password = password;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPort(int port) {
      this.port = port;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUserid(String userid) {
      this.userId = userid;
    }

  }

  /**
   * Call-back interface for handling HTTP responses.
   *
   * @param <R> the type of result of handling the response.
   */
  public interface IResponseHandler<R> {

    /**
     * Handle a HTTP response.
     *
     * @param method the method that was executed
     * @param monitor the monitor to report progress on
     * @return the result of handling the response
     * @throws IOException if reading the response fails
     */
    R handleResponse(HttpMethodBase method, IProgressMonitor monitor) throws IOException;

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

  /**
   * Closes a {@link Closeable} silently.
   * 
   * @param closable the {@link Closeable} to close
   */
  protected static void closeQuietly(Closeable closable) {
    if (closable != null) {
      try {
        closable.close();
      } catch (IOException e) {
        // ignore
      }
    }
  }

  private HostConfiguration createHostConfiguration(IProxyData proxyData, String url) throws IOException {
    HostConfiguration configuration = new HostConfiguration();

    if (proxyData != null) {
      ProxyHost proxyHost = new ProxyHost(proxyData.getHost(), proxyData.getPort());
      configuration.setProxyHost(proxyHost);
    }

    return configuration;
  }

  private HttpState createHttpSate(IProxyData proxyData) {
    HttpState state = new HttpState();
    if (proxyData != null && proxyData.isRequiresAuthentication()) {
      Credentials credentials = new UsernamePasswordCredentials(proxyData.getUserId(), proxyData.getPassword());
      state.setProxyCredentials(new AuthScope(proxyData.getHost(), proxyData.getPort()), credentials);
    }
    return state;
  }

  private IProxyData getProxyData(String url) throws CoreException {
    if (getProxyService() != null && getProxyService().isProxiesEnabled()) {
      try {
        URI uri = new URI(url);
        if (getProxyService().isSystemProxiesEnabled()) {
          return getSystemProxyData(uri);
        } else {
          return getEclipseProxyData(getProxyService(), uri);
        }
      } catch (URISyntaxException e) {
        IStatus status = new Status(IStatus.ERROR, getBundleSymbolicName(), "invlid URL" + url, e);
        throw new CoreException(status); // NOPMD by michael on 11/20/10 2:22 PM
      }
    }
    return null;
  }

  private IProxyData getEclipseProxyData(IProxyService service, URI uri) {
    IProxyData[] proxies = service.select(uri);
    if (proxies.length > 0) {
      return proxies[0];
    }
    return null;
  }

  private IProxyData getSystemProxyData(URI uri) throws CoreException {
    ProxySelector selector = ProxySelector.getDefault();
    List<Proxy> proxies = selector.select(uri);
    if (!proxies.isEmpty()) {
      Proxy proxy = proxies.get(0);
      if (proxy.type() != Proxy.Type.DIRECT) {
        try {
          return new NativeProxyData(proxy);
        } catch (IllegalArgumentException e) {
          /*
           * This should really never happen because we just checked the proxy type
           * and there are just Internet addresses.
           */
          IStatus status = new Status(IStatus.ERROR, getBundleSymbolicName(), "could not create proxy" + proxy, e);
          throw new CoreException(status); // NOPMD 2010-11-20 pellaton: stack trace is preserved inside the status
        }
      }
    }
    return null;
  }

  private int executeMethod(HttpClient client, HttpMethod method, String url) throws IOException,
  HttpException, CoreException {

    IProxyData proxyData = this.getProxyData(method.getURI().getURI());
    HostConfiguration hostConfiguration = this.createHostConfiguration(proxyData, url);
    HttpState state = this.createHttpSate(proxyData);
    return client.executeMethod(hostConfiguration, method, state);
  }

  private <R> R executePreparedMethod(String url, IResponseHandler<R> handler, IProgressMonitor monitor,
      HttpMethodBase method,
      HttpClient client) throws CoreException {
    try {
      int statusCode = this.executeMethod(client, method, url);
      if (statusCode == HttpStatus.SC_OK) {
        return handler.handleResponse(method, monitor);
      } else {
        throw convertHttpStatusToException(statusCode, url);
      }
    } catch (HttpException e) {
      throw wrapHttpException(e);
    } catch (SSLHandshakeException e) {
      throw wrapSslHandshakeException(e);
    } catch (IOException e) {
      throw wrapIoException(e);
    } finally {
      method.releaseConnection();
    }
  }

  private HttpClient createClient() {
    return new HttpClient();
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
    HttpClient client = this.createClient();
    GetMethod get = new GetMethod(url);
    get.addRequestHeader("Accept-Encoding", "gzip");
    get.setFollowRedirects(true);

    return executePreparedMethod(url, handler, monitor, get, client);
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

  private CoreException convertHttpStatusToException(int httpStatusCode, String baseUrl) {
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
   * @param method the HTTP method
   * @param stream the input stream
   * @param monitor the progress monitor
   * @return the wrapped input stream
   * @throws IOException on error
   */
  protected InputStream wrapResponseStream(HttpMethodBase method, InputStream stream, IProgressMonitor monitor)
      throws IOException {
    InputStream input = stream;
    Header contentEncoding = method.getResponseHeader("Content-Encoding");
    long contentLength = method.getResponseContentLength();
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
  public void unbindProxyService(IProxyService proxyService) {
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
