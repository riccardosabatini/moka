/**
 * 
 */
package org.moka.tools.events;

import java.awt.event.MouseEvent;

/**
 * @author riki
 *
 */
public interface mouseReceiver {
	
	public final static int MOUSE_CLICKED = 0;
	public final static int MOUSE_ENETERD = 1;
	public final static int MOUSE_EXITED = 2;
	public final static int MOUSE_PRESSED = 3;
	public final static int MOUSE_RELEASED = 4;
	
	void doActionOnMouse(MouseEvent e, int type, int src) ;
}
