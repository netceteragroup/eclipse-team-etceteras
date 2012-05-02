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
package ch.netcetera.eclipse.workspaceconfig.ui.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.netcetera.eclipse.workspaceconfig.ui.WorkspaceConfigurationUIPlugin;

/**
 * Preference Page to configure the workspace configuration process.
 */
public class WorkspaceConfigPreferencePage extends FieldEditorPreferencePage implements
    IWorkbenchPreferencePage {


  /** {@inheritDoc} */
  @Override
  public void init(IWorkbench workbench) {
    setPreferenceStore(WorkspaceConfigurationUIPlugin.getDefault().getPreferenceStore());
  }

  /** {@inheritDoc} */
  @Override
  protected void createFieldEditors() {
  
    addField(new EditableUrlStringListFieldEditor(WorkspaceConfigurationConstants.CONFIG_URL,
        WorkspaceConfigurationUIPlugin.getDefault().getText("preference.page.url.label"),
        getFieldEditorParent(), WorkspaceConfigurationUIPlugin.getDefault(), true));

    addField(new EditableStringListFieldEditor(WorkspaceConfigurationConstants.CONFIG_ENV_REPLACEMENT_LIST,
        WorkspaceConfigurationUIPlugin.getDefault().getText("preference.page.envreplace.list"),
        getFieldEditorParent(), WorkspaceConfigurationUIPlugin.getDefault()));
    
    addField(new BooleanFieldEditor(WorkspaceConfigurationConstants.CONFIG_STARTUP_CHECK,
        WorkspaceConfigurationUIPlugin.getDefault().getText("preference.page.check.label"),
        getFieldEditorParent()));
  }
  

}