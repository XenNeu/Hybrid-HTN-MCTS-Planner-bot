/*******************************************************************************
 * Hybrid HTN MCTS Planner 
 * Created by Xenija Neufeld, 2019
 ******************************************************************************/
package ai.abstraction.pathfinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import rts.GameState;
import rts.PhysicalGameState;
import rts.ResourceUsage;
import rts.UnitAction;
import rts.units.Unit;
import rts.units.UnitType;
import util.Pair;

public class SimpleFloodFill {
	// PathFinding altPF=new AStarPathFinding();
	private static final int ALT_THRESHOLD = 0;
	HashMap<Integer, int[][]> cache = new HashMap<Integer, int[][]>();
	boolean free[][] = null;
	int distances[][] = null;
	int w, h;
	int lastFrame = -1;

	public int[][] pathExistsDist(int startX, int startY, int targetpos, GameState gs, ResourceUsage ru) {
		if (pathExists(startX, startY, targetpos, gs, ru)) {
			if (distances == null) {
				PhysicalGameState pgs = gs.getPhysicalGameState();
				distances = new int[pgs.getWidth()][pgs.getHeight()];
				for (int[] row : distances) {
					Arrays.fill(row, 0);
				}
				int a = 0;
			}
			return distances.clone(); // TODO: fix: null if start==target
		}
		return null;
	}

	public boolean pathExists(int startX, int startY, int targetpos, GameState gs, ResourceUsage ru) {
		int x = targetpos % gs.getPhysicalGameState().getWidth();
		int y = targetpos / gs.getPhysicalGameState().getWidth();

		if (startX == x && startY == y) {
			return true;
		}
		if (findPath(startX, startY, targetpos, gs, ru) != null) {
			return true;
		}
		return false;
	}

	public boolean pathToPositionInRangeExists(int startX, int startY, int targetpos, int range, GameState gs,
			ResourceUsage ru) {
		int x = targetpos % gs.getPhysicalGameState().getWidth();
		int y = targetpos / gs.getPhysicalGameState().getWidth();
		int d = (x - startX) * (x - startX) + (y - startY) * (y - startY);
		if (d <= range * range)
			return true;
		if (findPathToPositionInRange(startX, startY, targetpos, range, gs, ru) != null)
			return true;
		return false;
	}

	public UnitAction findPath(int startX, int startY, int targetpos, GameState gs, ResourceUsage ru) {
		return findPathToPositionInRange(startX, startY, targetpos, 0, gs, ru);
	}

	private boolean bounds(int x, int y) {
		return x >= 0 && y >= 0 && x < w && y < h;
	}

	private void doFloodFill(int x, int y, GameState gs, int finalX, int finalY) {
		assert (distances[x][y] != Integer.MAX_VALUE);
		boolean gsFree[][] = gs.getPhysicalGameState().getAllFree();

		int fx = x;
		int fy = y;

		int index = 0;
		ArrayList<Pair<Integer, Integer>> fringe = new ArrayList<Pair<Integer, Integer>>(h * w);
		fringe.add(new Pair<Integer, Integer>(x, y));
		boolean reached = false;
		while (index < fringe.size()) {
			x = fringe.get(index).m_a;
			y = fringe.get(index).m_b;

			// left
			int nextX = x - 1;
			int nextY = y;
			if (nextX == finalX && nextY == finalY)
				reached = true;
			if (bounds(nextX, nextY) && distances[nextX][nextY] == Integer.MAX_VALUE
					&& gsFree(nextX, nextY, gsFree, gs.getPhysicalGameState())) {
				distances[nextX][nextY] = distances[x][y] + 1;
				fringe.add(new Pair<Integer, Integer>(nextX, nextY));
			}

			// up
			nextX = x;
			nextY = y - 1;
			if (nextX == finalX && nextY == finalY)
				reached = true;
			if (bounds(nextX, nextY) && distances[nextX][nextY] == Integer.MAX_VALUE
					&& gsFree(nextX, nextY, gsFree, gs.getPhysicalGameState())) {
				distances[nextX][nextY] = distances[x][y] + 1;
				fringe.add(new Pair<Integer, Integer>(nextX, nextY));
			}

			// right
			nextX = x + 1;
			nextY = y;
			if (nextX == finalX && nextY == finalY)
				reached = true;
			if (bounds(nextX, nextY) && distances[nextX][nextY] == Integer.MAX_VALUE
					&& gsFree(nextX, nextY, gsFree, gs.getPhysicalGameState())) {
				distances[nextX][nextY] = distances[x][y] + 1;
				fringe.add(new Pair<Integer, Integer>(nextX, nextY));
			}

			// down
			nextX = x;
			nextY = y + 1;
			if (nextX == finalX && nextY == finalY)
				reached = true;
			if (bounds(nextX, nextY) && distances[nextX][nextY] == Integer.MAX_VALUE
					&& gsFree(nextX, nextY, gsFree, gs.getPhysicalGameState())) {
				distances[nextX][nextY] = distances[x][y] + 1;
				fringe.add(new Pair<Integer, Integer>(nextX, nextY));
			}
			if (reached) {
				// System.out.println("breaking early");
				break;
			}
			index++;
		}

		for (int yi = 0; yi < distances[0].length; ++yi) {
			String line = "";
			for (int xi = 0; xi < distances.length; ++xi) {
				if (xi == fx && yi == fy) {
					line += "S";
				} else if (xi == finalX && yi == finalY) {
					line += "F";
				} else {
					if (distances[xi][yi] != Integer.MAX_VALUE) {
						line += Integer.toString(distances[xi][yi]);
					} else {
						line += "X";
					}
				}
			}
			// System.out.println(line);
		}
		// System.out.println("###");
	}

