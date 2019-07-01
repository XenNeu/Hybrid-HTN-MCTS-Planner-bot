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
import rts.UnitAction;
import rts.UnitActionAssignment;
import rts.units.Unit;
import rts.units.UnitTypeTable;
import util.Helper;
import util.Pair;

public class AttackAllEF extends EvaluationFunction {

	public float RESOURCE = 0.2905078f;
	public float RESOURCE_COLLECTION = 0.0f;
	public float[] W_BASE = new float[] {0.27780592f, -0.05797437f };
	public float[] W_BARRACKS = new float[] {1.0f, -1.0f };
	public float[] W_WORKER = new float[] {0.8472936f, -1.0f };
	public float[] W_LIGHT = new float[] { 0.15073738f, -0.01908572f };
	public float[] W_RANGE = new float[] {0.02756919f, -0.24656579f };
	public float[] W_HEAVY = new float[] {0.122111425f, -0.19328979f };
	public float W_DIST = 0.28046623f;

	public AttackAllEF() 
	{
		int mapsize = Helper.MAP_SIZE;
		// Get current weights from EA
		if (Helper.TRAINING_MODE_ON) 
		{
			if (Generator.FUNCTION_CLASS_TO_TRAIN.equals(AttackAllEF.class)) 
			{
				RESOURCE = Generator.CURRENT_GENES[0].weightValue;
				RESOURCE_COLLECTION = Generator.CURRENT_GENES[1].weightValue;

				W_BASE[0] = Generator.CURRENT_GENES[2].weightValue;
				W_BASE[1] = -Generator.CURRENT_GENES[3].weightValue;
				W_BARRACKS[0] = Generator.CURRENT_GENES[4].weightValue;
				W_BARRACKS[1] = -Generator.CURRENT_GENES[5].weightValue;
				W_WORKER[0] = Generator.CURRENT_GENES[6].weightValue;
				W_WORKER[1] = -Generator.CURRENT_GENES[7].weightValue;
				W_LIGHT[0] = Generator.CURRENT_GENES[8].weightValue;
				W_LIGHT[1] = -Generator.CURRENT_GENES[9].weightValue;
				W_RANGE[0] = Generator.CURRENT_GENES[10].weightValue;
				W_RANGE[1] = -Generator.CURRENT_GENES[11].weightValue;
				W_HEAVY[0] = Generator.CURRENT_GENES[12].weightValue;
				W_HEAVY[1] = -Generator.CURRENT_GENES[13].weightValue;

				W_DIST = Generator.CURRENT_GENES[14].weightValue;
				return;
			}
			else if (!Generator.FUNCTION_CLASS_TO_TRAIN.equals(AttackAllEF.class) && Helper.USE_WEIGHTS_FROM_ARCHIVE ) 
			{
				if (Generator.CURRENT_INDIVIDUALS[3] != null) {
					RESOURCE = Generator.CURRENT_INDIVIDUALS[3].genes[0].weightValue;
					RESOURCE_COLLECTION = Generator.CURRENT_INDIVIDUALS[3].genes[1].weightValue;

					W_BASE[0] = Generator.CURRENT_INDIVIDUALS[3].genes[2].weightValue;
					W_BASE[1] = -Generator.CURRENT_INDIVIDUALS[3].genes[3].weightValue;
					W_BARRACKS[0] = Generator.CURRENT_INDIVIDUALS[3].genes[4].weightValue;
					W_BARRACKS[1] = -Generator.CURRENT_INDIVIDUALS[3].genes[5].weightValue;
					W_WORKER[0] = Generator.CURRENT_INDIVIDUALS[3].genes[6].weightValue;
					W_WORKER[1] = -Generator.CURRENT_INDIVIDUALS[3].genes[7].weightValue;
					W_LIGHT[0] = Generator.CURRENT_INDIVIDUALS[3].genes[8].weightValue;
					W_LIGHT[1] = -Generator.CURRENT_INDIVIDUALS[3].genes[9].weightValue;
					W_RANGE[0] = Generator.CURRENT_INDIVIDUALS[3].genes[10].weightValue;
					W_RANGE[1] = -Generator.CURRENT_INDIVIDUALS[3].genes[11].weightValue;
					W_HEAVY[0] = Generator.CURRENT_INDIVIDUALS[3].genes[12].weightValue;
					W_HEAVY[1] = -Generator.CURRENT_INDIVIDUALS[3].genes[13].weightValue;

					W_DIST = Generator.CURRENT_INDIVIDUALS[3].genes[14].weightValue;
					return;
				} 
			}
		}

		if (Helper.USE_WEIGHTS_FROM_ARCHIVE && !Helper.TRAINING_MODE_ON) 
		{
			if (TournamentFromArchive.CURRENT_INDIVIDUALS[3] != null) 
			{
				RESOURCE = TournamentFromArchive.CURRENT_INDIVIDUALS[3].genes[0].weightValue;
				RESOURCE_COLLECTION = TournamentFromArchive.CURRENT_INDIVIDUALS[3].genes[1].weightValue;

				W_BASE[0] = TournamentFromArchive.CURRENT_INDIVIDUALS[3].genes[2].weightValue;
				W_BASE[1] = -TournamentFromArchive.CURRENT_INDIVIDUALS[3].genes[3].weightValue;
				W_BARRACKS[0] = TournamentFromArchive.CURRENT_INDIVIDUALS[3].genes[4].weightValue;
				W_BARRACKS[1] = -TournamentFromArchive.CURRENT_INDIVIDUALS[3].genes[5].weightValue;
				W_WORKER[0] = TournamentFromArchive.CURRENT_INDIVIDUALS[3].genes[6].weightValue;
				W_WORKER[1] = -TournamentFromArchive.CURRENT_INDIVIDUALS[3].genes[7].weightValue;
				W_LIGHT[0] = TournamentFromArchive.CURRENT_INDIVIDUALS[3].genes[8].weightValue;
				W_LIGHT[1] = -TournamentFromArchive.CURRENT_INDIVIDUALS[3].genes[9].weightValue;
				W_RANGE[0] = TournamentFromArchive.CURRENT_INDIVIDUALS[3].genes[10].weightValue;
				W_RANGE[1] = -TournamentFromArchive.CURRENT_INDIVIDUALS[3].genes[11].weightValue;
				W_HEAVY[0] = TournamentFromArchive.CURRENT_INDIVIDUALS[3].genes[12].weightValue;
				W_HEAVY[1] = -TournamentFromArchive.CURRENT_INDIVIDUALS[3].genes[13].weightValue;

				W_DIST = TournamentFromArchive.CURRENT_INDIVIDUALS[3].genes[14].weightValue;
				return;
			} 
		}

		if (mapsize == 0) 
		{
			// <<
			RESOURCE = 0.5405078f;
			RESOURCE_COLLECTION = 0.0f;
			W_BASE      = new float[]{0.62715626f, -0.16760468f};   
		    	W_BARRACKS   = new float[]{0.9671177f, -0.6630061f};  
		    	W_WORKER    = new float[]{0.9329065f, -0.9654116f};  
		    	W_LIGHT     = new float[]{0.20155168f, -0.20486742f};  
		    	W_RANGE     = new float[]{0.45291403f, -0.6923747f}; 	
		    	W_HEAVY     = new float[]{0.66301143f, -0.57504714f};  
		    	W_DIST = 0.48983508f;
		} 
		else if (mapsize == 1) 
		{
			// <<
			RESOURCE = 0.28437474f;
			RESOURCE_COLLECTION = 0.0419661f;
			W_BASE      = new float[]{0.382469f, -0.357565f};   
	    	W_BARRACKS   = new float[]{0.260867f, -0.380343f};  
	    	W_WORKER    = new float[]{0.576655f, -0.271638f};  
	    	W_LIGHT     = new float[]{0.187826f, -0.457606f};  
	    	W_RANGE     = new float[]{0.699649f, -0.958983f}; 	
	    	W_HEAVY     = new float[]{0.241478f, -0.505123f};  
	    	W_DIST = 0.771973f;
		} 
		else if (mapsize == 2) 
		{	
			//11_4 <<

			RESOURCE = 0.28437474f;
			RESOURCE_COLLECTION = 0.7419661f;
			W_BASE = new float[] { 0.5933875f, -0.08638668f };
			W_BARRACKS = new float[] { 0.5746629f, -0.2244944f };
			W_WORKER = new float[] { 0.8690041f, -0.80485225f };
			W_LIGHT = new float[] { 0.026071545f, -0.29589388f };
			W_RANGE = new float[] { 0.4381667f, -0.98999566f };
			W_HEAVY = new float[] { 0.34268215f, -0.8622559f };

			W_DIST = 0.82908666f;
		}
		
	}

