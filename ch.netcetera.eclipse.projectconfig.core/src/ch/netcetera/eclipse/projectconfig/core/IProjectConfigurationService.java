/*
 * @(#) $CVSHeader:  $
 *
 * Copyright (C) 2010 by Netcetera AG.
 * All rights reserved.
 *
 * The copyright to the computer program(s) herein is the property of
 * Netcetera AG, Switzerland.  The program(s) may be used and/or copied
 * only with the written permission of Netcetera AG or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the program(s) have been supplied.
 *
 * @(#) $Id: codetemplates.xml,v 1.5 2004/06/29 12:49:49 hagger Exp $
 */
package ch.netcetera.eclipse.projectconfig.core;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;

import ch.netcetera.eclipse.common.text.ITextAccessor;

/**
 * Interface defining services that run a project configuration script on projects.
 */
public interface IProjectConfigurationService {

  /**
   * Runs a project configuration script.
   * <p>
   * The status returned is set to {@link IStatus#WARNING} if at least one
   * command reported not {@link IStatus#isOK()}. Details about the individual
   * errors can be found in the error log.
   * </p>
   *
   * @param projectList the projects to apply the properties
   * @param scriptUrl the URL of the script
   * @param textAccessor the text accessor
   * @param pluginId the plugin id
   * @param log the log
   * @return the status
   */
  IStatus runConfigurationScript(List<IProject> projectList, String scriptUrl, ITextAccessor textAccessor,
      String pluginId, ILog log);
}
