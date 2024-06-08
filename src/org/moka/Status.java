package org.moka;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.filechooser.FileFilter;


import java.io.FileReader;
import java.io.LineNumberReader;
import java.net.URLClassLoader;
import org.moka.structures.Configuration;
import org.moka.structures.MokaController;
import org.moka.tools.gui.CursorToolkitTwo;


public class Status implements Serializable {

	Moka app;
	
	//----------------
	//Per COPY e PASTE
	//----------------
	ArrayList<double[]> positionsCopy = new ArrayList<double[]>();
	ArrayList<String> namesCopy = new ArrayList<String>();
	Configuration confCopy = new Configuration();

	//----------------
	//Per APERTURA FILE
	//----------------
    FileFilter mokaFiles = new FileFilter() {
        
		public boolean accept(java.io.File file) {
			String filename = file.getName();
            return filename.endsWith(".moka");
        }
        public String getDescription() {
            return "*.moka";
        }
	};
    
	FileFilter allFiles = new FileFilter() {
        
		public boolean accept(java.io.File file) {
            return true;
        }
        public String getDescription() {
            return "*.*";
        }
	};

	String fullName=null;
	String fileName=null;
	String workingDir=null;
	
	//----------------
	//	Per WAIT / LOADING
	//----------------
	JDialog waitingDialog;
	boolean appWaiting = false;
	int workingList = 0;

    //----------------
	//	Per ATOMSDATA
	//----------------
    public double[] atomsWeights, atomsCovRadii, atomsVdwRadii;
    public int[] atomsNumber;
    public String[] atomsNames, atomsElements;
    
	
	boolean isModified = false;
	//int editingMode = Costants.mNORMAL;
	
	//----------------
	//	Per INTERFACCIA LOOK
	//----------------
	Hashtable<String, String> viewStatus;
	int[] repetitions = new int[] {0,0,0};
	boolean refold = false;
    
	Dimension mokaDimesion;
	int rightBarWidht = 150;
	
	MokaController colorControl;

    //----------------
	//	INIIZIALIZZAZIONE
	//----------------

