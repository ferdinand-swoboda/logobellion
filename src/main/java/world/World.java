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
		for (int i = 0; i < getDimension(); i++) {
			for (int j = 0; j < getDimension(); j++) {
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

		if (!freeNeighbourPatches.isEmpty()) {
			Patch<T> newPatch = freeNeighbourPatches
					.get(ThreadLocalRandom.current().nextInt(freeNeighbourPatches.size()));
			moveTo(entity, newPatch);
		}

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

	private LinkedList<Patch<T>> nearPatchesOf(Patch<T> centre, int scope) {
		LinkedList<Patch<T>> nearPatches = new LinkedList<Patch<T>>();

		int startX = mod(centre.getxCoordinate() - scope, getDimension());
		int startY = mod(centre.getyCoordinate() - scope, getDimension());

		for (int i = 0; i < scope; i++) {
			for (int j = 0; j < scope; j++) {
				int indexX = (startX + i) % getDimension();
				int indexY = (startY + j) % getDimension();
				// do not include the centre patch and prevent duplicate patches
				if (indexX != centre.getxCoordinate() && indexY != centre.getyCoordinate()
						&& !nearPatches.contains(globe[indexX][indexY])) {
					nearPatches.add(globe[indexX][indexY]);
				}
			}
		}

		return nearPatches;
	}

	private int mod(int x, int m) {
		int r = x % m;
		return r < 0 ? r + m : r;
	}

}
