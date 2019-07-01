/*******************************************************************************
 * Hybrid HTN MCTS Planner 
 * Created by Xenija Neufeld, 2019
 ******************************************************************************/
package HTNPlanner.Methods;

import HTNPlanner.AbstractGameState;
import HTNPlanner.Method;
import HTNPlanner.CompoundTasks.c_BigMap;
import HTNPlanner.CompoundTasks.c_SmallMap;
import rts.PhysicalGameState;
import util.Helper;

public class m_handleBigMap extends Method {
	public m_handleBigMap() {
		super();

		this.name = "m_handleBigMap";

		this.tasksToDecompose.add(new c_BigMap());
	}

	@Override
	public boolean CheckPreconditions(AbstractGameState currentGameState) {
		boolean holds = true;

		if (Helper.MAX_MAP_DIST < 48) {
			return false;
		}

		return holds;
	}
}