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
	private static final String FIND_SOL_PATTERN_TEST_FILE = TwoPhaseTestUtils.TEST_FILES_DIR + "/cubesSolutionWithPattern.txt";
	private static final String FIND_SOL_VALIDATE_TEST_FILE = TwoPhaseTestUtils.TEST_FILES_DIR + "/verifyCubes.txt";
	private static final String SOLVABLE_CUBE = "RLRUURDLFUFDBRLFRBLFRFFDDLUFULRDUDFLUBFRLULBRBDBBBDUDB";
	
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
				//call tested function
				errorCode = TwoPhase.findSolution(params[0], 24, 1000, solution);
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
	 * Tests TwoPhase.findSolution function with pattern option for different inputs
	 */
	@Test
	public void testFindSolutionWithPattern() {
		try (Scanner scanner = new Scanner(Paths.get(FIND_SOL_PATTERN_TEST_FILE))) {
			List<Move> solution = new ArrayList<>();
			int i = 1;
			int errorCode;
			//read first line
			scanner.nextLine();
			//iterate through all cubes for test
			while(scanner.hasNextLine()) {				
				String[] params = scanner.nextLine().split("\t");
				//call tested function
				errorCode = TwoPhase.findSolution(params[0], 24, 1000, solution, params[1]);
				//translate solution to string
				String solutionString = generateSolutionString(solution);
				//assert result
				assertEquals(String.format("Solution is not as expected in pattern test no.%d", i), params[2], solutionString);
				//assert that returned value is 0
				assertEquals(String.format("Error code should be 0 in pattern test no.%d", i), 0, errorCode);
				i++;
			}
		} catch (IOException e) {
			System.out.printf("Error opening test file %s %n", FIND_SOL_PATTERN_TEST_FILE);
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
				testValidationForSpecificCode(0, "Valid", cubeString, solution);
			}
			
			//test cubes that not all edges exists exactly ones
			while(!(cubeString = scanner.nextLine()).startsWith("--")) {
				testValidationForSpecificCode(-2, "Not all 12 edges exist exactly once", cubeString, solution);
			}
			
			//test cubes that has a flippedd edge
			while(!(cubeString = scanner.nextLine()).startsWith("--")) {
				testValidationForSpecificCode(-3, "Flip error: One edge has to be flipped", cubeString, solution);
			}
			
			//test cubes that not all corners exists exactly ones
			while(!(cubeString = scanner.nextLine()).startsWith("--")) {
				testValidationForSpecificCode(-4, "Not all corners exist exactly once", cubeString, solution);
			}
			
			//test cubes that has a twisted corner
			while(!(cubeString = scanner.nextLine()).startsWith("--")) {
				testValidationForSpecificCode(-5, "Twist error: One corner has to be twisted", cubeString, solution);
			}
			
			//test cubes that two corners or two edges exchanged
			while(!(cubeString = scanner.nextLine()).startsWith("--")) {
				testValidationForSpecificCode(-6, "Parity error: Two corners or two edges have to be exchanged in pattern", cubeString, solution);
			}
			
			//test cubes that don't have exactly one facelet of each color
			while(!(cubeString = scanner.nextLine()).startsWith("--")) {
				testValidationForSpecificCode(-1, "There are not exactly 9 facelets of each color", cubeString, solution);
			}
			
			//test max depth limitation
			errorCode = TwoPhase.findSolution(SOLVABLE_CUBE, 10, 1000, solution);
			assertEquals("Error code  should be -7 (No solution exists for the given maxDepth)",
					-7, errorCode);
			
		} catch (IOException e) {
			System.out.printf("Error opening test file %s %n", FIND_SOL_VALIDATE_TEST_FILE);
			e.printStackTrace();
		}
	}
	
	/**
	 * Tests TwoPhase.findSolution function for a specific input (both as cube and pattern),
	 * and checks that returned error code is like expectedErrorCode
	 * @param expectedErrorCode - the expected error code (non positive)
	 * @param errorMeaning - a string meaning of the expected error code
	 * @param cubeString - the input. will be checked both as cube and as pattern
	 * @param solution - a list to contain suggested solution
	 */
	private void testValidationForSpecificCode(int expectedErrorCode, String errorMeaning, String cubeString,
			List<Move> solution) {
		//test cube validation
		int errorCode = TwoPhase.findSolution(cubeString, 24, 1000, solution);
		assertEquals(String.format("Error code should be %d (%s) for cube %s", expectedErrorCode, errorMeaning, cubeString),
				expectedErrorCode, errorCode);
		//test pattern validation
		errorCode = TwoPhase.findSolution(SOLVABLE_CUBE, 24, 1000, solution, cubeString);
		assertEquals(String.format("Error code should be %d (%s) for pattern %s", Math.abs(expectedErrorCode), errorMeaning, cubeString),
				Math.abs(expectedErrorCode), errorCode);
	}
}
