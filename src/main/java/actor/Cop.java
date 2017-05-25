package actor;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import world.IWorld;

/**
 * A cop moves around in a world and jails a random rebelling agent in its
 * vision
 * 
 * @author Ferdinand
 *
 */
public class Cop extends Turtle {

	/**
	 * The maximum jail time, a rebelling agent will be sentenced to
	 */
	private int max_jail_term;

	/**
	 * Creates a cop with the given world, vision and maximum jail time
	 * 
	 * @param world
	 *            the world
	 * @param vision
	 *            the vision
	 * @param max_jail_term
	 *            the maximum jail time to sentence rebelling agents to
	 */
	public Cop(IWorld<Turtle> world, int vision, int max_jail_term) {
		super(world, vision);
		this.max_jail_term = max_jail_term;
	}

	/**
	 * Always returns true, since cops are always active
	 */
	@Override
	public boolean isActive() {
		return true;
	}

	/**
	 * Randomly selects a rebelling agent in its vision, sets the rebel's jail
	 * time to a random value between 1 (inclusive) and max_jail_term
	 * (inclusive) and move to the rebel's location
	 */
	@Override
	public void act() {
		List<? extends Turtle> neighbours = this.world.neighbourhoodOf(this, vision);
		List<? extends Turtle> nearbyRebels = neighbours.stream()
				.filter(p -> p instanceof Agent && p.isActive() && ((Agent) p).isRebel())
				.collect(Collectors.toList());

		if (!nearbyRebels.isEmpty()) {
			Agent suspect = (Agent) nearbyRebels
					.get(ThreadLocalRandom.current().nextInt(nearbyRebels.size()));
			suspect.setJail_term(ThreadLocalRandom.current().nextInt(max_jail_term) + 1);
			world.moveTo(this, suspect);
		}
	}

}
