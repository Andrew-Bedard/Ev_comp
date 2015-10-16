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
        selectionSize = popSize/4; // Arbitrary for now. Has to be a multitude of 4!
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
        int popIndex;

        double[] popFitness = new double[popSize];
        double[][] parents  = new double[selectionSize][10];
        double[][] children = new double[selectionSize][10];

        int[] parentsIndices  = new int[selectionSize];
        int[] deceasedIndices = new int[selectionSize];

        Arrays.fill(parentsIndices, -1);
        Arrays.fill(deceasedIndices, -1);

        // fill in the fitness with the current population
        for( int i = 0; i < popSize; i++){
            popFitness[i] = (double) evaluation_.evaluate(population[i]);
        }

        while(evals<evaluations_limit_){

            // choose parents
            for( int i = 0; i < selectionSize; i++){
                // take unique random indices from the top part of the population
                do{
                    index = popSize - 1 - rnd_.nextInt(selectionSize*2);
                }while( parents.contains( population[i] ));
                parents[i] = population[i];
            }

            // Sort the elements by fitness, ascending.
            Arrays.sort(popFitness);
            evals++;
        }
    }

    private boolean contains(final int[] array, final int key) {
        for (final int i : array) {
            if (i == key) {
                return true;
            }
        }
        return false;
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