package twophase;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

import org.junit.Test;

public class CubieCubeTest {
	
	private static final String ROTATE_TEST_FILE = TwoPhaseTestUtils.TEST_FILES_DIR + "/rotateArrays.txt";
	private static final String MULTIPLY_TEST_FILE = TwoPhaseTestUtils.TEST_FILES_DIR + "/multiplyCubes.txt";
	private static final String INV_CUBE_TEST_FILE = TwoPhaseTestUtils.TEST_FILES_DIR + "/inverseCubes.txt";
	private static final String COORDINATES_TEST_FILE = TwoPhaseTestUtils.TEST_FILES_DIR + "/cubesWithData.txt";
	private static final String VERIFY_TEST_FILE = TwoPhaseTestUtils.TEST_FILES_DIR + "/verifyCubes.txt";
	
	/**
	 * Tests CubieCube.rotateLeft and CubieCube.rotateRight functions
	 */
	@Test
	public void testRotate() {
		try (Scanner scanner = new Scanner(Paths.get(ROTATE_TEST_FILE))) {
			//test corners rotation left
			scanner.nextLine(); //read first line
			String line;
			int i = 1;
			//check if reached next section
			while(!(line = scanner.nextLine()).startsWith("--")) {
				String[] params = line.split("\t");
				//parse original array, l, r and expected array
				Corner[] original = TwoPhaseTestUtils.parseCornerArray(params[0]);
				int l = Integer.parseInt(params[1]);
				int r = Integer.parseInt(params[2]);
				Corner[] expected = TwoPhaseTestUtils.parseCornerArray(params[3]);
				//rotate and assert result
				CubieCube.rotateLeft(original, l, r);
				assertArrayEquals(String.format("Corner left rotation result is not as expected in test no.%d", i),
						expected, original);
				i++;
			}
			
			//test corners rotation right
			i = 1;
			while(!(line = scanner.nextLine()).startsWith("--")) {
				String[] params = line.split("\t");
				//parse original array, l, r and expected array
				Corner[] original = TwoPhaseTestUtils.parseCornerArray(params[0]);
				int l = Integer.parseInt(params[1]);
				int r = Integer.parseInt(params[2]);
				Corner[] expected = TwoPhaseTestUtils.parseCornerArray(params[3]);
				//rotate and assert result
				CubieCube.rotateRight(original, l, r);
				assertArrayEquals(String.format("Corner right rotation result is not as expected in test no.%d", i),
						expected, original);
				i++;
			}
			
			//test edges rotation left
			i = 1;
			while(!(line = scanner.nextLine()).startsWith("--")) {
				String[] params = line.split("\t");
				//parse original array, l, r and expected array
				Edge[] original = TwoPhaseTestUtils.parseEdgeArray(params[0]);
				int l = Integer.parseInt(params[1]);
				int r = Integer.parseInt(params[2]);
				Edge[] expected = TwoPhaseTestUtils.parseEdgeArray(params[3]);
				//rotate and assert result
				CubieCube.rotateLeft(original, l, r);
				assertArrayEquals(String.format("Edge left rotation result is not as expected in test no.%d", i),
						expected, original);
				i++;
			}
			
			//test edges rotation right
			i = 1;
			while(scanner.hasNext()) {
				String[] params = scanner.nextLine().split("\t");
				//parse original array, l, r and expected array
				Edge[] original = TwoPhaseTestUtils.parseEdgeArray(params[0]);
				int l = Integer.parseInt(params[1]);
				int r = Integer.parseInt(params[2]);
				Edge[] expected = TwoPhaseTestUtils.parseEdgeArray(params[3]);
				//rotate and assert result
				CubieCube.rotateRight(original, l, r);
				assertArrayEquals(String.format("Edge right rotation result is not as expected in test no.%d", i),
						expected, original);
				i++;
			}
		} catch (IOException e) {
			System.out.printf("Error opening test file %s %n", ROTATE_TEST_FILE);
			e.printStackTrace();
		}
	}
	
