package org.moka;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 * 
 */

/**
 * @author riki
 *
 */
public class MenuBar extends JMenuBar {

	
		Moka app;
    	
    	//Per menu
        //JMenuBar menuBar;
        JMenu menuFile, menuEdit, menuTools, menuWindows, menuPlug;
        JMenuItem mNew, mOpen, mSave, mSaveAs, mClose,  mExit,mImport;
        JMenuItem mCopy, mPaste, mCopyConf, mPasteConf, mNewCell;
        JMenuItem mCell, mScratchPad, mTableAtoms, mTableConf, mConsole,mKmanager, mInfo, mCoordTrans;
        JMenuItem mAddAtom, mDelAtom, mDraw, mPrintAll, mPrintXYZ, mAddConf, mDelConf, mDuplicateConf, mRefresh;
        JMenuItem mMakeSuper, mAllineateCell, mPrintSel, mWanierize,mColoProperty, mTest;
        
        JMenu[] mPlugs;
        
        public MenuBar(Moka _app) {
        	
		    this.app = _app;
		    
		    //File
		    menuFile = new JMenu("File");
		    this.add(menuFile);
		
		    mNew = new JMenuItem("New", KeyEvent.VK_N);
		    mNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.ALT_MASK));
		    mNew.addActionListener(_app);
		    mNew.setActionCommand("mNew");
		    menuFile.add(mNew);
		    
		    mOpen = new JMenuItem("Open...");
		    mOpen.addActionListener(_app);
		    mOpen.setActionCommand("mOpen");
		    menuFile.add(mOpen);
		    
		    mSave = new JMenuItem("Save", KeyEvent.VK_S);
		    mSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK));
		    mSave.addActionListener(_app);
		    mSave.setActionCommand("mSave");
		    menuFile.add(mSave);
		    
		    mSaveAs = new JMenuItem("Save As...");
		    mSaveAs.addActionListener(_app);
		    mSaveAs.setActionCommand("mSaveAs");
		    menuFile.add(mSaveAs);
		    
		    mClose = new JMenuItem("Close");
		    mClose.addActionListener(_app);
		    mClose.setActionCommand("mClose");
		        menuFile.add(mClose);
		 
		        menuFile.addSeparator();
		        
		        mImport = new JMenuItem("Import..", KeyEvent.VK_I);
		    mImport.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.ALT_MASK));
		    mImport.addActionListener(_app);
		    mImport.setActionCommand("mImport");
		    menuFile.add(mImport);
		
		    menuFile.addSeparator();
		    
		    mExit = new JMenuItem("Exit", KeyEvent.VK_X);
		    mExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.ALT_MASK));
		    mExit.addActionListener(_app);
		    mExit.setActionCommand("mExit");
		    menuFile.add(mExit);
		    
		    
		    //Edit
		    menuEdit = new JMenu("Edit");
		    this.add(menuEdit);
		    
		    mCell = new JMenuItem("Cell", KeyEvent.VK_C);
		    mCell.addActionListener(_app);
		    mCell.setActionCommand("mCell");
		    menuEdit.add(mCell);
		    
		    JMenu subEditAtoms = new JMenu("Atoms");
		    
		    mAddAtom = new JMenuItem("Add", KeyEvent.VK_A);
		    mAddAtom.addActionListener(_app);
		    mAddAtom.setActionCommand("mAddAtom");
		    subEditAtoms.add(mAddAtom);
		    
		    mDelAtom = new JMenuItem("Delete", KeyEvent.VK_D);
		    mDelAtom.addActionListener(_app);
		    mDelAtom.setActionCommand("mDelAtom");
		    subEditAtoms.add(mDelAtom);
		    
		    subEditAtoms.addSeparator();
		    
		    mCopy = new JMenuItem("Copy", KeyEvent.VK_C);
		    mCopy.addActionListener(_app);
		    mCopy.setActionCommand("mCopy");
		    subEditAtoms.add(mCopy);
		    
		    mPaste = new JMenuItem("Paste", KeyEvent.VK_V);
		    mPaste.addActionListener(_app);
		    mPaste.setActionCommand("mPaste");
		    subEditAtoms.add(mPaste);
		    
		    subEditAtoms.addSeparator();
		    
		    mTableAtoms = new JMenuItem("Atoms table", KeyEvent.VK_A);
		    mTableAtoms.addActionListener(_app);
		    mTableAtoms.setActionCommand("mTableAtoms");
		    subEditAtoms.add(mTableAtoms);
		    
		    menuEdit.add(subEditAtoms);
		    //---
		    
		    menuEdit.addSeparator();//separatore
		    
		    JMenu subEditConf = new JMenu("Configurations");
		    
		    mAddConf = new JMenuItem("Add", KeyEvent.VK_D);
		    mAddConf.addActionListener(_app);
		    mAddConf.setActionCommand("mAddConf");
		    subEditConf.add(mAddConf);
		    
		    mDelConf = new JMenuItem("Delete", KeyEvent.VK_D);
		    mDelConf.addActionListener(_app);
		    mDelConf.setActionCommand("mDelConf");
		    subEditConf.add(mDelConf);
		          
		    subEditConf.addSeparator();
		    
		    mCopyConf = new JMenuItem("Copy");
		    mCopyConf.addActionListener(_app);
		    mCopyConf.setActionCommand("mCopyConf");
		    subEditConf.add(mCopyConf);
		    
		    mPasteConf = new JMenuItem("Paste");
		    mPasteConf.addActionListener(_app);
		    mPasteConf.setActionCommand("mPasteConf");
		    subEditConf.add(mPasteConf);
		    
		    mDuplicateConf = new JMenuItem("Duplicate", KeyEvent.VK_D);
		    mDuplicateConf.addActionListener(_app);
		    mDuplicateConf.setActionCommand("mDuplicateConf");
		    subEditConf.add(mDuplicateConf);
		    
		    subEditConf.addSeparator();
		    
		    mTableConf = new JMenuItem("Conf. table", KeyEvent.VK_T);
		    mTableConf.addActionListener(_app);
		    mTableConf.setActionCommand("mTableConf");
		    subEditConf.add(mTableConf);
		    
		    menuEdit.add(subEditConf);
		    
		    //Tools
		    menuTools = new JMenu("Tools");
		    this.add(menuTools);
		    
		    mCoordTrans = new JMenuItem("Transform coord");
		    mCoordTrans.addActionListener(_app);
		    mCoordTrans.setActionCommand("mCoordTrans");
		    menuTools.add(mCoordTrans);
		    
		    
		    JMenu subEditCellTools = new JMenu("Cell tools");
		    
		    mMakeSuper = new JMenuItem("Make supercell");
		    mMakeSuper.addActionListener(_app);
		    mMakeSuper.setActionCommand("mMakeSuper");
		    subEditCellTools.add(mMakeSuper);
		    
		    mNewCell = new JMenuItem("Define new cell");
		    mNewCell.addActionListener(_app);
		    mNewCell.setActionCommand("mNewCell");
		    subEditCellTools.add(mNewCell);
		    
		    mAllineateCell = new JMenuItem("Allineate");
		    mAllineateCell.addActionListener(_app);
		    mAllineateCell.setActionCommand("mAllineateCell");
		    subEditCellTools.add(mAllineateCell);
		  
		    menuTools.add(subEditCellTools);
		
		    JMenu subEditPrintTools = new JMenu("Print");
		    
		    mPrintAll = new JMenuItem("Print all", KeyEvent.VK_O);
		    mPrintAll.addActionListener(_app);
		    mPrintAll.setActionCommand("mPrintAll");
		    subEditPrintTools.add(mPrintAll);
		    
		    mPrintSel = new JMenuItem("Print Selected");
		    mPrintSel.addActionListener(_app);
		    mPrintSel.setActionCommand("mPrintSel");
		    subEditPrintTools.add(mPrintSel);
		    
		    mPrintXYZ = new JMenuItem("Print XYZ");
		    mPrintXYZ.addActionListener(_app);
		    mPrintXYZ.setActionCommand("mPrintXYZ");
		    subEditPrintTools.add(mPrintXYZ);
		    
		    menuTools.add(subEditPrintTools);
		    
		    menuTools.addSeparator();
		    
		    mWanierize = new JMenuItem("Wanierize");
		    mWanierize.addActionListener(_app);
		    mWanierize.setActionCommand("mWanierize");
		    menuTools.add(mWanierize);
		    
		    
		    mRefresh = new JMenuItem("Refresh draw", KeyEvent.VK_R);
		    mRefresh.addActionListener(_app);
		    mRefresh.setActionCommand("mRefresh");
		    menuTools.add(mRefresh);
		    
		    mTest = new JMenuItem("Test");
		    mTest.addActionListener(_app);
		    mTest.setActionCommand("mTest");
		    menuTools.add(mTest);
		    
		    
		    
		    //Windows
		    menuWindows = new JMenu("Windows");
		    this.add(menuWindows);
		    
		    mInfo = new JMenuItem("Info");
		    mInfo.addActionListener(_app);
		    mInfo.setActionCommand("mInfo");
		    menuWindows.add(mInfo);
		    
		    mScratchPad = new JMenuItem("Scratch Pad", KeyEvent.VK_S);
		    mScratchPad.addActionListener(_app);
		    mScratchPad.setActionCommand("mScratchPad");
		    menuWindows.add(mScratchPad);
		    
		    mConsole = new JMenuItem("Viewer console");
		    mConsole.addActionListener(_app);
		    mConsole.setActionCommand("mConsole");
		    menuWindows.add(mConsole);
		    
		    menuWindows.addSeparator();
		    
//		    mDraw = new JMenuItem("Draw");
//		    mDraw.addActionListener(_app);
//		    mDraw.setActionCommand("mDraw");
//		    menuWindows.add(mDraw);
		    
		    mKmanager = new JMenuItem("K manager", KeyEvent.VK_S);
		    mKmanager.addActionListener(_app);
		    mKmanager.setActionCommand("mKmanager");
		    menuWindows.add(mKmanager);
		    
		    //Plug-ins
		    menuPlug = new JMenu("Plug-in");
		    this.add(menuPlug);
		    
		    mPlugs = new JMenu[app.plugins.length];
		    
		    for (int i=0; i<app.plugins.length; i++) {
		    	mPlugs[i] = new JMenu(app.pluginsNames[i]);
		    	
		    	JMenuItem pLoad = new JMenuItem("Load...");
		    	pLoad.addActionListener(_app);
		    	pLoad.setActionCommand(app.pluginsNames[i]);
		    	
		    	mPlugs[i].add(pLoad);
		    	
		    	menuPlug.add(mPlugs[i]);
		    }
		    
        }

}
