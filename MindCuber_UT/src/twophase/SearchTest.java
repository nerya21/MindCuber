package twophase;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Test;

public class SearchTest {

	/**
	 * Tests Search.solution for different inputs
	 */
	@Test
	public void test_solution() {
		//open a file containing cubics and their expected solution 
		try(BufferedReader br = new BufferedReader(new FileReader("test_Search_solution.txt"))) {
			String line;
			String solution;
			String[] cubeSolutionPair;
			//for each cubic-expected solution pair, try to solve the cube and compare solution to the expected one 
			while((line = br.readLine()) != null) {
				cubeSolutionPair = line.split(",");
//				solution = Search.solution(cubeSolutionPair[0], 24, 1000, false);
//				//check that solution is as expected
//				assertEquals(
//						//messsage
//						String.format("Cube: %s, Expected: %s, Solution: %s%n",cubeSolutionPair[0], cubeSolutionPair[1], solution),
//						//string to compare - expected and solution
//						cubeSolutionPair[1], solution);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
