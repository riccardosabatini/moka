package org.moka;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.moka.common.Costants;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import javax.swing.TransferHandler;
import javax.swing.table.JTableHeader;
import org.moka.structures.Atom;
import org.moka.structures.Module;
import org.moka.tools.ArrayTools;
import org.moka.tools.gui.ComboDialog;
import org.moka.tools.gui.GUItools;
import org.moka.tools.table.SortButtonRenderer;
import org.moka.tools.table.SortableTableModel;


/**
 * @author riki
 *
 */
public class AtomsEditor extends JFrame implements  TableModelListener, ActionListener, Module, ListSelectionListener {

    static final int column_X = 1;
    static final int column_Y = 2;
    static final int column_Z = 3;
    static final int column_Name = 0;
    static final int column_Id = 4;

    public Moka app;

	JLabel statusAtoms;
    JTable table;
    
	SortableTableModel tm;
	// SortButtonRenderer renderer;
	//JTableHeader header;
    
    ListSelectionModel listSelectionModel;
    // JScrollPane tableScroller;
	
	JComboBox coordTypeMenu;
	//boolean appWorking = false;
	
    
	public AtomsEditor(Moka _app) {
		
		
		JPanel frameATOMS = new JPanel();
		frameATOMS.setLayout(new BorderLayout());
		//setContentPane(framePanel);
		getContentPane().setLayout(new BorderLayout());
		
		
		this.app = _app;

		//----------------------
		//	STATUS
		//----------------------

		
		Box statusBox = Box.createHorizontalBox();		
		statusBox.add(Box.createHorizontalGlue());
		
		statusAtoms = new JLabel("None");
		statusAtoms.setOpaque(true);
        //statusCell.setBackground(Color.RED);
		//statusAtoms.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.red));
		statusBox.add(statusAtoms);

		statusBox.add(Box.createHorizontalGlue());
		frameATOMS.add(GUItools.createPanelForComponent(statusBox, "Status",new Dimension(350,50)), BorderLayout.PAGE_START);
		
		//----------------------
		//	TABELLA
		//----------------------
		
		tm = new SortableTableModel();
		
		tm.addColumn("Type");
		tm.addColumn("x");
		tm.addColumn("y");
		tm.addColumn("z");
		tm.addColumn("N");

        
		table = new JTable(tm);
		table.setDragEnabled(true);
        table.getTableHeader().setReorderingAllowed(false);
		table.getModel().addTableModelListener(this);

        //Per selezioni
		listSelectionModel = table.getSelectionModel();
		listSelectionModel.addListSelectionListener(this);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        //Per sorting
        SortButtonRenderer renderer = new SortButtonRenderer();
        JTableHeader header = table.getTableHeader();
		header.addMouseListener(new HeaderListener(header,renderer));

        //Per dragging
        // table.setDropMode(javax.swing.DropMode.INSERT_ROWS);
        table.setDragEnabled(true);
		//table.setTransferHandler(new AtomsEditorTransferHandler());




		JScrollPane tableScroller = new JScrollPane(table);
		JPanel tmPanel = GUItools.createPanelForComponent(tableScroller, "Atoms",new Dimension(350,200));
		frameATOMS.add(tmPanel, BorderLayout.CENTER);
		

		//----------------------
		//	TRASFORMAZIONI
		//----------------------

		Box convertBox = Box.createHorizontalBox();	
		
		String[] coordList = { "Alat", "Crystal", "Angstrom"};
		coordTypeMenu = new JComboBox(coordList);
		coordTypeMenu.setSelectedIndex(0);
		//coordTypeMenu.addActionListener(this);
		//coordTypeMenu.addMouseListener(new mouseAcceptor(this, 0));
		convertBox.add(coordTypeMenu);

        JButton convertB = new JButton("Convert");
		convertB.setActionCommand("convertB");
		convertB.addActionListener(this);
		convertBox.add(convertB);
		
        convertBox.add(Box.createHorizontalStrut(10));

        JButton reorderB= new JButton("Reorder");
		reorderB.setActionCommand("reorderB");
		reorderB.addActionListener(this);
		convertBox.add(reorderB);

        JButton refoldB= new JButton("Refold");
		refoldB.setActionCommand("refoldB");
		refoldB.addActionListener(this);
		convertBox.add(refoldB);

        convertBox.add(Box.createHorizontalGlue());

		frameATOMS.add(GUItools.createPanelForComponent(convertBox, "Convert",new Dimension(350,60)), BorderLayout.PAGE_END);
		
		
		this.getContentPane().add(frameATOMS, BorderLayout.CENTER);

	}

