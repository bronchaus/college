// Binary encoded chromosome
// S. Sheridan 27/02/2014
// Institute of Technology Blanchardstown

import java.io.*;
import java.util.*;

public class BinaryEncodedChromosome{
		
	private BitSet genes;			// Chromosome genes
	private int length;				// Chromosome length
	private float fitness;			// Fitness value for chromosome

	public BinaryEncodedChromosome(int length){
		this.length = length;
		
		// Create the gene array
		genes = new BitSet(length);
							
	    // Initialises the genes to random bits
        for (int j=0; j<length; j++)
        {
           if (Math.random() < 0.5) 
			   genes.set(j);
		   else
			   genes.clear(j);
        }
        
	}
	// Returns the BitSet that represents the chromosomes genes
	public BitSet getBitSet(){
		return genes;
	}
	// Returns gene value at given locus
	public boolean getGeneAt(int pos){
		return genes.get(pos);
	}
	// Used for changing a gene value at given locus in chromosome
	public void setGeneAt(int pos, boolean val){
		if (val)
			genes.set(pos);
		else
			genes.clear(pos);
	}
	// Will mutate a gene at given locus by its complement
	public void mutateGeneAt(int pos){
		// TODO
	}
	// Sets the chromosome length
	public void setLength(int length){
		this.length = length;
	}
	// Returns the length of a chromosome
	public int getLength(){
		return length;
	}
	// Sets the fitness value for a chromosome
	public void setFitness(float fitness){
		this.fitness = fitness;
	}
	// Returns the fitness value for a chromosome
	public float getFitness(){
		return fitness;
	}
	// Used to compare chromosomes in order to remove duplicates
	// Compare this chromosome against argument c
	public boolean equals(BinaryEncodedChromosome c){
		return genes.equals(c.getBitSet());
	}
	
	// Convert BitSet into float value
	public float geneToFloat(){
		// TODO - See notes on how to convert 10bit string to binary
	}
	// Helps to show chromosome values on screen
	public String toString(){
		String val  = "";
		for(int i = length - 1; i >= 0; i--){
			if (genes.get(i))
				val = val + "1";
			else
				val = val + "0";
		}
		return val;
	}
	
}