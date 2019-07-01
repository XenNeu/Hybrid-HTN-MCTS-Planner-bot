/*******************************************************************************
 * Hybrid HTN MCTS Planner 
 * Created by Xenija Neufeld, 2019
 ******************************************************************************/
package HTNPlanner.Methods;

import HTNPlanner.AbstractGameState;
import HTNPlanner.Method;
import HTNPlanner.CompoundTasks.c_Act;
import HTNPlanner.CompoundTasks.c_Opening;
import HTNPlanner.PrimitiveTasks.p_AttackAllMidsize;
import HTNPlanner.PrimitiveTasks.p_PreventAttackAll;
import util.Helper;

public class m_PreventAttackAll extends Method {
	public m_PreventAttackAll() {
		super();

		this.name = "m_PreventAttackAll";

		this.tasksToDecompose.add(new p_PreventAttackAll());

	}

	@Override
	public boolean CheckPreconditions(AbstractGameState currentGameState) {
		boolean holds = true;

		return holds;
	}
}