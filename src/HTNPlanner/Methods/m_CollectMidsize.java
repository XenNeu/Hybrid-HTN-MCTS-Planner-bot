/*******************************************************************************
 * Hybrid HTN MCTS Planner 
 * Created by Xenija Neufeld, 2019
 ******************************************************************************/
package HTNPlanner.Methods;

import HTNPlanner.AbstractGameState;
import HTNPlanner.Method;
import HTNPlanner.CompoundTasks.c_MiddleGame;
import HTNPlanner.PrimitiveTasks.*;
import util.Helper;

public class m_CollectMidsize extends Method {
	public m_CollectMidsize() {
		super();

		this.name = "m_CollectMidsize";

		this.tasksToDecompose.add(new p_CollectMidsize());
		this.tasksToDecompose.add(new c_MiddleGame());
	}

	@Override
	public boolean CheckPreconditions(AbstractGameState currentGameState) {
		boolean holds = true;

		if (Helper.MAX_MAP_DIST < 31 || Helper.MAX_MAP_DIST >= 48) {
			return false;
		}

		if (Helper.OBSERVABLE < 0) {
			return false;
		}

		return holds;
	}
}