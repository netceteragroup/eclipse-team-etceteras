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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import ch.netcetera.eclipse.common.text.ITextAccessor;
import ch.netcetera.eclipse.common.validator.UriInputValidator;
import ch.netcetera.eclipse.workspaceconfig.ui.preferences.ConfigurationUtil;

/**
 * The one and only page of the {@link RemotePreferencesImportWizard}.
 */
public class RemotePreferencesImportPage extends WizardPage {

  private final UriInputValidator uriValidator = new UriInputValidator(Collections.<String>emptyList(), "",
      WorkspaceConfigurationUIPlugin.getDefault());
  private Text urlText;
  private Table table;
  private ITextAccessor textAccessor;
  private TableColumn tableColumn;

  
  /**
   * Default constructor.
   */
  protected RemotePreferencesImportPage() {
    this("remotePreferencesImportPage");
  }
  
  /**
   * Constructor.
   * 
   * @param pageName the page name
   */
  protected RemotePreferencesImportPage(String pageName) {
    super(pageName);
    this.textAccessor = WorkspaceConfigurationUIPlugin.getDefault();
    setTitle(this.textAccessor.getText("import.wizard.title"));
    setDescription(this.textAccessor.getText("import.wizard.description"));
  }
  

  /** {@inheritDoc} */
  @Override
  public void createControl(Composite parent) {
    initializeDialogUnits(parent);
    Composite composite = new Composite(parent, SWT.NULL);
    composite.setLayout(new GridLayout());
    composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
    
    // first option: direct URL input
    Button directImportRadioButton = new Button(composite, SWT.RADIO);
    directImportRadioButton.setText(this.textAccessor.getText("import.wizard.fromurl"));
    directImportRadioButton.addSelectionListener(new SelectionAdapter() {
      
      /** {@inheritDoc} */
      @Override
      public void widgetSelected(SelectionEvent e) {
        super.widgetSelected(e);
        RemotePreferencesImportPage.this.table.setEnabled(false);
        RemotePreferencesImportPage.this.urlText.setEnabled(true);
        validateUrl();
      }
    });
    
    this.urlText = new Text(composite, SWT.BORDER);
    this.urlText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL));
    this.urlText.setEnabled(false);
    this.urlText.addKeyListener(new KeyAdapter() {
      
      /** {@inheritDoc} */
      @Override
      public void keyReleased(KeyEvent e) {
        super.keyReleased(e);
        validateUrl();
      }
    });
    
    // second option: list of configured URLs
    Button importConfiguredRadioButton = new Button(composite, SWT.RADIO);
    importConfiguredRadioButton.setText(this.textAccessor.getText("import.wizard.fromlist"));
    importConfiguredRadioButton.setSelection(true);
    importConfiguredRadioButton.addSelectionListener(new SelectionAdapter() {
      
      /** {@inheritDoc} */
      @Override
      public void widgetSelected(SelectionEvent e) {
        super.widgetSelected(e);
        RemotePreferencesImportPage.this.table.setEnabled(true);
        RemotePreferencesImportPage.this.urlText.setEnabled(false);
        validateTableSelection();
      }
    });
    
    this.table = new Table(composite, SWT.BORDER | SWT.CHECK);
    GridData tgd = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_BOTH);
    tgd.heightHint = 200;
    this.table.setLayoutData(tgd);
    this.table.addSelectionListener(new SelectionAdapter() {
      
      /** {@inheritDoc} */
      @Override
      public void widgetSelected(SelectionEvent e) {
        super.widgetSelected(e);
        validateTableSelection();
      }
    });
    
    tableColumn = new TableColumn(this.table, SWT.LEFT);
    table.addControlListener(new ControlAdapter() {
      @Override
      public void controlResized(ControlEvent e) {
        resiseTableColumnWidth();
      }
    });
    
    loadTableItems();
    setControl(composite);
    Dialog.applyDialogFont(composite);
    calculatePageComplete();
  }

  /**
   * Calculates the single table column's with so that it always fills the entire table width.
   * 40 pixels are subtracted for the space the checkbox and vertical scroll bar may take. This
   * value was found by testing on different platforms and might be wrong in some cases. However,
   * it's an educated guess and being off a bit does not render the UI unusable.
   */
  private void resiseTableColumnWidth() {
    tableColumn.setWidth(table.getSize().x - 40);
  }
  
  /**
   * Validates the contents of the URL text field to be a valid URL.
   */
  void validateUrl() {
    String url = RemotePreferencesImportPage.this.urlText.getText();
    String message = this.uriValidator.isValid(url);
    setErrorMessage(message);
    calculatePageComplete();
  }
  
  /**
   * Validates that at least one table item is checked.
   */
  void validateTableSelection() {
    setErrorMessage(null);
    if (getCheckedTableItemsAsString().isEmpty()) {
      setErrorMessage(this.textAccessor.getText("import.wizard.error.selectone"));
    }
    calculatePageComplete();
  }

  /**
   * Finds out whether this wizard page is in a state that allows finishing the wizard.
   */
  private void calculatePageComplete() {
    boolean urlTextOk = !this.urlText.isEnabled() || !this.urlText.getText().isEmpty();
    boolean hasError = getErrorMessage() != null;
    setPageComplete(!hasError && urlTextOk);
  }

  /**
   * Loads the URLs configured in the preferences and adds them to the table.
   */
  private void loadTableItems() {
    for (String url : ConfigurationUtil.getEpfUrls()) {
      TableItem item = new TableItem(this.table, SWT.NONE);
      item.setText(url);
      item.setChecked(true);
    }
  }
  
  /**
   * Gets the list of URLs that need to be imported when this wizard page finishes.
   * <p>
   * In case the user opted to enter an url, the list contains the entered URL as
   * sole item. In case the user opted to select from the configured URLs, all
   * checked URLs are returned.
   * </p>
   * 
   * @return the list of URLs that need to be imported
   */
  List<String> getImportUrlList() {
    if (this.urlText.isEnabled()) {
      return Collections.singletonList(this.urlText.getText().trim());
    }
    return getCheckedTableItemsAsString();
  }
  
  /**
   * Finds out whether system variable substitutions shall be performed during the import.
   * 
   * @return {@code true} if variable substitutions shall be performed and {@code false} otherwise
   */
  boolean doVariableSubstitution() {
    return this.table.isEnabled();
  }

  /**
   * Gets a list containing all URLs belonging to checked items of the table.
   * 
   * @return a list containing all URLs belonging to checked items of the table
   */
  private List<String> getCheckedTableItemsAsString() {
    List<String> checkedTableItems = new ArrayList<String>();
    for (TableItem tableItem : this.table.getItems()) {
      if (tableItem.getChecked()) {
        checkedTableItems.add(tableItem.getText());
      }
    }
    return checkedTableItems;
  }
}