	//--------------------
	//	AZIONI
	//--------------------
	
	public void actionPerformed(ActionEvent e) {
		
		if (app.status.isAppWaiting()) return;

        String actionCommand = e.getActionCommand();

		if (actionCommand.equals("convertB")) {
			//Il comboBox ha gli indici compatibili con cALAT, cANG....
			if (GUItools.getYesNoFromUser("Convert all ?")) {
                convertPositions(coordTypeMenu.getSelectedIndex());
                app.reset();
            }
            
		}

        if (actionCommand.equals("reorderB")) {
			//Il comboBox ha gli indici compatibili con cALAT, cANG....
			if (GUItools.getYesNoFromUser("Reorder atom list ?")) {
                reorderAtoms();
                app.reset();
            }
		}

        if (actionCommand.equals("refoldB")) {
			//Il comboBox ha gli indici compatibili con cALAT, cANG....
			if (GUItools.getYesNoFromUser("Reorder selected atoms ?")) {
                refoldSelectedAtoms();
                app.reset();
            }
		}

		
	}
	
	public void tableChanged(TableModelEvent e) {
		
		if (app.status.isAppWaiting()) return;
		
		if (app.status.isWorking()) return;
		
		if (e.getType() == TableModelEvent.UPDATE) {
			
            int lineSelected = e.getFirstRow();
            int atomSelected = Integer.parseInt(tm.getValueAt(lineSelected, column_Id).toString())-1;

			int column = e.getColumn();
			TableModel model = (TableModel)e.getSource();
			//String columnName = model.getColumnName(column);
			//Object data = model.getValueAt(row, column);
	
			if (column==column_X || column==column_Y || column==column_Z) {
				
				double oldX = app.workConf.getAtomPos(atomSelected)[0];
				double oldY = app.workConf.getAtomPos(atomSelected)[1];
				double oldZ = app.workConf.getAtomPos(atomSelected)[2];

                app.workConf.setAtomPos(atomSelected, column-1, Double.parseDouble((String) model.getValueAt(lineSelected, column)));
				
				double thisX = app.workConf.getAtomPos(atomSelected)[0];
				double thisY = app.workConf.getAtomPos(atomSelected)[1];
				double thisZ = app.workConf.getAtomPos(atomSelected)[2];
				
				double[] oldVect = {thisX-oldX,thisY-oldY,thisZ-oldZ};
				app.viewer.traslateAtoms(new int[] {atomSelected},oldVect);
				
				
			}
			
			if (column==column_Name) {
				
				String newName = (String) model.getValueAt(lineSelected, column);
				app.workConf.setAtomName(atomSelected, newName);
				
				if (ArrayTools.isInArray(app.status.atomsElements, newName)) {
					app.workConf.setAtomElement(atomSelected, newName);
				} else {
					
					int newElement = ComboDialog.showDialog(app.frame, null, "Element for:"+newName, "Element for:"+newName, app.status.atomsElements, 0);
					app.workConf.setAtomElement(atomSelected, app.status.atomsElements[newElement]);

				}
				
				app.viewer.refreshDraw(true);
			}
			
		}
	}
	
	public void valueChanged(ListSelectionEvent e) {
		
		if (app.status.isAppWaiting()) return;
		
		if (app.status.isWorking()) return;
		
		if (e.getValueIsAdjusting()) return;

        
        if (!listSelectionModel.isSelectionEmpty() ) {

        	app.selectAtoms(getSelectedAtomsInTable());

        } else {
            app.clearSelection();
        }

        
        
    }

