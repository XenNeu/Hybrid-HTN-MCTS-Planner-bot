/*******************************************************************************
 * Hybrid HTN MCTS Planner 
 * Created by Xenija Neufeld, 2019
 ******************************************************************************/
package HTNPlanner.Methods;

import HTNPlanner.AbstractGameState;
import HTNPlanner.Method;
import HTNPlanner.CompoundTasks.c_MidsizeMap;
import HTNPlanner.CompoundTasks.c_SmallMap;
import rts.PhysicalGameState;
import util.Helper;

public class m_handleMidsizeMap extends Method {
	public m_handleMidsizeMap() {
		super();

		this.name = "m_handleMidsizeMap";

		this.tasksToDecompose.add(new c_MidsizeMap());
	}

	@Override
	public boolean CheckPreconditions(AbstractGameState currentGameState) {
		boolean holds = true;

		if (Helper.MAX_MAP_DIST < 30 || Helper.MAX_MAP_DIST >= 48) {
			return false;
		}

		return holds;
	}
}
