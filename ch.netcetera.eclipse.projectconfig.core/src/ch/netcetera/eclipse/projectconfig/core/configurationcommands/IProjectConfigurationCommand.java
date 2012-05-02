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
import org.eclipse.core.runtime.IStatus;

/**
 * Interface to a single command executed as part of a project configuration script.
 */
public interface IProjectConfigurationCommand {

  /**
   * Executes the command on all passed projects.
   * <p>
   * The status returned is set to {@link IStatus#WARNING} if the operation
   * reported not {@link IStatus#isOK()} on at least one project. Details about
   * the individual errors can be found in the error log.
   * </p>
   *
   * @param projectList the projects to apply the command to
   * @return the status of the operation
   */
  IStatus execute(List<IProject> projectList);
}