/*******************************************************************************
 * Hybrid HTN MCTS Planner 
 * Created by Xenija Neufeld, 2019
 ******************************************************************************/
package HTNPlanner;

import rts.GameState;
import rts.PhysicalGameState;
import util.Helper;

public class AbstractGameState {
	public GameState gameState;

	public boolean anyOppBuildingReachable = false;
	public boolean anyOppUnitReachable = false;

	public int currentNumResources = 0;
	public int currentNumWorkers = 0;
	public int currentNumBases = 0;
	public int currentNumBarracks = 0;
	public int currentNumMilitary = 0;

	// this should be used for the actual gameState
	public AbstractGameState(GameState gs) {
		gameState = gs;
		anyOppBuildingReachable = Helper.ANY_OPP_BUILDING_REACHABLE;
		anyOppUnitReachable = Helper.ANY_OPP_UNIT_REACHABLE;
		currentNumResources = gs.getPlayer(Planner.INSTANCE.player).getResources();
		currentNumWorkers = Helper.CURRENT_NUM_WORKERS;
		currentNumBases = Helper.CURRENT_NUM_BASES;
		currentNumBarracks = Helper.CURRENT_NUM_BARRACKS;
		currentNumMilitary = Helper.CURRENT_NUM_MILITARY;
	}

	// this should be used for effect propagation
	public AbstractGameState(GameState gs, int numRes, int numWorkers, int numBases, int numBarracks, int numMilitary,
			boolean buildingReachable, boolean unitReachable) {
		gameState = gs.clone();
		currentNumResources = numRes;
		currentNumWorkers = numWorkers;
		currentNumBases = numBases;
		currentNumBarracks = numBarracks;
		currentNumMilitary = numMilitary;
		anyOppBuildingReachable = buildingReachable;
		anyOppUnitReachable = unitReachable;
	}

	public AbstractGameState clone() {
		AbstractGameState ags = new AbstractGameState(this.gameState, this.currentNumResources, this.currentNumWorkers,
				this.currentNumBases, this.currentNumBarracks, this.currentNumMilitary, this.anyOppBuildingReachable,
				this.anyOppUnitReachable);
		return ags;
	}

}
