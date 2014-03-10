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
package ch.netcetera.eclipse.projectconfig.core.internal;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.FrameworkUtil;

import ch.netcetera.eclipse.common.io.IOUtil;
import ch.netcetera.eclipse.common.text.ITextAccessor;
import ch.netcetera.eclipse.projectconfig.core.IProjectConfigurationService;
import ch.netcetera.eclipse.projectconfig.core.ProjectConfigurationScript;
import ch.netcetera.eclipse.projectconfig.core.configurationcommands.IProjectConfigurationCommand;
import ch.netcetera.eclipse.projectconfig.net.IProjectConfigurationClient;
import ch.netcetera.eclipse.projectconfig.net.IProjectConfigurationScriptData;


/**
 * Service that imports the remote workspace preferences.
 */
public class ProjectConfigurationService implements IProjectConfigurationService {

  private static final String PROTOCOL_PREFIX_FILE = "file";
  private static final String PROTOCOL_PREFIX_HTTP = "http";

  private volatile IProjectConfigurationClient client;

  /**
   * Binds the {@link IProjectConfigurationClient} service reference.
   *
   * @param client the client service reference to bind
   */
  public void bindClient(IProjectConfigurationClient client) {
    this.client = client;
  }

  /**
   * Unbinds the {@link IProjectConfigurationClient} service reference.
   *
   * @param client the client service reference to bind
   */
  public void unbindClient(@SuppressWarnings("unused") IProjectConfigurationClient client) {
    this.client = null;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public IStatus runConfigurationScript(List<IProject> projectList, String scriptUrl,
      ITextAccessor textAccessor, String pluginId, ILog log) {
    ProjectConfigurationScript script = new ProjectConfigurationScript(scriptUrl);
    IStatus status = getProjectPropertiesScript(script, textAccessor, pluginId, log);

    if (status.isOK()) {
      status = runScriptOnProjects(projectList, script, textAccessor, pluginId);
    }
    return status;
  }

  private IStatus getProjectPropertiesScript(ProjectConfigurationScript script, ITextAccessor textAccessor,
      String pluginId, ILog log) {
    IStatus status = Status.OK_STATUS;
    String commandFileUrl = script.getUrl();
    if (commandFileUrl.toLowerCase().startsWith(PROTOCOL_PREFIX_HTTP)) {
      status = getProjectConfigurationScriptFromHTTP(script, textAccessor, pluginId, log);
    } else if (commandFileUrl.toLowerCase().startsWith(PROTOCOL_PREFIX_FILE)) {
      status = getProjectConfigurationScriptFromFile(script, textAccessor, pluginId, log);
    }
    return status;
  }

  /**
   * Gets the script file from a local (file://) source and parses it's commands and metadata.
   *
   * @param script the script object to which the commands and metadata shall be appended
   * @return the status
   */
  private IStatus getProjectConfigurationScriptFromFile(ProjectConfigurationScript script,
      ITextAccessor textAccessor, String pluginId, ILog log) {
    IStatus importStatus = Status.OK_STATUS;
    String bundleSymbolicName = FrameworkUtil.getBundle(this.getClass()).getSymbolicName();
    InputStream inputStream = null;

    try {
      File sourceFile = new File(new URI(script.getUrl()));
      if (sourceFile.canRead()) {
        try {
          inputStream = new FileInputStream(sourceFile);
          ProjectConfigurationParser.parse(script, inputStream, textAccessor, pluginId, log);
        } catch (FileNotFoundException e) {
          importStatus = wrapExceptionInErrorStatus(e);
        } catch (SecurityException e) {
          importStatus = wrapExceptionInErrorStatus(e);
        } finally {
          IOUtil.closeSilently(inputStream);
        }
      } else {
        importStatus = new Status(IStatus.ERROR, bundleSymbolicName, "Could not read local file.");
      }
    } catch (IOException e) {

      importStatus = new Status(IStatus.ERROR, bundleSymbolicName, e.getLocalizedMessage(), e);
    } catch (URISyntaxException e) {
      importStatus = new Status(IStatus.ERROR, bundleSymbolicName, e.getLocalizedMessage(), e);
    }
    return importStatus;
  }

  /**
   * Gets the script file from a remote (http(s)://) source and parses it's commands and metadata.
   *
   * @param script the script object to which the commands and metadata shall be appended
   * @return the status
   */
  private IStatus getProjectConfigurationScriptFromHTTP(ProjectConfigurationScript script,
      ITextAccessor textAccessor, String pluginId, ILog log) {

    IStatus importStatus = Status.OK_STATUS;

    if (this.client != null) {
      try {
        IProjectConfigurationScriptData file = this.client.getProjectConfiguationScriptFileData(script.getUrl(),
            new NullProgressMonitor());
        ProjectConfigurationParser.parse(script, new ByteArrayInputStream(file.getData()), textAccessor, pluginId, log);
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
   * Runs the script passed on the projects passed.
   *
   * @param projectList the projects
   * @param script the script
   * @return the status
   */
  private IStatus runScriptOnProjects(List<IProject> projectList, ProjectConfigurationScript script,
      ITextAccessor textAccessor, String pluginId) {
    IStatus status = Status.OK_STATUS;
    if (script != null && script.getCommandList() != null) {
      boolean statusOK = true;
      List<IProjectConfigurationCommand> commandList = script.getCommandList();

      // run the commands of the script one after another on each projects
      for (IProjectConfigurationCommand command : commandList) {
        statusOK &= command.execute(projectList).isOK();
      }
      if (!statusOK) {
        status = new Status(IStatus.WARNING, pluginId, textAccessor.getText("error.config"));
      }
    } else {
      status = new Status(IStatus.WARNING, pluginId, textAccessor.getText("error.config"));
    }
    return status;
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