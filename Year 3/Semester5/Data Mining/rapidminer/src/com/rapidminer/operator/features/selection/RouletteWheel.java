/*
 *  RapidMiner
 *
 *  Copyright (C) 2001-2013 by Rapid-I and the contributors
 *
 *  Complete list of developers available at our web site:
 *
 *       http://rapid-i.com
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package com.rapidminer.operator.features.selection;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.rapidminer.operator.features.Individual;
import com.rapidminer.operator.features.Population;
import com.rapidminer.operator.features.PopulationOperator;


/**
 * Selects a given fixed number of individuals by subdividing a roulette wheel
 * in sections of size proportional to the individuals' fitness values.
 * Optionally keep the best individual.
 * 
 * @author Simon Fischer, Ingo Mierswa
 */
public class RouletteWheel implements PopulationOperator {

	private int popSize;

	private boolean keepBest;

    private Random random;
    
	public RouletteWheel(int popSize, boolean keepBest, Random random) {
		this.popSize = popSize;
		this.keepBest = keepBest;
        this.random = random;
	}

	/** The default implementation returns true for every generation. */
	public boolean performOperation(int generation) {
		return true;
	}

	/**
	 * Subclasses may override this method and recalculate the fitness based on
	 * the given one, e.g. Boltzmann selection or scaled selection. The default
	 * implementation simply returns the given fitness.
	 */
	public double filterFitness(double fitness) {
		return fitness;
	}

	public void operate(Population population) {
		List<Individual> newGeneration = new LinkedList<Individual>();

		if (keepBest) {
			newGeneration.add(population.getBestIndividualEver());
		}

		double fitnessSum = 0;
		for (int i = 0; i < population.getNumberOfIndividuals(); i++) {
			fitnessSum += filterFitness(population.get(i).getPerformance().getMainCriterion().getFitness());
		}

		while (newGeneration.size() < popSize) {
			double r = fitnessSum * random.nextDouble();
			int j = 0;
			double f = 0;
			Individual individual = null;
			do {
				if (j >= population.getNumberOfIndividuals()) {
					break;
				}
				individual = population.get(j++);
				f += filterFitness(individual.getPerformance().getMainCriterion().getFitness());
			} while (f < r);
			newGeneration.add(individual);
		}

		population.clear();
		population.addAllIndividuals(newGeneration);
	}
}
