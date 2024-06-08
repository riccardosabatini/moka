package org.moka;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import org.moka.common.Costants;

import org.moka.structures.Cell;
import org.moka.structures.Module;
import org.moka.tools.gui.GUItools;


/**
 * @author riki
 *
 */
public class CellEditor extends JFrame implements ActionListener, Module {

	JTextField[] paramText = new JTextField[6];
	JTextField[] cellText = new JTextField[9];
	
	JComboBox cellTypeMenu;
	JButton fromAppB, toAppB, reascaleAB;
	Border defaultBorder;
	
	public Moka app;
    
	int cellType = 0;
	
	Cell cellTemp;
	
	//------------------------
	//	INTERFACCIA
	//------------------------
	public CellEditor(Moka _app) {
		
		JPanel framePanel = new JPanel();
		setContentPane(framePanel);
		getContentPane().setLayout(new BorderLayout());
		
		this.app = _app;
		
		//------------------------------
		// Type panel
		//------------------------------

		cellTypeMenu = new JComboBox(Costants.cellTypes);
		cellTypeMenu.setSelectedIndex(0);
		cellTypeMenu.addActionListener(this);
		
		Box typeBox = Box.createHorizontalBox();
		typeBox.add(new JLabel("ibrav: "));
		typeBox.add(cellTypeMenu);
		
		JPanel typePanel = GUItools.createPanelForComponent(typeBox, "Cell type",new Dimension(380,60));
		this.getContentPane().add(typePanel, BorderLayout.PAGE_START);

		//------------------------------
		//Parameters Box
		//------------------------------
		int textWparam = 80;
		
		JPanel col1 = new JPanel();
		col1.setLayout(new GridLayout(2,1));
		col1.add(new JLabel("a"));
		col1.add(new JLabel("(4)"));
		
		JPanel col2 = new JPanel();
		col2.setLayout(new GridLayout(2,1));
		paramText[0] = new JTextField();
		paramText[0].setColumns(10);
		paramText[0].setPreferredSize(new Dimension(textWparam,30));
		paramText[0].setMinimumSize(new Dimension(textWparam,30));
		paramText[3] = new JTextField();
		paramText[3].setColumns(10);
		paramText[3].setPreferredSize(new Dimension(textWparam,30));
		paramText[3].setMinimumSize(new Dimension(textWparam,30));
		col2.add(paramText[0]);
		col2.add(paramText[3]);
		
		JPanel col3 = new JPanel();
		col3.setLayout(new GridLayout(2,1));
		col3.add(new JLabel("b/a"));
		col3.add(new JLabel("(5)"));
		
		JPanel col4 = new JPanel();
		col4.setLayout(new GridLayout(2,1));
		paramText[1] = new JTextField();
		paramText[1].setColumns(10);
		paramText[1].setPreferredSize(new Dimension(textWparam,30));
		paramText[1].setMinimumSize(new Dimension(textWparam,30));
		paramText[4] = new JTextField();
		paramText[4].setColumns(10);
		paramText[4].setPreferredSize(new Dimension(textWparam,30));
		paramText[4].setMinimumSize(new Dimension(textWparam,30));
		col4.add(paramText[1]);
		col4.add(paramText[4]);
		
		JPanel col5 = new JPanel();
		col5.setLayout(new GridLayout(2,1));
		col5.add(new JLabel("c"));
		col5.add(new JLabel("(6)"));
		
		JPanel col6 = new JPanel();
		col6.setLayout(new GridLayout(2,1));
		paramText[2] = new JTextField();
		paramText[2].setColumns(10);
		paramText[2].setPreferredSize(new Dimension(textWparam,30));
		paramText[2].setMinimumSize(new Dimension(textWparam,30));
		paramText[5] = new JTextField();
		paramText[5].setColumns(10);
		paramText[5].setPreferredSize(new Dimension(textWparam,30));
		paramText[5].setMinimumSize(new Dimension(textWparam,30));
		col6.add(paramText[2]);
		col6.add(paramText[5]);
		
		Box paramBox = Box.createHorizontalBox();
		paramBox.add(col1);
		paramBox.add(col2);
		paramBox.add(col3);
		paramBox.add(col4);
		paramBox.add(col5);
		paramBox.add(col6);
		
		//------------------------------
		//Cell Basis Box
		//------------------------------
		int textWcell = 80;
		
		JPanel col1cell = new JPanel();
		col1cell.setLayout(new GridLayout(3,1));
		col1cell.add(new JLabel("v1"));
		col1cell.add(new JLabel("v2"));
		col1cell.add(new JLabel("v3"));
		
		JPanel col2cell = new JPanel();
		col2cell.setLayout(new GridLayout(3,1));
		cellText[0] = new JTextField();
		cellText[0].setColumns(10);
		cellText[0].setPreferredSize(new Dimension(textWcell,30));
		cellText[0].setMinimumSize(new Dimension(textWcell,30));
		cellText[3] = new JTextField();
		cellText[3].setColumns(10);
		cellText[3].setPreferredSize(new Dimension(textWcell,30));
		cellText[3].setMinimumSize(new Dimension(textWcell,30));
		cellText[6] = new JTextField();
		cellText[6].setColumns(10);
		cellText[6].setPreferredSize(new Dimension(textWcell,30));
		cellText[6].setMinimumSize(new Dimension(textWcell,30));
		col2cell.add(cellText[0]);
		col2cell.add(cellText[3]);
		col2cell.add(cellText[6]);
		
		JPanel col3cell = new JPanel();
		col3cell.setLayout(new GridLayout(3,1));
		cellText[1] = new JTextField();
		cellText[1].setColumns(10);
		cellText[1].setPreferredSize(new Dimension(textWcell,30));
		cellText[1].setMinimumSize(new Dimension(textWcell,30));
		cellText[4] = new JTextField();
		cellText[4].setColumns(10);
		cellText[4].setPreferredSize(new Dimension(textWcell,30));
		cellText[4].setMinimumSize(new Dimension(textWcell,30));
		cellText[7] = new JTextField();
		cellText[7].setColumns(10);
		cellText[7].setPreferredSize(new Dimension(textWcell,30));
		cellText[7].setMinimumSize(new Dimension(textWcell,30));
		col3cell.add(cellText[1]);
		col3cell.add(cellText[4]);
		col3cell.add(cellText[7]);
		
		JPanel col4cell = new JPanel();
		col4cell.setLayout(new GridLayout(3,1));
		cellText[2] = new JTextField();
		cellText[2].setColumns(10);
		cellText[2].setPreferredSize(new Dimension(textWcell,30));
		cellText[2].setMinimumSize(new Dimension(textWcell,30));
		cellText[5] = new JTextField();
		cellText[5].setColumns(10);
		cellText[5].setPreferredSize(new Dimension(textWcell,30));
		cellText[5].setMinimumSize(new Dimension(textWcell,30));
		cellText[8] = new JTextField();
		cellText[8].setColumns(10);
		cellText[8].setPreferredSize(new Dimension(textWcell,30));
		cellText[8].setMinimumSize(new Dimension(textWcell,30));
		col4cell.add(cellText[2]);
		col4cell.add(cellText[5]);
		col4cell.add(cellText[8]);

		Box cellBox = Box.createHorizontalBox();
		cellBox.add(col1cell);
		cellBox.add(col2cell);
		cellBox.add(col3cell);
		cellBox.add(col4cell);
		
		Box centerBox = Box.createVerticalBox();
		centerBox.add(GUItools.createPanelForComponent(paramBox, "Parameters",new Dimension(380,90)));
		centerBox.add(GUItools.createPanelForComponent(cellBox, "Cell Basis",new Dimension(380,120)));
		
		this.getContentPane().add(centerBox, BorderLayout.CENTER);
		
		Box buttonBox = Box.createHorizontalBox();
		
		fromAppB = new JButton("Get from app");
		fromAppB.addActionListener(this);
		buttonBox.add(fromAppB);
		
		toAppB = new JButton("Send to app");
		toAppB.addActionListener(this);
		buttonBox.add(toAppB);
		
		reascaleAB = new JButton("Change a");
		reascaleAB.addActionListener(this);
		buttonBox.add(reascaleAB);
		
		this.getContentPane().add(GUItools.createPanelForComponent(buttonBox, "",new Dimension(350,60)), BorderLayout.PAGE_END);
		setSize(400,600);
		
		
		defaultBorder = paramText[0].getBorder();
		getFromApp();
	}

