package org.moka.plugins;



import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.moka.qeReader.qeParser;
import org.moka.common.Costants;
import org.moka.GraphXY;
import org.moka.Moka;

import org.moka.structures.Plugin;
import org.moka.tools.ArrayTools;

import org.moka.tools.*;
import org.moka.tools.gui.CheckDialog;

public class PjwfcPLUGIN extends JFrame implements ActionListener, Plugin{

	Moka app;
	PjwfcConfiguration pjWfc;
	JFileChooser fc;
	
	boolean readyToReciveSelection = false;
	//newAreaPlot areaStacked;
	
	int[] orbitalsSelected; 
    //double eFermi=0; 
    boolean spin=false;
    //----------------------
	//	CARATTERI / SEPARATORI
	//----------------------	
	String[] orbitalsList = {"s","p","d","f","_all_"};
	
    ///---------------------------
    /// CONSTRUCTOR	(bottoni, pannelli)
    ///---------------------------
	public PjwfcPLUGIN(Moka _app) {
		
		this.app = _app;
		fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
	}

    ///---------------------------
    /// CREA INTERFACCIA
    ///---------------------------

	public  JMenu makePluginMenu() {
		 
		 JMenu mPjwfc;
		 JMenuItem mImport, mPlot,mPlotSel, mCalc, mPrint,mColor, mSetMouse;
		 
		 mPjwfc = new JMenu("ProjWFC");
		 
		 mImport = new JMenuItem("Import...");
		 mImport.addActionListener(this);
		 mImport.setActionCommand("mImport");
		 mPjwfc.add(mImport);

		 mPlot = new JMenuItem("Plot all");
		 mPlot.addActionListener(this);
		 mPlot.setActionCommand("mPlot");
		 mPjwfc.add(mPlot);
		 
		 mPlotSel = new JMenuItem("Plot selection...");
		 mPlotSel.addActionListener(this);
		 mPlotSel.setActionCommand("mPlotSel");
		 mPjwfc.add(mPlotSel);
		 
		 mCalc = new JMenuItem("Calculations...");
		 mCalc.addActionListener(this);
		 mCalc.setActionCommand("mCalc");
		 mPjwfc.add(mCalc);
		
		 mPrint = new JMenuItem("Print band...");
		 mPrint.addActionListener(this);
		 mPrint.setActionCommand("mPrint");
		 mPjwfc.add(mPrint);
		 
		 mColor = new JMenuItem("Color atoms...");
		 mColor.addActionListener(this);
		 mColor.setActionCommand("mColor");
		 mPjwfc.add(mColor);

         mSetMouse = new JMenuItem("Set/Unset mouse selection");
		 mSetMouse.addActionListener(this);
		 mSetMouse.setActionCommand("mSetMouse");
		 mPjwfc.add(mSetMouse);


		 return mPjwfc;
	        
	}
	
	public void actionPerformed(ActionEvent e) {

		String buttClicked = e.getActionCommand(); //getClassName(e.getSource().getClass());

		//------------------
		// menu FILE
		//------------------
		if (buttClicked.equals("mImport")) {
		    
			if (app.status.getWorkingDir()!=null) fc.setCurrentDirectory(new File(app.status.getWorkingDir()));
			
			if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {            	
				File filePJWFC = fc.getSelectedFile();
		
				app.status.startWaiting();
				try {
					//------------------
					// Apertura file OUT
					//------------------
					
					pjWfc = qeParser.readFilePJWFC(filePJWFC.getAbsolutePath());
					app.status.setWorkingOff();
					
					
					app.log("Funzioni onda importante correttamente, n: "+pjWfc.orbitals.size());
					if (app.workConf!=null) this.readyToReciveSelection = true;
		
				} catch (NumberFormatException e1) {
					app.log("Errore formato numero.");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					app.log("Errore lettura file.");
				}
				app.status.stopWaiting();
			}
			
			resetArrays();
		
		} 
		
		if (buttClicked.equals("mColor")) {
			colorAtomsCharge();
		}
		
		if (buttClicked.equals("mPlot")) {
        	
			resetArrays();
			plotAll();
		}

		if (buttClicked.equals("mPlotSel")) {
        	
			resetArrays();
        	askData();
        	
        	if (app.workConf==null) { app.log("No atoms selected"); return;}
        	plotSelected(app.workConf.getAtomsFlagged(Costants.aSELECTED));

		}
		
		if (buttClicked.equals("mCalc")) {
			
			resetArrays();
			askData();
		
			if (app.workConf==null) { app.log("No atoms selected"); return;}
			printData(app.workConf.getAtomsFlagged(Costants.aSELECTED));
		
			
		}
		
		if (buttClicked.equals("mPrint")) {
			
			resetArrays();
			askData();
			
			if (app.workConf==null) { app.log("No atoms selected"); return;}
			printOrbitalsSelected(app.workConf.getAtomsFlagged(Costants.aSELECTED));
			
			
		}
		
        if (buttClicked.equals("mSetMouse")) {
			
			
			readyToReciveSelection = !readyToReciveSelection;
            if (readyToReciveSelection) {
                app.log("PJWFC plugin reciving selections.");
            } else {
                app.log("PJWFC plugin STOPPED selections.");
            }
			
		}


		
    }
	
