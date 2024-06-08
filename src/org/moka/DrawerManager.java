package org.moka;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.net.URLClassLoader;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.vecmath.Point3f;

import math.geom3d.Point3D;
import math.geom3d.Vector3D;
import math.geom3d.line.StraightLine3D;
import math.geom3d.plane.Plane3D;

import org.moka.common.Costants;

import org.moka.structures.Cell;
import org.moka.structures.Drawable;
import org.moka.structures.Module;
import org.moka.structures.MokaController;
import org.moka.tools.ArrayTools;
import org.moka.tools.events.mouseAcceptor;
import org.moka.tools.events.mouseReceiver;
import org.moka.tools.gui.GUItools;
import org.moka.tools.gui.SizedTextField;


/**
 * @author riki
 *
 */
public class DrawerManager extends JPanel implements ActionListener, Module, mouseReceiver, TableModelListener, MokaController {

	JComboBox objTypeMenu;
	
	JTextField[] paramText ; //= new JTextField[5];
	Border defaultBorder = (new JTextField()).getBorder();
	JTextField offsetT, percT;
	SizedTextField alphaT;
	JButton addB, delB, reloadB, millerB;
	JCheckBox perpCheck;
	
	JTable drawablesTable;
	DrawablesTM drawablesTableModel;
	
	public Moka app;
    
	double[][] points; double[] off; int perc = 100; double alpha = 0.2;
	
	int[] siteSelected = ArrayTools.arrayNewFill(10,-1);
	int paramToSet = -1;
	int coordinatesInput = 4;
	
	Cell cellTemp;
	JLabel colorLabel;
	Color color = Color.MAGENTA;
	
	boolean isActive = false;
	//int barWidth = 150, barHeigth = 400;
	
