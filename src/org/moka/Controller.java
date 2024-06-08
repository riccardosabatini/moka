package org.moka;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.DecimalFormat;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.moka.common.Costants;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import org.moka.structures.Module;
import org.moka.structures.MokaController;
import org.moka.tools.ArrayTools;
import org.moka.tools.MathTools;
import org.moka.tools.events.mouseAcceptor;
import org.moka.tools.gui.GUItools;
import org.moka.tools.gui.SizedTextField;

/**
 * @author riki
 *
 */
public class Controller extends JPanel implements ActionListener, ChangeListener, ItemListener, Module, MokaController {

    public JTextArea logArea;
    Moka app;
    //Per stepper
    JRadioButton xyzR, axisR;
    JLabel[] tralsateLabels = new JLabel[3];
    JLabel[] rotateLabels = new JLabel[3];
    static final int refXYZ = 1;
    static final int refAXES = 2;
    int reference = refXYZ;
    JSlider shiftStep;
    SizedTextField shiftStepMax, transVector;
    JLabel shiftStepValue;
    DecimalFormat twoDecimals = new DecimalFormat("###.####");
    double diagFraction = 4;
    public double step = 0;

    //Per view
    JButton viewXY, viewYZ, viewXZ;
    public Checkbox drawCell,  drawAxes;
    public JTextField repetitions;
    boolean isActive = false;
    //int barWidth = 150, barHeigth = 400;

