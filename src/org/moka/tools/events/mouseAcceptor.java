/**
 * 
 */
package org.moka.tools.events;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import org.moka.tools.events.mouseReceiver;

/**
 * @author riki
 *
 */
public class mouseAcceptor implements MouseListener {

	final static int MOUSE_CLICKED = 0;
	final static int MOUSE_ENETERD = 1;
	final static int MOUSE_EXITED = 2;
	final static int MOUSE_PRESSED = 3;
	final static int MOUSE_RELEASED = 4;
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	mouseReceiver app;
	int src;
	
	public mouseAcceptor(mouseReceiver _app, int _src) {
		this.app = _app;
		this.src = _src;
	}
	
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub			
			app.doActionOnMouse(e, MOUSE_CLICKED, src);
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		app.doActionOnMouse(e, MOUSE_ENETERD, src);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		app.doActionOnMouse(e, MOUSE_EXITED, src);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
	
			app.doActionOnMouse(e, MOUSE_PRESSED, src);
	
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		app.doActionOnMouse(e, MOUSE_RELEASED, src);
	}

}
