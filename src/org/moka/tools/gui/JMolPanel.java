/**
 * 
 */
package org.moka.tools.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JPanel;

import org.jmol.adapter.smarter.SmarterJmolAdapter;
import org.jmol.api.JmolAdapter;
import org.jmol.api.JmolSimpleViewer;

/**
 * @author riki
 *
 */
public class JMolPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JmolSimpleViewer viewer;
	JmolAdapter adapter;
	public JMolPanel() {
		adapter = new SmarterJmolAdapter();
		viewer = JmolSimpleViewer.allocateSimpleViewer(this, adapter);
	}

	public JmolSimpleViewer getViewer() {
		return viewer;
	}

	final Dimension currentSize = new Dimension();
	final Rectangle rectClip = new Rectangle();

	public void paint(Graphics g) {
		getSize(currentSize);
		g.getClipBounds(rectClip);
		viewer.renderScreenImage(g, currentSize, rectClip);
	}
}