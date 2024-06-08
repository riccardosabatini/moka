package org.moka;


import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import jhplot.HPlot;
import jhplot.shapes.Circle;
import jhplot.shapes.Line;

import org.moka.structures.Cell;
import org.moka.structures.Configuration;
import org.moka.structures.Kpoints;
import org.moka.structures.Module;
import org.moka.tools.CellTools;
import org.moka.tools.MathTools;
import org.moka.tools.gui.ComboDialog;
import org.moka.tools.gui.GUItools;
import org.moka.tools.gui.RadioDialog;

import org.moka.common.Costants;


/**
 * @author riki
 *
 */
public class KPointEditor extends JFrame implements ActionListener, Module, TableModelListener {

	public Moka app;
	public JTextArea textArea;
	private JFileChooser fc;
    
	//PlottingPanel plotter;
	HPlot plotter;
	JTextField mainText;
	JButton printB, delB, addB, do3DB, pathB, convertB;
	
	JTable table;
	DefaultTableModel tm;
	JScrollPane tableScroller;
	ListSelectionModel listSelectionModel;
	
	//boolean appWorking = false;
	
	JComboBox graphToolBox, compareToolBox, coordKtoolBox;
	Checkbox compareSelect;
	
	String[] graphs = { "b1 / b2", "b2 / b3", "b1 / b3"};
	int firstVect = 0, secondVect = 1;
	
	int firstCoord = 0, secondCoord = 1;
	String graphTitle = graphs[0];
	
	Configuration kConf = new Configuration();
	
	//------------------------
	//	INTERFACCIA
	//------------------------
	
	public KPointEditor(Moka _app) {
		
		JPanel framePanel = new JPanel();
		setContentPane(framePanel);
		getContentPane().setLayout(new BorderLayout());
		
		this.app = _app;
		
		plotter = new HPlot("Canvas",600,400);
		plotter.setAutoRange(true);
		//plotter.setAutoRangeAll();
//		plotter = new PlottingPanel(null,null,null);
//		plotter.addDrawable(this);
//		plotter.setSquareAspect(true);
		this.getContentPane().add(GUItools.createPanelForComponent(plotter.getCanvasPanel(), "Plotter", new Dimension(350,350)), BorderLayout.CENTER);

		//----------------------
		//	TABELLA
		//----------------------
		
		tm = new DefaultTableModel();
		
		tm.addColumn("Num");
		tm.addColumn("1");
		tm.addColumn("2");
		tm.addColumn("3");
		tm.addColumn("w");
		
		table = new JTable(tm);
		table.setDragEnabled(true);
		table.getModel().addTableModelListener(this);
		//Per selezioni
		listSelectionModel = table.getSelectionModel();
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		tableScroller = new JScrollPane(table);
		
		Box tableBox = Box.createVerticalBox();
		
		coordKtoolBox = new JComboBox(new String[] {"Alat", "Crysal"});
		coordKtoolBox.addActionListener(this);
		
		tableBox.add(tableScroller);
		tableBox.add(coordKtoolBox);
		
		JPanel tmPanel = GUItools.createPanelForComponent(tableBox, "Kpoints",new Dimension(350,200));
		this.getContentPane().add(tmPanel, BorderLayout.EAST);
		
		
		Box downPanel = Box.createVerticalBox();
        
        textArea = new JTextArea(10, 15);
        textArea.setText("");
        JScrollPane scrollPane2 = new JScrollPane(textArea);
        
        downPanel.add(scrollPane2);
        
        Box comboPanel = Box.createHorizontalBox();
        
        graphToolBox = new JComboBox(graphs);
        graphToolBox.setSelectedIndex(0);
        graphToolBox.addActionListener(this);
        comboPanel.add(graphToolBox);
        
        compareSelect = new Checkbox("Compare");
        compareSelect.setState(false);
        comboPanel.add(compareSelect);
        
        compareToolBox = new JComboBox(new String[] {"none"});
        compareToolBox.setSelectedIndex(0);
        compareToolBox.addActionListener(this);
        compareToolBox.setEnabled(true);
        comboPanel.add(compareToolBox);
        
        Box buttonPanel = Box.createHorizontalBox();
        
        addB= new JButton("Add");
        addB.setActionCommand("addB");
        addB.addActionListener(this);
        buttonPanel.add(addB);
        
        delB= new JButton("Del");
        delB.setActionCommand("delB");
        delB.addActionListener(this);
        buttonPanel.add(delB);
        
        pathB= new JButton("Make path");
        pathB.setActionCommand("pathB");
        pathB.addActionListener(this);
        buttonPanel.add(pathB);
        
        
        buttonPanel.add(new JSeparator(JSeparator.VERTICAL));
        
        printB= new JButton("Print list");
        printB.setActionCommand("sendB");
        printB.addActionListener(this);
        buttonPanel.add(printB);
        
        do3DB= new JButton("Go 3D");
        do3DB.setActionCommand("do3DB");
        do3DB.addActionListener(this);
        buttonPanel.add(do3DB);
        
        buttonPanel.add(Box.createHorizontalGlue());
        
        downPanel.add(comboPanel);
        downPanel.add(buttonPanel);
        this.getContentPane().add(downPanel, BorderLayout.SOUTH);
        
       
		
		setSize(550,350);

	}
	
