/*******************************************************************************
 * Hybrid HTN MCTS Planner 
 * Created by Xenija Neufeld, 2019
 ******************************************************************************/
package HTNPlanner.Methods;

import HTNPlanner.AbstractGameState;
import HTNPlanner.Method;
import HTNPlanner.PrimitiveTasks.p_AttackMilitaryBig;
import util.Helper;

public class m_AttackMilitaryBig extends Method {
	public m_AttackMilitaryBig() {
		super();

		this.name = "m_AttackMilitaryBig";

		this.tasksToDecompose.add(new p_AttackMilitaryBig());
	}

	@Override
	public boolean CheckPreconditions(AbstractGameState currentGameState) {
		boolean holds = true;

		if (Helper.MAX_MAP_DIST < 48) {
			return false;
		}

		if (Helper.PER_BASE_CLOSEST_RES_INFOS.isEmpty() || Helper.MY_BASES.isEmpty()) {
			return false;
		}

		return holds;
	}
}
