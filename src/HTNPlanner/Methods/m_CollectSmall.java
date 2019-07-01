/*******************************************************************************
 * Hybrid HTN MCTS Planner 
 * Created by Xenija Neufeld, 2019
 ******************************************************************************/
package HTNPlanner.Methods;

import java.util.Vector;

import rts.GameState;
import util.Helper;
import util.Pair;
import HTNPlanner.AbstractGameState;
import HTNPlanner.Method;
import HTNPlanner.Planner;
import HTNPlanner.CompoundTasks.*;
import HTNPlanner.PrimitiveTasks.*;

public class m_CollectSmall extends Method {
	public m_CollectSmall() {
		super();

		this.name = "m_Collect";

		this.tasksToDecompose.add(new p_CollectSmall());
		this.tasksToDecompose.add(new c_EndGame());
	}

	@Override
	public boolean CheckPreconditions(AbstractGameState currentGameState) {
		boolean holds = true;

		if (Helper.MAX_MAP_DIST >= 31) {
			return false;
		}

		if (Helper.OBSERVABLE < 0) {
			return false;
		}

		return holds;
	}
}
