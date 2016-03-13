
import java.util.*;
import java.io.*;


/*
 * 
 * 
 */
/**
 *
 * @author Damian Adams
 * @version November 10th, 2015
 */


 // Program takes a file of social security numbers ( in list form) and puts them into a hash datastructure

public class HashingDriver {

    private class Node {

        int data;
        Node nextNode;

        private Node(int data) {
            this.data = data;
        }
    }

    static Node[] hashTable = new Node[200];
    static int[] socialNumbers;
    static final int TOMBSTONE = -1;
    
    // Main function prints menu options. If you are reading this comment, Ill tell you there is a secret 0 (zero) option number that runs a statistical analysis on the differnt hashing functions.

    public static void main(String[] args) throws FileNotFoundException, IOException {

        HashingDriver myHash = new HashingDriver();
        boolean done = false;
        Scanner myScanner = new Scanner(System.in);

        int hashFunction = 1;

        myHash.bigSSNWriter("SSN");
        while (!done) {
            System.out.println("1. Choose Hash Function");
            System.out.println("2. Load SSN Numbers from file");
            System.out.println("3. Insert a SS Number");
            System.out.println("4. Delete a SS Number");
            System.out.println("5. Search for a SS  Number");
            System.out.println("6. Print out Hash Table into a file");
            System.out.println("7. Quit");

            int input = Integer.parseInt(myScanner.nextLine());

            // selects the appropriate hash function
            if (input == 1) {
                System.out.println("1: Multiplication\n2: Middle Squaring\n3: My Function");
                hashFunction = Integer.parseInt(myScanner.nextLine());
            
            // load a file of the given name to be used as teh SSN database
            } else if (input == 2) {
                System.out.println("Enter your file's name: ");
                String fileName = myScanner.nextLine();
                try {

                    boolean result = myHash.readSocialFile(fileName, hashFunction);
                    if (!result) {
                        System.out.println("Error in file processing");
                    }
                } catch (IOException ex) {
                    System.out.println("File Not Found");
                }
               
            // adds an individual SSN to the list    
            } else if (input == 3) {
                System.out.println("Enter Social Security Number: ");
                int social = Integer.parseInt(myScanner.nextLine());

                int index;

                index = myHash.hashAlternator(hashFunction, social);

                myHash.add(social, index);

                myHash.checkFull(hashFunction);
            // searches for a SSN and removes it if found    
            } else if (input == 4) {
                System.out.println("Enter Social Security Number: ");
                int social = Integer.parseInt(myScanner.nextLine());

                int index;

                index = myHash.hashAlternator(hashFunction, social);

                myHash.delete(social, index);
                
            // Searches for a specific social and returns its location

            } else if (input == 5) {
                System.out.println("Enter Social: ");
                input = Integer.parseInt(myScanner.nextLine());

                int index = myHash.hashAlternator(hashFunction, input);
                int searchNumber = myHash.search(input, index);
                if (searchNumber != -1) {
                    System.out.println("Social Security Number: " + input + " found at location: " + index);
                } else {
                    System.out.println("Social Not Found");
                }
                
            // prints social list out to a given file   

            } else if (input == 6) {
                System.out.println("Enter file name: ");
                String fileName = myScanner.nextLine();
                myHash.writeFile(fileName, hashTable);
                
            // exits program

            } else if (input == 7) {

                done = true;
            
            // runs statistical analysis
                
            } else if (input == 0) {

                myHash.test();

            } else {
                System.out.println("No choise found");
            }
        }

    }
    
