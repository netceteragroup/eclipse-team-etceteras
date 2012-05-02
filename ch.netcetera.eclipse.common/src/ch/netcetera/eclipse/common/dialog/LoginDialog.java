/*
 * Copyright (c) 2009 the Eclipse Team Etceteras Project and others.
 * All rights reserved.
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * - Eclipse Team Etceteras - http://ete.kenai.com
 */
package ch.netcetera.eclipse.common.dialog;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ch.netcetera.eclipse.common.text.ITextAccessor;

/**
 * Dialog that asks the user for the login data (username, password).
 */
public class LoginDialog extends TitleAreaDialog implements KeyListener {

  private final ITextAccessor textAccessor;
  private final String initName;
  private final String initPassword;
  private Text nameText;
  private Text passwordText;

  // need to store the values from the widgets to obtain them after the widgets were disposed
  private String resultName;
  private String resultPassword;
  private boolean resultIsStorePassword;
  private Button storePasswordCheckBox;

  /**
   * Constructor.
   *
   * @param parentShell the parent shell
   * @param textAccessor the text accessor
   * @param initName the initial name
   * @param initPassword the initial password
   */
  public LoginDialog(Shell parentShell, ITextAccessor textAccessor, String initName, String initPassword) {
    super(parentShell);
    this.textAccessor = textAccessor;
    this.initName = initName;
    this.initPassword = initPassword;
    setHelpAvailable(false);
    setBlockOnOpen(true);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void create() {
    super.create();
    setTitle(this.textAccessor.getText("login.dialog.message.title"));
    setMessage(this.textAccessor.getText("login.dialog.message.text"));
    keyPressed(null);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(Shell shell) {
    super.configureShell(shell);
    shell.setText(this.textAccessor.getText("login.dialog.title"));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createDialogArea(Composite parent) {
    Composite composite = new Composite(parent, SWT.NONE);
    GridLayout layout = new GridLayout();
    layout.marginHeight = 10;
    layout.marginWidth = 15;
    layout.numColumns = 2;
    composite.setLayout(layout);
    composite.setLayoutData(new GridData(GridData.FILL_BOTH));
    composite.setFont(parent.getFont());

    Label nameLabel = new Label(composite, SWT.LEFT);
    nameLabel.setText(this.textAccessor.getText("login.dialog.username"));

    this.nameText = new Text(composite, SWT.BORDER);
    GridData nameTextGridData = new GridData(SWT.LEFT, SWT.TOP, true, false);
    nameTextGridData.minimumWidth = 300;
    this.nameText.setLayoutData(nameTextGridData);
    this.nameText.addKeyListener(this);
    this.nameText.setText(this.initName);

    Label passwordLabel = new Label(composite, SWT.LEFT);
    passwordLabel.setText(this.textAccessor.getText("login.dialog.password"));

    this.passwordText = new Text(composite, SWT.PASSWORD | SWT.BORDER);
    GridData passwordTextGridData = new GridData(SWT.LEFT, SWT.TOP, true, false);
    passwordTextGridData.minimumWidth = 300;
    this.passwordText.setLayoutData(passwordTextGridData);
    this.passwordText.addKeyListener(this);
    this.passwordText.setText(this.initPassword);

    new Label(composite, SWT.LEFT);
    this.storePasswordCheckBox = new Button(composite, SWT.CHECK);
    this.storePasswordCheckBox.setText(this.textAccessor.getText("login.dialog.storepassword"));

    return composite;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void keyPressed(KeyEvent e) {
    Button okButton = getButton(IDialogConstants.OK_ID);
    if (okButton != null) {
      okButton.setEnabled(this.nameText.getText().trim().length() > 0 
          && this.passwordText.getText().trim().length() > 0);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void keyReleased(KeyEvent e) {
    keyPressed(e);
  }

  /**
   * Gets the user name entered in the dialog.
   *
   * @return the user name
   */
  public String getUsername() {
    return this.resultName;
  }

  /**
   * Gets the password entered in the dialog.
   *
   * @return the password
   */
  public String getPassword() {
    return this.resultPassword;
  }

  /**
   * Gets the status of the checkbox to store the password.
   *
   * @return the store password checkbox state
   */
  public boolean isStorePassword() {
    return this.resultIsStorePassword;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    this.resultName = this.nameText.getText();
    this.resultPassword = this.passwordText.getText();
    this.resultIsStorePassword = this.storePasswordCheckBox.getSelection();
    super.okPressed();
  }
}