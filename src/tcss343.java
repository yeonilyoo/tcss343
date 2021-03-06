import java.io.*;
import java.util.Random;
import java.util.Stack;

/**
 * Yeonil Yoo, Antonio Orozco, Zack Parker
 * Programming Assignment for TCSS343
 */
public class tcss343 {
    public static void main(String[] args) throws IOException {
        //createTestFiles(); //Creates random files filled with test arrays
        try {
            double startTime, endTime;
            //Arguments are given as "sample_input.txt" "test_matrix0.txt" "test_matrix1.txt"...
            //Buf even if 1 arguments, program works fine :)
            for(int i = 0; i < args.length; i++) {
                int[][] array = readFile(args[i]); //Read array from file
                //Run Dynamic
                startTime = System.currentTimeMillis();
                dynamic(array);
                endTime = System.currentTimeMillis();
                writeTimeResultsToFile("Dynamic", startTime, endTime, array.length);
                //Run Divide and Conquer
                startTime = System.currentTimeMillis();
                divideAndConquer(array);
                endTime = System.currentTimeMillis();
                writeTimeResultsToFile("Divide and Conquer", startTime, endTime, array.length);
                //Run Brute Force
                startTime = System.currentTimeMillis();
                bruteforce(array);
                endTime = System.currentTimeMillis();
                writeTimeResultsToFile("Brute Force", startTime, endTime, array.length);
            }
        } catch(IOException e) {
            System.out.println(e);
        }
    }

