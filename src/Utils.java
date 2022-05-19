import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Utils {
    
    /**
     * Gets a wordlist from a filename and returns it as an ArrayList of Strings
     * 
     * @param filename  The name / location fof the file to read from
     * @return          The ArrayList<String> of words
     */
    public static ArrayList<String> getWordlist(String filename) {
        ArrayList<String> wordlist = new ArrayList<String>();
        File wordlistFile = new File("words/" + filename);
        try (
            Scanner reader = new Scanner(wordlistFile);
        ) {
            while (reader.hasNextLine()) {
                wordlist.add(reader.nextLine());
            }
            
        } catch (FileNotFoundException e) {
            System.out.println("Wordlist " + filename + "not found:");
            e.printStackTrace();
        }

        return wordlist;
    }

    /**
     * Turns an ArrayList<String> of words into multiple lines, wrapped at a certain number of characters
     * 
     * @param words         The ArrayList<String> of words to use
     * @param lineLength    The maximum length for each line
     * @return              The list of lines, with words separated by spaces
     */
    public static ArrayList<String> wrapWords(ArrayList<String> words, int lineLength) {

        String line = "";
        for (int i = 0; i < words.size() - 1; i++) {
            
            // return if wrap limit has been reached
            if (line.length() + words.get(i).length() > lineLength) {
                return new ArrayList<String>(words.subList(0, i));
            }

            // add spaces, but only if it's not at the very beginning
            if (i != 0) {
                line += " ";
            }

            line += words.get(i);
        }

        // if the list of words is shorter than limit, just return a copy of the list of words
        return new ArrayList<String>(words);

    }


    /**
     * reads a character of input from the user without hitting enter
     * also returns special characters (like arrow keys)
     * 
     * @return  A string with the character entered, or the escape code if it's a special character
     */
    public static String getChar() throws IOException {
        int ch = -1;
        String esc = ""; // if it's a special character, this var will catch it
        while (ch == -1) {
            ch = System.in.read();
        }
        if (ch == 27) {
            esc += ((char) System.in.read() + ""); // extra '['
            esc += ((char) System.in.read() + ""); // actual escape character
        }
        return ((char) ch) + esc;
    }

    /**
     * Converts a Color object into an ANSI escape code
     * 
     * @param col   The color to convert
     * @param bg    Whether or the ANSI escape code should target the background or the foreground
     * @return      The ANSI escape code for the color
     */
    public static String colorToAnsi(Color col, boolean bg) {
        return String.format("\033[%d;2;%d;%d;%dm", (bg ? 48 : 38), col.getRed(), col.getGreen(), col.getBlue());
    }
    
    /**
     * Converts a Color object into an ANSI escape code for the foreground
     * 
     * @param col   The color to convert
     * @return      The ANSI escape code for the color
     */
    public static String colorToAnsi(Color col) {
        return colorToAnsi(col, false);
    }

    /**
     * Converts a hex code into an ANSI escape code
     * 
     * @param col   The color to convert
     * @param bg    Whether or the ANSI escape code should target the background or the foreground
     * @return      The ANSI escape code for the color
     */
    public static String hexToAnsi(String col, boolean bg) {
        return colorToAnsi(Color.decode(col), bg);
    }
    /**
     * Converts a hex code into an ANSI escape code for the foreground
     * 
     * @param col   The color to convert
     * @return      The ANSI escape code for the color
     */
    public static String hexToAnsi(String col) {
        return colorToAnsi(Color.decode(col));
    }

    /**
     * Calculates the amount of spacing needed on one side to center text horzontally in an area with a certain
     * width
     * 
     * @param text      The text to use for calculations
     * @param width     The width of the area to center in
     * @return          The integer amount of spacing needed (in characters)
     */
    public static int centerHorizSpacing(String text, int width) {
        return (width / 2) - (text.length() / 2);
    }
    /**
     * Calculates the amount of spacing needed on one side to center text of a certain length horzontally in an
     * area with a certain width
     * 
     * @param chars     The amount of characters to use for calculations
     * @param width     The width of the area to center in
     * @return          The integer amount of spacing needed (in characters)
     */
    public static int centerHorizSpacing(int chars, int width) {
        return (width / 2) - (chars / 2);
    }
   
    /**
     * Calculates the amount of spacing needed on the top to center text vertically in an area with a certain
     * height
     * 
     * @param text      The text to use for calculations
     * @param height    The height of the area to center in
     * @return          The integer amount of spacing needed (in characters)
     */
    public static int centerVertSpacing(String text, int height) {
        return (height / 2) - (text.split("\n").length / 2) - 2;
    }
    /**
     * Calculates the amount of spacing needed on the top to center a certain number of lines vertically in an
     * area with a certain height
     * 
     * @param lines     The amount of lines to use for calculations
     * @param height    The height of the area to center in
     * @return          The integer amount of spacing needed (in characters)
     */
    public static int centerVertSpacing(int lines, int height) {
        return (height / 2) - (lines / 2) - 2;
    }

    /**
     * Centers a block of text horizontally and vertically
     * 
     * @param text      The text to center
     * @param width     The width of the area to center the text within
     * @param height    The height of the area to center the text within
     * @return          The centered text
     */
    public static String centerText(String text, int width, int height) {

        int longestLength = 0;
        for (String line: text.split("\n")) {
            longestLength = Math.max(longestLength, line.length());
        }

        int leftSpacing = centerHorizSpacing(longestLength, width);
        int topSpacing = centerVertSpacing(text, height);

        String out = "\n".repeat(topSpacing);
        for (String line: text.split("\n")) {
            out += " ".repeat(leftSpacing) + line + "\n";
        }

        return out;
    }

    /**
     * Gets a certain amount of random values from an ArrayList of Strings
     * 
     * @param vals      The ArrayList<String> to pick from
     * @param amount    The amount of values to pick
     * @return          <amount> randomly picked values from the array
     */
    public static ArrayList<String> getRandom(ArrayList<String> vals, int amount) {
        ArrayList<String> unusedVals = new ArrayList<String>(vals);
        ArrayList<String> pickedVals = new ArrayList<String>();
        for (int i = 0; i < amount; i++) {
            pickedVals.add(unusedVals.remove(
                (int) (Math.random() * unusedVals.size())
            ));
        }
        return pickedVals;
    }

}
