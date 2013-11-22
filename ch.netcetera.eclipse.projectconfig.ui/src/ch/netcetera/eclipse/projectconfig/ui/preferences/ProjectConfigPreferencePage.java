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
package ch.netcetera.eclipse.projectconfig.ui.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.netcetera.eclipse.projectconfig.ui.ProjectConfigurationUIPlugin;

/**
 * Preference Page to configure the project configuration process.
 */
public class ProjectConfigPreferencePage extends FieldEditorPreferencePage implements
    IWorkbenchPreferencePage {

  /** {@inheritDoc} */
  @Override
  public void init(IWorkbench workbench) {
    setPreferenceStore(ProjectConfigurationUIPlugin.getDefault().getPreferenceStore());
  }

  /** {@inheritDoc} */
  @Override
  protected void createFieldEditors() {
    addField(new EditableStringListFieldEditor(ProjectConfigurationUIPlugin.CONFIG_CMDFILE_URL,
        ProjectConfigurationUIPlugin.getDefault().getText("preference.page.description"),
        getFieldEditorParent(), ProjectConfigurationUIPlugin.getDefault()));
  }
}