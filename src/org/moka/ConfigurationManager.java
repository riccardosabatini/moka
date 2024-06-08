package org.moka;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;
import javax.swing.table.*;

import org.moka.structures.AtomData;
import org.moka.structures.Module;
import org.moka.tools.*;
import org.moka.tools.gui.*;
import org.moka.tools.table.*;



/**
 * @author riki
 *
 */
public class ConfigurationManager extends JFrame implements  ActionListener, Module {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8930076797547998168L;

	public Moka app;

	AtomData dataINDEX = new AtomData();

	
	//Table
	public JTable table;
	public JScrollPane tableScroller;
	public ListSelectionModel listSelectionModel;
	SortButtonRenderer renderer;
	TableColumnModel model;
	JTableHeader header;
	
	JMenuBar menuBar;
	JMenu menuView;
	JButton loadB,plotB;
	
	JCheckBoxMenuItem[] dataCbView = new JCheckBoxMenuItem[dataINDEX.numDATA];

	public ConfigurationManager(Moka _app) {

		JPanel framePanel = new JPanel();
		setContentPane(framePanel);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().setBackground(new Color(129));

		this.app = _app;

		boolean[] initView = new boolean[] {true, true};

		//MENU
		menuBar = new JMenuBar();

		//File
		menuView = new JMenu("View");
		menuBar.add(menuView);

		for (int i=0; i<dataCbView.length; i++) {
			dataCbView[i] = new JCheckBoxMenuItem(dataINDEX.names[i]);
			menuView.add(dataCbView[i]);
			if (i<initView.length) {
				dataCbView[i].setState(initView[i]); 
			} else {
				dataCbView[i].setState(false);
			}
			dataCbView[i].addActionListener(this);
		}

		setJMenuBar(menuBar);
		
		//----------------------
		//	TABELLA
		//----------------------

		table = new JTable(makeTBMode(initView));
		
//		table.setDropMode(javax.swing.DropMode.INSERT_ROWS);
//        table.setDragEnabled(true);
//		table.setTransferHandler(new TransferHandler() {
//
//            public boolean canImport(TransferHandler.TransferSupport info) { return true; }
//            public boolean importData(TransferHandler.TransferSupport info) { return true; }
//            public void exportDone(JComponent c, Transferable t, int action) {}
//            public int getSourceActions(JComponent c) { return MOVE; }
//            protected Transferable createTransferable(JComponent c) {
//                    return new StringSelection("Test");
//            }
//    });
		
		renderer = new SortButtonRenderer();
		model = table.getColumnModel();

		//Per selezioni
//		listSelectionModel = table.getSelectionModel();
//		listSelectionModel.addListSelectionListener(this);
//		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		header = table.getTableHeader();
		header.addMouseListener(new HeaderListener(header,renderer));

//		table.setDragEnabled(true);
//		table.getModel().addTableModelListener(this);
//		//Per selezioni
//		listSelectionModel = table.getSelectionModel();
//		listSelectionModel.addListSelectionListener(this);
//		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		tableScroller = new JScrollPane(table);
		JPanel tmPanel = GUItools.createPanelForComponent(tableScroller, "Configurations",new Dimension(450,500));
		this.getContentPane().add(tmPanel, BorderLayout.CENTER);


		//----------------------
		//	Bottoni
		//----------------------

		Box convertBox = Box.createHorizontalBox();	

		loadB= new JButton("Load");
		loadB.setActionCommand("loadB");
		loadB.addActionListener(this);
		convertBox.add(loadB);

		plotB= new JButton("Plot trend");
		plotB.setActionCommand("plotB");
		plotB.addActionListener(this);
		convertBox.add(plotB);
		
		this.getContentPane().add(GUItools.createPanelForComponent(convertBox, "Comands",new Dimension(450,60)), BorderLayout.PAGE_END);
		setSize(500,550);

	}

	//--------------------
	//	AZIONI
	//--------------------

	class HeaderListener extends MouseAdapter {
		JTableHeader   header;
		SortButtonRenderer renderer;

		HeaderListener(JTableHeader header,SortButtonRenderer renderer) {
			this.header   = header;
			this.renderer = renderer;
		}

		public void mousePressed(MouseEvent e) {
			int col = header.columnAtPoint(e.getPoint());
			int sortCol = header.getTable().convertColumnIndexToModel(col);
			renderer.setPressedColumn(col);
			renderer.setSelectedColumn(col);
			header.repaint();

			if (header.getTable().isEditing()) {
				header.getTable().getCellEditor().stopCellEditing();
			}

			boolean isAscent;
			if (SortButtonRenderer.DOWN == renderer.getState(col)) {
				isAscent = true;
			} else {
				isAscent = false;
			}
			((SortableTableModel)header.getTable().getModel()).sortByColumn(sortCol, isAscent);    
		}

