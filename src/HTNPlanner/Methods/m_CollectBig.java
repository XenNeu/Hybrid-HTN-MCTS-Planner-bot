/*******************************************************************************
 * Hybrid HTN MCTS Planner 
 * Created by Xenija Neufeld, 2019
 ******************************************************************************/
package HTNPlanner.Methods;

import HTNPlanner.AbstractGameState;
import HTNPlanner.Method;
import HTNPlanner.CompoundTasks.c_EndGame;
import HTNPlanner.CompoundTasks.c_MiddleGame;
import HTNPlanner.PrimitiveTasks.*;
import util.Helper;

public class m_CollectBig extends Method {
	public m_CollectBig() {
		super();

		this.name = "m_CollectBig";

		this.tasksToDecompose.add(new p_CollectBig());
		this.tasksToDecompose.add(new c_MiddleGame());
		// this.tasksToDecompose.add(new c_EndGame());

		// this.tasksToDecompose.add(new p_BuildAndDefendBig());
		// this.tasksToDecompose.add(new p_AttackAllBig());
	}

	@Override
	public boolean CheckPreconditions(AbstractGameState currentGameState) {
		boolean holds = true;

		if (Helper.MAX_MAP_DIST < 48) {
			return false;
		}

		if (Helper.OBSERVABLE < 0) {
			return false;
		}

		return holds;
	}
}