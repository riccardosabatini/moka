/**
 * 
 */
package org.moka.tools;

import java.io.File;

/**
 * @author riki
 *
 */
public class ImageFileFilter extends javax.swing.filechooser.FileFilter {

	String[] acceptExtendsions; // = {"jpg","jpeg","gif","png"};

	public ImageFileFilter (String[] _acceptExtendsions) {
		acceptExtendsions = _acceptExtendsions;
	}
	
	public String getDescription() {
		String outPut = "Accepted images :";
		
		for (int i=0; i<acceptExtendsions.length; i++) {
			outPut+= " *."+acceptExtendsions[i];
		}
		return outPut;
	}
	
	public boolean accept(File f) {
		if (f.isDirectory()) 
			return true;
		
		String extension = getExtension(f);
		if (extension == null)
			return true;
		for (int i = 0; i < acceptExtendsions.length; i++) {
			if (extension.equals(acceptExtendsions[i]))
				return true;
		}
		return false;
			
	}
		
	String getExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 &&  i < s.length() - 1) {
			ext = s.substring(i+1).toLowerCase();
		}
		return ext;
	}
}