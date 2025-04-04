package workshop05code;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 * @author sqlitetutorial.net
 */
public class App {
    private static final Logger logger = Logger.getLogger(App.class.getName());
    // Start code for logging exercise
    static {
        // must set before the Logger
        // loads logging.properties from the classpath
        try {// resources\logging.properties
            LogManager.getLogManager().readConfiguration(new FileInputStream("resources/logging.properties"));
        } catch (SecurityException | IOException e1) {
            logger.log(Level.SEVERE, "Failed to load logging.properties", e1);
        }
    }

    // End code for logging exercise
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SQLiteConnectionManager wordleDatabaseConnection = new SQLiteConnectionManager("words.db");

        wordleDatabaseConnection.createNewDatabase("words.db");
        if (wordleDatabaseConnection.checkIfConnectionDefined()) {
            logger.log(Level.INFO, "Wordle created and connected.");
        } else {
            logger.log(Level.INFO, "Not able to connect. Sorry!");
            return;
        }
        if (wordleDatabaseConnection.createWordleTables()) {
            logger.log(Level.INFO, "Wordle structures in place.");
        } else {
            logger.log(Level.INFO, "Not able to launch. Sorry!");
            return;
        }

        // let's add some words to valid 4 letter words from the data.txt file

        try (BufferedReader br = new BufferedReader(new FileReader("resources/data.txt"))) {
            String line;
            int i = 1;
            while ((line = br.readLine()) != null) {
                wordleDatabaseConnection.addValidWord(i, line);
                i++;
            }

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load words from data.txt", e);
            System.out.println("Not able to load. Sorry!");
        }

        // let's get them to enter a word

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter a 4 letter word for a guess or q to quit: ");
            String guess = scanner.nextLine();

            while (!guess.equals("q")) {
                if(guess.length() != 4){
                    logger.log(Level.WARNING, "Invalid " + guess + " recorded");
                    System.out.print("The text inputted is not four characters long, enter a 4 letter word: ");
                    guess = scanner.nextLine();
                }else if(!guess.matches("[a-z]+")){
                    System.out.print("The text inputted is contains non alphabetic characters, enter a 4 letter word: ");
                    guess = scanner.nextLine();
                }else{
                    System.out.println("You've guessed '" + guess+"'.");

                    if (wordleDatabaseConnection.isValidWord(guess)) { 
                        logger.log(Level.SEVERE, "Valid word " + guess + " read from the data db");
                        System.out.println("Success! It is in the the list.\n");
                    }else{
                        logger.log(Level.SEVERE, "Invalid word " + guess + " read from the data db");
                        System.out.println("Sorry. This word is NOT in the the list.\n");
                    }

                    System.out.print("Enter a 4 letter word for a guess or q to quit: " );
                    guess = scanner.nextLine();
                }
            }
        } catch (NoSuchElementException | IllegalStateException e) {
            logger.log(Level.SEVERE, "Unexpected input error", e);
        }

    }
}