	@Override
	public float evaluate(int maxplayer, int minplayer, GameState gs) {
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

			// UNITS
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
				} else {
					opp_score_nr_barracks[0] += W_BARRACKS[meOrOpp] * (u.getHitPoints() / u.getMaxHitPoints());
					opp_score_nr_barracks[1] += 1;
				}
				continue;
			} else if (u.getType() != Helper.RESOURCE_TYPE) {
				Long oppID = null;
				Unit opp = null;
				int[][] oppFF = null;

				if (meOrOpp == 0) {
					Long assignedToBaseIndex = Helper.GetBaseIDForUnit(u, gs, false);

					if (!Helper.PER_BASE_CLOSEST_OPP_BUILDING_INFOS.isEmpty()) {
						if (assignedToBaseIndex != null) {
							Pair<Long, int[][]> oppInfo = Helper.PER_BASE_CLOSEST_OPP_BUILDING_INFOS
									.get(assignedToBaseIndex);
							if (oppInfo != null) {
								oppID = oppInfo.m_a;
								opp = pgs.getUnit(oppID);
								if (Helper.MAP_SIZE == 2) {
									oppFF = oppInfo.m_b;
								}
							}
						}
					}

					if (opp == null && !Helper.PER_BASE_CLOSEST_OPP_INFOS.isEmpty()) {
						if (assignedToBaseIndex != null) {
							opp = Helper.PER_BASE_CLOSEST_OPP_INFOS.get(assignedToBaseIndex);
						}
					}

					// if no bases are left, find closest to unit
					if (opp == null) {
						opp = Helper.FindClosestReachableOppBuildingForBase(u, gs, false);
					}
					if (opp == null) {
						opp = Helper.FindClosestReachableOppUnitForBase(u, gs, false);
					}

					if (opp != null) 
					{
						float currentDist = (Math.abs(u.getX() - opp.getX()) + Math.abs(u.getY() - opp.getY()));

						if (oppFF != null) // only for buildings
						{
							currentDist = oppFF[u.getX()][u.getY()];
						}
						distScore += (Helper.MAX_MAP_DIST - currentDist);

						// count mobile units
						nr_mobileUnits += 1;

					}
				}

				if (u.getType() == Helper.WORKER_TYPE) {
					if (meOrOpp == 0) {
						my_score_nr_workers[0] += W_WORKER[meOrOpp] * (u.getHitPoints() / u.getMaxHitPoints());
						my_score_nr_workers[1]++;

						// continue collecting
						if (u.getResources() > 0) {
							res_collection_score += u.getResources() * RESOURCE_COLLECTION;
						}
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
				? (float) (my_score_nr_bases[0] / Helper.DESIRED_NUM_BASES): 1;
		my_score_nr_barracks[0] = my_score_nr_barracks[0] < Helper.DESIRED_NUM_BARRACKS
				? (float) (my_score_nr_barracks[0] / Helper.DESIRED_NUM_BARRACKS): 1;
		my_score_nr_bases[0] = W_BASE[0] * my_score_nr_bases[0];
		my_score_nr_barracks[0] = W_BARRACKS[0] * my_score_nr_barracks[0];

		float score_opp_bases = opp_score_nr_bases[1] == 0 ? 0 : opp_score_nr_bases[0] ;// / opp_score_nr_bases[1];
		float score_opp_barracks = opp_score_nr_barracks[1] == 0 ? 0
				: opp_score_nr_barracks[0];// / opp_score_nr_barracks[1];

		float score_my_workers = my_score_nr_workers[1] == 0 ? 0
				: my_score_nr_workers[0];// / (my_score_nr_workers[1] + opp_score_nr_workers[1]);
		float score_my_light = my_score_nr_light[1] == 0 ? 0
				: my_score_nr_light[0];// / (my_score_nr_light[1] + opp_score_nr_light[1]);
		float score_my_ranged = my_score_nr_ranged[1] == 0 ? 0
				: my_score_nr_ranged[0];// / (my_score_nr_ranged[1] + opp_score_nr_ranged[1]);
		float score_my_heavy = my_score_nr_heavy[1] == 0 ? 0
				: my_score_nr_heavy[0] ;/// (my_score_nr_heavy[1] + opp_score_nr_heavy[1]);

		float score_opp_workers = opp_score_nr_workers[1] == 0 ? 0
				: opp_score_nr_workers[0];/// (my_score_nr_workers[1] + opp_score_nr_workers[1]);
		float score_opp_light = opp_score_nr_light[1] == 0 ? 0
				: opp_score_nr_light[0];// / (my_score_nr_light[1] + opp_score_nr_light[1]);
		float score_opp_ranged = opp_score_nr_ranged[1] == 0 ? 0
				: opp_score_nr_ranged[0];/// (my_score_nr_ranged[1] + opp_score_nr_ranged[1]);
		float score_opp_heavy = opp_score_nr_heavy[1] == 0 ? 0
				: opp_score_nr_heavy[0] ;/// (my_score_nr_heavy[1] + opp_score_nr_heavy[1]);

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
		total_score += score_my_ranged;
		total_score += score_my_heavy;
		total_score += score_opp_heavy;

		distScore = nr_mobileUnits == 0 ? 0 : distScore / nr_mobileUnits;
		distScore /= Helper.MAX_MAP_DIST;
		distScore *= W_DIST;
		total_score += distScore;

		// normalize total w.o. res. collection
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

	@Override
	public float upperBound(GameState gs) {
		return 1;
	}

}
