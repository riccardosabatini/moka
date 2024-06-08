/**
 * 
 */
package org.moka.tools.events;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

/**
 * @author riki
 *
 */



public class dropAcceptor implements DropTargetListener {
	
	dropReceiver app;
	DataFlavor whatToGet;
	boolean acceptableType;
	int src;
	
	public dropAcceptor(dropReceiver _app, DataFlavor _inflv, int _src) {
		this.app = _app;
		this.whatToGet = _inflv;
		this.src = _src;
	}
	
	///---------------------------
    /// Drag & Drop section
    ///---------------------------
	
	
	
	public void dragEnter(DropTargetDragEvent dtde) {
		DnDUtils.debugPrintln("dragEnter, drop action = "+ DnDUtils.showActions(dtde.getDropAction()));

		// Get the type of object being transferred and determine
		// whether it is appropriate.
		checkTransferType(dtde);

		// Accept or reject the drag.
		acceptOrRejectDrag(dtde);
	}

	public void dragExit(DropTargetEvent dte) {
		DnDUtils.debugPrintln("DropTarget dragExit");
	}

	public void dragOver(DropTargetDragEvent dtde) {
		DnDUtils.debugPrintln("DropTarget dragOver, drop action = "+ DnDUtils.showActions(dtde.getDropAction()));
		// Accept or reject the drag
		acceptOrRejectDrag(dtde);
	}

	public void dropActionChanged(DropTargetDragEvent dtde) {
		DnDUtils.debugPrintln("DropTarget dropActionChanged, drop action = "+ DnDUtils.showActions(dtde.getDropAction()));

		// Accept or reject the drag
		acceptOrRejectDrag(dtde);
	}

	protected boolean acceptOrRejectDrag(DropTargetDragEvent dtde) {
	    
		int dropAction = dtde.getDropAction();
	    int sourceActions = dtde.getSourceActions();
	    boolean acceptedDrag = false;

	    DnDUtils.debugPrintln("\tSource actions are "+ DnDUtils.showActions(sourceActions) + ", drop action is " + DnDUtils.showActions(dropAction));

	    // Reject if the object being transferred
	    // or the operations available are not acceptable.
	    if (!acceptableType || (sourceActions & DnDConstants.ACTION_COPY_OR_MOVE) == 0) {
	      DnDUtils.debugPrintln("Drop target rejecting drag");
	      dtde.rejectDrag();
	    } else if ((dropAction & DnDConstants.ACTION_COPY_OR_MOVE) == 0) {
	      // Not offering copy or move - suggest a copy
	      DnDUtils.debugPrintln("Drop target offering COPY");
	      dtde.acceptDrag(DnDConstants.ACTION_COPY);
	      acceptedDrag = true;
	    } else {
	      // Offering an acceptable operation: accept
	      DnDUtils.debugPrintln("Drop target accepting drag");
	      dtde.acceptDrag(dropAction);
	      acceptedDrag = true;
	    }

	    return acceptedDrag;
	  }
	  
	protected void checkTransferType(DropTargetDragEvent dtde) {
		    // Only accept a list of files
		    acceptableType = dtde.isDataFlavorSupported(whatToGet);
		    DnDUtils.debugPrintln("File type acceptable - " + acceptableType);
		  }

	public void drop(DropTargetDropEvent dtde) {
		DnDUtils.debugPrintln("DropTarget drop, drop action = "+ DnDUtils.showActions(dtde.getDropAction()));

		// Check the drop action
		if ((dtde.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) != 0) {
			// Accept the drop and get the transfer data
			dtde.acceptDrop(dtde.getDropAction());
			Transferable transferable = dtde.getTransferable();

			try {
				//boolean result = dropComponent(transferable);
				boolean result = app.doActionOnDrop(transferable, src);
				
				dtde.dropComplete(result);
				
				
				DnDUtils.debugPrintln("Drop completed, success: " + result);
			} catch (Exception e) {
				DnDUtils.debugPrintln("Exception while handling drop " + e);
				dtde.dropComplete(false);
			}
		} else {
			DnDUtils.debugPrintln("Drop target rejected drop");
			dtde.rejectDrop();
		}
	}
}
