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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ch.netcetera.eclipse.common.io.IOUtil;


/**
 * {@link InputStream} that reads the input line-wise from another {@link InputStream}
 * and allows call-back in the form of an {@link IReplacer} instance to
 * manipulate the contents before they get passed on.
 */
public class BufferedReplacementInputStream extends InputStream {

  private final StringBuffer buffer = new StringBuffer(); // NOPMD pellaton 2010-11-23 ok
  private int pos = 0;
  private final IReplacer replacer;
  
  /**
   * Constructor.
   * 
   * @param replacer the {@link IReplacer} instance to use
   * @param stream the stream to read the data from
   * @throws IOException on reading errors
   */
  public BufferedReplacementInputStream(IReplacer replacer, InputStream stream) throws IOException {
    this.replacer = replacer;
    if (stream != null) {
      BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
      try {
        String line = reader.readLine();
        while (line != null) {
          this.buffer.append(replace(line));
          this.buffer.append('\n');
          line = reader.readLine();
        }
      } finally {
        IOUtil.closeSilently(reader);
      }
    }
  }

  /**
   * Does the call-back to the {@link IReplacer} instance if set.
   * 
   * @param line the line to pass on to the {@link IReplacer}
   * @return the new line
   */
  private String replace(String line) {
    if (this.replacer != null) {
     return this.replacer.replace(line);
    }
    return line;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int read() throws IOException {
    if (this.pos < this.buffer.length()) {
      return this.buffer.codePointAt(this.pos++);
    } else {
      return -1;
    }
  }
}
