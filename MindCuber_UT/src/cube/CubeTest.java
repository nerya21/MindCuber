package cube;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import robot.Direction;
import robot.NxtCommand;

public class CubeTest {

	/**
	 * Tests Cube constructor
	 */
	@Test
	public void testCube() {
		try {
			Cube c = new Cube();
		}
		catch (Exception e) {
			System.out.printf("Error creating cube %s %n");
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Tests Cube.updateOrientations function
	 */
	@Test
	public void testUpdateOrientations() {
		try {
			//case 1- DOWN becomes down
			Cube c = new Cube();
			c.updateOrientations(Orientation.D);
			if (c.getFace(Orientation.D).dynamicOrientation != Orientation.D)
				throw new Exception();
			if (c.getFace(Orientation.R).dynamicOrientation != Orientation.R)
				throw new Exception();
			if (c.getFace(Orientation.L).dynamicOrientation != Orientation.L)
				throw new Exception();
			if (c.getFace(Orientation.B).dynamicOrientation != Orientation.B)
				throw new Exception();
			if (c.getFace(Orientation.F).dynamicOrientation != Orientation.F)
				throw new Exception();
			if (c.getFace(Orientation.U).dynamicOrientation != Orientation.U)
				throw new Exception();
			
			//case 2 - UP becomes down
			c = new Cube();
			c.updateOrientations(Orientation.U);
			if (c.getFace(Orientation.D).dynamicOrientation != Orientation.U)
				throw new Exception();
			if (c.getFace(Orientation.R).dynamicOrientation != Orientation.L)
				throw new Exception();
			if (c.getFace(Orientation.L).dynamicOrientation != Orientation.R)
				throw new Exception();
			if (c.getFace(Orientation.B).dynamicOrientation != Orientation.B)
				throw new Exception();
			if (c.getFace(Orientation.F).dynamicOrientation != Orientation.F)
				throw new Exception();
			if (c.getFace(Orientation.U).dynamicOrientation != Orientation.D)
				throw new Exception();
			
			//case 3 - FRONT becomes down
			c = new Cube();
			c.updateOrientations(Orientation.F);
			if (c.getFace(Orientation.R).dynamicOrientation != Orientation.F)
				throw new Exception();
			if (c.getFace(Orientation.D).dynamicOrientation != Orientation.R)
				throw new Exception();
			if (c.getFace(Orientation.L).dynamicOrientation != Orientation.B)
				throw new Exception();
			if (c.getFace(Orientation.B).dynamicOrientation != Orientation.U)
				throw new Exception();
			if (c.getFace(Orientation.F).dynamicOrientation != Orientation.D)
				throw new Exception();
			if (c.getFace(Orientation.U).dynamicOrientation != Orientation.L)
				throw new Exception();
			
			//case 4 - BACK becomes down
			c = new Cube();
			c.updateOrientations(Orientation.B);
			if (c.getFace(Orientation.R).dynamicOrientation != Orientation.B)
				throw new Exception();
			if (c.getFace(Orientation.D).dynamicOrientation != Orientation.R)
				throw new Exception();
			if (c.getFace(Orientation.L).dynamicOrientation != Orientation.F)
				throw new Exception();
			if (c.getFace(Orientation.B).dynamicOrientation != Orientation.D)
				throw new Exception();
			if (c.getFace(Orientation.F).dynamicOrientation != Orientation.U)
				throw new Exception();
			if (c.getFace(Orientation.U).dynamicOrientation != Orientation.L)
				throw new Exception();
			
			//case 5 - LEFT becomes down
			c = new Cube();
			c.updateOrientations(Orientation.L);
			if (c.getFace(Orientation.R).dynamicOrientation != Orientation.U)
				throw new Exception();
			if (c.getFace(Orientation.D).dynamicOrientation != Orientation.R)
				throw new Exception();
			if (c.getFace(Orientation.L).dynamicOrientation != Orientation.D)
				throw new Exception();
			if (c.getFace(Orientation.B).dynamicOrientation != Orientation.B)
				throw new Exception();
			if (c.getFace(Orientation.F).dynamicOrientation != Orientation.F)
				throw new Exception();
			if (c.getFace(Orientation.U).dynamicOrientation != Orientation.L)
				throw new Exception();
			
			//case 6 - RIGHT becomes down
			c = new Cube();
			c.updateOrientations(Orientation.R);
			if (c.getFace(Orientation.R).dynamicOrientation != Orientation.D)
				throw new Exception();
			if (c.getFace(Orientation.D).dynamicOrientation != Orientation.R)
				throw new Exception();
			if (c.getFace(Orientation.L).dynamicOrientation != Orientation.U)
				throw new Exception();
			if (c.getFace(Orientation.B).dynamicOrientation != Orientation.F)
				throw new Exception();
			if (c.getFace(Orientation.F).dynamicOrientation != Orientation.B)
				throw new Exception();
			if (c.getFace(Orientation.U).dynamicOrientation != Orientation.L)
				throw new Exception();
			
			
		}
		catch (Exception e) {
			System.out.printf("Error in updateOrientations %s %n");
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Tests Cube.Face.turn function
	 */
	@Test
	public void testTurn() {
		try {
			NxtCommand.initMock();
			
			//DOWN face
			Cube c = new Cube();
			c.getFace(Orientation.D).turn(Direction.RIGHT);
			if (c.getFace(Orientation.D).dynamicOrientation != Orientation.D)
				throw new Exception();
			c.getFace(Orientation.D).turn(Direction.LEFT);
			if (c.getFace(Orientation.D).dynamicOrientation != Orientation.D)
				throw new Exception();
			c.getFace(Orientation.D).turn(Direction.MIRROR);
			if (c.getFace(Orientation.D).dynamicOrientation != Orientation.D)
				throw new Exception();
			c.getFace(Orientation.D).turn(Direction.NONE);
			if (c.getFace(Orientation.D).dynamicOrientation != Orientation.D)
				throw new Exception();
			
			//UP face
			c.getFace(Orientation.U).turn(Direction.RIGHT);
			if (c.getFace(Orientation.U).dynamicOrientation != Orientation.D)
				throw new Exception();
			
		}
		catch (Exception e) {
			System.out.printf("Error in Cube.Face.turn %s %n");
			e.printStackTrace();
		}
		
	}
}
	
