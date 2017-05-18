package actor;

import java.util.List;
import java.util.stream.Collectors;

import process.Parameters;
import world.IWorld;

public class Cop extends Person {

	public Cop(IWorld<Person> world, int vision) {
		super(world, vision);
	}

	@Override
	public boolean isActive() {
		return true;
	}

	@Override
	public void move() {
		this.world.move(this, vision);
	}

	@Override
	public void act() {
		List<Person> neighbours = this.world.neighbourhoodOf(this, vision);
		neighbours = neighbours.stream().filter(p -> p instanceof Agent && p.isActive() && ((Agent) p).isRebel())
				.collect(Collectors.toList());
		Agent suspect = (Agent) neighbours.get(random.nextInt(neighbours.size()));
		suspect.setJail_term(random.nextInt(Parameters.MAX_JAIL_TERM) + 1);
		world.moveTo(this, suspect);
	}

}
