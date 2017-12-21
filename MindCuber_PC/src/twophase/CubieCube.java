package twophase;

import static twophase.Corner.*;
import static twophase.Edge.*;

/** Cube on the cubie level
 * <br><a href="http://kociemba.org/math/cubielevel.htm">explanation on kociemba's site</a> */
class CubieCube {

	/* All arrays are initialized to Id-Cube (permutations and orientations of a solved cube).
	 * The "is replaced by" representation means that if, for example, B is placed
	 * where A is placed in a solved cube, we put B in A's original location in the array. */ 

	/** Corner permutation, represented in the "is replaced by" representation */
	Corner[] cp = { URF, UFL, ULB, UBR, DFR, DLF, DBL, DRB };

	/** Corner orientation. <br>
	 *  The i-th orientation is the orientation of the corner that is currently at index i in cp */
	byte[] co = { 0, 0, 0, 0, 0, 0, 0, 0 };

	/** Edge permutation, represented in the "is replaced by" representation */
	Edge[] ep = { UR, UF, UL, UB, DR, DF, DL, DB, FR, FL, BL, BR };

	/** Edge orientation <br>
	 *  The i-th orientation is the orientation of the edge that is currently at index i in ep */
	byte[] eo = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

	// ************************************** Moves on the cubie level ***************************************************

	private static Corner[] cpU = { UBR, URF, UFL, ULB, DFR, DLF, DBL, DRB };
	private static byte[] coU = { 0, 0, 0, 0, 0, 0, 0, 0 };
	private static Edge[] epU = { UB, UR, UF, UL, DR, DF, DL, DB, FR, FL, BL, BR };
	private static byte[] eoU = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

	private static Corner[] cpR = { DFR, UFL, ULB, URF, DRB, DLF, DBL, UBR };
	private static byte[] coR = { 2, 0, 0, 1, 1, 0, 0, 2 };
	private static Edge[] epR = { FR, UF, UL, UB, BR, DF, DL, DB, DR, FL, BL, UR };
	private static byte[] eoR = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

	private static Corner[] cpF = { UFL, DLF, ULB, UBR, URF, DFR, DBL, DRB };
	private static byte[] coF = { 1, 2, 0, 0, 2, 1, 0, 0 };
	private static Edge[] epF = { UR, FL, UL, UB, DR, FR, DL, DB, UF, DF, BL, BR };
	private static byte[] eoF = { 0, 1, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0 };

	private static Corner[] cpD = { URF, UFL, ULB, UBR, DLF, DBL, DRB, DFR };
	private static byte[] coD = { 0, 0, 0, 0, 0, 0, 0, 0 };
	private static Edge[] epD = { UR, UF, UL, UB, DF, DL, DB, DR, FR, FL, BL, BR };
	private static byte[] eoD = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

	private static Corner[] cpL = { URF, ULB, DBL, UBR, DFR, UFL, DLF, DRB };
	private static byte[] coL = { 0, 1, 2, 0, 0, 2, 1, 0 };
	private static Edge[] epL = { UR, UF, BL, UB, DR, DF, FL, DB, FR, UL, DL, BR };
	private static byte[] eoL = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

	private static Corner[] cpB = { URF, UFL, UBR, DRB, DFR, DLF, ULB, DBL };
	private static byte[] coB = { 0, 0, 1, 2, 0, 0, 2, 1 };
	private static Edge[] epB = { UR, UF, UL, BR, DR, DF, DL, BL, FR, FL, UB, DB };
	private static byte[] eoB = { 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 1 };

