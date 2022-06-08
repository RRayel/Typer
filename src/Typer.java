import java.io.IOException;
import java.util.ArrayList;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class Typer {

    private ArrayList<String> wordlist;
    private int wordcount;
    private Theme pallet = new Theme();
    private String[] colors = pallet.getTheme();
    private ArrayList<String> usedWords; // the list of words, randomly picked from the wordlist, that are being
                                         // used in this game
    private String[] lines; // the lines of words that are printed to the terminal
    private int maxCharsPerLine = 64; // the maximum characters per line, can shrink if the terminal window is 
                                      // smaller
    private Terminal term; // the terminal object
    private long startTime = 0; // stores when the test starts, 0 before it's set
    private int wrongChars = 0; // how many wrong characters are typed
    private ArrayList<String> wrongWords = new ArrayList<String>(); // which words were failed
    private int termWidth;
    private int termHeight;
    // private static final String wordlistBase64 = "";
    
    /**
     * Creates a Typer game object, automatically starts the game in the terminal
     * 
     * @param wordlist      // the ArrayList of words to pick from, default is the top 1000 most common english
     *                         words
     * @param wordcount     // the amount of words the player has to type
     * @throws IOException
     */
    public Typer(ArrayList<String> wordlist, int wordcount) throws IOException {
        this.wordlist = wordlist;
        this.wordcount = wordcount;

         this.term = TerminalBuilder.terminal();
        //this.term = TerminalBuilder.builder().system(false).streams(System.in, System.out).build();
         this.termWidth = term.getWidth();
        this.termHeight = term.getHeight();
        //this.termWidth = 179;
       // this.termHeight = 11;

        // This hooks into the program exit, and resets the terminal to normal
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                System.out.print("\033[0m\033[2J\033[0;0H\033[?25h\033[1 q\033[!p");
            }
        });


        // set background color of terminal, and change cursor to blinking bar
        System.out.print(getColor(3, true) + "\033[2J\033[0;0H\033[6 q");
    }
    /**
     * Creates a Typer game objectw ith the wordlist being
     * the top 1000 most common english words
     * 
     * @param wordlist      // the ArrayList of words to pick from
     * @throws IOException
     */
    public Typer(ArrayList<String> wordlist) throws IOException {
        this(wordlist, 50);
    }
    /**
     * Creates a Typer game object, where the user has to type 50 words
     * 
     * @param wordcount     // the amount of words the player has to type
     * @throws IOException
     */
    public Typer(int wordcount) throws IOException {
        this(Utils.getWordlist("1000en.txt"), wordcount);
    }
    /**
     * Creates a Typer game object, where the user has to
     * type 50 words, and with the wordlist being the top
     * 1000 most common english words
     * 
     * @throws IOException
     */
    public Typer() throws IOException {
        this(50);
    }
    

    /**
     * Generates the words to use, and prints them to the terminal
     */
    private void genWords() {

        usedWords = Utils.getRandom(wordlist, wordcount);

        maxCharsPerLine = Math.min(maxCharsPerLine, termWidth - 6);
        int numLines = (String.join(" ", usedWords).length() / maxCharsPerLine) + 1;

        lines = new String[numLines];

        int currentWord = 0; // what word is next at the start of the line
        for (int i = 0; i < numLines; i++) {

            ArrayList<String> lineWords = Utils.wrapWords(new ArrayList<String>(
                                                            usedWords.subList(currentWord, usedWords.size())
                                                        ), maxCharsPerLine);
            lines[i] = String.join(" ", lineWords);
            currentWord += lineWords.size();

        }

        // center vertically
        System.out.println("\n".repeat(Utils.centerVertSpacing(lines.length, termHeight)));

        // output lines
        for (int i = 0; i < lines.length; i++) {
            System.out.println(getColor(0) + " ".repeat(Utils.centerHorizSpacing(lines[i], termWidth)) + lines[i]);
        }
        System.out.print("\033[" + numLines + "A\033[" + Utils.centerHorizSpacing(lines[0], termWidth) + "C" + getColor(1));
        
    }


    /**
     * Starts a Typer game
     * 
     * @throws IOException
     */
    public void start() throws IOException {

        // set up terminal
        term.echo(false);
        term.enterRawMode();

        // output starting words and generate wordlist
        genWords();

        int currentWordIndex = 0; // currentWordIndex is the index of the current word in usedWords
        int currentLineIndex = 0; // the current line
        String typedText = ""; // typed text for the current word
        String typedLine = ""; // typed text for the current line, used to find when the line ends

        while (true) {
            String ch = Utils.getChar().toLowerCase(); // get character of input, lowercase will mess up escape
                                                       // codes but it doesn't matter because they're not used
            if (startTime == 0) {
                startTime = System.nanoTime();
            }
            
            // if it's not a valid character, skip (included are space, enter, backspace, and ctrl+backspace)
            if ("abcdefghijklmnopqrstuvwxyz \r\n\u007f\u008f\u0017".indexOf(ch) == -1) {
                continue;
            }

            // if it's a space, fill out the rest of the word as wrong and move
            // on to the next word
            if (ch.equals(" ")) {
                if (typedText.length() > 0) {
                    // display the rest of the word if it hasn't been typed yet
                    String extraChars = usedWords.get(currentWordIndex).substring(typedText.length()); // the rest of the characters
                    System.out.print(getColor(2) + extraChars); 
                    // update all the variables
                    typedLine += extraChars;
                    wrongChars += extraChars.length();
                    // if there are wrong characters, and the word isn't already marked as an incorrect word,
                    // add it to the wrongWords ArrayList
                    if (extraChars.length() > 0 && !wrongWords.contains(usedWords.get(currentWordIndex))) wrongWords.add(usedWords.get(currentWordIndex));
                    
                    currentWordIndex++;
                    typedLine += typedText;
                    typedText = "";

                    // if not at end of game, and at end of line, move to the next line
                    if (currentWordIndex < usedWords.size() - 1 &&  typedLine.length() + usedWords.get(currentWordIndex).length() > maxCharsPerLine) {
                        currentLineIndex += 1;
                        System.out.print("\n" + " ".repeat(Utils.centerHorizSpacing(lines[currentLineIndex], termWidth)));
                        typedLine = "";
                    } else {
                        typedLine += " ";
                        System.out.print(" ");
                    }

                    // if game is over
                    if (currentWordIndex >= usedWords.size()) {
                        break;
                    }

                }

            } else if (ch.equals("\u007f") || ch.equals("\u008f")) { // backspace, 7f is backspace and 8f is DEL (for windows)
                if (typedText.length() == 0) continue; // skip if already at beginning
                typedText = typedText.substring(0, typedText.length() - 1); // cut off character
                if (!typedLine.equals("")) typedLine = typedLine.substring(0, typedLine.length() - 1);
                System.out.print("\033[1D" + getColor(0) + // move back and reset to untyped text color
                                usedWords.get(currentWordIndex).substring(typedText.length(), typedText.length() + 1) +
                                "\033[1D");

            } else if (ch.equals("\u0017")) { // ctrl + backspace (deletes whole word)

                if (typedText.length() == 0) continue; // skip if already at beginning
                System.out.print("\033[" + typedText.length() + "D" + // move back to beginning of word
                                getColor(0) + usedWords.get(currentWordIndex) + // overwrite word with original
                                "\033[" + usedWords.get(currentWordIndex).length() + "D"); // go back to beginning of word
                typedText = "";

            } else if (ch.equals("\r") || ch.equals("\n")) { // enter / return, acts as space if at end of line
                
                // (basically just copies space code but only if at end of line, see space code for documentation / comments)
                if (currentWordIndex + 1 < usedWords.size() - 1 &&  typedLine.length() + usedWords.get(currentWordIndex + 1).length() > maxCharsPerLine) {
                    String extraChars = usedWords.get(currentWordIndex).substring(typedText.length());
                    System.out.print(getColor(2) + extraChars);
                    typedLine += extraChars;
                    wrongChars += extraChars.length();
                    currentWordIndex++;
                    typedLine += typedText;
                    typedText = "";
                    
                    currentLineIndex += 1;
                    System.out.print("\n" + " ".repeat(Utils.centerHorizSpacing(lines[currentLineIndex], termWidth)));
                    typedLine = "";
                }
                if (currentWordIndex >= usedWords.size()) {
                    break;
                }

            } else { // any normal character

                // if already at the end of the word, skip
                if (typedText.length() >= usedWords.get(currentWordIndex).length()) {
                    continue;
                }

                // if it's correct
                if (ch.equals("" + usedWords.get(currentWordIndex).charAt(typedText.length()))) {
                    System.out.print(getColor(1) + ch); // default text color
                } else {
                    System.out.print(getColor(2) + ch); // incorrect text color
                    wrongChars++;
                    // add to wrongWords if it's wrong
                    if (!wrongWords.contains(usedWords.get(currentWordIndex))) wrongWords.add(usedWords.get(currentWordIndex));
                }

                typedText += ch;
            }
                            
        }

        endGame(); // after loop is broken (on finish), display end menu
    }

    /**
     * Displays a statistics menu for the current Typer game
     * 
     * @throws IOException
     */
    private void endGame() throws IOException {

        // clears screen
        System.out.print(getColor(3, true) + "\033[2J\033[?25l");

        // some stats variables
        double secondsElapsed = (System.nanoTime() - startTime) / 1000_000_000.0;
        // words per minute
        double wpm = Math.round(1000 * (60 / secondsElapsed) * wordcount) / 1000.0;
        // characters per minute
        double cpm = Math.round(1000 * (60 / secondsElapsed) * String.join(" ", usedWords).length()) / 1000.0;
        // accuracy
        double acc = Math.round(10000 * ((double) (String.join(" ", usedWords).length() - wrongChars) / String.join(" ", usedWords).length())) / 100;

        // the output variable with the formatted text
        String wpmDisp = Utils.centerText("Time:       " + (Math.round(secondsElapsed * 100) / 100.0) + "s" + 
                                        "\nWPM:        " + wpm + 
                                        "\nCPM:        " + cpm +
                                        "\nAccuracy:   " + acc + "%" + 
                                        (wrongWords.size() < 8 ? "\nMistakes:   " + String.join(", ", wrongWords) : "") + 
                                        "\n \n ", // so the next line can be centered vertically 
                                        termWidth, termHeight);

        System.out.print("\033[0;0H" + getColor(1) + wpmDisp);
        System.out.print("\033[1A" + " ".repeat(Utils.centerHorizSpacing(" Press Enter to continue...",
                        termWidth)) + "Press Enter to continue..." + "\033[0;0H");

        // wait for enter to exit
        while (true) {
            if ("\r\n".indexOf(Utils.getChar()) != -1) break;
        }
        
        // reset terminal
        term.echo(true);
        System.out.print("\033[0m\033[2J\033[0;0H\033[?25h\033[!p");
    }

    /**
     * Gets a color from the list of colors, using the index, and converts to ANSI escape code
     * 
     * @param index     The index of the color
     * @return
     */
    private String getColor(int index) {
        return Utils.hexToAnsi(colors[index]);
    }

    /**
     * Gets a color from the list of colors, using the index, and converts to ANSI escape code
     * 
     * @param index     The index of the color
     * @param bg        Whether or not the color should be for the background
     * @return
     */
    private String getColor(int index, boolean bg) {
        return Utils.hexToAnsi(colors[index], bg);
    }

    public static void main(String[] args) throws Exception {

        Typer t = new Typer();
        t.start();
        System.out.println("hey");

    }

}
