package org.moka.structures;
/**
 * @author riki
 *
 */
public interface Module {

	//public void reciveMouseSelection(int site);
	public void reset();
	public void confChanged();
	public void atomsChanged(int[] listChanged);
	
}
