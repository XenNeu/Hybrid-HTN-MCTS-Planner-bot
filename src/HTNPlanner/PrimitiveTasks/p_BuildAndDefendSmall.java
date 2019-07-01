/*******************************************************************************
 * Hybrid HTN MCTS Planner 
 * Created by Xenija Neufeld, 2019
 ******************************************************************************/
package HTNPlanner.PrimitiveTasks;

import HTNPlanner.AbstractGameState;
import HTNPlanner.PrimitiveTask;
import HTNPlanner.PrimitiveTasks.EvaluationFunctions.*;
import rts.GameState;
import rts.PhysicalGameState;
import rts.units.Unit;
import util.Helper;

public class p_BuildAndDefendSmall extends PrimitiveTask {
	public p_BuildAndDefendSmall() {
		super();

		this.name = "p_BuildAndDefendSmall";

		this.taskEF = new BuildAndDefendEF();
	}

	public boolean CheckPreconditions(AbstractGameState currentGameState) {
		boolean holds = true;

		// my bases destroyed and no resources to build new ones
		if (currentGameState.currentNumBases <= 0 && (currentGameState.currentNumResources < Helper.BASE_TYPE.cost
				|| currentGameState.currentNumWorkers < 1)) {
			holds = false;
			// if(Helper.DEBUG_PRECONDITIONS)
			// {
			// System.out.println(this.name + " does not hold: no bases/resources
			// available");
			// }
		}

		// no resources left to build
		if (currentGameState.currentNumResources < 5 && (Helper.PER_BASE_CLOSEST_RES_INFOS.isEmpty())) {
			holds = false;
			// if(Helper.DEBUG_PRECONDITIONS)
			// {
			// System.out.println(this.name + " does not hold: no resources available");
			// }
		}

		PhysicalGameState pgs = currentGameState.gameState.getPhysicalGameState();

		if (currentGameState.currentNumBases > 0 && !Helper.PER_BASE_CLOSEST_OPP_INFOS.isEmpty()) {
			for (Long baseID : Helper.PER_BASE_CLOSEST_OPP_INFOS.keySet()) {
				int dist = Integer.MAX_VALUE;

				Unit base = pgs.getUnit(baseID);
				Unit opp = Helper.PER_BASE_CLOSEST_OPP_INFOS.get(baseID);

				if (base != null && opp != null) {
					dist = Math.abs(base.getX() - opp.getX()) + Math.abs(base.getY() - opp.getY());
				}
				if (dist < (float) (Helper.MAX_MAP_DIST / 4)) {
					holds = false;
					// if(Helper.DEBUG_PRECONDITIONS)
					// {
					// System.out.println(this.name + " does not hold: an enemy unit is too close");
					// }
					return holds;
					// break;
				}
			}
		}

		return holds;
	}

	public boolean IsReached(int maxplayer, int minplayer, GameState currentGameState) {
		boolean reached = false;

		if (Helper.CURRENT_NUM_BARRACKS >= Helper.DESIRED_NUM_BARRACKS
				&& Helper.CURRENT_NUM_MILITARY >= Helper.DESIRED_NUM_MILITARY
				&& (Helper.ANY_OPP_BUILDING_REACHABLE || Helper.ANY_OPP_UNIT_REACHABLE)
				&& (Helper.CURRENT_NUM_WORKERS + Helper.CURRENT_NUM_MILITARY >= Helper.CURRENT_NUM_OPP_MOBILE_UNITS - 3
						|| Helper.CURRENT_NUM_MILITARY > (Helper.CURRENT_NUM_OPP_MOBILE_UNITS
								- Helper.CURRENT_NUM_OPP_MILITARY_UNITS) / 2)) {
			reached = true;
		}

		/*
		 * if((Helper.ANY_OPP_BUILDING_REACHABLE || Helper.ANY_OPP_UNIT_REACHABLE) &&
		 * Helper.CURRENT_NUM_WORKERS + Helper.CURRENT_NUM_MILITARY >
		 * Helper.CURRENT_NUM_OPP_MOBILE_UNITS + 3) { reached = true; }
		 */

		// if(reached && Helper.DEBUG_POSTCONDITIONS)
		// {
		// System.out.println(this.name + " reached");
		// }
		return reached;
	}

	@Override
	public AbstractGameState Simulate(AbstractGameState prevGameState) {
		AbstractGameState agsCopy = super.Simulate(prevGameState);

		agsCopy.anyOppBuildingReachable = true;
		agsCopy.anyOppUnitReachable = true;

		agsCopy.currentNumMilitary = Math.max(
				Math.max(Helper.DESIRED_NUM_MILITARY, Helper.CURRENT_NUM_OPP_MOBILE_UNITS),
				prevGameState.currentNumMilitary);
		agsCopy.currentNumBarracks = Math.max(Helper.DESIRED_NUM_BARRACKS, prevGameState.currentNumBarracks);
		agsCopy.currentNumWorkers = Math.max(Helper.DESIRED_NUM_WORKERS, prevGameState.currentNumWorkers);
		agsCopy.currentNumBases = Math.max(Helper.DESIRED_NUM_BASES, prevGameState.currentNumBases);

		return agsCopy;
	}

}