    /**
     * writeTimeResultsToFile is being called from main method to store data to time_results.csv file.
     * It will take start and end time of algorithm and automatically compute the run time of algorithm
     * @param method_name string that will store data if it is dynamic, brute force, or divide and conquer
     * @param start start time of algorithm
     * @param end end time of algorithm
     * @param size size of the array
     */
    private static void writeTimeResultsToFile(String method_name, double start, double end, int size) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File("time_results.csv"), true));
            bw.write(method_name + "\n");
            bw.write(size + "," + ((end - start) / 1000) + "\n");
            bw.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("Done Printing Results...");
    }

    /**
     * createTestFile creates multiple text file by arrayToText with given array size by using createMatrix
     */
    private static void createTestFiles(){
        int[][] testArray;
        try {
            testArray = createMatrix(1000);
            arrayToText(testArray, "test_matrix0.txt");
            testArray = createMatrix(2000);
            arrayToText(testArray, "test_matrix1.txt");
            testArray = createMatrix(3000);
            arrayToText(testArray, "test_matrix2.txt");
            testArray = createMatrix(4000);
            arrayToText(testArray, "test_matrix3.txt");
            testArray = createMatrix(5000);
            arrayToText(testArray, "test_matrix4.txt");
            testArray = createMatrix(6000);
            arrayToText(testArray, "test_matrix5.txt");
            testArray = createMatrix(7000);
            arrayToText(testArray, "test_matrix6.txt");
            testArray = createMatrix(8000);
            arrayToText(testArray, "test_matrix7.txt");
        } catch (Exception e) {

        }

    }


    /**
     * bruteforce finds lowest cost and path using brute force, checking every possibility
     * @param array 2D array to find the minimum
     */
    public static void bruteforce(int[][] array) {
        int shortest[] = new int[array.length-2];   //Storage that will contain shortest path
        int cheapest = array[0][array.length-1];    //Initializing cheapest path to from 0 to n
        int storage[] = new int[array.length-2];    //Storage that will contain for testing path
        //Storage points from array's 1~n-1, so it's off by 1
        for(int j = 0; j < array.length-2; j++)     //Setting path to zero
            shortest[j] = 0;

        for(long i = 1; i < Math.pow(2, array.length-2); i++) { //Loop 2^n times, brute force every possibility
            int cost = 0;                                       //cost which keeps track new path's cost
            int pointer = 0;                                    //Pointer which keeps track row
            //Setting up bits
            //System.out.println(i);
            for(int j = 0; j < array.length-2; j++) {           //Checking if Jth bit is active
                if((i & (1L << j)) != 0) {
                    storage[j] = 1;
                    cost += array[pointer][j+1];                //Add path's cost
                    pointer = j+1;                              //jump the row
                } else
                    storage[j]=0;
            }

            cost += array[pointer][array.length-1];             //Add final path cost
            if(cost < cheapest) {                               //Check if cost is cheaper than before
                cheapest = cost;
                for(int j = 0; j < array.length-2; j++) //rewrite path, but array is pointer so writing one by one
                    shortest[j] = storage[j];
            }
        }
        //Printing result of brute force
        System.out.println("Brute Force : " + cheapest);
        System.out.print("Brute Force Path : [0");
        for(int j = 0; j < array.length-2; j++)
            if(shortest[j] == 1)
                System.out.print(" -> " + (j+1));
        System.out.println(" -> " + (array.length-1) + "]");
    }

    /**
     * divideAndConquer prints the lowest value and the solution.
     * This method implements shortest path algorithm without using graph.
     * Instead of graph, it uses array's index as vertex and array's value as edge's weight.
     * @param array 2D array to find the minimum
     */
    public static void divideAndConquer(int[][] array) {
        Datapath n = new tcss343().divideAndConquer(array, array.length - 1);
        System.out.println("Minimum Weight Divide and Conquer: "+ n.value);
        System.out.println("Divide and Conquer: [ 0 -> " + n.path+ " ]");
        System.out.println("Divide and Conquer: "+ n.value);
        System.out.println("Divide and Conquer Path: " + n.path);
    }

    /**
     * divideAndConquer finds the lowest cost using divide and conquer
     * @param array 2D int array to find the minimum
     * @param point the int point in the matix that is final destination
     */
    public Datapath divideAndConquer(int[][] array, int point) {
        int min = array[0][point];
        String path = Integer.toString(point);
        for(int i = 1; i < point; i++) {                //Go through every possible route to reach current
            Datapath data = divideAndConquer(array, i); //Recurive call divideAndConquer to find other route cost
            int temp = data.value + array[i][point];    //Add previous route cost and previous route to current cost
            if(temp < min) {                            //Compare to find which cost less
                min = temp;                             //Replace cost
                path = data.path + " -> " + Integer.toString(point);
            }
        }
        return  new Datapath(min, path);
    }

    /**
     * dynamic finds the lowest cost using dynamic programming.
     * @param array 2D array to find the minimum
     */
    public static void dynamic(int[][] array) {
        int storage[][] = new int[2][array.length];
        for(int i = 0; i < array.length; i++) { //Finding lowest cost with dynamic programming approach
            int shortest = array[0][i];         //Initialize shortest to get to ith element is from 0th
            int used = 0;                       //Storing path
            for(int j = 1; j < i; j++) {        //iterate through each possible path to ith element
                int tempshort = array[j][i] + storage[0][j];
                if(tempshort < shortest) {      //Found lower cost
                    shortest = tempshort;       //Set shortest to new lower cost
                    used = j;                   //Store new path
                }
            }                                   //End of iteration
            storage[0][i]=shortest;             //Store lowest cost
            storage[1][i]=used;                 //Store path
        }                                       //End of finding lowest cost
        //printMatrix(storage);
        System.out.println("Dynamic Programming: "+storage[0][array.length-1]); //Printing Lowest cost

        // Grabing Path
        Stack path = new Stack();               //Stack that will store path start from nth to 0th
        int pointer = array.length - 1;         //Pointer which points to current element starting from nth
        while(pointer != 0) {                   //Iterate until pointer reaches 0
            path.push(pointer);                 //store which path has be used
            pointer = storage[1][pointer];
        }                                       //End of iteration, now stack has full path to nth
        //Start printing Path
        System.out.print("Dynamic Programming Path: [ 0");
        while(!path.isEmpty())                  //Print until stack is empty.
            System.out.print(" -> "+path.pop());
        System.out.println(" ]");
    }

    /**
     * readFile method reads file from given string name and converts to 2D array.
     * @param filename String name of file
     * @return two dimensional array with "NA" converted to -1
     * @throws IOException from BufferedReader
     */
    private static int[][] readFile(String filename) throws IOException {
        String split = "\t";
        String na = "NA";
        BufferedReader reader = new BufferedReader( new FileReader(filename));
        int linecounter = 1;    //linecounter tracks of line. It is set to 1 because 0th line will be handled manually
        String line = reader.readLine();   //Read first line. This is required to fiqure out size of n and create array
        String temp[] = line.split(split);  //Split string by tab
        int array[][] = new int[temp.length][temp.length];  //Create n x n array

        for(int i = 0; i < temp.length; i++) {              //Since first line is read, put info into array
            if(temp[i].compareTo(na) == 0) {
                array[0][i] = -1;
            } else {
                array[0][i] = Integer.parseInt(temp[i]);
            }
        }

        while((line = reader.readLine()) != null) {         //Read until end of file
            temp = line.split(split);
            for(int i = 0; i < temp.length; i++) {
                if(temp[i].compareTo(na) == 0) {
                    array[linecounter][i] = -1;
                } else {
                    array[linecounter][i] = Integer.parseInt(temp[i]);
                }
            }
            linecounter++;
        }                                                   //End of reading file
        reader.close();                                     //Close buffered reader
        return array;                                       //Return 2D array
    }

    /**
     * printMatrix prints any given 2D array onto the console into matrix form
     * @param matrix is the 2D int matrix array to print
     */
    private static void printMatrix(int[][] matrix) {
        int n = matrix.length;
        int m = matrix[0].length;
        for (int i = 0; i < n; i++) {
            System.out.print("|");
            for (int j = 0; j < m; j++) {
                System.out.print(matrix[i][j]);
                if(j != m - 1) {
                    System.out.print("\t");
                }
                if(j == m-1) {
                    System.out.print("|");
                }
            }

            System.out.print("\n");
        }
        System.out.print("\n");
    }

    /**
     * createMatrix creates a random n x n matrix with values 1 - 9.
     * @param size it the size of the matrix
     * @return is the randomized matrix
     */
    private static int[][] createMatrix(int size) {
        Random value = new Random();
        int[][] randArray = new int[size][size];
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                if( i > j ) {
                    randArray[i][j] = -1;
                }else if(i == j){
                    randArray[i][j] = 0;
                }else{
                    randArray[i][j] = value.nextInt(8) + 1;
                }
            }
        }
        return randArray;

    }

    /**
     * arrayToText takes a 2D array and creates a text file.
     * @param arr is the 2D array to convert
     * @param fileName is the file that you want to save it to
     */
    private static void arrayToText(int[][] arr, String fileName) throws IOException {
        BufferedWriter matrix = new BufferedWriter(new FileWriter(fileName));
        int n = arr.length;
        int m = arr[0].length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if( arr[i][j] == -1) {
                    matrix.write("NA\t");
                }else{
                    matrix.write(Integer.toString(arr[i][j]));
                    if(j != m - 1) {
                        matrix.write("\t");
                    }
                }
            }
            matrix.newLine();
        }
        matrix.flush();
        matrix.close();

    }

    /**
     * Datapath is being used by bruteforce method. Datapath is required only because bruteforce needs to return
     * two value (int value, String path).
     */
    private class Datapath {
        private int value;
        private String path;
        private Datapath(int value, String path) {
            this.value = value;
            this.path = path;
        }
    }
}
