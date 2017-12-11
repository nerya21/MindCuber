package twophase;

import java.util.List;

import application.Logger;
import application.LoggerGroup;
import application.LoggerLevel;
import robot.Direction;
import cube.Orientation;

public class TwoPhase {
	/** The axis of the move (U,R,F,D,L,B)*/
	static int[] ax = new int[31];
	/** The power of the move (90, 180 or -90 degrees)*/
	static int[] po = new int[31];

	// phase1 coordinates
	/** corners orientation coordinates*/
	static int[] flip = new int[31];
	/** edges orientation coordinates */
	static int[] twist = new int[31];
	/** UD slice coordinates (phase 1) */
	static int[] slice = new int[31];

	// phase2 coordinates
	static int[] parity = new int[31];
	static int[] URFtoDLF = new int[31];
	static int[] FRtoBR = new int[31];
	static int[] URtoDF = new int[31];
	//helper coordinates to calculate URtoDF at the beginning of phase 2  
	static int[] URtoUL = new int[31];
	static int[] UBtoDF = new int[31];

	// IDA* distance do goal estimations
	static int[] minDistPhase1 = new int[31];
	static int[] minDistPhase2 = new int[31];
	/**
	 * Computes the solver string for a given cube.
	 * 
	 * @param facelets
	 *          is the cube definition string, see {@link Facelet} for the format.
	 * 
	 * @param maxDepth
	 *          defines the maximal allowed maneuver length. For random cubes, a maxDepth of 21 usually will return a
	 *          solution in less than 0.5 seconds. With a maxDepth of 20 it takes a few seconds on average to find a
	 *          solution, but it may take much longer for specific cubes.
	 * 
	 * @param timeOut
	 *          defines the maximum computing time of the method in seconds. If it does not return with a solution, it returns with
	 *          an error code.
	 * 
	 * @param useSeparator
	 *          determines if a " . " separates the phase1 and phase2 parts of the solver string like in F' R B R L2 F .
	 *          U2 U D for example.<br>
	 * @return The solution string or an error code:<br>
	 *         Error 1: There is not exactly one facelet of each color<br>
	 *         Error 2: Not all 12 edges exist exactly once<br>
	 *         Error 3: Flip error: One edge has to be flipped<br>
	 *         Error 4: Not all corners exist exactly once<br>
	 *         Error 5: Twist error: One corner has to be twisted<br>
	 *         Error 6: Parity error: Two corners or two edges have to be exchanged<br>
	 *         Error 7: No solution exists for the given maxDepth<br>
	 *         Error 8: Timeout, no solution within given time
	 */
	public static int findSolution(Color[] facelets, int maxDepth, long timeOut, List<Move> moves) {
		int status;
		//validate input
		int[] count = new int[6];
		try {
			for (int i = 0; i < 54; i++)
				count[facelets[i].ordinal()]++;
		} catch (Exception e) {
			//invalid cube, there is not exactly one facelet of each color
			return -1;
		}
		for (int i = 0; i < 6; i++)
			if (count[i] != 9)
				//invalid cube, there is not exactly one facelet of each color
				return -1;
		FaceCube fc = new FaceCube(facelets);
		CubieCube cc = fc.toCubieCube();
		if ((status = cc.verify()) != 0)
			//invalid cube
			return status;

		// +++++++++++++++++++++++ initialization +++++++++++++++++++++++++++++++++
		CoordCube c = new CoordCube(cc);
		ax[0] = 0;
		po[0] = 0;
		flip[0] = c.flip;
		twist[0] = c.twist;
		parity[0] = c.parity;
		slice[0] = c.FRtoBR / 24;
		URFtoDLF[0] = c.URFtoDLF;
		FRtoBR[0] = c.FRtoBR;
		URtoUL[0] = c.URtoUL;
		UBtoDF[0] = c.UBtoDF;
		
		//set up search
		int move; //the move to make - axis + power
		int n = 0; //current depth
		int depthPhase1 = 1; //current depth of the search
		minDistPhase1[1] = 1; //to avoid failure for depth=1, n=0
		boolean busy = false; //if true, indicate that we are backtracking and still looking for the next move
		int totalDepth;
		
		long tStart = System.currentTimeMillis();

		// +++++++++++++++++++ Main loop ++++++++++++++++++++++++++++++++++++++++++
		//run until solution found
		do {
			//compute next move (IDA*)
			do {
				/*if not all branches that continue from current position are "dead ends",
				  i.e. current position can solved within depthPhase1-n or less moves, go deeper*/
				if ((depthPhase1 - n > minDistPhase1[n + 1]) && !busy) {
					if (ax[n] == 0 || ax[n] == 3)
						//if previous move rotated U or D faces, start from R axis
						//we don't want to twist the same faces or the parallel faces move after move 
						ax[++n] = 1;
					else
						ax[++n] = 0; //start from U axis
					po[n] = 1; //start from 90 degrees rotation
				}
				//otherwise, try the next branch
				//increase power and check if we tried all powers
				else if (++po[n] > 3) {
					//we tried all possible powers for current axis, so move to the next axis
					do {
						//increase axis and check if we tried all axes
						if (++ax[n] > 5) {
							//check for timeout
							if (System.currentTimeMillis() - tStart > timeOut << 10)
								//timeout, no solution within given time
								return -8;
							
							//we tried all 18 moves, so we need to backtrack or increase search depth
							//if we finished the DFS for current search depth, increase search depth
							if (n == 0) {
								//increase search depth (if possible)
								if (depthPhase1 >= maxDepth)
									//no solution exists for the given maxDepth
									return -7;
								else {
									depthPhase1++;
									ax[n] = 0; //start from U axis
									po[n] = 1; //start from 90 degrees rotation
									busy = false;
									break;
								}
							}
							//else, we need to backtrack
							else {
								n--;
								busy = true;
								//busy since we still need to calculate our next move
								//we need to backtrack and move to another branch 
								break;
							}
						}
						else {
							//just move to next axis (increasing done in the if condition)
							po[n] = 1; //start from 90 degrees
							busy = false;
						}
					} while (n != 0 && (ax[n - 1] == ax[n] || ax[n - 1] == ax[n] + 3));
					//we don't want to twist the same faces or the parallel faces move after move
				}
				else
					busy = false; //we found our next move
			} while (busy);

			//compute new coordinates and new minDistPhase1
			move = 3 * ax[n] + po[n] - 1;
			flip[n + 1] = CoordCube.flipMove[flip[n]][move];
			twist[n + 1] = CoordCube.twistMove[twist[n]][move];
			slice[n + 1] = CoordCube.FRtoBR_Move[slice[n] * 24][move] / 24;
			//get a lower bound on the number of moves needed to solve current position
			//(in phase 1, solve means reaching H subgroup)
			minDistPhase1[n + 1] = Math.max(
					//according to edges orientations and UD slice
					CoordCube.getPruning(CoordCube.Slice_Flip_Prun, CoordCube.N_SLICE1 * flip[n + 1] + slice[n + 1]),
					//according to corners orientations and UD slice
					CoordCube.getPruning(CoordCube.Slice_Twist_Prun, CoordCube.N_SLICE1 * twist[n + 1] + slice[n + 1]));

			//if minDistPhase1 = 0, the H subgroup is reached
			Logger.log(LoggerLevel.ERROR, LoggerGroup.ROBOT, "9.2");
			if (minDistPhase1[n + 1] == 0 && n >= depthPhase1 - 5) {
				minDistPhase1[n + 1] = 10; //we don't want to go deeper in this branch, so any value >5 is possible
				//we look for phase2 solution only if this is a "new maneuver", meaning n == depthPhase1 - 1
				if (n == depthPhase1 - 1 && (totalDepth = totalDepth(depthPhase1, maxDepth)) >= 0) {
					//if depthPhase2 = 0, this is an optimal solution
					//we also check that we don't twist the same faces or the parallel faces move after move (in the phase1 phase2 connection)
					if (totalDepth == depthPhase1
							|| (ax[depthPhase1 - 1] != ax[depthPhase1] && ax[depthPhase1 - 1] != ax[depthPhase1] + 3)) {
						createMovesList(moves, totalDepth);
						return 0;
					}
				}
			}
		} while (true);
	}
	
	

