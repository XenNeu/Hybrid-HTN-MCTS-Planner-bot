/*******************************************************************************
 * Hybrid HTN MCTS Planner 
 * Created by Xenija Neufeld, 2019
 ******************************************************************************/
package PlannerMCTSBot;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import HTNPlanner.AbstractGameState;
import HTNPlanner.OrderedPlanner;
import HTNPlanner.Planner;
import HTNPlanner.PrimitiveTask;
import HTNPlanner.PrimitiveTasks.EvaluationFunctions.AttackAllEF;
import HTNPlanner.PrimitiveTasks.EvaluationFunctions.AttackMilitaryEF;
import HTNPlanner.PrimitiveTasks.EvaluationFunctions.CollectEF;
import HTNPlanner.PrimitiveTasks.EvaluationFunctions.PreventAttackAllEF;
import HTNPlanner.PrimitiveTasks.EvaluationFunctions.PreventAttackMilitaryEF;
import ai.RandomBiasedAI;
import ai.abstraction.AbstractAction;
import ai.abstraction.AbstractionLayerAI;
import ai.abstraction.Build;
import ai.abstraction.pathfinding.BFSPathFinding;
import ai.abstraction.pathfinding.PathFinding;
import ai.core.AI;
import ai.core.AIWithComputationBudget;
import ai.core.ParameterSpecification;
import ai.evaluation.EvaluationFunction;
import ai.mcts.MCTSNode;
import ai.mcts.naivemcts.NaiveMCTS;
import ai.mcts.uct.UCT;
import rts.GameState;
import rts.PhysicalGameState;
import rts.Player;
import rts.PlayerAction;
import rts.ResourceUsage;
import rts.UnitAction;
import rts.units.Unit;
import rts.units.UnitType;
import rts.units.UnitTypeTable;
import util.Helper;
import util.Pair;

public class Hybrid_HTN_MCTS_Planner_Bot extends NaiveMCTS{
	// >>>>>>>>>>>>>>>>>>>>>>>>>>Planner related data<<<<<<<<<<<<<<<<<<<<<<<
	Planner planner = null;
	Deque<PrimitiveTask> currentPlan = null;
	PrimitiveTask currentTask = null;
	// >>>>>>>>>>>>>>>>>>>>>>>>>>Game related data<<<<<<<<<<<<<<<<<<<<<<<
	UnitTypeTable m_utt = null;
	Random r = new Random();
	AI randomAI = new RandomBiasedAI();
	int framesSinceLastUpdate = 0;

	int UPDATE_FREQUENCY = 10;
	static int TIME = 100;
	static int MAX_SIMULATION_TIME = 100;

	public Hybrid_HTN_MCTS_Planner_Bot(UnitTypeTable utt) {
		this(utt, TIME, -1, MAX_SIMULATION_TIME, 100, 0.33f, 0.0f, 0.75f, new RandomBiasedAI(), new CollectEF(), true);
	}

	public Hybrid_HTN_MCTS_Planner_Bot(UnitTypeTable utt, int available_time, int max_playouts, int lookahead, int max_depth,
			float e_l, float discout_l, float e_g, float discout_g, float e_0, float discout_0, AI policy,
			EvaluationFunction a_ef, boolean fensa) {
		super(available_time, max_playouts, lookahead, max_depth, e_l, discout_l, e_g, discout_g, e_0, discout_0,
				policy, a_ef, fensa);
		Init(utt);
	}

	public Hybrid_HTN_MCTS_Planner_Bot(UnitTypeTable utt, int available_time, int max_playouts, int lookahead, int max_depth,
			float e_l, float e_g, float e_0, AI policy, EvaluationFunction a_ef, boolean fensa) {
		super(available_time, max_playouts, lookahead, max_depth, e_l, e_g, e_0, policy, a_ef, fensa);
		Init(utt);
	}

	public Hybrid_HTN_MCTS_Planner_Bot(UnitTypeTable utt, int available_time, int max_playouts, int lookahead, int max_depth,
			float e_l, float e_g, float e_0, int a_global_strategy, AI policy, EvaluationFunction a_ef, boolean fensa) {
		super(available_time, max_playouts, lookahead, max_depth, e_l, e_g, e_0, a_global_strategy, policy, a_ef,
				fensa);
		Init(utt);
	}

	private void Init(UnitTypeTable utt) {
		planner = new OrderedPlanner(utt);
		currentPlan = new LinkedList<PrimitiveTask>();

		m_utt = utt;
		Helper.BASE_TYPE = utt.getUnitType("Base");
		Helper.WORKER_TYPE = utt.getUnitType("Worker");
		Helper.BARRACKS_TYPE = utt.getUnitType("Barracks");
		Helper.HEAVY_TYPE = utt.getUnitType("Heavy");
		Helper.LIGHT_TYPE = utt.getUnitType("Light");
		Helper.RANGED_TYPE = utt.getUnitType("Ranged");
		Helper.RESOURCE_TYPE = utt.getUnitType("Resource");
		//Helper.Reset();
		
	}

