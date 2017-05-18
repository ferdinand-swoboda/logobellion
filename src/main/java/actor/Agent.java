package actor;

import process.Parameters;
import process.Rebellion;
import world.IWorld;

public class Agent extends Person {

	private boolean rebel;

	private int jail_term;

	private double risk_aversion;

	private double perceived_hardship;

	public Agent(IWorld<Person> world, int vision) {
		super(world, vision);
		rebel = false;
		jail_term = 0;
		this.risk_aversion = random.nextDouble();
		this.perceived_hardship = random.nextDouble();
	}

	@Override
	public void move() {
		if (Parameters.MOVEMENT) {
			world.move(this, vision);
		}
	}

	@Override
	public void act() {
		rebel = (grievance() > net_risk() + Rebellion.threshold) ? true : false;
	}

	@Override
	public boolean isActive() {
		return jail_term == 0;
	}

	public boolean isRebel() {
		return rebel;
	}

	public void setRebel(boolean rebel) {
		this.rebel = rebel;
	}

	public int getJail_term() {
		return jail_term;
	}

	public void setJail_term(int jail_term) {
		this.jail_term = jail_term;
	}

	private double net_risk() {
		long c = world.neighbourhoodOf(this, vision).stream().filter(p -> p instanceof Cop).count();
		long a = 1 + world.neighbourhoodOf(this, vision).stream()
				.filter(p -> p instanceof Agent && p.isActive() && ((Agent) p).isRebel()).count();
		double estimated_arrest_prob = 1 - Math.pow(2, ((-Rebellion.k) * Math.floor(c / a)));
		return this.risk_aversion * estimated_arrest_prob;
	}

	private double grievance() {
		return this.perceived_hardship * (1 - this.governmentLegitimacy());
	}

	private double governmentLegitimacy() {
		double legitimacy;
		if (Parameters.INDIVIDUAL_LEGITIMACY) {
			long nearAgents = world.neighbourhoodOf(this, vision).stream().filter(p -> p instanceof Agent).count();
			long nearJailedAgents = world.neighbourhoodOf(this, vision).stream()
					.filter(p -> p instanceof Agent && !p.isActive()).count();
			legitimacy = Parameters.INDIVIDUAL_LEGITIMACY_INCREASE_FACTOR * (nearJailedAgents / nearAgents);
		} else {
			legitimacy = Parameters.GOVERNMENT_LEGITIMACY;
		}
		return legitimacy;
	}

}
