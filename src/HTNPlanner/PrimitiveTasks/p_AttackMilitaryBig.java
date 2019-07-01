/*******************************************************************************
 * Hybrid HTN MCTS Planner 
 * Created by Xenija Neufeld, 2019
 ******************************************************************************/
package HTNPlanner.PrimitiveTasks;

import HTNPlanner.AbstractGameState;
import HTNPlanner.Planner;
import HTNPlanner.PrimitiveTask;
import HTNPlanner.PrimitiveTasks.EvaluationFunctions.AttackMilitaryEF;
import rts.GameState;
import util.Helper;

public class p_AttackMilitaryBig extends PrimitiveTask {
	public p_AttackMilitaryBig() {
		super();

		this.name = "p_AttackMilitaryBig";

		this.taskEF = new AttackMilitaryEF();
	}

	public boolean CheckPreconditions(AbstractGameState currentGameState) {
		boolean holds = true;

		if (currentGameState.currentNumBases <= 0) {
			return false;
		}

		if (currentGameState.currentNumMilitary < 2) {
			holds = false;
			if (Helper.DEBUG_PRECONDITIONS) {
				System.out.println(this.name + " does not hold: not enough military units");
			}
		}

		if (!currentGameState.anyOppUnitReachable && !currentGameState.anyOppBuildingReachable) {
			holds = false;
			if (Helper.DEBUG_PRECONDITIONS) {
				System.out.println(this.name + " does not hold: enemy is not reachable");
			}
		}
		
		if (currentGameState.currentNumMilitary <= Helper.CURRENT_NUM_OPP_MILITARY_UNITS
				&& Helper.MAX_MAP_DIST != 64) 
		{
			holds = false;
			if (Helper.DEBUG_PRECONDITIONS) {
				System.out.println(this.name + " does not hold: i have less units");
			}
		}
		
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