	/**
	 * Tests cornerMultiply function
	 */
	@Test
	public void testCornerMultiply() {
		try (Scanner scanner = new Scanner(Paths.get(MULTIPLY_TEST_FILE))) {
			scanner.nextLine(); //read first line
			CubieCube cubeA = new CubieCube();
			CubieCube cubeB = new CubieCube();
			int i = 1;
			//iterate through all cubes for test
			while(scanner.hasNextLine()) {
				String[] line = scanner.nextLine().split("\t");
				//create cube to be multiplied
				cubeA.cp = TwoPhaseTestUtils.parseCornerArray(line[0]);
				cubeA.co = TwoPhaseTestUtils.parseByteArray(line[1]);
				Edge[] originalEdgePermutaion = TwoPhaseTestUtils.parseEdgeArray(line[2]);
				byte[] originalEdgeOrientation = TwoPhaseTestUtils.parseByteArray(line[3]);
				System.arraycopy(originalEdgePermutaion, 0, cubeA.ep, 0, originalEdgePermutaion.length);
				System.arraycopy(originalEdgeOrientation, 0, cubeA.eo, 0, originalEdgeOrientation.length);
				//create cube to multiply by
				cubeB.cp = TwoPhaseTestUtils.parseCornerArray(line[4]);
				cubeB.co = TwoPhaseTestUtils.parseByteArray(line[5]);
				cubeB.ep = TwoPhaseTestUtils.parseEdgeArray(line[6]);
				cubeB.eo = TwoPhaseTestUtils.parseByteArray(line[7]);
				//parse expected fields
				Corner[] expectedCornerPermutaion = TwoPhaseTestUtils.parseCornerArray(line[8]);
				byte[] expectedCornerOrientation = TwoPhaseTestUtils.parseByteArray(line[9]);
				//multiply
				cubeA.cornerMultiply(cubeB);
				//assert fields
				assertArrayEquals(String.format("corner permutation array is not as expected in corner multiplication test no.%d", i),
						expectedCornerPermutaion, cubeA.cp);
				assertArrayEquals(String.format("corner orientation array is not as expected in corner multiplication test no.%d", i),
						expectedCornerOrientation, cubeA.co);
				//edges should remain the same
				assertArrayEquals(String.format("edge permutation array is not as expected in corner multiplication test no.%d", i),
						originalEdgePermutaion, cubeA.ep);
				assertArrayEquals(String.format("edge orientation array is not as expected in corner multiplication test no.%d", i),
						originalEdgeOrientation, cubeA.eo);
				i++;
			}
		}
		catch (IOException e) {
			System.out.printf("Error opening test file %s %n", MULTIPLY_TEST_FILE);
			e.printStackTrace();
		}
	}
	
	/**
	 * Tests cornerMultiply function
	 */
	@Test
	public void testEdgeMultiply() {
		try (Scanner scanner = new Scanner(Paths.get(MULTIPLY_TEST_FILE))) {
			scanner.nextLine(); //read first line
			CubieCube cubeA = new CubieCube();
			CubieCube cubeB = new CubieCube();
			int i = 1;
			//iterate through all cubes for test
			while(scanner.hasNextLine()) {
				String[] line = scanner.nextLine().split("\t");
				//create cube to be multiplied
				Corner[] originalCornerPermutaion = TwoPhaseTestUtils.parseCornerArray(line[0]);
				byte[] originalCornerOrientation = TwoPhaseTestUtils.parseByteArray(line[1]);
				System.arraycopy(originalCornerPermutaion, 0, cubeA.cp, 0, originalCornerPermutaion.length);
				System.arraycopy(originalCornerOrientation, 0, cubeA.co, 0, originalCornerOrientation.length);
				cubeA.ep = TwoPhaseTestUtils.parseEdgeArray(line[2]);
				cubeA.eo = TwoPhaseTestUtils.parseByteArray(line[3]);
				//create cube to multiply by
				cubeB.cp = TwoPhaseTestUtils.parseCornerArray(line[4]);
				cubeB.co = TwoPhaseTestUtils.parseByteArray(line[5]);
				cubeB.ep = TwoPhaseTestUtils.parseEdgeArray(line[6]);
				cubeB.eo = TwoPhaseTestUtils.parseByteArray(line[7]);
				//parse expected fields
				Edge[] expectedEdgePermutaion = TwoPhaseTestUtils.parseEdgeArray(line[10]);
				byte[] expectedEdgeOrientation = TwoPhaseTestUtils.parseByteArray(line[11]);
				//multiply
				cubeA.edgeMultiply(cubeB);
				//assert fields
				//corners should remain the same
				assertArrayEquals(String.format("corner permutation array is not as expected in edge multiplication test no.%d", i),
						originalCornerPermutaion, cubeA.cp);
				assertArrayEquals(String.format("corner orientation array is not as expected in edge multiplication test no.%d", i),
						originalCornerOrientation, cubeA.co);
				assertArrayEquals(String.format("edge permutation array is not as expected in edge multiplication test no.%d", i),
						expectedEdgePermutaion, cubeA.ep);
				assertArrayEquals(String.format("edge orientation array is not as expected in edge multiplication test no.%d", i),
						expectedEdgeOrientation, cubeA.eo);
				i++;
			}
		}
		catch (IOException e) {
			System.out.printf("Error opening test file %s %n", MULTIPLY_TEST_FILE);
			e.printStackTrace();
		}
	}
	