	protected void updateComboCompare() {
		
		compareToolBox.removeAllItems();
		
        
		String[] confs = new String[app.confDB.size()];
		
		if (app.confDB.size() == 0) {
			confs = new String[1];
			confs[0] = "none";
			compareSelect.setState(false);
			compareSelect.setEnabled(false);
			
			
		} else {
			
			for (int i =0; i< app.confDB.size(); i++) {
				confs[i] = "Conf "+(i+1);
			}
			compareSelect.setEnabled(true);
			
		}
		
		for (int i =0; i< confs.length; i++) {
			compareToolBox.addItem(confs[i]);
		}
		
        compareToolBox.setSelectedIndex(0);
        
		
	}
	
	//------------------------
	//	Azioni
	//------------------------
	
	public void actionPerformed(ActionEvent e) {
		
		if (app.status.isAppWaiting()) return;
		
		if (e.getSource() == addB) {
			
			addK();
			this.reset();
		}
		
		if (e.getSource() == delB) {
			
			delSelected();
			this.reset();
		
		}
		
		if (e.getSource() == pathB) {
			
			app.status.setWorkingOn();
			//appWorking = true;
			
			int startK = -1;
			int endK = -1;
			Integer steps = 5;
			
			startK = ComboDialog.showDialog(this,null,"Select K start","Start",listKpoint(),0);
			endK = ComboDialog.showDialog(this,null,"Select K end","End",listKpoint(),0);
			
			try {
				steps = GUItools.getIntFormUser("Step numbers", steps);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			if (startK!=-1 && endK!=-1 && startK!=endK && steps > 0) {
				addPath(startK, endK, steps);
				app.workConf.getKpoints().setKGenerator(null);
				
			}
			this.reset();
			
			app.status.setWorkingOff();
			//appWorking = false;
		}
		
		if (e.getSource() == do3DB) {
			
			//appWorking = true;
			app.status.setWorkingOn();
			
			draw3D();
			
			app.status.setWorkingOff();
			//appWorking = false;
		}
		
		if (e.getSource() == printB) {
			
			//appWorking = true;
			app.status.setWorkingOn();
			
			printKpoint();
			
			app.status.setWorkingOff();
			//appWorking = false;
		}
		
		if (e.getSource() == graphToolBox) {
			
			//appWorking = true;
			app.status.setWorkingOn();
			
			int graphID = graphToolBox.getSelectedIndex();
			graphTitle = graphs[graphID];
			
			if (graphID==0) {
				firstVect = 0; secondVect = 1;
				firstCoord = 0; secondCoord = 1;
				
			}
			
			if (graphID==1) {
				firstVect = 1; secondVect = 2;
				firstCoord = 1; secondCoord = 2;
			}
			
			if (graphID==2) {
				firstVect = 0; secondVect = 2;
				firstCoord = 0; secondCoord = 2;
			}
			
			draw2D();
			
			//appWorking = false;
			app.status.setWorkingOff();
		}
		
		if (e.getSource() == coordKtoolBox) {
			
			if (!app.status.isWorking() && app.workConf!=null && app.workConf.getKpoints()!=null
					&& coordKtoolBox.getSelectedIndex()!=app.workConf.getKpoints().getKType()) {
				
				app.status.setWorkingOn();
				
				app.workConf.convertKpoints(coordKtoolBox.getSelectedIndex());
				
				app.status.setWorkingOff();
				
				this.reset();
			}
			
		}
		
	}
		
	public void tableChanged(TableModelEvent e) {
		
		if (app.status.isAppWaiting()) return;
		
		if (app.status.isWorking()) return;
		
		if (e.getType() == TableModelEvent.UPDATE) {
			int selected = e.getFirstRow();
			int column = e.getColumn();
			TableModel model = (TableModel)e.getSource();
			//String columnName = model.getColumnName(column);
			//Object data = model.getValueAt(row, column);
	
			if (column>=1 && column<=3) {
				
				app.workConf.getKpoints().setKvalue(selected, column-1, Double.parseDouble((String) model.getValueAt(selected, column)));
				
			}
			
			if (column==4) {
				
				app.workConf.getKpoints().setWeigth(selected, Double.parseDouble((String) model.getValueAt(selected, column)));
			}
			
			this.repaint();
		}
	}
	
	public void generateDrawCONF() {
		
		kConf.setCell(new Cell(0, new double[] {1/Costants.Ry}, app.workConf.getCell().getCellReciprocal().clone()));
		kConf.setCoordType(Costants.cANGSTROM);
		
		kConf.getAtoms().clear();
		kConf.setNAtoms(0);
		if (app.workConf.getKpoints()==null) {
			return;
		}
		
		for (int i=0; i<app.workConf.getKpoints().getNPoints(); i++) {
			
			double[] toPlace = app.workConf.getKpoints().getKpoint(i).clone();
			
			if (app.workConf.getKpoints().getKType() == Costants.kCRYSTAL) {
				toPlace = app.workConf.getCell().convertKpoint(Costants.kTBIBA, toPlace.clone());
			}
			
			kConf.addAtom("H", toPlace);
			kConf.setAtomFlag(i, Costants.aSPECIAL, 1);
		}
		
	}
	
	//------------------------
	//	ADD / DELETE / INSERT
	//------------------------
	
	public void addK() {
		
		int addType = RadioDialog.showDialog(this,null,"Add k points","Add", new String[] {"Single", "Mesh"}, 0);
		
		if (app.workConf.getKpoints()==null) {
			app.workConf.setKpoints(new Kpoints(0));
		}
		
		if (addType == 0) {
			
			app.workConf.getKpoints().addKpoint(new double[] {0,0,0});
			app.workConf.getKpoints().setKGenerator(null);
			
		} else {
			
			String meshStr = GUItools.getStrFormUser("Mesh", "2 2 2 0 0 0");
			String[] parts = meshStr.trim().split(" ");
			if (app.workConf.getKpoints().getNPoints()==0) {
				app.workConf.getKpoints().setKGenerator(meshStr);
			} else {
				app.workConf.getKpoints().setKGenerator(null);
			}
			
			app.workConf.getKpoints().addKpoint(CellTools.makeMonkhorstPackgrid(app.workConf.getCell().getCellReciprocal(), 
					Integer.parseInt(parts[0]),
					Integer.parseInt(parts[1]),
					Integer.parseInt(parts[2]),
					Integer.parseInt(parts[3]),
					Integer.parseInt(parts[4]),
					Integer.parseInt(parts[5])));
			
			
			
		}
		
	}
	
	public void delSelected() {
		
		
		boolean isAdjusting = listSelectionModel.getValueIsAdjusting(); 
		
        if (!listSelectionModel.isSelectionEmpty() && !isAdjusting) {
        
            // Find out which indexes are selected.
            int minIndex = listSelectionModel.getMinSelectionIndex();
            int maxIndex = listSelectionModel.getMaxSelectionIndex();
            
            for (int i = maxIndex; i >=minIndex ; i--) {
                if (listSelectionModel.isSelectedIndex(i)) {
                    app.workConf.getKpoints().delKpoint(i);
                    app.workConf.getKpoints().setKGenerator(null);
                }
            }
           
        }
        
	}

	public void addPath(int start, int end, int points) {
		
		double[] Ks = app.workConf.getKpoints().getKpoint(start);
		double[] Ke = app.workConf.getKpoints().getKpoint(end);
		
		double[] step = new double[3];
		
		step[0] = (Ke[0]-Ks[0])/points;
		step[1] = (Ke[1]-Ks[1])/points;
		step[2] = (Ke[2]-Ks[2])/points;
		
		for (int i=0; i<points-1; i++) {
			
			Ks = MathTools.arrayAdd(Ks, step);
			
			app.workConf.getKpoints().insertKpoint(Ks, start+1+i);
			
		}
		
	}
	
	//----------------------------------------
	//	Filler e printer
	//----------------------------------------
	
	public void tableAllkpoints(){

		if (app.workConf.getKpoints()==null) {
			tm.setRowCount(0);
			return;
		}
		
		tm.setRowCount(0);
		double[] kToPlace = new double[3];
		double wToPlace = 1.0;
		
		for (int i=0; i<app.workConf.getKpoints().getNPoints(); i++) {
			
			kToPlace = app.workConf.getKpoints().getKpoint(i);
			wToPlace = app.workConf.getKpoints().getKweight(i);
			
			tm.addRow(new String[] { "k"+(i+1), String.format(Costants.decimalPlace4,kToPlace[0]), 
															 String.format(Costants.decimalPlace4,kToPlace[1]),
															 String.format(Costants.decimalPlace4,kToPlace[2]),
															 String.format(Costants.decimalPlace4,wToPlace)});
			
		}
		
		coordKtoolBox.setSelectedIndex(app.workConf.getKpoints().getKType());
		
	}
	
	public void printKpoint() {
		
		textArea.append("---- Reciprocal cell (a: "+app.workConf.getCell().getParameters()[0]+ ")\n");
		
		for (int i =0; i<3; i++) {
			String line = "";
			line += "b"+(i+1);
			line+=Costants.separatorTAB;
			line += String.format(Costants.decimalPlace8,app.workConf.getCell().getCellReciprocal()[i][0]);
			line+=Costants.separatorTAB;
			line += String.format(Costants.decimalPlace8,app.workConf.getCell().getCellReciprocal()[i][1]);
			line+=Costants.separatorTAB;
			line += String.format(Costants.decimalPlace8,app.workConf.getCell().getCellReciprocal()[i][2]);
			line += Costants.newline;
			textArea.append(line);
		}

		textArea.append("\n---- K points\n");
		
		for (int i =0; i<app.workConf.getKpoints().getNPoints(); i++) {
			
			String line = "";
			line += "k "+(i+1);
			line+=Costants.separatorTAB;
			line += String.format(Costants.decimalPlace8,app.workConf.getKpoints().getKpoint(i)[0]);
			line+=Costants.separatorTAB;
			line += String.format(Costants.decimalPlace8,app.workConf.getKpoints().getKpoint(i)[1]);
			line+=Costants.separatorTAB;
			line += String.format(Costants.decimalPlace8,app.workConf.getKpoints().getKpoint(i)[2]);
			line += Costants.newline;
			
			textArea.append(line);
		}
		
	}

	public String[] listKpoint() {
		
		if (app.workConf.getKpoints()!=null){
		
			String[] out = new String[app.workConf.getKpoints().getNPoints()];
			
			for (int i =0; i<app.workConf.getKpoints().getNPoints(); i++) {
				
				out[i] = "k "+(i+1)+" [";
				out[i]+= String.format(Costants.decimalPlace8,app.workConf.getKpoints().getKpoint(i)[0])+" ";
				out[i]+= String.format(Costants.decimalPlace8,app.workConf.getKpoints().getKpoint(i)[1])+" ";
				out[i]+= String.format(Costants.decimalPlace8,app.workConf.getKpoints().getKpoint(i)[2])+" ";
				out[i]+="]";
			}
			return out;
		} else {
			return new String[] {"..."};
		}
		
	}

	//---------------------
	//	Drawer, chiamato un sacco di volte e si aggiorna ogni evento
	//---------------------
	
	public double ballRadius(Configuration _in) {
		
		double minDist = Double.MAX_VALUE;
		
		for (int i=0; i<_in.getNAtoms()-1; i++) {
			for (int j=i+1; j<_in.getNAtoms(); j++) {
				
				double dist = MathTools.getDistance(_in.getAtomPos(i), _in.getAtomPos(j));
				if (dist<minDist && dist>0) minDist = dist;
			}
		}
		
		return minDist/3;
		
	}
	
	public void draw3D(){
		
		if (app.workConf==null || app.workConf.getCell()==null){
			return;
		}
		
		generateDrawCONF();
		
		//app.setEditingMode(Costants.mDRAW);
		app.status.setRepetitions(new int[] {1, 1, 1});
		app.viewer.drawConf(kConf, "blue");
		
		if (compareSelect.getState()) {
			app.viewer.drawCell(app.confDB.get(compareToolBox.getSelectedIndex()).getCell().getCellReciprocal(), "green");
		}
		app.viewer.execCommand("select all; wireframe 0.0; spacefill "+ballRadius(kConf)+";");
		
	}
	
	public void draw2D(){
		
		if (app.workConf==null || app.workConf.getCell()==null){
			return;
		}
		
		generateDrawCONF();
		//plotter.clearData();
		plotter.removePrimitives();
		plotter.resetStyleAll();
		
		kConf.allineateCell(firstVect, secondVect);
		
		double[] vec1 = kConf.getCell().getCellAngstrom()[firstVect];
		double[] vec2 = kConf.getCell().getCellAngstrom()[secondVect];
		
		plotter.setNameX("b"+(firstVect+1));
		plotter.setNameY("b"+(secondVect+1));
		
		drawVectorBox2D(vec1,vec2,0,1, Color.blue, plotter);
		
		//Punti K
		
		double minX = 0;
		double maxX = 0;
		double minY = 0;
		double maxY = 0;
		
		double radius = ballRadius(kConf); 
		
		if (kConf.getNAtoms()>0){
		
			//Math.sqrt((MathTools.getArea2D(vec1, vec2, firstCoord, secondCoord)/app.workConf.kpoints.nPoints)/4*Math.PI)*0.3;
			
			for (int i=0; i<kConf.getNAtoms(); i++) {
				
				double[] point = kConf.getAtomPos(i);
				
				if (point[0]>maxX) maxX = point[0];
				if (point[0]<minX) minX = point[0];
				
				if (point[1]>maxY) maxY = point[1];
				if (point[1]<minY) minY = point[1];
				
				plotter.add(CircleColored(point[0] , point[1] , radius, getColor(0)));
				
			}
		
		}
		
		for (int i=0; i<8; i++) {
			
			double[] point =  CellTools.getCorner(i, kConf.getCell().getCellAngstrom());
			
			if (point[0]>maxX) maxX = point[0];
			if (point[0]<minX) minX = point[0];
			
			if (point[1]>maxY) maxY = point[1];
			if (point[1]<minY) minY = point[1];
			
		}
		
		plotter.setRange(0, minX-radius, maxX+radius);
		plotter.setRange(1, minY-radius, maxY+radius);
		plotter.update();
		//---------------
		//
		//---------------
//		if (compareSelect.getState()) {
//			
//			g.setColor(Color.green);
//			double[] vec1c = app.confDB.get(compareToolBox.getSelectedIndex()).cell.cellReciprocal[firstVect];
//			double[] vec2c = app.confDB.get(compareToolBox.getSelectedIndex()).cell.cellReciprocal[secondVect];
//			
//			drawVectorBox2D(vec1c,vec2c,firstCoord,secondCoord, Color.blue, p,g);
//			
//			//Punti K
//			
//			
//			if (app.confDB.get(compareToolBox.getSelectedIndex()).kpoints!=null){
//			
//				double radius = ballRadius(app.confDB.get(compareToolBox.getSelectedIndex())); // Math.sqrt((MathTools.getArea2D(vec1c, vec2c, firstCoord, secondCoord)/app.confDB.get(compareToolBox.getSelectedIndex()).kpoints.nPoints)/4*Math.PI)*0.3;
//				
//				for (int i=0; i<app.confDB.get(compareToolBox.getSelectedIndex()).kpoints.nPoints; i++) {
//					
//					double[] point = app.confDB.get(compareToolBox.getSelectedIndex()).kpoints.getKpoint(i);
//					if (app.confDB.get(compareToolBox.getSelectedIndex()).kpoints.kType == CONST.kCRYSTAL) {
//						point = CONVERT.convertKpoint(CONST.kTBIBA, point.clone() , app.confDB.get(compareToolBox.getSelectedIndex()).cell);
//					}
//					
//					double[] toPlace = new double[2]; 
//					toPlace[0] = point[firstCoord];
//					toPlace[1] = point[secondCoord];
//					
//					int pxRadius = Math.abs(p.xToPix(radius)-p.xToPix(0));
//					int pyRadius = Math.abs(p.yToPix(radius)-p.yToPix(0));
//					
//					int xPix = p.xToPix(toPlace[0])-pxRadius;
//					int yPix = p.yToPix(toPlace[1])-pyRadius;
//					
//					g.setColor(getColor(1));
//					g.fillOval(xPix, yPix, 2*pxRadius, 2*pyRadius);
//				}
//			
//				p.setPreferredMinMax(
//						Math.min(getMinGraph(vec1, vec2, app.workConf.kpoints.toDoubleArray(), firstCoord),getMinGraph(vec1c, vec2c, app.confDB.get(compareToolBox.getSelectedIndex()).kpoints.toDoubleArray(), firstCoord)), 
//						Math.max(getMaxGraph(vec1, vec2, app.workConf.kpoints.toDoubleArray(), firstCoord),getMaxGraph(vec1c, vec2c, app.confDB.get(compareToolBox.getSelectedIndex()).kpoints.toDoubleArray(), firstCoord)),
//						Math.min(getMinGraph(vec1, vec2, app.workConf.kpoints.toDoubleArray(), secondCoord),getMinGraph(vec1c, vec2c, app.confDB.get(compareToolBox.getSelectedIndex()).kpoints.toDoubleArray(), secondCoord)),
//						Math.max(getMaxGraph(vec1, vec2, app.workConf.kpoints.toDoubleArray(), secondCoord),getMaxGraph(vec1c, vec2c, app.confDB.get(compareToolBox.getSelectedIndex()).kpoints.toDoubleArray(), secondCoord)));
//						 plotter.setMessage(graphTitle);
//
//						 
//			} else {
//				
//				p.setPreferredMinMax(Math.min(getMinGraph(vec1, vec2, null, firstCoord),getMinGraph(vec1c, vec2c, null, firstCoord)), 
//						Math.max(getMaxGraph(vec1, vec2, null, firstCoord),getMaxGraph(vec1c, vec2c, null, firstCoord)),
//						Math.min(getMinGraph(vec1, vec2, null, secondCoord),getMinGraph(vec1c, vec2c, null, secondCoord)),
//						Math.max(getMaxGraph(vec1, vec2, null, secondCoord),getMaxGraph(vec1c, vec2c, null, secondCoord)));
//				
//			}
//		}

	}
	
	public Color getColor(int i){
		
		if (i==0) {
			return Color.RED;
		} else {
			return Color.YELLOW;
		}
		 
		
	}
	
	//---------------------
	//	Disegna oggetti
	//---------------------
	
	private Line LineColored(double _x0,double _y0,double _x1,double _y1, Color c) {
		
		Line l1 = new Line(_x0,_y0,_x1,_y1);
		l1.setColor(c);
		return l1;
		
	}

	private Circle CircleColored(double _x0,double _y0 ,double radius, Color c) {
		
		Circle c1 = new Circle(_x0 , _y0 , radius);
		c1.setColor(c);
		
		return c1;
		
		
	}
	
	private void drawClosedShape(double[][] points, Color c,HPlot p) {
		
		for (int i=0; i<points.length-1; i++) {
		
			//drawLine(points[i][0], points[i][1], points[i+1][0], points[i+1][1], c, p,g);
			p.add(LineColored(points[i][0], points[i][1], points[i+1][0], points[i+1][1], c));
		}
		p.add(LineColored(points[points.length-1][0], points[points.length-1][1], points[0][0], points[0][1], c));
		//drawLine(points[points.length-1][0], points[points.length-1][1], points[0][0], points[0][1], c,p,g);
	}
	
	private void drawVectorBox2D(double[] vec1, double[] vec2, int dim1, int dim2, Color c, HPlot p) {
		
		double[][] points = new double[4][2];
		
		points[0][0] = 0;
		points[0][1] = 0;
		
		points[1][0] = vec1[dim1];
		points[1][1] = vec1[dim2];
		
		points[2][0] = vec1[dim1]+vec2[dim1];
		points[2][1] = vec1[dim2]+vec2[dim2];
		
		points[3][0] = vec2[dim1];
		points[3][1] = vec2[dim2];
		
		drawClosedShape(points, c, p);
		
	}
	
	private void drawShape(double[][] points, Color c, HPlot p) {
		
		int x0,y0,x1,y1;
		for (int i=0; i<points.length-1; i++) {
		
			p.add(LineColored(points[i][0], points[i][1], points[i+1][0], points[i+1][1], c));
			//drawLine(points[i][0], points[i][1], points[i+1][0], points[i+1][1], c, p,g);
		}
		p.add(LineColored(points[points.length-1][0], points[points.length-1][1], points[0][0], points[0][1], c));
		//drawLine(points[points.length-1][0], points[points.length-1][1], points[0][0], points[0][1], c,p,g);
	}
	
	//---------------------
	//	Per funzionamento
	//---------------------
	
	class ClosingWindowAdapter extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			setVisible(false);
			System.exit(0);
		}
	}

	/* PER MODULI
	 *
	 */
	public void confChanged() {
		// TODO Auto-generated method stub
		app.status.setWorkingOn();
		
		generateDrawCONF();
		updateComboCompare();
		tableAllkpoints();
		draw2D();
		app.status.setWorkingOff();
	}

	public void atomsChanged(int[] listChanged) {
		// TODO Auto-generated method stub
		
	} 
	
	public void reset() {
		// TODO Auto-generated method stub
		app.status.setWorkingOn();
		
		generateDrawCONF();
		updateComboCompare();
		tableAllkpoints();
		draw2D();
		
		app.status.setWorkingOff();
	} 

	public void reciveMouseSelection(int site) {
		// TODO Auto-generated method stub
		
	} 
}
