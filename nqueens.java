import java.util.*;
import java.io.*;

public class nqueens{
    private static final int NO_OF_RUNS = 2000;		//Number of times to run Algorithm 		-TESTING
    private static final double MUTATION_RATE = 1;		//Chance of Genetic Mutation
    private static final int GAME_SIZE = 8;			//Number of Queens and Size of Board
    private static final int POP_SIZE = 10;			//Size of Initial Population
    private static final int EPOCH_LIMIT = 100;		//If this many generations occur restart

    private static ArrayList <String> population = new ArrayList <String>();
    private static int epochs = 0;				//Counter for Epochs until success
    private static int totalepochs = 0;			//Total number of epochs across all Runs 	-TESTING
    private static boolean finished = false;		//Turns true when goal case is reached
    private static Random rand = new Random();		//For Random calculations

    /**
     * Takes A Member and mutates it, changing the location of one piece
     * @param offspring The Member to be mutated
     * @return The Member After mutation
     */
    private static String mutate(String offspring){
        char[] choffspring = offspring.toCharArray();
        choffspring[rand.nextInt(GAME_SIZE)] = (char)(((int)'0')+rand.nextInt(GAME_SIZE));	//Mutates bit
        return (String.valueOf(choffspring));
    }

    /**
     * Takes two members and 'breeds' them returning a new offspring
     * The first half of the first parent is combined with the second half of the second to create a new member
     * This member has a chance to be mutated according to the predetermined MUTATION_RATE
     * @param parentA The array index of the first parent, supplied by the newGeneration Method
     * @param parentB The array index of the second parent, supplied by the newGeneration Method
     * @return The newly created 'child' represented as a String
     */
    private static String breed(int parentA, int parentB){
//		int split = rand.nextInt(GAME_SIZE);		//Random Splits
        int split = GAME_SIZE/2;			//Half Splits.. ONLY ONE OF THESE TWO ACTIVE AT A TIME

        String offspring = population.get(parentA).substring(0,split)
                + population.get(parentB).substring(split, GAME_SIZE);

        if (rand.nextFloat() <= MUTATION_RATE){
            offspring = mutate(offspring);
        }
        return offspring;
    }

    /**
     * If the algorithm has been running for too many generations it clears the entire population and starts again
     * Otherwise it kills the weaker 3/4 of the population
     * Then breeds the remaining members randomly until the population is back up to size
     */
    private static void newGeneration(){
        int count = 0;
        if(epochs%EPOCH_LIMIT==0){ initializePopulation(); } else {

            for (int i = 0; i < POP_SIZE/1.5; i++){
                population.remove(population.size()-1);		//Kill the weaker 3/4 of the population
            }

            while (population.size() < POP_SIZE){		//Breed randomly until population back to size
                population.add(0
                        , breed(rand.nextInt(population.size())
                        , rand.nextInt(population.size()))); 	// Adds at start of array so new pop favoured
                count++;
            }

        }
        epochs++;
    }

    /**
     *  Takes a single population member / game and iterates through each of its pieces
     *  Calculates the total number of attacking pieces
     *  @param positions A String representing an entire game board
     *  @return The number of conflicts / the cost
     */
    private static int numberOfCollisions(String positions){
        int conflicts = 0;
        char[] chpositions = positions.toCharArray();
        for (int i = 0; i < GAME_SIZE - 1; i++){ 	                //last element does not need checked.
            int dcount = 1;			                                //for diagonal calculation
            for (int j = i+1; j < GAME_SIZE; j++){	                //check each element against each other element

                if(chpositions[j] == chpositions[i]){conflicts++;}  // Check Horizontal

                if(chpositions[j] == chpositions[i] + dcount){conflicts++;} // Check Diagonal Up

                if(chpositions[j] == chpositions[i] - dcount){conflicts++;} // Check Diagonal Down

                dcount++;
            }
        }
        return conflicts;
    }

    /**
     * Computes the fitness of each member of the population
     * Sorts the results based on that calculation
     */
    private static void computeFitness(){
        for(int i = 0; i < POP_SIZE; i++){
            String posandfit = population.get(i);
            posandfit = posandfit.substring(0,GAME_SIZE) + numberOfCollisions(posandfit); // add fitness value to end
            population.set(i, posandfit);

        }


        Collections.sort(population, new Comparator<String>() {				//Sort based on fitness
            public int compare(String a, String b) {
                return Integer.signum(fixString(a) - fixString(b));
            }
            private int fixString(String in) {
                return Integer.parseInt(in.substring(GAME_SIZE));
            }
        });
        if (population.get(0).charAt(GAME_SIZE) == '0'){ finished = true;}
    }


    /**
     * Clears the population array and initializes it with new random members
     */
    private static void initializePopulation(){
        population.clear();
        for (int i = 0; i < POP_SIZE; i++){
            String positions = "";
            for (int j = 0; j < GAME_SIZE; j++){
                positions = positions + rand.nextInt(GAME_SIZE); //Generate the positions
            }
            population.add(positions);
        }
    }

    /**
     * Draw a graphical representation of the winning game
     * Q's Represent Queens, -'s Represent empty spaces
     */
    private static void drawGame(){
        char[] winner = population.get(0).toCharArray();
        for (int i = 0; i < GAME_SIZE; i++){
            for (int j = 0; j < GAME_SIZE; j++){
                System.out.print(winner[j] == '7' ? "Q " : "- ");//Print Bottom line, Q for queen else -
                winner[j] = (char)(((int)winner[j])+1);
            }
            System.out.println("");
        }
    }

    /**
     * Prints A representation of the finished game as a String
     * Prints The number of Epochs taken for that particular game
     * Calls drawGame() which will draw a graphical representation of the finished game
     */
    private static void gameOver(){
        System.out.println("FINISHED!! : " + population.get(0).substring(0,GAME_SIZE));
        System.out.println("Epochs : " + epochs);
        drawGame();
    }


    /**
     * Main Method
     * Runs A Genetic Algorithm for solving the NQUEENS problem
     * Set to run Multiple Times for Testing and Demonstration Purposes
     * Prints Stats about the Time and Number of Epochs Taken
     * Calls the gameOver method which Prints a representation of a sample game
     * @param args No args Taken
     */
    public static void main(String[] args) throws IOException{
        final long startTime = System.currentTimeMillis();
        int count = 0;


        while (count < NO_OF_RUNS){		//Repeat algorithm a set number of times
            epochs = 0;
            finished = false;

            initializePopulation();
            computeFitness();

            while(!finished){
                newGeneration();
                computeFitness();
            }

            System.out.println(epochs);
            totalepochs += epochs;
            count++;
        }

        System.err.println("Average Number of Epochs: " + totalepochs/NO_OF_RUNS);
        gameOver(); // Print Some Game Stats, Turn off if you need data for Graphing
        System.err.println("Time Taken: " + (System.currentTimeMillis() - startTime));
    }
}