	/**
	 * Tests multiply function
	 */
	@Test
	public void testMultiply() {
		try (Scanner scanner = new Scanner(Paths.get(MULTIPLY_TEST_FILE))) {
			scanner.nextLine(); //read first line
			CubieCube cubeA = new CubieCube();
			CubieCube cubeB = new CubieCube();
			CubieCube identityCube = new CubieCube();
			int i = 1;
			//iterate through all cubes for test
			while(scanner.hasNextLine()) {
				String[] line = scanner.nextLine().split("\t");
				//create cube to be multiplied
				Corner[] originalCornerPermutaion = TwoPhaseTestUtils.parseCornerArray(line[0]);
				byte[] originalCornerOrientation = TwoPhaseTestUtils.parseByteArray(line[1]);
				Edge[] originalEdgePermutaion = TwoPhaseTestUtils.parseEdgeArray(line[2]);
				byte[] originalEdgeOrientation = TwoPhaseTestUtils.parseByteArray(line[3]);
				System.arraycopy(originalCornerPermutaion, 0, cubeA.cp, 0, originalCornerPermutaion.length);
				System.arraycopy(originalCornerOrientation, 0, cubeA.co, 0, originalCornerOrientation.length);
				System.arraycopy(originalEdgePermutaion, 0, cubeA.ep, 0, originalEdgePermutaion.length);
				System.arraycopy(originalEdgeOrientation, 0, cubeA.eo, 0, originalEdgeOrientation.length);
				//test multiplying by the identity cube
				cubeA.multiply(identityCube);
				//assert that fields remain the same
				assertArrayEquals(String.format("corner permutation array changed in multiplication with id cube test no.%d", i),
						originalCornerPermutaion, cubeA.cp);
				assertArrayEquals(String.format("corner orientation array changed in multiplication with id cube test no.%d", i),
						originalCornerOrientation, cubeA.co);
				assertArrayEquals(String.format("edge permutation array changed in multiplication with id cube test no.%d", i),
						originalEdgePermutaion, cubeA.ep);
				assertArrayEquals(String.format("edge orientation array changed in multiplication with id cube test no.%d", i),
						originalEdgeOrientation, cubeA.eo);
				//create cube to multiply by
				cubeB.cp = TwoPhaseTestUtils.parseCornerArray(line[4]);
				cubeB.co = TwoPhaseTestUtils.parseByteArray(line[5]);
				cubeB.ep = TwoPhaseTestUtils.parseEdgeArray(line[6]);
				cubeB.eo = TwoPhaseTestUtils.parseByteArray(line[7]);
				//parse expected fields
				Corner[] expectedCornerPermutaion = TwoPhaseTestUtils.parseCornerArray(line[8]);
				byte[] expectedCornerOrientation = TwoPhaseTestUtils.parseByteArray(line[9]);
				Edge[] expectedEdgePermutaion = TwoPhaseTestUtils.parseEdgeArray(line[10]);
				byte[] expectedEdgeOrientation = TwoPhaseTestUtils.parseByteArray(line[11]);
				//multiply
				cubeA.multiply(cubeB);
				//assert fields
				assertArrayEquals(String.format("corner permutation array is not as expected in multiplication test no.%d", i),
						expectedCornerPermutaion, cubeA.cp);
				assertArrayEquals(String.format("corner orientation array is not as expected in multiplication test no.%d", i),
						expectedCornerOrientation, cubeA.co);
				assertArrayEquals(String.format("edge permutation array is not as expected in multiplication test no.%d", i),
						expectedEdgePermutaion, cubeA.ep);
				assertArrayEquals(String.format("edge orientation array is not as expected in multiplication test no.%d", i),
						expectedEdgeOrientation, cubeA.eo);
				i++;
			}
		}
		catch (IOException e) {
			System.out.printf("Error opening test file %s %n", MULTIPLY_TEST_FILE);
			e.printStackTrace();
		}
	}
	
