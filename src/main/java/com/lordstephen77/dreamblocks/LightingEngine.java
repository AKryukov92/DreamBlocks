/*
 _(`-')      (`-')  (`-')  _(`-')  _ <-. (`-')  <-.(`-')                               <-.(`-')  (`-').->
( (OO ).-><-.(OO )  ( OO).-/(OO ).-/    \(OO )_  __( OO)    <-.        .->    _         __( OO)  ( OO)_
 \    .'_ ,------,)(,------./ ,---.  ,--./  ,-.)'-'---.\  ,--. )  (`-')----.  \-,-----.'-'. ,--.(_)--\_)
 '`'-..__)|   /`. ' |  .---'| \ /`.\ |   `.'   || .-. (/  |  (`-')( OO).-.  '  |  .--./|  .'   //    _ /
 |  |  ' ||  |_.' |(|  '--. '-'|_.' ||  |'.'|  || '-' `.) |  |OO )( _) | |  | /_) (`-')|      /)\_..`--.
 |  |  / :|  .   .' |  .--'(|  .-.  ||  |   |  || /`'.  |(|  '__ | \|  |)|  | ||  |OO )|  .   ' .-._)   \
 |  '-'  /|  |\  \  |  `---.|  | |  ||  |   |  || '--'  / |     |'  '  '-'  '(_'  '--'\|  |\   \\       /
 `------' `--' '--' `------'`--' `--'`--'   `--'`------'  `-----'    `-----'    `-----'`--' '--' `-----'
                                     _                    _ ___
                                    |_) _    _ ._  _|  __|_  |o._ _  _
                                    |_)(/_\/(_)| |(_| (_)|   ||| | |(/_
                                          /


Copyright (C) 2017 Stefano Peris

nickname: LordStephen77
eMail: lordstephen77@gmail.com
Github: https://github.com/DreamBlocks

is free software: you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along
with this program.  If not, see <http://www.gnu.org/licenses/>.
*/


package com.lordstephen77.dreamblocks;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

public class LightingEngine implements Serializable {
	
	private static final long serialVersionUID = 1L;

	;
	
	public Direction[][] lightFlow;
	
	private int[][] lightValues;
	private int width, height;
	private Tile[][] tiles;
	
	private final boolean isSun;

	public LightingEngine(World world, boolean isSun){
		this.width = world.width;
		this.height = world.height;
		this.tiles = world.tiles;
		this.isSun = isSun;
		lightValues = new int[width][height];
		lightFlow = new Direction[width][height];
		init();
	}
	
