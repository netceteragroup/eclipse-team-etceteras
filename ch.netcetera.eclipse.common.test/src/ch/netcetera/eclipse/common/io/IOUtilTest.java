/*
 * Copyright (c) 2009 the Eclipsed Team Etceteras Project and others.
 * All rights reserved.
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * - Eclipse Team Etceteras - http://netceteragroup.github.io/eclipse-team-etceteras/updatesite/
 */
package ch.netcetera.eclipse.common.io;

import java.io.Closeable;
import java.io.IOException;

import org.junit.Test;

/**
 * Tests for {@link IOUtil}.
 */
public class IOUtilTest {

  /**
   * Tests {@link IOUtil#closeSilently(java.io.Closeable)} with a null closeable.
   */
  @Test
  public void testClieseSilentlyNull() {
    IOUtil.closeSilently(null);
  }

  /**
   * Tests {@link IOUtil#closeSilently(java.io.Closeable)} with a closeable that was already closed.
   */
  @Test
  public void testClieseSilentlyCloseableAlreadyClosed() {
    @SuppressWarnings("resource")
    Closeable closeable = new Closeable() {
      @Override
      public void close() throws IOException {
        throw new IOException("was closed before");
      }
    };
    IOUtil.closeSilently(closeable);
  }

  /**
   * Tests {@link IOUtil#closeSilently(java.io.Closeable)} with a closeable that can be closed.
   */
  @Test
  public void testClieseSilentlyAllFine() {
    @SuppressWarnings("resource")
    Closeable closeable = new Closeable() {
      @Override
      public void close() throws IOException {
        // empty.
      }
    };
    IOUtil.closeSilently(closeable);
  }
}
