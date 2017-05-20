package world;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class World<T extends IEntity> implements IWorld<T> {

	private Patch<T>[][] currentGlobe;

	private Patch<T>[][] oldGlobe;

	private Hashtable<T, Patch<T>> currentEntityIndex;

	private Hashtable<T, Patch<T>> oldEntityIndex;

	private List<Patch<T>> currentFreePatches;

	private List<Patch<T>> oldFreePatches;

	public World(int dimension) {
		this.currentGlobe = new Patch[dimension][dimension];
		this.oldGlobe = new Patch[dimension][dimension];
		this.currentEntityIndex = new Hashtable<T, Patch<T>>();
		this.oldEntityIndex = new Hashtable<T, Patch<T>>();
		this.currentFreePatches = new LinkedList<Patch<T>>();
		this.oldFreePatches = new LinkedList<Patch<T>>();

		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				currentGlobe[i][j] = new Patch<T>(i, j);
				oldGlobe[i][j] = new Patch<T>(i, j);
				currentFreePatches.add(currentGlobe[i][j]);
				oldFreePatches.add(oldGlobe[i][j]);
			}
		}
	}

	@Override
	public void enter(List<? extends T> entities) {
		Collections.shuffle(currentFreePatches);
		Iterator<Patch<T>> iter = currentFreePatches.iterator();

		for (T entity : entities) {
			Patch<T> patch = iter.next();
			patch.addOccupant(entity);
			iter.remove();
			currentEntityIndex.put(entity, patch);
		}
	}

	@Override
	public void clear() {
		currentEntityIndex.clear();
		currentFreePatches.clear();
		for (int i = 0; i < currentGlobe.length; i++) {
			for (int j = 0; j < currentGlobe[i].length; j++) {
				currentGlobe[i][j].clearOccupants();
				currentFreePatches.add(currentGlobe[i][j]);
			}
		}
	}

	@Override
	public int getDimension() {
		return currentGlobe.length;
	}

	@Override
	public void move(T entity, int scope) {
		Patch<T> patch = oldEntityIndex.get(entity);

		List<Patch<T>> neighbourhood = this.nearPatchesOf(oldGlobe, patch.getxCoordinate(), patch.getyCoordinate(),
				scope);
		List<Patch<T>> freeNeighbourPatches = neighbourhood.stream().filter((Patch<T> p) -> !p.containsActive())
				.collect(Collectors.toList());
		Patch<T> newPatch = freeNeighbourPatches.get(ThreadLocalRandom.current().nextInt(freeNeighbourPatches.size()));

		moveTo(entity, newPatch.getxCoordinate(), newPatch.getyCoordinate());
	}

	@Override
	public void moveTo(T entity, T target) {
		Patch<T> patch = oldEntityIndex.get(target);
		moveTo(entity, patch.getxCoordinate(), patch.getyCoordinate());
	}

	@Override
	public List<T> neighbourhoodOf(T entity, int scope) {
		Patch<T> patch = oldEntityIndex.get(entity);

		List<T> neighbours = nearPatchesOf(oldGlobe, patch.getxCoordinate(), patch.getyCoordinate(), scope).stream()
				.map(Patch::getOccupants).flatMap(List::stream).collect(Collectors.toList());
		return neighbours;
	}

	@Override
	public void synchronise() {
		// take a snapshot of the current globe state and reset the old state to
		// the snapshot state
		snapshot();
	}

	private void snapshot() {

		oldFreePatches.clear();
		oldEntityIndex.clear();
		oldGlobe = new Patch[currentGlobe.length][currentGlobe.length];

		// perform a deep copy of the current globe state; only entities are
		// shallow copied
		for (int i = 0; i < currentGlobe.length; i++) {
			for (int j = 0; j < currentGlobe[i].length; j++) {

				LinkedList<T> occupants = new LinkedList<T>();
				occupants.addAll(currentGlobe[i][j].getOccupants());
				oldGlobe[i][j] = new Patch<T>(i, j, occupants);

				if (!oldGlobe[i][j].isOccupied()) {
					oldFreePatches.add(oldGlobe[i][j]);
				} else {
					for (T entity : oldGlobe[i][j].getOccupants()) {
						oldEntityIndex.put(entity, oldGlobe[i][j]);
					}
				}
			}
		}
	}

	private void moveTo(T entity, int xCoordinate, int yCoordinate) {
		Patch<T> currentPatch = currentEntityIndex.remove(entity);
		currentPatch.removeOccupant(entity);
		if (!currentPatch.isOccupied()) {
			currentFreePatches.add(currentPatch);
		}

		Patch<T> newPatch = currentGlobe[xCoordinate][yCoordinate];
		newPatch.addOccupant(entity);
		currentFreePatches.remove(newPatch);
		currentEntityIndex.put(entity, newPatch);
	}

	// TODO refactor
	private LinkedList<Patch<T>> nearPatchesOf(Patch<T>[][] globe, int xCoordinate, int yCoordinate, int scope) {
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
