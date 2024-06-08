/**
 * 
 */
package org.moka.tools.events;

import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.net.MalformedURLException;


/**
 * @author riki
 *
 */
public interface dropReceiver {

	boolean doActionOnDrop(Transferable transferable, int src) throws IOException, UnsupportedFlavorException, MalformedURLException;
}