	private void doFloodFill(int x, int y, GameState gs) {
		assert (distances[x][y] != Integer.MAX_VALUE);
		boolean gsFree[][] = gs.getPhysicalGameState().getAllFree();

		int fx = x;
		int fy = y;

		int index = 0;
		ArrayList<Pair<Integer, Integer>> fringe = new ArrayList<Pair<Integer, Integer>>(h * w);
		fringe.add(new Pair<Integer, Integer>(x, y));
		boolean reached = false;
		while (index < fringe.size()) {
			x = fringe.get(index).m_a;
			y = fringe.get(index).m_b;

			// left
			int nextX = x - 1;
			int nextY = y;
			if (bounds(nextX, nextY) && distances[nextX][nextY] == Integer.MAX_VALUE
					&& gsFree(nextX, nextY, gsFree, gs.getPhysicalGameState())) {
				distances[nextX][nextY] = distances[x][y] + 1;
				fringe.add(new Pair<Integer, Integer>(nextX, nextY));
			}

			// up
			nextX = x;
			nextY = y - 1;
			if (bounds(nextX, nextY) && distances[nextX][nextY] == Integer.MAX_VALUE
					&& gsFree(nextX, nextY, gsFree, gs.getPhysicalGameState())) {
				distances[nextX][nextY] = distances[x][y] + 1;
				fringe.add(new Pair<Integer, Integer>(nextX, nextY));
			}

			// right
			nextX = x + 1;
			nextY = y;
			if (bounds(nextX, nextY) && distances[nextX][nextY] == Integer.MAX_VALUE
					&& gsFree(nextX, nextY, gsFree, gs.getPhysicalGameState())) {
				distances[nextX][nextY] = distances[x][y] + 1;
				fringe.add(new Pair<Integer, Integer>(nextX, nextY));
			}

			// down
			nextX = x;
			nextY = y + 1;
			if (bounds(nextX, nextY) && distances[nextX][nextY] == Integer.MAX_VALUE
					&& gsFree(nextX, nextY, gsFree, gs.getPhysicalGameState())) {
				distances[nextX][nextY] = distances[x][y] + 1;
				fringe.add(new Pair<Integer, Integer>(nextX, nextY));
			}
			if (reached) {
				// System.out.println("breaking early");
				break;
			}
			index++;
		}
		/*
		 * for(int yi = 0; yi < distances[0].length; ++yi) { String line =""; for(int
		 * xi=0; xi<distances.length; ++xi) { if(xi == fx && yi == fy) { line += "S"; }
		 * else { if(distances[xi][yi]!= Integer.MAX_VALUE) { line +=
		 * Integer.toString(distances[xi][yi]); } else { line += "X"; } } }
		 * System.out.println(line); } System.out.println("###");
		 */
	}

	private boolean gsFree(int x, int y, boolean gsFree[][], PhysicalGameState pgs) {
		if (gsFree[x][y]) {
			return true;
		}

		Unit u = pgs.getUnitAt(x, y);
		if (u == null) {
			return false;
		}

		UnitType utype = u.getType();
		if (!utype.canMove) {
			return false;
		}
		return true;
	}

	private UnitAction calculateDistances(int startX, int startY, int targetpos, int range, GameState gs,
			ResourceUsage ru) {
		int x = targetpos % w;
		int y = targetpos / w;

		// if(Math.abs(start.getX()-x) +Math.abs(start.getY()-y) <= ALT_THRESHOLD){
		// return altPF.findPathToPositionInRange(start, targetpos, range, gs, ru);
		// }
		// if (distances==null || distances.length<w || distances[0].length<h) {
		distances = new int[w][h];
		// }
		for (int[] row : distances) {
			Arrays.fill(row, Integer.MAX_VALUE);
		}

		distances[x][y] = 0;
		doFloodFill(x, y, gs, startX, startY);
		cache.put(targetpos, distances);
		return getAction(startX, startY);
	}

