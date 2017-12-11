import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import twophase.Move;
import cube.Orientation;
import cube.Cube;
import cube.ICube;
import cube.IFace;
import robot.Robot;
import robot.Colors;
import twophase.Color;
import twophase.TwoPhase;

public class RubiksCubeSolver {

	public static void main(String[] args) {
		Robot.init();
		ICube cube = new Cube();
		cube.setColors();
		
		//map colors to face colors
		IFace face;
		Map<Colors, Color> colors2FaceColors = new HashMap<Colors, Color>();
		for(Orientation orientation: Orientation.values()) {
			face = cube.getFace(orientation);
			Colors color = face.getColor(1, 1);
			Color faceColor = convertOrientation2FaceColor(orientation);
			colors2FaceColors.put(color, faceColor);
		}
		
		//build cube representation for the algorithm
		Color[] facelets = new Color[53];
		Colors realColor;
		Orientation orientation;
		int i,j,c = 0;
		for(Color faceColor : Color.values()) {
			orientation = convertFaceColor2Orientation(faceColor);
			face = cube.getFace(orientation);
			for (i = 0; i < 3; i++) {
				for (j = 0; j < 3; j++) {
					realColor = face.getColor(i, j);
					facelets[c++] = colors2FaceColors.get(realColor);
				}
			}
		}
		
		List<Move> moves = new ArrayList<>();
		int depth = 24;
		int status;
		do {
			status = TwoPhase.findSolution(facelets, depth, 120, moves);
			depth = moves.size() - 1;
		} while (status == 0);
		
		if(moves.size() != 0) {
			handleSolution(cube, moves);
		}
		else {
			//handle error
		}
				
	}
	
	private static void handleSolution(ICube cube, List<Move> moves) {
		for(Move move: moves) {
			cube.getFace(move.orientation).turn(move.direction);
		}
	}
	
	private static Color convertOrientation2FaceColor(Orientation orientation) {
		switch (orientation) {
		case B:
			return Color.B;
		case D:
			return Color.D;
		case F:
			return Color.F;
		case L:
			return Color.L;
		case R:
			return Color.R;
		case U:
			return Color.U;
		default:
			return null;
		}
	}
	
	private static Orientation convertFaceColor2Orientation(twophase.Color faceColor) {
		switch (faceColor) {
		case B:
			return Orientation.B;
		case D:
			return Orientation.D;
		case F:
			return Orientation.F;
		case L:
			return Orientation.L;
		case R:
			return Orientation.R;
		case U:
			return Orientation.U;
		default:
			return null;
		}
	}

}
