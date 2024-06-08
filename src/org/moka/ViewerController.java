package org.moka;


import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.DecimalFormat;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

import org.moka.common.Costants;

import org.moka.structures.AtomData;
import org.moka.structures.Module;
import org.moka.structures.MokaController;
import org.moka.tools.ArrayTools;
import org.moka.tools.MathTools;
import org.moka.tools.events.mouseAcceptor;
import org.moka.tools.events.mouseReceiver;
import org.moka.tools.gui.GUItools;
import org.moka.tools.gui.JMultilineLabel;
import org.moka.tools.gui.SizedTextField;

/**
 * @author riki
 *
 */
public class ViewerController extends JPanel implements  mouseReceiver, ActionListener, ItemListener, Module, MokaController {

	Moka app;
	
	//Per conf
//	JButton confPrev, confNext;
//	JTextField thisConf;
//	JLabel totConfs;
	
	//Per status
    //JLabel statusCell, statusAtoms, statusCoord;
//	JTextArea infoArea;
//	JTextArea plugInArea;
	
	//Per view
	JButton viewXY,viewYZ,viewXZ;
	public Checkbox drawCell, drawAxes, pcbRefold;
	public JTextField repetitions;

	JComboBox colorPropertyBox;
	JSlider slideColorMax,slideColorMin;
	JButton colorizeB;
	
	boolean isCtrlPressed = false;
	boolean isActive = false;
	
	//int barWidth = 150, barHeigth = 400;
	
	public ViewerController(Moka _app) {

//		this.setFloatable(true);
//        this.setRollover(true);
//        this.setSize(150,400);
		
		this.app = _app;
		this.setLayout(new BorderLayout());
		
		Box viewerControllerP = Box.createVerticalBox();
		
		Box colorB = Box.createVerticalBox();
		
		//----------------------------------------
		//	Pannello per VIEWER
		//----------------------------------------
		
		Box colorB2 = Box.createHorizontalBox();
		colorPropertyBox = new JComboBox(new String[] {"x","y","z"});
		colorPropertyBox.setSelectedIndex(0);
		colorB2.add(colorPropertyBox);
		
		colorizeB = GUItools.makeNavigationButton("org/moka/images/Export16.gif", "colorizeB", "Colorize", "colorizeB",this);
		colorizeB.addActionListener(this);
		colorB2.add(colorizeB);
		colorB.add(colorB2);
		
		Box colorBslide = Box.createHorizontalBox();
		slideColorMin = new JSlider(JSlider.HORIZONTAL,0, 100, 0);
		colorBslide.add(slideColorMin);
		
		slideColorMax = new JSlider(JSlider.HORIZONTAL,0, 100, 100);
		colorBslide.add(slideColorMax);
		colorB.add(colorBslide);
		
		viewerControllerP.add(GUItools.createPanelForComponent(colorB, "Color", new Dimension(app.status.rightBarWidht,100)));
		
		//----------------------------------------
		//	Pannello per VIEWER
		//----------------------------------------
		Box viewB = Box.createVerticalBox();
		
		Box typeViewBox = Box.createHorizontalBox();
		viewXY= new JButton("xy");
		viewXY.setActionCommand("viewXY");
		viewXY.addActionListener(this);
		//viewXY.setPreferredSize(new Dimension(30,30));
		//viewXY.setMinimumSize(new Dimension(55,40));
		typeViewBox.add(viewXY);
		
		viewXZ= new JButton("xz");
		viewXZ.setActionCommand("viewXZ");
		viewXZ.addActionListener(this);
		//viewXZ.setMinimumSize(new Dimension(55,40));
		typeViewBox.add(viewXZ);
		
		viewYZ= new JButton("yz");
		viewYZ.setActionCommand("viewYZ");
		viewYZ.addActionListener(this);
		//viewYZ.setMinimumSize(new Dimension(55,40));
		typeViewBox.add(viewYZ);
		
		viewB.add(typeViewBox);
		
		Box checkViewBox = Box.createVerticalBox();
		checkViewBox.add(Box.createHorizontalGlue());
		drawCell = new Checkbox("Cell", null, true);
		drawCell.addItemListener(this);
		checkViewBox.add(Box.createVerticalGlue());
		checkViewBox.add(drawCell);
		checkViewBox.add(Box.createVerticalGlue());
		
		drawAxes = new Checkbox("Axes", null, false);
		drawAxes.addItemListener(this);
		checkViewBox.add(drawAxes);
		checkViewBox.add(Box.createVerticalGlue());
		
        pcbRefold = new Checkbox("PBC", null, false);
		pcbRefold.addItemListener(this);
		checkViewBox.add(pcbRefold);
		checkViewBox.add(Box.createVerticalGlue());
        
		viewB.add(checkViewBox);
		
		Box repBox = Box.createHorizontalBox();
		repBox.add(new JLabel("Rep:"));
		
		repetitions = new JTextField("0 0 0");
		repetitions.addActionListener(this);
		repetitions.setMaximumSize(new Dimension(60,20));
		repBox.add(repetitions);
		viewB.add(repBox);
		
		
		viewerControllerP.add(GUItools.createPanelForComponent(viewB, "View", new Dimension(app.status.rightBarWidht,150)));
		
		//-----------in FONDO
		viewerControllerP.add(Box.createVerticalGlue());
		
		
//		//----------------------------------------
//		//	Pannello selezione CONF ATOMS
//		//----------------------------------------
//        
//		Box stepPanel = Box.createHorizontalBox();
//
//		
//		confPrev = makeNavigationButton("Back16.gif", "confPrev", "Prev conf", "Prev");
//		confPrev.addActionListener(this);
//		//confPrev.setPreferredSize(new Dimension(30,50));
//		stepPanel.add(confPrev);
//		
//		thisConf = new JTextField("0");
//		thisConf.addActionListener(this);
//		stepPanel.add(thisConf);
//		
//		totConfs = new JLabel("/ 0");
//		stepPanel.add(totConfs);
//		
//		confNext = makeNavigationButton("Forward16.gif", "confNext", "Next conf", "Next");
//		confNext.addActionListener(this);
//		//confNext.setPreferredSize(new Dimension(30,50));
//		stepPanel.add(confNext);
//		
//		viewerControllerP.add(GUItools.createPanelForComponent(stepPanel, "Confs", new Dimension(barWidth,50)));
		

		//----------------------------------------
		//	Status box
		//----------------------------------------
//		controllerP.add(Box.createVerticalGlue());
//
//	    Box statusBox = Box.createHorizontalBox();
//        statusBox.add(Box.createHorizontalGlue());
//        
//        statusCell = new JLabel("Cell");
//        statusCell.setOpaque(true);
//        //statusCell.setBackground(Color.RED);
//        statusCell.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.red));
//        statusBox.add(statusCell);
//        statusBox.add(Box.createHorizontalGlue());
//        
//        statusAtoms = new JLabel("Atoms");
//        statusAtoms.setOpaque(true);
//        statusAtoms.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.red));
//        statusBox.add(statusAtoms);
//        statusBox.add(Box.createHorizontalGlue());
//        
//        statusCoord = new JLabel("None");
//        statusCoord.setBackground(Color.RED);
//        statusBox.add(statusCoord);
//        statusBox.add(Box.createHorizontalGlue());
//        controllerP.add(GUItools.createPanelForComponent(statusBox,"Status", new Dimension(200,55)));
        
		//----------------------------------------
		//	Inserimento e fine
		//----------------------------------------
		
		this.add(viewerControllerP, BorderLayout.CENTER);
		
		
	}
	