	public int[][] calculateDistancesWithoutTarget(int startX, int startY, int range, GameState gs, ResourceUsage ru) {
		PhysicalGameState pgs = gs.getPhysicalGameState();
		w = pgs.getWidth();
		h = pgs.getHeight();
		distances = new int[w][h];

		for (int[] row : distances) {
			Arrays.fill(row, Integer.MAX_VALUE);
		}

		distances[startX][startY] = 0;
		doFloodFill(startX, startY, gs);
		// cache.put(targetpos, distances);
		return distances.clone();
	}

	private void initFree(GameState gs, ResourceUsage ru) {
		if (free == null || free.length < w || free[0].length < h) {
			free = new boolean[w][h];
		}
		for (boolean row[] : free) {
			Arrays.fill(row, true);
		}

		if (ru != null) {
			for (int pos : ru.getPositionsUsed()) {
				free[pos % w][pos / w] = false;
			}
		}
	}

	private UnitAction getAction(int startX, int startY) {
		int x = startX;
		int y = startY;

		int dists[] = { bounds(x - 1, y) ? distances[x - 1][y] : Integer.MAX_VALUE,
				bounds(x, y - 1) ? distances[x][y - 1] : Integer.MAX_VALUE,
				bounds(x + 1, y) ? distances[x + 1][y] : Integer.MAX_VALUE,
				bounds(x, y + 1) ? distances[x][y + 1] : Integer.MAX_VALUE };
		int index = 0, min = dists[0];
		for (int i = 1; i < dists.length; i++) {
			if (dists[i] < min) {
				index = i;
				min = dists[i];
			}
		}
		if (min == Integer.MAX_VALUE) {
			return null;
		}
		switch (index) {
		case 0:
			return new UnitAction(UnitAction.TYPE_MOVE, UnitAction.DIRECTION_LEFT);
		case 1:
			return new UnitAction(UnitAction.TYPE_MOVE, UnitAction.DIRECTION_UP);
		case 2:
			return new UnitAction(UnitAction.TYPE_MOVE, UnitAction.DIRECTION_RIGHT);
		case 3:
			return new UnitAction(UnitAction.TYPE_MOVE, UnitAction.DIRECTION_DOWN);
		}
		return null;

	}

	public UnitAction findPathToPositionInRange(int startX, int startY, int targetpos, int range, GameState gs,
			ResourceUsage ru) {
		// System.out.println(range);
		PhysicalGameState pgs = gs.getPhysicalGameState();
		w = pgs.getWidth();
		h = pgs.getHeight();

		int x = targetpos % w;
		int y = targetpos / w;
		// System.out.print(x+" "+y+" ");
		if ((startX - x) * (startX - x) + (startY - y) * (startY - y) <= range * range) {// already there
			// System.out.println("Already in range");
			return null;
		}
		if (gs.getTime() < lastFrame) {// new game
			cache.clear();
			// System.out.println("Removed, new game");
		}
		lastFrame = gs.getTime();
		initFree(gs, ru);
		if (cache.containsKey(targetpos)) {
			distances = cache.get(targetpos);
			UnitAction action = getAction(startX, startY);
			if (action != null) {
				/*
				 * ResourceUsage r=action.resourceUsage(start, pgs); for(int
				 * pos:r.getPositionsUsed()){ if(!free[pos%w][pos/w]||!gsFree(pos%w,pos/w,
				 * gs.getAllFree(),pgs)) //!gs.free(pos%w, pos/w)) { cache.remove(targetpos); //
				 * System.out.println("In cache, invalid, calculating"); return
				 * calculateDistances(startX, startY, targetpos, range,gs,ru); } }
				 */
				return calculateDistances(startX, startY, targetpos, range, gs, ru);
			} else {
				// System.out.println("found null action");
				// this is to fix cases where there used to be no path to get somewhere, but now
				// there is
				cache.remove(targetpos);
				return calculateDistances(startX, startY, targetpos, range, gs, ru);
			}
			// System.out.println("In cache, OK");
			// return action;
		} else {
			// System.out.println("Not in cache, calculating");
			return calculateDistances(startX, startY, targetpos, range, gs, ru);
		}
	}

	public UnitAction findPathToAdjacentPosition(int startX, int startY, int targetpos, GameState gs,
			ResourceUsage ru) {
		return findPathToPositionInRange(startX, startY, targetpos, 1, gs, ru);
	}

}