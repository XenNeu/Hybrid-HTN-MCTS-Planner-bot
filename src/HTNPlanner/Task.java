/*******************************************************************************
 * Hybrid HTN MCTS Planner 
 * Created by Xenija Neufeld, 2019
 ******************************************************************************/
package HTNPlanner;

import java.util.LinkedList;

import util.Pair;
import rts.GameState;

public abstract class Task {
	public String name;

	public AbstractGameState prevGameState;

	protected LinkedList<Task> linkedTasks;

	// for compound tasks this means 'decompose'; for primitive - 'simulate'
	public abstract AbstractGameState Decompose(AbstractGameState gameState, LinkedList<Task> prevTasks);

	public abstract void IncreaseSuccessStatistics(float currentReward);

	public float GetBonus() {
		return 0;
	}

	public void AddBonus(float bonus) {
	}

	protected abstract void FlushMethodRewards();
}