    public Controller(Moka _app) {

//		this.setFloatable(true);
//        this.setRollover(true);
//        this.setSize(150,400);

        this.app = _app;

        Box controllerP = Box.createVerticalBox();

        //-------------------------
        //  -> Kind of trasformation
        //-------------------------

        Box kindP = Box.createHorizontalBox();
        xyzR = new JRadioButton("XYZ");
        xyzR.setActionCommand("xyzR");
        xyzR.setSelected(true);
        xyzR.addActionListener(this);

        axisR = new JRadioButton("Crys. axes");
        axisR.setActionCommand("axisR");
        axisR.addActionListener(this);
        axisR.setSelected(false);
        ButtonGroup groupR = new ButtonGroup();
        groupR.add(xyzR);
        groupR.add(axisR);
        kindP.add(Box.createHorizontalGlue());
        kindP.add(xyzR);
        kindP.add(Box.createHorizontalGlue());
        kindP.add(axisR);
        kindP.add(Box.createHorizontalGlue());
        controllerP.add(GUItools.createPanelForComponent(kindP, "Reference", new Dimension(app.status.rightBarWidht, 50)));

        //-------------------------
        //  -> Mouse tools
        //-------------------------

//        Box mouseTools = Box.createVerticalBox();
//        
//        mouseToolBox = new JComboBox(modesSelectable);
//        mouseToolBox.setSelectedIndex(0);
//        mouseToolBox.addActionListener(this);
//        
//        mouseTools.add(mouseToolBox);
//        mouseTools.setComponentZOrder(mouseToolBox, 0);
//        mouseTools.add(Box.createVerticalGlue());
//        
//        
//        controllerP.add(GUItools.createPanelForComponent(mouseTools,"Mouse tools", new Dimension(app.status.rightBarWidht,60)));
        //----------------------------------------
        //	Pannello di movimenti
        //----------------------------------------

        //X
        JButton xM, xP, yM, yP, zM, zP;

        tralsateLabels[0] = new JLabel(" x ");
        tralsateLabels[1] = new JLabel(" y ");
        tralsateLabels[2] = new JLabel(" z ");

        xM = GUItools.makeNavigationButton("org/moka/images/list-remove.png", "xM", "X minus", "xM", app);
        xM.setActionCommand("xM");
        xM.addActionListener(this);

        xP = GUItools.makeNavigationButton("org/moka/images/list-add.png", "xP", "X plus", "xP", app);
        xP.setActionCommand("xP");
        xP.addActionListener(this);

        Box xBp = Box.createHorizontalBox();
        xBp.add(xM);
//		xBp.add(Box.createHorizontalGlue());
        xBp.add(tralsateLabels[0]);
//		xBp.add(Box.createHorizontalGlue());
        xBp.add(xP);

        //Y
        yM = GUItools.makeNavigationButton("org/moka/images/list-remove.png", "yM", "Y minus", "yM", app);
        yM.setActionCommand("yM");
        yM.addActionListener(this);

        yP = GUItools.makeNavigationButton("org/moka/images/list-add.png", "yP", "Y plus", "yP", app);
        yP.setActionCommand("yP");
        yP.addActionListener(this);

        Box yBp = Box.createHorizontalBox();
        yBp.add(yM);
        //yBp.add(Box.createHorizontalGlue());
        yBp.add(tralsateLabels[1]);
        //yBp.add(Box.createHorizontalGlue());
        yBp.add(yP);

        //Z
        zM = GUItools.makeNavigationButton("org/moka/images/list-remove.png", "zM", "Z minus", "zM", app);
        zM.setActionCommand("zM");
        zM.addActionListener(this);

        zP = GUItools.makeNavigationButton("org/moka/images/list-add.png", "zP", "Z plus", "zP", app);
        zP.setActionCommand("zP");
        zP.addActionListener(this);

        Box zBp = Box.createHorizontalBox();
        zBp.add(zM);
        //zBp.add(Box.createHorizontalGlue());
        zBp.add(tralsateLabels[2]);
        //zBp.add(Box.createHorizontalGlue());
        zBp.add(zP);

        Box xyzB = Box.createVerticalBox();
        xyzB.add(xBp);
        xyzB.add(Box.createVerticalGlue());
        xyzB.add(yBp);
        xyzB.add(Box.createVerticalGlue());
        xyzB.add(zBp);
        xyzB.add(Box.createVerticalGlue());

        controllerP.add(GUItools.createPanelForComponent(xyzB, "Move", new Dimension(app.status.rightBarWidht, 120)));


        //----------------------------------------
        //	Pannello di rotazione
        //----------------------------------------
        JButton aM, aP, bM, bP, gM, gP;

        rotateLabels[0] = new JLabel(" x ");
        rotateLabels[1] = new JLabel(" y ");
        rotateLabels[2] = new JLabel(" z ");
        //X
        aM = GUItools.makeNavigationButton("org/moka/images/arrow_rotate_anticlockwise.png", "aM", "A anticlockwise", "aM", app);
        aM.setActionCommand("aM");
        aM.addActionListener(this);

        aP = GUItools.makeNavigationButton("org/moka/images/arrow_rotate_clockwise.png", "aP", "A clockwise", "aP", app);
        aP.setActionCommand("aP");
        aP.addActionListener(this);

        Box aBp = Box.createHorizontalBox();
        aBp.add(aM);
        aBp.add(rotateLabels[0]);
        aBp.add(aP);

        //Y
        bM = GUItools.makeNavigationButton("org/moka/images/arrow_rotate_anticlockwise.png", "bM", "B anticlockwise", "bM", app);
        bM.setActionCommand("bM");
        bM.addActionListener(this);

        bP = GUItools.makeNavigationButton("org/moka/images/arrow_rotate_clockwise.png", "bP", "B clockwise", "bP", app);
        bP.setActionCommand("bP");
        bP.addActionListener(this);

        Box bBp = Box.createHorizontalBox();
        bBp.add(bM);
        bBp.add(rotateLabels[1]);
        bBp.add(bP);

        //Z
        gM = GUItools.makeNavigationButton("org/moka/images/arrow_rotate_anticlockwise.png", "gM", "G anticlockwise", "gM", app);
        gM.setActionCommand("gM");
        gM.addActionListener(this);

        gP = GUItools.makeNavigationButton("org/moka/images/arrow_rotate_clockwise.png", "gP", "G clockwise", "gP", app);
        gP.setActionCommand("gP");
        gP.addActionListener(this);

        Box gBp = Box.createHorizontalBox();
        gBp.add(gM);
        gBp.add(rotateLabels[2]);
        gBp.add(gP);

        Box abgB = Box.createVerticalBox();
        abgB.add(Box.createVerticalGlue());
        abgB.add(aBp);
        abgB.add(Box.createVerticalGlue());
        abgB.add(bBp);
        abgB.add(Box.createVerticalGlue());
        abgB.add(gBp);
        abgB.add(Box.createVerticalGlue());

        controllerP.add(GUItools.createPanelForComponent(abgB, "Rotate", new Dimension(app.status.rightBarWidht, 120)));
        //----------------------------------------
        //	Pannello per SLIDER
        //----------------------------------------

        shiftStepValue = new JLabel("1.0");
        shiftStepMax = new SizedTextField(9);
        shiftStepMax.setText("2.0");

        shiftStep = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
        shiftStep.addChangeListener(this);
        shiftStep.setMajorTickSpacing(10);
        shiftStep.setMinorTickSpacing(1);

        //setLabelShift(shiftStep);
        setStepValue(shiftStep);

        Box shiftStepAll = Box.createVerticalBox();
        shiftStepAll.add(shiftStep);

        Box shiftStepDown = Box.createHorizontalBox();
        shiftStepDown.add(Box.createHorizontalGlue());
        shiftStepDown.add(shiftStepValue);
        shiftStepDown.add(Box.createHorizontalGlue());
        shiftStepDown.add(shiftStepMax);

        shiftStepAll.add(shiftStepDown);

        controllerP.add(GUItools.createPanelForComponent(shiftStepAll, "Step", new Dimension(app.status.rightBarWidht, 90)));

        //----------------------------------------
        //	Pannello per VETTORE
        //----------------------------------------

        JButton transM, transP;
        transVector = new SizedTextField(20);
        transVector.setText("[0.0 0.0 0.0]");

        transM = GUItools.makeNavigationButton("org/moka/images/list-remove.png", "transM", "Sub vector", "transM", app);
        transM.setActionCommand("transM");
        transM.addActionListener(this);

        transP = GUItools.makeNavigationButton("org/moka/images/list-add.png", "transP", "Add vector", "transP", app);
        transP.setActionCommand("transP");
        transP.addActionListener(this);

        Box translateVectB = Box.createHorizontalBox();
        translateVectB.add(transM);
        translateVectB.add(transP);

        Box translateVectAll = Box.createVerticalBox();
        translateVectAll.add(transVector);
        translateVectAll.add(Box.createVerticalGlue());
        translateVectAll.add(translateVectB);

        controllerP.add(GUItools.createPanelForComponent(translateVectAll, "Translation", new Dimension(app.status.rightBarWidht, 80)));


        //----------------------------------------
        //	Pannello per VIEWER
        //----------------------------------------


//		Box viewB = Box.createVerticalBox();
//		
//		Box typeViewBox = Box.createHorizontalBox();
//		viewXY= new JButton("xy");
//		viewXY.setActionCommand("viewXY");
//		viewXY.addActionListener(this);
//		//viewXY.setPreferredSize(new Dimension(30,30));
//		//viewXY.setMinimumSize(new Dimension(55,40));
//		typeViewBox.add(viewXY);
//		
//		viewXZ= new JButton("xz");
//		viewXZ.setActionCommand("viewXZ");
//		viewXZ.addActionListener(this);
//		//viewXZ.setMinimumSize(new Dimension(55,40));
//		typeViewBox.add(viewXZ);
//		
//		viewYZ= new JButton("yz");
//		viewYZ.setActionCommand("viewYZ");
//		viewYZ.addActionListener(this);
//		//viewYZ.setMinimumSize(new Dimension(55,40));
//		typeViewBox.add(viewYZ);
//		
//		viewB.add(typeViewBox);
//		
//		Box checkViewBox = Box.createHorizontalBox();
//		checkViewBox.add(Box.createHorizontalGlue());
//		drawCell = new Checkbox("Cell", null, true);
//		drawCell.addItemListener(this);
//		checkViewBox.add(Box.createHorizontalGlue());
//		checkViewBox.add(drawCell);
//		checkViewBox.add(Box.createHorizontalGlue());
//		
//		drawAxes = new Checkbox("Axes", null, false);
//		drawAxes.addItemListener(this);
//		checkViewBox.add(drawAxes);
//		checkViewBox.add(Box.createHorizontalGlue());
//		
//		viewB.add(checkViewBox);
//		
//		Box repBox = Box.createHorizontalBox();
//		repBox.add(new JLabel("Rep:"));
//		
//		repetitions = new JTextField("0 0 0");
//		repetitions.addActionListener(this);
//		repetitions.setMaximumSize(new Dimension(60,20));
//		repBox.add(repetitions);
//		viewB.add(repBox);
//		
//		
//		controllerP.add(GUItools.createPanelForComponent(viewB, "View", new Dimension(barWidth,150)));
//		
//		//-----------in FONDO
//		controllerP.add(Box.createVerticalGlue());
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
//		atomsStep = new JTextField("0");
//		stepPanel.add(atomsStep);
//		
//		totAtomsStep = new JLabel("/ 0");
//		stepPanel.add(totAtomsStep);
//		
//		confNext = makeNavigationButton("Forward16.gif", "confNext", "Next conf", "Next");
//		confNext.addActionListener(this);
//		//confNext.setPreferredSize(new Dimension(30,50));
//		stepPanel.add(confNext);
//		
//		controllerP.add(GUItools.createPanelForComponent(stepPanel, "Confs", new Dimension(barWidth,50)));
//		

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

        this.add(controllerP, BorderLayout.CENTER);


    }

