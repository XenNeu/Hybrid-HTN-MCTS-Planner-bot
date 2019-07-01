/*******************************************************************************
 * Hybrid HTN MCTS Planner 
 * Created by Xenija Neufeld, 2019
 ******************************************************************************/
package EA;

import EA.Gene.GeneType;

public class PreventAttackAllEFIndividual extends Individual {

	public PreventAttackAllEFIndividual(Integer individualIndex) {
		super(individualIndex);
		genes = new Gene[] { new Gene(GeneType.HP_BASE),
				new Gene(GeneType.HP_BARRACKS), new Gene(GeneType.HP_WORKER),
				new Gene(GeneType.HP_WORKER_OPP), new Gene(GeneType.HP_LIGHT),
				new Gene(GeneType.HP_LIGHT_OPP), new Gene(GeneType.HP_RANGED),
				new Gene(GeneType.HP_RANGED_OPP), new Gene(GeneType.HP_HEAVY),
				new Gene(GeneType.HP_HEAVY_OPP), new Gene(GeneType.DIST) };
	}
}
