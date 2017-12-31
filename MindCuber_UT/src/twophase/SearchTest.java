package twophase;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.junit.Test;

import robot.Direction;

public class SearchTest {

	private static final String FIND_SOL_TEST_FILE = TwoPhaseTestUtils.TEST_FILES_DIR + "/cubesSolution.txt";
	private static final String FIND_SOL_VALIDATE_TEST_FILE = TwoPhaseTestUtils.TEST_FILES_DIR + "/verifyCubes.txt";
	
	/**
	 * Creates a string representing found solution
	 * @param solution - a list of moves representing solution
	 * @return a string that represents the solution
	 */
	private static String generateSolutionString(List<Move> solution) {
		StringBuilder sb = new StringBuilder();
		for(Move move : solution) {
			sb.append(move.orientation.toString());
			if(move.direction == Direction.LEFT) {
				sb.append("'");
			}
			else if(move.direction == Direction.MIRROR) {
				sb.append("2");
			}
			sb.append(" ");
		}
		return sb.toString();
	}
	
	/**
	 * Tests TwoPhase.findSolution function for different inputs
	 */
	@Test
	public void testFindSolution() {
		try (Scanner scanner = new Scanner(Paths.get(FIND_SOL_TEST_FILE))) {
			List<Move> solution = new ArrayList<>();
			int i = 1;
			int errorCode;
			//read first line
			scanner.nextLine();
			//iterate through all cubes for test
			while(scanner.hasNextLine()) {				
				String[] params = scanner.nextLine().split("\t");
				//create facelets array
				Color[] facelets = TwoPhaseTestUtils.parseColorsArray(params[0]);
				//call tested function
				errorCode = TwoPhase.findSolution(facelets, 24, 1000, solution);
				//translate solution to string
				String solutionString = generateSolutionString(solution);
				//assert result
				assertEquals(String.format("Solution is not as expected in test no.%d", i), params[1], solutionString);
				//assert that returned value is 0
				assertEquals(String.format("Error code should be 0 in test no.%d", i), 0, errorCode);
				i++;
			}
		} catch (IOException e) {
			System.out.printf("Error opening test file %s %n", FIND_SOL_TEST_FILE);
			e.printStackTrace();
		}
	}
	
	/**
	 * Tests TwoPhase.findSolution function for invalid inputs, and checks returned error code 
	 */
	@Test
	public void testFindSolutionValidation() {
		try (Scanner scanner = new Scanner(Paths.get(FIND_SOL_VALIDATE_TEST_FILE))) {
			List<Move> solution = new ArrayList<>();
			int errorCode;
			//test valid cubes
			scanner.nextLine(); //read first line
			String cubeString;
			//check if reached next section
			while(!(cubeString = scanner.nextLine()).startsWith("--")) {
				//create facelets array
				Color[] facelets = TwoPhaseTestUtils.parseColorsArray(cubeString);
				//call tested function
				errorCode = TwoPhase.findSolution(facelets, 24, 1000, solution);
				assertEquals(String.format("Error code should be 0 for cube %s", cubeString),
						0, errorCode);
			}
			
			//test cubes that not all edges exists exactly ones
			while(!(cubeString = scanner.nextLine()).startsWith("--")) {
				//create facelets array
				Color[] facelets = TwoPhaseTestUtils.parseColorsArray(cubeString);
				//call tested function
				errorCode = TwoPhase.findSolution(facelets, 24, 1000, solution);
				assertEquals(String.format("Error code should be -2 (Not all 12 edges exist exactly once) for cube %s", cubeString),
						-2, errorCode);
			}
			
			//test cubes that has a flippedd edge
			while(!(cubeString = scanner.nextLine()).startsWith("--")) {
				//create facelets array
				Color[] facelets = TwoPhaseTestUtils.parseColorsArray(cubeString);
				//call tested function
				errorCode = TwoPhase.findSolution(facelets, 24, 1000, solution);
				assertEquals(String.format("Error code should be -3 (Flip error: One edge has to be flipped) for cube %s", cubeString),
						-3, errorCode);
			}
			
			//test cubes that not all corners exists exactly ones
			while(!(cubeString = scanner.nextLine()).startsWith("--")) {
				//create facelets array
				Color[] facelets = TwoPhaseTestUtils.parseColorsArray(cubeString);
				//call tested function
				errorCode = TwoPhase.findSolution(facelets, 24, 1000, solution);
				assertEquals(String.format("Error code should be -4 (Not all corners exist exactly once) for cube %s", cubeString),
						-4, errorCode);
			}
			
			//test cubes that has a twisted corner
			while(!(cubeString = scanner.nextLine()).startsWith("--")) {
				//create facelets array
				Color[] facelets = TwoPhaseTestUtils.parseColorsArray(cubeString);
				//call tested function
				errorCode = TwoPhase.findSolution(facelets, 24, 1000, solution);
				assertEquals(String.format("Error code should be -5 (twisted corner) for cube %s", cubeString),
						-5, errorCode);
			}
			
			//test cubes that two corners or two edges exchanged
			while(!(cubeString = scanner.nextLine()).startsWith("--")) {
				//create facelets array
				Color[] facelets = TwoPhaseTestUtils.parseColorsArray(cubeString);
				//call tested function
				errorCode = TwoPhase.findSolution(facelets, 24, 1000, solution);
				assertEquals(String.format("Error code  should be -6 (exchanged two corners or two edges) for cube %s", cubeString),
						-6, errorCode);
			}
			
			//test cubes that don't have exactly one facelet of each color
			while(!(cubeString = scanner.nextLine()).startsWith("--")) {
				//create facelets array
				Color[] facelets = TwoPhaseTestUtils.parseColorsArray(cubeString);
				//call tested function
				errorCode = TwoPhase.findSolution(facelets, 24, 1000, solution);
				assertEquals(String.format("Error code  should be -1 (There is not exactly one facelet of each color) for cube %s", cubeString),
						-1, errorCode);
			}
			
			//test max depth limitation
			Color[] facelets = TwoPhaseTestUtils.parseColorsArray("RLRUURDLFUFDBRLFRBLFRFFDDLUFULRDUDFLUBFRLULBRBDBBBDUDB");
			errorCode = TwoPhase.findSolution(facelets, 10, 1000, solution);
			assertEquals("Error code  should be -7 (No solution exists for the given maxDepth)",
					-7, errorCode);
			
		} catch (IOException e) {
			System.out.printf("Error opening test file %s %n", FIND_SOL_VALIDATE_TEST_FILE);
			e.printStackTrace();
		}
	}
}
