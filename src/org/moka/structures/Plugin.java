package org.moka.structures;
import javax.swing.JComponent;
import javax.swing.JMenu;

/**
 * @author riki
 *
 */
public interface Plugin {

	JMenu makePluginMenu();

	public boolean receiveSelection(int atomSelected);
	public boolean isReadyToReciveSelection();
	
	public Object getAtomProperty(int atomSelected);
	public Object getConfProperty(int _property);

    public void confChanged();
}