	/**
	 * Tests getInvCubieCube function
	 */
	@Test
	public void testGetInvCubieCube() {
		try (Scanner scanner = new Scanner(Paths.get(INV_CUBE_TEST_FILE))) {
			scanner.nextLine(); //read first line
			CubieCube cube = new CubieCube();
			//iterate through all cubes for test
			while(scanner.hasNextLine()) {			
				String[] line = scanner.nextLine().split("\t");
				//generate cube
				cube.cp = TwoPhaseTestUtils.parseCornerArray(line[0]);
				cube.co = TwoPhaseTestUtils.parseByteArray(line[1]);
				cube.ep = TwoPhaseTestUtils.parseEdgeArray(line[2]);
				cube.eo = TwoPhaseTestUtils.parseByteArray(line[3]);
				//generate inverse cube
				CubieCube inverseCube = cube.getInvCubieCube();
				//parse expected fields
				Corner[] expectedCornerPermutaion = TwoPhaseTestUtils.parseCornerArray(line[4]);
				byte[] expectedCornerOrientation = TwoPhaseTestUtils.parseByteArray(line[5]);
				Edge[] expectedEdgePermutaion = TwoPhaseTestUtils.parseEdgeArray(line[6]);
				byte[] expectedEdgeOrientation = TwoPhaseTestUtils.parseByteArray(line[7]);
				//assert fields
				assertArrayEquals(String.format("corner permutation array is not as expected for the inverse of cube %s", line[0]),
						expectedCornerPermutaion, inverseCube.cp);
				assertArrayEquals(String.format("corner orientation array is not as expected for the inverse of cube %s", line[0]),
						expectedCornerOrientation, inverseCube.co);
				assertArrayEquals(String.format("edge permutation array is not as expected for the inverse of cube %s", line[0]),
						expectedEdgePermutaion, inverseCube.ep);
				assertArrayEquals(String.format("edge orientation array is not as expected for the inverse of cube %s", line[0]),
						expectedEdgeOrientation, inverseCube.eo);
			}
		}
		catch (IOException e) {
			System.out.printf("Error opening test file %s %n", INV_CUBE_TEST_FILE);
			e.printStackTrace();
		}
	}
	
	/**
	 * Tests CubieCube getters (coordinates getters)
	 */
	@Test
	public void testGetters() {
		try (Scanner scanner = new Scanner(Paths.get(COORDINATES_TEST_FILE))) {
			short parity;
			CubieCube cube = new CubieCube();
			//read first line
			scanner.nextLine();
			//iterate through all cubes for test
			while(scanner.hasNextLine()) {				
				String[] line = scanner.nextLine().split("\t");
				//set corners and edges
				cube.cp = TwoPhaseTestUtils.parseCornerArray(line[9]);
				cube.co = TwoPhaseTestUtils.parseByteArray(line[10]);
				cube.ep = TwoPhaseTestUtils.parseEdgeArray(line[11]);
				cube.eo = TwoPhaseTestUtils.parseByteArray(line[12]);
				//assert getters
				assertEquals(String.format("twist is not as expected for cube %s", line[0]),
						Short.parseShort(line[1]), cube.getTwist());
				assertEquals(String.format("flip is not as expected for cube %s", line[0]),
						Short.parseShort(line[2]), cube.getFlip());
				parity = Short.parseShort(line[3]);
				assertEquals(String.format("cornerParity is not as expected for cube %s", line[0]),
						parity, cube.cornerParity());
				assertEquals(String.format("edgeParity is not as expected for cube %s", line[0]),
						parity, cube.edgeParity());
				assertEquals(String.format("FRtoBR is not as expected for cube %s", line[0]),
						Short.parseShort(line[4]), cube.getFRtoBR());
				assertEquals(String.format("URFtoDLF is not as expected for cube %s", line[0]),
						Short.parseShort(line[5]), cube.getURFtoDLF());
				assertEquals(String.format("URtoDF is not as expected for cube %s", line[0]),
						Integer.parseInt(line[6]), cube.getURtoDF());
				assertEquals(String.format("URtoUL is not as expected for cube %s", line[0]),
						Short.parseShort(line[7]), cube.getURtoUL());
				assertEquals(String.format("UBtoDF is not as expected for cube %s", line[0]),
						Short.parseShort(line[8]), cube.getUBtoDF());
			}
		} catch (IOException e) {
			System.out.printf("Error opening test file %s %n", COORDINATES_TEST_FILE);
			e.printStackTrace();
		}
	}
	
