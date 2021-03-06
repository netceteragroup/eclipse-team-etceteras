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
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import ch.netcetera.eclipse.common.text.ITextAccessor;

/**
 * Project configuration command to remove empty directories (rmdir).
 */
public class RmdirProjectConfigurationCommand extends AbstractProjectConfigurationCommand {

  /** Command name that specifies the command handled by this class. */
  public static final String COMMAND_NAME = "rmdir";

  /**
   * Constructor.
   *
   * @param argumentList the arguments
   * @param textAccessor the text accessor to retrieve text resources
   * @param pluginId the plugin id used for logging
   * @param log the log
   */
  public RmdirProjectConfigurationCommand(List<String> argumentList, ITextAccessor textAccessor, String pluginId, 
      ILog log) {
    super(argumentList, textAccessor, pluginId, log);
  }

  /** 
   * {@inheritDoc} 
   */
  @Override
  IStatus executeOnProject(IProject project) {
    IStatus status = Status.OK_STATUS;
    IPath pathToDirectory = project.getLocation().append(getArgumentList().get(1));
    File dirFile = pathToDirectory.toFile();
    if (dirFile != null && dirFile.exists() && dirFile.isDirectory() && dirFile.list().length == 0) {
      try {
        if (!dirFile.delete()) {
          status = createStatus(IStatus.ERROR, getTextAccessor().getText("error.rmdir"));
        }
      } catch (SecurityException e) {
        status = createStatus(IStatus.ERROR, e.getLocalizedMessage(), e);
      }
    }
    return status;
  }

  /** 
   * {@inheritDoc} 
   */
  @Override
  boolean isEnabled() {
    return getArgumentList() != null && getArgumentList().size() == 2;
  }
}
