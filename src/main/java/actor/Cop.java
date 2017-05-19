package actor;

import java.util.List;
import java.util.stream.Collectors;

import world.IWorld;

public class Cop extends Person {

	private int max_jail_term;

	public Cop(IWorld<Person> world, int vision, int max_jail_term) {
		super(world, vision);
		this.max_jail_term = max_jail_term;
	}

	@Override
	public boolean isActive() {
		return true;
	}

	@Override
	public void act() {
		List<Person> neighbours = this.world.neighbourhoodOf(this, vision);
		neighbours = neighbours.stream().filter(p -> p instanceof Agent && p.isActive() && ((Agent) p).isRebel())
				.collect(Collectors.toList());
		Agent suspect = (Agent) neighbours.get(random.nextInt(neighbours.size()));
		suspect.setJail_term(random.nextInt(max_jail_term) + 1);
		world.moveTo(this, suspect);
	}

}