	/**
	 * Tests CubieCube setters (coordinates setters)
	 */
	@Test
	public void testSetters() {
		try (Scanner scanner = new Scanner(Paths.get(COORDINATES_TEST_FILE))) {
			//create empty CubieCube
			CubieCube cube = new CubieCube();
			short param;
			int i, paramInt;
			//read first line
			scanner.nextLine();
			//iterate through all cubes for test
			while(scanner.hasNextLine()) {
				String[] line = scanner.nextLine().split("\t");
				//parse expected results
				Corner[] expectedCornerPermutaion = TwoPhaseTestUtils.parseCornerArray(line[9]);
				byte[] expectedCornerOrientation = TwoPhaseTestUtils.parseByteArray(line[10]);
				Edge[] expectedEdgePermutaion = TwoPhaseTestUtils.parseEdgeArray(line[11]);
				byte[] expectedEdgeOrientation = TwoPhaseTestUtils.parseByteArray(line[12]);
				//test twist setter
				param = Short.parseShort(line[1]);
				cube.setTwist(param);
				assertArrayEquals(String.format("corner orientation array is not as expected after twist %d", param),
						expectedCornerOrientation, cube.co);
				//test flip setter
				param = Short.parseShort(line[2]);
				cube.setFlip(param);
				assertArrayEquals(String.format("edge orientation array is not as expected after flip %d", param),
						expectedEdgeOrientation, cube.eo);
				//test FRtoBR setter
				param = Short.parseShort(line[4]);
				cube.setFRtoBR(param);
				//find indexes of FR,FL,BL and BR edges in the expected edge permutation array and compare to actual array
				for(i = 0; i < 12; i++) {
					if(expectedEdgePermutaion[i] == Edge.FR || expectedEdgePermutaion[i] == Edge.FL ||
							expectedEdgePermutaion[i] == Edge.BL || expectedEdgePermutaion[i] == Edge.BR) {
						assertEquals(String.format("edge %s location is not as expected after setting FRtoBR to %d", expectedEdgePermutaion[i], param),
								cube.ep[i], expectedEdgePermutaion[i]);
					}
				}
				//test URFtoDLF setter
				param = Short.parseShort(line[5]);
				cube.setURFtoDLF(param);
				//find indexes of URF, UFL, ULB, UBR, DFR and DLF corners in the expected corner permutation array and compare to actual array
				for(i = 0; i < 8; i++) {
					if(expectedCornerPermutaion[i] != Corner.DBL && expectedCornerPermutaion[i] != Corner.DRB) {
						assertEquals(String.format("corner %s location is not as expected after setting URFtoDLF to %d", expectedCornerPermutaion[i], param),
								cube.cp[i], expectedCornerPermutaion[i]);
					}
				}
				//test URtoDF setter
				paramInt = Integer.parseInt(line[6]);
				cube.setURtoDF(paramInt);
				//find indexes of UR,UF,UL,UB,DR and DF edges in the expected edge permutation array and compare to actual array
				for(i = 0; i < 12; i++) {
					if(expectedEdgePermutaion[i] == Edge.UR || expectedEdgePermutaion[i] == Edge.UF ||
							expectedEdgePermutaion[i] == Edge.UL || expectedEdgePermutaion[i] == Edge.UB ||
							expectedEdgePermutaion[i] == Edge.DR || expectedEdgePermutaion[i] == Edge.DF) {
						assertEquals(String.format("edge %s location is not as expected after setting URtoDF to %d", expectedEdgePermutaion[i], paramInt),
								cube.ep[i], expectedEdgePermutaion[i]);
					}
				}
				//test URtoUL setter
				param = Short.parseShort(line[7]);
				cube.setURtoUL(param);
				//find indexes of UR,UF and UL edges in the expected edge permutation array and compare to actual array
				for(i = 0; i < 12; i++) {
					if(expectedEdgePermutaion[i] == Edge.UR || expectedEdgePermutaion[i] == Edge.UF ||
							expectedEdgePermutaion[i] == Edge.UL) {
						assertEquals(String.format("edge %s location is not as expected after setting URtoUL to %d", expectedEdgePermutaion[i], param),
								cube.ep[i], expectedEdgePermutaion[i]);
					}
				}
				//test UBtoDF setter
				param = Short.parseShort(line[8]);
				cube.setUBtoDF(param);
				//find indexes of UB, DR and DF edges in the expected edge permutation array and compare to actual array
				for(i = 0; i < 12; i++) {
					if(expectedEdgePermutaion[i] == Edge.UB || expectedEdgePermutaion[i] == Edge.DR ||
							expectedEdgePermutaion[i] == Edge.DF) {
						assertEquals(String.format("edge %s location is not as expected after setting UBtoDF to %d", expectedEdgePermutaion[i], param),
								cube.ep[i], expectedEdgePermutaion[i]);
					}
				}
			}
		} catch (IOException e) {
			System.out.printf("Error opening test file %s %n", COORDINATES_TEST_FILE);
			e.printStackTrace();
		}
	}
	
