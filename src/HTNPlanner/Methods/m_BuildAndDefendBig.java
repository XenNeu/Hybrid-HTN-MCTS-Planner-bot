/*******************************************************************************
 * Hybrid HTN MCTS Planner 
 * Created by Xenija Neufeld, 2019
 ******************************************************************************/
package HTNPlanner.Methods;

import HTNPlanner.AbstractGameState;
import HTNPlanner.Method;
import HTNPlanner.CompoundTasks.c_EndGame;
import HTNPlanner.PrimitiveTasks.*;
import util.Helper;

public class m_BuildAndDefendBig extends Method {
	public m_BuildAndDefendBig() {
		super();

		this.name = "m_BuildAndDefendBig";

		this.tasksToDecompose.add(new p_BuildAndDefendBig());
		this.tasksToDecompose.add(new c_EndGame());
	}

	@Override
	public boolean CheckPreconditions(AbstractGameState currentGameState) {
		boolean holds = true;

		if (Helper.MAX_MAP_DIST < 48) {
			return false;
		}

		// //if cannot reach the opponent to attack him - build
		// if((!Helper.OPP_BUILDINGS.isEmpty() &&
		// currentGameState.anyOppBuildingReachable)
		// || Helper.OPP_BUILDINGS.isEmpty())
		// {
		// return false;
		// }

		return holds;
	}
}