    public void actionPerformed(ActionEvent e) {

        if (app.status.isAppWaiting()) {
            return;
        }

        String actionCommand = e.getActionCommand();
//		if (e.getSource() == mouseToolBox) {
//			
//			if (app.status.isWorking()) return;
//			
//			int modeSelected = mouseToolBox.getSelectedIndex();
//			switch (mouseToolBox.getSelectedIndex()) {
//				case 0 : app.setEditingMode(Costants.mTRANSFORM); break;
//				case 1 : app.setEditingMode(Costants.mBLOCK); break;
//				case 2 : app.setEditingMode(Costants.mDELETE); break;
//			}
//				
//			
//			
//		}

//		if (app.status.getEditingMode()==Costants.mTRANSFORM &&
//				(e.getSource() == xM || e.getSource() == xP ||e.getSource() == yM||e.getSource() == yP||e.getSource() == zM ||e.getSource() == zP)) {

        if (actionCommand.equals("xyzR")) {

            tralsateLabels[0].setText(" x ");
            tralsateLabels[1].setText(" y ");
            tralsateLabels[2].setText(" z ");

            reference = refXYZ;

        }

        if (actionCommand.equals("axisR")) {

            tralsateLabels[0].setText(" a1 ");
            tralsateLabels[1].setText(" a2 ");
            tralsateLabels[2].setText(" a3 ");

            reference = refAXES;

        }


        if (actionCommand.equals("xM") || actionCommand.equals("xP") ||
                actionCommand.equals("yM") || actionCommand.equals("yP") ||
                actionCommand.equals("zM") || actionCommand.equals("zP")) {
            //Becca tutte le righe selezionate
            int[] allSelected = app.workConf.getAtomsFlagged(Costants.aSELECTED);

            double[] vectTrans = {0, 0, 0};

            int ax = 0;
            double sign = 1.0;
            if (actionCommand.contains("x")) ax = 0;
            if (actionCommand.contains("y")) ax = 1;
            if (actionCommand.contains("z")) ax = 2;

            if (actionCommand.contains("M")) sign = -1.0;
            if (actionCommand.contains("P")) sign = 1.0;

            if (reference == refXYZ) {
                vectTrans[ax] = sign * step;

            }

            if (reference == refAXES) {


                double angleT = app.workConf.getCell().getAxAngleWithXYZ(ax, 0);
                vectTrans[0] = sign * step * Math.cos(angleT);

                double angleA = app.workConf.getCell().getAxAngleWithXYZ(ax, 1);
                vectTrans[1] = sign * step * Math.cos(angleA);

                double angleP = app.workConf.getCell().getAxAngleWithXYZ(ax, 2);
                vectTrans[2] = sign * step * Math.cos(angleP);


            }

            app.workConf.traslateAtomCoord(allSelected, vectTrans);
            app.viewer.traslateAtoms(allSelected, vectTrans);

            app.atomsChanged(allSelected);

        }


        if (actionCommand.equals("aM") || actionCommand.equals("aP") ||
                actionCommand.equals("bM") || actionCommand.equals("bP") ||
                actionCommand.equals("gM") || actionCommand.equals("gP")) {

            //Becca tutte le righe selezionate
            int[] allSelected = app.workConf.getAtomsFlagged(Costants.aSELECTED);

            int ax = 0;
            double sign = 1.0;
            if (actionCommand.contains("a")) ax = 0;
            if (actionCommand.contains("b")) ax = 1;
            if (actionCommand.contains("g")) ax = 2;

            if (actionCommand.contains("M")) sign = -1.0;
            if (actionCommand.contains("P")) sign = 1.0;

            double angle = sign* (2*Math.PI/360) * step;

            
            if (allSelected.length < 2) {
                JOptionPane.showMessageDialog(app.frame, "Selezionare almeno due atomi.");
            } else {

                double[] rotAx = new double[3];

                if (reference == refXYZ)  rotAx[ax] = 1;
                if (reference == refAXES) rotAx = app.workConf.getCell().getCellAngstrom()[ax];

                app.viewer.rotateAtomCoordAboutAxis(allSelected,
                                                        rotAx,
                                                        app.workConf.getBaricenterSelected(allSelected, Costants.cANGSTROM),
                                                        angle);

                app.workConf.rotateAtomCoordAboutAxis(allSelected,
                                                            rotAx,
                                                            app.workConf.getBaricenterSelected(allSelected, -1),
                                                            angle);

            //app.tableFrame.tableAllArray();
            //app.tableFrame.selectTable();
            }
            app.atomsChanged(allSelected);

        }

//		if (app.status.getEditingMode()==Costants.mTRANSFORM &&
//				(e.getSource() == transM || e.getSource() == transP)) {

        if (actionCommand.equals("transM") || actionCommand.equals("transP")) {

            double[] vect = {0, 0, 0};
            int[] allSelected = app.workConf.getAtomsFlagged(Costants.aSELECTED);

            String[] parts = transVector.getText().trim().substring((transVector.getText().charAt(0) == "[".charAt(0) ? 1 : 0), transVector.getText().charAt(transVector.getText().length() - 1) == "]".charAt(0) ? transVector.getText().length() - 2 : transVector.getText().length() - 1).split(" ");
            if (parts.length == 3) {
                vect[0] = Double.parseDouble(parts[0]);
                vect[1] = Double.parseDouble(parts[1]);
                vect[2] = Double.parseDouble(parts[2]);

                if (actionCommand.equals("transM")) {
                    vect = MathTools.arrayMultiply(vect, -1.0);
                }

                app.workConf.traslateAtomCoord(allSelected, vect);
                app.viewer.traslateAtoms(allSelected, vect);
                app.atomsChanged(allSelected);

                app.log("Vettore di traslazione:" + ArrayTools.elegantPrint(vect));
            }


        }

//		if (e.getSource() == confPrev) {
//
//			int thisStep = Integer.valueOf(atomsStep.getText());
//			if (thisStep>1) {
//				app.storeConf();
//				app.selectConf((thisStep-1)-1);
//				app.confChanged();		
//			}
//			
//		}
//
//		if (e.getSource() == confNext) {
//
//			int thisStep = Integer.valueOf(atomsStep.getText());
//			if (thisStep<app.confDB.size()) {
//				app.storeConf();
//				app.selectConf((thisStep-1)+1);
//				app.confChanged();
//			}
//	
//		}
//		
//		//Viewer translations
//		if (app.status.getEditingMode()==CONST.mNORMAL &&
//				(e.getSource() == xM || e.getSource() == xP ||e.getSource() == yM||e.getSource() == yP)) {
//
//			String command = "shiftView ";
//			
//			if (e.getSource() == xM) { command += "x m "+(step)+";"; }
//			if (e.getSource() == xP) { command += "x p "+(step)+";"; }
//			if (e.getSource() == yM) { command += "y m "+(step)+";"; }
//			if (e.getSource() == yP) { command += "y p "+(step)+";"; }
//						
//			app.viewer.execCommand(command);
//		}
//		
//		if (app.status.getEditingMode()==CONST.mNORMAL &&
//				(e.getSource() == zM ||e.getSource() == zP)) {
//			
//			String command = "zoom ";
//			if (e.getSource() == zM) { command = "zoom m "+(step)+";"; }
//			if (e.getSource() == zP) { command = "zoom p "+(step)+";"; }
//			
//			app.viewer.execCommand(command);
//		}
//
//		//Viewer rotations
//		if (app.status.getEditingMode()==CONST.mNORMAL &&
//				(e.getSource() == aM || e.getSource() == aP ||e.getSource() == bM||e.getSource() == bP||e.getSource() == gM ||e.getSource() == gP)) {
//			
//			String command = "rotate ";
//			
//			if (e.getSource() == aM) { command += "x -"+step+";"; }
//			if (e.getSource() == aP) { command += "x "+step+";"; }
//			if (e.getSource() == bM) { command += "y -"+step+";"; }
//			if (e.getSource() == bP) { command += "y "+step+";"; }
//			if (e.getSource() == gM) { command += "z -"+step+";"; }
//			if (e.getSource() == gP) { command += "z "+step+";"; }
//
//			app.viewer.execCommand(command);
//		}
//		
//		
//			
//		if (e.getSource() == viewXY) { app.viewer.setView(0); }
//		if (e.getSource() == viewXZ) { app.viewer.setView(1); }
//		if (e.getSource() == viewYZ) { app.viewer.setView(2); }
//	
//		if (e.getSource() == repetitions) {
//
//			app.viewer.execCommand("setRepetitions "+repetitions.getText());
//			app.viewer.refreshDraw(false);
//	
//		}



    }

