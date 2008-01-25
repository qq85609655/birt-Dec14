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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.birt.core.data.DataTypeUtil;
import org.eclipse.birt.core.format.DateFormatter;
import org.eclipse.birt.report.designer.internal.ui.util.UIUtil;
import org.eclipse.birt.report.designer.nls.Messages;
import org.eclipse.birt.report.designer.ui.views.attributes.providers.ChoiceSetFactory;
import org.eclipse.birt.report.designer.util.FormatDateTimePattern;
import org.eclipse.birt.report.model.api.StyleHandle;
import org.eclipse.birt.report.model.api.elements.DesignChoiceConstants;
import org.eclipse.birt.report.model.api.elements.structures.DateTimeFormatValue;
import org.eclipse.birt.report.model.api.util.StringUtil;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.ibm.icu.util.ULocale;

/**
 * Format date time page for formatting date and time.
 */

public class FormatDateTimePage extends Composite implements IFormatPage
{

	private static final String LABEL_FORMAT_DATE_TIME_PAGE = Messages.getString( "FormatDateTimePage.label.format.page" ); //$NON-NLS-1$
	private static final String LABEL_GENERAL_PREVIEW_GROUP = Messages.getString( "FormatDateTimePage.label.general.preview.group" ); //$NON-NLS-1$
	private static final String LABEL_CUSTOM_SETTINGS = Messages.getString( "FormatDateTimePage.label.custom.settings" ); //$NON-NLS-1$
	private static final String LABEL_CUSTOM_SETTINGS_LABEL = Messages.getString( "FormatDateTimePage.label.custom.settings.label" ); //$NON-NLS-1$
	private static final String LABEL_PREVIEW_DATETIME = Messages.getString( "FormatDateTimePage.label.preview.dateTime" ); //$NON-NLS-1$
	private static final String LABEL_FORMAT_CODE = Messages.getString( "FormatDateTimePage.label.format.code" ); //$NON-NLS-1$
	private static final String LABEL_PREVIEW_GROUP = Messages.getString( "FormatDateTimePage.label.preview.group" ); //$NON-NLS-1$
	private static final String LABEL_PREVIEW_LABEL = Messages.getString( "FormatDateTimePage.label.preview.label" ); //$NON-NLS-1$
	private static final String LABEL_TABLE_COLUMN_EXAMPLE_FORMAT_NAME = Messages.getString( "FormatDateTimePage.label.table.column.format.name" ); //$NON-NLS-1$
	private static final String LABEL_TABLE_COLUMN_EXAMPLE_FORMAT_RESULT = Messages.getString( "FormatDateTimePage.label.table.column.format.result" ); //$NON-NLS-1$
	private static final String LABEL_TABLE_COLUMN_EXAMPLE_FORMAT_CODE = Messages.getString( "FormatDateTimePage.label.table.column.format.code" ); //$NON-NLS-1$

	private static final String ENTER_DATE_TIME_GUIDE_TEXT = Messages.getString( "FormatDateTimePage.label.guide.text" ); //$NON-NLS-1$

	private static final String PREVIEW_TEXT_INVALID_DATETIME_TO_PREVIEW = Messages.getString( "FormatDateTimePage.preview.invalid.dateTime" ); //$NON-NLS-1$
	private static final String PREVIEW_TEXT_INVALID_FORMAT_CODE = Messages.getString( "FormatDateTimePage.preview.invalid.formatCode" ); //$NON-NLS-1$

	private String pattern = null;
	private String category = null;
	private String oldCategory = null;
	private String oldPattern = null;

	private HashMap categoryPageMaps;

	private static String[][] choiceArray = null;
	private static String[] formatTypes = null;

	private static final int FORMAT_CODE_INDEX = 2;
	private static final int DEFAULT_CATEGORY_CONTAINER_WIDTH = 220;

	private int pageAlignment;

	private Combo typeChoicer;
	private Composite infoComp;
	private Composite formatCodeComp;