	public Status(Moka _app) {
		
		this.app = _app;
		
		Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
		mokaDimesion = new Dimension(Math.round(2*screenDim.width/3),screenDim.height-80);


        try {
            loadAtmosData();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Status.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Status.class.getName()).log(Level.SEVERE, null, ex);
        }
        
	}
	
    public void loadAtmosData() throws FileNotFoundException, IOException {

        int nAtoms = 110;

        atomsWeights = new double[nAtoms];
        atomsCovRadii = new double[nAtoms];
        atomsVdwRadii = new double[nAtoms];
        atomsNumber = new int[nAtoms];;
        atomsNames = new String[nAtoms];
        atomsElements = new String[nAtoms];


        String fileAtoms = "org/moka/common/atomsData.txt";

        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileAtoms);
        LineNumberReader lnr = new LineNumberReader(new InputStreamReader(inputStream));
        
		//URLClassLoader urlLoader = (URLClassLoader) this.getClass().getClassLoader();
		//URL fileURL = urlLoader.findResource(fileAtoms).getFile();
		//LineNumberReader lnr = new LineNumberReader(new FileReader(urlLoader.findResource(fileAtoms).getFile()));
        
		lnr.setLineNumber(0);
		String line = lnr.readLine();
        int nLine = 0;


        do  {

			String[] words = line.replaceAll("\\t", " ").split("\\s+");

            atomsNames[nLine] = words[0];
            atomsElements[nLine] = words[1];
            atomsNumber[nLine] = Integer.parseInt(words[5]);
            atomsWeights[nLine] = Double.parseDouble(words[6]);
            atomsCovRadii[nLine] = Double.parseDouble(words[7]);
            atomsVdwRadii[nLine] = Double.parseDouble(words[8]);

            nLine++;
            
		} while ((line = lnr.readLine()) != null);

    }

    //------------------
	// SAVING
	//------------------

	public void setModified() {
	
		isModified = true;
		
	}
	
	public void unsetModified() {
		
		isModified = true;
		
	}
	
	public void save() {
		
		mokaDimesion = new Dimension(app.frame.getHeight(), app.frame.getWidth());
		
	}
	
	//------------------
	// FILENAME / DIRS
	//------------------

	public String getFileName() {
		
		return fullName;
		
	}
	
	public void setMokaName(String _in) {
		
		this.fullName = _in;
		
		this.fileName = new File(_in).getName();
		this.workingDir = new File(_in).getPath();
		
	}
	
	public boolean isNameSet() {
		
		return fullName == null ? false : true;
	}
	
	public void unsetMokaName() {
		
		this.fullName = null;
		this.fileName = null;
		
	}

	public String getWorkingDir() {
		
		return workingDir;
		
	}
	
	public String getOnlyFileName() {
		
		return fileName;
		
	}

	//------------------
	//		WORKING
	//------------------
	
	public void setWorkingOn() {
		
		if (workingList<0) workingList = 0;
		workingList++;
	}
	
	public void setWorkingOff() {
		workingList--;
	}
	
	public boolean isWorking() {
		
		return workingList>0?true:false;
	}
	
	
	//------------------
	//		WAITING
	//------------------	

	public void startWaiting() {
		  
		CursorToolkitTwo.startWaitCursor(app.frame.getRootPane());
		appWaiting = true;
	}
	
	public void stopWaiting() {
		
		CursorToolkitTwo.stopWaitCursor(app.frame.getRootPane());
		appWaiting = false;	
	}
	
	public void startWaiting(JComponent _comp) {
		
		CursorToolkitTwo.startWaitCursor(_comp);
		appWaiting = true;
	}
	
	public void stopWaiting(JComponent _comp) {
		
		CursorToolkitTwo.stopWaitCursor(_comp);
		appWaiting = false;	
		
	}
	
	public boolean isAppWaiting() {
		return appWaiting;
	}
	
	//------------------
	//		VIEW
	//------------------	

	public void storeViewStatus(Hashtable<String, String> _in) {
		viewStatus = _in;
	}
	
	public Hashtable<String, String> getViewStatus() {
		return viewStatus;
	}

	public int[] getRepetitions() {
		return repetitions;
	}
	
	public void setRepetitions(int[] _in) {
		
		if (_in.length==3) {
			repetitions[0] = _in[0];
			repetitions[1] = _in[1];
			repetitions[2] = _in[2];
		} else {
			repetitions = new int[] {0,0,0};
		}
		
	}
	
	//------------------
	//	MOKA CONTROLLERS
	//------------------	

	public void setColorConroller(MokaController _in) {
		colorControl = _in;
	}
	
	public Object getColorController() {
		return colorControl;
	}
	
	//------------------
	//	GETTERS / SETTERS
	//------------------	
	
	public Dimension getMokaDimesion() {
	
		return mokaDimesion;
	}


	public void setMokaDimesion(Dimension mokaDimesion) {
		this.mokaDimesion = mokaDimesion;
	}

	
	public void setWorkingDir(String workingDir) {
		this.workingDir = workingDir;
	}

	//------------------
	//	RESET VARS
	//------------------		

	public void resetAllVars() {
		
		positionsCopy.clear();
		namesCopy.clear();
		confCopy.clear();

		//----------------
		//Per APERTURA FILE
		//----------------
	    
		fullName=null;
		fileName=null;
		workingDir=System.getProperty("user.home");
		
		//----------------
		appWaiting = false;
		workingList = 0;
		isModified = false;
		
		//----------------
		//	Per INTERFACCIA LOOK
		//----------------
		repetitions = new int[] {0,0,0};
		
		
	}
	
	public void resetConfigurationVars() {
		
		
	}
	
	public void resetFileVars() {
		
		fullName=null;
		fileName=null;
	
	}

    //-------------------
    // CLASSI
    //-------------------

}