		public void mouseReleased(MouseEvent e) {
			//int col = header.columnAtPoint(e.getPoint());
			renderer.setPressedColumn(-1);                // clear
			header.repaint();
		}
	}

	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == loadB) {
			int row = table.getSelectedRow();

			int numCol = -1;
			for (int i=0; i<table.getModel().getColumnCount(); i++) {

				if (table.getColumnName(i).equals("Num")) {
					numCol = i;
				}
			}

			app.loadConf(Integer.valueOf(table.getValueAt(row, numCol).toString())-1);
			app.confChanged();
		}
		
		if (e.getSource() == plotB) {
			
			int dataToPlot = ComboDialog.showDialog(this, null, "Data to plot", "Plot", dataINDEX.names, 0);

			if (dataToPlot==-1) return;
			
			int nonNullData = 0;
			for (int i=0; i<app.confDB.size(); i++){
				if (!app.confDB.get(i).getData().isNull(dataToPlot)) {
					nonNullData++;
				}				
			}
			if (nonNullData==0) return;
			
			double[] x = new double[nonNullData];
			double[] y = new double[nonNullData];
			
			nonNullData=0;
			for (int i=0; i<app.confDB.size(); i++){
				if (!app.confDB.get(i).getData().isNull(dataToPlot)) {
					x[nonNullData]=i;
					y[nonNullData] = (Double)app.confDB.get(i).getData().getData(dataToPlot);
					nonNullData++;
				} 
				
			}
			
			GraphXY graph = new GraphXY(0,600,400);
			graph.addSeriesXY(x, y, Color.red);
			graph.createAndShowGUI();
		}
		
		if (e.getSource().getClass().equals(JCheckBoxMenuItem.class)) {
			
			table.setModel(makeTBMode(getViewCols()));
			tableFillConf();
		
		}
		
		
		

	}

	//-----------------------
	//	PER GENERAZIONE TABELLA
	//-----------------------
	
	public boolean[] getViewCols() {
		
		boolean[] status = new boolean[dataCbView.length];
		
		for (int i=0; i<dataCbView.length; i++) {
			status[i] = dataCbView[i].getState();
		}
		
		return status;
	}
	
	public SortableTableModel makeTBMode(boolean[] cols) {

		SortableTableModel output;

		output = new SortableTableModel();
		output.addColumn("Num");

		for (int i=0; i<cols.length; i++) {
			if (cols[i])
				output.addColumn(dataINDEX.names[i]);	
		}

//		int[] columnWidth = {50,150,50,100,100,100,100,100,100};
//		for (int i=0;i<tmConf.getColumnCount();i++) {
//		model.getColumn(i).setHeaderRenderer(renderer);
//		model.getColumn(i).setPreferredWidth(columnWidth[i]);
//		}

		return output;

	}


	//----------------------------------------
	//	Filler delle tabelle
	//----------------------------------------

	public void tableFillConf(){

		if (app.confDB==null) {
			return;
		}

		app.status.setWorkingOn();	//appWorking = true;

		((SortableTableModel)table.getModel()).setRowCount(0);

		for (int i=0; i<app.confDB.size(); i++) {

			boolean[] colsToView =getViewCols();
			int totColsToView = ArrayTools.countInArray(colsToView, Boolean.TRUE);
			 
			
			String[] tableRow = new String[totColsToView+1];
			
			tableRow[0] = ""+(i+1);

			int indexCol = 0;
			int j=0;
			
			do {

				if (colsToView[j]) {
					indexCol++;
					
					Object obj = app.confDB.get(i).getData().values[j];

					if (obj.equals(dataINDEX.dataNull(obj.getClass()))) {
						tableRow[indexCol] = "-0.0";
					} else {
						tableRow[indexCol] = obj.toString();
					}
	
				} 
				j++;
			} while (indexCol<totColsToView);

			((SortableTableModel)table.getModel()).addRow(tableRow);

		}

		app.status.setWorkingOff();	//appWorking = false;
	}

	//---------------------------------
	//	FUNZIONE DEL MODULO 
	//---------------------------------

	public void reset() {
		tableFillConf();
	}
	
	public void confChanged() {
		tableFillConf();
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
