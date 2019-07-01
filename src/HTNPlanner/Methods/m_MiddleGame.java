/*******************************************************************************
 * Hybrid HTN MCTS Planner 
 * Created by Xenija Neufeld, 2019
 ******************************************************************************/
package HTNPlanner.Methods;

import HTNPlanner.AbstractGameState;
import HTNPlanner.Method;
import HTNPlanner.Planner;
import HTNPlanner.CompoundTasks.c_MiddleGame;
import HTNPlanner.PrimitiveTasks.*;
import rts.GameState;
import rts.units.Unit;
import util.Helper;

public class m_MiddleGame extends Method {
	public m_MiddleGame() {
		super();

		this.name = "m_MiddleGame";

		this.tasksToDecompose.add(new c_MiddleGame());
	}

	@Override
	public boolean CheckPreconditions(AbstractGameState currentGameState) {
		boolean holds = false;

		// if enough recourses to start building
		if (currentGameState.currentNumResources >= Helper.DESIRED_NUM_RES
				&& currentGameState.currentNumWorkers >= Helper.DESIRED_NUM_WORKERS) {
			return true;
		}

		if (currentGameState.currentNumResources >= Helper.DESIRED_NUM_RES
				&& currentGameState.currentNumBarracks >= Helper.DESIRED_NUM_BARRACKS) {
			return true;
		}

		if (currentGameState.currentNumWorkers >= Helper.DESIRED_NUM_WORKERS) {
			return true;
		}

		if (currentGameState.currentNumBarracks >= Helper.DESIRED_NUM_BARRACKS
				&& currentGameState.currentNumWorkers > 1) {
			return true;
		}
		// if cannot reach the opponent to attack him - build
		if (!Helper.OPP_BUILDINGS.isEmpty() && !currentGameState.anyOppBuildingReachable
				&& currentGameState.currentNumWorkers > 1) {
			return true;
		}

		if (currentGameState.currentNumResources < 1 && currentGameState.currentNumWorkers < 1
				&& Helper.CURRENT_NUM_WORKERS + Helper.CURRENT_NUM_MILITARY < Helper.CURRENT_NUM_OPP_MOBILE_UNITS + 3) {
			return true;
		}

		return holds;
	}
}