    public void orderTable(int[] toInsert, int lineStart) {

        if (app.workConf==null) {
			return;
		}

		app.status.setWorkingOn(); //appWorking = true;


        if (lineStart<0 || lineStart > tm.getRowCount()) {
            return;
        }

        int[] newOrder = new int[tm.getRowCount()];

        int counter = 0;
        for (int i = 0; i < tm.getRowCount(); i++) {

            if (i == lineStart) {

                for (int j = 0; j < toInsert.length; j++) {
                    newOrder[counter] = toInsert[j];
                    counter++;
                }

            }

            int num = Integer.parseInt(tm.getValueAt(i, column_Id).toString());
            if (!ArrayTools.isInArray(toInsert, num)) {
                newOrder[counter] = num;
                counter++;
            }


        }


        tm.setRowCount(0);
		table.editingCanceled(new ChangeEvent(this));

		double[] toPlace = new double[3];

		for (int i=0; i<newOrder.length; i++) {

			toPlace = app.workConf.getAtomPos(newOrder[i]-1);

//			toPlace[0] = app.workConf.positions.get(i)[0];
//			toPlace[1] = app.workConf.positions.get(i)[1];
//			toPlace[2] = app.workConf.positions.get(i)[2];

			//if (coordType == 2) toPlace = CellTools.toCrystalCoord(cellDim, array.get(i));

			tm.addRow(new String[] { app.workConf.getAtomName(newOrder[i]-1), String.format(Costants.decimalPlace4,toPlace[0]),
															 String.format(Costants.decimalPlace4,toPlace[1]),
															 String.format(Costants.decimalPlace4,toPlace[2]) , ""+newOrder[i] });

		}

        app.selectAtoms();

        app.status.setWorkingOff(); //appWorking = false;

    }

    public void refreshTable() {

        fillTableWithPos();
        selectTable(app.workConf.getAtomsSelected());
        statusAtoms.setText(Costants.coordList[app.workConf.getCoordType()]);
        
    }

	//----------------------------------------
	//	Azioni
	//----------------------------------------
	
	public void convertPositions(int toConvert) {
		
		if (app.workConf.getCoordType() == toConvert) {
			return;
		} else {
			
			for (int i =0; i<app.workConf.getNAtoms(); i++) {
				
				double[] in = app.workConf.getAtomPos(i);
//				in[0] = app.workConf.positions.get(i)[0];
//				in[1] = app.workConf.positions.get(i)[1];
//				in[2] = app.workConf.positions.get(i)[2];
				
				if (app.workConf.getCoordType() == Costants.cALAT && toConvert == Costants.cCRYSTAL) {
					in = app.workConf.getCell().convertAlatToCrystal(in);
				}
				if (app.workConf.getCoordType() == Costants.cALAT && toConvert == Costants.cANGSTROM) {
					in = app.workConf.getCell().convertAlatToAngstrom(in);
				}
				if (app.workConf.getCoordType() == Costants.cCRYSTAL && toConvert == Costants.cALAT) {
					in = app.workConf.getCell().convertCrystalToAlat(in);
				}
				if (app.workConf.getCoordType() == Costants.cCRYSTAL && toConvert == Costants.cANGSTROM) {
					in = app.workConf.getCell().convertCrystalToAngstrom(in);
				}
				if (app.workConf.getCoordType() == Costants.cANGSTROM && toConvert == Costants.cALAT) {
					in = app.workConf.getCell().convertAngstromToAlat(in);
				}
				if (app.workConf.getCoordType() == Costants.cANGSTROM && toConvert == Costants.cCRYSTAL) {
					in = app.workConf.getCell().convertAngstromToCrystal(in);
				}
				
			double[] out = new double[3];
			out[0] = in[0];
			out[1] = in[1];
			out[2] = in[2];
			
			app.workConf.setAtomPos(i, out);
			
			}
			app.workConf.setCoordType(toConvert);
		}
		
		
	}
	
	public void reorderAtoms() {

        ArrayList<Atom> oldAtoms = (ArrayList<Atom>) app.workConf.getAtoms().clone();

        app.workConf.delAllAtoms();

        for (int i=0; i<tm.getRowCount(); i++) {

            int idAtom = Integer.parseInt(tm.getValueAt(i, column_Id).toString())-1;
            app.workConf.addAtom(oldAtoms.get(idAtom));

        }
        
    }

    public void refoldSelectedAtoms() {

        app.workConf.refoldAtoms(getSelectedAtomsInTable());

    }

	//----------------------------------------
	//	GESTIONE TABELLS
	//----------------------------------------

    public int[] getSelectedAtomsInTable() {

        // Find out which indexes are selected.
        int minIndex = listSelectionModel.getMinSelectionIndex();
        int maxIndex = listSelectionModel.getMaxSelectionIndex();

        ArrayList<Integer> temp = new ArrayList<Integer>();

        for (int i = minIndex; i <= maxIndex; i++) {
            if (listSelectionModel.isSelectedIndex(i)) {
                temp.add(Integer.parseInt(tm.getValueAt(i, column_Id).toString())-1);
            }
        }


        int[] out = new int[temp.size()];
        for (int i=0; i<temp.size(); i++) {
            out[i] = temp.get(i);
        }

        return out;
    }

