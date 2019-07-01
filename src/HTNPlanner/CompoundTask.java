/*******************************************************************************
 * Hybrid HTN MCTS Planner 
 * Created by Xenija Neufeld, 2019
 ******************************************************************************/
package HTNPlanner;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;

import HTNPlanner.CompoundTasks.*;
import rts.GameState;
import util.Helper;

public abstract class CompoundTask extends Task {

	public ArrayList<Method> methods;

	protected int indMethodSelected;
	protected float prevMethodsReward;
	protected float tempReward;
	protected float[] lastMethodsValues;

	private int prevPlanLength = 0;

	public CompoundTask() {
		this.methods = new ArrayList<Method>();
	}

	public abstract void SetUCBValues(String[] values);

	public AbstractGameState Decompose(AbstractGameState prevGameState, LinkedList<Task> prevTasks) {
		this.prevGameState = prevGameState;
		// save the whole tree path of tasks that lead to a prim. task
		prevTasks.add(this);
		this.linkedTasks = (LinkedList<Task>) prevTasks.clone();

		AbstractGameState toChangeGameState = prevGameState.clone();

		AbstractGameState newGameState = null;

		// select a method depending on preconditions and selection mechanism
		int methodIndex = -1;
		int[] methodsTried = new int[this.methods.size()];
		Method toDecompose = null;

		toDecompose = Planner.INSTANCE.SelectNextMethod(this, toChangeGameState, methodIndex, methodsTried);

		boolean decompositionNeeded = toDecompose == null ? false : true;

		while (decompositionNeeded) {

			long currTime = System.currentTimeMillis();
			if (currTime - Planner.INSTANCE.time >= Helper.MAX_TIME_FOR_PLANNER) {
				if (Helper.DEBUG_TIME_OUT) {
					System.out.println("Task " + this.name + " : no computation time left for decomposition.");
				}
				return null;
			}

			this.indMethodSelected = this.methods.indexOf(toDecompose);
			this.tempReward = 0;
			this.lastMethodsValues = null;
			this.prevMethodsReward = 0;

			newGameState = toDecompose.DecomposeTask(toChangeGameState, this.linkedTasks);

			methodsTried[this.indMethodSelected] = 1;

			if (newGameState != null) {
				// decomposition succeeded, stop
				decompositionNeeded = false;

				// this.IncreaseSelectionStatistics(indMethodSelected);
				// this.FlushMethodRewards();
			} else {
				// decomposition failed, try next method
				if (Helper.DEBUG_METHOD_DECOMPOSITION) {
					System.out.println("Decomposing task " + this.name + " failed, trying to find next method");
				}

				currTime = System.currentTimeMillis();
				if (currTime - Planner.INSTANCE.time >= Helper.MAX_TIME_FOR_PLANNER) {
					if (Helper.DEBUG_TIME_OUT) {
						System.out.println("Task " + this.name + " : no computation time left for decomposition.");
					}
					return null;
				}

				methodIndex = this.indMethodSelected;
				toDecompose = Planner.INSTANCE.SelectNextMethod(this, toChangeGameState, methodIndex, methodsTried);
				decompositionNeeded = toDecompose == null ? false : true;
			}
		}

		return newGameState;
	}

	protected abstract void IncreaseSelectionStatistics(int methodIndex);

	public abstract float GetExplorationValForMethod(int methodIndex);

	public abstract float GetExploitationValForMethod(int methodIndex);

	public abstract void PrintAllUCBValuesOfTask(PrintWriter pw);

	public void IncreaseSuccessStatistics(float[] methodsValues, int methodsIndex, float reward, float succeeded) {
		if (this.lastMethodsValues == null) {
			this.lastMethodsValues = methodsValues;
			this.prevMethodsReward = methodsValues[methodsIndex];
		}

		float successPortion = (float) reward;

		successPortion = reward;
		succeeded += reward;

		tempReward += successPortion;

		// do not update for every subtask, because some can fail later. Update only
		// when all succeed
		// methodsValues[methodsIndex] += successPortion;
		// if(Helper.DEBUG_UCB_STATICTICS)
		// {
		// System.out.println("when SUCCESS ind of " + this.name + " = " +
		// this.indMethodSelected + ", temp val = " +successPortion);
		// }
	}

	@Override
	protected void FlushMethodRewards() {
		if (this.lastMethodsValues != null) {
			this.lastMethodsValues[this.indMethodSelected] += tempReward;
		}

		if (Helper.DEBUG_UCB_STATICTICS) {
			System.out.println("total reward of " + tempReward + " added to method "
					+ this.methods.get(this.indMethodSelected).name + " in " + this.name);
		}
	}

}
