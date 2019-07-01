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
import rts.units.UnitTypeTable;
import util.Helper;

public class PreventAttackAllEF extends EvaluationFunction {

	public float W_BASE = 0.8356437f;
	public float W_BARRACKS = 0.74268097f;
	public float[] W_WORKER = new float[] { 0.20246285f, -0.07019373f };
	public float[] W_LIGHT = new float[] { 0.5385816f, -0.030770233f };
	public float[] W_RANGE = new float[] { 0.12689166f, -0.73695606f };
	public float[] W_HEAVY = new float[] { 0.050213832f, -0.90733975f };
	public float W_DIST = 0.84486884f;

	private int mapsize = -1;
	
	public PreventAttackAllEF() 
	{
		
	}
	
	private void InitValues()
	{
		mapsize = Helper.MAP_SIZE;
		if (mapsize == 0) 
		{
			// 12_9 <<
			W_BASE = 0.8356437f;
			W_BARRACKS = 0.74268097f;
			W_WORKER = new float[] { 0.20246285f, -0.07019373f };
			W_LIGHT = new float[] { 0.5385816f, -0.030770233f };
			W_RANGE = new float[] { 0.12689166f, -0.73695606f };
			W_HEAVY = new float[] { 0.050213832f, -0.90733975f };
			W_DIST = 0.84486884f;
		} 
		else if (mapsize == 1) 
		{
			// 12_9  <<
			W_BASE = 0.8356437f;
			W_BARRACKS = 0.74268097f;
			W_WORKER = new float[] { 0.20246285f, -0.07019373f };
			W_LIGHT = new float[] { 0.5385816f, -0.030770233f };
			W_RANGE = new float[] { 0.12689166f, -0.73695606f };
			W_HEAVY = new float[] { 0.050213832f, -0.90733975f };
			W_DIST = 0.84486884f;
		}
		else if (mapsize == 2) 
		{
			// 12_9 <<
			W_BASE = 0.8356437f;
			W_BARRACKS = 0.74268097f;
			W_WORKER = new float[] { 0.20246285f, -0.07019373f };
			W_LIGHT = new float[] { 0.5385816f, -0.030770233f };
			W_RANGE = new float[] { 0.12689166f, -0.73695606f };
			W_HEAVY = new float[] { 0.050213832f, -0.90733975f };
			W_DIST = 0.84486884f;
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
						if (!Helper.PER_BASE_CLOSEST_OPP_INFOS.isEmpty()) {
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

		float score_my_workers = my_score_nr_workers[1] == 0 ? 0 : my_score_nr_workers[0];
		float score_my_light = my_score_nr_light[1] == 0 ? 0 : my_score_nr_light[0];
		float score_my_ranged = my_score_nr_ranged[1] == 0 ? 0 : my_score_nr_ranged[0];
		float score_my_heavy = my_score_nr_heavy[1] == 0 ? 0 : my_score_nr_heavy[0];

		float score_opp_workers = opp_score_nr_workers[1] == 0 ? 0 : opp_score_nr_workers[0];
		float score_opp_light = opp_score_nr_light[1] == 0 ? 0 : opp_score_nr_light[0];
		float score_opp_ranged = opp_score_nr_ranged[1] == 0 ? 0 : opp_score_nr_ranged[0];
		float score_opp_heavy = opp_score_nr_heavy[1] == 0 ? 0 : opp_score_nr_heavy[0];

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
		total_score /= 11;

		if (Helper.DEBUG_EF_VALUES) {
			System.out.println(", my bases: " + my_score_nr_bases[0] + ", my barracks: " + my_score_nr_barracks[0]
					+ ", my workers: " + score_my_workers + ", opp workers: " + score_opp_workers + ", my light: "
					+ score_my_light + ", opp light: " + score_opp_light + ", my ranged: " + score_my_ranged
					+ ", opp ranged: " + score_opp_ranged + ", my heavy: " + score_my_heavy + ", opp heavy: "
					+ score_opp_heavy + ", dist: " + distScore + " --- total: " + total_score);
		}

		return total_score;
	}

	@Override
	public float upperBound(GameState gs) {
		return 1;
	}

}