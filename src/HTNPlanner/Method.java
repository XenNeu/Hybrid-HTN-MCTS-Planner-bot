/*******************************************************************************
 * Hybrid HTN MCTS Planner 
 * Created by Xenija Neufeld, 2019
 ******************************************************************************/
package HTNPlanner;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;

import rts.GameState;
import rts.PlayerAction;
import util.Helper;

public abstract class Method {
	public PlayerAction myAct;

	public String name;

	protected ArrayList<Task> tasksToDecompose;

	protected int prevPlanSize = 0;

	public abstract boolean CheckPreconditions(AbstractGameState currentGameState);

	public Method() {
		tasksToDecompose = new ArrayList<Task>();
	}

	public void PrintAllUCBValuesOfMethod(PrintWriter pw) {
		if (this.tasksToDecompose.size() == 0) {
			return;
		}

		for (int i = 0; i < this.tasksToDecompose.size(); ++i) {
			if (this.tasksToDecompose.get(i).getClass().getSimpleName().contains("p_")) {
				continue;
			}
			((CompoundTask) this.tasksToDecompose.get(i)).PrintAllUCBValuesOfTask(pw);
		}
	}

	public AbstractGameState DecomposeTask(AbstractGameState gameState, LinkedList<Task> prevTasks) {
		AbstractGameState prevGameState = gameState;
		AbstractGameState toChangeGameState = prevGameState.clone();

		prevPlanSize = Planner.INSTANCE.GetCurrentPlanLength();

		if (this.tasksToDecompose.size() == 0) {
			if (Helper.DEBUG_METHOD_DECOMPOSITION) {
				System.out.println("no tasks defined for method " + this.name);
			}
			return toChangeGameState;
		}

		long currTime;

		for (int i = 0; i < this.tasksToDecompose.size(); ++i) {
			currTime = System.currentTimeMillis();
			if (currTime - Planner.INSTANCE.time >= Helper.MAX_TIME_FOR_PLANNER) {
				if (Helper.DEBUG_TIME_OUT) {
					System.out.println("Method " + this.name + " : no computation time left.>>>>>>>>>>>>>");
				}
				return null;
			}

			toChangeGameState = this.tasksToDecompose.get(i).Decompose(toChangeGameState,
					(LinkedList<Task>) prevTasks.clone());

			// one of the tasks could not be decomposed by any methods
			if (toChangeGameState == null) {
				currTime = System.currentTimeMillis();

				// if prim tasks were added by this method, remove them again
				int diff = Planner.INSTANCE.GetCurrentPlanLength() - prevPlanSize;
				if (diff != 0 && currTime - Planner.INSTANCE.time < Helper.MAX_TIME_FOR_PLANNER) {
					for (int j = 0; j < diff; ++j) {
						Planner.INSTANCE.RemoveLastPlanStep();
					}
				}

				break;
			}
		}

		return toChangeGameState;
	}

	public int GetAmountOfTasks() {
		return this.tasksToDecompose.size();
	}
}
