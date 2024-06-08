package org.moka.common;


/**
 * @author riki
 *
 */
public class Costants {

	static final public double minDist = 0.00001;
	
	
	//----------------------
	//	ATOMS FLAGS
	//----------------------
	
	static final public int numFlags = 4;
	static final public int[] emptyFlags = new int[] {0,0,0,0};
	
	static final public int aSELECTED = 0;
	static final public int aFREE = 1;
	static final public int aBLOCKED = 2;
	static final public int aSPECIAL = 3;

	//----------------------
	//	EDITING MODES
	//----------------------
	//public final static String[] editingModes = { "Normal", "Transform", "Draw", "Block", "Delete" };
	
//	static final public int mNORMAL = 0;
//	static final public int mTRANSFORM = 1;
//	static final public int mDRAW = 2;
//	static final public int mBLOCK = 3;
//	static final public int mDELETE = 4;
//	static final public int mCELLDEF = 5;
	
	//----------------------
	//	KPOINTS
	//----------------------
	public static String[] kTypesList = {"tpiba","crystal","automatic","gamma"};
	static final public int kTBIBA = 0;
	static final public int kCRYSTAL = 1;
//	static final public int kAUTOMATIC = 2;
//	static final public int kGAMMA = 3;
//	
	//----------------------
	//	OBJECT
	//----------------------

	public static String[] objTypeList = {"line",
		"plane",
		"arrow",
		"vector",
		"curve",
		"circle",
        "cell"};
	
	public static int objPointsPerType[] = {2,3,2,2,3,2,4};
	
	static final public int oLINE = 0;
	static final public int oPLANE = 1;
	static final public int oARROW = 2;
	static final public int oVECTOR = 3;
	static final public int oCURVE = 4;
	static final public int oCIRCLE = 5;
    static final public int oCELL = 6;
	
	//----------------------
	//	COORDINATES
	//----------------------
	
	static final public int cALAT = 0;
	static final public int cCRYSTAL = 1;
	static final public int cANGSTROM = 2;
	static final public double cSPECIALPARAM = 1.889726;
	
	static final public String[] coordList = {"Alat", "Crystal", "Angstrom"};
	
	//----------------------
	//	CELL
	//----------------------
	
	//Vettori di che paramtri sono editabile per ogni cella
	static public double[][] paramtersEditablePerCellType = {{1,0,0,0,0,0},
				{1,0,0,0,0,0},
				{1,0,0,0,0,0},
				{1,0,0,0,0,0},
				{1,0,1,0,0,0},
				{1,0,0,1,0,0},
				{1,0,1,0,0,0},
				{1,0,1,0,0,0},
				{1,1,1,0,0,0},
				{1,1,1,0,0,0},
				{1,1,1,0,0,0},
				{1,1,1,0,0,0},
				{1,1,1,1,0,0},
				{1,1,1,1,0,0},
				{1,1,1,1,1,1}};
	static public String[] cellTypes = { "0: free", "1: cubic P (sc) ", "2: cubic F (fcc)", "3: cubic I (bcc)", "4: Hexagonal and Trigonal P",
			"5:Trigonal R", "6: Tetragonal P (st)", "7: Tetragonal I (bct)", "8: Orthorhombic P", "9: Orthorhombic base-centered(bco)",
			"10: Orthorhombic face-centered", "11: Orthorhombic body-centered", "12: Monoclinic P", "13: Monoclinic base-centered",
			"14: Triclinic"};
	
	static final public int inputSIMPLE = 0;
	static final public int inputRELAX = 1;
	static final public int inputDYNAMICS = 2;

	static final public int readFIRST = 0;
	static final public int readALL = 1;
	static final public int readLAST = 2;
	
	static final public String[] readList = {"First", "All", "Last"};
	
	static final public int cellInputINITIAL = 1;
	static final public int cellInputNEXT = 2;
	static final public int atomsInputINITIAL = 1;
	static final public int atomsInputNEXT = 2;

	//---------------------------
	//	DATI CONFIGURAZIONI
	//---------------------------
	static public String[] atomsDataNames = { "eTot", "eFermi", "magnTot", "forceTot", "eKin","temp","eKineTot","timeStep"};
	static public Class[] atomsDataTypes = { Double.class, Double.class, Double.class, Double.class, Double.class, Double.class, Double.class, Double.class};
	
	//----------------------
	//	CARATTERI / SEPARATORI
	//----------------------
	
	static final public double Ry = 0.5291772;
	
	static final public String newline = System.getProperty("line.separator");
	static final public String separatorTAB = "\t";
	static final public String separatorSPACE = " ";
	
	static final public String decimalPlace8 = "%.8f";	//Per atomi, celle in write
	static final public String decimalPlace4 = "%.4f";	//Per tabelle
	
	//------------------
    // MISC
    //------------------

    static final public int RADIUS_COVALENT = 0;
    static final public int RADIUS_VDW = 1;
    static final public int RADIUS_MANUAL = 2;

    //----------------------
	//	DATI ATOMI JMOL
	//----------------------
	
