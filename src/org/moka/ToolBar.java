package org.moka;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import org.moka.tools.gui.GUItools;

/**
 * 
 */

/**
 * @author riki
 *
 */
public class ToolBar extends JToolBar {
	
	Moka app;
	
	//Per conf
	JButton confPrev, confNext;
	JTextField thisConf;
	JLabel totConfs;
	
	ToolBar(Moka _app) {
		
		this.app = _app;
		
		this.setFloatable(false);
        this.setRollover(true);

		  
        JButton button = null;

        //first button
        button = GUItools.makeNavigationButton("org/moka/images/New16.gif", "mNew", "New file", "New", app);
        button.addActionListener(app);
        this.add(button);

        //sec button
        button = GUItools.makeNavigationButton("org/moka/images/Open16.gif", "mOpen", "Open file", "Open", app);
        button.addActionListener(app);
        this.add(button);
        
        button = GUItools.makeNavigationButton("org/moka/images/Save16.gif", "mSave", "Save file", "Save", app);
        button.addActionListener(app);
        this.add(button);

        //sec button
        button = GUItools.makeNavigationButton("org/moka/images/Import16.gif", "mImport", "Import file", "Import", app);
        button.addActionListener(app);
        this.add(button);
        this.addSeparator();
        
        //sec button
        button = GUItools.makeNavigationButton("org/moka/images/Copy16.gif", "mCopy", "Copy atoms", "Copy", app);
        button.addActionListener(app);
        this.add(button);

      //sec button
        button = GUItools.makeNavigationButton("org/moka/images/Paste16.gif", "mPaste", "Paste atoms", "Paste", app);
        button.addActionListener(app);
        this.add(button);
        
      //sec button
        button = GUItools.makeNavigationButton("org/moka/images/Delete16.gif", "mDelAtom", "Del atoms", "Del", app);
        button.addActionListener(app);
        this.add(button);
        
        //separator
        this.addSeparator();

        button = GUItools.makeNavigationButton("org/moka/images/Edit16.gif", "mTableAtoms", "Edit atoms", "Atoms", app);
        button.addActionListener(app);
        this.add(button);
        
        button = GUItools.makeNavigationButton("org/moka/images/History16.gif", "mScratchPad", "Scratch pad", "Scratch", app);
        button.addActionListener(app);
        this.add(button);
        
        button = GUItools.makeNavigationButton("org/moka/images/AlignCenter16.gif", "mCell", "Cell edit", "Cell", app);
        button.addActionListener(app);
        this.add(button);
        
        this.addSeparator();
        
        button = GUItools.makeNavigationButton("org/moka/images/Refresh16.gif", "mRefresh", "Refresh", "Refresh", app);
        button.addActionListener(app);
        this.add(button);
        
        button = GUItools.makeNavigationButton("org/moka/images/Print16.gif", "mPrintAll", "Print structure", "Print", app);
        button.addActionListener(app);
        this.add(button);
        
        this.addSeparator();

	}

	//---------------------------------
	//	FUNZIONE DEL MODULO 
	//--------------------------------
	
}
