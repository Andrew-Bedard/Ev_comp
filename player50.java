import org.vu.contest.ContestSubmission;
import org.vu.contest.ContestEvaluation;

import java.util.Random;
import java.util.Arrays;
import java.util.Properties;

public class player50 implements ContestSubmission
{
    private Random rnd_;
    private ContestEvaluation evaluation_;
    private int evaluations_limit_;

    private int popSize;
    private int selectionSize;
    private int nrParents;

    private double mutationAdjustment;
    private double crossOverProb;
    private double[][] population;

    public player50()
    {
        rnd_ = new Random();
    }

    public void setSeed(long seed)
    {
        // Set seed of algorithm's random process
        rnd_.setSeed(seed);
    }

    public void setEvaluation(ContestEvaluation evaluation)
    {
        // Set evaluation problem used in the run
        evaluation_ = evaluation;

        // Get evaluation properties
        Properties props = evaluation.getProperties();
        evaluations_limit_ = Integer.parseInt(props.getProperty("Evaluations"));
        boolean isMultimodal = Boolean.parseBoolean(props.getProperty("Multimodal"));
        boolean hasStructure = Boolean.parseBoolean(props.getProperty("Regular"));
        boolean isSeparable = Boolean.parseBoolean(props.getProperty("Separable"));

        // Change settings(?)
        if(isMultimodal){
            // Do sth
        }else{
            // Do sth else
        }

        // Change settings(?)
        if(hasStructure){
            // Do sth
        }else{
            // Do sth else
        }

        // Change settings(?)
        if(isSeparable){
            // Do sth
        }else{
            // Do sth else
        }

        popSize = 16; // Arbitrary for now. Should be <= evaluations_limit obviously.
        nrParents = 4;
        selectionSize = (int) popSize/nrParents; // selectionSize needs to be a multitude of the nrParents
        mutationAdjustment = 0.03;
        crossOverProb = 1;
        initialize();
    }

    private void initialize() {
        population = new double[popSize][10];
        double[] solution = new double[10];
        // Load the population with random solution
        for (int i=0; i<popSize; i++) {
            // Load a solution with ten random doubles out of [-5, 5]       
            for (int j=0; j<10; j++) {
                solution[j] = 10 * rnd_.nextDouble() - 5;
            }
            population[i] = solution;
        }
    }

    public void run()
    {
        int evals = 0;
        int index;

        double[][] popFitness = new double[popSize][2];
        double[][] parents    = new double[selectionSize][10];
        double[][] children   = new double[selectionSize][10];

        int[] parentsIndices = new int[selectionSize];
        int[] childIndices   = new int[selectionSize];
        
        Quicksort qs = new Quicksort();

        // fill in the fitness with the current population
        popFitness = calculate_fitness(popFitness);
        evals += popSize;

        // Sort the elements by fitness, ascending.
        qs.sort(popFitness);

        while(evals<evaluations_limit_){

            for(int i = 0; i < selectionSize; i++){
                // choose parents
                do{
                    index = popSize - 1 - rnd_.nextInt(selectionSize*2);
                    index = (int) popFitness[ index ][1];
                }while( Arrays.asList(parentsIndices).contains( index ));
                parents[i] = population[index];
                parentsIndices[i] = index;

                // choose index of children to come
                do{
                    index = rnd_.nextInt(selectionSize*2);
                    index = (int) popFitness[ index ][1];
                }while( Arrays.asList(childIndices).contains( index ));
                childIndices[i] = index;

                // when there are 4 (nrParents) new chosen parents, children get created and mutated
                if( i%3 == 0 && i != 0){
                    // children have a chance to be a clone of a parent or get a crossover of 4 parents
                    if (rnd_.nextDouble() > 1 - crossOverProb) {
                        // i-3 is the first index to use for these children and there parents
                        // i-2 the second etc.
                        children[i-3] = crossOver( parents[i-3], parents[i-2], parents[i-1], parents[i]   );
                        children[i-2] = crossOver( parents[i],   parents[i-1], parents[i-2], parents[i-3] );
                        children[i-1] = crossOver( parents[i-1], parents[i],   parents[i-3], parents[i-2] );
                        children[i]   = crossOver( parents[i-2], parents[i-3], parents[i],   parents[i-1] );
                    }
                    else{
                        children[i-3] = parents[i-3];
                        children[i-2] = parents[i-2];
                        children[i-1] = parents[i-1];
                        children[i]   = parents[i];
                    }
                    children[i-3] = mutate(children[i-3]);
                    children[i-2] = mutate(children[i-2]);
                    children[i-1] = mutate(children[i-1]);
                    children[i]   = mutate(children[i]);

                    // the childIndices are the chosen individuals to get replaced by children
                    population[ childIndices[i-3]] = children[i-3];
                    population[ childIndices[i-2]] = children[i-2];
                    population[ childIndices[i-1]] = children[i-1];
                    population[ childIndices[i]]   = children[i];


                    // update the fitnesses
                    //popFitness[ childIndices[i-3]][0] = (double) evaluation_.evaluate( children[i-3]);
                    //popFitness[ childIndices[i-2]][0] = (double) evaluation_.evaluate( children[i-2]);
                    //popFitness[ childIndices[i-1]][0] = (double) evaluation_.evaluate( children[i-1]);
                    //popFitness[ childIndices[i]][0]   = (double) evaluation_.evaluate( children[i]);
                    System.out.println(childIndices[i-3] + "    " + i%3);
                }
                //evals++;
            }

            popFitness = calculate_fitness(popFitness);
            evals += popSize;

            // Sort the elements by fitness, ascending.
            qs.sort(popFitness);
        }
    }
    
    private double[][] calculate_fitness(double[][] fitness){
        for( int i = 0; i < popSize; i++){
            fitness[i][0] = (double) evaluation_.evaluate(population[i]);
            fitness[i][1] = i;
        }
        return fitness;
    }

    private double[] mutate(double[] child) {
        double[] solution = child;

        for (int i = 0; i < 10; i++) {
            solution[i] += rnd_.nextGaussian() * mutationAdjustment;
            if (solution[i] < -5) { 
                solution[i] = -5;
            } else if (solution[i] > 5) { 
                solution[i] = 5;
            }
        }

        return solution;
    }

    private double[] crossOver(double[] parent1, double[] parent2, double[] parent3, double[] parent4) {
        double[] solution = new double[10];

        // choose 3 points in the array to get the solutions of the 4 parents in the child
        int coMidPoint  = rnd_.nextInt(6)+2;
        int coPointStart= rnd_.nextInt(coMidPoint-1)+1;
        int coPointEnd  = coMidPoint + 2 + rnd_.nextInt(8-coMidPoint);

        for (int i=0; i<10; i++) {
            if (i<coPointStart) {
                solution[i] = parent1[i];
            } else if (i<coMidPoint) {
                solution[i] = parent2[i];
            } else if (i<coPointEnd) {
                solution[i] = parent3[i];
            } else {
                solution[i] = parent4[i];
            }
        }

        return solution;
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
    }
}