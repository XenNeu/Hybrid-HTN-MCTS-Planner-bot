/*******************************************************************************
 * Hybrid HTN MCTS Planner 
 * Created by Xenija Neufeld, 2019
 ******************************************************************************/
package EA;

import EA.Gene.GeneType;

public class CollectEFIndividual extends Individual {

	public CollectEFIndividual(Integer individualIndex) {
		super(individualIndex);
		genes = new Gene[] { new Gene(GeneType.RESOURCE),
				new Gene(GeneType.RESOURCE_IN_WORKER), new Gene(GeneType.HP_WORKER),
				new Gene(GeneType.DIST), new Gene(GeneType.HP_BARRACKS) };
	}
}
