/*******************************************************************************
 * Copyright (c) 2004 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package org.eclipse.birt.report.designer.internal.ui.dialogs;

import org.eclipse.birt.report.designer.internal.ui.util.ExceptionHandler;
import org.eclipse.birt.report.designer.nls.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;

/**
 * A field editor containing a Text field, with a additional button for getting
 * file name.
 */

public class BgImageFieldEditor extends AbstractFieldEditor
{
	private static final String[] IMAGE_TYPES = new String[]{
		".bmp", //$NON-NLS-1$
		".jpg", //$NON-NLS-1$
		".jpeg", //$NON-NLS-1$
		".jpe", //$NON-NLS-1$
		".jfif", //$NON-NLS-1$
		".gif", //$NON-NLS-1$
		".png", //$NON-NLS-1$
		".tif", //$NON-NLS-1$
		".tiff", //$NON-NLS-1$
		".ico", //$NON-NLS-1$
		".svg" //$NON-NLS-1$
};
	/**
	 * the text widget.
	 */
	private Text fText;

	/**
	 * the button widget.
	 */
	private Button fButton;

	/**
	 * @param name
	 *            property name of the field editor.
	 * @param labelText
	 *            the display label for the field editor.
	 * @param parent
	 *            parent composite
	 */
	public BgImageFieldEditor( String name, String labelText, Composite parent )
	{
		init( name, labelText );
		createControl( parent );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.FieldEditor#getNumberOfControls()
	 */
	public int getNumberOfControls( )
	{
		return 3;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.FieldEditor#doLoad()
	 */
	protected void doLoad( )
	{
		String value = getPreferenceStore( ).getString( getPreferenceName( ) );
		if ( value != null )
		{
			fText.setText( value );
		}
		else
		{
			fText.setText( "" ); //$NON-NLS-1$
		}
		setOldValue( getStringValue( ) );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.FieldEditor#doLoadDefault()
	 */
	protected void doLoadDefault( )
	{
		String value = getPreferenceStore( ).getDefaultString( getPreferenceName( ) );
		if ( value != null )
		{
			fText.setText( value );
		}
		else
		{
			fText.setText( "" ); //$NON-NLS-1$
		}
		setOldValue( getStringValue( ) );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see EditableComboFieldEditor#adjustForNumColumns(int)
	 */
	protected void adjustForNumColumns( int numColumns )
	{
		Control control = getLabelControl( );
		if ( control != null )
		{
			( (GridData) control.getLayoutData( ) ).horizontalSpan = 1;
			numColumns--;
		}
		( (GridData) getTextControl( null ).getLayoutData( ) ).horizontalSpan = 1;
		( (GridData) getTextControl( null ).getLayoutData( ) ).widthHint = 85;
		numColumns--;

		( (GridData) getButtonControl( null ).getLayoutData( ) ).horizontalSpan = numColumns;
		( (GridData) getButtonControl( null ).getLayoutData( ) ).widthHint = Math.max( getButtonControl( null ).computeSize( SWT.DEFAULT, SWT.DEFAULT ).x, 85 );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.FieldEditor#doFillIntoGrid()
	 */
	protected void doFillIntoGrid( Composite parent, int numColumns )
	{
		Control control = getLabelControl( parent );
		control.setLayoutData( new GridData( ) );

		control = getTextControl( parent );
		control.setLayoutData( new GridData( ) );

		Button button = getButtonControl( parent );
		button.setText( Messages.getString( "BgImageFieldEditor.displayname.Browse" ) ); //$NON-NLS-1$
		button.setLayoutData( new GridData( ) );
	}

	/**
	 * Lazily creates and returns the text control.
	 * 
	 * @param parent
	 *            The parent composite to hold the field editor.
	 * @return Text The text control
	 */
	public Text getTextControl( Composite parent )
	{
		if ( fText == null )
		{
			fText = new Text( parent, SWT.BORDER );
			fText.setFont( parent.getFont( ) );
			fText.addModifyListener( new ModifyListener( ) {

				public void modifyText( ModifyEvent e )
				{
					valueChanged( VALUE );
				}
			} );
		}
		return fText;
	}

	/**
	 * Lazily creates and returns the button control.
	 * 
	 * @param parent
	 *            The parent Composite contains the control.
	 * @return Button The button control
	 */
	protected Button getButtonControl( final Composite parent )
	{
		if ( fButton == null )
		{
			fButton = new Button( parent, SWT.PUSH );

			fButton.setFont( parent.getFont( ) );
			fButton.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent evt )
				{
					String ext[] = new String[]{
						"*.bmp;*.jpg;*.jpeg;*.jpe;*.jfif;*.gif;*.png;*.tif;*.tiff;*.ico;*.svg"//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					} ;
					FileDialog fd = new FileDialog( parent.getShell( ),
							SWT.OPEN );
					fd.setFilterExtensions(ext );
					// fd.setFilterNames( new String[]{
					// "SWT image" + " (gif, jpeg, png, ico, bmp)" //$NON-NLS-1$
					// //$NON-NLS-2$
					// } );


					
					String file = fd.open( );
					if ( file != null )					
					{					
					// should check extensions in Linux enviroment
						if ( checkExtensions( IMAGE_TYPES,file ) == false )
						{
							ExceptionHandler.openErrorMessageBox( Messages.getString( "EmbeddedImagesNodeProvider.FileNameError.Title" ), //$NON-NLS-1$
									Messages.getString( "EmbeddedImagesNodeProvider.FileNameError.Message" ) ); //$NON-NLS-1$
						}else
						{
							getTextControl( null ).setText( file );
							valueChanged( VALUE );
						}

					}
				}
			} );
		}
		return fButton;
	}

	/**
	 * Gets string value of the field editor.
	 * 
	 * @return the field editor 's string value.
	 */
	protected String getStringValue( )
	{
		if ( fText != null )
		{
			return fText.getText( );
		}
		return getPreferenceStore( ).getString( getPreferenceName( ) );
	}
	
	private boolean checkExtensions(String fileExt[], String fileName )
	{		
		for ( int i = 0; i < fileExt.length; i++ )
		{
			String ext = fileExt[i].substring(fileExt[i].lastIndexOf('.') );
			if ( fileName.toLowerCase( ).endsWith( ext.toLowerCase( ) ) )
			{
				return true;
			}
		}
		return false;
	}

}