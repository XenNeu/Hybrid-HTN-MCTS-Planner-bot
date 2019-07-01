/*******************************************************************************
 * Hybrid HTN MCTS Planner 
 * Created by Xenija Neufeld, 2019
 ******************************************************************************/
package Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;

import javax.swing.JFrame;

import org.jdom.JDOMException;

import EA.*;
import HTNPlanner.Planner;
import HTNPlanner.PrimitiveTasks.EvaluationFunctions.*;
import PlannerMCTSBot.Hybrid_HTN_MCTS_Planner_Bot;
import ai.PassiveAI;
import ai.RandomAI;
import ai.RandomBiasedAI;
import ai.ahtn.AHTNAI;
import ai.competition.capivara.Capivara;
import ai.competition.tiamat.Tiamat;
import ai.core.AI;
import ai.mcts.naivemcts.NaiveMCTS;
import ai.puppet.PuppetSearchMCTS;
import ai.pvai.PVAIML_ED;
import gui.PhysicalGameStatePanel;
import rts.GameState;
import rts.PartiallyObservableGameState;
import rts.PhysicalGameState;
import rts.PlayerAction;
import rts.units.UnitTypeTable;
import standard.StrategyTactics;
import util.Helper;

public class TournamentFromArchive {
	// public static int Helper.MAP_SIZE_TO_TRAIN;
	public static Individual[] CURRENT_INDIVIDUALS = null;

	// Game related vars
	private static int MAXCYCLES = 5000;
	private static int TIME = 100;
	private static int MAX_ACTIONS = 1000; // 100
	private static int MAX_PLAYOUTS = -1;
	private static int PLAYOUT_TIME = 100;
	private static int MAX_DEPTH = 100; // 10
	private static int RANDOMIZED_AB_REPEATS = 10;
	private static int PERIOD = 20;
	private static int NUMBER_MAPS;

	private static int NUMBER_OPPONENTS;
	private static boolean VISUALIZE = true;
	private static int RUNS_PER_INDIVIDUAL;
	private static int GAMES_COMPLETED;
	private static int WON;
	private static int LOST;
	private static int DRAW;

	private static String gamesPath;
	private static Path resultPath;

