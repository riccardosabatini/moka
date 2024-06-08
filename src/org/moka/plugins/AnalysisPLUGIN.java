package org.moka.plugins;

import java.awt.event.*;
import javax.swing.*;
import org.moka.Moka;


import org.moka.structures.*;
import org.moka.common.*;
import org.moka.tools.gui.*;

import java.util.Hashtable;

public class AnalysisPLUGIN extends JFrame implements ActionListener, Plugin{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7602854889748241547L;
    
    private static final int ACTION_NULL = -1;
    private static final int ACTION_NORM_COORDINATION = 1;
    private static final int ACTION_FIND_ATOMS_IN_SHELL = 2;
    private static final int ACTION_FIND_ATOMS_INSIDE_RADIUS = 3;
	Moka app;
	
    private JFileChooser fc;
    boolean readyToReciveSelection = false;
    int actionType = ACTION_NULL;

    int nCloserAtoms = 1;
    double sensitivity = 0.0005;


    double manualRadius = 1.0;
    int radiusType = Costants.RADIUS_MANUAL;
    double[] normRadius = new double[200];
    
    Hashtable<String, Double> boundData;
    
    ///---------------------------
    /// CONSTRUCTOR	(bottoni, pannelli)
    ///---------------------------
	public AnalysisPLUGIN(Moka _app) {
		
		this.app = _app;
		fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
	}

    ///---------------------------
    /// CREA INTERFACCIA
    ///---------------------------

	 public JMenu makePluginMenu() {
		 
		 JMenu mANALYS;
		 JMenuItem mNormCoordination, mFindCloser, mSetMouse, mAtomsInShell;
		 //JCheckBoxMenuItem mAtomsInShellAskRadius;
         
		 mANALYS = new JMenu("Analysis");

         JMenu coordMenu = new JMenu("Selections");

         mFindCloser = new JMenuItem("Select consecutive shell");
		 mFindCloser.addActionListener(this);
		 mFindCloser.setActionCommand("mSelectShell");
		 coordMenu.add(mFindCloser);

         mAtomsInShell = new JMenuItem("Select inside radius");
		 mAtomsInShell.addActionListener(this);
		 mAtomsInShell.setActionCommand("mInsideRadius");
		 coordMenu.add(mAtomsInShell);

         mANALYS.add(coordMenu);

         mNormCoordination = new JMenuItem("Norm. coordination");
         mNormCoordination.addActionListener(this);
         mNormCoordination.setActionCommand("mNormCoordination");
		 mANALYS.add(mNormCoordination);

         mSetMouse = new JMenuItem("Set/Unset mouse selection");
		 mSetMouse.addActionListener(this);
		 mSetMouse.setActionCommand("mSetMouse");
		 mANALYS.add(mSetMouse);

		 
		 return mANALYS;
	        
	 }
	
	public void actionPerformed(ActionEvent e) {

		String buttClicked = e.getActionCommand(); //getClassName(e.getSource().getClass());

		//------------------
		// menu FILE
		//------------------
		
		if (buttClicked.equals("mNormCoordination")) {

            actionType = ACTION_NORM_COORDINATION;
            readyToReciveSelection = true;
			
		}

        if (buttClicked.equals("mSelectShell")) {

            
            String[] answ = GUItools.getStrFormUser("Num. closer atoms : [num,sensitivty]", "2,0.0005").split(",");
            if (answ==null) return;
            
            nCloserAtoms = Integer.parseInt(answ[0]);
            sensitivity = Double.parseDouble(answ[1]);
            
            actionType = ACTION_FIND_ATOMS_IN_SHELL;
            readyToReciveSelection = true;

		}

        if (buttClicked.equals("mInsideRadius")) {

            
            String answ = GUItools.getStrFormUser("Radius : [covalent|vdW|value]", "vdW");
            if (answ==null) return;
            
            if (answ.toLowerCase().equals("covalent") || answ.toLowerCase().equals("cov") || answ.toLowerCase().equals("c")) {

                radiusType = Costants.RADIUS_COVALENT;
            } else if (answ.toLowerCase().equals("vdw") || answ.toLowerCase().equals("v")) {
                radiusType = Costants.RADIUS_VDW;
            } else {
                manualRadius = Double.parseDouble(answ);
                radiusType = Costants.RADIUS_MANUAL;
            }

            actionType = ACTION_FIND_ATOMS_INSIDE_RADIUS;
            readyToReciveSelection = true;



		}

//        if (buttClicked.equals("mInsideRadiusAsk")) {
//
//            askRadius = ! askRadius;
//            
//        }
        
        if (buttClicked.equals("mSetMouse")) {

            readyToReciveSelection = !readyToReciveSelection;
            if (readyToReciveSelection) {
                app.log("ANALYSIS plugin reciving selections.");
            } else {
                app.log("ANALYSIS plugin STOPPED selections.");
            }

        }


		
    }