	static final public String dummyAtomName = "Xx";
	
	public final static int[] atomColorCpk = {
		0xFFFF1493, // Xx 0
		0xFFFFFFFF, // H  1
		0xFFD9FFFF, // He 2
		0xFFCC80FF, // Li 3
		0xFFC2FF00, // Be 4
		0xFFFFB5B5, // B  5
		0xFF909090, // C  6 - changed from ghemical
		0xFF3050F8, // N  7 - changed from ghemical
		0xFFFF0D0D, // O  8
		0xFF90E050, // F  9 - changed from ghemical
		0xFFB3E3F5, // Ne 10
		0xFFAB5CF2, // Na 11
		0xFF8AFF00, // Mg 12
		0xFFBFA6A6, // Al 13
		0xFFF0C8A0, // Si 14 - changed from ghemical
		0xFFFF8000, // P  15
		0xFFFFFF30, // S  16
		0xFF1FF01F, // Cl 17
		0xFF80D1E3, // Ar 18
		0xFF8F40D4, // K  19
		0xFF3DFF00, // Ca 20
		0xFFE6E6E6, // Sc 21
		0xFFBFC2C7, // Ti 22
		0xFFA6A6AB, // V  23
		0xFF8A99C7, // Cr 24
		0xFF9C7AC7, // Mn 25
		0xFFE06633, // Fe 26 - changed from ghemical
		0xFFF090A0, // Co 27 - changed from ghemical
		0xFF50D050, // Ni 28 - changed from ghemical
		0xFFC88033, // Cu 29 - changed from ghemical
		0xFF7D80B0, // Zn 30
		0xFFC28F8F, // Ga 31
		0xFF668F8F, // Ge 32
		0xFFBD80E3, // As 33
		0xFFFFA100, // Se 34
		0xFFA62929, // Br 35
		0xFF5CB8D1, // Kr 36
		0xFF702EB0, // Rb 37
		0xFF00FF00, // Sr 38
		0xFF94FFFF, // Y  39
		0xFF94E0E0, // Zr 40
		0xFF73C2C9, // Nb 41
		0xFF54B5B5, // Mo 42
		0xFF3B9E9E, // Tc 43
		0xFF248F8F, // Ru 44
		0xFF0A7D8C, // Rh 45
		0xFF006985, // Pd 46
		0xFFC0C0C0, // Ag 47 - changed from ghemical
		0xFFFFD98F, // Cd 48
		0xFFA67573, // In 49
		0xFF668080, // Sn 50
		0xFF9E63B5, // Sb 51
		0xFFD47A00, // Te 52
		0xFF940094, // I  53
		0xFF429EB0, // Xe 54
		0xFF57178F, // Cs 55
		0xFF00C900, // Ba 56
		0xFF70D4FF, // La 57
		0xFFFFFFC7, // Ce 58
		0xFFD9FFC7, // Pr 59
		0xFFC7FFC7, // Nd 60
		0xFFA3FFC7, // Pm 61
		0xFF8FFFC7, // Sm 62
		0xFF61FFC7, // Eu 63
		0xFF45FFC7, // Gd 64
		0xFF30FFC7, // Tb 65
		0xFF1FFFC7, // Dy 66
		0xFF00FF9C, // Ho 67
		0xFF00E675, // Er 68
		0xFF00D452, // Tm 69
		0xFF00BF38, // Yb 70
		0xFF00AB24, // Lu 71
		0xFF4DC2FF, // Hf 72
		0xFF4DA6FF, // Ta 73
		0xFF2194D6, // W  74
		0xFF267DAB, // Re 75
		0xFF266696, // Os 76
		0xFF175487, // Ir 77
		0xFFD0D0E0, // Pt 78 - changed from ghemical
		0xFFFFD123, // Au 79 - changed from ghemical
		0xFFB8B8D0, // Hg 80 - changed from ghemical
		0xFFA6544D, // Tl 81
		0xFF575961, // Pb 82
		0xFF9E4FB5, // Bi 83
		0xFFAB5C00, // Po 84
		0xFF754F45, // At 85
		0xFF428296, // Rn 86
		0xFF420066, // Fr 87
		0xFF007D00, // Ra 88
		0xFF70ABFA, // Ac 89
		0xFF00BAFF, // Th 90
		0xFF00A1FF, // Pa 91
		0xFF008FFF, // U  92
		0xFF0080FF, // Np 93
		0xFF006BFF, // Pu 94
		0xFF545CF2, // Am 95
		0xFF785CE3, // Cm 96
		0xFF8A4FE3, // Bk 97
		0xFFA136D4, // Cf 98
		0xFFB31FD4, // Es 99
		0xFFB31FBA, // Fm 100
		0xFFB30DA6, // Md 101
		0xFFBD0D87, // No 102
		0xFFC70066, // Lr 103
		0xFFCC0059, // Rf 104
		0xFFD1004F, // Db 105
		0xFFD90045, // Sg 106
		0xFFE00038, // Bh 107
		0xFFE6002E, // Hs 108
		0xFFEB0026, // Mt 109
	};


	
}
