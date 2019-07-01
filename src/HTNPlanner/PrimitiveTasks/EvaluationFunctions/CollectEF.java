/*******************************************************************************
 * Hybrid HTN MCTS Planner 
 * Created by Xenija Neufeld, 2019
 ******************************************************************************/
package HTNPlanner.PrimitiveTasks.EvaluationFunctions;

import EA.CollectEFIndividual;
import EA.Generator;
import HTNPlanner.Planner;
import Test.TournamentFromArchive;
import ai.evaluation.EvaluationFunction;
import rts.GameState;
import rts.PhysicalGameState;
import rts.UnitAction;
import rts.UnitActionAssignment;
import rts.units.Unit;
import rts.units.UnitType;
import rts.units.UnitTypeTable;
import util.Helper;
import util.Pair;

public class CollectEF extends EvaluationFunction {

	public float RESOURCE = 0.37877777f;
	public float RESOURCE_COLLECTION = 0.04026464f;
	public float W_WORKER = 0.324173f;
	public float W_DIST = 0.5645752f;
	public float W_BARRACKS = 0.5766494f;
	
	public CollectEF() 
	{
		int mapsize = Helper.MAP_SIZE;

		// Get current weights from EA
		if (Helper.TRAINING_MODE_ON) 
		{
			if (Generator.FUNCTION_CLASS_TO_TRAIN.equals(CollectEF.class)) 
			{
				RESOURCE = Generator.CURRENT_GENES[0].weightValue;
				RESOURCE_COLLECTION = Generator.CURRENT_GENES[1].weightValue;
				W_WORKER = Generator.CURRENT_GENES[2].weightValue;
				W_DIST = Generator.CURRENT_GENES[3].weightValue;
				W_BARRACKS = Generator.CURRENT_GENES[4].weightValue;
				return;
			} 
			else if (Helper.USE_WEIGHTS_FROM_ARCHIVE) 
			{
				if (Generator.CURRENT_INDIVIDUALS[0] != null) 
				{

					RESOURCE = Generator.CURRENT_INDIVIDUALS[0].genes[0].weightValue;
					RESOURCE_COLLECTION = Generator.CURRENT_INDIVIDUALS[0].genes[1].weightValue;
					W_WORKER = Generator.CURRENT_INDIVIDUALS[0].genes[2].weightValue;
					W_DIST = Generator.CURRENT_INDIVIDUALS[0].genes[3].weightValue;
					W_BARRACKS = Generator.CURRENT_INDIVIDUALS[0].genes[4].weightValue;
					return;
				}
			}
		}

		if (Helper.USE_WEIGHTS_FROM_ARCHIVE && !Helper.TRAINING_MODE_ON) 
		{
			if (TournamentFromArchive.CURRENT_INDIVIDUALS[0] != null) 
			{
				RESOURCE = TournamentFromArchive.CURRENT_INDIVIDUALS[0].genes[0].weightValue;
				RESOURCE_COLLECTION = TournamentFromArchive.CURRENT_INDIVIDUALS[0].genes[1].weightValue;
				W_WORKER = TournamentFromArchive.CURRENT_INDIVIDUALS[0].genes[2].weightValue;
				W_DIST = TournamentFromArchive.CURRENT_INDIVIDUALS[0].genes[3].weightValue;
				W_BARRACKS = TournamentFromArchive.CURRENT_INDIVIDUALS[0].genes[4].weightValue;
				return;
			} 
		}

		if (mapsize == 0) {
			//39_3 <<
			RESOURCE = 0.9139032f;
			RESOURCE_COLLECTION = 0.057641707f;
			W_WORKER = 0.58428717f;
			W_DIST = 0.9070341f;
			W_BARRACKS = 0.8106673f;
		} else if (mapsize == 1) {
			//39_3 
			RESOURCE = 0.9139032f;
			RESOURCE_COLLECTION = 0.057641707f;
			W_WORKER = 0.58428717f;
			W_DIST = 0.9070341f;
			W_BARRACKS = 0.8106673f;
		} else if (mapsize == 2) {
			//52_7 
			RESOURCE = 0.37877777f;
			RESOURCE_COLLECTION = 0.04026464f;
			W_WORKER = 0.0f;
			W_DIST = 0.7645752f;
			W_BARRACKS = 0.0f;
		}	
	}