	private void init() {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				lightValues[x][y] = 0;
				lightFlow[x][y] = Direction.UNKNOWN;
			}
		}
		LinkedList<LightingPoint> sources = new LinkedList<>();
		if (isSun) {
			for (int x = 0; x < width; x++) {
				sources.addAll(getSunSources(x));
			}
		} else {
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					if (tiles[x][y].type.lightEmitting > 0) {
						lightFlow[x][y] = Direction.SOURCE;
						lightValues[x][y] = tiles[x][y].type.lightEmitting;
					}
					
				}
			}
		}
		spreadLightingDijkstra(sources);
	}
	
	public int getLightValue(int x, int y) {
		return lightValues[x][y];
	}

	public void removedTile(int x, int y) {
		if (!isSun && lightFlow[x][y] == Direction.SOURCE) {
			lightFlow[x][y] = Direction.UNKNOWN;
			resetLighting(x, y);
			return;
		}
		lightFlow[x][y] = Direction.UNKNOWN;
		if (isSun) {
			spreadLightingDijkstra(getSunSources(x));
		}
		LightingPoint p = new LightingPoint(x, y, Direction.UNKNOWN, lightValues[x][y]);
		spreadLightingDijkstra(getNeighbors(p));
	}
	
	public void addedTile(int x, int y) {
		lightFlow[x][y] = Direction.UNKNOWN;
		if (isSun) {
			// redo the column for sun
			boolean sun = true;
			for (int i = 0; i < height; i++) {
				if (tiles[x][i].type.lightBlocking != 0) {
					sun = false;
				}
				if (sun) {
					lightFlow[x][i] = Direction.SOURCE;
				} else {
					lightFlow[x][i] = Direction.UNKNOWN;
				}
			}
		} else if (tiles[x][y].type.lightEmitting > 0) {
			lightValues[x][y] = tiles[x][y].type.lightEmitting;
			lightFlow[x][y] = Direction.SOURCE;
		}
		resetLighting(x, y);
	}
	
	public List<LightingPoint> getSunSources(int column) {
		LinkedList<LightingPoint> sources = new LinkedList<>();
		for (int y = 0; y < height - 1; y++) {
			if (tiles[column][y].type.lightBlocking != 0) {
				break;
			}
			sources.add(new LightingPoint(column, y, Direction.SOURCE, Constants.LIGHT_VALUE_SUN));
		}
		return sources;
	}
	
	public void resetLighting(int x, int y) {
		int left = Math.max(x - Constants.LIGHT_VALUE_SUN, 0);
		int right = Math.min(x + Constants.LIGHT_VALUE_SUN, width - 1);
		int top = Math.max(y - Constants.LIGHT_VALUE_SUN, 0);
		int bottom = Math.min(y + Constants.LIGHT_VALUE_SUN, height - 1);
		List<LightingPoint> sources = new LinkedList<>();
		
		// safely circle around the target zeroed zone
		boolean bufferLeft = (left > 0);
		boolean bufferRight = (right < width - 1);
		boolean bufferTop = (top > 0);
		boolean bufferBottom = (bottom < height - 1);
		if (bufferTop) {
			if (bufferLeft) {
				sources.add(getLightingPoint(left - 1, top - 1));
				zeroLightValue(left - 1, top - 1);
			}
			if (bufferRight) {
				sources.add(getLightingPoint(right + 1, top - 1));
				zeroLightValue(right + 1, top - 1);
			}
			for (int i = left; i <= right; i++) {
				sources.add(getLightingPoint(i, top - 1));
				zeroLightValue(i, top - 1);
			}
		}
		if (bufferBottom) {
			if (bufferLeft) {
				sources.add(getLightingPoint(left - 1, bottom + 1));
				zeroLightValue(left - 1, bottom + 1);
			}
			if (bufferRight) {
				sources.add(getLightingPoint(right + 1, bottom + 1));
				zeroLightValue(right + 1, bottom + 1);
			}
			for (int i = left; i <= right; i++) {
				sources.add(getLightingPoint(i, bottom + 1));
				zeroLightValue(i, bottom + 1);
			}
		}
		if (bufferLeft) {
			for (int i = top; i <= bottom; i++) {
				sources.add(getLightingPoint(left - 1, i));
				zeroLightValue(left - 1, i);
			}
		}
		if (bufferRight) {
			for (int i = top; i <= bottom; i++) {
				sources.add(getLightingPoint(right + 1, i));
				zeroLightValue(right + 1, i);
			}
		}
		for (int i = left; i <= right; i++) {
			for (int j = top; j <= bottom; j++) {
				if (lightFlow[i][j] == Direction.SOURCE) {
					sources.add(getLightingPoint(i, j));
				}
				lightValues[i][j] = 0;
			}
		}
		spreadLightingDijkstra(sources);
	}
	
	private void zeroLightValue(int x, int y) {
		lightValues[x][y] = 0;
	}
	
	private LightingPoint getLightingPoint(int x, int y) {
		return new LightingPoint(x, y, lightFlow[x][y], lightValues[x][y]);
	}

	private LightingPoint makeLightingPoint(int x, int y, Direction dir, int lightingValue){
		return new LightingPoint(x + dir.dx, y + dir.dy, dir, lightingValue);
	}
	
	public static class LightingPoint {
		
		public int x, y, lightValue;
		public Direction flow;

		public LightingPoint(int x, int y, Direction flow, int lightValue) {
			this.x = x;
			this.y = y;
			this.flow = flow;
			this.lightValue = lightValue;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			LightingPoint that = (LightingPoint) o;

			if (x != that.x) return false;
			return y == that.y;
		}

		@Override
		public int hashCode() {
			return x * 13 + y * 17;
			
		}
	}
	
	public class LightValueComparator implements Comparator<LightingPoint> {
		@Override
		public int compare(LightingPoint arg0, LightingPoint arg1) {
			if (arg0.lightValue < arg1.lightValue) {
				return 1;
			} else if (arg0.lightValue > arg1.lightValue) {
				return -1;
			}
			return 0;
		}
	}

	public List<LightingPoint> getNeighbors(LightingPoint p) {
		if (tiles[p.x][p.y].type.lightBlocking == Constants.LIGHT_VALUE_OPAQUE) {
			return new LinkedList<>();
		}
		int newValue = p.lightValue - 1 - tiles[p.x][p.y].type.lightBlocking;
		return getExactNeighbors(p, newValue);
	}



	public List<LightingPoint> getExactNeighbors(LightingPoint p, int lightingValue) {
		int x = p.x;
		int y = p.y;
		LinkedList<LightingPoint> neighbors = new LinkedList<>();

		boolean bufferLeft = (x > 0);
		boolean bufferRight = (x < width - 1);
		boolean bufferUp = (y > 0);
		boolean bufferDown = (y < height - 1);

		if (bufferRight) {
			neighbors.add(makeLightingPoint(x, y, Direction.RIGHT, lightingValue));
			if (bufferUp) {
				neighbors.add(makeLightingPoint(x, y, Direction.UP_RIGHT, lightingValue));
			}
			if (bufferDown) {
				neighbors.add(makeLightingPoint(x, y, Direction.DOWN_RIGHT, lightingValue));
			}
		}
		if (bufferLeft) {
			neighbors.add(makeLightingPoint(x, y, Direction.LEFT, lightingValue));
			if (bufferUp) {
				neighbors.add(makeLightingPoint(x, y, Direction.UP_LEFT, lightingValue));
			}
			if (bufferDown) {
				neighbors.add(makeLightingPoint(x, y, Direction.DOWN_LEFT, lightingValue));
			}
		}
		if (bufferDown) {
			neighbors.add(makeLightingPoint(x, y, Direction.DOWN, lightingValue));
		}
		if (bufferUp) {
			neighbors.add(makeLightingPoint(x, y, Direction.UP, lightingValue));
		}
		return neighbors;
	}

	private void spreadLightingDijkstra(List<LightingPoint> sources) {
		if (sources.isEmpty())
			return;
		HashSet<LightingPoint> out = new HashSet<>();
		PriorityQueue<LightingPoint> in = new PriorityQueue<>(sources.size(),
                new LightValueComparator());
		// consider that the input sources are done (this is not a good assumption if different
		// light sources have different values......)
		out.addAll(sources);
		
		in.addAll(sources);
		while (!in.isEmpty()) {
			LightingPoint current = in.poll();
			out.add(current);
			
			if (current.lightValue <= lightValues[current.x][current.y] || current.lightValue < 0) {
				continue;
			}
			lightValues[current.x][current.y] = current.lightValue;
			lightFlow[current.x][current.y] = current.flow;
			if (lightFlow[current.x][current.y] == Direction.SOURCE
					&& current.flow != Direction.SOURCE) {
				System.out.println("There's a bug in the source map!");
			}
			List<LightingPoint> neighbors = getNeighbors(current);
			for (LightingPoint next : neighbors) {
				if (out.contains(next)) {
					continue;
				}
				in.add(next);
			}
		}
	}
}
