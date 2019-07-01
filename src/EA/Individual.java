/*******************************************************************************
 * Hybrid HTN MCTS Planner 
 * Created by Xenija Neufeld, 2019
 ******************************************************************************/
package EA;

import util.Helper;

public class Individual {
	public Gene[] genes = null;

	public int generationIndex;

	// for debugging purposes
	public int index;

	public long score = -1;

	protected Individual(int individualIndex) {
		this.index = individualIndex;
		score = 0;
	}

	public void SetScore(long newScore) {
		score += newScore;
		System.out.println("score: " + score);
	}

	public void Mutate(double probability) {
		for (int i = 0; i < this.genes.length; ++i) {
			double rand = Math.random();
			if (rand <= probability) {
				float newWeightValue = 0.0f;
				if (Helper.MUTATE_CONTINUOUSLY) {
					float valToAdd = (float) (Math.random() * 0.2f);
					valToAdd -= 0.1f;
					newWeightValue = valToAdd + genes[i].weightValue;
				} else {
					newWeightValue = (float) (Math.random() * (genes[i].maxValue - genes[i].minVal));
				}
				this.genes[i].ChangeWeight(newWeightValue);
			}
		}
	}

	public void Crossover(Individual other, double probability) {

		for (int i = 0; i < this.genes.length; ++i) {
			double rand = Math.random();
			if (rand <= probability) {
				Gene temp = this.genes[i];
				this.genes[i] = other.genes[i];
				other.genes[i] = temp;
			}
		}
	}

	public long GetScore() {
		// read from a file
		return score;
	}

	public Gene[] GetGenesClone() {
		Gene[] newGenes = new Gene[genes.length];

		for (int i = 0; i < genes.length; ++i) {
			newGenes[i] = genes[i].Clone();
		}

		return newGenes;
	}

	public Individual Clone() {
		Individual newIndividual = new Individual(this.index);
		newIndividual.genes = GetGenesClone();
		return newIndividual;
	}

	public void Print() {
		String s = "";
		for (int i = 0; i < genes.length; i++) {
			s += "|" + genes[i].weightValue;
		}
		System.out.println(s);
	}
}