	//------------------------
	//	INTERFACCIA
	//------------------------
	public DrawerManager(Moka _app) {
		
		this.app = _app;
		
		Box drawingP = Box.createVerticalBox();
		
		
		//---------------------------
		//	INPUT
		//---------------------------
		Box centerBox = Box.createVerticalBox();
		
		JPanel centralCOL = new JPanel(new GridLayout(0,1));
		JPanel rightCOLS = new JPanel(new GridLayout(0,1));
		
		paramText = new JTextField[coordinatesInput];
		
		//Param ONE
		objTypeMenu = new JComboBox(Costants.objTypeList);
		objTypeMenu.setSelectedIndex(0);
		objTypeMenu.addActionListener(this);
		centralCOL.add(objTypeMenu);
		rightCOLS.add(new JLabel(""));
		
		//Param ONE
		paramText[0] = new JTextField();
		paramText[0] = new JTextField();
		centralCOL.add(paramText[0]);
		JLabel h0 = new JLabel(GUItools.createImageIcon("org/moka/images/dot_red_16.gif", app));
		h0.addMouseListener(new mouseAcceptor(this, 100));
		rightCOLS.add(h0);
		
		//Param TWO
		paramText[1] = new JTextField();
		paramText[1] = new JTextField();
		centralCOL.add(paramText[1]);
		JLabel h1 = new JLabel(GUItools.createImageIcon("org/moka/images/dot_blue_16.gif", app));
		h1.addMouseListener(new mouseAcceptor(this, 101));
		rightCOLS.add(h1);
		
		//Param THREE
		paramText[2] = new JTextField();
		paramText[2] = new JTextField();
		centralCOL.add(paramText[2]);
		JLabel h2 = new JLabel(GUItools.createImageIcon("org/moka/images/dot_green_16.gif", app));
		h2.addMouseListener(new mouseAcceptor(this, 102));
		rightCOLS.add(h2);
		
		//Param FOUR
		paramText[3] = new JTextField();
		paramText[3] = new JTextField();
		centralCOL.add(paramText[3]);
		JLabel h3 = new JLabel(GUItools.createImageIcon("org/moka/images/dot_yellow_16.gif", app));
		h3.addMouseListener(new mouseAcceptor(this, 103));
		rightCOLS.add(h3);
		
		//----------------------------
		//	COLORI
		//----------------------------
		
		Box colorBox = Box.createHorizontalBox();
		colorBox.add(Box.createHorizontalGlue());
		colorLabel = new JLabel("         ");
		colorLabel.setOpaque(true);
		colorLabel.setBackground(color);
		colorLabel.addMouseListener(new mouseAcceptor(this, 200));
		colorLabel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createLoweredBevelBorder()));
		colorBox.add(colorLabel);
		
		alphaT = new SizedTextField(3);
		alphaT.setText(alpha+"");
		colorBox.add(alphaT);
		
		centralCOL.add(colorBox);
		JLabel hColor = new JLabel(GUItools.createImageIcon("org/moka/images/palette.png", app));
		rightCOLS.add(hColor);

		//----------------------------
		//	PERCENTUAL
		//----------------------------

		percT = new JTextField();
		percT.setText("100");
		percT.setColumns(10);
		centralCOL.add(percT);
		rightCOLS.add(new JLabel(GUItools.createImageIcon("org/moka/images/percentual_16.png", app)));

		//----------------------------
		//	OFFSET
		//----------------------------

		offsetT = new JTextField();
		offsetT.setColumns(5);
		centralCOL.add(offsetT);
		JLabel hOffset = new JLabel(GUItools.createImageIcon("org/moka/images/cursor.png", app));
		hOffset.addMouseListener(new mouseAcceptor(this, 104));
		rightCOLS.add(hOffset);
		
		perpCheck = new JCheckBox();
		Box tempB = Box.createHorizontalBox();
		tempB.add(Box.createHorizontalGlue());
		tempB.add(perpCheck);
		centralCOL.add(tempB);
		rightCOLS.add(new JLabel(GUItools.createImageIcon("org/moka/images/orthogonal_16.png", app)));
		
		
		JPanel inputs = new JPanel(new BorderLayout());
		inputs.add(centralCOL,BorderLayout.CENTER);
		inputs.add(rightCOLS,BorderLayout.EAST);
		centerBox.add(inputs);

		//----------------------------
		//	BUTTONS
		//----------------------------
		
		Box buttons = Box.createHorizontalBox();
		
		addB = GUItools.makeNavigationButton("org/moka/images/list-add.png", "addB", "Add", "addB",app);
		addB.addActionListener(this);
		buttons.add(addB);
		
		millerB = GUItools.makeNavigationButton("org/moka/images/shape_ungroup.png", "millerB", "Miller Plane", "millerB",app);
		millerB.addActionListener(this);
		buttons.add(millerB);
		
		centerBox.add(buttons);
	
		drawingP.add(GUItools.createPanelForComponent(centerBox,"Input", new Dimension(app.status.rightBarWidht,320)));
		
		//------------------------------
		//	DRAW MANAGER
		//------------------------------
		
		Box managerBox = Box.createVerticalBox();
		
		drawablesTableModel = new DrawablesTM();
		drawablesTable = new JTable(drawablesTableModel);
		drawablesTable.setDragEnabled(false);
		drawablesTable.getModel().addTableModelListener(this);
		JScrollPane tableScroller = new JScrollPane(drawablesTable);

		managerBox.add(tableScroller);
		
		
		Box buttDown = Box.createHorizontalBox();
		delB = GUItools.makeNavigationButton("org/moka/images/list-remove.png", "delB", "Del", "delB",app);
		delB.addActionListener(this);
		buttDown.add(delB);
		
		reloadB = GUItools.makeNavigationButton("org/moka/images/arrow_rotate_anticlockwise.png", "reloadB", "Reload", "reloadB",app);
		reloadB.addActionListener(this);
		buttDown.add(reloadB);
		
		
		managerBox.add(buttDown,BorderLayout.SOUTH);
		
		
		drawingP.add(GUItools.createPanelForComponent(managerBox,"Manager", new Dimension(app.status.rightBarWidht,210)));
		
		this.add(drawingP);
		setSize(app.status.rightBarWidht, app.status.mokaDimesion.height);
		
		tableAllDrawables();
		
	}

	//------------------------
	//	Azioni
	//------------------------
	
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == addB) {
			
			addObject();
			tableAllDrawables();
		}
		
		if (e.getSource() == delB) {
			
			delObject(drawablesTable.getSelectedRow());
			tableAllDrawables();
			
		}
		
		if (e.getSource() == reloadB) {
			
			loadObjectData(drawablesTable.getSelectedRow());
			//tableAllDrawables();
			
		}
		
		if (e.getSource() == millerB) {
			
			fillVectors();
			
			int[] values = GUItools.getArrayIntFormUser("Miller plane", "[1 1 1]");
			
			app.workConf.addDrawable(getMillerPlane(values,off, perc, getColor()));
			app.viewer.drawDrawable(app.workConf.getLastDrawable());

			tableAllDrawables();
			
			app.log("Added Miller plane "+values[0]+values[1]+values[2]);
		}
		
	}

	public void tableChanged(TableModelEvent e) {
		
		if (app.status.isAppWaiting()) return;
		
		if (app.status.isWorking()) return;
		
		if (e.getType() == TableModelEvent.UPDATE) {
			int selected = e.getFirstRow();
			int column = e.getColumn();
			
			if (column == 1) {
				
				Boolean visibile = (Boolean)drawablesTableModel.getValueAt(selected, column);
				setObjectVisible(selected,visibile);
				
			}
			
			
		}
	}
	
	//------------------------
	//	MOUSE handling
	//------------------------
	
	public void doActionOnMouse(MouseEvent e, int eventType, int src) {
		// TODO Auto-generated method stub
		
		//Annulla tutti
		
		
		int onmask = MouseEvent.CTRL_DOWN_MASK | MouseEvent.BUTTON1_DOWN_MASK;
		int offmask = MouseEvent.SHIFT_DOWN_MASK;
		
		
		if (eventType == MOUSE_CLICKED) { //type 0 e' il canale del mouse
		
			
			if (100<=src && src<200) {
				
				src-=100;
				
				//Pulisco tutti i bordi
				for (int i=0; i<paramText.length; i++){
					paramText[i].setBorder(defaultBorder);
				}
				offsetT.setBorder(defaultBorder);
			
				
				//Setto 
				if (paramToSet!=src) {
					paramText[src].setBorder(new LineBorder(Color.red));					
					paramToSet = src;
				} else {
					paramToSet = -1;
				}
				
			}
			
			else if (src==200) {
				color = JColorChooser.showDialog(
		                this,
		                "Choose Color",
		                color);
				colorLabel.setBackground(color);
			}	
			
			
			//SELEZIONA
			else if (src==0 && paramToSet!=-1){
				
				int site = app.viewer.findNearestAtomIndex( e.getX(), e.getY() );
				if (!(site>=0 && site<app.workConf.getNAtoms())) {
					app.log("Atom not selectable.");
					return ; 
				}
				
				int check = ArrayTools.whereIsInArray(siteSelected, site);
				
				if (check==paramToSet && check!=-1) {
					app.viewer.colorAtom(siteSelected[paramToSet], "CPK");
					siteSelected[paramToSet] = -1;
					paramText[paramToSet].setText("");
					paramText[paramToSet].setBorder(defaultBorder);
					paramToSet=-1;
						
				} else if (check>=0) {
					app.log("Select a different atomo");
					return;

				} else {
				
					app.viewer.colorAtom(siteSelected[paramToSet], "CPK");
					siteSelected[paramToSet] = site;
					app.viewer.colorAtom(siteSelected[paramToSet], getColor(paramToSet));
					
					Point3f p = app.viewer.getAtomPoint3f(site);
					paramText[paramToSet].setText("["+String.format(Costants.decimalPlace8,p.x)
													+" "+String.format(Costants.decimalPlace8,p.y)
													+" "+String.format(Costants.decimalPlace8,p.z)+"]");
					
					paramText[paramToSet].setBorder(defaultBorder);
					//setMousePrecedence(false);
					paramToSet = -1;
				}
				
				
			}
		}

		
		
	} 
	
