/*******************************************************************************
 * Hybrid HTN MCTS Planner 
 * Created by Xenija Neufeld, 2019
 ******************************************************************************/
package EA;

import java.util.ArrayList;

public class Archive {
	public ArrayList<Individual> archive;

	private int archiveSize;

	// Worst/maximal score in archive
	public float maxScore = Float.MAX_VALUE;
	// Index of the worst solution.
	private int maxIndex = -1;

	private int numSolutions = 0;

	public Archive(int size) {
		this.archiveSize = size;
		this.archive = new ArrayList<Individual>();
	}

	public void AddSolution(Individual individual) {
		float individualScore = individual.GetScore();
		FindMax();

		System.out.println(
				"individual score = " + individualScore + ", maxscore = " + maxScore + ", maxIndex: " + maxIndex);
		archive.add(individual);
		FindMax();

		// archive not full
		if (archive.size() <= archiveSize) {
			/*
			 * archive.add(individual); FindMax();
			 * 
			 * if(individualScore <= maxScore || archive.size()==1) { maxScore =
			 * individualScore; maxIndex = archive.size()-1; } else { FindMax(); }
			 */
			return;
		}

		/*
		 * //solution is worse than the worst one if(individualScore >= maxScore) {
		 * return; }
		 * 
		 * FindMax();
		 * 
		 */

		// solution is better than the worst one -> replace
		archive.remove(maxIndex);
		// archive.add(individual);

		FindMax();
	}

	public String PrintInd() {
		String s = "";
		for (int i = 0; i < archive.size(); i++) {
			s += " individual: " + archive.get(i).generationIndex + "_" + archive.get(i).index + " ; score: "
					+ archive.get(i).GetScore() + " \n";
		}
		return s;
	}

	private void FindMax() {
		maxScore = -Float.MAX_VALUE;
		maxIndex = -1;
		for (int i = 0; i < archive.size(); i++) {
			if (archive.get(i).GetScore() > maxScore) {
				maxScore = archive.get(i).GetScore();
				maxIndex = i;
			}
		}
	}

}
