/*******************************************************************************
 * Hybrid HTN MCTS Planner 
 * Created by Xenija Neufeld, 2019
 ******************************************************************************/
package HTNPlanner.Methods;

import HTNPlanner.AbstractGameState;
import HTNPlanner.Method;
import HTNPlanner.CompoundTasks.c_EndGame;
import HTNPlanner.CompoundTasks.c_SmallMap;
import rts.PhysicalGameState;
import util.Helper;

public class m_handleSmallMap extends Method {
	public m_handleSmallMap() {
		super();

		this.name = "m_handleSmallMap";

		this.tasksToDecompose.add(new c_SmallMap());
	}

	@Override
	public boolean CheckPreconditions(AbstractGameState currentGameState) {
		boolean holds = true;

		if (Helper.MAX_MAP_DIST >= 30) {
			return false;
		}

		return holds;
	}
}