	public void selectTable(int[] atoms) {

        int numSelected = 0;

		app.status.setWorkingOn(); //appWorking = true;	//stop listeners

        for (int i=0; i< atoms.length; i++) {

            for (int line=0; line<tm.getRowCount(); line++) {

                int idAtom = Integer.parseInt(tm.getValueAt(line, column_Id).toString())-1;
                if (idAtom==atoms[i]) {
                    if (i==0) {
                        listSelectionModel.setSelectionInterval(line,line);
                    } else {
                        listSelectionModel.addSelectionInterval(line,line);
                    }
                }
            }

        }
		
		app.status.setWorkingOff(); //appWorking = false; //re-start listeners
	}
	
	public void fillTableWithPos(){

		if (app.workConf==null) {
			return;
		}
		
		app.status.setWorkingOn(); //appWorking = true;
		
		tm.setRowCount(0);
		table.editingCanceled(new ChangeEvent(this));
		
		double[] toPlace = new double[3];
		
		for (int i=0; i<app.workConf.getNAtoms(); i++) {
			
			toPlace = app.workConf.getAtomPos(i);
			
//			toPlace[0] = app.workConf.positions.get(i)[0];
//			toPlace[1] = app.workConf.positions.get(i)[1];
//			toPlace[2] = app.workConf.positions.get(i)[2];
			
			//if (coordType == 2) toPlace = CellTools.toCrystalCoord(cellDim, array.get(i));
			
			tm.addRow(new String[] { app.workConf.getAtomName(i), String.format(Costants.decimalPlace4,toPlace[0]), 
															 String.format(Costants.decimalPlace4,toPlace[1]),
															 String.format(Costants.decimalPlace4,toPlace[2]) , ""+(i+1) });
			
		}
		
		app.status.setWorkingOff(); //appWorking = false;
	}

 
	//----------------------------------------
	//	MODULI
	//----------------------------------------
	
	public void reset() {
		// TODO Auto-generated method stub
		
        refreshTable();
		
	} 
		
	public void confChanged() {
		// TODO Auto-generated method stub
//		fillTableWithPos();
//		statusAtoms.setText(Costants.coordList[app.workConf.getCoordType()]);

        refreshTable();
	}

	public void atomsChanged(int[] listChanged) {
		// TODO Auto-generated method stub
        refreshTable();
//		if (!app.status.isWorking()) {
//			fillTableWithPos();
//		}
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


    //----------------------------------------
	//	PER DRAG & DROP
	//----------------------------------------

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

//    class AtomsEditorTransferHandler extends TransferHandler {
//
//        public boolean canImport(TransferHandler.TransferSupport info) {
//            return true;
//        }
//
//        public boolean importData(TransferHandler.TransferSupport info) {
//
//            javax.swing.JTable.DropLocation loc = (javax.swing.JTable.DropLocation)info.getDropLocation();
//            
//            try {
//                dropComponent(info.getTransferable(),loc.getRow());
//            } catch (IOException ex) {
//                Logger.getLogger(AtomsEditor.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (UnsupportedFlavorException ex) {
//                Logger.getLogger(AtomsEditor.class.getName()).log(Level.SEVERE, null, ex);
//            }
//
//            return true;
//
//        }
//
//        protected boolean dropComponent(Transferable transferable, int line) throws IOException, UnsupportedFlavorException, MalformedURLException {
//
//            String temp[] = ((String) transferable.getTransferData(DataFlavor.stringFlavor)).split(":");
//            int[] list = new int[temp.length];
//
//            for (int i = 0; i < temp.length; i++) {
//                list[i] = Integer.parseInt(temp[i]);
//            }
//
//            orderTable(list,line);
//
//            //System.out.println(line);
//            return true;
//        }
//
//        public void exportDone(JComponent c, Transferable t, int action) {
//        }
//
//        public int getSourceActions(JComponent c) {
//            return MOVE;
//        }
//
//        protected Transferable createTransferable(JComponent c) {
//
//            ListSelectionModel lsm = ((JTable) c).getSelectionModel();
//
//            int minIndex = lsm.getMinSelectionIndex();
//            int maxIndex = lsm.getMaxSelectionIndex();
//
//            String transfer = "";
//            for (int i = minIndex; i <= maxIndex; i++) {
//
//                if (lsm.isSelectedIndex(i)) {
//                    int num = Integer.parseInt(tm.getValueAt(i, column_Id).toString());
//                    transfer+= transfer.length()>0 ? ":"+num : num;
//                }
//
//            }
//
//            return new StringSelection(transfer);
//        }
//    }

}
