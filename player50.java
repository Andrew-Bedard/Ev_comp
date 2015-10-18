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

    private boolean structured = false;
    private boolean multimodal = false;
    private boolean separable  = false;

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

        if(isMultimodal){
            multimodal = true;
            System.out.println("Multimodal");
        }
        if(hasStructure){
            structured = true;
            System.out.println("Structured");
        }
        if(isSeparable){
            separable = true;
            System.out.println("Separable");
        }

        popSize = 200; // Arbitrary for now. Should be <= evaluations_limit obviously.
        nrParents = 8;
        // Needs to be lager then nrParents, the top 50 and bottom 50 will be chosen for parents and children respectively
        selectionSize = 20;
        mutationAdjustment = 0.015;
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

            if(multimodal){
                // do sth
            }
            if(structured){
                // the chance at crossover and the change at mutation gets smaller the longer this runs
                if(crossOverProb > 0.2){
                    crossOverProb -= evals / (double) evaluations_limit_;
                }
                if(mutationAdjustment > 0.005){
                    mutationAdjustment -= evals / (double) evaluations_limit_;
                }
            }
            if(separable){
                // do sth
            }

            for(int i = 0; i < selectionSize; i++){
                // choose parents
                do{
                    index = popSize - 1 - rnd_.nextInt(selectionSize);
                }while( Arrays.asList(parentsIndices).contains( index ));
                parents[i] = population[index];
                parentsIndices[i] = index;

                // choose index of children to come
                do{
                    index = rnd_.nextInt(selectionSize);
                }while( Arrays.asList(childIndices).contains( index ));
                childIndices[i] = index;

                // when there are 4 (nrParents) new chosen parents, children get created and mutated
                if( i%nrParents-1 == 0 && i != 0){
                    // only 4 parents can be used for the crossover, so if nrParents > 8 the last few won't be used
                    for (int j=0; j<nrParents; j++){
                        if (evals > evaluations_limit_) break;
                        // children have a chance to be a clone of a parent or get a crossover of 4 parents
                        if (rnd_.nextDouble() > 1 - crossOverProb) {
                            children[j] = crossOver(parents[j], parents[j+1%nrParents], parents[j+2%nrParents], parents[j+3%nrParents]);
                        }
                        else{
                            children[j] = parents[j];
                        }
                        children[j] = mutate(children[j]);
                        // the childIndices (the index in popFitness, so popFitness[index][0] is the index in the population)
                        // are the chosen individuals to get replaced by children
                        population[ (int) popFitness[ childIndices[j]][1]] = children[j];
                        // update the fitness of the children
                        popFitness[ childIndices[j]][0] = (double) evaluation_.evaluate( children[j]);
                        evals++;
                    }
                }
            }

            for (int i=0; i<popSize; i++){
                popFitness[i][1] = i;
            }

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
            solution[i] = (parent1[i]+parent2[i]+parent3[i]+parent4[i])/4; 
        }
        /*
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
        */
        return solution;
    }

    public static void main(String[] args) {
        // Neede
    }
}