	//------------------------
	//	Azioni
	//------------------------
	
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == cellTypeMenu) {
			
			cellType = cellTypeMenu.getSelectedIndex();
			setCellEdit(cellType);
			setParamEdit(cellType);
			
		}

		if (e.getSource() == fromAppB) {
			
			getFromApp();
			
		}
		
		if (e.getSource() == toAppB) {
			
			if (!genCell()) { reset(); return; };
			
			if ( sendToApp() ) {
				app.storeConf();
				app.reset();
			} else {
				reset();
			}
			
		}
		
		if (e.getSource() == reascaleAB) {
			
			Double newA = Double.parseDouble(GUItools.getStrFormUser("New a", Costants.cSPECIALPARAM+""));
			
			app.workConf.rescaleCellA(newA);
			
			app.storeConf();
			app.reset();
		}
		
	}
	
	//------------------------------------------------
	//	Aggiorna che field sono editabili o meno
	//------------------------------------------------
	
	public void setCellEdit(int _type) {
		
		if (_type==0) {
			for (int i=0; i<9; i++) {
				cellText[i].setEditable(true);
				cellText[i].setBorder(new LineBorder(Color.red));
			}
		} else {
			for (int i=0; i<9; i++) {
				cellText[i].setEditable(false);
				cellText[i].setBorder(defaultBorder);
			}
		}
	}
	
	public void setParamEdit(int type) {
		for (int i=0; i<6; i++) {
			if (Costants.paramtersEditablePerCellType[type][i] == 0) {
				paramText[i].setEditable(false);
				paramText[i].setBorder(defaultBorder);
				
			}
			if (Costants.paramtersEditablePerCellType[type][i] == 1) {
				paramText[i].setEditable(true);
				paramText[i].setBorder(new LineBorder(Color.red));
			}
		}
	}
	
	//------------------------------------------------
	//	GENERATORI 
	//------------------------------------------------
	
	public boolean genCell() {
		
		if (cellType==0) {
			cellTemp = new Cell(cellType, arrayParameters(),arrayCell());
		} else {
			cellTemp = new Cell(cellType, arrayParameters());
		}
		
		if (cellTemp.getCellBase()!=null) {
			
			cellType = cellTemp.getType();
			fillCellFromArray(cellTemp.getCellBase());
			fillParamFromArray(cellTemp.getParameters());
			fillTypeCombo(cellType);
			setParamEdit(cellType);
			
			return true;
		}else {
			return false;
		}
		
		
		
	}
	
	public void getFromApp() {
		if (app.workConf!=null) {
			if (app.workConf.getCell()!=null) {
				cellTemp = app.workConf.getCell();
				
				cellType = cellTemp.getType();
				fillCellFromArray(cellTemp.getCellBase());
				fillParamFromArray(cellTemp.getParameters());
				fillTypeCombo(cellType);
				setParamEdit(cellType);
			}
		}
	}

	
	//------------------------------------------------
	//	WRITE text fields 
	//------------------------------------------------
	
	public void fillCellFromArray(double[][] _cell) {
		
		cellText[0].setText(String.format(Costants.decimalPlace8,_cell[0][0]));
		cellText[1].setText(String.format(Costants.decimalPlace8,_cell[0][1]));
		cellText[2].setText(String.format(Costants.decimalPlace8,_cell[0][2]));
		
		cellText[3].setText(String.format(Costants.decimalPlace8,_cell[1][0]));
		cellText[4].setText(String.format(Costants.decimalPlace8,_cell[1][1]));
		cellText[5].setText(String.format(Costants.decimalPlace8,_cell[1][2]));
		
		cellText[6].setText(String.format(Costants.decimalPlace8,_cell[2][0]));
		cellText[7].setText(String.format(Costants.decimalPlace8,_cell[2][1]));
		cellText[8].setText(String.format(Costants.decimalPlace8,_cell[2][2]));
	}
	
	public void fillParamFromArray(double[] _param) {
		
		paramText[0].setText(String.format(Costants.decimalPlace8,_param[0]));
		paramText[1].setText(String.format(Costants.decimalPlace8,_param[1]));
		paramText[2].setText(String.format(Costants.decimalPlace8,_param[2]));
		paramText[3].setText(String.format(Costants.decimalPlace8,_param[3]));
		paramText[4].setText(String.format(Costants.decimalPlace8,_param[4]));
		paramText[5].setText(String.format(Costants.decimalPlace8,_param[5]));
		
	}

	public void fillTypeCombo(int _type) {
		
		cellTypeMenu.setSelectedIndex(_type);

	}
	
	//------------------------------------------------
	//	READ text fields 
	//------------------------------------------------
	
	public double[] arrayParameters() {
		
		for (int i =0; i<6; i++) {
			if (paramText[i].getText().length()==0) paramText[i].setText("0.0"); 
		}
		
		double[] parameters = new double[6];
		
		for (int i=0; i<6; i++) {
			
			try {
				parameters[i] = Double.valueOf(paramText[i].getText());
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				parameters[i] = 0.0;
			}
		}
		
		return parameters;
		
	}
	
	public double[][] arrayCell() {
		
		for (int i =0; i<9; i++) {
			if (cellText[i].getText().length()==0) cellText[i].setText("0.0"); 
		}
		
		double[][] cell = new double[3][3];
		
		int j=0, k=0;
		
		for (int i=0; i<9; i++) {
			
			if (i%3==0 && i>0) j+=1;
			k=i%3;
			
			try {
				cell[j][k] = Double.valueOf(cellText[i].getText());
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				cell[j][k] = 0.0;
			}
		}
		
//		cell[0][0] = Double.valueOf(cellText[0].getText());
//		cell[0][1] = Double.valueOf(cellText[1].getText());
//		cell[0][2] = Double.valueOf(cellText[2].getText());
//		
//		cell[1][0] = Double.valueOf(cellText[3].getText());
//		cell[1][1] = Double.valueOf(cellText[4].getText());
//		cell[1][2] = Double.valueOf(cellText[5].getText());
//		
//		cell[2][0] = Double.valueOf(cellText[6].getText());
//		cell[2][1] = Double.valueOf(cellText[7].getText());
//		cell[2][2] = Double.valueOf(cellText[8].getText());
		
		return cell;
		
	}
	
	//------------------------------------------------
	//	ESPORTATORI da text fileds a fuori
	//------------------------------------------------

	public boolean sendToApp() {
		
		if (cellTemp!=null) {
			
			if (app.workConf==null) {
//				if (GUItools.getYesNoFromUser("Create initial conf ?")) {
//					app.addConf();
//					app.selectConf(0);
//				}
				return false;
			}
			
			if (app.workConf.getCell()==null) {
				app.workConf.setCell(cellTemp);
				return true;
				
			} else {
				if (GUItools.getYesNoFromUser("Replace unit cell ?")) {
					app.workConf.setCell(cellTemp);
					return true;
				} else {
					return false;
				}
			}
		}
		
		return false;
	}
	
	//---------------------------------
	//	FUNZIONE DEL MODULO 
	//---------------------------------

	public void reset() {
		
		getFromApp();
		
	}
	
	public void confChanged() {
		
		getFromApp();
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
