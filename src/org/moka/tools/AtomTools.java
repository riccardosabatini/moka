/**
 * 
 */
package org.moka.tools;

/**
 * @author riki
 *
 */
public class AtomTools {

	//Tools
	public static String getPjwfcType(int i) {
		switch (i){
		case 0: return "s";
		case 1:	return "p";
		case 2:	return "d";
		case 3:	return "f";
		default:	return "x";
		}
	}

	public static int getPjwfcType(String i) {
		if (i.equals("s")) { return 0;}
		else if (i.equals("p")) {return 1;}
		else if (i.equals("d")) {return 2;}
		else if (i.equals("f")) {return 3;}
		else {return -1;}

	}

	public static int getProjwfcDegeneracy(int i) {
		switch (i){
		case 0: return 0;
		case 1:	return 3;
		case 2:	return 5;
		case 3:	return 7;
		default:	return 0;
		}
	}
	
}