	public void actionPerformed(ActionEvent e) {
		
		if (app.status.isAppWaiting()) return;
		
		if (e.getSource() == viewXY) { app.viewer.setView(0); }
		if (e.getSource() == viewXZ) { app.viewer.setView(1); }
		if (e.getSource() == viewYZ) { app.viewer.setView(2); }
	
		if (e.getSource() == repetitions) {

			Pattern pattern = Pattern.compile("([0-9])+(\\ )+([0-9])+(\\ )+([0-9])+");
			Matcher matcher = pattern.matcher(repetitions.getText().trim());
			
			app.status.setRepetitions(app.io.parseIntArray(repetitions.getText()));
			app.viewer.refreshDraw(false);
			this.reset();
		}
		
		if (e.getSource() == colorizeB) {
			
			if (app.workConf==null) return;
			
			int propertySelected = colorPropertyBox.getSelectedIndex();
			
			double[] property = new double[app.workConf.getNAtoms()];
			
			for (int i =0; i<app.workConf.getNAtoms(); i++) {
				property[i] = app.workConf.getAtomPos(i)[propertySelected];
			}
			
			double propMin = ArrayTools.getMin(property);
			double propMax = ArrayTools.getMax(property);
			
			double colorMin = propMin + ((propMax-propMin)/100)*(slideColorMin.getValue());
			double colorMax = propMax - ((propMax-propMin)/100)*(100-slideColorMax.getValue());
		
			
			app.viewer.colorByProperty(property, colorMin, colorMax);
			
		}
		
    }
	
