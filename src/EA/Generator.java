/*******************************************************************************
 * Hybrid HTN MCTS Planner 
 * Created by Xenija Neufeld, 2019
 ******************************************************************************/
package EA;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import javax.swing.JFrame;

import org.jdom.JDOMException;

import HTNPlanner.Planner;
import HTNPlanner.PrimitiveTasks.EvaluationFunctions.*;
import PlannerMCTSBot.Hybrid_HTN_MCTS_Planner_Bot;
import ai.MicroCCG_v2;
import ai.PassiveAI;
import ai.ahtn.AHTNAI;
import ai.competition.capivara.Capivara;
import ai.competition.tiamat.Tiamat;
import ai.core.AI;
import ai.evaluation.EvaluationFunction;
import ai.mcts.naivemcts.NaiveMCTS;
import ai.puppet.PuppetSearchMCTS;
import ai.pvai.PVAIML_ED;
import gui.MouseController;
import gui.PhysicalGameStatePanel;
import rts.GameState;
import rts.PartiallyObservableGameState;
import rts.PhysicalGameState;
import rts.PlayerAction;
import rts.units.UnitTypeTable;
import standard.StrategyTactics;
import util.Helper;

public class Generator {
	// public static boolean TRAINING_MODE_ON = true;
	public static boolean TASK_REACHED = false;
	public static boolean TASK_FAILED = false;
	public static int TASK_START_FRAME = -1;

	@SuppressWarnings("rawtypes")
	public static Class FUNCTION_CLASS_TO_TRAIN;

	private static HashMap<Class, Class> EF_CLASS_TO_INDIVIDUAL_CLASS_MAPPING;
	// public static int Helper.MAP_SIZE_TO_TRAIN;

	public static int NUMBER_MAPS;
	public static int NUMBER_OPPONENTS;

	private static boolean VISUALIZE = true;
	// EA related vars
	public static int RUNS_PER_INDIVIDUAL;
	private static int POPULATION_SIZE; // for crossover better keep population size even
	private static int MAX_GENERATIONS;
	private static float MUTATION_PROBABILITY;
	private static float CROSSOVER_PROBABILITY;

	public static Gene[] CURRENT_GENES = null;
	private static int currentGenerationIndex;
	private static Individual[] currentPopulation;
	private static Archive archive;

	private static boolean READ_FROM_ARCHIVE;
	private static String ARCHIVE_FILE_TO_READ;

	// Game related vars
	public static int MAXCYCLES = 4000;
	private static int TIME = 100;
	private static int MAX_ACTIONS = 1000; // 100
	private static int MAX_PLAYOUTS = -1;
	private static int PLAYOUT_TIME = 100;
	private static int MAX_DEPTH = 100; // 10
	private static int RANDOMIZED_AB_REPEATS = 10;
	private static int PERIOD = 20;

	private static String gamesPath;
	private static Path resultPath;

	public static Individual[] CURRENT_INDIVIDUALS = null;

