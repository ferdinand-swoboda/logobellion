package world;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * A world represents a two dimensional square matrix of patches that wraps
 * around. A world can be used to store and move entities within it.
 * 
 * @author Ferdinand
 *
 * @param <T>
 *            the type of entities to be managed by this world
 */
public class World<T extends IEntity> implements IWorld<T> {

	/**
	 * The square matrix of patches
	 */
	private Patch<T>[][] globe;

	/**
	 * entity index to map entities to their patches
	 */
	private Hashtable<T, Patch<T>> entityIndex;

	/**
	 * list of free, i.e. empty patches (!= unoccupied patches)
	 */
	private List<Patch<T>> freePatches;

	/**
	 * Creates a world with the given scale
	 * 
	 * @param scale
	 *            the given scale
	 */
	public World(int scale) {
		this.globe = new Patch[scale][scale];
		this.entityIndex = new Hashtable<T, Patch<T>>();
		this.freePatches = new LinkedList<Patch<T>>();

		for (int i = 0; i < scale; i++) {
			for (int j = 0; j < scale; j++) {
				globe[i][j] = new Patch<T>(i, j);
				// all patches are initially empty
				freePatches.add(globe[i][j]);
			}
		}
	}

	/**
	 * Assigns the given entities to random locations within the world. The
	 * number of given entities should be less than the number of available
	 * locations in the world.
	 * 
	 * @param entities
	 *            the entities to be added to the world
	 */
	@Override
	public void enter(List<? extends T> entities) {
		// randomise the free patches
		Collections.shuffle(freePatches);
		Iterator<Patch<T>> iter = freePatches.iterator();

		for (T entity : entities) {
			Patch<T> patch = iter.next();
			// allocate an entity to a free patch
			patch.addOccupant(entity);
			iter.remove();
			// update the entity index accordingly
			entityIndex.put(entity, patch);
		}
	}

	/**
	 * Resets the world to an empty state
	 */
	@Override
	public void clear() {
		entityIndex.clear();
		freePatches.clear();
		// clear the square matrix and update the free patches
		for (int i = 0; i < getScale(); i++) {
			for (int j = 0; j < getScale(); j++) {
				globe[i][j].clearOccupants();
				freePatches.add(globe[i][j]);
			}
		}
	}

	/**
	 * Returns the scale of the two-dimensional square world
	 * 
	 * @return the scale
	 */
	@Override
	public int getScale() {
		return globe.length;
	}

	/**
	 * Moves the given entity to another random, unoccupied location in the
	 * world within the given scope. If no such location is available, the
	 * entity stays in its current location.
	 * 
	 * @param entity
	 *            the given entity
	 * @param scope
	 *            the given scope
	 */
	@Override
	public void move(T entity, int scope) {
		Patch<T> patch = entityIndex.get(entity);

		List<Patch<T>> neighbourhood = this.nearPatchesOf(patch, scope);
		// calculate the list of nearby unoccupied or occupied (by inactive
		// entities) patches
		List<Patch<T>> freeNeighbourPatches = neighbourhood.stream()
				.filter((Patch<T> p) -> !p.containsActive()).collect(Collectors.toList());

		if (!freeNeighbourPatches.isEmpty()) {
			// assign the given entity to a random free patch nearby
			Patch<T> newPatch = freeNeighbourPatches
					.get(ThreadLocalRandom.current().nextInt(freeNeighbourPatches.size()));
			moveTo(entity, newPatch);
		}
		// if no free nearby patch is available, the entity will stay on its
		// current patch

	}

	/**
	 * Moves the given entity to a target entity's location
	 * 
	 * @param entity
	 *            the given entity to be moved
	 * @param target
	 *            the target entity to whose location to move to
	 */
	@Override
	public void moveTo(T entity, T target) {
		Patch<T> patch = entityIndex.get(target);
		moveTo(entity, patch);
	}

	/**
	 * Returns the neighbourhood of the given entity. The neighbourhood is
	 * comprised of all entities within the given scope of the given entity.
	 * 
	 * @param entity
	 *            the given entity
	 * @param scope
	 *            the given scope
	 * @return the list of nearby entities
	 */
	@Override
	public List<T> neighbourhoodOf(T entity, int scope) {
		Patch<T> patch = entityIndex.get(entity);
		// calculate the list of nearby turtles based on the list of nearby
		// patches and their occupants
		List<T> neighbours = nearPatchesOf(patch, scope).stream().map(Patch::getOccupants)
				.flatMap(List::stream).collect(Collectors.toList());
		return neighbours;
	}

	/**
	 * Moves the given entity to the given new patch
	 * 
	 * @param entity
	 *            the given entity to be moved
	 * @param newPatch
	 *            the new patch to move the entity to
	 */
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

	/**
	 * Returns the list of patches within the given scope of the given patch.
	 * The given patch is not part of the result and the result does not contain
	 * duplicate patches.
	 * 
	 * @param centre
	 *            the given patch
	 * @param scope
	 *            the given scope that specifies the width or length of the
	 *            nearby square patch field around the centre patch
	 * @return the patches within the square-formed scope of the centre patch
	 */
	private LinkedList<Patch<T>> nearPatchesOf(Patch<T> centre, int scope) {
		LinkedList<Patch<T>> nearPatches = new LinkedList<Patch<T>>();

		// calculate the start coordinates of the upper left corner of the
		// square patch field around the centre patch;
		// consider that the globe matrix wraps around
		int startX = mod(centre.getxCoordinate() - scope, getScale());
		int startY = mod(centre.getyCoordinate() - scope, getScale());

		// add the patches of the (scope + 1)*(scope + 1) matrix around the
		// centre patch
		// to the list of nearby patches
		for (int i = 0; i < 2 * scope + 1; i++) {
			for (int j = 0; j < 2 * scope + 1; j++) {
				// consider that the globe matrix wraps around
				int indexX = (startX + i) % getScale();
				int indexY = (startY + j) % getScale();
				// do not include the centre patch and prevent duplicate patches
				// being added
				if (!(indexX == centre.getxCoordinate() && indexY == centre.getyCoordinate())
						&& !nearPatches.contains(globe[indexX][indexY])) {
					nearPatches.add(globe[indexX][indexY]);
				}
			}
		}

		return nearPatches;
	}

	/**
	 * calculates the mathematical x modulo m
	 * 
	 * @param x
	 * @param m
	 * @return x mod m (mathematical)
	 */
	private int mod(int x, int m) {
		int r = x % m;
		return r < 0 ? r + m : r;
	}

}
