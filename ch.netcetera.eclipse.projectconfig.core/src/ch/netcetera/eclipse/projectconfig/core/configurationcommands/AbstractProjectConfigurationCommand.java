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

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import ch.netcetera.eclipse.common.text.ITextAccessor;

/**
 * Abstract superclass of all project configuration commands.
 */
public abstract class AbstractProjectConfigurationCommand implements IProjectConfigurationCommand {

  private final List<String> argumentList;
  private final ITextAccessor textAccessor;
  private final String pluginId;
  private final ILog log;

  /**
   * Constructor.
   *
   * @param argumentList the arguments
   * @param textAccessor the text accessor to retrieve text resources
   * @param pluginId the plugin id used for logging
   * @param log the log
   */
  AbstractProjectConfigurationCommand(List<String> argumentList, ITextAccessor textAccessor, String pluginId, 
      ILog log) {
    this.argumentList = argumentList;
    this.textAccessor = textAccessor;
    this.pluginId = pluginId;
    this.log = log;
  }


  /**
   * Gets the argument list.
   *
   * @return the arguments list
   */
  List<String> getArgumentList() {
    return this.argumentList;
  }

  /** 
   * {@inheritDoc} 
   */
  @Override
  public IStatus execute(List<IProject> projectList) {
    IStatus globalResult = Status.OK_STATUS;
    boolean error = false;
    if (projectList != null && !projectList.isEmpty() && isEnabled()) {
      for (IProject project : projectList) {
        IStatus result = executeOnProject(project);
        if (!result.isOK()) {
          getLog().log(result);
          error = true;
        }
      }
    }
    if (error) {
      globalResult = new Status(IStatus.WARNING, getPluginId(), getTextAccessor().getText("error.config"));
    }
    return globalResult;
  }

  /**
   * Executes the command on a single project.
   *
   * @param project the project
   * @return the status of the operation
   */
  abstract IStatus executeOnProject(IProject project);

  /**
   * Finds out whether the command is enabled. This gives the commands the
   * possibility to formally check the arguments before being executed.
   *
   * @return <code>true</code> if the command is enabled and <code>false</code> otherwise
   */
  abstract boolean isEnabled();

  /**
   * Gets the text accessor.
   *
   * @return the text accessor
   */
  ITextAccessor getTextAccessor() {
    return this.textAccessor;
  }

  /**
   * Gets the plugin id.
   *
   * @return the plugin id
   */
  String getPluginId() {
    return this.pluginId;
  }

  /**
   * Gets the log.
   *
   * @return the log
   */
  ILog getLog() {
    return this.log;
  }

  /**
   * Gets a {@link IStatus}.
   *
   * @param severity the severity
   * @param message the message
   * @param exception the exception
   * @return the status
   */
  IStatus createStatus(int severity, String message, Throwable exception) {
    return new Status(severity, this.pluginId, this.textAccessor.getText(message), exception);
  }

  /**
   * Gets a {@link IStatus}.
   *
   * @param severity the severity
   * @param message the message
   * @return the status
   */
  IStatus createStatus(int severity, String message) {
    return createStatus(severity, message, null);
  }
}