    ///---------------------------
    /// DATA TOOLS
    ///---------------------------
	
	public double getEfermi() {
		double eFermi = (Double)app.workConf.getData().getDataByName("eFermi");
		if (eFermi==(Double)app.workConf.getData().dataNull(Double.class)) {
			eFermi=0;
		}
		
		return eFermi;
	}
	
	public void resetArrays() {
		
		orbitalsSelected = ArrayTools.arrayNewFill(orbitalsList.length, 0);
		
	}
	
	public void askData(){
		
    	//Seleziona gli orbitali in input
		
		
		orbitalsSelected = CheckDialog.showDialog(app.frame,null,"Select orbitals","Orbital",
				orbitalsList,
				orbitalsList.length-1);
		
		if (orbitalsSelected[orbitalsSelected.length-1]==1) {
    		orbitalsSelected = ArrayTools.arrayNewFill(orbitalsList.length-1, 1);
    	} 
    	
	}
	
	public double[] calcAreaCenterWidth(int atom, int orbital) {
		
		PjwfcOrbital proj = pjWfc.getAtomAllOrbital(atom)[orbital];
		
		double eFermi = getEfermi();
		
		double[] energy = proj.getEnergy();
		double[] occupation = proj.getProjection();
		
		
		double area = 0;
		for (int i=0; i<energy.length-1; i++){
			area+=occupation[i] * (energy[i+1]-energy[i]);
		}
		
		
		double center = 0;
		for (int i=0; i<energy.length-1; i++){
			center+=((occupation[i]*(energy[i]-eFermi)) * (energy[i+1]-energy[i]))/area;
		}
		
		
		double width = 0;
		for (int i=0; i<energy.length-1; i++){
			width+=((occupation[i]*Math.pow((energy[i]-eFermi-center),2)) * (energy[i+1]-energy[i]))/area;
		}
		
		return new double[] {area,center,width};
	}
	
	//---------------------------------
	//	PRINTERS
	//---------------------------------	

	public void printData(int[] _atomsSelected){
		
		double eFermi = getEfermi();
		
		app.output("Name"+Costants.separatorTAB+" Efermi"+Costants.separatorTAB+"area"+Costants.separatorTAB+"center"+Costants.separatorTAB+"width\n", true);
		
		for (int idAtomo=0; idAtomo<_atomsSelected.length; idAtomo++) {
			
			for (int j=0; j<pjWfc.getAtomAllOrbital(idAtomo).length; j++) {
				
				if (orbitalsSelected[pjWfc.getAtomAllOrbital(_atomsSelected[idAtomo])[j].getOrbitalIndex()]==1) {
					
					PjwfcOrbital proj = pjWfc.getAtomAllOrbital(_atomsSelected[idAtomo])[j];
					
					if (proj!=null) {
					
						double[] acw =  calcAreaCenterWidth(_atomsSelected[idAtomo], j);
						
						String line = app.workConf.getAtomName(_atomsSelected[idAtomo])+""+(_atomsSelected[idAtomo]+1)+"["+AtomTools.getPjwfcType(proj.getOrbitalIndex())+"]"+Costants.separatorTAB;
						line+=String.format(Costants.decimalPlace8,eFermi)+Costants.separatorTAB;
						line+=String.format(Costants.decimalPlace8,acw[0])+Costants.separatorTAB;
						line+=String.format(Costants.decimalPlace8,acw[1])+Costants.separatorTAB;
						line+=String.format(Costants.decimalPlace8,acw[2])+Costants.separatorTAB;
						
						app.output(line+"\n");
					}
				}
			}
		
		}
		
		
		
//		newAreaPlot plot = new newAreaPlot("prova",ArrayTools.arrayAdd(xVect,-1.0*eFermi));
//		
//		new newAreaPlot.plotData(occupation, tempPlot.getName()+"["+(i+1)+"] "+AtomTools.getPjwfcType(tempPlot.getOrbital()));
	}
	
