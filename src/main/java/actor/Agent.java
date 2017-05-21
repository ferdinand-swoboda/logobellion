package actor;

import java.util.concurrent.ThreadLocalRandom;

import process.Rebellion;
import world.IWorld;

/**
 * An agent moves around in a world and determines if it becomes a rebel or not
 * 
 * @author Ferdinand
 *
 */
public class Agent extends Turtle {

	/**
	 * whether the agent is rebelling
	 */
	private boolean rebel;

	/**
	 * the current jail time left
	 */
	private int jail_term;

	/**
	 * the individual risk aversion
	 */
	private double risk_aversion;

	/**
	 * the individual perceived hardship
	 */
	private double perceived_hardship;

	/**
	 * whether the government legitimacy should be calculated individually
	 */
	private boolean individual_legitimacy;

	/**
	 * the initial government legitimacy
	 */
	private double government_legitimacy;

	/**
	 * whether this agent is allowed to move
	 */
	private boolean movement;

	/**
	 * Creates an agent and initialises it. The agent's risk aversion and
	 * perceived hardship are assigned random values.
	 * 
	 * @param world
	 * @param vision
	 * @param individual_legitimacy
	 * @param government_legitimacy
	 * @param movement
	 */
	public Agent(IWorld<Turtle> world, int vision, boolean individual_legitimacy,
			double government_legitimacy, boolean movement) {
		super(world, vision);
		rebel = false;
		jail_term = 0;
		this.risk_aversion = ThreadLocalRandom.current().nextDouble();
		this.perceived_hardship = ThreadLocalRandom.current().nextDouble();
		this.individual_legitimacy = individual_legitimacy;
		this.government_legitimacy = government_legitimacy;
		this.movement = movement;
	}

	/**
	 * Moves the agent to another random, unoccupied location in the world
	 * within its vision. If no such location is available or movement is
	 * disabled or the agent is currently jailed, the turtle stays in its
	 * current location.
	 */
	@Override
	public void move() {
		// only move if not jailed and MOVEMENT enabled
		if (jail_term == 0 && movement) {
			super.move();
		}
	}

	/**
	 * Determines if the agent is rebelling or not, if it is not jailed
	 */
	@Override
	public void act() {
		// only act if not jailed
		if (jail_term == 0) {
			rebel = (grievance() > net_risk() + Rebellion.threshold) ? true : false;
		}
	}

	/**
	 * Returns true if and only if the agent is currently not in jail
	 */
	/**
	 * 
	 */
	@Override
	public boolean isActive() {
		return jail_term == 0;
	}

	/**
	 * Returns true if and only if the agent is currently rebelling
	 * 
	 * @return true if rebelling, false otherwise
	 */
	public boolean isRebel() {
		return rebel;
	}

	/**
	 * Sets the rebel status
	 * 
	 * @param rebel
	 *            the new rebel status
	 */
	public void setRebel(boolean rebel) {
		this.rebel = rebel;
	}

	/**
	 * Returns the time left in jail
	 * 
	 * @return
	 */
	public int getJail_term() {
		return jail_term;
	}

	/**
	 * Sets the time left in jail
	 * 
	 * @param jail_term
	 *            the new time left in jail
	 */
	public void setJail_term(int jail_term) {
		this.jail_term = jail_term;
	}

	/**
	 * Reduces the time left in jail by 1 to a minimum value of 0
	 */
	public void decreaseJailTerm() {
		if (jail_term > 0) {
			setJail_term(getJail_term() - 1);
		}
	}

	/**
	 * Determines the agent's net risk
	 * 
	 * @return the agent's net risk
	 */
	private double net_risk() {
		// calculate the number of cops nearby
		long c = world.neighbourhoodOf(this, vision).stream().filter(p -> p instanceof Cop).count();
		// calculate the number of rebelling agents nearby
		long a = 1 + world.neighbourhoodOf(this, vision).stream()
				.filter(p -> p instanceof Agent && p.isActive() && ((Agent) p).isRebel()).count();
		double cop_rebel_ratio = 0;
		if (a != 0) {
			cop_rebel_ratio = Math.floor(c / a);
		}
		// calculate the estimated arrest probability
		double estimated_arrest_prob = 1 - Math.pow(2, ((-Rebellion.k) * cop_rebel_ratio));
		return this.risk_aversion * estimated_arrest_prob;
	}

	/**
	 * Calculates the agent's grievance level
	 * 
	 * @return the agent's grievance level
	 */
	private double grievance() {
		return this.perceived_hardship * (1 - this.governmentLegitimacy());
	}

	/**
	 * Returns the government legitimacy perceived by the agent
	 * 
	 * @return
	 */
	private double governmentLegitimacy() {
		double legitimacy;
		if (individual_legitimacy) {
			// EXTENSION
			// if the extension has been enabled, the government legitimacy will
			// be calculated for the agent individually
			legitimacy = individualGovernmentLegitimacy();
		} else {
			// otherwise the perceived legitimacy is the one set at creation
			legitimacy = government_legitimacy;
		}
		return legitimacy;
	}

	/**
	 * EXTENSION Returns the individually calculated perceived government
	 * legitimacy on the basis of nearby jailed and free agents
	 * 
	 * @return the calculated individual perceived government legitimacy
	 */
	private double individualGovernmentLegitimacy() {
		double legitimacy;
		// calculate the number of nearby agents
		long nearAgents = world.neighbourhoodOf(this, vision).stream()
				.filter(p -> p instanceof Agent).count();
		// calculate the number of nearby jailed agents
		long nearJailedAgents = world.neighbourhoodOf(this, vision).stream()
				.filter(p -> p instanceof Agent && !p.isActive()).count();
		double increaseFactor = 0;
		if (nearAgents != 0) {
			increaseFactor = nearJailedAgents / nearAgents;
		}
		// the perceived government legitimacy increases proportionally with the
		// relative number of nearby jailed agents
		legitimacy = (1 + increaseFactor) * government_legitimacy;
		return legitimacy;
	}

}
