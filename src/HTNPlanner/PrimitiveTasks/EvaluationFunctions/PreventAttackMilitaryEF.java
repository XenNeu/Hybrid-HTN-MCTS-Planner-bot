/*******************************************************************************
 * Hybrid HTN MCTS Planner 
 * Created by Xenija Neufeld, 2019
 ******************************************************************************/
package HTNPlanner.PrimitiveTasks.EvaluationFunctions;

import EA.Generator;
import HTNPlanner.Planner;
import Test.TournamentFromArchive;
import ai.evaluation.EvaluationFunction;
import rts.GameState;
import rts.PhysicalGameState;
import rts.units.Unit;
import rts.units.UnitTypeTable;
import util.Helper;
import util.Pair;

public class PreventAttackMilitaryEF extends EvaluationFunction 
{
	public float RESOURCE = 0.68607f;
	public float RESOURCE_COLLECTION = 0.22535f;

	public float W_BASE = 0.72532f;
	public float W_BARRACKS = 0.691834f;
	public float[] W_WORKER = new float[] { 0.715388f, -0.597434f };
	public float[] W_LIGHT = new float[] { 0.191363f, -0.990601f };
	public float[] W_RANGE = new float[] { 0.192451f, -0.959466f };
	public float[] W_HEAVY = new float[] { 0.167399f, -0.948596f };
	public float W_DIST = 3.973707f;

	public PreventAttackMilitaryEF() 
	{
		int mapsize = Helper.MAP_SIZE;

		// Get current weights from EA
		if (Helper.TRAINING_MODE_ON) 
		{
			if (Generator.FUNCTION_CLASS_TO_TRAIN.equals(PreventAttackMilitaryEF.class)) 
			{
				RESOURCE = Generator.CURRENT_GENES[0].weightValue;
				RESOURCE_COLLECTION = Generator.CURRENT_GENES[1].weightValue;

				W_BASE = Generator.CURRENT_GENES[2].weightValue;
				W_BARRACKS = Generator.CURRENT_GENES[3].weightValue;
				W_WORKER[0] = Generator.CURRENT_GENES[4].weightValue;
				W_WORKER[1] = -Generator.CURRENT_GENES[5].weightValue;
				W_LIGHT[0] = Generator.CURRENT_GENES[6].weightValue;
				W_LIGHT[1] = -Generator.CURRENT_GENES[7].weightValue;
				W_RANGE[0] = Generator.CURRENT_GENES[8].weightValue;
				W_RANGE[1] = -Generator.CURRENT_GENES[9].weightValue;
				W_HEAVY[0] = Generator.CURRENT_GENES[10].weightValue;
				W_HEAVY[1] = -Generator.CURRENT_GENES[11].weightValue;

				W_DIST = Generator.CURRENT_GENES[12].weightValue;
				return;
			} 
			else if (Helper.USE_WEIGHTS_FROM_ARCHIVE) 
			{
				if (Generator.CURRENT_INDIVIDUALS[6] != null) 
				{
					RESOURCE = Generator.CURRENT_INDIVIDUALS[6].genes[0].weightValue;
					RESOURCE_COLLECTION = Generator.CURRENT_INDIVIDUALS[6].genes[1].weightValue;

					W_BASE = Generator.CURRENT_INDIVIDUALS[6].genes[2].weightValue;
					W_BARRACKS = Generator.CURRENT_INDIVIDUALS[6].genes[3].weightValue;
					W_WORKER[0] = Generator.CURRENT_INDIVIDUALS[6].genes[4].weightValue;
					W_WORKER[1] = -Generator.CURRENT_INDIVIDUALS[6].genes[5].weightValue;
					W_LIGHT[0] = Generator.CURRENT_INDIVIDUALS[6].genes[6].weightValue;
					W_LIGHT[1] = -Generator.CURRENT_INDIVIDUALS[6].genes[7].weightValue;
					W_RANGE[0] = Generator.CURRENT_INDIVIDUALS[6].genes[8].weightValue;
					W_RANGE[1] = -Generator.CURRENT_INDIVIDUALS[6].genes[9].weightValue;
					W_HEAVY[0] = Generator.CURRENT_INDIVIDUALS[6].genes[10].weightValue;
					W_HEAVY[1] = -Generator.CURRENT_INDIVIDUALS[6].genes[11].weightValue;

					W_DIST = Generator.CURRENT_INDIVIDUALS[6].genes[12].weightValue;
					return;
				}
			}
		}

		if (Helper.USE_WEIGHTS_FROM_ARCHIVE && !Helper.TRAINING_MODE_ON) 
		{
			if (TournamentFromArchive.CURRENT_INDIVIDUALS[6] != null) 
			{

				RESOURCE = TournamentFromArchive.CURRENT_INDIVIDUALS[6].genes[0].weightValue;
				RESOURCE_COLLECTION = TournamentFromArchive.CURRENT_INDIVIDUALS[6].genes[1].weightValue;

				W_BASE = TournamentFromArchive.CURRENT_INDIVIDUALS[6].genes[2].weightValue;
				W_BARRACKS = TournamentFromArchive.CURRENT_INDIVIDUALS[6].genes[3].weightValue;
				W_WORKER[0] = TournamentFromArchive.CURRENT_INDIVIDUALS[6].genes[4].weightValue;
				W_WORKER[1] = -TournamentFromArchive.CURRENT_INDIVIDUALS[6].genes[5].weightValue;
				W_LIGHT[0] = TournamentFromArchive.CURRENT_INDIVIDUALS[6].genes[6].weightValue;
				W_LIGHT[1] = -TournamentFromArchive.CURRENT_INDIVIDUALS[6].genes[7].weightValue;
				W_RANGE[0] = TournamentFromArchive.CURRENT_INDIVIDUALS[6].genes[8].weightValue;
				W_RANGE[1] = -TournamentFromArchive.CURRENT_INDIVIDUALS[6].genes[9].weightValue;
				W_HEAVY[0] = TournamentFromArchive.CURRENT_INDIVIDUALS[6].genes[10].weightValue;
				W_HEAVY[1] = -TournamentFromArchive.CURRENT_INDIVIDUALS[6].genes[11].weightValue;

				W_DIST = TournamentFromArchive.CURRENT_INDIVIDUALS[6].genes[12].weightValue;
				return;
			} 
		}

		//used on big maps only		
		RESOURCE = 0.68607f;
		RESOURCE_COLLECTION = 0.22535f;

		W_BASE = 0.72532f;
		W_BARRACKS = 0.691834f;
		W_WORKER = new float[] { 0.715388f, -0.597434f };
		W_LIGHT = new float[] { 0.191363f, -0.990601f };
		W_RANGE = new float[] { 0.192451f, -0.959466f };
		W_HEAVY = new float[] { 0.167399f, -0.948596f };
		W_DIST = 3.973707f;
	}

