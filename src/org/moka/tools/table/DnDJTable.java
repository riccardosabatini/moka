package org.moka.tools.table;

import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * A JTable with (in my opinion) improved DragDrop.
 */
 
public class DnDJTable extends JTable {
    
    private boolean canSelect = true;
    private boolean isDragging;
    private MouseEvent pressEvent = null;
    
    /**
     * Constructor for NTable.
     */
    public DnDJTable() {
        super();
    }
 
    /**
     * Constructor for NTable.
     * @param dm
     */
    public DnDJTable(TableModel dm) {
        super(dm);
    }
 
    /**
     * Constructor for NTable.
     * @param dm
     * @param cm
     */
    public DnDJTable(TableModel dm, TableColumnModel cm) {
        super(dm, cm);
    }
 
    /**
     * Constructor for NTable.
     * @param dm
     * @param cm
     * @param sm
     */
    public DnDJTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
        super(dm, cm, sm);
    }
 
    /**
     * @see java.awt.Component#processMouseMotionEvent(java.awt.event.MouseEvent)
     */
    protected void processMouseMotionEvent(MouseEvent e) {
        handleMouseEvent(e);
        super.processMouseMotionEvent(e);
    }
 
    /**
     * @see java.awt.Component#processMouseEvent(java.awt.event.MouseEvent)
     */
    protected void processMouseEvent(MouseEvent e) {
        handleMouseEvent(e);
        super.processMouseEvent(e);
    }
    
    /**
     * Performs two tasks:
     * 1. Start Drag/Drop.
     * Commence drag/drop operation if we are not already dragging.
     * Commencing a drag/drop requires the mouse event of the MOUSE_PRESSED
     * kind - which has hopefully been recorded before the MOUSE_DRAGGED mouse event
     * is received.
     * 
     * 2. Disable row selection while dragging.
     * 
     * @param e the mouse event
     */
    private void handleMouseEvent(MouseEvent e) {
        canSelect = (e.getID() != MouseEvent.MOUSE_DRAGGED);
        if (e.getID() == MouseEvent.MOUSE_PRESSED) {
            isDragging = false;
            pressEvent = e;
        } else if (e.getID() == MouseEvent.MOUSE_DRAGGED) {
            if (!isDragging && pressEvent != null) {
                TransferHandler handler = getTransferHandler();
                handler.exportAsDrag(this, pressEvent, TransferHandler.COPY);
            }
            pressEvent = null;
            isDragging = true;
        } else if (e.getID() == MouseEvent.MOUSE_RELEASED) {
            isDragging = false;
        }
    }
    
    /**
     * Only permit row selection if we are not dragging.
     * @see javax.swing.JTable#changeSelection(int, int, boolean, boolean)
     */
    public void changeSelection(
        int rowIndex,
        int columnIndex,
        boolean toggle,
        boolean extend) {
            
        if (canSelect) {
            super.changeSelection(rowIndex, columnIndex, toggle, extend);
        }
    }
 
    /**
     * Only permit row selection if we are not dragging.
     * @see javax.swing.JTable#setColumnSelectionInterval(int, int)
     */
    public void setColumnSelectionInterval(int index0, int index1) {
        if (canSelect) {
            super.setColumnSelectionInterval(index0, index1);
        }
    }
 
    /**
     * Only permit row selection if we are not dragging.
     * @see javax.swing.JTable#setRowSelectionInterval(int, int)
     */
    public void setRowSelectionInterval(int index0, int index1) {
        if (canSelect) {
            super.setRowSelectionInterval(index0, index1);
        }
    }
 
}
 