/*******************************************************************************
 * Hybrid HTN MCTS Planner 
 * Created by Xenija Neufeld, 2019
 ******************************************************************************/
package HTNPlanner.Methods;

import HTNPlanner.AbstractGameState;
import HTNPlanner.Method;
import HTNPlanner.Planner;
import HTNPlanner.CompoundTasks.*;
import HTNPlanner.PrimitiveTasks.*;
import rts.GameState;
import util.Helper;

public class m_Opening extends Method {
	public m_Opening() {
		super();

		this.name = "m_Opening";

		this.tasksToDecompose.add(new c_Opening());
	}

	@Override
	public boolean CheckPreconditions(AbstractGameState currentGameState) {
		boolean holds = false;

		// exploration required
		if (Helper.OPP_BUILDINGS.size() < 0 && Helper.OBSERVABLE < 0) {
			return true;
		}

		if (currentGameState.currentNumResources < 1 && currentGameState.currentNumWorkers < 1) {
			return false;
		}

		if (currentGameState.currentNumResources < Helper.DESIRED_NUM_RES
				|| currentGameState.currentNumWorkers < Helper.DESIRED_NUM_WORKERS) {
			return true;
		}

		// no mobile units
		if (currentGameState.currentNumWorkers <= 0 && currentGameState.currentNumMilitary < 5
				&& currentGameState.currentNumBases > 0 && currentGameState.currentNumResources > 0) {
			return true;
		}

		// big map, many res, opponent far away
		if (currentGameState.currentNumResources < Helper.DESIRED_NUM_RES
				&& currentGameState.currentNumBarracks < Helper.DESIRED_NUM_BARRACKS
				&& currentGameState.currentNumMilitary < Helper.DESIRED_NUM_MILITARY) {
			return true;
		}

		if (currentGameState.currentNumWorkers
				+ currentGameState.currentNumMilitary < Helper.CURRENT_NUM_OPP_MOBILE_UNITS) {
			return true;
		}

		return holds;
	}
}