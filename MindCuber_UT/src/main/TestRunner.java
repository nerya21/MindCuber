package main;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import cube.CubeTest;
import twophase.CubieCubeTest;
import twophase.FaceCubeTest;
import twophase.SearchTest;

public class TestRunner {

	
	/**
	 * Tests' main routine
	 */
	public static void main(String[] args) {
		System.out.println("Running CubieCube class tests");
		Result result = JUnitCore.runClasses(CubieCubeTest.class);
		for (Failure failure : result.getFailures()) {
			System.out.println(failure.toString());
		}
		if(result.wasSuccessful()) {
			System.out.println("CubieCube class tests finished successfully");
		}
		System.out.println("Running FaceCube class tests");
		result = JUnitCore.runClasses(FaceCubeTest.class);
		for (Failure failure : result.getFailures()) {
			System.out.println(failure.toString());
		}
		if(result.wasSuccessful()) {
			System.out.println("FaceCube class tests finished successfully");
		}
		
		System.out.println("Running Search class tests");
		result = JUnitCore.runClasses(SearchTest.class);
		for (Failure failure : result.getFailures()) {
			System.out.println(failure.toString());
		}
		if(result.wasSuccessful()) {
			System.out.println("Search class tests finished successfully");
		}
		
		//cube
		CubeTest ct = new CubeTest();
		ct.testCube();
		ct.testUpdateOrientations();
		ct.testTurn();
		System.out.println("Cube class tests finished successfully");
		
		System.out.println("Finished...");
	}
}
