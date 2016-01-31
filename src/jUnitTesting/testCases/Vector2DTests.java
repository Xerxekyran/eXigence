package jUnitTesting.testCases;

import eXigence.util.math.Vector2D;
import junit.framework.TestCase;

public class Vector2DTests extends TestCase
{
	Vector2D a, b;
	Double n;
	
	@Override
	protected void setUp() throws Exception
	{
		a = new Vector2D(7.5, 8.6);
		b = new Vector2D(12.0, 7.6);
		n = 2.3;
	}
	
	/**
	 * Addition von Vektoren überprüfen
	 */
	public void testVectorAddition() 
	{		
		Vector2D result = a.add(b);
		
		assertEquals(19.5, result.getX());
		assertEquals(16.2, result.getY());
	}
	
	/**
	 * Subtraktion von Vektoren überprüfen
	 */
	public void testVectorSubtraction()
	{
		Vector2D result = a.sub(b);
		
		assertEquals(-4.5, result.getX());
		assertEquals(1.0, result.getY());
	}
	
	/**
	 * Multiplikation von Vektoren überprüfen
	 */
	public void testVectorMul()
	{
		Vector2D result = a.mul(n);
				
		assertEquals(17.25, result.getX(), 0.000001);
		assertEquals(19.78, result.getY(), 0.000001);
	}
	
	/**
	 * Division von Vektoren überprüfen
	 */
	public void testVectorDiv()
	{	
		Vector2D result = a.divide(n);
		assertEquals(3.2608695, result.getX(), 0.000001);
		assertEquals(3.7391304, result.getY(), 0.000001);
	}
	
	/**
	 * Punktprodukt von Vektoren überprüfen
	 */
	public void testVectorDot()
	{
		Double result = a.dot(b);
		
		assertEquals(155.36, result, 0.000001);
	}
	
	
}