	@Override
	public float evaluate(int maxplayer, int minplayer, GameState gs) {
		float baseScoreMax = base_score(maxplayer, gs);
		float score = baseScoreMax;// - baseScoreMin;
		return score;
	}

	public float base_score(int player, GameState gs) {
		PhysicalGameState pgs = gs.getPhysicalGameState();

		float[] my_score_nr_bases = { 0.0f, 0.0f };

		float[] my_score_nr_barracks = { 0.0f, 0.0f };

		float[] my_score_nr_workers = { 0.0f, 0.0f };
		float[] opp_score_nr_workers = { 0.0f, 0.0f };

		float[] my_score_nr_light = { 0.0f, 0.0f };
		float[] opp_score_nr_light = { 0.0f, 0.0f };

		float[] my_score_nr_ranged = { 0.0f, 0.0f };
		float[] opp_score_nr_ranged = { 0.0f, 0.0f };

		float[] my_score_nr_heavy = { 0.0f, 0.0f };
		float[] opp_score_nr_heavy = { 0.0f, 0.0f };

		float nr_mobileUnits = 0.0f;
		float distScore = 0.0f;

		float res_score = gs.getPlayer(player).getResources();
		float res_collection_score = 0.0f;

		UnitTypeTable utt = gs.getUnitTypeTable();

		for (Unit u : pgs.getUnits()) {
			int meOrOpp = (player == Planner.INSTANCE.player && u.getPlayer() == player) ? 0 : 1;

			// UNITS
			if (u.getType() == Helper.BASE_TYPE) {
				if (meOrOpp == 0) {
					my_score_nr_bases[0] += (u.getHitPoints() / u.getMaxHitPoints());
					my_score_nr_bases[1] += 1;
				}
				continue;
			} else if (u.getType() == Helper.BARRACKS_TYPE) {

				if (meOrOpp == 0) {
					my_score_nr_barracks[0] += (u.getHitPoints() / u.getMaxHitPoints());
					my_score_nr_barracks[1] += 1;
				}
				continue;
			} else if (u.getType() != Helper.RESOURCE_TYPE) {

				if (meOrOpp == 0) {
					Long oppID = null;
					Unit opp = null;
					Long baseID = Helper.GetBaseIDForUnit(u, gs, false);
					if (Helper.MY_BASES != null) {
						if (!Helper.PER_BASE_CLOSEST_OPP_INFOS.isEmpty() && u.getType() != Helper.WORKER_TYPE) {
							if (baseID != null) {
								opp = Helper.PER_BASE_CLOSEST_OPP_INFOS.get(baseID);
							}

							if (opp != null) {
								float currentDist = (Math.abs(u.getX() - opp.getX()) + Math.abs(u.getY() - opp.getY()));
								distScore += (Helper.MAX_MAP_DIST - currentDist);
								// count mobile units
								nr_mobileUnits += 1;
							}

						}
					}
				}

				if (u.getType() == Helper.WORKER_TYPE) {
					if (meOrOpp == 0) {
						my_score_nr_workers[0] += W_WORKER[meOrOpp] * (u.getHitPoints() / u.getMaxHitPoints());
						my_score_nr_workers[1]++;

						Unit closestBase = null;
						Unit closestRes = null;
						int[][] resFF = null;
						int[][] baseFF = null;
						Long baseID = Helper.GetBaseIDForUnit(u, gs, false);
						Long resID = null;
						if (baseID != null) {
							Pair<Long, int[][]> resInfo = Helper.PER_BASE_CLOSEST_RES_INFOS.get(baseID);
							if (resInfo != null) {
								resID = resInfo.m_a;
								if (Helper.MAP_SIZE == 2) {
									resFF = resInfo.m_b;
								}
							}
						}

						if (baseID != null) {
							closestBase = pgs.getUnit(baseID);
							if (Helper.MAP_SIZE == 2) {
								baseFF = Helper.MY_BASES_FLOODFILL.get(baseID);
							}
						}
						if (resID != null) {
							closestRes = pgs.getUnit(resID);
						}

						if (u.getResources() > 0 && closestBase != null) {
							float currentDist = (Math.abs(u.getX() - closestBase.getX())
									+ Math.abs(u.getY() - closestBase.getY()));
							if (baseFF != null) {
								currentDist = baseFF[u.getX()][u.getY()];
							}
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
						// count mobile units
						nr_mobileUnits += 1;
					} else {
						opp_score_nr_workers[0] += W_WORKER[meOrOpp] * (u.getHitPoints() / u.getMaxHitPoints());
						opp_score_nr_workers[1]++;
					}
				} else if (u.getType() == Helper.LIGHT_TYPE) {
					if (meOrOpp == 0) {
						my_score_nr_light[0] += W_LIGHT[meOrOpp] * (u.getHitPoints() / u.getMaxHitPoints());
						my_score_nr_light[1]++;
					} else {
						opp_score_nr_light[0] += W_LIGHT[meOrOpp] * (u.getHitPoints() / u.getMaxHitPoints());
						opp_score_nr_light[1]++;
					}
				} else if (u.getType() == Helper.RANGED_TYPE) {
					if (meOrOpp == 0) {
						my_score_nr_ranged[0] += W_RANGE[meOrOpp] * (u.getHitPoints() / u.getMaxHitPoints());
						my_score_nr_ranged[1]++;
					} else {
						opp_score_nr_ranged[0] += W_RANGE[meOrOpp] * (u.getHitPoints() / u.getMaxHitPoints());
						opp_score_nr_ranged[1]++;
					}
				} else if (u.getType() == Helper.HEAVY_TYPE) {
					if (meOrOpp == 0) {
						my_score_nr_heavy[0] += W_HEAVY[meOrOpp] * (u.getHitPoints() / u.getMaxHitPoints());
						my_score_nr_heavy[1]++;
					} else {
						opp_score_nr_heavy[0] += W_HEAVY[meOrOpp] * (u.getHitPoints() / u.getMaxHitPoints());
						opp_score_nr_heavy[1]++;
					}
				}
			}

		}

		float total_score = 0.0f;

		my_score_nr_bases[0] = my_score_nr_bases[0] < Helper.DESIRED_NUM_BASES
				? (float) (my_score_nr_bases[0] / Helper.DESIRED_NUM_BASES)
				: 1;
		my_score_nr_barracks[0] = my_score_nr_barracks[0] < Helper.DESIRED_NUM_BARRACKS
				? (float) (my_score_nr_barracks[0] / Helper.DESIRED_NUM_BARRACKS)
				: 1;
		my_score_nr_bases[0] = W_BASE * my_score_nr_bases[0];
		my_score_nr_barracks[0] = W_BARRACKS * my_score_nr_barracks[0];

		float score_my_workers = my_score_nr_workers[1] == 0 ? 0 : my_score_nr_workers[0];/// (my_score_nr_workers[1]+opp_score_nr_workers[1]);
		float score_my_light = my_score_nr_light[1] == 0 ? 0 : my_score_nr_light[0];/// (my_score_nr_light[1]+opp_score_nr_light[1]);
		float score_my_ranged = my_score_nr_ranged[1] == 0 ? 0 : my_score_nr_ranged[0];/// (my_score_nr_ranged[1]+opp_score_nr_ranged[1]);
		float score_my_heavy = my_score_nr_heavy[1] == 0 ? 0 : my_score_nr_heavy[0];/// (my_score_nr_heavy[1]+opp_score_nr_heavy[1]);

		float score_opp_workers = opp_score_nr_workers[1] == 0 ? 0 : opp_score_nr_workers[0];/// (my_score_nr_workers[1]+opp_score_nr_workers[1]);
		float score_opp_light = opp_score_nr_light[1] == 0 ? 0 : opp_score_nr_light[0];/// (my_score_nr_light[1]+opp_score_nr_light[1]);
		float score_opp_ranged = opp_score_nr_ranged[1] == 0 ? 0 : opp_score_nr_ranged[0];/// (my_score_nr_ranged[1]+opp_score_nr_ranged[1]);
		float score_opp_heavy = opp_score_nr_heavy[1] == 0 ? 0 : opp_score_nr_heavy[0];/// (my_score_nr_heavy[1]+opp_score_nr_heavy[1]);

		res_score = res_score < Helper.DESIRED_NUM_RES ? (res_score / 1)// Helper.DESIRED_NUM_RES)
				: 1;
		res_score *= RESOURCE;

		res_collection_score = my_score_nr_workers[1] == 0 ? 0 : res_collection_score / my_score_nr_workers[1];

		total_score += res_score;
		total_score += res_collection_score;

		total_score += my_score_nr_bases[0];
		total_score += my_score_nr_barracks[0];

		total_score += score_my_workers;
		total_score += score_opp_workers;
		total_score += score_my_light;
		total_score += score_opp_light;
		total_score += score_my_ranged;
		total_score += score_my_ranged;
		total_score += score_my_heavy;
		total_score += score_opp_heavy;

		distScore = nr_mobileUnits == 0 ? 0 : distScore / nr_mobileUnits;
		distScore /= Helper.MAX_MAP_DIST;
		distScore *= W_DIST;
		total_score += distScore;

		// normalize total
		total_score += 4;
		total_score /= 13;

		if (Helper.DEBUG_EF_VALUES) {
			System.out.println("sc res: " + res_score + ", colletion: " + res_collection_score + ", my bases: "
					+ my_score_nr_bases[0] + ", my barracks: " + my_score_nr_barracks[0] + ", my workers: "
					+ score_my_workers + ", opp workers: " + score_opp_workers + ", my light: " + score_my_light
					+ ", opp light: " + score_opp_light + ", my ranged: " + score_my_ranged + ", opp ranged: "
					+ score_opp_ranged + ", my heavy: " + score_my_heavy + ", opp heavy: " + score_opp_heavy
					+ ", dist: " + distScore + " --- total: " + total_score);
		}

		return total_score;
	}

	@Override
	public float upperBound(GameState gs) {
		return 1;
	}

}
