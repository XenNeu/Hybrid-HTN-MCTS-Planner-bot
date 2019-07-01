/*******************************************************************************
 * Hybrid HTN MCTS Planner 
 * Created by Xenija Neufeld, 2019
 ******************************************************************************/
package HTNPlanner;

import java.util.LinkedList;
import java.util.Vector;

import HTNPlanner.CompoundTasks.*;
import rts.GameState;
import rts.PlayerAction;
import util.Helper;
import ai.evaluation.EvaluationFunction;

public abstract class PrimitiveTask extends Task {

	public PlayerAction myAction;
	protected EvaluationFunction taskEF;
	protected AbstractGameState newGameState;

	public EvaluationFunction GetTaskEF() {
		return taskEF;
	}

	public abstract boolean CheckPreconditions(AbstractGameState currentGameState);

	public AbstractGameState Decompose(AbstractGameState prevGameState, LinkedList<Task> prevTasks) {
		this.linkedTasks = (LinkedList<Task>) prevTasks.clone();
		this.linkedTasks.add(this);

		if (!this.CheckPreconditions(prevGameState)) {
			if (Helper.DEBUG_METHOD_DECOMPOSITION) {
				System.out.println("Prim. task " + this.name + " could not be applied");
			}
			return null;
		}

		return Simulate(prevGameState);
	}

	public boolean IsReached(int maxplayer, int minplayer, GameState currentGameState) {
		boolean reached = false;

		float currentVal = taskEF.evaluate(maxplayer, minplayer, currentGameState);
		float upperVal = 0.8f * taskEF.upperBound(currentGameState);

		reached = currentVal >= upperVal;

		if (reached && Helper.DEBUG_POSTCONDITIONS) {
			System.out.println(this.name + " reached");
		}
		return reached;
	}

	public AbstractGameState Simulate(AbstractGameState prevGameState) {
		AbstractGameState toChangeGameState = prevGameState.clone();
		Planner.INSTANCE.AddPlanStep(this);
		return toChangeGameState;
	}

	@Override
	public void IncreaseSuccessStatistics(float currentReward) {
		float totalBonus = 0;
		// size()-1 because we increase for this one afterwards
		for (int i = this.linkedTasks.size() - 2; i >= 0; --i) {
			float bonus = i < this.linkedTasks.size() - 2 ? this.linkedTasks.get(i + 1).GetBonus() : 0;

			totalBonus += bonus;
			this.linkedTasks.get(i).AddBonus(totalBonus);
			this.linkedTasks.get(i).IncreaseSuccessStatistics(currentReward);
			if (Helper.REWARD_FROM_EXECUTION) {
				this.linkedTasks.get(i).FlushMethodRewards();
			}
		}

		if (Helper.DEBUG_UCB_STATICTICS) {
			System.out.println("Prim. task " + this.name + " curr reward = " + currentReward);
		}
	}

	@Override
	protected void FlushMethodRewards() {
	}
}
