import java.util.*;

public class nqueens{
	private static final double MUTATION_RATE = 0.01;	//Chance of Genetic Mutation
	private static final int GAME_SIZE = 8;			//Number of Queens and Size of Board
//	private static final int SPLIT_POINT = GAME_SIZE / 2;	//Split down the middle when breeding		
	private static final int POP_SIZE = 75;
	private static ArrayList <String> population = new ArrayList <String>();
	private static int epochs = 0;				//Number of Generations before success
	private static int mutations = 0;			//Number of Mutations before success
	private static boolean finished = false;		//Game over man
	private static Random rand = new Random();		//For Random calculations
	private static final int EPOCH_LIMIT = 200000;		//If this many generations occur restart
	private static String mutate(String offspring){
		char[] choffspring = offspring.toCharArray();
		choffspring[rand.nextInt(GAME_SIZE)] = (char)(((int)'0')+rand.nextInt(GAME_SIZE));    
		choffspring[rand.nextInt(GAME_SIZE)] = (char)(((int)'0')+rand.nextInt(GAME_SIZE));    


		mutations++;
		return (String.valueOf(choffspring));

	}

	
	private static String breed(int parentA, int parentB){
		int split = rand.nextInt(GAME_SIZE);
		String offspring = population.get(parentA).substring(0,split)
					+ population.get(parentB).substring(split, GAME_SIZE);
		if (rand.nextFloat() <= MUTATION_RATE){
			offspring = mutate(offspring);
		} 
		return offspring;
	}

	/*
	Note to self: I think that adding on to the end of the array here was causing 
	My algorithm to get stuck at local minima, when the array was sorted the most recently added values
	were getting stuck at the end and then immediately deleted.
	now inserts at beginning instead of end, hopefully this should favor new additions
	*/
	private static void newGeneration(){

		if(epochs%EPOCH_LIMIT==0){ initializePopulation(); } else {

			for (int i = 0; i < POP_SIZE/4; i++){
				population.remove(population.size()-1);			//Kill the weaker half of the population
			}

			while (population.size() < POP_SIZE){		//Breed randomly until population back to size
				population.add(0, breed(rand.nextInt(population.size()), rand.nextInt(population.size())));
			}
		}
	epochs++;
	}

	/*
	check for collisions
	only have to check diagonals forwards
	*/
	private static int numberOfCollisions(String positions){
		int conflicts = 0;
		char[] chpositions = positions.toCharArray();

		for (int i = 0; i < GAME_SIZE - 1; i++){ 	//last element does not need checked.
			int dcount = 1;				//for diagonal calculation
			for (int j = i+1; j < GAME_SIZE; j++){	//check each element against each other element
				
				if(chpositions[j] == chpositions[i]){conflicts++;} // Check Horizontal

				if(chpositions[j] == chpositions[i] + dcount){conflicts++;} // Check Diag Up

				if(chpositions[j] == chpositions[i] - dcount){conflicts++;} // Check Diag Down

				dcount++;

			}	
		}
		return conflicts;
	}


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

	private static void drawGame(){
		char[] winner = population.get(0).toCharArray();
		for (int i = 0; i < GAME_SIZE; i++){
			for (int j = 0; j < GAME_SIZE; j++){
				System.out.print(winner[j] == '7' ? "Q " : "- "); 	//Print Bottom line, Q for queen else .
				winner[j] = (char)(((int)winner[j])+1);

			}
			System.out.println("");
			
		}
	}

	public static void main(String[] args){
		initializePopulation();	
		computeFitness();

		while(!finished){
			newGeneration();
			computeFitness();
		}
		System.out.println("FINISHED!! : " + population.get(0).substring(0,GAME_SIZE) + " Mutations : " + mutations);
		System.out.println("Epochs: " + epochs);
		drawGame();
	}
}