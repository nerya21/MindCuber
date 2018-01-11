package twophase;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

import org.junit.Test;

public class CoordCubeTest {

	@Test
	public void test_statics() throws FileNotFoundException, IOException {
		try (Scanner scanner = new Scanner(Paths.get("testsFiles/test_matrix.txt"))) {
			String[] line = scanner.nextLine().split(",");
			int i =0;
			for (short[] array : CoordCube.twistMove)
				for (short s : array){
					assertEquals(String.format("twistMove at %d is not as expected, expected: %s, actual: %d", i, line[i], s),
							Short.parseShort(line[i]), s);
					i++;
				}
			
			line = scanner.nextLine().split(",");
			i =0;
			for (short[] array : CoordCube.flipMove)
				for (short s : array){
					assertEquals(String.format("flipMove at %d is not as expected, expected: %s, actual: %d", i, line[i], s),
							Short.parseShort(line[i]), s);
					i++;
				}
			
			line = scanner.nextLine().split(",");
			i =0;
			for (short[] array : CoordCube.FRtoBR_Move)
				for (short s : array){
					assertEquals(String.format("FRtoBR_Move at %d is not as expected, expected: %s, actual: %d", i, line[i], s),
							Short.parseShort(line[i]), s);
					i++;
				}
			
			line = scanner.nextLine().split(",");
			i =0;
			for (short[] array : CoordCube.URFtoDLF_Move)
				for (short s : array){
					assertEquals(String.format("URFtoDLF_Move at %d is not as expected, expected: %s, actual: %d", i, line[i], s),
							Short.parseShort(line[i]), s);
					i++;
				}
			
			line = scanner.nextLine().split(",");
			i =0;
			for (short[] array : CoordCube.URtoDF_Move)
				for (short s : array){
					assertEquals(String.format("URtoDF_Move at %d is not as expected, expected: %s, actual: %d", i, line[i], s),
							Short.parseShort(line[i]), s);
					i++;
				}
			
			line = scanner.nextLine().split(",");
			i =0;
			for (short[] array : CoordCube.URtoUL_Move)
				for (short s : array){
					assertEquals(String.format("URtoUL_Move at %d is not as expected, expected: %s, actual: %d", i, line[i], s),
							Short.parseShort(line[i]), s);
					i++;
				}

			line = scanner.nextLine().split(",");
			i =0;
			for (short[] array : CoordCube.UBtoDF_Move)
				for (short s : array){
					assertEquals(String.format("UBtoDF_Move at %d is not as expected, expected: %s, actual: %d", i, line[i], s),
							Short.parseShort(line[i]), s);
					i++;
				}

			line = scanner.nextLine().split(",");
			i =0;
			for (short[] array : CoordCube.MergeURtoULandUBtoDF)
				for (short s : array){
					assertEquals(String.format("MergeURtoULandUBtoDF at %d is not as expected, expected: %s, actual: %d", i, line[i], s),
							Short.parseShort(line[i]), s);
					i++;
				}

			line = scanner.nextLine().split(",");
			i =0;
			for (byte b : CoordCube.Slice_URFtoDLF_Parity_Prun){
				assertEquals(String.format("Slice_URFtoDLF_Parity_Prun at %d is not as expected, expected: %s, actual: %d", i, line[i], b),
						Short.parseShort(line[i]), (short)b);
				i++;
			}

			line = scanner.nextLine().split(",");
			i =0;
			for (byte b : CoordCube.Slice_URtoDF_Parity_Prun){
				assertEquals(String.format("Slice_URtoDF_Parity_Prun at %d is not as expected, expected: %s, actual: %d", i, line[i], b),
						Short.parseShort(line[i]), (short)b);
				i++;
			}
			
			line = scanner.nextLine().split(",");
			i =0;
			for (byte b : CoordCube.Slice_Twist_Prun){
				assertEquals(String.format("Slice_Twist_Prun at %d is not as expected, expected: %s, actual: %d", i, line[i], b),
						Short.parseShort(line[i]), (short)b);
				i++;
			}
			
			line = scanner.nextLine().split(",");
			i =0;
			for (byte b : CoordCube.Slice_Flip_Prun){
				assertEquals(String.format("Slice_Flip_Prun at %d is not as expected, expected: %s, actual: %d", i, line[i], b),
						Short.parseShort(line[i]), (short)b);
				i++;
			}
		}
	}

}
