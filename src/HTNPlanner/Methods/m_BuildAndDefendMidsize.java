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

public class m_BuildAndDefendMidsize extends Method {
	public m_BuildAndDefendMidsize() {
		super();

		this.name = "m_BuildAndDefendMidsize";

		this.tasksToDecompose.add(new p_BuildAndDefendMidsize());
		this.tasksToDecompose.add(new c_EndGame());
	}

	@Override
	public boolean CheckPreconditions(AbstractGameState currentGameState) {
		boolean holds = true;

		if (Helper.MAX_MAP_DIST < 31 || Helper.MAX_MAP_DIST >= 48) {
			return false;
		}
		return holds;
	}
}