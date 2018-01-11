package main;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import application.CubeSolverTest;
import cube.CubeTest;
import twophase.CoordCubeTest;
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
		System.out.println("Running CoordCube class tests");
		result = JUnitCore.runClasses(CoordCubeTest.class);
		for (Failure failure : result.getFailures()) {
			System.out.println(failure.toString());
		}
		if(result.wasSuccessful()) {
			System.out.println("CoordCube class tests finished successfully");
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
		System.out.println("Running Cube class tests");
		if (CubeTest.cubeTestRun()) {
			System.out.println("Cube class tests finished successfully");
		}
		
		System.out.println("Running CubeSolverTest class tests");
		result = JUnitCore.runClasses(CubeSolverTest.class);
		for (Failure failure : result.getFailures()) {
			System.out.println(failure.toString());
		}
		if(result.wasSuccessful()) {
			System.out.println("CubeSolverTest class tests finished successfully");
		}

		System.out.println("Finished...");
	}
}
