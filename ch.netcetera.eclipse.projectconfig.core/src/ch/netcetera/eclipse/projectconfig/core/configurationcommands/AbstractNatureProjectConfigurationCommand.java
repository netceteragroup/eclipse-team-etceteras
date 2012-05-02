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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;

import ch.netcetera.eclipse.common.text.ITextAccessor;

/**
 * Abstract project configuration command to handle nature operations.
 */
abstract class AbstractNatureProjectConfigurationCommand extends AbstractProjectConfigurationCommand {

  /**
   * Constructor.
   *
   * @param argumentList the arguments
   * @param textAccessor the text accessor to retrieve text resources
   * @param pluginId the plugin id used for logging
   * @param log the log
   */
  public AbstractNatureProjectConfigurationCommand(List<String> argumentList, ITextAccessor textAccessor,
      String pluginId, ILog log) {
    super(argumentList, textAccessor, pluginId, log);
  }

  /**
   * Finds out whether the project passed has the nature defined by the nature ID passed configured.
   * 
   * @param project the project to check
   * @param natureID the nature ID to check
   * @return {@code true} if the project has the nature defined by the nature ID passed configured and {@code false}
   * otherwise
   * @throws CoreException on error
   */
  protected boolean projectHasNature(IProject project, String natureID) throws CoreException {
    return project.getNature(natureID) != null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean isEnabled() {
    return getArgumentList() != null && getArgumentList().size() == 2;
  }
}