	public void printOrbitalsSelected(int[] _atomsSelected) {
		
		double eFermi = getEfermi();
		
		app.output("--------------------------------------------\n", true);
		app.output("# Zero shifted at the fermi energy: "+eFermi+"\n");
		app.output("--------------------------------------------\n\n");
		
		String line = "";
		
		line+="#E"+Costants.separatorTAB;
		for (int i=0; i<_atomsSelected.length; i++) {
			
			for (int j=0; j<orbitalsSelected.length; j++) {
				if (orbitalsSelected[j]==1) {
					
					PjwfcOrbital tempPlot = pjWfc.getAtomOrbital(_atomsSelected[i], j);
					
					if (tempPlot!=null)
						line+=app.workConf.getAtomName(_atomsSelected[i])+_atomsSelected[i]+AtomTools.getPjwfcType(tempPlot.getOrbitalIndex())+Costants.separatorTAB;
				}
			}
		
		}
		line+=Costants.newline;
		app.output(line);
		
		
		for (int de=0; de<pjWfc.getEnergy(0).length; de++) {
			
			line=(pjWfc.getEnergy(0)[de]-eFermi)+Costants.separatorTAB;
			
			for (int i=0; i<_atomsSelected.length; i++) {
				
				for (int j=0; j<orbitalsSelected.length; j++) {
					if (orbitalsSelected[j]==1) {
						
						PjwfcOrbital tempPlot = pjWfc.getAtomOrbital(_atomsSelected[i], j);
						
						if (tempPlot!=null)
							line+=String.format(Costants.decimalPlace8,tempPlot.getProjection()[de])+Costants.separatorTAB;
						
					}
				}
			}
			line+=Costants.newline;
			app.output(line);
			
		}
		
		
	}
	
	public void printListImported() {

		//Stampa la lista di atomi e relativi orbitali
		String orbN = "";

		for (int i =0; i<pjWfc.orbitals.size(); i++){

			orbN = pjWfc.orbitals.get(i)[0].getAtomName();
			app.output("["+(i+1)+"] "+orbN+" (");

			for (int j =0; j<pjWfc.orbitals.get(i).length; j++){
				app.output(AtomTools.getPjwfcType(pjWfc.orbitals.get(i)[j].getOrbitalIndex()));
			}
			app.output(")"+Costants.newline);
			
		}

	}
	
	///---------------------------
    /// PLOTTER
    ///---------------------------	
	
	public void plotAll() {
		
		PjwfcOrbital temp = pjWfc.orbitals.get(0)[0];
		
		double eFermi = getEfermi();
		
		double[] xVect = temp.getEnergy();
		
		GraphXY graph = new GraphXY(0,600,400);
		
		
		for (int i=0; i<pjWfc.orbitals.size(); i++){
			//
			for (int j=0; j<pjWfc.orbitals.get(i).length; j++) {
				//
				PjwfcOrbital tempPlot = pjWfc.orbitals.get(i)[j];
				if (tempPlot!=null)
					graph.addSeriesXY(MathTools.arrayAdd(xVect,-1.0*eFermi),tempPlot.getProjection(), Color.black, tempPlot.getAtomName()+"["+(i+1)+"] "+AtomTools.getPjwfcType(tempPlot.getOrbitalIndex()));
			}
		}


		graph.addSeriesXY(new double[] {eFermi,eFermi}, new double[]{graph.plotter.getMinValue(1),graph.plotter.getMaxValue(1)}, Color.red, "Ef");
		
		graph.createAndShowGUI();
	}
	
	public void plotSelected(int[] _atomsSelected) {
		
		
		PjwfcOrbital temp = pjWfc.orbitals.get(0)[0];
		double[] xVect = temp.getEnergy();
		double eFermi = getEfermi();
		
		GraphXY graph = new GraphXY(0,600,400);
		
		for (int i=0; i<_atomsSelected.length; i++) {
			
			for (int j=0; j<orbitalsSelected.length; j++) {
				if (orbitalsSelected[j]==1) {
					
					PjwfcOrbital tempPlot = pjWfc.getAtomOrbital(_atomsSelected[i], j);
					if (tempPlot!=null)
						graph.addSeriesXY(xVect, 
										tempPlot.getProjection(), 
										tempPlot.getAtomName()+"["+(_atomsSelected[i]+1)+"] "+AtomTools.getPjwfcType(tempPlot.getOrbitalIndex()));
				}
			}
		
		}
		
		graph.addSeriesXY(new double[] {eFermi,eFermi}, new double[]{graph.plotter.getMinValue(1),graph.plotter.getMaxValue(1)}, Color.red, "Ef");
		
		graph.createAndShowGUI();
	}