//	public void setMousePrecedence(boolean activate) {
//		
//		if (activate) {
//			app.log("Draw will grab mouse selection...");
//			app.mousePrecedence = this;
//		} else {
//			app.log("...done");
//			app.mousePrecedence = null;
//		}
//		
//	}
	
	public void reciveMouseSelection(int site) {
		
	}

	//------------------------
	//	IO / PRINTERS
	//------------------------
	
	public int[] getColor() {
		int[] rgb = new int[] {color.getRed(), color.getGreen(), color.getBlue()};
		return rgb;
	}
	
	public void fillVectors(){
		//Carica i vettori
		points = new double[coordinatesInput][];
		
		for (int i=0; i<coordinatesInput; i++){
			
			if (paramText[i].getText().trim().length()>0){
				points[i] = app.io.parseDoubleArrayWithMath(paramText[i].getText());
			}else {
				points[i] = null;
			}
			
		}
		
		if (offsetT.getText().trim().length()>0){
			off = app.io.parseDoubleArrayWithMath(offsetT.getText());
		} else {
			off = null;
		}
		
		if (percT.getText().trim().length()>0){
			perc = Integer.parseInt(percT.getText());
		} else {
			perc = 100;
		}
		
		if (alphaT.getText().trim().length()>0){
			alpha = Double.parseDouble(alphaT.getText());
		} else {
			alpha = 0.0;
		}
				
	}
	
	public void tableAllDrawables(){
		
		app.status.setWorkingOn();
		
		drawablesTableModel.clear();
		
		
		for (int i=0; i<app.workConf.getNumDrawable(); i++) {
			drawablesTableModel.addRow(new Object[] { app.workConf.getDrawable(i).getName(), new Boolean(app.workConf.getDrawable(i).isActive())});
		}
		
		app.status.setWorkingOff();
	}
	
	//------------------------
	//	OBJECTS
	//------------------------
	
	   public void addObject() {

        fillVectors();

        int objType = objTypeMenu.getSelectedIndex();
        String name = getFreeName(Costants.objTypeList[objTypeMenu.getSelectedIndex()]);


        //Casi speciali per perpendicolare e parallelo
        if (perpCheck.isSelected()) {

            if (drawablesTable.getSelectedRow() < 0) {
                app.alert("Select a reference object !");
                return;
            }


            if (app.workConf.getDrawable(drawablesTable.getSelectedRow()).getType() == Costants.oPLANE) {

                points[1] = getPointProjectionOnPlane(points[0], app.workConf.getDrawable(drawablesTable.getSelectedRow()));

                if (ArrayTools.isEqual(points[1], points[0])) {
                    fillVectors();
                    if (points[1]!=null) {
                        points[1] = getPointExtensionFromPlane(points[0], app.workConf.getDrawable(drawablesTable.getSelectedRow()), points[1]);
                    } else {
                        app.alert("Point on plane, select another ref. point !");
                        return;
                    }
                    
                }

            } else {
                app.alert("Not yet implemented...[ :( ]");
                return;
            }


        }

        if (objType == Costants.oCELL) {

            for (int i = 0; i < 3; i++) {
                for (int j = i + 1; j < 4; j++) {

                    if (points[i].equals(points[j])) {
                        app.alert("Select four different points !");
                        return;
                    }

                }
            }

        }

        app.workConf.addDrawable(new Drawable(objType, name, points, off, perc, getColor(), alpha, true));
        app.viewer.drawDrawable(app.workConf.getLastDrawable());
        app.log("Object added");


    }
	
	public void delObject(int toDel) {

		if (toDel<0 || toDel > app.workConf.getNumDrawable()) return;
		app.viewer.removeDrawable(app.workConf.getDrawable(toDel));
		app.workConf.delDrawable(toDel);
		
		tableAllDrawables();
		app.log("Object deleted");
		
	}
	
	public void setObjectVisible(int _id, boolean _status) {
		
		app.workConf.setDrawableVisible(_id, _status);
		if (_status) {
			app.viewer.drawDrawable(app.workConf.getDrawable(_id));
		} else {
			app.viewer.removeDrawable(app.workConf.getDrawable(_id));
		}
		
	}
	
	public void drawActiveObjects() {
		
		for (int i=0; i<app.workConf.getNumDrawable(); i++) {
			if (app.workConf.isDrawableVisible(i)) {
				app.viewer.drawDrawable(app.workConf.getDrawable(i));
			}
 		}
		
	}
	
	public void loadObjectData(int _obj) {
		
		if (_obj<0 || _obj > app.workConf.getNumDrawable()) return;
		
		double[][] points = app.workConf.getDrawable(_obj).getPoints();
		
		for (int i=0; i<coordinatesInput; i++){
		
			if (i>=points.length || points[i]==null) {
				paramText[i].setText("");
			} else {
				String point = "["+String.format(Costants.decimalPlace8,points[i][0])
				+" "+String.format(Costants.decimalPlace8,points[i][1])
				+" "+String.format(Costants.decimalPlace8,points[i][2])+"]";
		
				paramText[i].setText(point);
			}
			
			
		}
		
		if (app.workConf.getDrawable(_obj).getOffset()!=null){
			
			double[] offP = app.workConf.getDrawable(_obj).getOffset();
			
			offsetT.setText("["+String.format(Costants.decimalPlace8,offP[0])
							+" "+String.format(Costants.decimalPlace8,offP[1])
							+" "+String.format(Costants.decimalPlace8,offP[2])+"]");
			
		} else {
			offsetT.setText("");
		}
		
		percT.setText(app.workConf.getDrawable(_obj).getPerc()+"");
		alphaT.setText(app.workConf.getDrawable(_obj).getAlpha()+"");
		
		if (app.workConf.getDrawable(_obj).getColor()!=null) {
			int[] rgb = app.workConf.getDrawable(_obj).getColor();
			colorLabel.setBackground(new Color(rgb[0],rgb[1],rgb[2]));
		} else {
			colorLabel.setBackground(new Color(0,0,255));
		}
		
		
		
		
	}
	
	//------------------------
	//	COLOR
	//------------------------
	
	public String getColor(int pos) {
		
		int totColor = 4;
		int thisCol = pos%totColor;
		switch (thisCol) {
		case 0:	return "red";
		case 1:	return "blue";
		case 2:	return "green";
		case 3:	return "yellow";
		}
		return "CPK";
		
	}
	
