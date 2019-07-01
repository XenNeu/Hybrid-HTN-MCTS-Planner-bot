/*******************************************************************************
 * Hybrid HTN MCTS Planner 
 * Created by Xenija Neufeld, 2019
 ******************************************************************************/
package HTNPlanner.Methods;

import HTNPlanner.AbstractGameState;
import HTNPlanner.Method;
import HTNPlanner.PrimitiveTasks.*;
import util.Helper;

public class m_AttackAllBig extends Method {
	public m_AttackAllBig() {
		super();

		this.name = "m_AttackAllBig";

		this.tasksToDecompose.add(new p_AttackAllBig());
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