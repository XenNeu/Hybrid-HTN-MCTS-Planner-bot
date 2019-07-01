/*******************************************************************************
 * Hybrid HTN MCTS Planner 
 * Created by Xenija Neufeld, 2019
 ******************************************************************************/
package HTNPlanner.Methods;

import HTNPlanner.AbstractGameState;
import HTNPlanner.Method;
import HTNPlanner.CompoundTasks.c_EndGame;
import HTNPlanner.PrimitiveTasks.p_BuildAndDefendSmall;
import HTNPlanner.PrimitiveTasks.p_BuildRangedAndDefendSmall;
import util.Helper;

public class m_BuildRangedAndDefendSmall extends Method {
	public m_BuildRangedAndDefendSmall() {
		super();

		this.name = "m_BuildRangedAndDefendSmall";

		this.tasksToDecompose.add(new p_BuildRangedAndDefendSmall());
		this.tasksToDecompose.add(new c_EndGame());
	}

	@Override
	public boolean CheckPreconditions(AbstractGameState currentGameState) {
		boolean holds = true;

		if (Helper.MAX_MAP_DIST >= 31) {
			return false;
		}

		// if cannot reach the opponent to attack him - build
		if ((currentGameState.anyOppUnitReachable || currentGameState.anyOppBuildingReachable)
				&& currentGameState.currentNumWorkers
						+ currentGameState.currentNumMilitary >= Helper.CURRENT_NUM_OPP_MOBILE_UNITS - 3) {
			return false;
		}
		return holds;
	}
}