	@Override
	public float evaluate(int maxplayer, int minplayer, GameState gs) {
		long time = System.currentTimeMillis();

		float baseScoreMax = base_score(maxplayer, gs);
		float score = baseScoreMax;// - baseScoreMin;

		return score;
	}

	public float base_score(int player, GameState gs) {
		PhysicalGameState pgs = gs.getPhysicalGameState();

		float res_score = gs.getPlayer(player).getResources();
		float res_collection_score = 0.0f;

		float[] my_score_nr_workers = { 0.0f, 0.0f };
		float distScore = 0.0f;

		float score_barracks = 0.0f;

		float prod_score = 0.0f;

		UnitTypeTable utt = gs.getUnitTypeTable();

		for (Unit u : pgs.getUnits()) {
			int meOrOpp = (player == Planner.INSTANCE.player && u.getPlayer() == player) ? 0 : 1;

			// for collection, consider only my units
			if (meOrOpp == 1) {
				continue;
			}

			UnitActionAssignment uaa = gs.getActionAssignment(u);

			if (u.getType() == Helper.BARRACKS_TYPE || (uaa != null && uaa.action.getType() == UnitAction.TYPE_PRODUCE
					&& uaa.action.getUnitType() == Helper.BARRACKS_TYPE)) {
				score_barracks += 1;
				res_score -= Helper.BARRACKS_TYPE.cost;
			}

			if (uaa != null && uaa.action.getType() == UnitAction.TYPE_PRODUCE
					&& uaa.action.getUnitType() == Helper.BASE_TYPE) {
				res_score -= Helper.BASE_TYPE.cost;
			}

			// continue collecting
			if (u.getType() == Helper.WORKER_TYPE) {
				my_score_nr_workers[0] += W_WORKER * (u.getHitPoints() / u.getMaxHitPoints());
				my_score_nr_workers[1] += 1;

				Unit closestBase = null;
				Unit closestRes = null;

				Long baseID = Helper.GetBaseIDForUnit(u, gs, false);
				Long resID = null;

				int[][] resFF = null;

				if (baseID != null) {
					Pair<Long, int[][]> resInfo = Helper.PER_BASE_CLOSEST_RES_INFOS.get(baseID);
					if (resInfo != null) {
						resID = resInfo.m_a;
						if (Helper.MAP_SIZE == 2) {
							resFF = resInfo.m_b;
						}
					}
				}

				closestBase = pgs.getUnit(baseID);

				if (resID != null) {
					closestRes = pgs.getUnit(resID);
				}

				if (u.getResources() > 0 && closestBase != null) {
					float currentDist = (Math.abs(u.getX() - closestBase.getX())
							+ Math.abs(u.getY() - closestBase.getY()));
					distScore += (Helper.MAX_MAP_DIST - currentDist);
					res_collection_score += u.getResources() * RESOURCE_COLLECTION;
				} else if (u.getResources() == 0 && closestRes != null) {
					float currentDist = (Math.abs(u.getX() - closestRes.getX())
							+ Math.abs(u.getY() - closestRes.getY()));
					if (resFF != null) {
						currentDist = resFF[u.getX()][u.getY()];
					}
					distScore += (Helper.MAX_MAP_DIST - currentDist);
				}

			}
		}

		float total_score = 0.0f;

		res_score = res_score < Helper.DESIRED_NUM_RES ? (res_score / Helper.DESIRED_NUM_RES) : 1;
		res_score *= RESOURCE;

		res_collection_score /= my_score_nr_workers[1];

		total_score += res_score;
		total_score += res_collection_score;

		float score_my_workers = my_score_nr_workers[1] == 0 ? 0 : my_score_nr_workers[0] / my_score_nr_workers[1]; // +opp_score_nr_workers[1])

		if (Helper.CURRENT_NUM_WORKERS == 0 && my_score_nr_workers[1] > 0) {
			score_my_workers *= 5;
		}
		total_score += score_my_workers;

		distScore /= my_score_nr_workers[1];
		distScore /= Helper.MAX_MAP_DIST;
		distScore *= W_DIST;
		total_score += distScore;

		// normalize total
		total_score /= 4;

		if (Helper.DEBUG_EF_VALUES) {
			System.out.println("sc res: " + res_score + ", collection: " + res_collection_score + ", my barracks: "
					+ score_barracks + ", my workers: " + score_my_workers + ", dist: " + distScore + " --- total: "
					+ total_score);
		}

		return total_score;
	}

	public float upperBound(GameState gs) {
		return 1;
	}

}