	public static void main(String[] args) throws JDOMException, IOException, Exception {

		if (!Helper.TRAINING_MODE_ON) {
			System.out.println("Training mode off!");
			return;
		}
		InitClassMappings();

		String smallMaps[] = new String[] { 
				"maps/NoWhereToRun9x8.xml",
				 "maps/8x8/basesWorkers8x8A.xml",
				 "maps/8x8/FourBasesWorkers8x8.xml"
		};

		String midsizeMaps[] = new String[] { 
				"maps/16x16/basesWorkers16x16A.xml",
				"maps/16x16/TwoBasesBarracks16x16.xml" };

		String bigMaps[] = new String[] {
				 "maps/BWDistantResources32x32.xml",
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

		currentGenerationIndex = 0;

		Class[] classes = { CollectEF.class, BuildAndDefendEF.class, BuildRangedAndDefendEF.class, AttackAllEF.class,
				AttackMilitaryEF.class, PreventAttackAllEF.class, PreventAttackMilitaryEF.class };

		Class[] indivClasses = { CollectEFIndividual.class, BuildAndDefendEFIndividual.class,
				BuildRangedAndDefendEFIndividual.class, AttackAllEFIndividual.class, AttackMilitaryEFIndividual.class,
				PreventAttackAllEFIndividual.class, PreventAttackMilitaryEFIndividual.class };
		CURRENT_INDIVIDUALS = new Individual[8]; // for each EF
		
		 String archives[] = {"a_midsize_collect", "a_midsize_build", null,"a_midsize_attack", null, "a_midsize_preventall", null};
		 String individualsToRead[] = {"39_3", "34_1", null, "11_6", null, "12_9", null}; 
		// -------------------------------------------------------------------------->
		// Change experiment settings here
		FUNCTION_CLASS_TO_TRAIN = classes[4];
		Helper.MAP_SIZE = 2; // 0 = small, 1 = midsize, 2 = big
		NUMBER_OPPONENTS = 1; 

		VISUALIZE = false;
		Helper.DEBUG_ACTION_EXECUTION = false;

		RUNS_PER_INDIVIDUAL = 1;
		POPULATION_SIZE = 10; // for crossover better keep population size even
		MAX_GENERATIONS = 300;
		MUTATION_PROBABILITY = 0.5f;
		CROSSOVER_PROBABILITY = 0.5f;

		if (Helper.USE_WEIGHTS_FROM_ARCHIVE) {
			for (int c = 0; c < classes.length; ++c) {
				if (archives[c] != null) {
					if (!ReadFinishedArchiveFromFile(archives[c], indivClasses[c], c, individualsToRead[c])) {
						System.out.println("Individual " + indivClasses[c].getName() + individualsToRead[c]
								+ "could not be found");
						return;
					}
				}
			}
		}

		archive = new Archive(POPULATION_SIZE);

		READ_FROM_ARCHIVE = true;
		ARCHIVE_FILE_TO_READ = "61290698";

		// mapsize , class , archive
		if (args.length > 0) {
			Helper.MAP_SIZE = Integer.parseInt(args[0]);

			FUNCTION_CLASS_TO_TRAIN = classes[Integer.parseInt(args[1])];

			if (args.length > 2) {
				READ_FROM_ARCHIVE = true;
				ARCHIVE_FILE_TO_READ = args[2];
			}
		}

		if (READ_FROM_ARCHIVE) {
			if (!ReadArchiveFromFile()) {
				return;
			}
		}
		// -------------------------------------------------------------------------->

		System.out.println("Mapsize: " + Helper.MAP_SIZE);
		System.out.println("Population size: " + POPULATION_SIZE + ", mutation: " + MUTATION_PROBABILITY
				+ " crossover: " + CROSSOVER_PROBABILITY);
		System.out.println("EF class: " + FUNCTION_CLASS_TO_TRAIN.getName());

		NUMBER_MAPS = maps[Helper.MAP_SIZE].length;
		Start(maps[Helper.MAP_SIZE]);

	}

	private static long RunGame(String mapPath, int playerSide, int oppIndex)
			throws JDOMException, IOException, Exception {
		UnitTypeTable utt = new UnitTypeTable();
		PhysicalGameState pgs = PhysicalGameState.load(mapPath, utt);
		GameState gs = new GameState(pgs, utt);
		boolean gameover = false;

		AI[] opponents = {

			//	new Tiamat(utt),
				// new Capivara(utt),
				// new StrategyTactics(utt),
				// new AHTNAI(utt),
				// new PVAIML_ED(utt), 
				// new PuppetSearchMCTS(utt),
				 new NaiveMCTS(utt),
				// new PassiveAI(utt),
		};

		AI me = new Hybrid_HTN_MCTS_Planner_Bot(utt);
		AI opp = opponents[oppIndex];

		Planner.INSTANCE.player = playerSide;

		AI ai1;
		AI ai2;
		if (playerSide == 0) {
			ai1 = me;
			ai2 = opp;
		} else {
			ai1 = opp;
			ai2 = me;
		}

		// AI ai1 = new PlannerMCTSBot(utt);
		// AI ai2 = new NaiveMCTS_PartialObservability(utt);
		// AI ai2 = new Tiamat(utt);
		// AI ai2 = new Capivara(utt);
		// AI ai2 = new PVAIML_ED(utt); //SCV plus
		// AI ai2 = new UTalcaBot(utt); //not working
		// AI ai2 = new PuppetSearchMCTS(utt);

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

		TASK_REACHED = false;
		TASK_FAILED = false;
		TASK_START_FRAME = -1;

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
		} while (!gameover && gs.getTime() < MAXCYCLES && !TASK_REACHED && !TASK_FAILED);

		int frameDiff = gs.getTime() - TASK_START_FRAME;

		if (gameover && gs.winner() == Planner.INSTANCE.player && FUNCTION_CLASS_TO_TRAIN.equals(AttackAllEF.class)) {
			TASK_REACHED = true;
		}

		long result = EvaluateIndividual(gs, frameDiff);

		ai1.gameOver(gs.winner());
		ai2.gameOver(gs.winner());
		ai1.reset();
		ai2.reset();
		if (VISUALIZE) {
			w.dispose();
		}

		return result;
	}

