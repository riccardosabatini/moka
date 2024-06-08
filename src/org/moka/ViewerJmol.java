package org.moka;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.vecmath.Point3f;

import org.jmol.api.JmolViewer;

import org.moka.common.Costants;

import javax.media.j3d.Transform3D;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import org.moka.structures.Configuration;
import org.moka.structures.Drawable;
import org.moka.structures.MokaController;
import org.moka.structures.Viewer;
import org.moka.tools.ArrayTools;
import org.moka.tools.CellTools;
import org.moka.tools.MathTools;
import org.moka.tools.gui.JMolPanelAdv;

/**
 * @author riki
 *
 */
public class ViewerJmol implements Viewer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Moka app;
	
	JPanel javaPanel;
	JMolPanelAdv jmolPanel;
	JmolViewer jmolViewer;
	//Hashtable<String, String> viewPoint;


    String[] paintedAtomsElemen = new String[1];
    double[][] paintedAtomsPos = new double[1][3];

    boolean cellPainted = false;
    int atomsPainted = 0;
    int objPainted = 0;

	//int[] app.status.getRepetitions() = new int[] {0,0,0};
	
	///---------------------------
    /// Per interfaccia
    ///---------------------------	
	
	public ViewerJmol (Moka _app) {

		//super();
		javaPanel = new JPanel();
		javaPanel.setLayout(new BorderLayout());
		
		this.app = _app;

        //-------------------------
        //JMOL APPLET
		//-------------------------

		jmolPanel = new JMolPanelAdv();
        jmolViewer = jmolPanel.getViewer();
		javaPanel.add(jmolPanel,  BorderLayout.CENTER);

		//-------------------------
        //Pannello sotto viewer
        //-------------------------
        
        Box downViewer = Box.createHorizontalBox();
        downViewer.add(Box.createHorizontalGlue());

        //----------------------------------------
		//	INIZIALIZZA
		//----------------------------------------
        safeEval("zap");
		safeEval("set scriptQueue ON");

	}
	
	public JComponent getViewerPanel() {
		return this.javaPanel;
	}
	
	///---------------------------
    /// FUNZIONI DI INVIO COMANDI
    ///---------------------------	

    public void waitUntilFree() {

        while (jmolViewer.isScriptExecuting()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

    }

//	public void safeOpenStringInline(String _comm) {
//
//        jmolViewer.openStringInline(_comm);
//				
//	}
	
	public void safeEval(String _comm) {

        jmolViewer.script(_comm);

	}

    public void execCommand(String _in){

		String[] parts = _in.trim().split("\\s+");
		int numArgs = _in.trim().split("\\s+").length -1;

		String commStart = parts[0];
		String[] args = new String[numArgs];
		for (int i=0; i<numArgs; i++) {
			args[i] = parts[i+1];
		}

//		//--------------------
//		//	Comando RIPETIZIONI
//		//--------------------
//		if (commStart.equals("setapp.status.getRepetitions()")) {
//
//			if (args.length==3) {
//				app.status.getRepetitions()[0] = Integer.parseInt(args[0]);
//				app.status.getRepetitions()[1] = Integer.parseInt(args[1]);
//				app.status.getRepetitions()[2] = Integer.parseInt(args[2]);
//			}
//			return;
//		}
//
		//--------------------
		//	Comando ASSI
		//--------------------
		if (commStart.equals("setAxes")) {

			if (args[0].toLowerCase().equals("on")) {
				safeEval("axes ON; axes MOLECULAR; ");
			} else if (args[0].toLowerCase().equals("off")) {
				safeEval("axes OFF; axes MOLECULAR; ");
			}
			return;
		}

		//--------------------
		//	Comando VISUALIZZAZIONE
		//--------------------
		if (commStart.equals("shiftView")) {

			double x = Double.parseDouble(getMyProperty("trX"));
			double y = Double.parseDouble(getMyProperty("trY"));

			String command = "translate ";

			if (args[0].equals("x") && args[1].equals("m")) { command += "x "+(x-Integer.parseInt(args[2]))+";"; }
			if (args[0].equals("x") && args[1].equals("p")) { command += "x "+(x+Integer.parseInt(args[2]))+";"; }
			if (args[0].equals("y") && args[1].equals("m")) { command += "y "+(y-Integer.parseInt(args[2]))+";"; }
			if (args[0].equals("y") && args[1].equals("p")) { command += "y "+(y+Integer.parseInt(args[2]))+";"; }

			safeEval(command);

		}

		//--------------------
		//	Comando ZOOM
		//--------------------
		if (commStart.equals("zoom")) {

			String command = "translate ";
			double zoom = Double.parseDouble(getMyProperty("zoom"));

			if (args[0].equals("zoom") && args[1].equals("m")) { command = "zoom "+(zoom-Integer.parseInt(args[2]))+";"; }
			if (args[0].equals("zoom") && args[1].equals("p")) { command = "zoom "+(zoom+Integer.parseInt(args[2]))+";"; }

			safeEval(command);
		}

		//Se non e' un comando interno lo spedisce a JMOL

		safeEval(_in);
	}

    //------------------------
    //  Needed to interact manually
    //------------------------
	public String getExecutionCommand(String _comm) {
		
		if (_comm.equals("selectedAtomsList")) {
			
			String strSelect= "select ";
			
			
			for (int i = 0; i < app.workConf.getNAtoms(); i++) {
				
				if (app.workConf.isAtomSelected(i)) {
					strSelect +="atomno="+(i+1)+",";	
				}
						
			}
			strSelect=strSelect.substring(0, strSelect.length()-1); //toglie la virgola
			strSelect+=";\n";
			
			jmolViewer.script(strSelect);
			
			return strSelect;
			
		} else {
			
			return "Command not found";
		}
		
		
	}
	
	public String getMyProperty(String p) {
		
		String out = "";
		
		app.status.storeViewStatus((Hashtable<String, String>) jmolViewer.getProperty("getProperty", "orientationInfo", ""));
		// moveto timeSeconds {x y z} degrees zoomPercent transX transY {x y z} rotationRadius navigationCenter navTransX navTransY navDepth
		//moveto 1.0 {0 0 1 0} 100.0 0.08 0.0 {9.325798 0.0 6.299087} 13.572143 {0.0 0.0 0.0} -43.963158 0.0 50.0;
		//moveto 1.0 {0 0 1 0} 100.0 1.08 0.0 {9.325798 0.0 6.299087} 13.572143 {0.0 0.0 0.0} -42.96316 0.0 50.0;
		String all[] = app.status.getViewStatus().get("moveTo").split(" ");
		
		//String pos[] = (viewPoint.get("moveTo").substring(viewPoint.get("moveTo").lastIndexOf("{")+1,viewPoint.get("moveTo").lastIndexOf("}"))).split(" ");
		
		if (p.equals("zoom")) out = all[6];
		
		if (p.equals("trX")) out = all[7]; 
		if (p.equals("trY")) out = all[8];
		
		return out;
	}
		
	///---------------------------
	// Per MOUSE SELECTOR
	///---------------------------
	
	public void addMouseListener(MouseListener l) {

		jmolPanel.addMouseListener(l);
		
	}
	
	public void removeAllMouseListeners() {

		MouseListener[] list = jmolPanel.getMouseListeners();
		for (int i=0; i< list.length; i++) {
			if (!list[i].getClass().toString().contains("org.jmol.viewer")) {
				jmolPanel.removeMouseListener(list[i]);
			}
			
		}
		
	}
	
	///---------------------------
    /// Funzioni di evventi su atomi
    ///---------------------------

	public int findNearestAtomIndex(int x, int y) {
		return jmolViewer.findNearestAtomIndex(x, y);
	}
		
	///---------------------------
    /// View point
    ///---------------------------
	
	public Hashtable<String, String> getView() {
		return (Hashtable<String, String>) jmolViewer.getProperty("getProperty", "orientationInfo", "");
	}
	
	public void setView(Hashtable<String, String> _view) {
		
		if (_view.get("moveTo")!=null) {
			String command = "moveto 0.0" + _view.get("moveTo").substring(10);
			safeEval(command);
		}
		
	}

	public void setView(int view) {
		
		String viewComm = "";
		
		switch (view) {
			case 0: viewComm+="moveto 0 1 0 0 0;\n"; //XY from top
					break;
			case 1: viewComm+="moveto 0 1 0 0 -90;\n"; //XZ from side
					break;
			case 2: viewComm+="moveto 0 1 1 1 -120;\n"; //YZ from other side
					break;
			default: break;
		}
	
		safeEval(viewComm);

	}
	
	///---------------------------
    /// Opzioni di visualizzazione
    ///---------------------------

	public void setDefaultDrawOptions () {
		
		safeEval(setDefaultDrawOptionsString());
        
	}
	
	public String setDefaultDrawOptionsString() {
		
		String out = "";
		out += "set perspectiveDepth OFF; set measurementUnits Angstroms;";
		out += "select all; wireframe 0.1";
		out += "select refreshing FALSE;";
		
		return out;
	}
	
	
	
	public void setDrawOptions(Configuration _in) {
		
		
		safeEval(setDrawOptionsString(_in));
		
		
	}
	
	public String setDrawOptionsString(Configuration _in) {

		String comm = "";
		
		            
        if (_in!=null) {
            double[] center = _in.getCell().getCenter();

            comm += "center {"+center[0]+" "+center[1]+" "+center[2]+"}; " +
					  "centerAt absolute "+center[0]+" "+center[1]+" "+center[2]+";";

        }

	
        //
		// Add draw specs saved with the file
        //
		if (!_in.getDrawSpecs().equals("")) comm+= _in.getDrawSpecs();
		
		return comm;
		
	}
	
	///---------------------------
    //	Setta i colori
    ///---------------------------
	
	public void colorAtom(int idAtom, String color) {

		String coloScripts = "";
		int repX = app.status.getRepetitions()[0];
		int repY = app.status.getRepetitions()[1];
		int repZ = app.status.getRepetitions()[2];
		
		int repDone = 0;
		for (int stepX = 0; stepX<= repX; stepX++) {
			for (int stepY = 0; stepY<= repY; stepY++) {
				for (int stepZ = 0; stepZ<= repZ; stepZ++) {
		
					coloScripts+="select ATOMNO="+(idAtom+app.workConf.getNAtoms()*(repDone)+1)+"; color "+color+";\n";
					repDone++;
					
				}
			}
		}
		
		safeEval(coloScripts);
		//jmolViewer.repaintView();
	}
		
	public void colorAtoms(Configuration _in){

        //
        //  Se e' altra applicazione a gestire i colri passa la funzione a quella
        //
        if (app.status.getColorController()!=null) {
			((MokaController)app.status.getColorController()).colorAtoms();
            return;
		}

		String coloScripts = "";
		int repX = app.status.getRepetitions()[0];
		int repY = app.status.getRepetitions()[1];
		int repZ = app.status.getRepetitions()[2];
		
		coloScripts += "select *; color CPK;\n";
		
		int repDone = 0;
		for (int stepX = 0; stepX<= repX; stepX++) {
			for (int stepY = 0; stepY<= repY; stepY++) {
				for (int stepZ = 0; stepZ<= repZ; stepZ++) {
			
					//Setta i colori
					for (int i=0; i<_in.getNAtoms(); i++) {
						if (_in.isAtomSelected(i)) {
							coloScripts+="select ATOMNO="+(i+_in.getNAtoms()*(repDone)+1)+"; color green;\n";
						}
						
						
					}
					repDone++;
					
				}
			}
		}
		
		safeEval(coloScripts);
		//jmolViewer.repaintView();
	}
	
	public void colorByProperty(double[] _property, Double min, Double max) {
		
		if (_property.length<app.workConf.getNAtoms()){
			app.log("Errore nel comando plotColor");
		}
		String colorData = "varC=\"";
		
		int repX = app.status.getRepetitions()[0];
		int repY = app.status.getRepetitions()[1];
		int repZ = app.status.getRepetitions()[2];
		
		
		for (int stepX = 0; stepX<= repX; stepX++) {
			for (int stepY = 0; stepY<= repY; stepY++) {
				for (int stepZ = 0; stepZ<= repZ; stepZ++) {
					
					for (int i=0; i<app.workConf.getNAtoms(); i++) {
						colorData+=_property[i]+" ";
					}
				}
			}
		}
		
		if (min!=null && max!=null) {
			colorData+="\"; data \"property_mydata @varC\";select all;color atoms property_mydata range "+min+ " "+max;
		} else {
			colorData+="\"; data \"property_mydata @varC\";select all;color atoms property_mydata";
		}
		
		
		safeEval(colorData);
		
	}
	
    ///---------------------------
    ///	Draw atoms
    ///---------------------------
	
	public String drawAtomsString(Configuration _in) {
		
		if (_in.getNAtoms()==0) return null;
		
		double[] toPlace = new double[3];
        
        // Carica le ripetizioni
		int repX = app.status.getRepetitions()[0];
		int repY = app.status.getRepetitions()[1];
		int repZ = app.status.getRepetitions()[2];
		
		// Setta il nome
		String tempName = "temp";
        if (!_in.getName().trim().equals("")) tempName = _in.getName().trim();

		
        // Prima riga in numero di atomi
		String strDATA ="data \"model input\"\n"; 
		
		//Numero di atomi
		strDATA+=(_in.getNAtoms()*(repX+1)*(repY+1)*(repZ+1))+"\n"+tempName+"\n";
		
        // Loop per tutte le ripetizioni
		for (int stepX = 0; stepX<= repX; stepX++) {
			for (int stepY = 0; stepY<= repY; stepY++) {
				for (int stepZ = 0; stepZ<= repZ; stepZ++) {
					
					
					// Loop tra gli atomi
					for (int i=0; i<_in.getNAtoms(); i++) {
						
						
						//Prendo le posizioni degli atomi
                        if (app.status.refold) {
                            toPlace = _in.getAtomPosPBC(i, Costants.cANGSTROM);
                        } else {
                            toPlace = _in.getAtomPos(i,Costants.cANGSTROM);
                        }
												
						double[][] cellAng = _in.getCell().getCellAngstrom();
						
						toPlace[0] += stepX*cellAng[0][0]+stepY*cellAng[1][0]+stepZ*cellAng[2][0];
						toPlace[1] += stepX*cellAng[0][1]+stepY*cellAng[1][1]+stepZ*cellAng[2][1];
						toPlace[2] += stepX*cellAng[0][2]+stepY*cellAng[1][2]+stepZ*cellAng[2][2];

                        strDATA+=_in.getAtomElement(i)+" "+toPlace[0]+" "+toPlace[1]+" "+toPlace[2]+"\n";

                      
					}
					
				}
			}
		}
		
		strDATA = strDATA + "end \"model input\";";
		
		return strDATA;
	}
	
	///---------------------------
    ///	Draw cell
    ///---------------------------

	public void drawCell(double[][] _inCell, String name) {
		drawCell(_inCell, new double[] {0.0, 0.0, 0.0}, name, "gold");
	}

    public void drawCell(double[][] _inCell, String name, String color) {
		drawCell(_inCell, new double[] {0.0, 0.0, 0.0}, name, color);
	}
	
    public void drawCell(double[][] _inCell, double[] origin,  String name, String color) {
    	
    	safeEval(drawCellString(_inCell, new double[] {0.0, 0.0, 0.0}, name, color));
    	
    }
    
	public String drawCellString(double[][] _inCell, double[] origin,  String name, String color) {
		
		if (_inCell == null){
			return null;
		}
		
        
		int repX = app.status.getRepetitions()[0];
		int repY = app.status.getRepetitions()[1];
		int repZ = app.status.getRepetitions()[2];
		
		String cellScripts ="";
		
		if(name==null) name = "cell";
		if (color==null) color = "gold";
		
		int line=1;
		
		for (int stepX = 0; stepX<= repX; stepX++) {
			for (int stepY = 0; stepY<= repY; stepY++) {
				for (int stepZ = 0; stepZ<= repZ; stepZ++) {
					
					double[] arrayStepper = new double[3];
					
					arrayStepper[0] = stepX*_inCell[0][0]+stepY*_inCell[1][0]+stepZ*_inCell[2][0];
					arrayStepper[1] = stepX*_inCell[0][1]+stepY*_inCell[1][1]+stepZ*_inCell[2][1];
					arrayStepper[2] = stepX*_inCell[0][2]+stepY*_inCell[1][2]+stepZ*_inCell[2][2];
					
					
					for (int i=0; i<3; i++) {
						cellScripts+="draw ID "+name+""+line+" line "+ArrayTools.arrayToStringOne(MathTools.arrayAdd(origin,MathTools.arrayAdd(arrayStepper, CellTools.getCorner(0, _inCell))))+
						" "+ArrayTools.arrayToStringOne(MathTools.arrayAdd(origin,MathTools.arrayAdd(arrayStepper,CellTools.getCorner(i+1, _inCell))))+"; color $"+name+""+line+" "+color+";\n";
						line++;

					}

					cellScripts+="draw ID "+name+""+line+" line "+ArrayTools.arrayToStringOne(MathTools.arrayAdd(origin,MathTools.arrayAdd(arrayStepper,CellTools.getCorner(1, _inCell))))+
					" "+ArrayTools.arrayToStringOne(MathTools.arrayAdd(origin,MathTools.arrayAdd(arrayStepper,CellTools.getCorner(4, _inCell))))+"; color $"+name+""+line+" "+color+";\n";
					line++;

					cellScripts+="draw ID "+name+""+line+" line "+ArrayTools.arrayToStringOne(MathTools.arrayAdd(origin,MathTools.arrayAdd(arrayStepper,CellTools.getCorner(1, _inCell))))+
					" "+ArrayTools.arrayToStringOne(MathTools.arrayAdd(origin,MathTools.arrayAdd(arrayStepper,CellTools.getCorner(5, _inCell))))+"; color $"+name+""+line+" "+color+";\n";
					line++;

					cellScripts+="draw ID "+name+""+line+" line "+ArrayTools.arrayToStringOne(MathTools.arrayAdd(origin,MathTools.arrayAdd(arrayStepper,CellTools.getCorner(2, _inCell))))+
					" "+ArrayTools.arrayToStringOne(MathTools.arrayAdd(origin,MathTools.arrayAdd(arrayStepper,CellTools.getCorner(4, _inCell))))+"; color $"+name+""+line+" "+color+";\n";
					line++;

					cellScripts+="draw ID "+name+""+line+" line "+ArrayTools.arrayToStringOne(MathTools.arrayAdd(origin,MathTools.arrayAdd(arrayStepper,CellTools.getCorner(2, _inCell))))+
					" "+ArrayTools.arrayToStringOne(MathTools.arrayAdd(origin,MathTools.arrayAdd(arrayStepper,CellTools.getCorner(6, _inCell))))+"; color $"+name+""+line+" "+color+";\n";
					line++;

					cellScripts+="draw ID "+name+""+line+" line "+ArrayTools.arrayToStringOne(MathTools.arrayAdd(origin,MathTools.arrayAdd(arrayStepper,CellTools.getCorner(3, _inCell))))+
					" "+ArrayTools.arrayToStringOne(MathTools.arrayAdd(origin,MathTools.arrayAdd(arrayStepper,CellTools.getCorner(5, _inCell))))+"; color $"+name+""+line+" "+color+";\n";
					line++;

					cellScripts+="draw ID "+name+""+line+" line "+ArrayTools.arrayToStringOne(MathTools.arrayAdd(origin,MathTools.arrayAdd(arrayStepper,CellTools.getCorner(3, _inCell))))+
					" "+ArrayTools.arrayToStringOne(MathTools.arrayAdd(origin,MathTools.arrayAdd(arrayStepper,CellTools.getCorner(6, _inCell))))+"; color $"+name+""+line+" "+color+";\n";
					line++;

					cellScripts+="draw ID "+name+""+line+" line "+ArrayTools.arrayToStringOne(MathTools.arrayAdd(origin,MathTools.arrayAdd(arrayStepper,CellTools.getCorner(6, _inCell))))+
					" "+ArrayTools.arrayToStringOne(MathTools.arrayAdd(origin,MathTools.arrayAdd(arrayStepper,CellTools.getCorner(7, _inCell))))+"; color $"+name+""+line+" "+color+";\n";
					line++;

					cellScripts+="draw ID "+name+""+line+" line "+ArrayTools.arrayToStringOne(MathTools.arrayAdd(origin,MathTools.arrayAdd(arrayStepper,CellTools.getCorner(5, _inCell))))+
					" "+ArrayTools.arrayToStringOne(MathTools.arrayAdd(origin,MathTools.arrayAdd(arrayStepper,CellTools.getCorner(7, _inCell))))+"; color $"+name+""+line+" "+color+";\n";
					line++;

					cellScripts+="draw ID "+name+""+line+" line "+ArrayTools.arrayToStringOne(MathTools.arrayAdd(origin,MathTools.arrayAdd(arrayStepper,CellTools.getCorner(4, _inCell))))+
					" "+ArrayTools.arrayToStringOne(MathTools.arrayAdd(origin,MathTools.arrayAdd(arrayStepper,CellTools.getCorner(7, _inCell))))+"; color $"+name+""+line+" "+color+";\n";
					line++;

					safeEval(cellScripts);
				}
			}
		}

        //
        // Solo se nome = cell, altrimenti e' funzione usata per aggiugnere cella DRAWABLE
        //
		return cellScripts;
        
	}

	///---------------------------
    ///	GEstione OGGETTI DRAWABLES
    ///---------------------------

	public void drawDrawable(Drawable _in){
		
		// Invia comando
		safeEval(drawDrawableString(_in));
		objPainted++;
	}
	
	public String drawDrawableString(Drawable _in){

        if (!_in.isActive()) return null;

        //
        // Opzione cella
        //
        if (_in.getType()==Costants.oCELL) {

            // Becca il colore
            int[] rgb = _in.getColor();
            String colStr = " ["+rgb[0]+" "+rgb[1]+" "+rgb[2]+"]";

            // Costruisci la matrice della cella
            double[][] cPoints = new double[3][3];
            for (int i=0; i<3; i++) {
                cPoints[i] = MathTools.arraySubtract(_in.getPoints()[i+1], _in.getPoints()[0]);
            }

            // Disegna
            drawCell(cPoints, _in.getPoints()[0], "moka"+_in.getName(), colStr);

            return null;
        }


        //
        //  Tutti gli altri oggetti
        //
		String command="draw ID moka"+_in.getName();

        command+=" "+Costants.objTypeList[_in.getType()];
		command+=" "+_in.getPercentual();

		switch (_in.getType()) {
		//line
		case Costants.oLINE: if (_in.getPoints()[0]==null || _in.getPoints()[1]==null) return null;
							command+=" {"+_in.getPoints()[0][0]+" "+_in.getPoints()[0][1]+" "+_in.getPoints()[0][2]+"} ";
							command+="{"+_in.getPoints()[1][0]+" "+_in.getPoints()[1][1]+" "+_in.getPoints()[1][2]+"}";
							break;
		//plane
		case Costants.oPLANE: if (_in.getPoints()[0]==null || _in.getPoints()[1]==null || _in.getPoints()[2]==null) return null;
							command+=" {"+_in.getPoints()[0][0]+" "+_in.getPoints()[0][1]+" "+_in.getPoints()[0][2]+"} ";
							command+="{"+_in.getPoints()[1][0]+" "+_in.getPoints()[1][1]+" "+_in.getPoints()[1][2]+"}";
							command+="{"+_in.getPoints()[2][0]+" "+_in.getPoints()[2][1]+" "+_in.getPoints()[2][2]+"}";

							if (_in.getPoints().length==4 && _in.getPoints()[3]!=null) {
								command+="{"+_in.getPoints()[3][0]+" "+_in.getPoints()[3][1]+" "+_in.getPoints()[3][2]+"}";
							}
							break;
		//arrow
		case Costants.oARROW: if (_in.getPoints()[0]==null || _in.getPoints()[1]==null) return null;
							command+=" {"+_in.getPoints()[0][0]+" "+_in.getPoints()[0][1]+" "+_in.getPoints()[0][2]+"}";
							command+="{"+_in.getPoints()[1][0]+" "+_in.getPoints()[1][1]+" "+_in.getPoints()[1][2]+"}";
							break;
		//vector
		case Costants.oVECTOR: if (_in.getPoints()[0]==null || _in.getPoints()[1]==null) return null;
							command+=" {"+_in.getPoints()[0][0]+" "+_in.getPoints()[0][1]+" "+_in.getPoints()[0][2]+"}";
							command+="{"+_in.getPoints()[1][0]+" "+_in.getPoints()[1][1]+" "+_in.getPoints()[1][2]+"}";
							break;
                            //vectir

        }

        // Offet ozionale
		if (_in.getOffset()!=null && _in.getOffset().length==3) command+=" offset {"+_in.getOffset()[0]+" "+_in.getOffset()[1]+" "+_in.getOffset()[2]+"}";

        // Colore
		if (_in.getColor()!=null){

            int[] rgb = _in.getColor();
			command+="; color $moka"+_in.getName()+" TRANSLUCENT "+_in.getAlpha()+"  ["+rgb[0]+" "+rgb[1]+" "+rgb[2]+"];";

		}

        return command;
	}

	public String removeDrawableString(Drawable _in){

		String command="draw ID moka"+_in.getName()+"* DELETE;";
		return command;

	}
	
	public void removeDrawable(Drawable _in){
		
		safeEval(removeDrawableString(_in));
		objPainted--;
	}

	public void removeAllDrawables() {

		safeEval(removeAllDrawablesString());
        objPainted=0;

	}
	
	public String removeAllDrawablesString() {

		return "draw moka* DELETE;";
		
	}

	public void drawAllDrawables(Configuration _in) {

        // Loop fra tutti gli oeggeti

		safeEval(drawAllDrawablesString(_in));
		
        for (int i=0; i<_in.getNumDrawable(); i++) {
            if (_in.isDrawableVisible(i)) objPainted++;            
 		}

	}
	
	public String drawAllDrawablesString(Configuration _in) {

        // Loop fra tutti gli oeggeti
		
		String out = "";
		
        for (int i=0; i<_in.getNumDrawable(); i++) {

            // Disegna solo quelli attivi

            if (_in.isDrawableVisible(i)) {
            	out += drawDrawableString(_in.getDrawable(i));
                objPainted++;
			}
            
 		}
        if (out.equals("")) return null;
        
        return out;

	}

	///---------------------------
    /// Trasla e ruota (PER VELCOITA DI RENDER)
    ///---------------------------

	public void traslateAtoms(int[] atomsToMove, double[] moveVect) {
	
		if (app.workConf.getCell()==null || app.workConf==null){
			return;
		}
		
		double[] moveVectCoord = app.workConf.getCell().convertVector(app.workConf.getCoordType(), Costants.cANGSTROM, moveVect);
		
		//Becca quante ripetizione deve fare
//		String[] parts = app.status.getRepetitions().trim().replaceAll("\\t", " ").split("\\s+");
		int repX = app.status.getRepetitions()[0];
		int repY = app.status.getRepetitions()[1];
		int repZ = app.status.getRepetitions()[2];
		
		String strMove = "";
		
		for (int i = 0; i < atomsToMove.length; i++) {
						
			strMove += "select ";
			
			int repDone = 0;
			for (int stepX = 0; stepX<= repX; stepX++) {
				for (int stepY = 0; stepY<= repY; stepY++) {
					for (int stepZ = 0; stepZ<= repZ; stepZ++) {
						
						strMove +="atomno="+(atomsToMove[i]+app.workConf.getNAtoms()*(repDone)+1)+",";
						repDone++;
					}
				}
			}
			strMove=strMove.substring(0, strMove.length()-1); //toglie la virgola
			strMove+=";\n";
			
			strMove += "translateSelected {"+moveVectCoord[0]+" "+moveVectCoord[1]+" "+moveVectCoord[2]+"};\n";
			
		}
		strMove+="connect;\n";
		
		safeEval(strMove);
		
		
	}

	public void rotateAtomCoordAboutAxis(int[] atomsToMove, double[] rotAx, double[] center, double angle) {
		
		if (app.workConf.getCell()==null || app.workConf==null){
			return;
		}
		
		//double[] moveVectCoord = app.converter.convert(app.atoms.coordType, CONST.cANGSTROM, moveVect);
		
		//Becca quante ripetizione deve fare
//		String[] parts = app.status.getRepetitions().trim().replaceAll("\\t", " ").split("\\s+");
		int repX = app.status.getRepetitions()[0];
		int repY = app.status.getRepetitions()[1];
		int repZ = app.status.getRepetitions()[2];
		
		String strMove = "";
		
		for (int i = 0; i < atomsToMove.length; i++) {
						
			strMove += "select ";
			
			int repDone = 0;
			for (int stepX = 0; stepX<= repX; stepX++) {
				for (int stepY = 0; stepY<= repY; stepY++) {
					for (int stepZ = 0; stepZ<= repZ; stepZ++) {
						
						strMove +="atomno="+(atomsToMove[i]+app.workConf.getNAtoms()*(repDone)+1)+",";
						repDone++;
					}
				}
			}
			strMove=strMove.substring(0, strMove.length()-1); //toglie la virgola
			strMove+=";\n";


            //System.out.println("Quaternion around point---");
            double[] pos = app.workConf.getAtomPos(atomsToMove[i],Costants.cANGSTROM);
            Point3d posP = new Point3d(pos[0], pos[1], pos[2]);

            Transform3D trasl = new Transform3D();
            trasl.setTranslation(new Vector3d(-center[0],-center[1],-center[2]));
            trasl.transform(posP);

            Transform3D rot = new Transform3D();
            rot.setRotation(new AxisAngle4d(rotAx[0],rotAx[1],rotAx[2], angle));
            rot.transform(posP);

            trasl.invert();
            trasl.transform(posP);
            
			double[] moveVectCoord = MathTools.arraySubtract(new double[] {posP.x, posP.y,posP.z}, pos);
			
			strMove += "translateSelected {"+moveVectCoord[0]+" "+moveVectCoord[1]+" "+moveVectCoord[2]+"};\n";
			
		}
		strMove+="connect;\n";
		
		safeEval(strMove);
		
		
	}

    
    ///---------------------------
    ///	Disgina CONF
    ///---------------------------

	public void drawConf(Configuration _in) {

		drawConf(_in, null);

	}

	public void drawConf(Configuration _in, String colorCell){

		if (_in==null){
			return;
		}

		String confString = setDefaultDrawOptionsString();
		
		//
		//	CHECK FOR ATOMS
		//
		if (_in.hasAtoms()){

				// Add atoms
				confString+=drawAtomsString( _in)+"show data;\n";
				
                //Aggiorna il vettore degli atomi presenti
		        paintedAtomsPos = _in.getAtomPosAll(Costants.cANGSTROM);
		        paintedAtomsElemen = _in.getAtomElementAll();

		        atomsPainted = _in.getNAtoms();
		}
		
		//
		//	Add cell
		//
		
		confString+=drawCellString( _in.getCell().getCellAngstrom(), 
									new double[] {0,0,0}, 
									null, 
									colorCell);
		cellPainted = true;
		
		//
		//	CHECK FOR DRAWABLES
		//
		if (_in.hasDrawables()) {
			
			// Add drawable
			confString += drawAllDrawablesString(_in);
			objPainted = _in.getNumDrawable();
		}

		//
		//	CHECK FOR OPTIONS
		//
		if (_in.hasOptions()) confString += setDrawOptionsString(_in);

		//
		//	SEND THE COMMAND
		//		
		safeEval(confString);
        
        //System.out.println(confString);
		
	}
	
  
	public void updateAtomsPos(Configuration _in) {
		
		if (_in.getCell()==null || _in==null){
			return;
		}
		
		//Becca quante ripetizione deve fare
//		String[] parts = app.status.getRepetitions().trim().replaceAll("\\t", " ").split("\\s+");
		int repX = app.status.getRepetitions()[0];
		int repY = app.status.getRepetitions()[1];
		int repZ = app.status.getRepetitions()[2];
		
		String strMove = "";
		
		for (int i =0; i<_in.getNAtoms(); i++) {
			

			double[] oldPos = paintedAtomsPos[i];
			double[] newPos = app.workConf.getAtomPos(i,Costants.cANGSTROM);
			
			//Costruisce il vettore di traslazione
			double[] vect = {(newPos[0]-oldPos[0]),
					 		(newPos[1]-oldPos[1]),
					 		(newPos[2]-oldPos[2])};
			
			strMove += "select ";
			
			int repDone = 0;
			for (int stepX = 0; stepX<= repX; stepX++) {
				for (int stepY = 0; stepY<= repY; stepY++) {
					for (int stepZ = 0; stepZ<= repZ; stepZ++) {
						
						strMove +="atomno="+(i+_in.getNAtoms()*(repDone)+1)+",";
						repDone++;
					}
				}
			}
			strMove=strMove.substring(0, strMove.length()-1);
			strMove+=";\n";
			strMove += "translateSelected {"+vect[0]+" "+vect[1]+" "+vect[2]+"};\n";

            paintedAtomsPos[i] = _in.getAtomPos(i,Costants.cANGSTROM).clone();

		}
		strMove+="connect;\n";
		
		safeEval(strMove);
		
	}

	///---------------------------
    ///	SALVA IMMAGINI
    ///---------------------------

	public BufferedImage getImage(int width, int height) {
		
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D ig = bi.createGraphics();
		
		jmolViewer.renderScreenImage(ig, new Dimension(width, height), new Rectangle(jmolPanel.getWidth(), jmolPanel.getWidth()));
		ig.dispose();
		
		return bi;
	}

	//---------------------------------
	//	FUNZIONE DEL MODULO 
	//---------------------------------

	public void reset() {

        boolean keepView = false;

        if (objPainted>0) safeEval("draw moka* DELETE;");

        objPainted = 0;
        atomsPainted = 0;
        cellPainted = false;

        // Se ci sono atomi salva la view
        if (atomsPainted>0) {
            app.status.storeViewStatus((Hashtable<String, String>) jmolViewer.getProperty("getProperty", "orientationInfo", ""));
            keepView = true;
        }

        // Ridisegna tutto
        drawConf(app.workConf);

        // Aggiorna la view precedente
		if (keepView) setView(app.status.getViewStatus());
        
	}
	
	public void refreshDraw(boolean keepView) {
		
		this.reset();
        
	}
	
	public void confChanged() {


		if (app.oldConf == -1) {
			reset();
			return;
		}

        //
		//  Se le configurazioni hanno gli stessi atomi e la stessa cella
        //
		if (	app.confDB.get(app.thisConf).getCell().getIdCell().equals(app.confDB.get(app.oldConf).getCell().getIdCell()) &&
				app.confDB.get(app.thisConf).getNAtoms() == app.confDB.get(app.oldConf).getNAtoms() &&
				app.confDB.get(app.thisConf).getAtomsID().equals(app.confDB.get(app.oldConf).getAtomsID())) {

            boolean keepView = false;
            // Se ci sono atomi salva la view
            if (atomsPainted>0) {
                app.status.storeViewStatus((Hashtable<String, String>) jmolViewer.getProperty("getProperty", "orientationInfo", ""));
                    keepView = true;
            }

            //viewNextStep(app.oldConf, app.thisConf);
            if (objPainted>0) safeEval("draw moka* DELETE;");
            objPainted = 0;


            updateAtomsPos(app.workConf);

            if (app.workConf.hasDrawables()) drawAllDrawables(app.workConf);

            //Controlla se deve essere lui a colorare o meno le cose
			if (app.status.getColorController()==null) {
				colorAtoms(app.workConf);
			} else {
				((MokaController)app.status.getColorController()).colorAtoms();
			}

			//drawObjects(app.objList);
			if (app.workConf.hasDrawables()) setDrawOptions(app.workConf);

            if (keepView) setView(app.status.getViewStatus());

            
        //
        //  Altrimeni refresha tutto
        //
		} else {

			refreshDraw(true);
		}
		
	}
	
	
	public void atomsChanged(int[] listChanged) {
		// TODO Auto-generated method stub
		
	} 
	
		
	public Point3f getAtomPoint3f(int site) {
		return  jmolViewer.getAtomPoint3f(site);	
	}
	
	
	/* (non-Javadoc)
	 * @see MokaMOD#reciveMouseSelection(int)
	 */
	public void reciveMouseSelection(int site) {
		// TODO Auto-generated method stub
		
	}


}
