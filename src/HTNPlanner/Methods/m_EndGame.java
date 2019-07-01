/*******************************************************************************
 * Hybrid HTN MCTS Planner 
 * Created by Xenija Neufeld, 2019
 ******************************************************************************/
package HTNPlanner.Methods;

import HTNPlanner.AbstractGameState;
import HTNPlanner.Method;
import HTNPlanner.Planner;
import HTNPlanner.CompoundTasks.c_EndGame;
import HTNPlanner.PrimitiveTasks.*;
import rts.GameState;
import util.Helper;

public class m_EndGame extends Method {
	public m_EndGame() {
		super();

		this.name = "m_EndGame";

		this.tasksToDecompose.add(new c_EndGame());
	}

	@Override
	public boolean CheckPreconditions(AbstractGameState currentGameState) {
		boolean holds = false;

		// if small map and have mobile units
		if (currentGameState.gameState.getPhysicalGameState().getWidth() < 16
				&& currentGameState.currentNumWorkers >= Helper.DESIRED_NUM_WORKERS) {
			return true;
		}

		// if big map and enough military units and at least bases/barracks
		if (currentGameState.currentNumBases >= Helper.DESIRED_NUM_BASES
				&& currentGameState.currentNumBarracks >= Helper.DESIRED_NUM_BARRACKS
				&& currentGameState.currentNumMilitary >= Helper.DESIRED_NUM_MILITARY
				&& currentGameState.anyOppBuildingReachable) {
			return true;
		}

		// or if i cannot build anymore - try attacking as last option
		if (currentGameState.currentNumBases == 0
				|| (currentGameState.currentNumResources < 1 && currentGameState.currentNumWorkers < 1)) {
			return true;
		}

		if (Helper.PER_BASE_CLOSEST_RES_INFOS.isEmpty()) {
			return true;
		}

		return holds;
	}
}
