/*******************************************************************************
 * Hybrid HTN MCTS Planner 
 * Created by Xenija Neufeld, 2019
 ******************************************************************************/
package HTNPlanner.PrimitiveTasks.EvaluationFunctions;

import HTNPlanner.Planner;
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

public class BuildAndDefendEF extends EvaluationFunction {

	public float RESOURCE = 0.66529256f;
	public float RESOURCE_COLLECTION = 0.21521364f;

	public float[] W_BASE = new float[] { 0.77146965f, -0.23777112f };
	public float[] W_BARRACKS = new float[] { 0.6252279f, -0.69220763f };
	public float[] W_WORKER = new float[] { 0.4936181f, -0.5426553f };
	public float[] W_LIGHT = new float[] { 0.07117022f, -0.59847224f };
	public float[] W_RANGE = new float[] { 0.2857486f, -0.0019860377f };
	public float[] W_HEAVY = new float[] { 0.6476949f, -0.7931391f };
	public float W_DIST = 0.19401464f;
	
	private int mapsize = -1;
	
	public BuildAndDefendEF() 
	{
				
	}
	
	private void InitValues()
	{
	    mapsize = Helper.MAP_SIZE;
		if (mapsize == 0) 
		{
			// 34_1 <<
			RESOURCE = 0.66529256f;
			RESOURCE_COLLECTION = 0.21521364f;

			W_BASE = new float[] { 0.77146965f, -0.23777112f };
			W_BARRACKS = new float[] { 0.6252279f, -0.69220763f };
			W_WORKER = new float[] { 0.4936181f, -0.5426553f };
			W_LIGHT = new float[] { 0.07117022f, -0.59847224f };

			W_RANGE = new float[] { 0.2857486f, -0.0019860377f };
			W_HEAVY = new float[] { 0.6476949f, -0.7931391f };
			W_DIST = 0.19401464f;

		} else if (mapsize == 1) {
			// 34_1 <<
			RESOURCE = 0.66529256f;
			RESOURCE_COLLECTION = 0.21521364f;

			W_BASE = new float[] { 0.77146965f, -0.23777112f };
			W_BARRACKS = new float[] { 0.6252279f, -0.69220763f };
			W_WORKER = new float[] { 0.4936181f, -0.5426553f };
			W_LIGHT = new float[] { 0.07117022f, -0.59847224f };

			W_RANGE = new float[] { 0.2857486f, -0.0019860377f };
			W_HEAVY = new float[] { 0.6476949f, -0.7931391f };
			W_DIST = 0.19401464f;
		} 
		else if (mapsize == 2) 
		{
			if(Helper.MAX_MAP_DIST <=48) 
			{
				//28_3 <<
				RESOURCE = 0.22856045f;
				RESOURCE_COLLECTION = 0.3019935f;

				W_BASE = new float[] { 0.51673216f, -0.4778745f };
				W_BARRACKS = new float[] { 0.6307218f, -0.43019077f };
				W_WORKER = new float[] {0.7811886f, -1.0f };
				W_LIGHT = new float[] {0.33484668f, -0.5840186f };

				W_RANGE = new float[] {1.0f, -0.84353477f };
				W_HEAVY = new float[] {0.4124147f, -0.7296402f };
				W_DIST = 0.95757884f;
			}
			else if(Helper.MAX_MAP_DIST == 64) // distant res 
			{
				//183_5 <<
				RESOURCE = 0.52972177f;
				RESOURCE_COLLECTION = 0.9166429f;

				W_BASE = new float[] { 0.8201425f, -0.7680027f };
				W_BARRACKS = new float[] { 0.065186925f, -0.5606649f };
				W_WORKER = new float[] {0.70500225f, -0.7205239f };
				W_LIGHT = new float[] {0.8369844f, -0.33152243f };	
				W_RANGE = new float[] {0.50921583f, -0.31053594f };
				W_HEAVY = new float[] {0.55352455f, -0.735466f };
				W_DIST = 0.6853478f;
			}
			else  //huge maps
			{
				//210_5 ,,,,,,,,,,,,,,,
				RESOURCE = 0.65733844f;
				RESOURCE_COLLECTION = 0.104820386f;

				W_BASE = new float[] { 0.5739964f, -0.0f };
				W_BARRACKS = new float[] { 0.6781974f, -0.0f };
				W_WORKER = new float[] {0.9260183f, -0.0f };
				W_LIGHT = new float[] {0.75808656f, -0.0f };	
				W_RANGE = new float[] {0.4170848f, -0.0f };
				W_HEAVY = new float[] {0.2054558f, -0.0f };
				W_DIST = 0.52586144f;

			}
		}
	}

