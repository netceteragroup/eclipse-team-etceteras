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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import ch.netcetera.eclipse.common.text.ITextAccessor;

/**
 * Project configuration command to remove natures from a project.
 */
public class RemoveNatureProjectConfigurationCommand extends AbstractNatureProjectConfigurationCommand {

  /** Command name that specifies the command handled by this class. */
  public static final String COMMAND_NAME = "removenature";

  /**
   * Constructor.
   *
   * @param argumentList the arguments
   * @param textAccessor the text accessor to retrieve text resources
   * @param pluginId the plugin id used for logging
   * @param log the log
   */
  public RemoveNatureProjectConfigurationCommand(List<String> argumentList, ITextAccessor textAccessor, String pluginId,
      ILog log) {
    super(argumentList, textAccessor, pluginId, log);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  IStatus executeOnProject(IProject project) {
    IStatus status = Status.OK_STATUS;

    try {
      String removeNatureID = getArgumentList().get(1);
      if (!projectHasNature(project, removeNatureID)) {
        return status;
      }

      IProjectDescription projectDescription = project.getDescription();
      List<String> natureIDs = new ArrayList<>(Arrays.asList(projectDescription.getNatureIds()));
      natureIDs.remove(removeNatureID);
      projectDescription.setNatureIds(natureIDs.toArray(new String[natureIDs.size()]));
      project.setDescription(projectDescription, null);
    } catch (CoreException e) {
      status = createStatus(IStatus.ERROR, e.getLocalizedMessage(), e);
    }
    return status;
  }
}
