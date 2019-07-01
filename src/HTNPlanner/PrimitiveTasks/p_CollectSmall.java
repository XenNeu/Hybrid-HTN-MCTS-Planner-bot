/*******************************************************************************
 * Hybrid HTN MCTS Planner 
 * Created by Xenija Neufeld, 2019
 ******************************************************************************/
package HTNPlanner.PrimitiveTasks;

import java.util.Vector;

import util.Helper;
import util.Pair;
import HTNPlanner.AbstractGameState;
import HTNPlanner.Planner;
import HTNPlanner.PrimitiveTask;
import HTNPlanner.PrimitiveTasks.EvaluationFunctions.*;
import rts.GameState;
import rts.PhysicalGameState;
import rts.units.Unit;

public class p_CollectSmall extends PrimitiveTask {
	public p_CollectSmall() {
		super();

		this.name = "p_CollectSmall";

		this.taskEF = new CollectEF();
	}

	public boolean CheckPreconditions(AbstractGameState currentGameState) {
		boolean holds = true;

		if (Helper.OBSERVABLE < 1) {
			holds = false;
			return holds;
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
				if (dist < (float) (Helper.MAX_MAP_DIST / 5)) {
					holds = false;
					if (Helper.DEBUG_PRECONDITIONS) {
						System.out.println(this.name + " does not hold: an enemy unit is too close");
					}
					return holds;
					// break;
				}
			}
		}

		if (currentGameState.currentNumBases == 0) {
			holds = false;
			if (Helper.DEBUG_PRECONDITIONS) {
				System.out.println(this.name + " does not hold: I do not have a base");
			}
		}

		if (Helper.PER_BASE_CLOSEST_RES_INFOS.isEmpty()) {
			holds = false;
			if (Helper.DEBUG_PRECONDITIONS) {
				System.out.println(this.name + " does not hold: no res. left or reachable");
			}
		}
		return holds;
	}

	public boolean IsReached(int maxplayer, int minplayer, GameState currentGameState) {
		boolean reached = false;

		if (currentGameState.getPlayer(Planner.INSTANCE.player).getResources() >= Helper.DESIRED_NUM_RES
				&& Helper.CURRENT_NUM_WORKERS >= Helper.DESIRED_NUM_WORKERS) {
			reached = true;
		}

		if (reached && Helper.DEBUG_POSTCONDITIONS) {
			System.out.println(this.name + " reached");
		}
		return reached;
	}

	@Override
	public AbstractGameState Simulate(AbstractGameState prevGameState) {
		AbstractGameState agsCopy = super.Simulate(prevGameState);
		agsCopy.anyOppBuildingReachable = true;

		agsCopy.currentNumResources = Helper.DESIRED_NUM_RES;
		agsCopy.currentNumBases = Helper.DESIRED_NUM_BASES;
		agsCopy.currentNumWorkers = Math.max(Helper.DESIRED_NUM_WORKERS, Helper.CURRENT_NUM_WORKERS);
		agsCopy.currentNumMilitary = Math.max(
				Math.max(Helper.DESIRED_NUM_MILITARY, Helper.CURRENT_NUM_OPP_MOBILE_UNITS),
				prevGameState.currentNumMilitary);

		return agsCopy;
	}

}
