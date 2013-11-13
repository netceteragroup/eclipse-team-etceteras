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
package ch.netcetera.eclipse.projectconfig.core.configurationcommands;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import ch.netcetera.eclipse.common.io.IOUtil;
import ch.netcetera.eclipse.common.text.ITextAccessor;

/**
 * Project configuration command to handle a file download.
 */
public class DownloadProjectConfigurationCommand extends AbstractProjectConfigurationCommand {

  private static final String SUBCOMMAND_OVERWRITE = "overwrite";
  private static final String PROTOCOL_PREFIX_HTTP = "http";
  private static final String PROTOCOL_PREFIX_FILE = "file";

  /** Command name that specifies the command handled by this class. */
  public static final String COMMAND_NAME = "download";

  /**
   * Constructor.
   *
   * @param argumentList the arguments
   * @param textAccessor the text accessor to retrieve text resources
   * @param pluginId the plugin id used for logging
   * @param log the log
   */
  public DownloadProjectConfigurationCommand(List<String> argumentList,
      ITextAccessor textAccessor,
      String pluginId,
      ILog log) {
    super(argumentList, textAccessor, pluginId, log);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  IStatus executeOnProject(IProject project) {
    String fileUrl = getFileUrl();
    IPath targetFilePath = project.getLocation().append(getTargetFileName());
    File targetFile = targetFilePath.toFile();

    IStatus status = Status.OK_STATUS;

    // only start file transfer if file does not exist or will be overwritten anyways
    if (!targetFile.exists() || isOverwrite()) {
      if (fileUrl.toLowerCase().startsWith(PROTOCOL_PREFIX_HTTP)) {
        status = downloadFileFromHTTP(fileUrl, targetFile);
      } else if (fileUrl.toLowerCase().startsWith(PROTOCOL_PREFIX_FILE)) {
        status = downloadFileFromFile(fileUrl, targetFile);
      }
    }

    if (!status.isOK()) {
      getLog().log(status);
    }
    return status;
  }

  /**
   * Downloads a file from a local (file://) source.
   *
   * @param fileUrl the file url
   * @param targetFile the target file
   * @return the status
   */
  private IStatus downloadFileFromFile(String fileUrl, File targetFile) {
    IStatus status = Status.OK_STATUS;

    try {
      URI uri = new URI(fileUrl);
      if (uri.getAuthority() == null) {
        File sourceFile = new File(uri);
        if (sourceFile.canRead()) {
          OutputStream outputStream = null;
          InputStream inputStream = null;
          try {
            outputStream = new FileOutputStream(targetFile);
            inputStream = new FileInputStream(sourceFile);
            copyFile(inputStream, outputStream);
          } finally {
            IOUtil.closeSilently(outputStream);
            IOUtil.closeSilently(inputStream);
          }
        } else {
          status = createStatus(IStatus.ERROR,
              getTextAccessor().getText("error.cannot.read.local.file"));
        }
      } else {
        status = createStatus(IStatus.ERROR,
            getTextAccessor().getText("error.cannot.read.local.file"));
      }
    } catch (IOException e) {
      status = createStatus(IStatus.ERROR, e.getLocalizedMessage(), e);
    } catch (URISyntaxException e) {
      status = createStatus(IStatus.ERROR, e.getLocalizedMessage(), e);
    }
    return status;
  }

  /**
   * Downloads a file from a remote (http(s)://) source.
   *
   * @param fileUrl the file url
   * @param targetFile the target file
   * @return the status
   */
  private IStatus downloadFileFromHTTP(String fileUrl, File targetFile) {
    IStatus status = Status.OK_STATUS;

    HttpClient httpClient = new DefaultHttpClient();
    HttpGet httpGet = new HttpGet(fileUrl);

    int httpStatus;

    try {
      HttpResponse response = httpClient.execute(httpGet);
      httpStatus = response.getStatusLine().getStatusCode();
      if (httpStatus != HttpStatus.SC_OK) {
        status = createStatus(IStatus.ERROR, response.getStatusLine().getReasonPhrase());
      } else {

        OutputStream outputStream = null;
        InputStream inputStream = null;
        try {
          // write response to the file
          outputStream = new FileOutputStream(targetFile);
          inputStream = response.getEntity().getContent();
          copyFile(inputStream, outputStream);
        } finally {
          IOUtil.closeSilently(outputStream);
          IOUtil.closeSilently(inputStream);
        }
      }
    } catch (IOException e) {
      status = createStatus(IStatus.ERROR, e.getLocalizedMessage(), e);
    }
    return status;
  }

  /**
   * Copies the file from the {@link InputStream} passed to the {@link OutputStream} passed.
   *
   * @param inputStream the input stream to read from
   * @param outputStream the output stream to write to
   * @throws IOException on error
   */
  private void copyFile(InputStream inputStream, OutputStream outputStream) throws IOException {
    byte[] buffer = new byte[0x2000]; // 8k
    int len;
    while ((len = inputStream.read(buffer)) != -1) { // NOPMD pellaton 2010-11-20 ok
      outputStream.write(buffer, 0, len);
    }
  }

  /**
   * Finds out whether the command was called with the 'overwrite' directive.
   *
   * @return <code>true</code> if the target file shall be overwritten if it exist or
   * <code>false</code> if not.
   */
  private boolean isOverwrite() {
    return getArgumentList().size() == 5 && SUBCOMMAND_OVERWRITE.equals(getArgumentList().get(1));
  }

  /**
   * Gets the target file name.
   *
   * @return the target file name
   */
  private String getTargetFileName() {
    return isOverwrite() ? getArgumentList().get(4) : getArgumentList().get(3);
  }

  /**
   * Gets the url of the file to download.
   *
   * @return the url or the file to download
   */
  private String getFileUrl() {
    return isOverwrite() ? getArgumentList().get(2) : getArgumentList().get(1);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  boolean isEnabled() {
    return getArgumentList() != null
        && (getArgumentList().size() == 4 || getArgumentList().size() == 5);
  }
}
