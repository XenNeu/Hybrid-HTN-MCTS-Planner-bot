/*******************************************************************************
 * Hybrid HTN MCTS Planner 
 * Created by Xenija Neufeld, 2019
 ******************************************************************************/
package HTNPlanner.PrimitiveTasks;

import HTNPlanner.AbstractGameState;
import HTNPlanner.PrimitiveTask;
import HTNPlanner.PrimitiveTasks.EvaluationFunctions.PreventAttackAllEF;
import HTNPlanner.PrimitiveTasks.EvaluationFunctions.PreventAttackMilitaryEF;
import rts.GameState;
import rts.PhysicalGameState;
import rts.units.Unit;
import util.Helper;

public class p_PreventAttackMilitary extends PrimitiveTask {
	public p_PreventAttackMilitary() {
		super();

		this.name = "p_PreventAttackMilitary";

		this.taskEF = new PreventAttackMilitaryEF();
	}

	public boolean CheckPreconditions(AbstractGameState currentGameState) {
		boolean holds = false;

		if (currentGameState.currentNumBases <= 0) {
			return false;
		}
		if (currentGameState.currentNumMilitary < 3) {
			holds = false;
			if (Helper.DEBUG_PRECONDITIONS) {
				System.out.println(this.name + " does not hold: not enough military units");
			}
			return holds;
		}

		if(Helper.MAX_MAP_DIST < 64)
		{
			holds = false;
			if (Helper.DEBUG_PRECONDITIONS) 
			{
				System.out.println(this.name + " does not hold: the map is too small");
			}
			return holds;
		}
		
		
		if (currentGameState.currentNumBases > 0 && !Helper.PER_BASE_CLOSEST_OPP_INFOS.isEmpty()) {
			PhysicalGameState pgs = currentGameState.gameState.getPhysicalGameState();

			for (long baseID : Helper.PER_BASE_CLOSEST_OPP_INFOS.keySet()) {
				int dist = Integer.MAX_VALUE;

				Unit base = pgs.getUnit(baseID);
				Unit opp = Helper.PER_BASE_CLOSEST_OPP_INFOS.get(baseID);
				if (base != null && opp != null) {
					dist = Math.abs(base.getX() - opp.getX()) + Math.abs(base.getY() - opp.getY());
				}
				if (dist < (float) (Helper.MAX_MAP_DIST / 4)) {
					holds = true;
					break;
				}
			}
		}

		if (!holds && Helper.DEBUG_PRECONDITIONS) {
			System.out.println(this.name + " does not hold: no enemy is close");
		}

		return holds;
	}

	public boolean IsReached(int maxplayer, int minplayer, GameState currentGameState) {
		boolean reached = true;

		if (Helper.CURRENT_NUM_BASES > 0 && !Helper.PER_BASE_CLOSEST_OPP_INFOS.isEmpty()) {
			PhysicalGameState pgs = currentGameState.getPhysicalGameState();

			for (long baseID : Helper.PER_BASE_CLOSEST_OPP_INFOS.keySet()) {
				int dist = Integer.MAX_VALUE;

				Unit base = pgs.getUnit(baseID);
				Unit opp = Helper.PER_BASE_CLOSEST_OPP_INFOS.get(baseID);
				if (base != null && opp != null) {
					dist = Math.abs(base.getX() - opp.getX()) + Math.abs(base.getY() - opp.getY());
				}
				if (dist < (float) (Helper.MAX_MAP_DIST / 4)) {
					reached = false;
					return reached;
				}
			}
		}

		if (reached && Helper.DEBUG_POSTCONDITIONS) {
			System.out.println(this.name + " reached");
		}

		return reached;
	}

	@Override
	public AbstractGameState Simulate(AbstractGameState prevGameState) {
		AbstractGameState agsCopy = super.Simulate(prevGameState);

		return agsCopy;
	}
}