	/**
	 * Tests verify function
	 */
	@Test
	public void testVerify() {
		try (Scanner scanner = new Scanner(Paths.get(VERIFY_TEST_FILE))) {
			int errorCode;
			//test valid cubes
			scanner.nextLine(); //read first line
			String cubeString;
			//check if reached next section
			while(!(cubeString = scanner.nextLine()).startsWith("--")) {
				//create cube
				CubieCube cube = new FaceCube(cubeString).toCubieCube();
				//verify cube
				errorCode = cube.verify();
				assertEquals(String.format("Error code should be 0 (Cube is solvable) for cube %s", cubeString),
						0, errorCode);
			}
			
			//test cubes that not all edges exists exactly ones
			while(!(cubeString = scanner.nextLine()).startsWith("--")) {
				//create cube
				CubieCube cube = new FaceCube(cubeString).toCubieCube();
				//verify cube
				errorCode = cube.verify();
				assertEquals(String.format("Error code should be -2 (Not all 12 edges exist exactly once) for cube %s", cubeString),
						-2, errorCode);
			}
			
			//test cubes that has a flippedd edge
			while(!(cubeString = scanner.nextLine()).startsWith("--")) {
				//create cube
				CubieCube cube = new FaceCube(cubeString).toCubieCube();
				//verify cube
				errorCode = cube.verify();
				assertEquals(String.format("Error code should be -3 (Flip error: One edge has to be flipped) for cube %s", cubeString),
						-3, errorCode);
			}
			
			//test cubes that not all corners exists exactly ones
			while(!(cubeString = scanner.nextLine()).startsWith("--")) {
				//create cube
				CubieCube cube = new FaceCube(cubeString).toCubieCube();
				//verify cube
				errorCode = cube.verify();
				assertEquals(String.format("Error code should be -4 (Not all corners exist exactly once) for cube %s", cubeString),
						-4, errorCode);
			}
			
			//test cubes that has a twisted corner
			while(!(cubeString = scanner.nextLine()).startsWith("--")) {
				//create cube
				CubieCube cube = new FaceCube(cubeString).toCubieCube();
				//verify cube
				errorCode = cube.verify();
				assertEquals(String.format("Error code should be -5 (twisted corner) for cube %s", cubeString),
						-5, errorCode);
			}
			
			//test cubes that two corners or two edges exchanged
			while(!(cubeString = scanner.nextLine()).startsWith("--")) {
				//create cube
				CubieCube cube = new FaceCube(cubeString).toCubieCube();
				//verify cube
				errorCode = cube.verify();
				assertEquals(String.format("Error code  should be -6 (exchanged two corners or two edges) for cube %s", cubeString),
						-6, errorCode);
			}
		} catch (IOException e) {
			System.out.printf("Error opening test file %s %n", VERIFY_TEST_FILE);
			e.printStackTrace();
		}
	}
}