	@Override
	public float evaluate(int maxplayer, int minplayer, GameState gs) {
		if(mapsize != Helper.MAP_SIZE)
		{
			InitValues();
		}
		float baseScoreMax = base_score(maxplayer, gs);
		float score = baseScoreMax;
		return score;
	}

	public float base_score(int player, GameState gs) {
		PhysicalGameState pgs = gs.getPhysicalGameState();

		float[] my_score_nr_bases = { 0.0f, 0.0f };
		float[] opp_score_nr_bases = { 0.0f, 0.0f };

		float[] my_score_nr_barracks = { 0.0f, 0.0f };
		float[] opp_score_nr_barracks = { 0.0f, 0.0f };

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

			if (u.getType() == Helper.BASE_TYPE) {
				if (meOrOpp == 0) {
					my_score_nr_bases[0] += (u.getHitPoints() / u.getMaxHitPoints());
					my_score_nr_bases[1] += 1;
				} else {
					opp_score_nr_bases[0] += W_BASE[meOrOpp] * (u.getHitPoints() / u.getMaxHitPoints());
					opp_score_nr_bases[1] += 1;
				}
				continue;
			} else if (u.getType() == Helper.BARRACKS_TYPE) {

				if (meOrOpp == 0) {
					my_score_nr_barracks[0] += (u.getHitPoints() / u.getMaxHitPoints());
					my_score_nr_barracks[1] += 1;

					UnitActionAssignment uaa = gs.getActionAssignment(u);

					if (uaa != null && uaa.action.getType() == UnitAction.TYPE_PRODUCE
							&& uaa.action.getUnitType() == Helper.HEAVY_TYPE) {
						my_score_nr_heavy[0] += W_HEAVY[meOrOpp] * 1;
						my_score_nr_heavy[1]++;
						distScore += (Helper.MAX_MAP_DIST - 1);
						res_score -= Helper.HEAVY_TYPE.cost;
					}
				} else {
					opp_score_nr_barracks[0] += W_BARRACKS[meOrOpp] * (u.getHitPoints() / u.getMaxHitPoints());
					opp_score_nr_barracks[1] += 1;
				}
				continue;
			} else if (u.getType() != Helper.RESOURCE_TYPE) {

				Unit closestBase = null;
				Unit closestRes = null;

				int[][] resFF = null;
				int[][] baseFF = null;

				if (meOrOpp == 0) {
					// count mobile units
					nr_mobileUnits += 1;

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
						// TODDO: remove hack - move map size to helper?
						if (Helper.MAP_SIZE == 2) {
							baseFF = Helper.MY_BASES_FLOODFILL.get(baseID);
						}
					}
					if (resID != null) {
						closestRes = pgs.getUnit(resID);
					}
				}

				if (u.getType() == Helper.WORKER_TYPE) {

					if (meOrOpp == 0) {
						my_score_nr_workers[0] += W_WORKER[meOrOpp] * (u.getHitPoints() / u.getMaxHitPoints());
						my_score_nr_workers[1]++;

						// continue collecting
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

						UnitActionAssignment uaa = gs.getActionAssignment(u);

						if (uaa != null && uaa.action.getType() == UnitAction.TYPE_PRODUCE
								&& uaa.action.getUnitType() == Helper.BARRACKS_TYPE) {
							my_score_nr_barracks[0] += 1;
							my_score_nr_barracks[1] += 1;
							res_score -= Helper.BARRACKS_TYPE.cost;
						}

						if (uaa != null && uaa.action.getType() == UnitAction.TYPE_PRODUCE
								&& uaa.action.getUnitType() == Helper.BASE_TYPE) {
							my_score_nr_bases[0] += 1;
							my_score_nr_bases[1] += 1;
							res_score -= Helper.BASE_TYPE.cost;
						}

					} else {
						opp_score_nr_workers[0] += W_WORKER[meOrOpp] * (u.getHitPoints() / u.getMaxHitPoints());
						opp_score_nr_workers[1]++;
					}
					continue;
				} else if (u.getType() == Helper.LIGHT_TYPE) {
					if (meOrOpp == 0) {
						my_score_nr_light[0] += W_LIGHT[meOrOpp] * (u.getHitPoints() / u.getMaxHitPoints());
						my_score_nr_light[1]++;

						if (closestBase != null) {
							float currentDist = (Math.abs(u.getX() - closestBase.getX())
									+ Math.abs(u.getY() - closestBase.getY()));
							if (baseFF != null) {
								currentDist = baseFF[u.getX()][u.getY()];
							}
							distScore += (Helper.MAX_MAP_DIST - currentDist);
						}
					} else {
						opp_score_nr_light[0] += W_LIGHT[meOrOpp] * (u.getHitPoints() / u.getMaxHitPoints());
						opp_score_nr_light[1]++;
					}
					continue;
				} else if (u.getType() == Helper.RANGED_TYPE) {
					if (meOrOpp == 0) {
						my_score_nr_ranged[0] += W_RANGE[meOrOpp] * (u.getHitPoints() / u.getMaxHitPoints());
						my_score_nr_ranged[1]++;

						if (closestBase != null) {
							float currentDist = (Math.abs(u.getX() - closestBase.getX())
									+ Math.abs(u.getY() - closestBase.getY()));
							if (baseFF != null) {
								currentDist = baseFF[u.getX()][u.getY()];
							}
							distScore += (Helper.MAX_MAP_DIST - currentDist);
						}
					} else {
						opp_score_nr_ranged[0] += W_RANGE[meOrOpp] * (u.getHitPoints() / u.getMaxHitPoints());
						opp_score_nr_ranged[1]++;
					}
					continue;
				} else if (u.getType() == Helper.HEAVY_TYPE) {
					if (meOrOpp == 0) {
						my_score_nr_heavy[0] += W_HEAVY[meOrOpp] * (u.getHitPoints() / u.getMaxHitPoints());
						my_score_nr_heavy[1]++;

						if (closestBase != null) {
							float currentDist = (Math.abs(u.getX() - closestBase.getX())
									+ Math.abs(u.getY() - closestBase.getY()));
							if (baseFF != null) {
								currentDist = baseFF[u.getX()][u.getY()];
							}
							distScore += (Helper.MAX_MAP_DIST - currentDist);
						}
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
				: (my_score_nr_bases[0] > Helper.DESIRED_NUM_BASES ? 0 : 1);
		my_score_nr_barracks[0] = my_score_nr_barracks[0] < Helper.DESIRED_NUM_BARRACKS
				? (float) (my_score_nr_barracks[0] / Helper.DESIRED_NUM_BARRACKS)
				: (my_score_nr_barracks[0] > Helper.DESIRED_NUM_BARRACKS ? 0 : 1);

		my_score_nr_bases[0] = W_BASE[0] * my_score_nr_bases[0];
		my_score_nr_barracks[0] = W_BARRACKS[0] * my_score_nr_barracks[0];

		float score_opp_bases = opp_score_nr_bases[1] == 0 ? 0 : opp_score_nr_bases[0] / opp_score_nr_bases[1];
		float score_opp_barracks = opp_score_nr_barracks[1] == 0 ? 0
				: opp_score_nr_barracks[0] / opp_score_nr_barracks[1];

		float score_my_workers = my_score_nr_workers[1] == 0 ? 0
				: my_score_nr_workers[0] / (my_score_nr_workers[1] + opp_score_nr_workers[1]);
		float score_my_light = my_score_nr_light[1] == 0 ? 0
				: my_score_nr_light[0] / (my_score_nr_light[1] + opp_score_nr_light[1]);
		float score_my_ranged = my_score_nr_ranged[1] == 0 ? 0
				: my_score_nr_ranged[0] / (my_score_nr_ranged[1] + opp_score_nr_ranged[1]);
		float score_my_heavy = my_score_nr_heavy[1] == 0 ? 0
				: my_score_nr_heavy[0] / (my_score_nr_heavy[1] + opp_score_nr_heavy[1]);

		float score_opp_workers = opp_score_nr_workers[1] == 0 ? 0
				: opp_score_nr_workers[0] / (my_score_nr_workers[1] + opp_score_nr_workers[1]);
		float score_opp_light = opp_score_nr_light[1] == 0 ? 0
				: opp_score_nr_light[0] / (my_score_nr_light[1] + opp_score_nr_light[1]);
		float score_opp_ranged = opp_score_nr_ranged[1] == 0 ? 0
				: opp_score_nr_ranged[0] / (my_score_nr_ranged[1] + opp_score_nr_ranged[1]);
		float score_opp_heavy = opp_score_nr_heavy[1] == 0 ? 0
				: opp_score_nr_heavy[0] / (my_score_nr_heavy[1] + opp_score_nr_heavy[1]);

		res_score = res_score < Helper.DESIRED_NUM_RES ? (res_score / Helper.DESIRED_NUM_RES) : 1;
		res_score *= RESOURCE;

		res_collection_score = my_score_nr_workers[1] == 0 ? 0 : res_collection_score / my_score_nr_workers[1];

		total_score += res_score;
		total_score += res_collection_score;

		total_score += my_score_nr_bases[0];
		total_score += my_score_nr_barracks[0];

		total_score += score_opp_bases;
		total_score += score_opp_barracks;

		total_score += score_my_workers;
		total_score += score_opp_workers;
		total_score += score_my_light;
		total_score += score_opp_light;
		total_score += score_my_ranged;
		total_score += score_opp_ranged;
		total_score += score_my_heavy;
		total_score += score_opp_heavy;

		distScore = nr_mobileUnits == 0 ? 0 : distScore / nr_mobileUnits;
		distScore /= Helper.MAX_MAP_DIST;
		distScore *= W_DIST;
		total_score += distScore;

		// normalizing total score
		total_score += 6;
		total_score /= 15;

		if (Helper.DEBUG_EF_VALUES) {
			System.out.println("sc res: " + res_score + ", colletion: " + res_collection_score + ", my bases: "
					+ my_score_nr_bases[0] + ", opp bases: " + score_opp_bases + ", my barracks: "
					+ my_score_nr_barracks[0] + ", opp barracks: " + score_opp_barracks + ", my workers: "
					+ score_my_workers + ", opp workers: " + score_opp_workers + ", my light: " + score_my_light
					+ ", opp light: " + score_opp_light + ", my ranged: " + score_my_ranged + ", opp ranged: "
					+ score_opp_ranged + ", my heavy: " + score_my_heavy + ", opp heavy: " + score_opp_heavy
					+ ", dist: " + distScore + " --- total: " + total_score);
		}

		return total_score;
	}

	public float upperBound(GameState gs) {
		// PhysicalGameState pgs = gs.getPhysicalGameState();
		// int free_resources = 0;
		// int player_resources[] =
		// {gs.getPlayer(0).getResources(),gs.getPlayer(1).getResources()};
		// for(Unit u:pgs.getUnits())
		// {
		// if (u.getPlayer()==-1) free_resources+=u.getResources();
		// if (u.getPlayer()==0) {
		// player_resources[0] += u.getResources();
		// player_resources[0] += u.getCost();
		// }
		// if (u.getPlayer()==1) {
		// player_resources[1] += u.getResources();
		// player_resources[1] += u.getCost();
		// }
		// }

		return 1;
	}

}
