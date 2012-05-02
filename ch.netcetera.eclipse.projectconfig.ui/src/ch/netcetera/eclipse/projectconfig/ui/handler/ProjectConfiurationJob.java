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
package ch.netcetera.eclipse.projectconfig.ui.handler;

import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import ch.netcetera.eclipse.common.text.ITextAccessor;
import ch.netcetera.eclipse.projectconfig.core.IProjectConfigurationService;
import ch.netcetera.eclipse.projectconfig.ui.ProjectConfigurationUIPlugin;

/**
 * Job that runs an Eclipse project configuration script on the seleced projects.
 */
public class ProjectConfiurationJob extends Job {

  private final List<IProject> projectList;
  private final String pluginId;
  private final String scriptUrl;
  private final ILog log;
  private final ITextAccessor textAccessor;

  /**
   * Constructor.
   *
   * @param name the human readable job name
   * @param pluginId the plug-in id
   * @param projectList the projects to run the configuration script on
   * @param scriptUrl the configuration script file URL
   * @param log the log
   * @param textAccessor the text accessor
   */
  public ProjectConfiurationJob(String name, String pluginId, List<IProject> projectList, String scriptUrl,
      ILog log, ITextAccessor textAccessor) {
    super(name);
    this.pluginId = pluginId;
    this.projectList = projectList;
    this.scriptUrl = scriptUrl;
    this.log = log;
    this.textAccessor = textAccessor;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IStatus run(IProgressMonitor monitor) {
    try {
      return runConfigurationScript();
    } catch (ExecutionException e) {
      return new Status(IStatus.ERROR, this.pluginId, e.getLocalizedMessage(), e);
    }
  }

  /**
   * Runs a configuration script on the project(s) passed.
   *
   * @throws ExecutionException if an error happens and no active shell is present
   */
  private IStatus runConfigurationScript() throws ExecutionException {
    IStatus status = Status.OK_STATUS;
    
    if (this.scriptUrl != null && this.scriptUrl.trim().length() > 0) {
      IProjectConfigurationService service = ProjectConfigurationUIPlugin.getDefault().getProjectConfigurationService();
      
      if (service != null) {
        status = service.runConfigurationScript(this.projectList, this.scriptUrl, this.textAccessor, this.pluginId,
            this.log);
        refreshProjects();
      } else {
        status = new Status(IStatus.ERROR, this.pluginId,
            "could not obtain service reference of IPreferencesImportService");
      }
    }
    return status;
  }

  /**
   * Refreshes all projects.
   */
  private void refreshProjects() {
    if (this.projectList != null) {
      for (IProject project : this.projectList) {
        try {
          project.refreshLocal(IResource.DEPTH_INFINITE, null);
        } catch (CoreException e) {
          // don't do anything if the refreshing did not work
        }
      }
    }
  }
}
