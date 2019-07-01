/*******************************************************************************
 * Hybrid HTN MCTS Planner 
 * Created by Xenija Neufeld, 2019
 ******************************************************************************/
package HTNPlanner.CompoundTasks;

import java.io.PrintWriter;
import java.util.LinkedList;

import HTNPlanner.AbstractGameState;
import HTNPlanner.CompoundTask;
import HTNPlanner.Planner;
import HTNPlanner.Task;
import HTNPlanner.Methods.*;
import rts.GameState;
import util.Helper;

public class c_MiddleGame extends CompoundTask {

	// how often each method was used to decompose this task
	protected static int[] methodsUsed;

	// how often each method was used to decompose this task
	protected static float[] methodsSucceeded;

	protected static int selected;

	protected static float succeeded;

	protected static float maxReward = Helper.EPSILON;

	public c_MiddleGame() {
		super();

		this.name = "c_MiddleGame";

		this.methods.add(new m_BuildRangedAndDefendSmall());
		this.methods.add(new m_BuildAndDefendSmall());
		this.methods.add(new m_BuildAndDefendMidsize());
		this.methods.add(new m_BuildAndDefendBig());

		c_MiddleGame.methodsUsed = new int[this.methods.size()];
		c_MiddleGame.methodsSucceeded = new float[this.methods.size()];
	}

	@Override
	public AbstractGameState Decompose(AbstractGameState prevGameState, LinkedList<Task> prevTasks) {
		AbstractGameState newGameState = super.Decompose(prevGameState, prevTasks);
		// if(newGameState == null || Planner.INSTANCE.GetCurrentPlanLength()==0)
		// {
		// newGameState =
		// this.methods.get(this.methods.size()-1).DecomposeTask(prevGameState,
		// prevTasks);
		// }

		return newGameState;
	}

	@Override
	public void SetUCBValues(String[] values) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void IncreaseSelectionStatistics(int methodIndex) {
		// TODO Auto-generated method stub

	}

	@Override
	public float GetExplorationValForMethod(int methodIndex) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float GetExploitationValForMethod(int methodIndex) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void PrintAllUCBValuesOfTask(PrintWriter pw) {
		// TODO Auto-generated method stub

	}

	@Override
	public void IncreaseSuccessStatistics(float currentReward) {
		// TODO Auto-generated method stub

	}
}