	/** CubieCube array representing the 6 basic cube moves */
	static CubieCube[] moveCube = new CubieCube[6];
	static {
		moveCube[0] = new CubieCube();
		moveCube[0].cp = cpU;
		moveCube[0].co = coU;
		moveCube[0].ep = epU;
		moveCube[0].eo = eoU;

		moveCube[1] = new CubieCube();
		moveCube[1].cp = cpR;
		moveCube[1].co = coR;
		moveCube[1].ep = epR;
		moveCube[1].eo = eoR;

		moveCube[2] = new CubieCube();
		moveCube[2].cp = cpF;
		moveCube[2].co = coF;
		moveCube[2].ep = epF;
		moveCube[2].eo = eoF;

		moveCube[3] = new CubieCube();
		moveCube[3].cp = cpD;
		moveCube[3].co = coD;
		moveCube[3].ep = epD;
		moveCube[3].eo = eoD;

		moveCube[4] = new CubieCube();
		moveCube[4].cp = cpL;
		moveCube[4].co = coL;
		moveCube[4].ep = epL;
		moveCube[4].eo = eoL;

		moveCube[5] = new CubieCube();
		moveCube[5].cp = cpB;
		moveCube[5].co = coB;
		moveCube[5].ep = epB;
		moveCube[5].eo = eoB;
	}

	CubieCube() {

	};

	//TODO: delete
	CubieCube(Corner[] cp, byte[] co, Edge[] ep, byte[] eo) {
		this();
		for (int i = 0; i < 8; i++) {
			this.cp[i] = cp[i];
			this.co[i] = co[i];
		}
		for (int i = 0; i < 12; i++) {
			this.ep[i] = ep[i];
			this.eo[i] = eo[i];
		}
	}

	/** n choose k */
	static int Cnk(int n, int k) {
		int i, j, s;
		if (n < k)
			return 0;
		if (k > n / 2)
			k = n - k;
		for (s = 1, i = n, j = 1; i != n - k; i--, j++) {
			s *= i;
			s /= j;
		}
		return s;
	}

	/** Left rotation of all array elements between l and r */
	static void rotateLeft(Corner[] arr, int l, int r) {
		Corner temp = arr[l];
		for (int i = l; i < r; i++)
			arr[i] = arr[i + 1];
		arr[r] = temp;
	}

	/** Right rotation of all array elements between l and r */
	static void rotateRight(Corner[] arr, int l, int r) {
		Corner temp = arr[r];
		for (int i = r; i > l; i--)
			arr[i] = arr[i - 1];
		arr[l] = temp;
	}

	/** Left rotation of all array elements between l and r */
	static void rotateLeft(Edge[] arr, int l, int r) {
		Edge temp = arr[l];
		for (int i = l; i < r; i++)
			arr[i] = arr[i + 1];
		arr[r] = temp;
	}

	/** Right rotation of all array elements between l and r */
	static void rotateRight(Edge[] arr, int l, int r) {
		Edge temp = arr[r];
		for (int i = r; i > l; i--)
			arr[i] = arr[i - 1];
		arr[l] = temp;
	}

	/** return cube in facelet representation */ 
	FaceCube toFaceCube() {
		FaceCube fcRet = new FaceCube();
		for (Corner c : Corner.values()) {
			int i = c.ordinal();
			int j = cp[i].ordinal();// cornercubie with index j is at
			// cornerposition with index i
			byte ori = co[i];// Orientation of this cubie
			for (int n = 0; n < 3; n++)
				fcRet.facelets[FaceCube.cornerFacelet[i][(n + ori) % 3].ordinal()] = FaceCube.cornerColor[j][n];
		}
		for (Edge e : Edge.values()) {
			int i = e.ordinal();
			int j = ep[i].ordinal();// edgecubie with index j is at edgeposition
			// with index i
			byte ori = eo[i];// Orientation of this cubie
			for (int n = 0; n < 2; n++)
				fcRet.facelets[FaceCube.edgeFacelet[i][(n + ori) % 2].ordinal()] = FaceCube.edgeColor[j][n];
		}
		return fcRet;
	}

