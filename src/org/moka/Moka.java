package org.moka;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.ChangeEvent;


import org.moka.common.Costants;

import org.moka.plugins.*;


import org.moka.structures.*;
import org.moka.structures.Module;
import org.moka.tools.*;
import org.moka.tools.gui.*;

/**
 * @author riki
 * 
 */
public class Moka extends JPanel implements ActionListener  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -790465384196475349L;
	/**
	 * @param args
	 * 
	 */
	public// Vettori per atomi
	ArrayList<Configuration> confDB = new ArrayList<Configuration>();
	public Configuration workConf = new Configuration();

	int thisConf = -1, oldConf = -1, numSelected = 0; // editingMode =
														// CONST.mNORMAL;
	boolean showCell = true, showAxes = false;

	// ArrayList<Drawable> objList = new ArrayList<Drawable>();
	// Object mousePrecedence = null;

	// ---------------
	// Per interfaccia
	// ---------------
	public static JFrame frame;
	JTextField confText;
	JLabel totConfsLabel;
	JTextField logText;
	JFileChooser fc;

	public Status status;
	IO io = new IO(this);

	// Moduli
	Controller controller;
	DrawerManager drawer;
	ViewerController viewcontroller;
	RightBar rightBar;

	AtomsEditor tableFrame;
	ScratchPad scratchFrame;
	CellEditor cellFrame;
	ConfigurationManager tableConf;
	public Viewer viewer;
	InfoPanel info;

	Console console;
	KPointEditor kmanager;
	Mathematics math;

	Module[] modulesList;

	// Plugins
	Plugin pjwfcPLUG, mdPLUG, phPLUG, bandsPLUG, analysisPLUG;

	Plugin[] plugins = new Plugin[] { pjwfcPLUG, mdPLUG, phPLUG, bandsPLUG, analysisPLUG };

	String[] pluginsNames = new String[] { "PjWFC", "MD", "Phonos", "Bands", "Analysis" };

	// /---------------------------
	// / CONSTRUCTOR (bottoni, pannelli)
	// /---------------------------

	MenuBar menu = new MenuBar(this);

	public Moka() {

		fc = new JFileChooser();

		// Create home directory if doesn't exists
		File home = new File(System.getProperty("user.home"));
		if (!new File(home.getAbsolutePath() + "/.moka").exists()) {
			new File(home.getAbsolutePath() + "/.moka").mkdir();
		}

		File statusBK = new File(home.getAbsolutePath() + "/.moka/status.xml");
		if (statusBK.exists()) {
			status = (Status) readXML(home.getAbsolutePath()
					+ "/.moka/status.xml");
		} else {
			status = new Status(this);
		}

		// Moduli
		controller = new Controller(this);
		drawer = new DrawerManager(this);
		viewcontroller = new ViewerController(this);
		rightBar = new RightBar(this);

		tableFrame = new AtomsEditor(this);
		scratchFrame = new ScratchPad(this);
		cellFrame = new CellEditor(this);
		tableConf = new ConfigurationManager(this);
		viewer = new ViewerJmol(this);
		info = new InfoPanel(this);

		console = new Console(this);
		kmanager = new KPointEditor(this);
		math = new Mathematics(this);

		modulesList = new Module[] { controller, viewcontroller, drawer,
				tableFrame, scratchFrame, cellFrame, tableConf, console,
				viewer, kmanager, math, info, rightBar };

		// ----------------
		// Initialize
		// ----------------
		viewcontroller.setActive(true);

	}

	// /---------------------------
	// / CREA INTERFACCIA
	// /---------------------------

	public void createAndShowGUI(JFrame _frame) {

		frame = _frame;
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		ToolBar toolBar = new ToolBar(this);
		add(toolBar, BorderLayout.PAGE_START);

		// ----------------------------------------------
		// Pannello di sinistra (Jmol, comandi sotto)
		// ----------------------------------------------
		add(viewer.getViewerPanel(), BorderLayout.CENTER);

		// ----------------------------------------------
		// Pannello di detsra
		// ----------------------------------------------

		// JToolBar mainBar = new JToolBar();
		// mainBar.setFloatable(true);
		// mainBar.setRollover(true);
		// mainBar.setSize(150,400);
		//    	
		// Box mainBarPanel = Box.createVerticalBox();
		//    	
		// mainBarTab = new JTabbedPane();
		// mainBarTab.addTab("View", createImageIcon("images/Bookmarks16.gif"),
		// viewcontroller,"Viewer Panel");
		// mainBarTab.addTab("Transform",
		// createImageIcon("images/Bookmarks16.gif"),
		// controller,"Transform Panel");
		// mainBarTab.addTab("Draw", createImageIcon("images/Bookmarks16.gif"),
		// drawer,"Drawing Panel");
		// mainBarTab.addChangeListener(this);
		// mainBarPanel.add(mainBarTab);
		// //----------------------------------------
		// // Pannello selezione CONF ATOMS
		// //----------------------------------------
		//        
		// Box stepPanel = Box.createHorizontalBox();
		//		
		// JButton confPrev = GUItools.makeNavigationButton("Back16.gif",
		// "confPrev", "Prev conf", "Prev",this);
		// confPrev.addActionListener(this);
		// //confPrev.setPreferredSize(new Dimension(30,50));
		// stepPanel.add(confPrev);
		//		
		// confText = new JTextField("0");
		// confText.setActionCommand("confText");
		// confText.addActionListener(this);
		// stepPanel.add(confText);
		//		
		// totConfsLabel = new JLabel("/ 0");
		// stepPanel.add(totConfsLabel);
		//		
		// JButton confNext = GUItools.makeNavigationButton("Forward16.gif",
		// "confNext", "Next conf", "Next",this);
		// confNext.addActionListener(this);
		// //confNext.setPreferredSize(new Dimension(30,50));
		// stepPanel.add(confNext);
		// mainBarPanel.add(GUItools.createPanelForComponent(stepPanel, "Confs",
		// new Dimension(status.rightBarWidht-40,50)));
		//		
		// mainBar.add(mainBarPanel);

		add(rightBar, BorderLayout.EAST);

		// ----------------------------------------------
		// Log in basso
		// ----------------------------------------------
		Box logBox = Box.createHorizontalBox();
		logBox.setMinimumSize(new Dimension(200, 30));
		logText = new JTextField("Logging...");
		logText.setEditable(false);
		logBox.add(logText);
		// progressBar = new JProgressBar(0);
		// progressBar.setValue(0);
		// progressBar.setStringPainted(true);
		// logBox.add(progressBar);
		add(logBox, BorderLayout.SOUTH);

		// ----------------------------------------------
		// Menu
		// ----------------------------------------------

		frame.setJMenuBar(menu);

		// ----------------------------------------------
		// Finestra aperte alla partenza
		// ----------------------------------------------
		frame.setPreferredSize(status.mokaDimesion);
		frame.setLocation(0, 0);

		// console.pack();
		// console.setVisible(true);
		// console.setLocation(0, dim.height-60);
		// console.setPreferredSize(new
		// Dimension(Math.round(2*dim.width/3),50));
		// console.setSize(new Dimension(Math.round(2*dim.width/3),50));

		tableFrame.pack();
		tableFrame.setVisible(true);
		tableFrame.setLocation(status.mokaDimesion.width + 20, 0);
		tableFrame.setPreferredSize(new Dimension(Math
				.round(status.mokaDimesion.width / 4), Math
				.round(status.mokaDimesion.height / 2)));
		tableFrame.setSize(new Dimension(Math
				.round(status.mokaDimesion.width / 4), Math
				.round(status.mokaDimesion.height / 2)));

		scratchFrame.pack();
		// inputFrame.setVisible(true);

		// logText = console.mainText;
	}

	// /---------------------------
	// / AZIONI
	// /---------------------------

	public void actionPerformed(ActionEvent e) {

		if (status.isAppWaiting())
			return;

		String buttClicked = e.getActionCommand(); // getClassName(e.getSource().getClass());

		// ------------------
		// menu FILE
		// ------------------
		if (buttClicked.equals("mNew")) {
			
			if (status.isModified) {
				closeFile();
			}

			clearVars(true);
			status.resetFileVars();
			
			addConf();
			loadConf(0);

			reset();

		}

		if (buttClicked.equals("mOpen")) {

			storeConf();
			if (status.isModified) {
				closeFile();
			}

            clearVars(true);
            status.resetFileVars();
            
			status.startWaiting();
			
			if (openMoka()) {
				loadConf(0);
				reset();
			} else {
                log("Errore lettura file MOKA.");
            }
			
			status.stopWaiting();

			

		}

		if (buttClicked.equals("mSave") || buttClicked.equals("mSaveAs")) {
			storeConf();

			saveMoka(buttClicked.equals("mSaveAs") ? true : false);

		}

		if (buttClicked.equals("mImport")) {

			storeConf();
			importFile();

		}

		if (buttClicked.equals("mClose")) {

			if (status.isModified) {
				closeFile();
			}

			clearVars(true);
			addConf();
			loadConf(0);

			status.unsetModified();
			status.unsetMokaName();
			reset();
			log("File closed.");

		}

		// ------------------
		// ATOMS
		// ------------------

		if (buttClicked.equals("mCopy")) {

			if (numSelected > 0) {
				copyAtoms();
				log("Atoms copied");
			} else {
				log("Seleziona alcuni atomi");
			}
		}

		if (buttClicked.equals("mPaste")) {

			workConf.clearFlags(Costants.aSELECTED);

			String[] pasteModes = { "Posizione originale", "Shift in zero" };
			int posToPaste = RadioDialog.showDialog(frame, (Component) e
					.getSource(), "Posizione di paste", "Paste", pasteModes, 0);

			if (posToPaste == 1) {
				pasteAtoms(true);
			} else {
				pasteAtoms(false);
			}

			status.setModified();
			tableFrame.fillTableWithPos();
			viewer.refreshDraw(true);

			log("Atoms pasted");
		}

		if (buttClicked.equals("mAddAtom")) {

			addAtom();
			log("Atom added");

			status.setModified();
			tableFrame.fillTableWithPos();
			viewer.refreshDraw(true);

			log("One atom added");
		}

		if (buttClicked.equals("mDelAtom")) {

			int[] allSelected = workConf.getAtomsFlagged(Costants.aSELECTED);

			if (allSelected.length >= 1) {

				delAtom(allSelected);
			}
			workConf.clearFlags(Costants.aSELECTED);

			status.setModified();
			tableFrame.fillTableWithPos();
			viewer.refreshDraw(true);

			log(allSelected.length + " atoms deleted");
		}

		// ------------------
		// CONFIGURATIONS
		// ------------------

		if (buttClicked.equals("mAddConf")) {

			storeConf();
			addConf();
			log("Configurazione aggiunta in pos. " + confDB.size());

			status.setModified();
			// selectConf(atomsConf.size());

			reset();
			// viewer.refreshDraw();
			log("Configuration added");

		}

		if (buttClicked.equals("mCopyConf")) {

			if (workConf != null) {
				copyConf();
				log("Configuration copied");
			} else {
				log("Errore...ooppss");
			}
		}

		if (buttClicked.equals("mPasteConf")) {

			workConf.clearFlags(Costants.aSELECTED);
			pasteConf();
			log("Configuration pasted");

			status.setModified();
			reset();
		}

		if (buttClicked.equals("mDuplicateConf")) {

			if (confDB != null && workConf != null)
				storeConf();
			duplicateConf();
			log("Configurazione duplicata in pos. " + confDB.size());
			// selectConf(atomsConf.size());

			status.setModified();
			reset();
			// viewer.refreshDraw();

		}

		if (buttClicked.equals("mDelConf")) {

			if (GUItools.getYesNoFromUser("Sicuro di cancellare ?")) {

				deleteConf(thisConf);
				log("Configurazione " + (thisConf + 1) + " eliminata.");

				if (confDB.size() == 0) {
					confDB.add(new Configuration(0));
					thisConf = 0;
					// viewer.refreshDraw(false);
				} else if (thisConf == confDB.size()) {
					loadConf(confDB.size() - 1);
				} else {
					loadConf(thisConf);
				}

				status.setModified();
				reset();

				log("Configuration " + thisConf + " pasted");
				// viewer.refreshDraw();
			}
		}

		// ---------------------
		// TOOLS
		// ---------------------

		if (buttClicked.equals("mTest")) {

			test();
			

		}

		if (buttClicked.equals("mWanierize")) {

			wanierizeConf();
			reset();

		}

		if (buttClicked.equals("mPrintSel")) {
			storeConf();
			scratchFrame.printStructure("qe");
			scratchFrame.printSelected();
			scratchFrame.setVisible(true);
		}

		if (buttClicked.equals("mPrintAll")) {
			storeConf();
			scratchFrame.printStructure("QE");
			scratchFrame.setVisible(true);
		}

		if (buttClicked.equals("mPrintXYZ")) {
			storeConf();
			scratchFrame.printStructure("XYZ");
			scratchFrame.setVisible(true);
		}

		if (buttClicked.equals("mRefresh")) {

			reset();

		}

		if (buttClicked.equals("mMakeSuper")) {

			if (workConf == null) {
				return;
			}
			int[] dims = GUItools.getArrayIntFormUser("Sueper cell vector",
					"[1 1 1]");
			makeSuperCell(dims);
			reset();

			log("Super-cell done");
		}

		if (buttClicked.equals("mAllineateCell")) {

			workConf.allineateCell();
			reset();

		}

        if (buttClicked.equals("mNewCell")) {

			int allObj = workConf.getDrawList().size();

            boolean hasCell = false;
            for (int i=0; i<allObj; i++) {
                if (workConf.getDrawList().get(i).getType()==Costants.oCELL) hasCell = true;
            }

            if (!hasCell) {
                tell("No new cell defined !");
                return;
            } else {
                makeNewConfFromCell();
            }


		}

		// ------------------
		// menu WINDOWS
		// ------------------
		if (buttClicked.equals("mTableAtoms")) {

			tableFrame.setVisible(true);
		}

		if (buttClicked.equals("mScratchPad")) {
			scratchFrame.setVisible(true);
		}

		if (buttClicked.equals("mInfo")) {
			info.pack();
			info.setVisible(true);
		}

		if (buttClicked.equals("mTableConf")) {
			tableConf.pack();
			tableConf.setVisible(true);
			tableConf.tableFillConf();

		}

		if (buttClicked.equals("mCell")) {
			cellFrame.pack();
			cellFrame.setVisible(true);

		}

		// if (buttClicked.equals("mConverter")) {
		// trasformer.pack();
		// trasformer.setVisible(true);
		//		
		// }

		if (buttClicked.equals("mConsole")) {
			console.pack();
			console.setVisible(true);

		}

		// if (buttClicked.equals("mDraw")) {
		//			
		// controller.mouseToolBox.setSelectedIndex(Costants.mDRAW);
		// //setEditingMode(Costants.mDRAW);
		//			
		// //draw.pack();
		// //draw.setVisible(true);
		//		
		// }

		if (buttClicked.equals("mKmanager")) {

			kmanager.pack();
			kmanager.setVisible(true);

		}

		if (buttClicked.equals("mExit")) {
			exit();
		}

		if (buttClicked.equals("mClose")) {
			closeFile();
		}

		int plugNum = -1;
		if ((plugNum = ArrayTools.whereIsInArray(pluginsNames, buttClicked)) != -1) {

			if (plugins[plugNum] != null)
				return;

			status.startWaiting();

			switch (plugNum) {

			case 0:
				plugins[plugNum] = new PjwfcPLUGIN(this);
				break;
			case 1:
				plugins[plugNum] = new MdPLUGIN(this);
				break;
			case 2:
				plugins[plugNum] = new PhononPLUGIN(this);
				break;
			case 3:
				plugins[plugNum] = new BandsPLUGIN(this);
				break;
            case 4:
				plugins[plugNum] = new AnalysisPLUGIN(this);
				break;
			}

			menu.menuPlug.remove(plugNum);
			menu.menuPlug.insert(plugins[plugNum].makePluginMenu(), plugNum);
			menu.repaint();

			status.stopWaiting();
		}

	}

	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub

	}

	// public void setEditingMode(int inMode) {
	//		
	// status.setEditingMode(inMode);
	//		
	// if (inMode==Costants.mNORMAL) {
	//			
	// mousePrecedence=null;
	// viewer.drawAtomsColors(workConf);
	// //rightBar.selectBar(Costants.mNORMAL);
	//			
	// }
	//		
	// if (inMode==Costants.mTRANSFORM || inMode==Costants.mBLOCK ||
	// inMode==Costants.mDELETE) {
	//			
	// viewer.drawAtomsColors(workConf);
	//			
	// status.setWorkingOn();
	// //controller.reset();
	// //controller.mouseToolBox.setSelectedIndex(ArrayTools.whereIsInArray(controller.modesSelectable,
	// Costants.editingModes[inMode]));
	// //rightBar.selectBar(Costants.mTRANSFORM);
	// status.setWorkingOff();
	// }
	//		
	// if (inMode==Costants.mDRAW) {
	//			
	// viewer.drawAtomsColors(workConf);
	//			
	// status.setWorkingOn();
	// rightBar.selectBar(Costants.mDRAW);
	// status.setWorkingOff();
	// }
	//		
	// log("Attivato mode: "+Costants.editingModes[inMode]);
	// }

	// /---------------------------
	// / DEVELOP
	// /---------------------------

	public void test(){

        double phi = Math.PI/2;

//        System.out.println("Quaternion way---");
//		Vect temp = new Vect(0,1,0);
//        Rotation rotate = new Rotation(2,1,0, phi);
//        System.out.println(rotate.getTransform(temp).outstring(3));
//
//        System.out.println("New way---");
//        Vect ax = new Vect(2,1,0);
//        double cphi = Math.cos(phi);
//        Vect p1 = temp.scalarMultiply(cphi);
//        Vect p2 = p1.add(ax.normalize().scalarMultiply((1-cphi)*(temp.invert().dot(ax.normalize()))));
//        Vect p3 = p2.add(ax.normalize().cross(temp).scalarMultiply(Math.sin(phi)));
//        System.out.println(p3.outstring(3));
//
//
//        System.out.println("Quaternion around point---");
//		temp = new Vect(0,1,0);
//        rotate = new Rotation(0,0,1, phi);
//        Vect point = new Vect(1,0,0);
//        System.out.println(rotate.getTransformAroundPoint(temp, point).outstring(3));


//        for (int i=0; i<3; i++)  {
//            double[][] dCell = new double[2][3];
//            dCell[0] = new double[] {0,0,0};
//            dCell[1] = workConf.getCell().getCellReciprocal()[i];
//            viewer.addDrawable(new Drawable(Costants.oVECTOR, "temp"+i,dCell));
//        }

//        double[] a = {0.1,1.1,-1.1,1.9};
//        for (int i=0; i<a.length; i++) {
//            System.out.println("Value: "+ a[i]+ " Round:"+Math.round(a[i]));
//        }
        

//        double[][] mat = {{1,2,3},{4,5,6},{6,7,8}};
//
//        System.out.println(ArrayTools.arrayToStringTwo(mat));
//        System.out.println();
//        System.out.println(ArrayTools.arrayToStringTwo(MathTools.arrayFlip(mat)));
        int[] sel = workConf.getAtomsSelected();
        double out = workConf.getAtomsDistancePBC(sel[0], sel[1], Costants.cANGSTROM);

        //System.out.println(out);
	}

	// /---------------------------
	// / FILES in/out/import
	// /---------------------------

	public boolean openMoka() {

		String fullName = "";

		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(false);
		fc.setFileFilter(status.mokaFiles);

		// Pulisci tutto

		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			fullName = fc.getSelectedFile().getAbsoluteFile().toString();
			try {

				ArrayList<Configuration> temp = io.readMoka(fullName);
				if (temp != null) {
					if (temp.size() > 0) {

						log("Importate correttamente " + confDB.size()
								+ " configurazioni.");
						status.setMokaName(fullName);

                        for (int i=0; i< temp.size(); i++) {
                            confDB.add(temp.get(i));
                        }

						return true;

					}
				}

			} catch (IOException e1) {
				// TODO Auto-generated catch block
				log("Import error !");
				e1.printStackTrace();
				return false;
			}

		} else {

			return false;
		}

		return false;
	}

	public void saveMoka(boolean saveAs) {

		boolean doIT = false;

		if (confDB == null) {
			tell("Nessuna configrazione !");
			return;
		}

		if (saveAs || !status.isNameSet()) {

			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fc.setMultiSelectionEnabled(false);

			fc.setFileFilter(status.mokaFiles);

			if (saveAs && status.isNameSet()) {
				fc.setCurrentDirectory(new File(status.getWorkingDir()));
				fc.setName(status.getOnlyFileName());
			}

			if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {

				doIT = true;

				if (fc.getSelectedFile().exists()) {
					doIT = GUItools
							.getYesNoFromUser("Sovrascrivere il file ?");
				}

				// Sistema il nome
				if (!fc.getSelectedFile().getName().endsWith(".moka")) {
					status.setMokaName(fc.getSelectedFile().getAbsoluteFile()
							+ ".moka");
				} else {
					status.setMokaName(fc.getSelectedFile().getAbsoluteFile()
							.toString());
				}

			}
		} else {
			doIT = true;
		}

		if (doIT) {

			// Concella il vecchio file
			if (new File(status.getFileName()).exists())
				new File(status.getFileName()).delete();

			try {

				io.writeMoka(status.getFileName());
				log("Salvataggio effettuato di: " + status.getFileName());
				status.unsetModified();

			} catch (IOException e1) {
				// TODO Auto-generated catch block
				// e1.printStackTrace();
				tell("Errore nel salvataggio !");
			}
		}

	}

	public void importFile() {

		if (confDB != null) {
			if (confDB.size() > 0) {
				if (!GUItools.getYesNoFromUser("Append ?")) {
					closeFile();
				}
			}
		}

		String name = "";
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(false);
		fc.setFileFilter(status.allFiles);

		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			name = fc.getSelectedFile().getAbsoluteFile().toString();
			status.workingDir = fc.getSelectedFile().getPath();

			int impType = RadioDialog.showDialog(frame, null, "Import type",
					"Mode", io.importTypes, 0);
			int confImp = io.importFile(name, impType);
			if (confImp > 0) {

				log("Importate correttamente " + confImp + " configurazioni.");

				// Setta lo step iniziale
				loadConf(0);

				reset();
			} else {

				log("Errore apertura file.");

			}

		}

	}

	public void closeFile() {

		if (status.isModified) {

			if (GUItools
					.getYesNoFromUser("Configuration not saved.\n Save ?")) {

				saveMoka(false);

			}
		}
		clearVars(true);

	}

	public Object readXML(String file) {

		Object out;
		XMLDecoder e;

		try {

			e = new XMLDecoder(new BufferedInputStream(
					new FileInputStream(file)));
			out = e.readObject();
			e.close();

		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			out = null;
		}

		return out;
	}

	// /---------------------------
	// / GESTIONE CONFS
	// /---------------------------

	public void loadConf(int _numConf) {

		if (_numConf >= 0 && _numConf < confDB.size()) {

			if (workConf != null && thisConf > -1)
				storeConf();

			// Per la traslazione degli atomi
			oldConf = thisConf;
			Configuration.copyStructure(confDB.get(_numConf), workConf);
			thisConf = _numConf;

			log("Loaded configuration " + (_numConf + 1));
		}

		// this.reset();
	}

	public void storeConf() {
		if (confDB != null && workConf != null && thisConf >= 0) {

			Configuration.copyStructure(workConf, confDB.get(thisConf));
		}

	}

	// public void selectConf(int confToSet) {
	//		
	// // Per la traslazione degli atomi
	// oldConf = thisConf;
	//		
	// Configuration.copyStructure(confDB.get(confToSet),workConf);
	// thisConf = confToSet;
	//		
	// }

	public void addConf() {
		// for saving

		confDB.add(new Configuration(1));

	}

	public void duplicateConf() {
		// for saving

		addConf();
		int lastConf = confDB.size() - 1;

		Configuration.copyStructure(workConf, confDB.get(lastConf));

	}

	public void deleteConf(int confToDelete) {
		// for saving

		confDB.remove(confToDelete);

	}

	public void wanierizeConf() {

		workConf.rescaleCellA(Costants.cSPECIALPARAM);
		workConf.convertPositions(Costants.cCRYSTAL);
		workConf.convertPositions(Costants.kCRYSTAL);

	}

	// /---------------------------
	// / ADD - DELETE atoms
	// /---------------------------

	public void addAtom() {

		workConf.addAtom();
		log("Added atom #" + (workConf.getNAtoms() + 1));

	}

	public void addAtom(String name, double[] pos) {

		workConf.addAtom(name, pos);

		log("Added atom #" + (workConf.getNAtoms() + 1));

	}

	public void delAtom(int[] arrayToDel) {

		if (GUItools.getYesNoFromUser("Sure ?")) {

			// workConf.positions.set(toDel,
			// workConf.positions.get(workConf.nAtoms-1).clone());
			// workConf.names.set(toDel, workConf.names.get(workConf.nAtoms-1));
			// workConf.flags.set(toDel, workConf.flags.get(workConf.nAtoms-1));
			workConf.delAtom(arrayToDel);

			log("Deleted atom #" + arrayToDel.length + " atoms.");

		}

	}

	// /---------------------------
	// / BLOCK atoms
	// /---------------------------

	public void blockAtom(int toBlock) {
		// -for saving

		if (workConf.isAtomBlocked(toBlock)) {
			workConf.unBlockAtom(toBlock);
			log("Un-blocked atom (" + workConf.getAtoms().get(toBlock).getName()
					+ ") #" + (toBlock + 1));
		} else {
			workConf.blockAtom(toBlock);
			log("Blocked atom (" + workConf.getAtoms().get(toBlock).getName()
					+ ") #" + (toBlock + 1));
		}

	}

    // /---------------------------
	// / SELECT / UNSELECT atoms
	// /---------------------------

    public void unSelectAtom(int toUnSel) {

        if (toUnSel==-1) {
            unSelectAtom(workConf.getAtomsSelected());
        } else {
            unSelectAtom(new int[] {toUnSel});
        }
        
    }

    public void unSelectAtom(int[] toUnSel) {

        for (int i=0; i< toUnSel.length; i++) {

            workConf.unSelectAtom(toUnSel[i]);

        }

        //Decolora i vecchi
		for (int i=0; i<toUnSel.length; i++) {

			viewer.colorAtom(toUnSel[i], "CPK");
			
		}

    }

    public void selectAtoms() {
        selectAtoms(workConf.getAtomsSelected());
    }

    public void selectAtom(int toSel, boolean addToSelection) {
        
        if (addToSelection) {

            int[] sel = new int[workConf.getAtomsSelected().length+1];
            for (int i=0; i<workConf.getAtomsSelected().length; i++) {
                sel[i] = workConf.getAtomsSelected()[i];
            }
            sel[sel.length-1] = toSel;

            selectAtoms(sel);
            
        } else {
            selectAtoms(new int[] {toSel});
        }



    }

	public void selectAtoms(int[] toSel) {

		// viewer.viewer.repaintView();

        //System.out.println("selectAtom");

        // Salva la vecchia selezione
        int[] oldSelected = workConf.getAtomsFlagged(Costants.aSELECTED);

        // Cancella tutto
		workConf.clearFlags(Costants.aSELECTED);

        // Setta la nuova selezione
        for (int i=0; i< toSel.length; i++) workConf.selectAtom(toSel[i]);

        int[] nowSelected = workConf.getAtomsFlagged(Costants.aSELECTED);

        //Decolora i vecchi
		for (int i=0; i<oldSelected.length; i++) {

			if (!ArrayTools.isInArray(nowSelected, oldSelected[i])) viewer.colorAtom(oldSelected[i], "CPK");

		}
        
		//Colora i nuovi
		for (int i=0; i<nowSelected.length; i++) {

			if (!ArrayTools.isInArray(oldSelected, nowSelected[i])) viewer.colorAtom(nowSelected[i], "green");
		}

        tableFrame.selectTable(nowSelected);

	}

    public void clearSelection() {

		// viewer.viewer.repaintView();

        int[] selected = workConf.getAtomsFlagged(Costants.aSELECTED);

		for (int i=0; i< selected.length; i++) {

            workConf.unSelectAtom(selected[i]);

        }

        for (int i=0; i<selected.length; i++) {
        	viewer.colorAtom(selected[i], "CPK");
		}


	}

	// /---------------------------
	// / COPY - PASTE atoms
	// /---------------------------

	public void copyAtoms() {

		status.positionsCopy.clear();
		status.namesCopy.clear();

		for (int i = 0; i < workConf.getNAtoms(); i++) {
			if (workConf.isAtomSelected(i)) {

                status.positionsCopy.add(workConf.getAtomPos(i, Costants.cANGSTROM));
				status.namesCopy.add(workConf.getAtomName(i));
			}
		}

	}

	public void pasteAtoms(boolean shiftToZero) {
		// for saving

		for (int i = 0; i < status.positionsCopy.size(); i++) {

			double[] vect;
			if (shiftToZero) {
				vect = MathTools.arraySubtract(status.positionsCopy.get(i),
						status.positionsCopy.get(0));
			} else {
				vect = status.positionsCopy.get(i);
			}

			addAtom(status.namesCopy.get(i),
                    workConf.getCell().convertVector( Costants.cANGSTROM, workConf.getCoordType(), vect)
                    );
            
			selectAtom(workConf.getNAtoms() - 1, i == 0 ? true : false);

		}

	}

	public void copyConf() {

		Configuration.copyStructure(workConf, status.confCopy);

	}

	public void pasteConf() {

		addConf();
		int lastConf = confDB.size() - 1;

		Configuration.copyStructure(status.confCopy, confDB.get(lastConf));

	}

    // /---------------------------
	// NEW CONF GENERATION
	// /---------------------------


    public void makeSuperCell(int[] dims) {

		workConf.addSuperCellAtoms(dims);
		workConf.getCell().makeSuperCell(dims);

	}

    public void makeNewConfFromCell() {

        ArrayList<String> cellNames = new ArrayList<String>();

        for (int i=0; i<workConf.getDrawList().size(); i++) {
            if (workConf.getDrawList().get(i).getType()==Costants.oCELL){
                cellNames.add(workConf.getDrawList().get(i).getName());
            }
        }

        int newCell = ComboDialog.showDialog(frame, null, "New cell:", "Element for:", ArrayTools.arrayListToString(cellNames), 0);

        Drawable newC = new Drawable();

        for (int i=0; i<workConf.getDrawList().size(); i++) {
            if (workConf.getDrawList().get(i).getName().equals(cellNames.get(newCell))){
                newC = workConf.getDrawList().get(i);
            }
        }

        double[] origin = newC.getPoints()[0];
        double[][] newCellVectors = new double[3][3];

        for (int i=0; i<3; i++) {
            newCellVectors[i] = MathTools.arraySubtract(newC.getPoints()[i+1], origin);
        }

        Cell c = new Cell(newCellVectors);

        this.unSelectAtom(-1);

        ArrayList<Integer> newSelection = new ArrayList<Integer>();
        for (int i=0; i<workConf.getNAtoms(); i++) {

            double[] posAng = MathTools.arraySubtract(workConf.getAtomPos(i, Costants.cANGSTROM), origin);

            if (c.isPointInCell(posAng,  Costants.cANGSTROM)) {
                newSelection.add(i);
            }
            
        }

        this.selectAtoms(ArrayTools.arrayListToInt(newSelection));

    }

	// /---------------------------
	// / RESET
	// /---------------------------

	public void clearVars(boolean clearConfs) {

		thisConf = -1;
		oldConf = -1;
		numSelected = 0;

		if (workConf != null) {
			workConf.getAtoms().clear();
		}

		if (clearConfs)
			confDB.clear();

	}

	public void reset() {

		// Num conf
		// totConfsLabel.setText("/ "+confDB.size());
		// confText.setText(""+(thisConf+1));
		//		

		for (int i = 0; i < modulesList.length; i++) {
			modulesList[i].reset();
		}

//		viewer.setFocus();
		frame.setTitle("Moka - " + workConf.getName());

	}

	public void confChanged() {

		// Num conf
		// totConfsLabel.setText("/ "+confDB.size());
		// confText.setText(""+(thisConf+1));
		//		
		for (int i = 0; i < modulesList.length; i++) {
			modulesList[i].confChanged();
		}

		frame.setTitle("Moka - " + workConf.getName());

	}

	public void atomsChanged(int[] list) {

		for (int i = 0; i < modulesList.length; i++) {
			modulesList[i].atomsChanged(list);
		}

	}

	// /---------------------------
	// / MAIN LAUNCHER
	// /---------------------------

	public void exit() {

		if (status.isModified)
			closeFile();
		status.save();

		System.exit(0);
	}

	// /---------------------------
	// / misc
	// /---------------------------

	// Returns just the class name -- no package info.
	protected String getClassName(Object o) {
		String classString = o.getClass().getName();
		int dotIndex = classString.lastIndexOf(".");
		return classString.substring(dotIndex + 1);
	}

	public void tell(String _in) {
		JOptionPane.showMessageDialog(frame, _in);
	}

	public void log(String _in) {
		logText.setText(_in);
	}

	public void alert(String _in) {
		JOptionPane.showMessageDialog(frame, _in);
	}

	public void output(String _in, boolean clean) {

		if (clean)
			scratchFrame.clean();
		scratchFrame.append(_in);
		scratchFrame.setVisible(true);
	}

	public void output(String _in) {
		output(_in, false);
	}

	public void outputPlugin(String _in, String _value, boolean clean) {

		info.fillPlugin(_in, _value, clean);
	}

}