    public boolean receiveSelection(int atomSelected) {
    	
    	boolean out = false;
    	
        switch (actionType) {
            case ACTION_NORM_COORDINATION: calcNormCoordination(atomSelected);
            							   out = true; // Non esegue la selzione dopo
            							   break;
            case ACTION_FIND_ATOMS_IN_SHELL: out = selectAtomsInShell(atomSelected); break;
            case ACTION_FIND_ATOMS_INSIDE_RADIUS: out = selectAtomsInsideRadius(atomSelected); break;
            default: break;
        }
        
        return out;

	}

    public void calcNormCoordination(int sel) {

    	int[] selected = app.workConf.getAtomsSelected();

        //----------------------------------
        //  Find the interacting species and 
        //	ask specific physical data
        //----------------------------------
        
        if (boundData==null) boundData = new Hashtable<String, Double>();
        
        for (int i=0; i<selected.length; i++) {
            
        	String boundID = app.workConf.getAtomName(sel)+app.workConf.getAtomName(selected[i]);
        	
        	if (!boundData.containsKey(boundID)) {
        		
        		        		// Distanza
        		String ans = GUItools.getStrFormUser(
								"Distance (A)" +app.workConf.getAtomName(sel)+"-"+app.workConf.getAtomName(selected[i]), 
								"1.0");
        		if (ans==null) return;
        		
        		double length = Double.parseDouble(ans);
        					

        		boundData.put(boundID, length);
        		
        	}
        }

        //----------------------------------
        //  Calculate the value for each species-species relation
        //----------------------------------
        double coord = 0;
        
        for (int i=0; i<selected.length; i++) {
        	
        	String boundID = app.workConf.getAtomName(sel)+app.workConf.getAtomName(selected[i]);
        	
        	coord += app.workConf.getAtomsDistancePBC(sel, selected[i], Costants.cANGSTROM) / boundData.get(boundID);
        	
        	app.outputPlugin("Coord. norm. ("+(sel+1)+")", ""+coord, true);
        	
        }
//        // Load the vectors for the calulation
//        double[] num = new double[species.size()];
//        int[] elements = new int[species.size()];
//
//        for (int i=0; i<species.size(); i++) {
//            num[i] = 0;
//            elements[i] = 0;
//        }
//
//        // Loop trought species
//        for (int i=0; i<inside.length; i++) {
//
//            int idSpec = ArrayTools.whereIsInArray(species, app.workConf.getAtomElement(inside[i]));
//
//            double dist = app.workConf.getAtomsDistancePBC(sel, inside[i], Costants.cANGSTROM);
//
//            num[idSpec] += normRadius[idSpec] / dist ;
//            System.out.println("Add: "+dist+"/"+normRadius[idSpec]+" with "+inside[i]);
//            elements[idSpec] ++;
//        }
//
//        for (int i=0; i<species.size(); i++) {
//
//            app.outputPlugin("Coord. with ["+species.get(i)+"]", ""+(num[i]), (i==0) ? true : false);
//        }


        //app.clearSelection();

    }

    public boolean selectAtomsInShell(int sel) {

        int[] closer = app.workConf.getAtomsInShell(sel, nCloserAtoms, sensitivity);
        app.clearSelection();
        app.selectAtoms(closer);
        
        return true;	//override selection
        
    }

    public boolean selectAtomsInsideRadius(int sel) {

        int[] inside;

        //----------------------------------
        //  Find the atoms inside the radius
        //----------------------------------
        if (radiusType==Costants.RADIUS_COVALENT) {
            inside = app.workConf.getAtomsInRadius(sel, app.status.atomsElements, app.status.atomsCovRadii);
        } else if (radiusType==Costants.RADIUS_VDW) {
            inside = app.workConf.getAtomsInRadius(sel, app.status.atomsElements, app.status.atomsVdwRadii);
        } else {

            double[] rmanual = new double[app.status.atomsElements.length];
            for (int i=0; i<rmanual.length; i++) rmanual[i] = manualRadius;

            inside = app.workConf.getAtomsInRadius(sel, app.status.atomsElements, rmanual);
        }

        
        app.clearSelection();
        app.selectAtoms(inside);
        
        return true;	//override selection

    }


    
    //----------------------
    //  Funzioni del modulo
    //----------------------

    public void confChanged() {
        
    }	
	
	class ClosingWindowAdapter extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			setVisible(false);
			System.exit(0);
		}
	}

	/* (non-Javadoc)
	 * @see MokaPLUGIN#reciveSelection()
	 */
	public boolean isReadyToReciveSelection() {
		// TODO Auto-generated method stub
		return readyToReciveSelection;
	}


	/* (non-Javadoc)
	 * @see MokaPLUGIN#getAtomProperty(int)
	 */
	public Object getAtomProperty(int atomSelected) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see MokaPLUGIN#getConfProperty(int)
	 */
	public Object getConfProperty(int _property) {
		// TODO Auto-generated method stub
		return null;
	} 

	
}
