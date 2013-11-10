/*
 * Copyright (c) 2013 Netcetera AG and others.
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

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;

import org.eclipse.core.net.proxy.IProxyData;

/**
 * A simple {@link IProxyData} that contains the information of a {@link Proxy}.
 * <p>
 * This class in not thread safe.
 * </p>
 */
final class NativeProxyData implements IProxyData {

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