	public void colorAtomsCharge(){

		if (pjWfc==null) return; //PJWFC
		
		double[] property = new double[app.workConf.getNAtoms()];
		for (int i =0; i<app.workConf.getNAtoms(); i++) {
			Double pr = (Double)getAtomProperty(i);
			if (pr==null) pr = 0.0;
			
			property[i] = pr;
		}
		
		app.viewer.colorByProperty(property,null,null);
		
		app.log("Colored as atoms charge. Min:"+ArrayTools.getMin(property)+" Max:"+ArrayTools.getMax(property));
		
	}
		
	///---------------------------
    /// PLOTTER
    ///---------------------------	
	
	public boolean receiveSelection(int atomSelected) {

        if (!readyToReciveSelection) return false;

		if (pjWfc==null) return false;

        if (atomSelected>pjWfc.orbitals.size()) return false;
		
		//atomSelected--;
		
		app.outputPlugin("Charge", pjWfc.getTotCharge(atomSelected).toString(), true);
		app.outputPlugin("","",false);
		
		for (int i=0; i<pjWfc.getAtomAllOrbital(atomSelected).length; i++) {
			
			app.outputPlugin(pjWfc.getAtomAllOrbital(atomSelected)[i].getOrbitalName(), ""+pjWfc.getAtomAllOrbital(atomSelected)[i].getCharge(), false);
			
			if (pjWfc.getAtomAllOrbital(atomSelected)[i].isSpin()) {
				//app.outputPlugin(pjWfc.getAtomAllOrbital(atomSelected)[i].getOrbitalName(), ""+(pjWfc.getAtomAllOrbital(atomSelected)[i].getChargeSpin(0)+ pjWfc.getAtomAllOrbital(atomSelected)[i].getChargeSpin(1)), false);
				//app.outputPlugin(pjWfc.getAtomAllOrbital(atomSelected)[i].getOrbitalName()+" spin pol.", ""+((pjWfc.getAtomAllOrbital(atomSelected)[i].getChargeSpin(0)-pjWfc.getAtomAllOrbital(atomSelected)[i].getChargeSpin(1))/pjWfc.getAtomAllOrbital(atomSelected)[i].getCharge()), false);
			}
			
		}
		
		app.outputPlugin("","",false);
		
		for (int i=0; i<pjWfc.getAtomAllOrbital(atomSelected).length; i++) {
			
			app.outputPlugin(pjWfc.getAtomAllOrbital(atomSelected)[i].getOrbitalName(),"",false);
			
			double[] acw =  calcAreaCenterWidth(atomSelected, i);
			app.outputPlugin("area",String.format(Costants.decimalPlace8,acw[0]),false);
			app.outputPlugin("center",String.format(Costants.decimalPlace8,acw[1]),false);
			app.outputPlugin("width",String.format(Costants.decimalPlace8,acw[2]),false);
			
		}
		
		
		app.outputPlugin("","",false);
		for (int i=0; i<pjWfc.getAtomAllOrbital(atomSelected).length; i++) {
			
			if (pjWfc.getAtomAllOrbital(atomSelected)[i].isSpin()) {
				
				double pol = ((pjWfc.getAtomAllOrbital(atomSelected)[i].getChargeSpin(0)-pjWfc.getAtomAllOrbital(atomSelected)[i].getChargeSpin(1))/pjWfc.getAtomAllOrbital(atomSelected)[i].getCharge());
				app.outputPlugin(" spin pol. "+pjWfc.getAtomAllOrbital(atomSelected)[i].getOrbitalName(), String.format(Costants.decimalPlace4, pol), false);
				
			}
		}	
		
		return false;
	} 	
	
	public boolean isReadyToReciveSelection() {
		// TODO Auto-generated method stub
		return readyToReciveSelection;
	}

	public Object getAtomProperty(int atomSelected) {
		// TODO Auto-generated method stub
		if (pjWfc==null) return null;
		if (atomSelected>pjWfc.orbitals.size()) return null;
		
		return pjWfc.getTotCharge(atomSelected);
	}
	
	public Object getConfProperty(int _property) {
		// TODO Auto-generated method stub
		return null;
	}

    public void confChanged() {

        pjWfc = null;
        readyToReciveSelection = false;
        
    }
	
	
	class ClosingWindowAdapter extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			setVisible(false);
			System.exit(0);
		}
	}

	
}
