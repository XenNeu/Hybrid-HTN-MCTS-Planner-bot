/*******************************************************************************
 * Hybrid HTN MCTS Planner 
 * Created by Xenija Neufeld, 2019
 ******************************************************************************/
package HTNPlanner.Methods;

import HTNPlanner.AbstractGameState;
import HTNPlanner.Method;
import HTNPlanner.PrimitiveTasks.p_AttackAllSmall;
import rts.GameState;
import util.Helper;

public class m_AttackSmall extends Method {
	public m_AttackSmall() {
		super();

		this.name = "m_Attack";

		this.tasksToDecompose.add(new p_AttackAllSmall());
	}

	@Override
	public boolean CheckPreconditions(AbstractGameState currentGameState) {
		boolean holds = true;

		if (Helper.MAX_MAP_DIST >= 31) {
			return false;
		}

		return holds;
	}
}
