/*******************************************************************************
 * Hybrid HTN MCTS Planner 
 * Created by Xenija Neufeld, 2019
 ******************************************************************************/
package HTNPlanner.Methods;

import HTNPlanner.AbstractGameState;
import HTNPlanner.Method;
import HTNPlanner.PrimitiveTasks.p_AttackAllMidsize;
import HTNPlanner.PrimitiveTasks.p_AttackAllSmall;
import util.Helper;

public class m_AttackMidsize extends Method {
	public m_AttackMidsize() {
		super();

		this.name = "m_AttackMidsize";

		this.tasksToDecompose.add(new p_AttackAllMidsize());
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