/*
 * Copyright (c) 2010 Eclipse Team Etceteras Project and others.
 * All rights reserved.
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * - Michael Pellaton: initial implementation
 */
package ch.netcetera.eclipse.workspaceconfig.ui;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

import ch.netcetera.eclipse.workspaceconfig.core.IPreferencesImportService;
import ch.netcetera.eclipse.workspaceconfig.ui.preferences.ConfigurationUtil;

/**
 * Import Wizard to import remote preference files. The user may either enter an
 * URL pointing to an EPF file directly or he may run any of the EPF URLs configured
 * in the Eclipse preferences.
 */
public class RemotePreferencesImportWizard extends Wizard implements IImportWizard {

  private RemotePreferencesImportPage wizardPage;
  
  /** {@inheritDoc} */
  @Override
  public void init(IWorkbench workbench, IStructuredSelection selection) {
    setWindowTitle(WorkspaceConfigurationUIPlugin.getDefault().getText("import.wizard.title"));
    setDefaultPageImageDescriptor(WorkspaceConfigurationUIPlugin.getImageDescriptor(PluginImages.IMG_IMPORT_WIZBAN));
  }

  /** {@inheritDoc} */
  @Override
  public void addPages() {
    super.addPages();
    this.wizardPage = new RemotePreferencesImportPage();
    addPage(this.wizardPage);
  }

  /** {@inheritDoc} */
  @Override
  public boolean performFinish() {
    List<String> importUrlList = this.wizardPage.getImportUrlList();
    IPreferencesImportService service = WorkspaceConfigurationUIPlugin.getDefault().getPreferencesImportService();
    
    List<String> variableSubstitutionList = Collections.<String>emptyList();
    if (this.wizardPage.doVariableSubstitution()) {
      variableSubstitutionList = ConfigurationUtil.getEnvReplacements();
    }
      
    for (String string : importUrlList) {
     IStatus status = service.importConfigFile(string, variableSubstitutionList);
      if (!status.isOK()) {
        WorkspaceConfigurationUIPlugin.getDefault().getLog().log(status);
      }
    }
    return true;
  }
}