	public static void main(String args[]) throws Exception {
		String smallMaps[] = new String[] { 
				"maps/8x8/FourBasesWorkers8x8.xml",
			//	 "maps/NoWhereToRun9x8.xml",
				 "maps/8x8/basesWorkers8x8A.xml",
		};

		String midsizeMaps[] = new String[] { 
				"maps/16x16/basesWorkers16x16A.xml",
				"maps/16x16/TwoBasesBarracks16x16.xml", };

		String bigMaps[] = new String[] {
				 "maps/BroodWar/(4)BloodBath.scmB.xml",
				 "maps/BroodWar/(4)Fortress.scxE.xml",

				"maps/24x24/basesWorkers24x24A.xml",
				 "maps/DoubleGame24x24.xml",
				 "maps/BWDistantResources32x32.xml",
		};

		String[][] maps = { smallMaps, midsizeMaps, bigMaps }; // change this for different types of maps

		// Available games:
		try {
			File jarFile = new File(
					Generator.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
			String jarDir = jarFile.getParentFile().getPath();
			gamesPath = jarDir + "/EA/";
		} catch (Exception e1) {
			System.out.println("not working");
			gamesPath = "/EA/";
		}

		Class[] classes = { CollectEFIndividual.class, BuildAndDefendEFIndividual.class,
				BuildRangedAndDefendEFIndividual.class, AttackAllEFIndividual.class, AttackMilitaryEFIndividual.class,
				PreventAttackAllEFIndividual.class, PreventAttackMilitaryEFIndividual.class};
		CURRENT_INDIVIDUALS = new Individual[7]; // for each EF

		String archives[] = {"a_midsize_collect", "a_midsize_build", null,"a_midsize_attack2", null, "a_midsize_preventall", null};
		String individualsToRead[] = {"39_3", "34_1", null, "11_6", null, "12_9", null}; //midsize
		
		Helper.MAP_SIZE = 0; // 0 = small, 1 = midsize, 2 = big
		NUMBER_OPPONENTS = 1; // TODO: do this in a nicer way

		VISUALIZE = false;
		Helper.DEBUG_ACTION_EXECUTION = false;

		RUNS_PER_INDIVIDUAL = 5;

		// --------------

		NUMBER_MAPS = maps[Helper.MAP_SIZE].length;
		GAMES_COMPLETED = 0;
		WON = 0;
		LOST = 0;
		DRAW = 0;
		long result = 0;

		for (int c = 0; c < classes.length; ++c) 
		{		
			if (archives[c] != null) 
			{
				if (!ReadArchiveFromFile(archives[c], classes[c], c, individualsToRead[c])) {
					System.out.println(
							"Individual " + classes[c].getName() + individualsToRead[c] + "could not be found");
					return;
				}
			}
		}
		
		for (int m = 0; m < NUMBER_MAPS; ++m) {
			System.out.println("**Map: " + m + maps[Helper.MAP_SIZE][m]);
			result = RunGamesForMap(maps[Helper.MAP_SIZE][m]);
			System.out.println("°Map: " + (maps[Helper.MAP_SIZE][m]) + " won: " + WON + " draw: " + DRAW
					+ " lost: " + LOST);
		}
		System.out.println("° Score for all maps: " + result + "/" + GAMES_COMPLETED);
		System.out.println("° won: " + WON + " draw: " + DRAW + " lost: " + LOST);
	}

	private static long RunGame(String mapPath, int playerSide, int oppIndex, int run)
			throws JDOMException, IOException, Exception {
		UnitTypeTable utt = new UnitTypeTable();
		PhysicalGameState pgs = PhysicalGameState.load(mapPath, utt);
		GameState gs = new GameState(pgs, utt);
		boolean gameover = false;

		AI[] opponents = {
				//new Tiamat(utt),
				// new Capivara(utt),
				// new PVAIML_ED(utt),
				 new NaiveMCTS(utt),
				// new AHTNAI(utt),
				//new StrategyTactics(utt), new PassiveAI(utt) 
				 };

		AI me = new Hybrid_HTN_MCTS_Planner_Bot(utt);
		Planner.INSTANCE.player = playerSide;

		AI opp = opponents[oppIndex];

		// System.out.println("**Opp: " + opp.getClass().getName() + ", pl: " +
		// playerSide + ", run: " + run);

		AI ai1;
		AI ai2;
		if (playerSide == 0) {
			ai1 = me;
			ai2 = opp;
		} else {
			ai1 = opp;
			ai2 = me;
		}

		/*
		 * 8x8 maps: 3000 16x16 maps: 4000 24x24 maps: 5000 32x32 maps: 6000 64x64 maps:
		 * 8000 > 64x64 maps: 12000
		 */
		if (pgs.getWidth() < 16) {
			MAXCYCLES = 3000;
		} else if (pgs.getWidth() == 16) {
			MAXCYCLES = 4000;
		} else if (pgs.getWidth() == 24) {
			MAXCYCLES = 5000;
		} else if (pgs.getWidth() == 32) {
			MAXCYCLES = 6000;
		} else if (pgs.getWidth() == 64) {
			MAXCYCLES = 8000;
		} else {
			MAXCYCLES = 12000;
		}

		JFrame w = null;
		if (VISUALIZE) {
			w = PhysicalGameStatePanel.newVisualizer(gs, 1280, 1280, true, PhysicalGameStatePanel.COLORSCHEME_BLACK);
		}
		// JFrame w =
		// PhysicalGameStatePanel.newVisualizer(gs,640,640,false,PhysicalGameStatePanel.COLORSCHEME_WHITE);

		boolean partiallyObservable = false;

		long nextTimeToUpdate = System.currentTimeMillis() + PERIOD;
		do {
			if (System.currentTimeMillis() >= nextTimeToUpdate) {
				PlayerAction pa1 = null;
				PlayerAction pa2 = null;
				if (!partiallyObservable) {
					pa1 = ai1.getAction(0, gs);
					pa2 = ai2.getAction(1, gs);
				} else {
					PartiallyObservableGameState po_gs = new PartiallyObservableGameState(gs, 0);
					pa1 = ai1.getAction(0, po_gs);
					pa2 = ai2.getAction(1, po_gs);
				}
				gs.issueSafe(pa1);
				gs.issueSafe(pa2);

				// simulate:
				gameover = gs.cycle();
				if (VISUALIZE) {
					w.repaint();
				}
				nextTimeToUpdate += PERIOD;

			} else {
				try {
					Thread.sleep(1);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} while (!gameover && gs.getTime() < MAXCYCLES);

		GAMES_COMPLETED++;

		long result = 0;
		if (gameover) {
			if (gs.winner() == playerSide) {
				result = 1;
				WON++;
			} else {
				result = -1;
				LOST++;
			}
		} else {
			DRAW++;
		}

		ai1.gameOver(gs.winner());
		ai2.gameOver(gs.winner());
		ai1.reset();
		ai2.reset();
		if (VISUALIZE) {
			w.dispose();
		}

		// System.out.println("° Score for this run: " + result );
		System.out.println("**Opp: " + opp.getClass().getName() + ", pl: " + playerSide + ", run: " + run + ", result: " + result);

		return result;
	}

	private static boolean ReadArchiveFromFile(String archiveFileToRead, Class EFclassName, int classIndex,
			String individualName) throws NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		// create individuals of the correct class
		Class individualClass = EFclassName;
		Constructor<?> constructor = null;
		Individual individual = null;

		constructor = individualClass.getConstructor(Integer.class);
		individual = (Individual) constructor.newInstance(0);

		String archivePath = (gamesPath + "/" + archiveFileToRead + ".txt");

		BufferedReader stdInput = new BufferedReader(new FileReader(archivePath));

		String s = null;

		s = stdInput.readLine();
		if (s != null) {
			int currentGenerationIndex = Integer.parseInt(s) + 1;
		}

		while ((s = stdInput.readLine()) != null) {
			if (s.contains(":")) {

				int gen = s.indexOf("_", 0);
				int ind = s.indexOf(",", gen);
				int sc = s.indexOf(":", ind);

				String genStr = s.substring(0, gen);
				String indStr = s.substring(gen + 1, ind);

				String nameStr = s.substring(0, ind);
				if (!nameStr.equals(individualName)) {
					continue;
				}
				String scoreStr = s.substring(ind + 1, sc);

				String chrStr = s.substring(sc + 1);
				String[] genes = chrStr.split(",");

				assert (genes.length == individual.genes.length);

				Individual newIndividual = individual.Clone();
				newIndividual.generationIndex = Integer.parseInt(genStr);
				newIndividual.index = Integer.parseInt(indStr);
				newIndividual.score = Long.parseLong(scoreStr);

				for (int c = 0; c < genes.length; ++c) {
					float w = Float.parseFloat(genes[c]);
					newIndividual.genes[c].weightValue = w;
				}

				CURRENT_INDIVIDUALS[classIndex] = newIndividual;
				return true;
			}
		}
		return false;
	}

	private static long RunGamesForMap(String map) throws JDOMException, IOException, Exception {
		long mapScore = 0;

		int wonBefore = WON;
		int drawBefore = DRAW;
		int lostBEFORE = LOST;

		for (int o = 0; o < NUMBER_OPPONENTS; ++o) {
			long oppScore = RunGamesForOpp(map, o);
			mapScore += oppScore;
		}
		System.out.println("° Score for all opponents: won: " + (WON - wonBefore) + " draw: " + (DRAW - drawBefore)
				+ " lost: " + (LOST - lostBEFORE));

		return mapScore;
	}

	private static long RunGamesForOpp(String map, int opponent) throws JDOMException, IOException, Exception {
		int wonBefore = WON;
		int drawBefore = DRAW;
		int lostBEFORE = LOST;

		long sideScore = 0;
		for (int s = 0; s < 2; ++s) {
			// System.out.println("**Playing as player: " + s);
			long playerSideScore = RunGamesForOppAndPlayerSide(map, s, opponent);
			sideScore += playerSideScore;
		}
		System.out.println("° Score against opp" + opponent + " : won: " + (WON - wonBefore) + " draw: "
				+ (DRAW - drawBefore) + " lost: " + (LOST - lostBEFORE));
		return sideScore;
	}

	private static long RunGamesForOppAndPlayerSide(String map, int playerSide, int oppIndex)
			throws JDOMException, IOException, Exception {

		long runsScore = 0;
		for (int r = 0; r < RUNS_PER_INDIVIDUAL; ++r) {
			// System.out.println("**Run: " + r);
			long gameScore = RunGame(map, playerSide, oppIndex, r);
			runsScore += gameScore;

		}
		return runsScore;
	}

}