	@Override
	public void reset() {
		currentPlan.clear();
		Helper.Reset();
		super.reset();
	}

	@Override
	public PlayerAction getAction(int player, GameState gs) throws Exception {
		if(gs.getTime() == 0)
		{
			Planner.INSTANCE.player = player;
			Helper.Reset();
		}
		
		long time = System.currentTimeMillis();
		long currTime = System.currentTimeMillis();
		planner.time = time;
		framesSinceLastUpdate++;

		if (Helper.OBSERVABLE == 0) {
			Helper.UpdateGameStateStats(gs);
		}

		// use this to better distribute time between HTN and MCTS
		// MCTS runs if true
		if (!gs.canExecuteAnyAction(player) && framesSinceLastUpdate >= UPDATE_FREQUENCY) {
			Helper.UpdateGameStateStats(gs);
			framesSinceLastUpdate = 0;
			return super.getAction(player, gs);
		}

		boolean decisionMade = false;
		boolean replan = false;

		AbstractGameState ags = new AbstractGameState(gs);

		// check preconditions for prev. goal - is it still valid/invalid/achieved?
		if (currentTask != null) {
			// if achieved - take next task
			if (currentTask.IsReached(player, 1 - player, gs)) {				
				// do nothing
			}
			// if still valid - proceed with MCTS
			else if (currentTask.CheckPreconditions(ags)) {
				decisionMade = true;
			}
			// if invalid
			else {
				replan = true;
			}
		}

		// potentially replan
		while (!decisionMade) {
			currTime = System.currentTimeMillis();

			// leave 20ms for MCTS?
			if (currTime - time >= Helper.MAX_TIME_FOR_PLANNER) {
				if (Helper.DEBUG_ACTION_EXECUTION) {
					System.out.println("No time for HTN - try MCTS");
				}
				return super.getAction(player, gs);
			}

			// create a new plan
			if (currentPlan.isEmpty() || replan) {
				currentPlan = planner.CreatePlan(player, gs);
			}

			// get next plan task
			if (!currentPlan.isEmpty()) {
				currentTask = currentPlan.removeFirst();

				// check whether it is still applicable
				boolean holds = currentTask.CheckPreconditions(ags);
				// it is applicable or we do not have enough time to re-plan
				if (holds || currTime - time >= Helper.MAX_TIME_FOR_PLANNER) {
					// assign evaluation function
					this.setEvaluationFunction(currentTask.GetTaskEF());
					if (Helper.DEBUG_ACTION_EXECUTION) {
						System.out.println("EF: " + this.ef.toString());
					}
					decisionMade = true;
				}
				if (!holds) {
					if (Helper.DEBUG_ACTION_EXECUTION) {
						System.out
								.println("Task " + currentTask.name + " not applicable. Re-planning.<<<<<<<<<<<<<<<<");
					}
					currentPlan.clear();
				}
			}
		}
		long plannerEndTime = System.currentTimeMillis();
		long diff = (plannerEndTime - time);

		TIME_BUDGET = (int) Math.max(1, ((TIME - diff) - 5));
		// Run MCTS for current EF
		PlayerAction action = super.getAction(player, gs);

		if (Helper.CURRENT_NUM_WORKERS == 0 && gs.canExecuteAnyAction(player) && Helper.CURRENT_NUM_MILITARY == 0
				&& gs.getPlayer(Planner.INSTANCE.player).getResources() != 0 && !Helper.MY_BASES.isEmpty()
				&& gs.getTime() <= 10) {
			Unit base = gs.getPhysicalGameState().getUnit(Helper.MY_BASES.keySet().iterator().next());

			if (action.getAction(base) == null) {
				PlayerAction alternativeAction = GetProduceAction(gs);
				if (alternativeAction != null) {
					return alternativeAction;
				}
			}
		}
		return action;
	}

	private PlayerAction GetProduceAction(GameState gs) {
		Unit base = gs.getPhysicalGameState().getUnit(Helper.MY_BASES.keySet().iterator().next());
		List<PlayerAction> children = super.tree.actions;

		for (PlayerAction pa : children) {
			for (Pair<Unit, UnitAction> uaa : pa.getActions()) {
				if (uaa.m_a.getID() == base.getID() && uaa.m_b.getType() == UnitAction.TYPE_PRODUCE
						&& uaa.m_b.getUnitType() == Helper.WORKER_TYPE) {
					return pa;
				}
			}
		}

		return null;
	}

	@Override
	public AI clone() {
		return new Hybrid_HTN_MCTS_Planner_Bot(m_utt, TIME_BUDGET, ITERATIONS_BUDGET, MAXSIMULATIONTIME, MAX_TREE_DEPTH, epsilon_l,
				discount_l, epsilon_g, discount_g, epsilon_0, discount_0, playoutPolicy, ef,
				forceExplorationOfNonSampledActions);
	}

	@Override
	public List<ParameterSpecification> getParameters() {
		return super.getParameters();
	}

}
