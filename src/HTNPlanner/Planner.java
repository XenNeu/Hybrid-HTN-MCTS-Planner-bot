/*******************************************************************************
 * Hybrid HTN MCTS Planner 
 * Created by Xenija Neufeld, 2019
 ******************************************************************************/
package HTNPlanner;

import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

import java.util.Vector;

import HTNPlanner.CompoundTasks.*;
import HTNPlanner.PrimitiveTasks.*;
import rts.GameState;
import rts.PhysicalGameState;
import rts.units.Unit;
import rts.units.UnitType;
import rts.units.UnitTypeTable;
import util.Helper;

public abstract class Planner {
	public Deque<PrimitiveTask> plan;

	protected Task goalTask;

	protected UnitTypeTable utt = null;

	public int player;

	public static Planner INSTANCE;

	public long time;
	public long secondsLeft;

	public boolean initialized = false;

	public Planner(UnitTypeTable utt) {
		Planner.INSTANCE = this;
		this.utt = utt;
		this.plan = new LinkedList<PrimitiveTask>();

	}

	public Deque<PrimitiveTask> CreatePlan(int player, GameState gameState) {
		// set initial task
		if(this.goalTask == null)
		{
			this.goalTask = new c_Act();
		}

		GameState gs2 = gameState.clone();
		AbstractGameState ags = new AbstractGameState(gs2);
		this.player = player;

		if (Helper.DESIRED_NUM_WORKERS == 0) {
			Helper.ComputeDesiredUnitNumbers(gameState);
		}

		this.plan.clear();
		// reset temp. vars

		LinkedList<Task> linkedTasks = new LinkedList<Task>();

		// search for a plan
		this.goalTask.Decompose(ags, linkedTasks);

		if (this.plan.size() == 0) {
			if (Helper.DEBUG_ACTION_EXECUTION) {
				System.out.println("No plan could be found");
			}
		} else {
			String pl = "new plan: ";
			for (Iterator<PrimitiveTask> i = this.plan.iterator(); i.hasNext();) {
				pl += i.next().name;
				pl += ", ";
			}
			if (Helper.DEBUG_ACTION_EXECUTION) {
				System.out.println(pl);
			}
		}
		return this.plan;
	}

	public abstract Method SelectNextMethod(CompoundTask cTask, AbstractGameState gameState, int nextIndex,
			int[] methodsTried);

	public abstract Method SelectNextMethodUCB(CompoundTask cTask, AbstractGameState gameState, int nextIndex,
			int[] methodsTried);

	public int GetCurrentPlanLength() {
		return Planner.INSTANCE.plan.size();
	}

	public void AddPlanStep(PrimitiveTask task) {
		if (Helper.DEBUG_PLAN) {
			System.out.println("+++ Task " + task.name + " added to the plan.");
		}
		Planner.INSTANCE.plan.addLast(task);
	}

	public void RemoveLastPlanStep() {
		PrimitiveTask removed = Planner.INSTANCE.plan.removeLast();
		if (Helper.DEBUG_PLAN) {
			System.out.println("Task " + removed.name + " removed from the plan.");
		}
	}

}
