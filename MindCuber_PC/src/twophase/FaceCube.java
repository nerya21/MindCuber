package twophase;

import static twophase.Color.*;
import static twophase.Corner.*;
import static twophase.Edge.*;
import static twophase.Facelet.*;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/** Cube on the facelet level
 *  <br><a href="http://kociemba.org/math/faceletlevel.htm">explanation on kociemba's site</a> */
class FaceCube {
	/** Cube facelets colors. The order of the facelets is:<br>
	 *  U1 U2 ... U9 R1 R2 ... R9 F1 F2 ... F9 D1 D2 ... D9 L1 L2 ... L9 B1 B2 ... B9 */
	public Color[] facelets = new Color[54];

	/** Map the corner positions to facelet positions. cornerFacelet[URF.ordinal()][0] e.g. gives the position of the
	  * facelet in the URF corner position, which defines the orientation.<br>
	  * cornerFacelet[URF.ordinal()][1] and cornerFacelet[URF.ordinal()][2] give the position of the other two facelets
	  * of the URF corner (clockwise). */
	final static Facelet[][] cornerFacelet = { { U9, R1, F3 }, { U7, F1, L3 }, { U1, L1, B3 }, { U3, B1, R3 },
			{ D3, F9, R7 }, { D1, L9, F7 }, { D7, B9, L7 }, { D9, R9, B7 } };

	/** Map the edge positions to facelet positions. edgeFacelet[UR.ordinal()][0] e.g. gives the position of the facelet in
	  * the UR edge position, which defines the orientation.<br>
	  * edgeFacelet[UR.ordinal()][1] gives the position of the other facelet */
	final static Facelet[][] edgeFacelet = { { U6, R2 }, { U8, F2 }, { U4, L2 }, { U2, B2 }, { D6, R8 }, { D2, F8 },
			{ D4, L8 }, { D8, B8 }, { F6, R4 }, { F4, L6 }, { B6, L4 }, { B4, R6 } };

	/** Map the corner positions to facelet colors */
	final static Color[][] cornerColor = { { U, R, F }, { U, F, L }, { U, L, B }, { U, B, R }, { D, F, R }, { D, L, F },
			{ D, B, L }, { D, R, B } };

	/** Map the edge positions to facelet colors */
	final static Color[][] edgeColor = { { U, R }, { U, F }, { U, L }, { U, B }, { D, R }, { D, F }, { D, L }, { D, B },
			{ F, R }, { F, L }, { B, L }, { B, R } };

	/**
	 * Constructs the facelet level of a solved cube.
	 * This cube represented by the string:
	 * UUUUUUUUURRRRRRRRRFFFFFFFFFDDDDDDDDDLLLLLLLLLBBBBBBBBB
	 */
	FaceCube() {
		this("UUUUUUUUURRRRRRRRRFFFFFFFFFDDDDDDDDDLLLLLLLLLBBBBBBBBB");
	}

	/**
	 * Constructs the facelet level of the cube from a string
	 * @param cubeString is the string representation of the cube:
	 * U1 U2 ... U9 R1 R2 ... R9 F1 F2 ... F9 D1 D2 ... D9 L1 L2 ... L9 B1 B2 ... B9 
	 */
	FaceCube(String cubeString) {
		for (int i = 0; i < cubeString.length(); i++)
			facelets[i] = Color.valueOf(cubeString.substring(i, i + 1));
	}
	
	FaceCube(Color[] facelets) {
		this.facelets = facelets;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Gives string representation of a facelet cube
	String to_String() {
		String s = "";
		for (int i = 0; i < 54; i++)
			s += facelets[i].toString();
		return s;
	}

	/**
	 * Returns a CubieCube instance which is the cubie level representation of the given cube.
	 * The given cube is represented in the facelet level
	 * @return the cubie level representation of the given cube
	 */
	CubieCube toCubieCube() {
		CubieCube ccRet = new CubieCube();
		//initialize corners and edges permutations with invalid data
		for (int i = 0; i < 8; i++)
			ccRet.cp[i] = URF;
		for (int i = 0; i < 12; i++)
			ccRet.ep[i] = UR;
		//set corners' orientation and permutation
		Color col1, col2;
		byte ori; //corner orientation
		for (Corner i : Corner.values()) {
			//get the colors of the cubie at corner i, starting with U/D cublie faces
			for (ori = 0; ori < 3; ori++)
				if (facelets[cornerFacelet[i.ordinal()][ori].ordinal()] == U || facelets[cornerFacelet[i.ordinal()][ori].ordinal()] == D)
					break;
			col1 = facelets[cornerFacelet[i.ordinal()][(ori + 1) % 3].ordinal()];
			col2 = facelets[cornerFacelet[i.ordinal()][(ori + 2) % 3].ordinal()];
			//find the original place of the ith corner cubie
			for (Corner j : Corner.values()) {
				if (col1 == cornerColor[j.ordinal()][1] && col2 == cornerColor[j.ordinal()][2]) {
					//in corner position i we have corner cubie j
					ccRet.cp[i.ordinal()] = j;
					ccRet.co[i.ordinal()] = (byte) (ori % 3);
					break;
				}
			}
		}
		//set edges' orientation and permutation
		for (Edge i : Edge.values())
			//find for each edge its original position and orientation
			for (Edge j : Edge.values()) {
				if (facelets[edgeFacelet[i.ordinal()][0].ordinal()] == edgeColor[j.ordinal()][0]
						&& facelets[edgeFacelet[i.ordinal()][1].ordinal()] == edgeColor[j.ordinal()][1]) {
					//in edge position i we have edge cubie j
					ccRet.ep[i.ordinal()] = j;
					ccRet.eo[i.ordinal()] = 0; //original orientation
					break;
				}
				if (facelets[edgeFacelet[i.ordinal()][0].ordinal()] == edgeColor[j.ordinal()][1]
						&& facelets[edgeFacelet[i.ordinal()][1].ordinal()] == edgeColor[j.ordinal()][0]) {
					//in edge position i we have edge cubie j
					ccRet.ep[i.ordinal()] = j;
					ccRet.eo[i.ordinal()] = 1; //flipped orientation
					break;
				}
			}
		return ccRet;
	};
}