	private static void Start(String[] maps) throws JDOMException, IOException, Exception {
		// needed just to distinguish between different versions
		String time = "" + new java.util.Date().getTime() % 143990887;
		resultPath = Paths.get(gamesPath + "/" + Helper.MAP_SIZE + "/" + time + "/");

		currentPopulation = new Individual[POPULATION_SIZE];

		// create individuals of the correct class
		Class individualClass = EF_CLASS_TO_INDIVIDUAL_CLASS_MAPPING.get(FUNCTION_CLASS_TO_TRAIN);
		Constructor<?> constructor = individualClass.getConstructor(Integer.class);

		for (int i = 0; i < POPULATION_SIZE; ++i) {
			currentPopulation[i] = (Individual) constructor.newInstance(i);
		}

		while (currentGenerationIndex < MAX_GENERATIONS) {
			System.out.println("**Generation: " + currentGenerationIndex);
			for (int i = 0; i < POPULATION_SIZE; ++i) {
				Gene[] prevGenes = currentPopulation[i].GetGenesClone();
				if (currentGenerationIndex > 0) {
					// get from archive
					currentPopulation[i] = archive.archive.get(i).Clone();

					// crossover only with the next neighbor, not with both (unless population size
					// is odd)
					if (i % 2 == 0) {
						int partnerIndex = i == POPULATION_SIZE - 1 ? 0 : i + 1;
						currentPopulation[i].Crossover(currentPopulation[partnerIndex], CROSSOVER_PROBABILITY);
					}
					currentPopulation[i].Mutate(MUTATION_PROBABILITY);
					currentPopulation[i].generationIndex = currentGenerationIndex;
					currentPopulation[i].index = i;
				}
			}

			RunGamesForPopulation(maps);

			currentGenerationIndex++;
		}

	}

	private static void RunGamesForPopulation(String[] maps) throws JDOMException, IOException, Exception {
		int playerSides = 2;

		// POPULATION_SIZE
		long[] scores = new long[POPULATION_SIZE];

		int j = 0;
		for (int i = 0; i < POPULATION_SIZE; ++i) {
			CURRENT_GENES = currentPopulation[i].genes;
			currentPopulation[i].Print();

			System.out.println("**Individual: " + i);

			long individualScore = RunGamesForIndividual(maps);
			scores[i] = individualScore;
		}

		for (int i = 0; i < POPULATION_SIZE; i++) {
			currentPopulation[i].SetScore(scores[i]);
			// update archive
			archive.AddSolution(currentPopulation[i]);
		}

		System.out.println("Best individual so far: ");
		String s = archive.PrintInd();
		System.out.println(s);

		SaveArchive();
	}

	private static long RunGamesForIndividual(String[] maps) throws JDOMException, IOException, Exception {
		long individualScore = 0;
		for (int m = 0; m < maps.length; ++m) {
			System.out.println("**Map: " + maps[m]);
			long mapScore = RunGamesForMap(maps[m]);
			individualScore += mapScore;
		}
		System.out.println("° Score for all maps: " + individualScore);
		return individualScore;
	}

	private static long RunGamesForMap(String map) throws JDOMException, IOException, Exception {
		long mapScore = 0;
		for (int s = 0; s < 2; ++s) {
			System.out.println("**Playing as player: " + s);
			long playerSideScore = RunGamesForPlayerSide(map, s);
			mapScore += playerSideScore;
		}
		// System.out.println("° Score for all sides: " + mapScore);
		return mapScore;
	}

	private static long RunGamesForPlayerSide(String map, int playerSide) throws JDOMException, IOException, Exception {
		long sideScore = 0;

		for (int o = 0; o < NUMBER_OPPONENTS; ++o) {
			System.out.println("**Opponent: " + o);
			long oppScore = RunGamesForOpp(map, playerSide, o);
			sideScore += oppScore;
		}
		// System.out.println("° Score for all opponents: " + sideScore);
		return sideScore;
	}

	private static long RunGamesForOpp(String map, int playerSide, int oppIndex)
			throws JDOMException, IOException, Exception {
		long runsScore = 0;
		for (int r = 0; r < RUNS_PER_INDIVIDUAL; ++r) {
			System.out.println("**Run: " + r);
			long gameScore = RunGame(map, playerSide, oppIndex);
			if (TASK_START_FRAME < 0) {
				System.out.println("Repeat the game"); // TODO: set a limit to repetitions?
				--r;
			} else {
				runsScore += gameScore;
			}
		}
		// System.out.println("° Score for all runs: " + runsScore);
		return runsScore;
	}