    //-----------------
    //Per slider
    //-----------------
    public void stateChanged(ChangeEvent e) {
        if (app.status.isAppWaiting()) {
            return;
        }

        JSlider source = (JSlider) e.getSource();
        if (source.getValueIsAdjusting()) {
            setLabelShift(source);
        }
        if (!source.getValueIsAdjusting()) {
            setStepValue(source);
        }
    }

    public void setLabelShift(JSlider source) {

        int thisValue = (int) source.getValue();
        String valueStr = twoDecimals.format((Double.parseDouble(shiftStepMax.getText()) / 100) * thisValue);
        shiftStepValue.setText(valueStr);

//		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
//		//labelTable.put( new Integer( 0 ), new JLabel("0") );
//		labelTable.put( new Integer( thisValue ), new JLabel(valueStr));
//		//labelTable.put( new Integer( 100 ), new JLabel("Cell") );
//
//		source.setLabelTable( labelTable );
//		source.setPaintLabels(true);
//		source.setPaintTicks(true);
    }

    public void setStepValue(JSlider source) {

        int thisValue = (int) source.getValue();
        step = (Double.parseDouble(shiftStepMax.getText()) / 100) * thisValue;

    }

    public void itemStateChanged(ItemEvent e) {
        if (app.status.isAppWaiting()) {
            return;
        }

        Object source = e.getItemSelectable();

        if (source == drawCell) {
            if (drawCell.getState()) {
                app.showCell = true;
            }
            if (!drawCell.getState()) {
                app.showCell = false;
            }
            app.viewer.refreshDraw(true);

        } else if (source == drawAxes) {
            if (drawAxes.getState()) {
                app.viewer.execCommand("setAxes on");
            }
            if (!drawAxes.getState()) {
                app.viewer.execCommand("setAxes off");
            }

        }

    }