	private Composite generalPage;
	private Composite customPage;

	private Composite generalFormatCodePage;
	private Composite customFormatCodePage;

	private Label generalPreviewLabel, cusPreviewLabel;
	private Label guideLabel;
	private Text previewTextBox;
	private Text formatCode;

	private Table table;

	private boolean hasLoaded = false;

	private String previewText = null;

	private boolean isDirty = false;

	/**
	 * Listener, or <code>null</code> if none
	 */
	private java.util.List listeners = new ArrayList( );

	private Date defaultDate = new Date( );

	private String defaultDateTime = new DateFormatter( DateFormatter.DATETIME_UNFORMATTED ).format( defaultDate ); //$NON-NLS-1$

	private FormatDateTimeAdapter formatAdapter;

	/**
	 * Constructs a page for formatting date time, default aligns the page
	 * virtically.
	 * 
	 * @param parent
	 *            The container
	 * @param style
	 *            The style of the page
	 */

	public FormatDateTimePage( Composite parent, int type, int style )
	{
		this( parent, type, style, PAGE_ALIGN_VIRTICAL );
	}

	/**
	 * Constructs a page for formatting date time.
	 * 
	 * @param parent
	 *            The container
	 * @param style
	 *            The style of the page
	 * @param pageAlignment
	 *            Aligns the page virtically(PAGE_ALIGN_VIRTICAL) or
	 *            horizontally(PAGE_ALIGN_HORIZONTAL).
	 */

	public FormatDateTimePage( Composite parent, int type, int style,
			int pageAlignment )
	{
		super( parent, style );
		this.pageAlignment = pageAlignment;
		formatAdapter = new FormatDateTimeAdapter( type );
		createContents( pageAlignment );
	}

	/**
	 * Creates the contents of the page.
	 * 
	 */

	protected void createContents( int pageAlignment2 )
	{
		initFormatTypes( );

		if ( pageAlignment == PAGE_ALIGN_HORIZONTAL )
		{
			createContentsHorizontally( );
		}
		else
		{
			createContentsVirtically( );
		}

	}

	private void initFormatTypes( )
	{
		choiceArray = formatAdapter.getFormatTypeChoiceSet( );
		formatTypes = formatAdapter.getFormatTypes( );
	}

