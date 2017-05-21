package actor;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import world.IWorld;

public class Cop extends Turtle {

	private int max_jail_term;

	public Cop(IWorld<Turtle> world, int vision, int max_jail_term) {
		super(world, vision);
		this.max_jail_term = max_jail_term;
	}

	@Override
	public boolean isActive() {
		return true;
	}

	@Override
	public void act() {
		List<? extends Turtle> neighbours = this.world.neighbourhoodOf(this, vision);
		List<? extends Turtle> nearbyRebels = neighbours.stream()
				.filter(p -> p instanceof Agent && p.isActive() && ((Agent) p).isRebel()).collect(Collectors.toList());

		if (!nearbyRebels.isEmpty()) {
			Agent suspect = (Agent) nearbyRebels.get(ThreadLocalRandom.current().nextInt(nearbyRebels.size()));
			suspect.setJail_term(ThreadLocalRandom.current().nextInt(max_jail_term) + 1);
			world.moveTo(this, suspect);
		}
	}

}
