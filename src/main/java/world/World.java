package world;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class World<T extends IEntity> implements IWorld<T> {

	private Random random;

	// TODO enable synchronous updates
	private Patch<T>[][] globe;

	private Hashtable<T, Patch<T>> entityIndex;

	private ArrayList<Patch<T>> freePatches;

	public World(int dimension) {
		random = new Random();
		entityIndex = new Hashtable<T, Patch<T>>();
		freePatches = new ArrayList<Patch<T>>();
		this.globe = new Patch[dimension][dimension];

		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				globe[i][j] = new Patch<T>(i, j);
				freePatches.add(globe[i][j]);
			}
			;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see world.IWorld#add(T)
	 */
	@Override
	public void add(T entity) {
		if (!entityIndex.containsKey(entity)) {
			int index = random.nextInt(freePatches.size());
			Patch<T> patch = freePatches.get(index);
			patch.addOccupant(entity);
			freePatches.remove(index);
			entityIndex.put(entity, patch);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see world.IWorld#remove(T)
	 */
	@Override
	public void remove(T entity) {
		Patch<T> patch = entityIndex.remove(entity);
		if (patch != null) {
			patch.removeOccupant(entity);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see world.IWorld#move(T, int)
	 */
	@Override
	public void move(T entity, int scope) {
		List<Patch<T>> neighbourhood = this.nearPatchesOf(entity, scope);
		List<Patch<T>> freeNeighbourPatches = neighbourhood.stream().filter((Patch<T> p) -> !p.containsActive())
				.collect(Collectors.toList());
		Patch<T> newPatch = freeNeighbourPatches.get(random.nextInt(freeNeighbourPatches.size()));

		moveTo(entity, newPatch);
	}

	@Override
	public void moveTo(T entity, T target) {
		moveTo(entity, entityIndex.get(target));
	}

	@Override
	public List<T> neighbourhoodOf(T entity, int scope) {
		List<T> neighbours = nearPatchesOf(entity, scope).stream().map(Patch::getOccupants).flatMap(List::stream)
				.collect(Collectors.toList());
		return neighbours;
	}

	@Override
	public int getDimension() {
		return globe.length;
	}

	private void moveTo(T entity, Patch<T> newPatch) {
		Patch<T> oldPatch = entityIndex.remove(entity);
		oldPatch.removeOccupant(entity);

		newPatch.addOccupant(entity);
		entityIndex.put(entity, newPatch);
	}

	// TODO refactor
	private LinkedList<Patch<T>> nearPatchesOf(T entity, int scope) {
		LinkedList<Patch<T>> nearPatches = new LinkedList<Patch<T>>();
		Patch<T> origin = entityIndex.get(entity);

		int startX = -1;
		int startY = -1;
		int endX = -1;
		int endY = -1;
		int startX2 = -1;
		int startY2 = -1;
		int endX2 = -1;
		int endY2 = -1;
		int diff = -1;

		diff = origin.getxCoordinate() - scope;
		if (diff >= 0) {
			startX = origin.getxCoordinate() - scope;

			diff = this.getDimension() - startX + (2 * scope);
			if (diff < 0) {
				endX = this.getDimension() - 1;
				startX2 = 0;
				endX2 = diff;
			} else {
				endX = startX + (2 * scope);
			}

		} else {
			startX = 0;
			endX = startX + (2 * scope) - Math.abs(origin.getxCoordinate() - scope);
			startX2 = this.getDimension() - Math.abs(origin.getxCoordinate() - scope);
			endX2 = this.getDimension() - 1;
		}

		diff = origin.getyCoordinate() - scope;
		if (diff >= 0) {
			startY = origin.getyCoordinate() - scope;

			diff = this.getDimension() - startY + (2 * scope);
			if (diff < 0) {
				endY = this.getDimension() - 1;
				startY2 = 0;
				endY2 = diff;
			} else {
				endY = startY + (2 * scope);
			}

		} else {
			startY = 0;
			endY = startY + (2 * scope) - Math.abs(origin.getyCoordinate() - scope);
			startY2 = getDimension() - Math.abs(origin.getyCoordinate() - scope);
			endY2 = getDimension() - 1;
		}

		nearPatches.addAll(getPatchfield(startX, startY, endX, endY));
		if (startX2 != -1) {
			nearPatches.addAll(getPatchfield(startX2, startY2, endX2, endY2));
		}

		return nearPatches;
	}

	private LinkedList<Patch<T>> getPatchfield(int x1, int y1, int x2, int y2) {
		LinkedList<Patch<T>> patches = new LinkedList<Patch<T>>();
		for (int i = x1; i < x2; i++) {
			for (int j = y1; j < y2; j++) {
				patches.add(globe[i][j]);
			}
		}
		return patches;
	}

}
