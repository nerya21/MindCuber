package application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Random;
import java.util.Scanner;

import org.junit.Test;

import cube.Cube;
import cube.Orientation;
import robot.Colors;

public class CubeSolverTest {
	
	private static final String CUBE_TO_ALG_REP_TEST_FILE = "testsFiles/cubeToAlgRep.txt";

	/**
	 * Tests conversion from cube to algorithm representation
	 */
	@Test
	public void testCreateCubeRepForAlgorithm() {
		Cube cube = new Cube();
		try (Scanner scanner = new Scanner(Paths.get(CUBE_TO_ALG_REP_TEST_FILE))) {
			int i = 1;
			//read first line
			scanner.nextLine();
			//iterate through all cubes for test
			while(scanner.hasNextLine()) {				
				String[] params = scanner.nextLine().split("\t");
				//parse cube colors
				Colors[][] up = parseColorsMatrix(params[0]);
				Colors[][] down = parseColorsMatrix(params[1]);
				Colors[][] front = parseColorsMatrix(params[2]);
				Colors[][] back = parseColorsMatrix(params[3]);
				Colors[][] left = parseColorsMatrix(params[4]);
				Colors[][] right = parseColorsMatrix(params[5]);
				//set cube colors
				cube.setColorsManual(up, down, front, back, left, right);
				//call tested function
				String result = CubeSolver.createCubeRepForAlgorithm(cube);
				//assert result
				assertEquals(String.format("Conversion is not as expected in test no.%d", i), params[6], result);
				i++;
			}
		} catch (IOException e) {
			System.out.printf("Error opening test file %s %n", CUBE_TO_ALG_REP_TEST_FILE);
			e.printStackTrace();
		}
	}
	
	/**
	 * Tests conversion from cube to algorithm representation
	 * for both valid and invalid cubes
	 */
	@Test
	public void testCreateCubeRepForAlgorithmValidation() {
		Random gen = new Random();
		for(int i = 0; i < 100; i++) {
			Cube cube = generateCube(gen);
			//check if cube has all 6 faces
			int[] faces = new int[6];
			for(Orientation o: Orientation.values()) {
				//the middle piece defines the face
				Colors face = cube.getFace(o).getColor(1, 1);
				faces[face.getValue()]++;
			}
			boolean allFacesExists = true;
			for(int face: faces) {
				if(face != 1)
					allFacesExists = false;
			}
			//convert cube to algorithm representation
			String result = CubeSolver.createCubeRepForAlgorithm(cube);
			if(allFacesExists) {
				//createCubeRepForAlgorithm can convert cube to algorithm representation
				assertNotNull(result);
			}
			else {
				//createCubeRepForAlgorithm can't convert cube to algorithm representation
				//so is should return null
				assertNull(result);
				
			}
		}
	}
	
	/**
	 * Parse argument as a Colors array.
	 * The string should contain Colors enum values separated by a comma
	 * @param str - a String containing the Edge array representation to be parsed
	 * @return the Colors array value represented by the argument
	 */
	private static Colors[][] parseColorsMatrix(String str) {
		Colors[][] colorsMatrix = new Colors[3][3];
		String[] colors = str.split(",");
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				colorsMatrix[i][j] =  Colors.valueOf(colors[i*3+j]);
			}
		}
		return colorsMatrix;
	}
	
	/**
	 * Generates pseudo random matrix of face colors (not necessarily valid face)
	 * @param gen - initialized random number generator
	 * @return 3x3 matrix of colors
	 */
	private Colors[][] generateFaceColors(Random gen) {
		Colors[] allColors = Colors.values();
		Colors[][] colorsMatrix = new Colors[3][3];
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				colorsMatrix[i][j] =  allColors[gen.nextInt(6)];
			}
		}
		return colorsMatrix;
	}
	
	/**
	 * Generates pseudo random cube (not necessarily valid one)
	 * @param gen - initialized random number generator
	 * @return generated cube
	 */
	private Cube generateCube(Random gen) {
		Cube cube  = new Cube();
		//generate faces colors
		Colors[][] up = generateFaceColors(gen);
		Colors[][] down = generateFaceColors(gen);
		Colors[][] front = generateFaceColors(gen);
		Colors[][] back = generateFaceColors(gen);
		Colors[][] left = generateFaceColors(gen);
		Colors[][] right = generateFaceColors(gen);
		//set cube colors
		cube.setColorsManual(up, down, front, back, left, right);
		return cube;
	}
}
