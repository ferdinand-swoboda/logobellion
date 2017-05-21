package actor;

import java.util.concurrent.ThreadLocalRandom;

import process.Rebellion;
import world.IWorld;

public class Agent extends Turtle {

	private boolean rebel;

	private int jail_term;

	private double risk_aversion;

	private double perceived_hardship;

	private boolean individual_legitimacy;

	private double government_legitimacy;

	private boolean movement;

	public Agent(IWorld<Turtle> world, int vision, boolean individual_legitimacy, double government_legitimacy,
			boolean movement) {
		super(world, vision);
		rebel = false;
		jail_term = 0;
		this.risk_aversion = ThreadLocalRandom.current().nextDouble();
		this.perceived_hardship = ThreadLocalRandom.current().nextDouble();
		this.individual_legitimacy = individual_legitimacy;
		this.government_legitimacy = government_legitimacy;
		this.movement = movement;
	}

	@Override
	public void move() {
		// only move if not jailed and MOVEMENT enabled
		if (jail_term == 0 && movement) {
			super.move();
		}
	}

	@Override
	public void act() {
		// only act if not jailed
		if (jail_term == 0) {
			rebel = (grievance() > net_risk() + Rebellion.threshold) ? true : false;
		}
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

	public void decreaseJailTerm() {
		if (jail_term > 0) {
			setJail_term(getJail_term() - 1);
		}
	}

	private double net_risk() {
		long c = world.neighbourhoodOf(this, vision).stream().filter(p -> p instanceof Cop).count();
		long a = 1 + world.neighbourhoodOf(this, vision).stream()
				.filter(p -> p instanceof Agent && p.isActive() && ((Agent) p).isRebel()).count();
		double estimated_arrest_prob = 1 - Math.pow(2, ((-Rebellion.k) * Math.floor(c / a)));
		return this.risk_aversion * estimated_arrest_prob;
	}

	private double grievance() {
		return this.perceived_hardship * (1 - this.legitimacy());
	}

	private double legitimacy() {
		double legitimacy;
		if (individual_legitimacy) {
			legitimacy = individualLegitimacy();
		} else {
			legitimacy = government_legitimacy;
		}
		return legitimacy;
	}

	private double individualLegitimacy() {
		double legitimacy;
		long nearAgents = world.neighbourhoodOf(this, vision).stream().filter(p -> p instanceof Agent).count();
		long nearJailedAgents = world.neighbourhoodOf(this, vision).stream()
				.filter(p -> p instanceof Agent && !p.isActive()).count();
		legitimacy = (1 + (nearJailedAgents / nearAgents)) * government_legitimacy;
		return legitimacy;
	}

}