	protected void createContentsVirtically( )
	{
		setLayout( UIUtil.createGridLayoutWithoutMargin( ) );

		Composite topContainer = new Composite( this, SWT.NONE );
		topContainer.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
		topContainer.setLayout( new GridLayout( 2, false ) );

		new Label( topContainer, SWT.NONE ).setText( LABEL_FORMAT_DATE_TIME_PAGE );
		typeChoicer = new Combo( topContainer, SWT.READ_ONLY );
		typeChoicer.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
		typeChoicer.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				reLayoutSubPages( );

				updatePreview( );
				notifyFormatChange( );
			}
		} );
		typeChoicer.setItems( formatTypes );
		typeChoicer.select( 0 );

		infoComp = new Composite( this, SWT.NONE );
		infoComp.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
		infoComp.setLayout( new StackLayout( ) );

		createCategoryPages( infoComp );

		setInput( null, null );
		setPreviewText( defaultDateTime );
	}

	protected void createContentsHorizontally( )
	{
		setLayout( UIUtil.createGridLayoutWithoutMargin( 2, false ) );

		// create format type choicer
		Composite container = new Composite( this, SWT.NONE );
		GridData data = new GridData( );
		data.widthHint = DEFAULT_CATEGORY_CONTAINER_WIDTH;
		container.setLayoutData( data );
		container.setLayout( new GridLayout( 1, false ) );

		new Label( container, SWT.NONE ).setText( LABEL_FORMAT_DATE_TIME_PAGE ); //$NON-NLS-1$
		typeChoicer = new Combo( container, SWT.READ_ONLY );
		typeChoicer.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
		typeChoicer.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				reLayoutSubPages( );

				updatePreview( );
				notifyFormatChange( );
			}

		} );
		typeChoicer.setItems( formatTypes );

		// create the right part setting pane
		infoComp = new Composite( this, SWT.NONE );
		data = new GridData( GridData.FILL_BOTH );
		data.verticalSpan = 2;
		infoComp.setLayoutData( data );
		infoComp.setLayout( new StackLayout( ) );

		createCategoryPages( infoComp );

		// create left bottom part format code pane
		formatCodeComp = new Composite( this, SWT.NONE );
		data = new GridData( GridData.FILL_VERTICAL );
		data.widthHint = DEFAULT_CATEGORY_CONTAINER_WIDTH;
		formatCodeComp.setLayoutData( data );
		formatCodeComp.setLayout( new StackLayout( ) );

		createFormatCodePages( formatCodeComp );

		setInput( null, null );
		setPreviewText( defaultDateTime );
	}

	/**
	 * Creates info panes for each format type choicer, adds them into paneMap
	 * for after getting.
	 * 
	 * @param parent
	 *            Parent contains these info panes.
	 */

	private void createCategoryPages( Composite parent )
	{
		categoryPageMaps = new HashMap( );

		categoryPageMaps.put( "General", getGeneralPage( parent ) ); //$NON-NLS-1$

		categoryPageMaps.put( "Custom", getCustomPage( parent ) ); //$NON-NLS-1$
	}

	private Object getPagebyCategory( String category )
	{
		if ( category.equals( formatAdapter.getCustomCategoryName( ) ) )
		{
			return categoryPageMaps.get( "Custom" ); //$NON-NLS-1$
		}
		return categoryPageMaps.get( "General" ); //$NON-NLS-1$
	}

	private void createFormatCodePages( Composite parent )
	{
		getHorizonGeneralFormatCodePage( parent );

		getHorizonCustomFormatCodePage( parent );
	}

	/**
	 * Gets the index of given category.
	 */

	private int getIndexOfCategory( String category )
	{
		if ( choiceArray != null && choiceArray.length > 0 )
		{
			for ( int i = 0; i < choiceArray.length; i++ )
			{
				if ( choiceArray[i][1].equals( category ) )
				{
					return i;
				}
			}
		}
		return 0;
	}

	/**
	 * Gets the corresponding category for given display name.
	 */

	private String getCategory4UIDisplayName( String displayName )
	{
		if ( choiceArray != null && choiceArray.length > 0 )
		{
			for ( int i = 0; i < choiceArray.length; i++ )
			{
				if ( formatTypes[i].equals( displayName ) )
				{
					return choiceArray[i][1];
				}
			}
		}
		return displayName;
	}

	/**
	 * Gets the corresponding internal display name given the category.
	 * 
	 * @param category
	 * @return
	 */

	private String getDisplayName4Category( String category )
	{
		return ChoiceSetFactory.getStructDisplayName( DateTimeFormatValue.FORMAT_VALUE_STRUCT,
				DateTimeFormatValue.CATEGORY_MEMBER,
				category );
	}

	private void fireFormatChanged( String newCategory, String newPattern )
	{
		if ( listeners.isEmpty( ) )
		{
			return;
		}
		FormatChangeEvent event = new FormatChangeEvent( this,
				StyleHandle.DATE_TIME_FORMAT_PROP,
				newCategory,
				newPattern );
		for ( Iterator iter = listeners.iterator( ); iter.hasNext( ); )
		{
			Object listener = iter.next( );
			if ( listener instanceof IFormatChangeListener )
			{
				( (IFormatChangeListener) listener ).formatChange( event );
			}
		}
	}

	private void notifyFormatChange( )
	{
		if ( hasLoaded )
		{
			fireFormatChanged( getCategory( ), getPattern( ) );
		}
	}

	/**
	 * Adds format change listener to the litener list of this format page.
	 * 
	 * @param listener
	 *            The Format change listener to add.
	 */

	public void addFormatChangeListener( IFormatChangeListener listener )
	{
		if ( !listeners.contains( listener ) )
		{
			listeners.add( listener );
		}
	}

	/**
	 * Sets input of the page.
	 * 
	 * @param formatString
	 *            The input format string.
	 * @author Liu sanyong: -----> for parameter dialog use.
	 */

	public void setInput( String formatString )
	{
		if ( formatString == null )
		{
			setInput( null, null );
			return;
		}
		String fmtStr = formatString;
		int pos = fmtStr.indexOf( ":" ); //$NON-NLS-1$
		if ( StringUtil.isBlank( fmtStr ) )
		{
			setInput( null, null );
			return;
		}
		else if ( pos == -1 )
		{
			setInput( fmtStr, fmtStr );
			return;
		}
		String category = fmtStr.substring( 0, pos );
		String patternStr = fmtStr.substring( pos + 1 );

		setInput( category, patternStr );
		return;
	}

	/**
	 * Sets input of the page.
	 * 
	 * @param category
	 *            The category of the format string.
	 * @param patternStr
	 *            The pattern of the format string.
	 */

	public void setInput( String categoryStr, String patternStr )
	{
		hasLoaded = false;

		initiatePageLayout( categoryStr, patternStr );
		reLayoutSubPages( );
		updatePreview( );

		// set initail.
		oldCategory = categoryStr;
		oldPattern = patternStr;

		hasLoaded = true;
		return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.designer.internal.ui.dialogs.IFormatPage#setPreviewText(java.lang.String)
	 */

	public void setPreviewText( String text )
	{
		setDefaultPreviewText( text );
		updatePreview( );
		return;
	}

	/**
	 * Returns the patternStr from the page.
	 */

	public String getPattern( )
	{
		return pattern;
	}

	/**
	 * Returns the category from the page.
	 */

	public String getCategory( )
	{
		return category;
	}

	/**
	 * Returns the formatString from the page.
	 */

	public String getFormatString( )
	{
		if ( category == null && pattern == null )
		{
			return formatAdapter.getUnformattedCategoryDisplayName( );
		}
		if ( category == null )
		{
			category = ""; //$NON-NLS-1$
		}
		if ( pattern == null )
		{
			pattern = ""; //$NON-NLS-1$
		}
		if ( category.equals( pattern ) )
		{
			return category;
		}
		return category + ":" + pattern; //$NON-NLS-1$
	}

	/**
	 * Determines the format string is modified or not from the page.
	 * 
	 * @return true if the format string is modified.
	 */

	public boolean isFormatModified( )
	{
		String c = getCategory( );
		String p = getPattern( );
		if ( oldCategory == null )
		{
			if ( c != null )
			{
				return true;
			}
		}
		else if ( !oldCategory.equals( c ) )
		{
			return true;
		}
		if ( oldPattern == null )
		{
			if ( p != null )
			{
				return true;
			}
		}
		else if ( !oldPattern.equals( p ) )
		{
			return true;
		}
		return false;
	}

	/**
	 * Returns whether the page is modified.
	 * 
	 * @return Returns the isDirty.
	 */

	public boolean isDirty( )
	{
		return isDirty;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.widgets.Control#setEnabled(boolean)
	 */

	public void setEnabled( boolean enabled )
	{
		super.setEnabled( enabled );
		setControlsEnabeld( enabled );
	}

	private void setDefaultPreviewText( String text )
	{
		if ( text == null || StringUtil.isBlank( text ) )
		{
			previewText = null;
		}
		else
		{
			previewText = text;
		}
		return;
	}

	/**
	 * @return Returns the previewText.
	 */

	private String getPreviewText( )
	{
		return previewText;
	}

	/**
	 * Sets the pattern string for this preference.
	 * 
	 * @param pattern
	 *            The pattern to set.
	 */

	private void setPattern( String pattern )
	{
		this.pattern = pattern;
	}

	/**
	 * @param datetiem_format_type_general_date
	 */

	private void setCategory( String category )
	{
		this.category = category;
	}

	/**
	 * Marks the dirty marker of the page.
	 * 
	 * @param dirty
	 */

	private void markDirty( boolean dirty )
	{
		isDirty = dirty;
	}

	private String validatedFmtStr( String fmtStr )
	{
		String text = fmtStr;
		if ( text == null )
		{
			text = PREVIEW_TEXT_INVALID_FORMAT_CODE;
		}
		return text;
	}

	/**
	 * Updates the format Pattern String, and Preview.
	 */

	private void updatePreview( )
	{
		markDirty( hasLoaded );

		String category = getCategory4UIDisplayName( typeChoicer.getText( ) );
		setCategory( category );

		Date sampleDateTime = defaultDate;
		if ( getPreviewText( ) != null
				&& !getPreviewText( ).equals( defaultDateTime ) )
		{
			try
			{
				sampleDateTime = new Date( getPreviewText( ) );
			}
			catch ( Exception e )
			{
				// do nothing, leave sampleDate to be defaultDate.
			}
		}

		if ( formatAdapter.getCustomCategoryName( ).equals( category ) )
		{
			String pattern = formatCode.getText( );
			String fmtStr;
			String text = previewTextBox.getText( );
			if ( StringUtil.isBlank( text ) || defaultDateTime.equals( text ) )
			{
				fmtStr = new DateFormatter( pattern, ULocale.getDefault( ) ).format( sampleDateTime );
			}
			else
			{
				try
				{
					fmtStr = new DateFormatter( pattern ).format( DataTypeUtil.toDate( text ) );
				}
				catch ( Exception e )
				{
					fmtStr = PREVIEW_TEXT_INVALID_DATETIME_TO_PREVIEW;
				}
			}
			cusPreviewLabel.setText( validatedFmtStr( fmtStr ) );
			setPattern( pattern );
		}
		else
		{
			String pattern = null;
			if ( !formatAdapter.getUnformattedCategoryDisplayName( )
					.equals( category ) )
			{
				pattern = FormatDateTimePattern.getPatternForCategory( category );
				setPattern( pattern );
			}
			else
			{
				pattern = formatAdapter.getUnformattedCategoryName( );
				setPattern( null );
			}
			String fmtStr = new DateFormatter( pattern ).format( sampleDateTime );
			generalPreviewLabel.setText( validatedFmtStr( fmtStr ) );
		}
	}

	private void initiatePageLayout( String categoryStr, String patternStr )
	{
		if ( categoryStr == null )
		{
			typeChoicer.select( 0 );
		}
		else
		{
			if ( categoryStr.equals( formatAdapter.getCustomCategoryName( ) ) )
			{
				formatCode.setText( patternStr == null ? "" : patternStr ); //$NON-NLS-1$
			}
			typeChoicer.select( getIndexOfCategory( categoryStr ) );
		}
	}

	/**
	 * Re layouts sub pages according to the selected format type.
	 */

	private void reLayoutSubPages( )
	{
		String category = getCategory4UIDisplayName( typeChoicer.getText( ) );

		Control control = (Control) getPagebyCategory( category );

		( (StackLayout) infoComp.getLayout( ) ).topControl = control;

		infoComp.layout( );

		if ( formatCodeComp != null )
		{
			if ( category.equals( DesignChoiceConstants.NUMBER_FORMAT_TYPE_CUSTOM ) )
			{
				( (StackLayout) formatCodeComp.getLayout( ) ).topControl = getHorizonCustomFormatCodePage( formatCodeComp );
			}
			else
			{
				( (StackLayout) formatCodeComp.getLayout( ) ).topControl = getHorizonGeneralFormatCodePage( formatCodeComp );
			}
			formatCodeComp.layout( );
		}
	}

	/**
	 * Lazily creates the general page and returns it.
	 * 
	 * @param parent
	 *            Parent contains this page.
	 * @return The general page.
	 */

	private Composite getGeneralPage( Composite parent )
	{
		if ( generalPage == null )
		{
			generalPage = new Composite( parent, SWT.NULL );
			GridLayout layout = new GridLayout( 1, false );
			layout.marginHeight = 0;
			generalPage.setLayout( layout );

			generalPreviewLabel = createGeneralPreviewPart( generalPage );
		}
		return generalPage;
	}

	/**
	 * Lazily creates the custom page and returns it.
	 * 
	 * @param parent
	 *            Parent contains this page.
	 * @return The custom page.
	 */

	private Composite getCustomPage( Composite parent )
	{
		if ( customPage == null )
		{
			customPage = new Composite( parent, SWT.NULL );
			customPage.setLayout( createGridLayout4Page( ) );

			createCustomSettingsPart( customPage );

			if ( pageAlignment == PAGE_ALIGN_VIRTICAL )
			{
				Composite container = new Composite( customPage, SWT.NONE );
				container.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
				container.setLayout( new GridLayout( 2, false ) );

				new Label( container, SWT.NULL ).setText( LABEL_FORMAT_CODE );
				formatCode = new Text( container, SWT.SINGLE | SWT.BORDER );
				formatCode.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
				formatCode.addModifyListener( new ModifyListener( ) {

					public void modifyText( ModifyEvent e )
					{
						if ( hasLoaded )
						{
							updatePreview( );
						}
					}
				} );
				formatCode.addFocusListener( new FocusListener( ) {

					public void focusLost( FocusEvent e )
					{
						notifyFormatChange( );
					}

					public void focusGained( FocusEvent e )
					{
					}
				} );
			}

			createCustomPreviewPart( customPage );
		}
		return customPage;
	}

	private Composite getHorizonGeneralFormatCodePage( Composite parent )
	{
		if ( generalFormatCodePage == null )
		{
			generalFormatCodePage = new Composite( parent, SWT.NULL );
			GridLayout layout = new GridLayout( 1, false );
			layout.marginHeight = 1;
			generalFormatCodePage.setLayout( layout );

			Label l = new Label( generalFormatCodePage, SWT.SEPARATOR
					| SWT.HORIZONTAL );
			l.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
		}
		return generalFormatCodePage;
	}

	private Composite getHorizonCustomFormatCodePage( Composite parent )
	{
		if ( customFormatCodePage == null )
		{
			customFormatCodePage = new Composite( parent, SWT.NONE );
			GridLayout layout = new GridLayout( 1, false );
			layout.marginHeight = 1;
			customFormatCodePage.setLayout( layout );

			Label l = new Label( customFormatCodePage, SWT.SEPARATOR
					| SWT.HORIZONTAL );
			l.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );

			Composite container = new Composite( customFormatCodePage, SWT.NONE );
			container.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
			container.setLayout( new GridLayout( 2, false ) );

			new Label( container, SWT.NULL ).setText( LABEL_FORMAT_CODE ); //$NON-NLS-1$
			formatCode = new Text( container, SWT.SINGLE | SWT.BORDER );
			formatCode.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
			formatCode.addModifyListener( new ModifyListener( ) {

				public void modifyText( ModifyEvent e )
				{
					if ( hasLoaded )
					{
						updatePreview( );
					}
				}
			} );
			formatCode.addFocusListener( new FocusListener( ) {

				public void focusLost( FocusEvent e )
				{
					notifyFormatChange( );
				}

				public void focusGained( FocusEvent e )
				{
				}
			} );
		}
		return customFormatCodePage;
	}

	/**
	 * Creates preview part for general page.
	 */

	private Label createGeneralPreviewPart( Composite parent )
	{
		Group group = new Group( parent, SWT.NONE );
		group.setText( LABEL_GENERAL_PREVIEW_GROUP );
		GridData data;
		if ( pageAlignment == PAGE_ALIGN_HORIZONTAL )
		{
			data = new GridData( GridData.FILL_BOTH );
		}
		else
		{
			data = new GridData( GridData.FILL_HORIZONTAL );
		}
		group.setLayoutData( data );
		group.setLayout( new GridLayout( 1, false ) );

		Label previewLabel = new Label( group, SWT.CENTER
				| SWT.HORIZONTAL
				| SWT.VERTICAL );
		previewLabel.setLayoutData( new GridData( GridData.FILL_BOTH ) );

		return previewLabel;
	}

	private void createCustomSettingsPart( Composite parent )
	{
		Group group = new Group( parent, SWT.NONE );
		group.setText( LABEL_CUSTOM_SETTINGS ); //$NON-NLS-1$
		group.setLayoutData( createGridData4Part( ) );
		group.setLayout( new GridLayout( 2, false ) );

		Label label = new Label( group, SWT.NONE );
		label.setText( LABEL_CUSTOM_SETTINGS_LABEL ); //$NON-NLS-1$
		GridData data = new GridData( );
		data.horizontalSpan = 2;
		label.setLayoutData( data );

		createTable( group );
	}

	private void createCustomPreviewPart( Composite parent )
	{
		Group group = new Group( parent, SWT.NONE );
		group.setText( LABEL_PREVIEW_GROUP ); //$NON-NLS-1$
		if ( pageAlignment == PAGE_ALIGN_HORIZONTAL )
		{
			group.setLayoutData( new GridData( GridData.FILL_BOTH ) );
			group.setLayout( new GridLayout( 1, false ) );
		}
		else
		{
			group.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
			group.setLayout( new GridLayout( 2, false ) );
		}

		new Label( group, SWT.NONE ).setText( LABEL_PREVIEW_DATETIME ); //$NON-NLS-1$
		previewTextBox = new Text( group, SWT.SINGLE | SWT.BORDER );
		previewTextBox.setText( defaultDateTime );
		GridData data = new GridData( GridData.FILL_HORIZONTAL );
		if ( pageAlignment == PAGE_ALIGN_HORIZONTAL )
		{
			data.horizontalIndent = 10;
		}
		previewTextBox.setLayoutData( data );
		previewTextBox.addModifyListener( new ModifyListener( ) {

			public void modifyText( ModifyEvent e )
			{
				setDefaultPreviewText( previewTextBox.getText( ) );
				if ( hasLoaded )
				{
					updatePreview( );
				}
				if ( StringUtil.isBlank( previewTextBox.getText( ) ) )
				{
					guideLabel.setText( "" ); //$NON-NLS-1$
				}
				else
				{
					guideLabel.setText( ENTER_DATE_TIME_GUIDE_TEXT );
				}
			}
		} );

		if ( pageAlignment == PAGE_ALIGN_VIRTICAL )
		{
			new Label( group, SWT.NONE );
		}
		guideLabel = new Label( group, SWT.NONE );
		guideLabel.setText( "" ); //$NON-NLS-1$
		Font font = JFaceResources.getDialogFont( );
		FontData fData = font.getFontData( )[0];
		fData.setHeight( fData.getHeight( ) - 1 );
		guideLabel.setFont( new Font( Display.getCurrent( ), fData ) );

		data = new GridData( GridData.FILL_HORIZONTAL );
		data.horizontalIndent = 10;
		guideLabel.setLayoutData( data );

		Label label = new Label( group, SWT.NONE );
		label.setText( LABEL_PREVIEW_LABEL ); //$NON-NLS-1$
		label.setLayoutData( new GridData( ) );

		cusPreviewLabel = new Label( group, SWT.CENTER
				| SWT.HORIZONTAL
				| SWT.VIRTUAL );
		cusPreviewLabel.setText( "" ); //$NON-NLS-1$
		data = new GridData( GridData.FILL_BOTH );
		data.horizontalSpan = 1;
		cusPreviewLabel.setLayoutData( data );
	}

	/**
	 * Creates the table in custom page.
	 * 
	 * @param parent
	 *            Parent contains the table.
	 */

	private void createTable( Composite parent )
	{
		table = new Table( parent, SWT.FULL_SELECTION
				| SWT.HIDE_SELECTION
				| SWT.BORDER
				| SWT.V_SCROLL
				| SWT.H_SCROLL );
		GridData data = new GridData( GridData.FILL_BOTH );
		data.horizontalSpan = 2;
		if ( pageAlignment == PAGE_ALIGN_HORIZONTAL )
		{
			data.widthHint = 240;
		}
		table.setLayoutData( data );

		table.setLinesVisible( true );
		table.setHeaderVisible( true );

		table.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				formatCode.setText( ( (TableItem) e.item ).getText( FORMAT_CODE_INDEX ) );
				updatePreview( );
				notifyFormatChange( );
			}
		} );
		TableColumn tableColumValue = new TableColumn( table, SWT.NONE );
		tableColumValue.setText( LABEL_TABLE_COLUMN_EXAMPLE_FORMAT_NAME );
		tableColumValue.setWidth( 90 );
		tableColumValue.setResizable( true );

		TableColumn tableColumnDisplay = new TableColumn( table, SWT.NONE );
		tableColumnDisplay.setText( LABEL_TABLE_COLUMN_EXAMPLE_FORMAT_RESULT );
		tableColumnDisplay.setWidth( 120 );
		tableColumnDisplay.setResizable( true );

		TableColumn tableColumnFormatCode = new TableColumn( table, SWT.NONE );
		tableColumnFormatCode.setText( LABEL_TABLE_COLUMN_EXAMPLE_FORMAT_CODE );
		tableColumnFormatCode.setWidth( 120 );
		tableColumnFormatCode.setResizable( true );

		String[] simpleFormatTypes = formatAdapter.getSimpleDateTimeFormatTypes( );
		for ( int i = 0; i < simpleFormatTypes.length; i++ )
		{
			new TableItem( table, SWT.NONE ).setText( new String[]{
					getDisplayName4Category( simpleFormatTypes[i] ),
					new DateFormatter( FormatDateTimePattern.getPatternForCategory( simpleFormatTypes[i] ) ).format( defaultDate ),
					new DateFormatter( FormatDateTimePattern.getPatternForCategory( simpleFormatTypes[i] ) ).getFormatCode( )
			} );
		}
		String[] customPatternCategorys = FormatDateTimePattern.getCustormPatternCategorys( );
		for ( int i = 0; i < customPatternCategorys.length; i++ )
		{
			new TableItem( table, SWT.NONE ).setText( new String[]{
					FormatDateTimePattern.getDisplayName4CustomCategory( customPatternCategorys[i] ),
					new DateFormatter( FormatDateTimePattern.getCustormFormatPattern( customPatternCategorys[i] ) ).format( defaultDate ),
					FormatDateTimePattern.getCustormFormatPattern( customPatternCategorys[i] )
			} );
		}

	}

	private GridLayout createGridLayout4Page( )
	{
		GridLayout layout;
		if ( pageAlignment == PAGE_ALIGN_HORIZONTAL )
		{
			layout = new GridLayout( 2, false );
			layout.marginHeight = 0;
		}
		else
		{
			layout = new GridLayout( 1, false );
			layout.marginHeight = 0;
		}
		return layout;
	}

	private GridData createGridData4Part( )
	{
		GridData data;
		if ( pageAlignment == PAGE_ALIGN_HORIZONTAL )
		{
			data = new GridData( GridData.FILL_VERTICAL );
		}
		else
		{
			data = new GridData( GridData.FILL_HORIZONTAL );
		}
		return data;
	}

	private void setControlsEnabeld( boolean b )
	{
		typeChoicer.setEnabled( b );

		formatCode.setEnabled( b );
		previewTextBox.setEnabled( b );
		table.setEnabled( b );
		return;
	}
}