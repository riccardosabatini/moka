/**
 * 
 */
package org.moka.tools.gui;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * @author riki
 *
 */
public class mouseWatingDialog {

	private static JDialog mouseWatingDialog(final JFrame frame) {
		
		final WaitEnabledDialog dialog = new WaitEnabledDialog(frame, "I'm not Modal");
		
		dialog.getContentPane().add(
		      new JLabel("I'm a busy non-modal dialog"));
		    dialog.getContentPane().add(
		      new JButton(new AbstractAction("Wait Cursor") {
		        public void actionPerformed(ActionEvent event){
		          setWaitCursor(dialog);
		        }
		      }));
		    dialog.setSize(300, 200);
		    return dialog;
	}
	
	public static void setWaitCursor(JDialog dialog) {
		//System.out.println("Setting Wait cursor on frame");
		CursorToolkitTwo.startWaitCursor(
				((JFrame)dialog.getOwner()).getRootPane());
		//System.out.println("Setting Wait cursor on dialog");
		CursorToolkitTwo.startWaitCursor(dialog.getRootPane());
	}

	


	
}
