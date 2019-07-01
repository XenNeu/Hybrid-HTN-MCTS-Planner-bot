/*******************************************************************************
 * Hybrid HTN MCTS Planner 
 * Created by Xenija Neufeld, 2019
 ******************************************************************************/
package HTNPlanner.CompoundTasks;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;

import util.Helper;
import util.Pair;
import HTNPlanner.AbstractGameState;
import HTNPlanner.CompoundTask;
import HTNPlanner.Method;
import HTNPlanner.Planner;
import HTNPlanner.Task;
import HTNPlanner.Methods.*;
import rts.GameState;

public class c_Act extends CompoundTask {

	// how often each method was used to decompose this task
	protected static int[] methodsUsed;

	// how often each method was used to decompose this task
	protected static float[] methodsSucceeded;

	protected static int selected;

	protected static float succeeded;

	protected static float maxReward = Helper.EPSILON;

	public c_Act() {
		super();

		this.name = "c_Act";

		this.methods.add(new m_handleBigMap());
		this.methods.add(new m_handleMidsizeMap());
		this.methods.add(new m_handleSmallMap());

		c_Act.methodsUsed = new int[this.methods.size()];
		c_Act.methodsSucceeded = new float[this.methods.size()];
	}

	@Override
	public AbstractGameState Decompose(AbstractGameState prevGameState, LinkedList<Task> prevTasks) {
		AbstractGameState newGameState = super.Decompose(prevGameState, prevTasks);
		if (newGameState == null || Planner.INSTANCE.GetCurrentPlanLength() == 0) {
			// System.out.println("No plan was found - getting movement action");
			newGameState = this.methods.get(this.methods.size() - 1).DecomposeTask(prevGameState, prevTasks);
		}

		return newGameState;
	}

	@Override
	public void IncreaseSuccessStatistics(float currentReward) {
		

	}

	@Override
	protected void FlushMethodRewards() {

	}

	@Override
	public void SetUCBValues(String[] values) {

	}

	@Override
	protected void IncreaseSelectionStatistics(int methodIndex) {
	}

	@Override
	public float GetExplorationValForMethod(int methodIndex) {
		
		return 0;
	}

	@Override
	public float GetExploitationValForMethod(int methodIndex) {
		return 0;
	}

	@Override
	public void PrintAllUCBValuesOfTask(PrintWriter pw) {

	}

}
