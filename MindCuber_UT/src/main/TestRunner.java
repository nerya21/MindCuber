package main;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import twophase.CubieCubeTest;
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
		System.out.println(result.wasSuccessful());
		
		System.out.println("Running Search class tests");
		result = JUnitCore.runClasses(SearchTest.class);
		for (Failure failure : result.getFailures()) {
			System.out.println(failure.toString());
		}
		System.out.println(result.wasSuccessful());
		
		System.out.println("Finished...");
	}
}
