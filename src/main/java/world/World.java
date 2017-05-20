package world;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class World<T extends IEntity> implements IWorld<T> {

	private Patch<T>[][] globe;

	private Hashtable<T, Patch<T>> entityIndex;

	private List<Patch<T>> freePatches;

	public World(int dimension) {
		this.globe = new Patch[dimension][dimension];
		this.entityIndex = new Hashtable<T, Patch<T>>();
		this.freePatches = new LinkedList<Patch<T>>();

		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				globe[i][j] = new Patch<T>(i, j);
				freePatches.add(globe[i][j]);
			}
		}
	}

	@Override
	public void enter(List<? extends T> entities) {
		Collections.shuffle(freePatches);
		Iterator<Patch<T>> iter = freePatches.iterator();

		for (T entity : entities) {
			Patch<T> patch = iter.next();
			patch.addOccupant(entity);
			iter.remove();
			entityIndex.put(entity, patch);
		}
	}

	@Override
	public void clear() {
		entityIndex.clear();
		freePatches.clear();
		for (int i = 0; i < globe.length; i++) {
			for (int j = 0; j < globe[i].length; j++) {
				globe[i][j].clearOccupants();
				freePatches.add(globe[i][j]);
			}
		}
	}

	@Override
	public int getDimension() {
		return globe.length;
	}

	@Override
	public void move(T entity, int scope) {
		Patch<T> patch = entityIndex.get(entity);

		List<Patch<T>> neighbourhood = this.nearPatchesOf(patch, scope);
		List<Patch<T>> freeNeighbourPatches = neighbourhood.stream().filter((Patch<T> p) -> !p.containsActive())
				.collect(Collectors.toList());
		Patch<T> newPatch = freeNeighbourPatches.get(ThreadLocalRandom.current().nextInt(freeNeighbourPatches.size()));

		moveTo(entity, newPatch);
	}

	@Override
	public void moveTo(T entity, T target) {
		Patch<T> patch = entityIndex.get(target);
		moveTo(entity, patch);
	}

	@Override
	public List<T> neighbourhoodOf(T entity, int scope) {
		Patch<T> patch = entityIndex.get(entity);

		List<T> neighbours = nearPatchesOf(patch, scope).stream().map(Patch::getOccupants).flatMap(List::stream)
				.collect(Collectors.toList());
		return neighbours;
	}

	private void moveTo(T entity, Patch<T> newPatch) {
		Patch<T> currentPatch = entityIndex.remove(entity);
		currentPatch.removeOccupant(entity);
		if (!currentPatch.isOccupied()) {
			freePatches.add(currentPatch);
		}

		newPatch.addOccupant(entity);
		freePatches.remove(newPatch);
		entityIndex.put(entity, newPatch);
	}

	// TODO refactor
	private LinkedList<Patch<T>> nearPatchesOf(Patch<T> patch, int scope) {
		LinkedList<Patch<T>> nearPatches = new LinkedList<Patch<T>>();
		Patch<T> origin = globe[xCoordinate][yCoordinate];

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

}