    // reads in a file 
    private boolean readSocialFile(String fileName, int hashChosen) throws FileNotFoundException, IOException {
        try {
            ArrayList<Integer> socialList = new ArrayList<>();

            FileReader myReader = new FileReader(fileName + ".txt");

            BufferedReader myBuffer = new BufferedReader(myReader);

            boolean done = false;

            while (!done) {
                String input = myBuffer.readLine();

                if (input != null) {
                    int integerInput = Integer.parseInt(input);
                    socialList.add(integerInput);
                } else {
                    done = true;
                }

            }
            int[] socialArray = new int[socialList.size()];
            double size = 0;
            for (int i = 0; i < socialList.size(); i++) {

                socialArray[i] = socialList.get(i);

                size++;
            }
            for (int i = 0; i < size; i++) {
                int index = hashAlternator(hashChosen, socialArray[i]);
                add(socialArray[i], index);
                checkFull(hashChosen);
            }
            myBuffer.close();
            myReader.close();

            return true;
        } catch (IOException myEx) {
            return false;
        }
    }

    // reads out a file
    private boolean writeFile(String fileName, Node[] hashMap) throws IOException {
        Node thisNode = hashMap[0];
        FileWriter myWriter = new FileWriter(fileName + ".txt");
        try {

            for (int i = 0; i < hashMap.length; i++) {
                if (hashMap[i] != null) {
                    while (thisNode != null) {
                        myWriter.write(String.valueOf(thisNode.data) + ", ");
                        thisNode = thisNode.nextNode;
                    }
                    myWriter.write("\n");
                    thisNode = hashMap[i];
                }

            }
            myWriter.close();
            return true;
        } catch (IOException | IndexOutOfBoundsException | NullPointerException myEx) {
            return false;
        } finally {
            myWriter.close();
        }

    }

    // gives a large list of soscial secutiry sized numbers. For testing.
    private void bigSSNWriter(String fileName) throws IOException {
        FileWriter myWriter = new FileWriter(fileName + ".txt");

        Random myRand = new Random();

        for (int i = 0; i < 100000; i++) {
            myWriter.write(String.valueOf(myRand.nextInt(899999999) + 100000000) + "\n");

        }
        myWriter.close();
    }

    // 
    private int multiplication(int socialNumber, int arraySize) {
        double socialDouble = socialNumber;
        double A = 0.538921;
        double fractional = socialDouble * A;
        double whole = Math.floor(socialDouble * A);
        double index = (fractional - whole) * arraySize;
        int j = (int) index;
        return j;
    }

    private int middleSquare(int socialNumber, int arraySize) {
        double dblSocial = socialNumber;
        double square = Math.pow(dblSocial, 2.0);
        long intSquare = (long) square;
        String myString = String.valueOf(intSquare);
        int index = Integer.parseInt(myString.substring(4, 4 + (String.valueOf(arraySize).length())));
        return index % arraySize;
    }

    private int myFunction(int socialNumber, int arraySize) {
        long seed = socialNumber;
        Random randInt = new Random(seed);

        return randInt.nextInt(arraySize);
    }

    private void add(int socialNumber, int index) {
        Node newNode = new Node(socialNumber);
        newNode.nextNode = hashTable[index];
        hashTable[index] = newNode;

    }

    private int delete(int socialNumber, int index) {
        Node thisNode = hashTable[index];

        do {
            if (thisNode.data == socialNumber) {
                int data = thisNode.data;
                thisNode.data = -1;
                return data;
            } else {
                thisNode = thisNode.nextNode;
            }
        } while (thisNode != null);
        return -1;
    }

    private int search(int socialNumber, int index) {
        Node thisNode = hashTable[index];

        do {
            if (thisNode.data == socialNumber) {

                return thisNode.data;
            } else {
                thisNode = thisNode.nextNode;
            }
        } while (thisNode != null);
        return -1;
    }

    private int hashAlternator(int hashFunction, int social) {
        int index;
        if (hashFunction == 1) {
            index = multiplication(social, hashTable.length);
            return index;
        } else if (hashFunction == 2) {
            index = middleSquare(social, hashTable.length);
            return index;
        } else if (hashFunction == 3) {
            index = myFunction(social, hashTable.length);
            return index;
        } else {
            return -1;
        }
    }

