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
import HTNPlanner.Methods.m_BuildAndDefendSmall;
import HTNPlanner.Methods.m_EndGame;
import HTNPlanner.Methods.m_MiddleGame;
import HTNPlanner.Methods.m_Opening;
import HTNPlanner.Methods.m_PreventAttackAll;
import HTNPlanner.Methods.m_PreventAttackMilitary;
import util.Helper;

public class c_MidsizeMap extends CompoundTask {

	// how often each method was used to decompose this task
	protected static int[] methodsUsed;

	// how often each method was used to decompose this task
	protected static float[] methodsSucceeded;

	protected static int selected;

	protected static float succeeded;

	protected static float maxReward = Helper.EPSILON;

	public c_MidsizeMap() {
		super();

		this.name = "c_MidsizeMap";

		this.methods.add(new m_EndGame());
		this.methods.add(new m_PreventAttackAll());
		this.methods.add(new m_MiddleGame());
		this.methods.add(new m_Opening());

		c_MidsizeMap.methodsUsed = new int[this.methods.size()];
		c_MidsizeMap.methodsSucceeded = new float[this.methods.size()];
	}

	@Override
	public AbstractGameState Decompose(AbstractGameState prevGameState, LinkedList<Task> prevTasks) {
		AbstractGameState newGameState = super.Decompose(prevGameState, prevTasks);
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