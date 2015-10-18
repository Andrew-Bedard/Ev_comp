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
    private double[][] population;
    private int popSize;
    private int selectionSize;
    private double mutationAdjustment;
    private double crossOverProb;

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
        }
        else if(hasStructure){
        }
        else if(isSeparable){
        }
        else{
        }

        popSize = 16; // 
        selectionSize = popSize/4; 
        mutationAdjustment = 0.15;
        crossOverProb = 1;
        initialize();
    }

    private void initialize() {
        population = new double[popSize][10];
        double[] solution = new double[10];
        // Load the population with random solutions
        for (int i=0; i<popSize; i++) {
            // Load a solution with ten random doubles out of [-5, 5]       
            for (int j=0; j<10; j++) {
                solution[j] = -5 + 10 * rnd_.nextDouble();
            }
            population[i] = solution;
        }
    }

    public void run()
    {

        int evals = 0;
        double[][] popFitness = new double[popSize][2];
        int[] parentsIndices = new int[selectionSize];
        int[] deceasedIndices = new int[selectionSize];
        Arrays.fill(parentsIndices, -1);
        Arrays.fill(deceasedIndices, -1);
        double[] parent1 = new double[10];
        double[] parent2 = new double[10];
        double[] child1  = new double[10];
        double[] child2  = new double[10];
        double[] parent3 = new double[10];
        double[] parent4 = new double[10];
        double[] child3  = new double[10];
        double[] child4  = new double[10];
        int popIndex;
        Quicksort qs = new Quicksort();

        while(evals<evaluations_limit_){

            int i = 0;
            // choose the parents to reproduce
            while (i < selectionSize) {
                // from the bottom performers
                popIndex = rnd_.nextInt(selectionSize*2); 
                if (!contains(deceasedIndices, popIndex)) {
                    deceasedIndices[i] = popIndex; 
                    i++;
                }
            }

            i = 0;
            // choose the least fit to die out
            while (i < selectionSize) {
                // from the top performers
                popIndex = popSize-1-rnd_.nextInt(selectionSize*2); 
                if (!contains(parentsIndices, popIndex)) {
                    parentsIndices[i] = popIndex; 
                    i++;
                }
            }

            int parent1Index, parent2Index, parent3Index, parent4Index;
            int child1Index, child2Index, child3Index, child4Index;
            // make children with every 4 parents in the selected group
            for (i=0; i<selectionSize; i+=4) {              
                parent1Index = (int) popFitness[parentsIndices[i]][1];
                parent2Index = (int) popFitness[parentsIndices[i+1]][1];
                parent3Index = (int) popFitness[parentsIndices[i+2]][1];
                parent4Index = (int) popFitness[parentsIndices[i+3]][1];
                parent1 = population[parent1Index];
                parent2 = population[parent2Index];
                parent3 = population[parent3Index];
                parent4 = population[parent4Index];
                // do crossover
                if (rnd_.nextDouble() > 1 - crossOverProb) {
                    child1 = crossOver(parent1, parent2, parent3, parent4);
                    child2 = crossOver(parent4, parent3, parent2, parent1);
                    child3 = crossOver(parent3, parent4, parent1, parent2);
                    child4 = crossOver(parent2, parent1, parent4, parent3);
                } else {
                    child1 = parent1;
                    child2 = parent2;
                    child3 = parent3;
                    child4 = parent4;
                }
                // mutate each child
                child1 = mutate(child1);
                child2 = mutate(child2);
                child3 = mutate(child3);
                child4 = mutate(child4);
                child1Index = (int) popFitness[deceasedIndices[i]][1];
                child2Index = (int) popFitness[deceasedIndices[i+1]][1];
                child3Index = (int) popFitness[deceasedIndices[i+2]][1];
                child4Index = (int) popFitness[deceasedIndices[i+3]][1];
                population[child1Index] = child1;
                population[child2Index] = child2;
                population[child3Index] = child3;
                population[child4Index] = child4;
            }

            for (i=0; i<popSize; i++) {
                if (evals > evaluations_limit_) break;
                // load popFitness with the new fitnesses of the population
                popFitness[i][0] = (double) evaluation_.evaluate(population[i]);
                popFitness[i][1] = i;
                evals++;
            }

            // Sort the elements by fitness, ascending, so we know which individuals are the best and worst
            qs.sort(popFitness);
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
        double adjustment = rnd_.nextGaussian()*mutationAdjustment;
        for (int i=0; i<10; i++) {
            solution[i] += rnd_.nextGaussian()*adjustment;
            if (solution[i]<-5) { solution[i]=-5; 
            } else if (solution[i]>5) { solution[i]=5; }
        }
        return solution;
    }

    private double[] crossOver(double[] parent1, double[] parent2, double[] parent3, double[] parent4) {
        double[] solution = new double[10];
        int coMidPoint = rnd_.nextInt(6)+2;
        int coPointStart = rnd_.nextInt(coMidPoint-1)+1;
        int coPointEnd = coMidPoint + 2 + rnd_.nextInt(8-coMidPoint);
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