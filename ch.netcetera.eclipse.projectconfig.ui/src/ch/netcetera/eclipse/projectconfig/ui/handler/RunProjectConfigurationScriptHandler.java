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
package ch.netcetera.eclipse.projectconfig.ui.handler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.handlers.HandlerUtil;

import ch.netcetera.eclipse.common.dialog.ComboSelectionDialog;
import ch.netcetera.eclipse.common.squasher.PreferenceListSquasher;
import ch.netcetera.eclipse.projectconfig.ui.ProjectConfigurationUIPlugin;

/**
 * Handler to run a project configuration script from the remote server on (a) project(s).
 */
public class RunProjectConfigurationScriptHandler extends AbstractHandler {

  /** {@inheritDoc} */
  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    ISelection selection = HandlerUtil.getCurrentSelection(event);
    List<IProject> projectList = getProjectsFromSelection(selection);
    if (!projectList.isEmpty()) {
      String scriptUrl = getScriptURL();

      Job job = new ProjectConfiurationJob(
          ProjectConfigurationUIPlugin.getDefault().getText("project.config.job.name"),
          ProjectConfigurationUIPlugin.PLUGIN_ID, projectList, scriptUrl,
          ProjectConfigurationUIPlugin.getDefault().getLog(), ProjectConfigurationUIPlugin.getDefault());
      job.setUser(true);
      job.schedule();
    }
    return null;
  }

  /**
   * Gets the script URL from the preferences and prompts the user a choice of more than one URL is
   * configured.
   *
   * @return the URL of the project configuration script to run
   */
  private String getScriptURL() {
    IPreferenceStore preferenceStore = ProjectConfigurationUIPlugin.getDefault().getPreferenceStore();
    List<String> urls = PreferenceListSquasher.splitListItemsToStringArray(
        preferenceStore.getString(ProjectConfigurationUIPlugin.CONFIG_CMDFILE_URL));
    String scriptUrl = "";

    // only one url configured --> no selection dialog
    if (urls.size() == 1) {
      scriptUrl = urls.get(0);

    // multiple urls configured --> display selection dialog
    } else if (urls.size() > 1) {
      ComboSelectionDialog selectionDialog = new ComboSelectionDialog(
          ProjectConfigurationUIPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(),
          ProjectConfigurationUIPlugin.getDefault().getText("apply.config.dialog.title"),
          ProjectConfigurationUIPlugin.getDefault().getText("apply.config.dialog.label"), urls, 0);
      selectionDialog.open();
      if (selectionDialog.getReturnCode() == Window.OK) {
        scriptUrl = selectionDialog.getSelectedString();
      }
    }
    return scriptUrl;
  }

  /**
   * Gets the selected projects.
   *
   * @param selection the current selection
   * @return the selected projects
   */
  private List<IProject> getProjectsFromSelection(ISelection selection) {
    List<IProject> projectList = new ArrayList<>();

    if (selection instanceof IStructuredSelection) {
      IStructuredSelection currentSelection = (IStructuredSelection) selection;
      if (!currentSelection.isEmpty()) {
        Iterator<IProject> iterator = currentSelection.iterator();

        while (iterator.hasNext()) {
          IAdaptable selectedObject = iterator.next();
          if (selectedObject instanceof IJavaProject) {
            IJavaProject javaProject = (IJavaProject) selectedObject;
            projectList.add(javaProject.getProject());
          } else if (selectedObject instanceof IProject) {
            projectList.add(((IProject) selectedObject).getProject());
          }
        }
      }
    }
    return projectList;
  }
}
