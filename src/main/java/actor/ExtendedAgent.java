package actor;

import world.IWorld;

/**
 * An extended agent's behaves differently such that its perceived government
 * legitimacy is bound to its neighbourhood and increases proportionally with
 * the ratio of jailed agents to all agents nearby.
 * 
 * @author Ferdinand
 *
 */
public class ExtendedAgent extends Agent {

	/**
	 * Creates an extended agent based on the same parameters as a normal agent.
	 * 
	 * @param world
	 * @param vision
	 * @param government_legitimacy
	 * @param movement
	 */
	public ExtendedAgent(IWorld<Turtle> world, int vision, double government_legitimacy,
			boolean movement) {
		super(world, vision, government_legitimacy, movement);
	}

	/**
	 * EXTENSION Returns the individually calculated perceived government
	 * legitimacy on the basis of nearby jailed agents and all agents.
	 * 
	 * @return the calculated individual perceived government legitimacy
	 */
	@Override
	protected double governmentLegitimacy() {
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