    //---------------------------------
    //	FUNZIONE DEL MODULO
    //---------------------------------
    public void setActive(boolean status) {

        if (status) {
            app.viewer.removeAllMouseListeners();
            app.viewer.addMouseListener(new mouseAcceptor(app.viewcontroller, 0));
        } else {
            app.viewer.removeAllMouseListeners();
        }

        isActive = status;
    }

    public boolean isActive() {
        return isActive;
    }

    public void reset() {

        app.status.setWorkingOn();
        //mouseToolBox.setSelectedIndex(0);
        app.status.setWorkingOff();
//		//Cell
//		if (app.atoms.cell!=null) {
//			statusCell.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.green));
//		}else {
//			statusCell.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.red));
//		}
//		
//		//Atoms
//		if (app.atoms.positions!=null) {
//			statusAtoms.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.green));
//		} else {
//			statusAtoms.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.red));
//		}
//	
//		//CoordType
//		statusCoord.setText(coordManageCONST.coordList[app.atoms.coordType]);


    //Num conf
//		totAtomsStep.setText("/ "+app.confDB.size());
//		atomsStep.setText(""+(app.thisConf+1));

    }

    public void confChanged() {
        //Cell
//		if (app.atoms.cell!=null) {
//			statusCell.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.green));
//		}else {
//			statusCell.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.red));
//		}
//		
//		//Atoms
//		if (app.atoms.positions!=null) {
//			statusAtoms.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.green));
//		} else {
//			statusAtoms.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.red));
//		}
//	
//		//CoordType
//		statusCoord.setText(coordManageCONST.coordList[app.atoms.coordType]);
        //Num conf
//		totAtomsStep.setText("/ "+app.confDB.size());
//		atomsStep.setText(""+(app.thisConf+1));
//		
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

    /* (non-Javadoc)
     * @see structures.MokaController#colorAtoms()
     */
    public void colorAtoms() {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see structures.MokaController#setActive(boolean)
     */
}