    private void checkFull(int hashChosen) {
        int size = 0;

        for (int i = 0; i < hashTable.length; i++) {
            if (hashTable[i] != null) {
                size = size + 1;
            }
        }
        if (hashTable.length - size < 100) {
            Node[] temp = hashTable;
            hashTable = new Node[temp.length + 100];

            for (int i = 0; i < temp.length; i++) {
                if (temp[i] != null) {
                    int index = hashAlternator(hashChosen, temp[i].data
                    );
                    hashTable[index] = temp[i];
                }
            }

        }

    }

    private void test() throws IOException {

        long multAverage = 0;
        long midAverage = 0;
        long randAverage = 0;
        double multCollisions = 0;
        double multSize = 0;
        double midCollisions = 0;
        double midSize = 0;
        double randCollisions = 0;
        double randSize = 0;
        double multCollRatio = 0;
        double midCollRatio = 0;
        double randCollRatio = 0;
        double multCollRatioAve = 0;
        double midCollRatioAve = 0;
        double randCollRatioAve = 0;
        Node thisNode;

        for (int i = 0; i < 100; i++) {
            final long startMultiplicationTime = System.currentTimeMillis();
            readSocialFile("SSN", 1);
            final long endMultiplicationTime = System.currentTimeMillis();

            for (int j = 0; j < hashTable.length; j++) {

                if (hashTable[j] != null) {
                    thisNode = hashTable[j];
                    while (thisNode.nextNode != null) {
                        multCollisions++;

                        thisNode = thisNode.nextNode;
                    }
                    multSize++;
                }
            }
            multCollRatio = multSize / multCollisions;
            multCollRatioAve = multCollRatioAve + multCollRatio;

            final long multRunTime = endMultiplicationTime - startMultiplicationTime;
            clear();

            final long startMiddleTime = System.currentTimeMillis();
            readSocialFile("SSN", 2);
            final long endMiddleTime = System.currentTimeMillis();

            for (int j = 0; j < hashTable.length; j++) {

                if (hashTable[j] != null) {
                    thisNode = hashTable[j];
                    while (thisNode.nextNode != null) {
                        midCollisions++;

                        thisNode = thisNode.nextNode;
                    }
                    midSize++;
                }
            }

            midCollRatio = midSize / midCollisions;
            midCollRatioAve = midCollRatioAve + midCollRatio;

            final long midRunTime = endMiddleTime - startMiddleTime;
            clear();

            final long startRandomTime = System.currentTimeMillis();
            readSocialFile("SSN", 3);
            final long endRandomTime = System.currentTimeMillis();

            for (int j = 0; j < hashTable.length; j++) {

                if (hashTable[j] != null) {
                    thisNode = hashTable[j];
                    while (thisNode.nextNode != null) {
                        randCollisions++;

                        thisNode = thisNode.nextNode;
                    }
                    randSize++;
                }
            }

            randCollRatio = randSize / randCollisions;
            randCollRatioAve = randCollRatioAve + randCollRatio;

            final long randRunTime = endRandomTime - startRandomTime;
            clear();

            multAverage = multAverage + multRunTime;
            midAverage = midAverage + midRunTime;
            randAverage = randAverage + randRunTime;

            System.out.println(i);
        }

        multAverage = multAverage / 100;
        midAverage = midAverage / 100;
        randAverage = randAverage / 100;

        multCollRatioAve = multCollRatioAve / 100;
        midCollRatioAve = midCollRatioAve / 100;
        randCollRatioAve = randCollRatioAve / 100;

        System.out.println("Multiplication's average run time (100x): " + multAverage + " with number of keys to collision ratio: " + multCollRatioAve);
        System.out.println("Middle Square's average run time(100x): " + midAverage + " with number of keys to collision ratio: " + midCollRatioAve);
        System.out.println("Random Generation's average run time(100x): " + randAverage + " with number of keys to collision ratio: " + randCollRatioAve);
    }

    private void clear() {
        hashTable = new Node[200];
    }

}