	public void doActionOnMouse(MouseEvent e, int eventType, int src) {

		int site = app.viewer.findNearestAtomIndex( e.getX(), e.getY() );
		if ( site == -1 ) { return ; }
		
//		if (app.mousePrecedence!=null) {
//			((Module)app.mousePrecedence).reciveMouseSelection(site);
//			return;
//		}
//		
		
		
		if ((eventType == MOUSE_CLICKED)) { //type 0 e' il canale del mouse
		
			
			//SELEZIONA
//			if (status.getEditingMode()==Costants.mNORMAL || status.getEditingMode()==Costants.mTRANSFORM) {
			if (site>=0 && site<app.workConf.getNAtoms()){

				
                //
                // Il picking per i plug-in e' gestito dal mouse
                //
                boolean overrideSelection = sendSelectionToPlugins(site);
                if (overrideSelection) {
                	app.log("-----Selection interrupted by plugin-----");
                	return;
                }
				
                //
                // Caso selezione, senza maschera o con CTRL
                //
                if (e.getModifiersEx()== MouseEvent.NOBUTTON ) {
                	app.selectAtom(site,false);
                	
                }
                else if (e.getModifiersEx() == MouseEvent.CTRL_DOWN_MASK )
                {

                    if (app.workConf.isAtomSelected(site)){

                        app.unSelectAtom(site);

                    } else {

                        app.selectAtom(site,true);

                    }

                    // In ogni caso aggiorna la tabella
                    //app.tableFrame.selectTable();

                }

                //
                //  Caso bloccaggio
                //
                else if (e.getModifiersEx()== MouseEvent.ALT_DOWN_MASK) {

                }

                //
                //  Caso eliminazione
                //
                else if (e.getModifiersEx()== MouseEvent.BUTTON1_DOWN_MASK) {

                }

                // Sicuramente e' l'atomo seleziona, opi aggiungo che azione e' stata fatta
                String toLog = "Atom "+(site+1)+" "+app.workConf.getAtomName(site)+" [";
                toLog += String.format(Costants.decimalPlace4,app.workConf.getAtomPos(site)[0])+" ";
                toLog += String.format(Costants.decimalPlace4,app.workConf.getAtomPos(site)[1])+" ";
                toLog += String.format(Costants.decimalPlace4,app.workConf.getAtomPos(site)[2])+" ";
                toLog += "] selected.";
                app.log(toLog);
                
			} else {
				app.log("Atom not selectable");
				
			}

			
			//BLOCCA
//			if (status.getEditingMode()==Costants.mBLOCK) {
//				
//				if (site>=0 && site<workConf.getNAtoms()){	//Atomo da bloccare
//					
//				
//						blockAtom(site);
//						viewer.drawAtomsColors(workConf);
//						
//					} else {
//						log("Atom not selectable");
//			
//					}
//			}
			//CANCELLA					
//			if (status.getEditingMode()==Costants.mDELETE) {
//
//					if (site>=0 && site<workConf.getNAtoms()){	//Atomo da cancellare
//		
//						int[] toDel = {site};
//						delAtom(toDel);						//Aggiungere la richiesta di conferma
//						
//						//tableFrame.tableAllArray();
//						//viewer.refreshDraw();
//						
//						viewer.refreshDraw(true);
//						
//						
//					} else {
//						log("Atom not selectable");
//					
//					}
//			
//			}
			
			
		}
	}
	
	public boolean sendSelectionToPlugins(int site) {
		
		boolean out = false; //Override the regular selection process
		
		for (int i =0; i<app.plugins.length; i++) {
			if (app.plugins[i]!=null) {
       			if (app.plugins[i].isReadyToReciveSelection()) {
       				out = out || app.plugins[i].receiveSelection(site);
				}
			}
		}
		
		return out;
		
	}
	
	
	public void itemStateChanged(ItemEvent e) {
		if (app.status.isAppWaiting()) return;
		
	    Object source = e.getItemSelectable();

	    if (source == drawCell) {
	        if (drawCell.getState()) {app.showCell = true;}
	        if (!drawCell.getState()) {app.showCell = false;}
	        app.viewer.refreshDraw(true);
	        
	    } else if (source == drawAxes) {
	    	if (drawAxes.getState()) {app.viewer.execCommand("setAxes on");}
	        if (!drawAxes.getState()) {app.viewer.execCommand("setAxes off");}

        } else if (source == pcbRefold) {
	    	if (pcbRefold.getState()) {app.status.refold = true;}
	        if (!pcbRefold.getState()) {app.status.refold = false;}

            app.viewer.refreshDraw(true);
        }



	}
	
	//---------------------------------
	//	FUNZIONE DEL MODULO 
	//---------------------------------

	public void reset() {
				
		repetitions.setText(app.status.getRepetitions()[0] + " "+ app.status.getRepetitions()[1] +" "+app.status.getRepetitions()[2]);
		
	}
	
	public void colorAtoms() {
		// TODO Auto-generated method stub
		
	}
	
	public void confChanged() {
		
	}
	
	public void setActive(boolean status) {
		// TODO Auto-generated method stub
		if (status) {
			app.viewer.removeAllMouseListeners();
			app.viewer.addMouseListener(new mouseAcceptor(this,0));
		} else {
			app.viewer.removeAllMouseListeners();
		}
		
		isActive = status;
	} 
	
	public boolean isActive() {
		return isActive;
	}
	
	public void atomsChanged(int[] listChanged) {
		// TODO Auto-generated method stub
		
	} 
	
	public void reciveMouseSelection(int site) {
		// TODO Auto-generated method stub
		
	} 
	
	class ClosingWindowAdapter extends WindowAdapter {
		 public void windowClosing(WindowEvent e) {
		  setVisible(false);
		  System.exit(0);
		 }
		}





	
}
