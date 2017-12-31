package twophase;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

import twophase.Color;
import twophase.CubieCube;
import twophase.FaceCube;

public class CubieCubeTest {

	/**
	 * Tests FaceCube constructor
	 */
	@Test
	public void test_ctor() {
		//test empty constructor
		Color[] expected = {
				Color.U, Color.U, Color.U, Color.U, Color.U, Color.U, Color.U, Color.U, Color.U,
				Color.R, Color.R, Color.R, Color.R, Color.R, Color.R, Color.R, Color.R, Color.R,
				Color.F, Color.F, Color.F, Color.F, Color.F, Color.F, Color.F, Color.F, Color.F,
				Color.D, Color.D, Color.D, Color.D, Color.D, Color.D, Color.D, Color.D, Color.D,
				Color.L, Color.L, Color.L, Color.L, Color.L, Color.L, Color.L, Color.L, Color.L,
				Color.B, Color.B, Color.B, Color.B, Color.B, Color.B, Color.B, Color.B, Color.B};
		FaceCube c = new FaceCube();
		assertArrayEquals(expected, c.facelets);
		
		//test constructor that receives a string
		Color[] expected2 = {
				Color.B, Color.B, Color.U, Color.R, Color.U, Color.D, Color.B, Color.F, Color.U,
				Color.F, Color.F, Color.F, Color.R, Color.R, Color.F, Color.U, Color.U, Color.F,
				Color.L, Color.U, Color.L, Color.U, Color.F, Color.U, Color.D, Color.L, Color.R,
				Color.R, Color.D, Color.B, Color.B, Color.D, Color.B, Color.D, Color.B, Color.L,
				Color.U, Color.D, Color.D, Color.F, Color.L, Color.L, Color.R, Color.R, Color.B,
				Color.R, Color.L, Color.L, Color.L, Color.B, Color.R, Color.D, Color.D, Color.F };
		c = new FaceCube("BBURUDBFUFFFRRFUUFLULUFUDLRRDBBDBDBLUDDFLLRRBRLLLBRDDF");
		assertArrayEquals(expected2, c.facelets);
	}
	
	@Test
	public void test_toCubieCube() {
		//solved cube
		FaceCube fc = new FaceCube();
		CubieCube cc = fc.toCubieCube();
		CubieCube ccExpected = new CubieCube();
		//check that all properties are equal
		assertArrayEquals("cp should be equal", ccExpected.cp, cc.cp);
		assertArrayEquals("co should be equal", ccExpected.co, cc.co);
		assertArrayEquals("ep should be equal", ccExpected.ep, cc.ep);
		assertArrayEquals("eo should be equal", ccExpected.eo, cc.eo);
	}
}
