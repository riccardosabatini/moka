/**
 * 
 */
package org.moka.tools.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;

public class WaitEnabledDialog extends JDialog {
	
	  public WaitEnabledDialog(final JFrame owner, String title) {
	    super(owner, title, false);
	    addWindowListener(new WindowAdapter() {
	      public void windowOpened(WindowEvent e) {
	        CursorToolkitTwo.startWaitCursor(owner.getRootPane());
	      }
	      public void windowClosing(WindowEvent e) {
	        CursorToolkitTwo.stopWaitCursor(owner.getRootPane());
	      }
	    });
	  }
	}