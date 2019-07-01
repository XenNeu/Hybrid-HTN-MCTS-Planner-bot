/*******************************************************************************
 * Hybrid HTN MCTS Planner 
 * Created by Xenija Neufeld, 2019
 ******************************************************************************/
package EA;

public class Gene {
	public GeneType type;

	public float weightValue;

	public float minVal = 0;
	public float maxValue = 1;

	public Gene(GeneType type) {
		this.type = type;
		weightValue = (float) (Math.random() * (maxValue - minVal));
	}

	public void ChangeWeight(float newVal) {
		weightValue = Math.min(maxValue, Math.max(minVal, newVal));
	}

	public Gene Clone() {
		Gene newChromosome = new Gene(this.type);
		newChromosome.weightValue = this.weightValue;
		newChromosome.minVal = this.minVal;
		newChromosome.maxValue = this.maxValue;
		return newChromosome;
	}

	enum GeneType {
		RESOURCE, RESOURCE_IN_WORKER,

		HP_WORKER, HP_LIGHT, HP_RANGED, HP_HEAVY, HP_BASE, HP_BARRACKS,

		HP_WORKER_OPP, HP_LIGHT_OPP, HP_RANGED_OPP, HP_HEAVY_OPP, HP_BASE_OPP, HP_BARRACKS_OPP,

		DIST,
		// DIST_TO_BASE,
		// DIST_TO_RES,
		// DIST_TO_OPP,

		PRODUCTION,
	}
}
