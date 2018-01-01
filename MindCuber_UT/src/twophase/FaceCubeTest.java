package twophase;

import static org.junit.Assert.assertArrayEquals;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

import org.junit.Test;

import twophase.Color;
import twophase.CubieCube;
import twophase.FaceCube;

public class FaceCubeTest {
	
	private static final String TO_CUBIE_CUBE_TEST_FILE = TwoPhaseTestUtils.TEST_FILES_DIR + "/cubesWithData.txt";

	/**
	 * Tests FaceCube constructor
	 */
	@Test
	public void testCtor() {
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
	
	/**
	 * Tests toCubieCube function
	 */
	@Test
	public void testToCubieCube() {
		//test identity cube (face cube with no parameters)
		FaceCube faceCube = new FaceCube();
		CubieCube cubieCube = faceCube.toCubieCube();
		CubieCube ccExpected = new CubieCube();
		assertArrayEquals("corner permutation array is not as expected for the identity cube",
				ccExpected.cp, cubieCube.cp);
		assertArrayEquals("corner orientation array is not as expected for the identity cube",
				ccExpected.co, cubieCube.co);
		assertArrayEquals("edge permutation array is not as expected for the identity cube",
				ccExpected.ep, cubieCube.ep);
		assertArrayEquals("edge orientation array is not as expected for the identity cube",
				ccExpected.eo, cubieCube.eo);
		//test cubes from file
		try (Scanner scanner = new Scanner(Paths.get(TO_CUBIE_CUBE_TEST_FILE))) {
			//read first line
			scanner.nextLine();
			//iterate through all cubes for test
			while(scanner.hasNextLine()) {				
				String[] params = scanner.nextLine().split("\t");
				//create facecube
				faceCube = new FaceCube(params[0]);
				//create cubiecube
				cubieCube = faceCube.toCubieCube();
				//parse expected fields
				Corner[] expectedCornerPermutaion = TwoPhaseTestUtils.parseCornerArray(params[9]);
				byte[] expectedCornerOrientation = TwoPhaseTestUtils.parseByteArray(params[10]);
				Edge[] expectedEdgePermutaion = TwoPhaseTestUtils.parseEdgeArray(params[11]);
				byte[] expectedEdgeOrientation = TwoPhaseTestUtils.parseByteArray(params[12]);
				//assert fields
				assertArrayEquals(String.format("corner permutation array is not as expected for cube %s", params[0]),
						expectedCornerPermutaion, cubieCube.cp);
				assertArrayEquals(String.format("corner orientation array is not as expected for cube %s", params[0]),
						expectedCornerOrientation, cubieCube.co);
				assertArrayEquals(String.format("edge permutation array is not as expected for cube %s", params[0]),
						expectedEdgePermutaion, cubieCube.ep);
				assertArrayEquals(String.format("edge orientation array is not as expected for cube %s", params[0]),
						expectedEdgeOrientation, cubieCube.eo);
			}
		} catch (IOException e) {
			System.out.printf("Error opening test file %s %n", TO_CUBIE_CUBE_TEST_FILE);
			e.printStackTrace();
		}
	}
}