	/**
	 * Multiply this CubieCube with another CubieCube b, restricted to the corners.
	 * This is equivalent to applying the maneuvers done to b on this cube, but we only get the effect on the corners
	 * (on both their permutation and orientation)
	 * @param b - the cube to multiply by
	 */
	void cornerMultiply(CubieCube b) {
		Corner[] cPerm = new Corner[8];
		byte[] cOri = new byte[8];
		for (Corner corn : Corner.values()) {
			cPerm[corn.ordinal()] = cp[b.cp[corn.ordinal()].ordinal()];

			byte oriA = co[b.cp[corn.ordinal()].ordinal()];
			byte oriB = b.co[corn.ordinal()];
			byte ori = 0;
			// if both cubes are regular cubes
			if (oriA < 3 && oriB < 3) {
				//just do an addition modulo 3
				ori = (byte) (oriA + oriB); 
				if (ori >= 3)
					ori -= 3; // the composition is a regular cube
			}
			cOri[corn.ordinal()] = ori;
		}
		for (Corner c : Corner.values()) {
			cp[c.ordinal()] = cPerm[c.ordinal()];
			co[c.ordinal()] = cOri[c.ordinal()];
		}
	}

	/**
	 * Multiply this CubieCube with another CubieCube b, restricted to the edges.
	 * This is equivalent to applying the maneuvers done to b on this cube, but we only get the effect on the edges
	 * (on both their permutation and orientation)
	 * @param b - the cube to multiply by
	 */
	void edgeMultiply(CubieCube b) {
		Edge[] ePerm = new Edge[12];
		byte[] eOri = new byte[12];
		for (Edge edge : Edge.values()) {
			ePerm[edge.ordinal()] = ep[b.ep[edge.ordinal()].ordinal()];
			eOri[edge.ordinal()] = (byte) ((b.eo[edge.ordinal()] + eo[b.ep[edge.ordinal()].ordinal()]) % 2);
		}
		for (Edge e : Edge.values()) {
			ep[e.ordinal()] = ePerm[e.ordinal()];
			eo[e.ordinal()] = eOri[e.ordinal()];
		}
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Multiply this CubieCube with another CubieCube b.
	//TODO: delete or use for implementing patterns
	void multiply(CubieCube b) {
		cornerMultiply(b);
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Compute the inverse CubieCube
	//TODO: delete or use for implementing patterns
	void invCubieCube(CubieCube c) {
		for (Edge edge : Edge.values())
			c.ep[ep[edge.ordinal()].ordinal()] = edge;
		for (Edge edge : Edge.values())
			c.eo[edge.ordinal()] = eo[c.ep[edge.ordinal()].ordinal()];
		for (Corner corn : Corner.values())
			c.cp[cp[corn.ordinal()].ordinal()] = corn;
		for (Corner corn : Corner.values()) {
			byte ori = co[c.cp[corn.ordinal()].ordinal()];
			if (ori >= 3)// Just for completeness. We do not invert mirrored
				// cubes in the program.
				c.co[corn.ordinal()] = ori;
			else {// the standard case
				c.co[corn.ordinal()] = (byte) -ori;
				if (c.co[corn.ordinal()] < 0)
					c.co[corn.ordinal()] += 3;
			}
		}
	}

	// ********************************************* Get and set coordinates *********************************************

	/** @return corners orientation coordinate */
	short getTwist() {
		short ret = 0;
		for (int i = URF.ordinal(); i < DRB.ordinal(); i++)
			ret = (short) (3 * ret + co[i]);
		return ret;
	}

	/**
	 * Sets corners orientation according to coordinate value
	 * @param twist - corners orientation coordinate. 0 <= twist < 3^7
	 */
	void setTwist(short twist) {
		int twistParity = 0;
		for (int i = DRB.ordinal() - 1; i >= URF.ordinal(); i--) {
			twistParity += co[i] = (byte) (twist % 3);
			twist /= 3;
		}
		co[DRB.ordinal()] = (byte) ((3 - twistParity % 3) % 3);
	}

	/** @return edges orientation coordinate */
	short getFlip() {
		short ret = 0;
		for (int i = UR.ordinal(); i < BR.ordinal(); i++)
			ret = (short) (2 * ret + eo[i]);
		return ret;
	}

	/**
	 * Sets edges orientation according to coordinate value
	 * @param flip - edges orientation coordinate. 0 <= flip < 2^11
	 */
	void setFlip(short flip) {
		int flipParity = 0;
		for (int i = BR.ordinal() - 1; i >= UR.ordinal(); i--) {
			flipParity += eo[i] = (byte) (flip % 2);
			flip /= 2;
		}
		eo[BR.ordinal()] = (byte) ((2 - flipParity % 2) % 2);
	}

	/**
	 * @return the parity of the corner permutation. Parity of corners and edges are the same if the cube is solvable.
	 */
	short cornerParity() {
		int s = 0;
		//count for each corner how many of the corners to its left's order is higher than its order, and sum for all corners
		//the natural order is URF < UFL < ULB < UBR < DFR < DLF < DBL < DRB
		for (int i = DRB.ordinal(); i >= URF.ordinal() + 1; i--)
			for (int j = i - 1; j >= URF.ordinal(); j--)
				if (cp[j].ordinal() > cp[i].ordinal())
					s++;
		return (short) (s % 2);
	}

	/**
	 * @return the parity of the edges permutation. Parity of corners and edges are the same if the cube is solvable.
	 */
	short edgeParity() {
		int s = 0;
		//count for each edge how many of the edges to its left's order is higher than its order, and sum for all edges
		//the natural order is UR < UF < UL < UB < DR < DF < DL < DB < FR < FL < BL < BR
		for (int i = BR.ordinal(); i >= UR.ordinal() + 1; i--)
			for (int j = i - 1; j >= UR.ordinal(); j--)
				if (ep[j].ordinal() > ep[i].ordinal())
					s++;
		return (short) (s % 2);
	}

	/**
	 * @return the UD-slice edges FR,FL,BL and BR permutation's coordinate
	 */
	short getFRtoBR() {
		int a = 0, x = 0;
		Edge[] edge4 = new Edge[4];
		// compute the index a < (12 choose 4) and the permutation array edge4
		for (int j = BR.ordinal(); j >= UR.ordinal(); j--)
			if (FR.ordinal() <= ep[j].ordinal() && ep[j].ordinal() <= BR.ordinal()) {
				a += Cnk(11 - j, x + 1);
				edge4[3 - x++] = ep[j];
			}
		//compute the index b < 4! for the permutation in edge4
		int b = 0;
		for (int j = 3; j > 0; j--) {
			int k = 0;
			while (edge4[j].ordinal() != j + 8) {
				rotateLeft(edge4, 0, j);
				k++;
			}
			b = (j + 1) * b + k;
		}
		return (short) (24 * a + b);
	}

	/**
	 * Sets permutation of the UD-slice edges FR,FL,BL and BR
	 * @param idx - permutation coordinate value
	 */
	void setFRtoBR(short idx) {
		int x;
		Edge[] sliceEdge = { FR, FL, BL, BR };
		Edge[] otherEdge = { UR, UF, UL, UB, DR, DF, DL, DB };
		int b = idx % 24; // Permutation
		int a = idx / 24; // Combination
		for (Edge e : Edge.values())
			ep[e.ordinal()] = DB;// Use UR to invalidate all edges
		//generate permutation from index b
		for (int j = 1, k; j < 4; j++) {
			k = b % (j + 1);
			b /= j + 1;
			while (k-- > 0)
				rotateRight(sliceEdge, 0, j);
		}
		//generate combination and set slice edges
		x = 3;
		for (int j = UR.ordinal(); j <= BR.ordinal(); j++)
			if (a - Cnk(11 - j, x + 1) >= 0) {
				ep[j] = sliceEdge[3 - x];
				a -= Cnk(11 - j, x-- + 1);
			}
		//set the remaining edges UR..DB
		x = 0;
		for (int j = UR.ordinal(); j <= BR.ordinal(); j++)
			if (ep[j] == DB)
				ep[j] = otherEdge[x++];

	}

	/**
	 * @return all corners except DBL and DRB permutation's coordinate
	 */
	short getURFtoDLF() {
		int a = 0, x = 0;
		Corner[] corner6 = new Corner[6];
		//compute the index a < (8 choose 6) and the corner permutation
		for (int j = URF.ordinal(); j <= DRB.ordinal(); j++)
			if (cp[j].ordinal() <= DLF.ordinal()) {
				a += Cnk(j, x + 1);
				corner6[x++] = cp[j];
			}
		//compute the index b < 6! for the permutation in corner6
		int b = 0;
		for (int j = 5; j > 0; j--) {
			int k = 0;
			while (corner6[j].ordinal() != j) {
				rotateLeft(corner6, 0, j);
				k++;
			}
			b = (j + 1) * b + k;
		}
		return (short) (720 * a + b);
	}

	/**
	 * Sets permutation of all corners except DBL and DRB
	 * @param idx - permutation coordinate value
	 */
	void setURFtoDLF(short idx) {
		int x;
		Corner[] corner6 = { URF, UFL, ULB, UBR, DFR, DLF };
		Corner[] otherCorner = { DBL, DRB };
		int b = idx % 720; // Permutation
		int a = idx / 720; // Combination
		for (Corner c : Corner.values())
			cp[c.ordinal()] = DRB; //use DRB to invalidate all corners
		//generate permutation from index b
		for (int j = 1, k; j < 6; j++) {
			k = b % (j + 1);
			b /= j + 1;
			while (k-- > 0)
				rotateRight(corner6, 0, j);
		}
		//generate combination and set corners
		x = 5;
		for (int j = DRB.ordinal(); j >= 0; j--)
			if (a - Cnk(j, x + 1) >= 0) {
				cp[j] = corner6[x];
				a -= Cnk(j, x-- + 1);
			}
		x = 0;
		for (int j = URF.ordinal(); j <= DRB.ordinal(); j++)
			if (cp[j] == DRB)
				cp[j] = otherCorner[x++];
	}

	/** Permutation of the six edges UR,UF,UL,UB,DR,DF.
	 *  Positions of the DL and DB edges are determined by the parity. 
	 *  Relevant in phase 2 when FR, FL, BL and BR are already in the UD slice
	 * @return the six edges UR,UF,UL,UB,DR,DF permutation's coordinate
	 */
	int getURtoDF() {
		int a = 0, x = 0;
		Edge[] edge6 = new Edge[6];
		// compute the index a < (12 choose 6) and the edge permutation
		for (int j = UR.ordinal(); j <= BR.ordinal(); j++)
			if (ep[j].ordinal() <= DF.ordinal()) {
				a += Cnk(j, x + 1);
				edge6[x++] = ep[j];
			}
		//compute the index b < 6! for the permutation in edge6
		int b = 0;
		for (int j = 5; j > 0; j--)
		{
			int k = 0;
			while (edge6[j].ordinal() != j) {
				rotateLeft(edge6, 0, j);
				k++;
			}
			b = (j + 1) * b + k;
		}
		return 720 * a + b;
	}

	/** Permutation of the six edges UR,UF,UL,UB,DR,DF.
	 *  Positions of the DL and DB edges are determined by the parity. 
	 *  Relevant in phase 2 when FR, FL, BL and BR are already in the UD slice
	 * @param idx - permutation coordinate value
	 */
	void setURtoDF(int idx) {
		int x;
		Edge[] edge6 = { UR, UF, UL, UB, DR, DF };
		Edge[] otherEdge = { DL, DB, FR, FL, BL, BR };
		int b = idx % 720; //permutation
		int a = idx / 720; //combination
		for (Edge e : Edge.values())
			ep[e.ordinal()] = BR; //use BR to invalidate all edges
		//generate permutation from index b
		for (int j = 1, k; j < 6; j++)
		{
			k = b % (j + 1);
			b /= j + 1;
			while (k-- > 0)
				rotateRight(edge6, 0, j);
		}
		//generate combination and set edges
		x = 5;
		for (int j = BR.ordinal(); j >= 0; j--)
			if (a - Cnk(j, x + 1) >= 0) {
				ep[j] = edge6[x];
				a -= Cnk(j, x-- + 1);
			}
		//set the remaining edges DL..BR
		x = 0;
		for (int j = UR.ordinal(); j <= BR.ordinal(); j++)
			if (ep[j] == BR)
				ep[j] = otherEdge[x++];
	}

	/**
	 * Permutation of the six edges UR,UF,UL,UB,DR,DF
	 * @param idx1 - UR, UF and UL permutation coordinate value
	 * @param idx2 - UB, DR and DF permutation coordinate value
	 * @return the six edges UR,UF,UL,UB,DR,DF permutation coordinate
	 */
	public static int getURtoDF(short idx1, short idx2) {
		CubieCube a = new CubieCube();
		CubieCube b = new CubieCube();
		a.setURtoUL(idx1);
		b.setUBtoDF(idx2);
		for (int i = 0; i < 8; i++) {
			if (a.ep[i] != BR)
				if (b.ep[i] != BR)// collision
					return -1;
				else
					b.ep[i] = a.ep[i];
		}
		return b.getURtoDF();
	}

	/**
	 * Permutation of the three edges UR,UF,UL
	 * @return the three edges UR,UF,UL permutation coordinate
	 */
	short getURtoUL() {
		int a = 0, x = 0;
		Edge[] edge3 = new Edge[3];
		// compute the index a < (12 choose 3) and the edge permutation.
		for (int j = UR.ordinal(); j <= BR.ordinal(); j++)
			if (ep[j].ordinal() <= UL.ordinal()) {
				a += Cnk(j, x + 1);
				edge3[x++] = ep[j];
			}

		int b = 0;
		for (int j = 2; j > 0; j--)// compute the index b < 3! for the
		// permutation in edge3
		{
			int k = 0;
			while (edge3[j].ordinal() != j) {
				rotateLeft(edge3, 0, j);
				k++;
			}
			b = (j + 1) * b + k;
		}
		return (short) (6 * a + b);
	}

	/**
	 * Sets the permutation of the three edges UR,UF,UL
	 * @param idx - permutation coordinate value
	 */
	void setURtoUL(short idx) {
		int x;
		Edge[] edge3 = { UR, UF, UL };
		int b = idx % 6; // Permutation
		int a = idx / 6; // Combination
		for (Edge e : Edge.values())
			ep[e.ordinal()] = BR;// Use BR to invalidate all edges

		for (int j = 1, k; j < 3; j++)// generate permutation from index b
		{
			k = b % (j + 1);
			b /= j + 1;
			while (k-- > 0)
				rotateRight(edge3, 0, j);
		}
		x = 2;// generate combination and set edges
		for (int j = BR.ordinal(); j >= 0; j--)
			if (a - Cnk(j, x + 1) >= 0) {
				ep[j] = edge3[x];
				a -= Cnk(j, x-- + 1);
			}
	}

	/**
	 * Permutation of the three edges UB,DR,DF
	 * @return the three edges UB,DR,DF permutation coordinate
	 */
	short getUBtoDF() {
		int a = 0, x = 0;
		Edge[] edge3 = new Edge[3];
		// compute the index a < (12 choose 3) and the edge permutation.
		for (int j = UR.ordinal(); j <= BR.ordinal(); j++)
			if (UB.ordinal() <= ep[j].ordinal() && ep[j].ordinal() <= DF.ordinal()) {
				a += Cnk(j, x + 1);
				edge3[x++] = ep[j];
			}

		int b = 0;
		for (int j = 2; j > 0; j--)// compute the index b < 3! for the
		// permutation in edge3
		{
			int k = 0;
			while (edge3[j].ordinal() != UB.ordinal() + j) {
				rotateLeft(edge3, 0, j);
				k++;
			}
			b = (j + 1) * b + k;
		}
		return (short) (6 * a + b);
	}

	/**
	 * Sets the permutation of the three edges UB,DR,DF
	 * @param idx - permutation coordinate value
	 */
	void setUBtoDF(short idx) {
		int x;
		Edge[] edge3 = { UB, DR, DF };
		int b = idx % 6; // Permutation
		int a = idx / 6; // Combination
		for (Edge e : Edge.values())
			ep[e.ordinal()] = BR;// Use BR to invalidate all edges

		for (int j = 1, k; j < 3; j++)// generate permutation from index b
		{
			k = b % (j + 1);
			b /= j + 1;
			while (k-- > 0)
				rotateRight(edge3, 0, j);
		}
		x = 2;// generate combination and set edges
		for (int j = BR.ordinal(); j >= 0; j--)
			if (a - Cnk(j, x + 1) >= 0) {
				ep[j] = edge3[x];
				a -= Cnk(j, x-- + 1);
			}
	}

	//TODO: delete. not in use
	int getURFtoDLB2() {
		Corner[] perm = new Corner[8];
		int b = 0;
		for (int i = 0; i < 8; i++)
			perm[i] = cp[i];
		for (int j = 7; j > 0; j--)// compute the index b < 8! for the permutation in perm
		{
			int k = 0;
			while (perm[j].ordinal() != j) {
				rotateLeft(perm, 0, j);
				k++;
			}
			b = (j + 1) * b + k;
		}
		return b;
	}

	/**
	 * Sets all corners according to coordinate
	 * @param idx - corners permutation coordinate
	 */
	void setURFtoDLB(int idx) {
		Corner[] perm = { URF, UFL, ULB, UBR, DFR, DLF, DBL, DRB };
		int k;
		for (int j = 1; j < 8; j++) {
			k = idx % (j + 1);
			idx /= j + 1;
			while (k-- > 0)
				rotateRight(perm, 0, j);
		}
		int x = 7;// set corners
		for (int j = 7; j >= 0; j--)
			cp[j] = perm[x--];
	}

	//TODO: delete. not in use
	int getURtoBR() {
		Edge[] perm = new Edge[12];
		int b = 0;
		for (int i = 0; i < 12; i++)
			perm[i] = ep[i];
		for (int j = 11; j > 0; j--)// compute the index b < 12! for the permutation in perm
		{
			int k = 0;
			while (perm[j].ordinal() != j) {
				rotateLeft(perm, 0, j);
				k++;
			}
			b = (j + 1) * b + k;
		}
		return b;
	}

	/**
	 * Sets all edges according to coordinate
	 * @param idx - edged permutation coordinate
	 */
	void setURtoBR(int idx) {
		Edge[] perm = { UR, UF, UL, UB, DR, DF, DL, DB, FR, FL, BL, BR };
		int k;
		for (int j = 1; j < 12; j++) {
			k = idx % (j + 1);
			idx /= j + 1;
			while (k-- > 0)
				rotateRight(perm, 0, j);
		}
		int x = 11;// set edges
		for (int j = 11; j >= 0; j--)
			ep[j] = perm[x--];
	}

	/** Validate received cube (check that it is a valid one)
	 * @return error code:
	 * 		0: Cube is solvable
	 * 		-2: Not all 12 edges exist exactly once
	 * 		-3: Flip error: One edge has to be flipped
	 * 		-4: Not all corners exist exactly once
	 * 		-5: Twist error: One corner has to be twisted
	 * 		-6: Parity error: Two corners or two edges have to be exchanged
	 */
	int verify() {
		int sum = 0;
		//check that all 12 edges exist exactly once
		int[] edgeCount = new int[12];
		for (Edge e : Edge.values())
			edgeCount[ep[e.ordinal()].ordinal()]++;
		for (int i = 0; i < 12; i++)
			if (edgeCount[i] != 1)
				return -2;
		//check that total edges orientation is even
		//if not, there is a flipped edge
		for (int i = 0; i < 12; i++)
			sum += eo[i];
		if (sum % 2 != 0)
			return -3;
		//check that all 8 corners exist exactly once
		int[] cornerCount = new int[8];
		for (Corner c : Corner.values())
			cornerCount[cp[c.ordinal()].ordinal()]++;
		for (int i = 0; i < 8; i++)
			if (cornerCount[i] != 1)
				return -4;
		//check that total corners orientation divides by 3
		//if not, there is a twisted corner
		sum = 0;
		for (int i = 0; i < 8; i++)
			sum += co[i];
		if (sum % 3 != 0)
			return -5;
		//check that 
		if ((edgeParity() ^ cornerParity()) != 0)
			return -6;// parity error

		return 0;// cube ok
	}
}
