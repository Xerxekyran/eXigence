package jUnitTesting;

import jUnitTesting.testCases.Vector2DTests;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class MainTest extends TestCase
{
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		junit.textui.TestRunner.run(startTests());

	}
	
	public static Test startTests()
	{
		TestSuite suite = new TestSuite("All eXigence! Tests");
		
		// Alle Untertestklasssen anmelden
		suite.addTestSuite(Vector2DTests.class);
		
		return suite;
	}

}

