/*******************************************************************************
 * Hybrid HTN MCTS Planner 
 * Created by Xenija Neufeld, 2019
 ******************************************************************************/
package HTNPlanner.PrimitiveTasks;

import HTNPlanner.AbstractGameState;
import HTNPlanner.Planner;
import HTNPlanner.PrimitiveTask;
import HTNPlanner.PrimitiveTasks.EvaluationFunctions.*;
import rts.GameState;
import util.Helper;

public class p_AttackAllBig extends PrimitiveTask {
	public p_AttackAllBig() {
		super();

		this.name = "p_AttackAllBig";

		this.taskEF = new AttackAllEF();
	}

	public boolean CheckPreconditions(AbstractGameState currentGameState) {
		boolean holds = true;

		if (Helper.PER_BASE_CLOSEST_RES_INFOS.isEmpty() || currentGameState.currentNumBases <= 0) {
			return true;
		}

		if (!currentGameState.anyOppUnitReachable && !currentGameState.anyOppBuildingReachable) {
			holds = false;
			if (Helper.DEBUG_PRECONDITIONS) {
				System.out.println(this.name + " does not hold: enemy is not reachable");
			}
		}

		if (currentGameState.currentNumMilitary < Helper.DESIRED_NUM_MILITARY
				&& currentGameState.currentNumMilitary < Helper.CURRENT_NUM_OPP_MILITARY_UNITS
				&& currentGameState.currentNumWorkers
						+ currentGameState.currentNumMilitary < Helper.CURRENT_NUM_OPP_MOBILE_UNITS) {
			holds = false;
			if (Helper.DEBUG_PRECONDITIONS) {
				System.out.println(this.name + " does not hold: i have less units");
			}
		}

		/*
		 * if(currentGameState.currentNumMilitary <=
		 * Helper.CURRENT_NUM_OPP_MILITARY_UNITS) { holds = false;
		 * if(Helper.DEBUG_PRECONDITIONS) { System.out.println(this.name +
		 * " does not hold: i have less units"); } }
		 */

		return holds;
	}

	public boolean IsReached(int maxplayer, int minplayer, GameState currentGameState) {
		boolean reached = false;

		if (currentGameState.gameover() && currentGameState.winner() == Planner.INSTANCE.player) {
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

		agsCopy.anyOppBuildingReachable = false;

		return agsCopy;
	}
}