//	public void updateColor() {
//		
//		String coloScripts ="";
//		
//		coloScripts = "select all; color CPK;";
//		
//		for (int i=0; i<siteSelected.length; i++) {
//			if (siteSelected[i]>=0) {
//				coloScripts+="select ATOMNO="+(siteSelected[i]+1)+"; color "+getColor(i)+";\n";
//			}
//		}
//		app.viewer.execCommand(coloScripts);
//		
//	}
	
	//--------------------
	//	SPECIAL OBJECTS
	//--------------------
	
	public String getFreeName(String objName) {
		
		int start = 1;

		boolean isPresent = false;
		do {
			
			for (int i =0; i<app.workConf.getNumDrawable(); i++) {
				String iName = app.workConf.getDrawable(i).getName();
				
				if ((objName+start).equals(iName)) {
					isPresent = true;
					start++;
				} else {
					isPresent = false;
				}
			}
			
		} while(isPresent);
		
		return (objName+start);
		
	}
	
	public double[] getPointProjectionOnPlane(double[] point, Drawable obj){
		
		double[][] vect = new double[2][];
		for (int i=0; i<2; i++){
			vect[i] = new double[] {obj.getPoints()[i+1][0] - obj.getPoints()[0][0], 
					   				obj.getPoints()[i+1][1] - obj.getPoints()[0][1],
					   				obj.getPoints()[i+1][2] - obj.getPoints()[0][2]	};
		}
		
		Point3D linePoint1 = new Point3D(point[0],point[1],point[2]);
		
		Plane3D plane = new Plane3D(new Point3D(obj.getPoints()[0][0],obj.getPoints()[0][1],obj.getPoints()[0][2]), 
									new Vector3D(new Point3D(vect[0][0],vect[0][1],vect[0][2])),
									new Vector3D(new Point3D(vect[1][0],vect[1][1],vect[1][2])));
		
		StraightLine3D line = new StraightLine3D(linePoint1,plane.getNormalVector());
		
		Point3D linePoint2 = plane.getLineIntersection(line);
		
		return new double[] {linePoint2.getX(),linePoint2.getY(),linePoint2.getZ()};
		
	}
	
	public double[] getPointExtensionFromPlane(double[] point, Drawable obj, double[] pointHeight){
		
		double[][] vect = new double[2][];
		for (int i=0; i<2; i++){
			vect[i] = new double[] {obj.getPoints()[i+1][0]- obj.getPoints()[0][0], obj.getPoints()[i+1][1]- obj.getPoints()[0][1],obj.getPoints()[i+1][2]- obj.getPoints()[0][2]};
		}
		
		Point3D linePoint1 = new Point3D(point[0],point[1],point[2]);
		Point3D linePointH = new Point3D(pointHeight[0],pointHeight[1],pointHeight[2]);
		
		Plane3D plane = new Plane3D(new Point3D(obj.getPoints()[0][0],obj.getPoints()[0][1],obj.getPoints()[0][2]), 
									new Vector3D(new Point3D(vect[0][0],vect[0][1],vect[0][2])),
									new Vector3D(new Point3D(vect[1][0],vect[1][1],vect[1][2])));
		
		StraightLine3D line = new StraightLine3D(linePoint1,plane.getNormalVector());
		
		Point3D linePoint2 = line.getPoint(line.getPosition(linePointH));
		
		return new double[] {linePoint2.getX(),linePoint2.getY(),linePoint2.getZ()};
		
	}
	
	public Point3f getMiddlePointParallelogram(Point3f p1, Point3f p2, Point3f p3) {
		
		double x1 = p1.x;
		double x2 = p2.x;
		double x3 = p3.x;
		
		double y1 = p1.y;
		double y2 = p2.y;
		double y3 = p3.y;
		
		double z1 = p1.z;
		double z2 = p2.z;
		double z3 = p3.z;
		
		double k =(x1*x1 + x2*x3 - x1*(x2 + x3) + (y1 - y2)*(y1 - y3) + (z1 - z2)*(z1 - z3))/(Math.pow((x1 - x2),2) + Math.pow((y1 - y2),2) + Math.pow((z1 - z2),2));

		double p4x = (x1 + k*(x1-x2));
		double p4y = (y1 + k*(y1-y2));
		double p4z = (z1 + k*(z1-z2));
		
		double midx = x1+ ((x1-x2)/2);
		double midy = y1+ ((y1-y2)/2);
		double midz = z1+ ((z1-z2)/2);
		
		double diffx = p4x - midx;
		double diffy = p4y - midy;
		double diffz = p4z - midz;
		
		Point3f out = new Point3f();
		
		out.x = (float) (x3+diffx);
		out.y = (float) (y3+diffy);
		out.z = (float) (z3+diffz);
		
		return out;
		
	}
	
	public Drawable getMillerPlane(int[] values, double[] off, int perc, int[] color) {
		
		if (color==null) color = new int[] {0,0,255};
		
		double[][] points;
		
		Point3f pt = new Point3f(values[0],values[1],values[2]);
	    
	    Point3f pt1 = new Point3f(pt.x == 0 ? 1 : 1 / pt.x, 0, 0);
	    Point3f pt2 = new Point3f(0, pt.y == 0 ? 1 : 1 / pt.y, 0);
	    Point3f pt3 = new Point3f(0, 0, pt.z == 0 ? 1 : 1 / pt.z);
	    //Point3f pt4 = new Point3f(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
	    
	    //trucco per 001 010 100, definisco l'altro punto sul bordo
	    if (pt.x == 0 && pt.y == 0 && pt.z == 0) {
	      return null; 
	    } else if (pt.x == 0 && pt.y == 0) {
	      pt1.set(1, 0, pt3.z);
	      pt2.set(0, 0, pt.z == 0 ? 1 : 1 / pt.z);
	      
	      pt3.set(0, 1, pt2.z);
	      
	      //pt4.set(1, 1, pt3.z);
	    } else if (pt.y == 0 && pt.z == 0) {
	      pt2.set(pt1.x, 0, 1);
	      pt3.set(pt1.x, 1, 0);
	      
	      //pt4.set(pt1.x, 1, 1);
	    } else if (pt.z == 0 && pt.x == 0) {
	      pt3.set(0, pt2.y, 1);
	      pt1.set(1, pt2.y, 0);
	      
	      //pt4.set(1, pt2.y, 1);
	    }
	    //trucco per 011 ecc
	    else if (pt.x == 0) {
	      pt1.set(1, pt2.y, 0);
	      
	    } else if (pt.y == 0) {
	      pt2.set(0, 0, pt.z == 0 ? 1 : 1 / pt.z);
	      pt3.set(0, 1, pt2.z);
	    } else if (pt.z == 0) {
	      pt3.set(pt1.x, 0, 1);
	    }
	    
	    pt3 = getMiddlePointParallelogram(pt1,pt2,pt3);
	    
//	    if (pt4.x != Integer.MAX_VALUE && pt4.z != Integer.MAX_VALUE && pt4.z != Integer.MAX_VALUE) {
//	    	points = new double[4][];
//	    	points[0] = app.workConf.getCell().toCartesian(new double[] {pt1.x,pt1.y,pt1.z});
//	 		points[1] = app.workConf.getCell().toCartesian(new double[] {pt2.x,pt2.y,pt2.z});
//	 		points[2] = app.workConf.getCell().toCartesian(new double[] {pt3.x,pt3.y,pt3.z});
//	 		points[3] = app.workConf.getCell().toCartesian(new double[] {pt4.x,pt4.y,pt4.z});
//	    } else {
	    	points = new double[3][];
	    	points[0] = app.workConf.getCell().toCartesian(new double[] {pt1.x,pt1.y,pt1.z});
	 		points[1] = app.workConf.getCell().toCartesian(new double[] {pt2.x,pt2.y,pt2.z});
	 		points[2] = app.workConf.getCell().toCartesian(new double[] {pt3.x,pt3.y,pt3.z});
	    //}
	    
		
		
		return new Drawable(Costants.oPLANE,
								getFreeName("miller_"+values[0]+values[1]+values[2]+"_"),
								points,
								off,
								perc,
								getColor(),
								0.2,
								true);
	}
	
	//--------------------
	//	MODULE FUNCTS
	//--------------------
	
	public void reset() {
	
		
		if (app.workConf.getDrawList().size()<1) return; 
		
		app.viewer.removeAllDrawables();
		tableAllDrawables();
		drawActiveObjects();
		
	}
	
	public void colorAtoms() {

		String coloScripts = "";
		
		coloScripts = "select all; color CPK;";
		
		int repX = app.status.getRepetitions()[0];
		int repY = app.status.getRepetitions()[1];
		int repZ = app.status.getRepetitions()[2];
		
		int repDone = 0;
		for (int stepX = 0; stepX<= repX; stepX++) {
			for (int stepY = 0; stepY<= repY; stepY++) {
				for (int stepZ = 0; stepZ<= repZ; stepZ++) {
			
					//	Setta i colori
					for (int i=0; i<siteSelected.length; i++) {
						if (siteSelected[i]>=0) {
							coloScripts+="select ATOMNO="+(siteSelected[i]+app.workConf.getNAtoms()*(repDone)+1)+"; color "+getColor(i)+";\n";
						}
					}					
					repDone++;

					
				}
			}
		}
		
		app.viewer.execCommand(coloScripts);
		
	}
	
	public void confChanged() {
		
		app.viewer.removeAllDrawables();
        tableAllDrawables();
        
		if (app.workConf.getDrawList().size()<1){
            
            drawActiveObjects();
        }
		
		
		
		
	}
	
	public void atomsChanged(int[] listChanged) {
		// TODO Auto-generated method stub
		
	} 
	
	public void setActive(boolean activate) {
		// TODO Auto-generated method stub
		if (activate) {
			
			app.viewer.removeAllMouseListeners();
			app.viewer.addMouseListener(new mouseAcceptor(this,0));
			
			app.status.setColorConroller(this);
			
		} else {
			
			app.viewer.removeAllMouseListeners();
			app.status.setColorConroller(null);
		}
		
		isActive = activate;
		
		app.viewer.refreshDraw(true);
	} 
	
	public boolean isActive() {
		return isActive;
	}
	
	class ClosingWindowAdapter extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			setVisible(false);
			System.exit(0);
		}
	}

	protected URL getURL(String fileName) {
		URLClassLoader urlLoader = (URLClassLoader)this.getClass().getClassLoader();
		URL fileLocation = urlLoader.findResource(fileName);
		return fileLocation;
	}

	
	class DrawablesTM extends AbstractTableModel {
        private String[] columnNames = {"Name",
                                        "Active"};
        private Object[][] tableData;

        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
        	if (tableData==null) return 0;
            return tableData.length;
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
            return tableData[row][col];
        }

        /*
         * JTable uses this method to determine the default renderer/
         * editor for each cell.  If we didn't implement this method,
         * then the last column would contain text ("true"/"false"),
         * rather than a check box.
         */
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        /*
         * Don't need to implement this method unless your table's
         * editable.
         */
        public boolean isCellEditable(int row, int col) {
            //Note that the data/cell address is constant,
            //no matter where the cell appears onscreen.
            if (col < 1) {
                return false;
            } else {
                return true;
            }
        }

        public void addRow(Object[] _row) {
        	
        	if (tableData!=null) {
		    	
        		Object[][] newdata = new Object[tableData.length+1][];
		    	
		    	for (int i =0; i<tableData.length; i++) {
		    		
		    		newdata[i] = new Object[tableData[i].length];
		    		
		    		for (int j =0; j<tableData[i].length; j++) {
		    			newdata[i][j] = tableData[i][j];
		    		}
		    	}
		    	
		    	newdata[tableData.length] = _row;
		    	
		    	this.tableData = newdata.clone();
        	
        	} else {
        		this.tableData = new Object[1][];
        		this.tableData[0] = _row;
        		
        	}
        	
        	fireTableDataChanged();
        }
        /*
         * Don't need to implement this method unless your table's
         * data can change.
         */
        public void setValueAt(Object value, int row, int col) {
            tableData[row][col] = value;
            fireTableCellUpdated(row, col);
        }
        
        public void removeRow(int row){
        	
        	if (getRowCount()==1) {
        		
        		this.tableData = null;
        		
        	} else {
        		
        		Object[][] newdata 	= new Object[tableData.length-1][];
		    	
        		int rowsCloned = 0;
		    	for (int i =0; i<tableData.length; i++) {
		    		if (i!=row) {
		    			newdata[rowsCloned] = tableData[i];
		    		}
		    	}
		    	
		    	this.tableData = newdata.clone();
        	}
        	
        	fireTableDataChanged();
        }	
        
        public void clear() {
        	this.tableData = null;
        	fireTableDataChanged();
        }
        
    }

}
