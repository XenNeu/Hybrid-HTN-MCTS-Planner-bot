/*******************************************************************************
 * Hybrid HTN MCTS Planner 
 * Created by Xenija Neufeld, 2019
 ******************************************************************************/
package HTNPlanner.Methods;

import HTNPlanner.AbstractGameState;
import HTNPlanner.Method;
import HTNPlanner.CompoundTasks.c_Act;
import HTNPlanner.CompoundTasks.c_MiddleGame;
import HTNPlanner.PrimitiveTasks.p_PreventAttackAll;
import HTNPlanner.PrimitiveTasks.p_PreventAttackMilitary;

public class m_PreventAttackMilitary extends Method {
	public m_PreventAttackMilitary() {
		super();

		this.name = "m_PreventAttackMilitary";

		this.tasksToDecompose.add(new p_PreventAttackMilitary());

	}

	@Override
	public boolean CheckPreconditions(AbstractGameState currentGameState) {
		boolean holds = true;

		return holds;
	}
}
