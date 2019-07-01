/*******************************************************************************
 * Hybrid HTN MCTS Planner 
 * Created by Xenija Neufeld, 2019
 ******************************************************************************/
package HTNPlanner.Methods;

import HTNPlanner.AbstractGameState;
import HTNPlanner.Method;
import HTNPlanner.PrimitiveTasks.*;
import util.Helper;

public class m_DefendMidsize extends Method {
	public m_DefendMidsize() {
		super();

		this.name = "m_DefendMidsize";

		// this.tasksToDecompose.add(new p_DefendMidsize());
	}

	@Override
	public boolean CheckPreconditions(AbstractGameState currentGameState) {
		boolean holds = true;

		if (Helper.MAX_MAP_DIST < 31 || Helper.MAX_MAP_DIST >= 63) {
			return false;
		}

		return holds;
	}
}