/*******************************************************************************
 * Hybrid HTN MCTS Planner 
 * Created by Xenija Neufeld, 2019
 ******************************************************************************/
package HTNPlanner.PrimitiveTasks;

import HTNPlanner.AbstractGameState;
import HTNPlanner.Planner;
import HTNPlanner.PrimitiveTask;
import HTNPlanner.PrimitiveTasks.EvaluationFunctions.CollectEF;
import rts.GameState;
import rts.PhysicalGameState;
import rts.units.Unit;
import util.Helper;

public class p_CollectMidsize extends PrimitiveTask {
	public p_CollectMidsize() {
		super();

		this.name = "p_CollectMidsize";

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

		PhysicalGameState pgs = currentGameState.getPhysicalGameState();

		// all barracks already built
		if ((Helper.CURRENT_NUM_BARRACKS >= 1
				|| currentGameState.getPlayer(Planner.INSTANCE.player).getResources() >= Helper.BARRACKS_TYPE.cost) // Helper.DESIRED_NUM_BARRACKS
				&& Helper.CURRENT_NUM_WORKERS >= Helper.DESIRED_NUM_WORKERS) {
			reached = true;
		}

		// some of the barracks already built and enough res. to build the rest
		if (Helper.CURRENT_NUM_BARRACKS > 0
				&& currentGameState.getPlayer(Planner.INSTANCE.player)
						.getResources() >= (Helper.DESIRED_NUM_BARRACKS - Helper.CURRENT_NUM_BARRACKS)
								* Helper.BARRACKS_TYPE.cost
				&& Helper.CURRENT_NUM_WORKERS >= Helper.DESIRED_NUM_WORKERS) {
			reached = true;
		}

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
		return agsCopy;
	}

}
