# `Typer`

`Typer` is a typing test that runs in the terminal. This document gives some information on what the functions are, and how the program works.

## Table of Contents

- [`Typer`](#typer)
  - [Table of Contents](#table-of-contents)
  - [Running the Program](#running-the-program)
  - [Code Layout](#code-layout)
    - [`Typer.java`](#typerjava)
      - [Constructor](#constructor)
      - [`genWords`](#genwords)
      - [`start`](#start)
      - [`endGame`](#endgame)
      - [`getColor`](#getcolor)
    - [`Utils.java`](#utilsjava)
      - [`getWordlist`](#getwordlist)
      - [`wrapWords`](#wrapwords)
      - [`getChar`](#getchar)
      - [`colorToAnsi`](#colortoansi)
      - [`hexToAnsi`](#hextoansi)
      - [`centerHorizSpacing`](#centerhorizspacing)
      - [`centerVertSpacing`](#centervertspacing)
      - [`centerText`](#centertext)
      - [`getRandom`](#getrandom)
    - [Wordlists](#wordlists)
    - [JLine](#jline)
  - [Process Walkthrough](#process-walkthrough)
  - [Extension Ideas](#extension-ideas)

## Running the Program

Before going into how it works, here's how to run the program, To use the class (though there's already a `main` function in `Typer.java`), you can just create a `Typer` object and call the `start()` method on it, see the constructor javadocs for more info on how to do that.

You will also need your code editor configured to have the `lib` directory in the classpath, or just add the `jline-xxx.jar` file to be accessible. This library is explained in the [`JLine`](#JLine) section.

You can just run Typer.java normally after that, but you need to make sure you have a terminal that supports it. If you're on a mac, that should be fine, but if you're on Windows, you will need to use [Windows Terminal](https://github.com/microsoft/terminal) ([Microsoft Store Link](https://aka.ms/terminal)), or any other terminal which fully supports ANSI escape codes (most do, besides the built-in Windows ones). ANSI escape codes are what tell the terminal what color to be (and other formatting things). In the code, you'll see a lot of `\033[`, that's because ANSI escape codes always start with that, then have some identifier information.

## Code Layout

The code of `Typer` is split into two files: `Typer.java` and `Utils.java`. The javadocs for individual fucntions are all in the files, but I'll still put a short overview here. You don't need to read all of this, it's just good reference. There are also some other files, 

Here's a tree view:

```none
├── Typer.java
│   ├── Typer
│   ├── genWords
│   ├── start
│   ├── endGame
│   └── getColor
├── Utils.java
│   ├── 
│   └── 
├── words
│   └── 1000en.txt
└── JLine
```

### `Typer.java`

`Typer.java` is the main file for running the program, and it holds the `main function` as well as most of the algorithmic code.

#### Constructor

The constructor sets up some of the variables as well as the terminal, and it takes a wordlist as well as an (optional) wordcount, which defaults to 50.

#### `genWords`

There's a function called `genWords`, which generates the words to be used in the typing test. It uses the `Utils.getRandom` function to pick `<wordcount>` random words from `<wordlist>`, and displays them on the screen using some text-rendering functions from the `Utils` class.

#### `start`

There's a `start` function which actually starts the game, but because I'm bad at coding, the constructor sets up the terminal so it only really works if they're called in succession. The `start` function has pretty much all of the main logic for the game, though I'll explain how that works farther down.

#### `endGame`

Another function is `endGame`. `endGame` just gets the statistics from the round of playing, and displays them centered in the terminal (using more `Utils` functions).

#### `getColor`

I wanted the colors to be easily changeable, so there's a variable called `colors` which just just a `String` array. It stores hex codes for the colors to be used in the terminal. It's commented in the code what's what, but the first one is untyped text color, the second one is typed text color, the third is incorrect typed text color, and the fourth one is the background color.

The reason this is it's own function is that you can't just use a hex code to display text in the terminal, you have to convert it to the right format. There is a `Utils.hexToAnsi` function that I wrote that does that, but having `getColor` be it's own function is much cleaner, and I can also have a version where it can be specified to display as a background color.

### `Utils.java`

`Utils.java` houses, as the name suggests, utility functions. A lot of it is text / terminal related, but there are a few other things too.

#### `getWordlist`

The `getWordlist` function just handles finding the wordlist file and reading it into an `ArrayList`. That's about it.

#### `wrapWords`

`wrapWords` is used for fitting the words into a space 64 characters wide, so it's in the middle of the terminal instead of stretching to either side. It just turns an `ArrayList` of words into an `ArrayList` of strings, where each string is all of the words on that 'line,' separated by spaces.

#### `getChar`

`getChar` is the function used for getting terminal input. You might notice that you can type characters without pressing enter, this works using getChar. See the section about `JLine` for some other related info, but this uses `System.in.read()`, and has some extra stuff for if it's a special character like backspace or a ctrl keybind.

#### `colorToAnsi`

`colorToAnsi` converts a `java.awt.Color` object to an ANSI escape code, text that you can print to the terminal that'll change the color of the text after it. There's also a `bg` parameter for whether it should change background or foreground color (default is false / foreground).

#### `hexToAnsi`

`hexToAnsi` is the same as `colorToAnsi`, it just takes a hex code String instead of a `Color` object.

#### `centerHorizSpacing`

`centerHorizSpacing` is for centering text horizontally. It takes some text, and a width of a space, then returns how many spaces should be on one side of the text to center it. There's also an option to pass an integer length of the string, since that's all that's needed for the calculation.

#### `centerVertSpacing`

`centerVertSpacing` is the same as `centerHorizSpacing`, it just works vertically, and returns how many lines you would need. This one also has the option to pass the number of lines of text as an integer.

#### `centerText`

`centerText` combines `centerHorizSpacing` and `centerVertSpacing` to center text absolutely in a width/height. There's no option for this one to just pass the number of characters.

#### `getRandom`

`getRandom` has a somewhat generic name, but it picks `n` random values from an `ArrayList` of strings, without duplicates, and returns that as a new `ArrayList`.

### Wordlists

`Typer` is designed to be able to be run with any wordlist, but it just comes with one, in the `words` folder, called `1000en.txt`. This wordlist just has the top 1000 most common English words.

### JLine

`Typer` uses one external library, `JLine`. `JLine` is a library for interfacing with the terminal, but `Typer` only uses it to enable 'raw mode,' which is what makes it possible to get user input without hitting enter. It also makes it possible for the user to press characters without them appearing on the screen.

## Process Walkthrough

This section is just going to be a walkthrough of a run of the program, what functions are called, and what's going on.

First, a `Typer` object is created. The constructor sets up all of the variables, and clears the terminal. If there was no wordlist specified, `getWordlist` is called to get the wordlist from `1000en.txt`.

After that, you need to call the `start()` function. This is where the main logic begins. The `start()` method calls `genWords()`, which generates the list of words to use and prints them to the screen, centered, using some functions from `Utils`. The `genWords()` function also calls `Utils.getRandom` to get the words from the wordlist (or `1000en.txt`)

After the words have been printed to the terminal, the main `while` loop starts. This loop waits for the user to press a key. If the key is a valid key, it uses an if/else ladder for each of the cases.

If it's a space, it will 'end' the word. If a user types space before they've typed the rest of the current word, the rest of the letters in the word are treated as incorrect and the program moves on to the next word. If it's at the end of the line, it needs to calculate where the start of the next line is, then move there. If it's the last word, it'll break out of the `while` loop.

If the character is a backspace, the program just sets the current character to be untyped (and displays it as such), then moves the cursor back one (unless the cursor is at the beginning of the word.)

If the character is ctrl+backspace, it does the same thing as backspace, but deletes the whole word instead of just a single character.

If the character is enter, it does the same thing as space, but only if it's at the end of the line.

Otherwise, if it's any other normal letter, it will check if it's correct at the current position. If it is, it marks it as correct and moves on, otherwise it'll mark it as incorrect and mark the word as having a mistake in it.

When the text is finished being typed (when the `while` loop is broken out of), the `endGame` function is called. The `endGame` function calculates the amount of time it took to type, the average words per minute, the average characters per minute, and the accuracy. It then prints this to the terminal, using the text centering functions from the `Utils` class.

Lastly, the program waits for the user to hit any key, then clears the terminal and exits.

## Extension Ideas

Here are just a few random ideas for ways you could extend the program without *too* much work, but other ideas could be better, so come up with whatever you want.

- Stats Class:
    You could make a class for storing statistics on past tests, and show progress over time.
- More Colorschemes:
    Right now, there's only one colorscheme (Monokai). You could add more colorschemes, and potentially make some way to change them.
- File Output:
    You could add some way to save typing test results to a file, and/or somehow connect this with the stats class idea (if you have extra time).
- Moving Back a Word:
    Though this probably isn't as good an option as the other ones, if you feel confident that you understand the program well, you could try to implement being able to move back a word. Currently, if you backspace all the letters in a word, you can't go back to change something in the word before it. You could try implementing this, but it'd be difficult, and you'd need decent familiarity with the code, so this isn't as good of an option.
- Fix End of Line Bug:
    There's bug where you can sometimes type past the end of the line, if you somehow figure out what the bug is (it's something with detecting when to move to the next line) then feel free to fix it, but it's not really a big change