	private static long EvaluateIndividual(GameState gs, int frameDiff) {

		long result = 0;
		if (TASK_REACHED && TASK_START_FRAME >= 0) {

			result = -(MAXCYCLES - frameDiff); // we are minimizing the time
			System.out.println("+ Task reached in " + frameDiff + " frames, result:  " + result);
		} else {
			if (TASK_START_FRAME >= 0) {

				result = MAXCYCLES - frameDiff; // the worse the faster it lost
				System.out.println("- Task failed after " + frameDiff + " frames, result:  " + result);
			} else {
				System.out.println("| Task did not start");
			}
		}

		return result;
	}

	private static void SaveArchive() {
		String archiveText = "";
		archiveText += currentGenerationIndex + "\n";

		for (Individual i : archive.archive) {
			archiveText += i.generationIndex + "_" + i.index + "," + i.GetScore() + ":";
			for (Gene chr : i.genes) {
				archiveText += chr.weightValue + ",";
			}
			archiveText += "\n";
		}

		Save(archiveText, currentGenerationIndex);
	}

	private static void Save(String archiveText, int generationIndex) {
		Path individualPath = Paths.get(resultPath + "/archive.txt");
		if (READ_FROM_ARCHIVE) {
			individualPath = Paths
					.get(gamesPath + "/" + Helper.MAP_SIZE + "/" + ARCHIVE_FILE_TO_READ + "/archive.txt");
		}

		try {
			Files.createDirectories(individualPath.getParent());
		} catch (IOException e) {
			e.printStackTrace();
		}

		byte data[] = archiveText.getBytes();

		try (OutputStream out = new BufferedOutputStream(
				Files.newOutputStream(individualPath, CREATE, WRITE, TRUNCATE_EXISTING))) {
			out.write(data, 0, data.length);
			out.flush();
			out.close();
		} catch (IOException x) {
			System.err.println(x);
		}
	}

	private static boolean ReadArchiveFromFile()
			throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, IOException {
		// create individuals of the correct class
		Class individualClass = EF_CLASS_TO_INDIVIDUAL_CLASS_MAPPING.get(FUNCTION_CLASS_TO_TRAIN);
		Constructor<?> constructor = null;
		Individual individual = null;

		constructor = individualClass.getConstructor(Integer.class);
		individual = (Individual) constructor.newInstance(0);

		String individualPath = (gamesPath + "/" + Helper.MAP_SIZE + "/" + ARCHIVE_FILE_TO_READ
				+ "/archive.txt");

		BufferedReader stdInput = new BufferedReader(new FileReader(individualPath));

		String s = null;

		s = stdInput.readLine();
		if (s != null) {
			currentGenerationIndex = Integer.parseInt(s) + 1;
		}

		int numIndividualsRead = 0;
		while ((s = stdInput.readLine()) != null) {
			if (s.contains(":")) {

				int gen = s.indexOf("_", 0);
				int ind = s.indexOf(",", gen);
				int sc = s.indexOf(":", ind);

				String genStr = s.substring(0, gen);
				String indStr = s.substring(gen + 1, ind);
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

				archive.AddSolution(newIndividual);
				++numIndividualsRead;
			}
		}

		if (numIndividualsRead != POPULATION_SIZE) {
			System.out.println("! Incorrect number of individuals read from file !");
			return false;
		}
		return true;
	}

	private static void InitClassMappings() {
		EF_CLASS_TO_INDIVIDUAL_CLASS_MAPPING = new HashMap<>();
		EF_CLASS_TO_INDIVIDUAL_CLASS_MAPPING.put(CollectEF.class, CollectEFIndividual.class);

		EF_CLASS_TO_INDIVIDUAL_CLASS_MAPPING.put(PreventAttackAllEF.class, PreventAttackAllEFIndividual.class);
		EF_CLASS_TO_INDIVIDUAL_CLASS_MAPPING.put(PreventAttackMilitaryEF.class,
				PreventAttackMilitaryEFIndividual.class);

		EF_CLASS_TO_INDIVIDUAL_CLASS_MAPPING.put(BuildAndDefendEF.class, BuildAndDefendEFIndividual.class);
		EF_CLASS_TO_INDIVIDUAL_CLASS_MAPPING.put(BuildRangedAndDefendEF.class, BuildRangedAndDefendEFIndividual.class);

		EF_CLASS_TO_INDIVIDUAL_CLASS_MAPPING.put(AttackAllEF.class, AttackAllEFIndividual.class);
		EF_CLASS_TO_INDIVIDUAL_CLASS_MAPPING.put(AttackMilitaryEF.class, AttackMilitaryEFIndividual.class);
	}

	private static boolean ReadFinishedArchiveFromFile(String archiveFileToRead, Class EFclassName, int classIndex,
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
}