	private static void createMovesList(List<Move> moves, int depth) {
		moves.clear();
		for(int i = 0; i < depth; i++) {
			moves.add(new Move(Orientation.values()[ax[i]], Direction.values()[po[i]]));
		}
	}



	/**
	 * Apply phase2 of algorithm and return the combined phase1 and phase2 depth.
	 * In phase2, only the moves U,D,R2,F2,L2 and B2 are allowed.
	 * @param depthPhase1 is the maneuver length phase1 that led to this phase2 search
	 * @param maxDepth defines the maximal allowed maneuver length
	 * @return total maneuver length
	 */
	static int totalDepth(int depthPhase1, int maxDepth) {
		int mv; //the move to make - axis + power (face + rotation degrees)
		maxDepth = Math.min(10, maxDepth - depthPhase1); //allow only max 10 moves in phase2
		//initialize phase2 coordinates for all the moves in phase1
		for (int i = 0; i < depthPhase1; i++) {
			mv = 3 * ax[i] + po[i] - 1;
			URFtoDLF[i + 1] = CoordCube.URFtoDLF_Move[URFtoDLF[i]][mv];
			FRtoBR[i + 1] = CoordCube.FRtoBR_Move[FRtoBR[i]][mv];
			parity[i + 1] = CoordCube.parityMove[parity[i]][mv];
		}		
		//get lower bound on the number of moves needed to solve current position
		int d1 = CoordCube.getPruning(CoordCube.Slice_URFtoDLF_Parity_Prun,
				(CoordCube.N_SLICE2 * URFtoDLF[depthPhase1] + FRtoBR[depthPhase1]) * 2 + parity[depthPhase1]);
		if (d1 > maxDepth)
			//no solution exists for the given maxDepth
			return -1;
		//initialize helping coordinates for all the moves in phase1
		//these coordinates are used for initializing the URtoDF coordinate
		for (int i = 0; i < depthPhase1; i++) {
			mv = 3 * ax[i] + po[i] - 1;
			URtoUL[i + 1] = CoordCube.URtoUL_Move[URtoUL[i]][mv];
			UBtoDF[i + 1] = CoordCube.UBtoDF_Move[UBtoDF[i]][mv];
		}
		URtoDF[depthPhase1] = CoordCube.MergeURtoULandUBtoDF[URtoUL[depthPhase1]][UBtoDF[depthPhase1]];
		//get lower bound on the number of moves needed to solve current position
		int d2 = CoordCube.getPruning(CoordCube.Slice_URtoDF_Parity_Prun,
				(CoordCube.N_SLICE2 * URtoDF[depthPhase1] + FRtoBR[depthPhase1]) * 2 + parity[depthPhase1]);
		if (d2 > maxDepth)
			//no solution exists for the given maxDepth
			return -1;

		if ((minDistPhase2[depthPhase1] = Math.max(d1, d2)) == 0)// already solved
			return depthPhase1;

		//set up search
		int n = depthPhase1; //current depth
		int depthPhase2 = 1; //current depth of the search
		minDistPhase2[n + 1] = 1; //to avoid failure for depthPhase2=1, n=depthPhase1
		boolean busy = false; //if true, indicate that we are backtracking and still looking for the next move
		po[depthPhase1] = 0;
		ax[depthPhase1] = 0;
		
		//run until solution found
		do {
			//compute next move (IDA*)
			//similar to the way it was done in phase1
			do {
				/*if not all branches that continue from current position are "dead ends",
				  i.e. current position can solved within (depthPhase1 + depthPhase2 - n) or less moves, go deeper*/
				if ((depthPhase1 + depthPhase2 - n > minDistPhase2[n + 1]) && !busy) {
					if (ax[n] == 0 || ax[n] == 3) {
						//if the previous move twisted U or D, start move iteration from R face
						//and power is set to 180 degrees since it is the only move allowed
						ax[++n] = 1;
						po[n] = 2;
					}
					else {
						//otherwise, start from U, and power starts from 90 degrees
						ax[++n] = 0;
						po[n] = 1;
					}
				}
				//otherwise, try the next branch
				//increase power and check if we tried all powers
				//for R, L, F and B we can only use the power of 2 (180 degrees)  
				else if ((ax[n] == 0 || ax[n] == 3) ? (++po[n] > 3) : ((po[n] = po[n] + 2) > 3)) {
					//we tried all possible powers for current axis, so move to the next axis
					do {
						//increase axis and check if we tried all axes
						if (++ax[n] > 5) {
							//we tried all possible moves, so we need to backtrack or increase search depth
							//if we finished the DFS for current search depth, increase search depth
							if (n == depthPhase1) {
								//increase search depth (if possible)
								if (depthPhase2 >= maxDepth)
									//no solution exists for the given maxDepth
									return -1;
								else {
									depthPhase2++;
									ax[n] = 0; //start from U axis
									po[n] = 1; //start from 90 degrees
									busy = false;
									break;
								}
							}
							//else, we need to backtrack
							else {
								n--;
								busy = true;
								break;
							}
						}
						else {
							//just move to next axis (increasing done in the if condition)
							if (ax[n] == 0 || ax[n] == 3)
								po[n] = 1; //if we move the U or D faces, start from 90 degrees
							else
								po[n] = 2; //otherwise, only 180 degrees rotation are allowed
							busy = false;
						}
					} while (n != depthPhase1 && (ax[n - 1] == ax[n] || ax[n - 1] == ax[n] + 3));
					//we don't want to twist the same faces or the parallel faces move after move
				}
				else
					busy = false; //we found our next move
			} while (busy);
			//compute new coordinates and new minDistPhase2
			mv = 3 * ax[n] + po[n] - 1;
			URFtoDLF[n + 1] = CoordCube.URFtoDLF_Move[URFtoDLF[n]][mv];
			FRtoBR[n + 1] = CoordCube.FRtoBR_Move[FRtoBR[n]][mv];
			parity[n + 1] = CoordCube.parityMove[parity[n]][mv];
			URtoDF[n + 1] = CoordCube.URtoDF_Move[URtoDF[n]][mv];
			//get lower bound on the number of moves needed to solve current position
			minDistPhase2[n + 1] = Math.max(
					CoordCube.getPruning(CoordCube.Slice_URtoDF_Parity_Prun,
							(CoordCube.N_SLICE2 * URtoDF[n + 1] + FRtoBR[n + 1]) * 2 + parity[n + 1]),
					CoordCube.getPruning(CoordCube.Slice_URFtoDLF_Parity_Prun,
							(CoordCube.N_SLICE2 * URFtoDLF[n + 1] + FRtoBR[n + 1]) * 2 + parity[n + 1]));
			
		} while (minDistPhase2[n + 1] != 0);
		//return total maneuver length
		return depthPhase1 + depthPhase2;
	}
}
