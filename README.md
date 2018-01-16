# Regular Expression Generator

## User Guide

**How to run the program:**

Use BlueJ to compile and run the program.

**OR**

Compile: `javac -encoding UTF-8 Regex.java NFA.java`

Run: `java -Dfile.encoding=UTF-8 Regex`

If the program is run with command line parameters, the program uses each parameter as a file from which it reads input.  The first line of the input file should be the regular expression and all lines following it should be the strings to test against the regular expression.

If the program is run with no command line parameters, it asks the user to enter a regular expression.  Afterwards, it asks the user for strings which it will test against the regular expression.  After each string is entered, the program displays true if the string is recognized by the regular expression and false if it is not.  If no string is supplied when prompted, the program exits.

**Inputs:**

A line separated file containing the regular expression to parse followed by the strings to test it against.

The regular expression may consists of:

* `*` – star
* `|` – union
* Concatenation is implied ie. “ab” or “a(b)” 
* `()` – for grouping
* `ε` – empty string
* Everything else is considered a letter in the alphabet

Strings contain characters from the alphabet or epsilon.

**Outputs:**

Prints to stdout whether or not each string was recognized by the language.

## Description

The regular expression engine uses Thompson’s construction to convert a regular expression into an NFA.  The conversion consists of building NFAs and from other NFAs using the star, union, and concatenation operators and from single symbols.  The program then runs through the NFA using a given string and returns true if the NFA accepts that string and false if not.

## Design

The program reads input from a file or stdin depending on whether command line parameters are present.  From the input, it reads a regular expression and parses it into a NFA object.  A NFA object consists of the elements of the formal description for an NFA: a set of states, a set containing the alphabet, a transition function, a start state, and a set of accept states.  In the NFA object, states are represented by integers and members of the alphabet are represented with characters.  The set of states and the set containing the alphabet are stored in a HashSet.  The transition function is represented by a HashMap.  The start state is an integer.  Since Thompson’s construction results in a NFA with only one accept state, the accept state is just stored as an integer rather than a HashSet like the state and alphabet sets.  Parsing is done recursively by creating larger NFAs from smaller NFAs.  The smallest NFAs which consist of a single transition are created with the NFA’s constructor.  Larger NFAs are built using static methods in the NFA class.  These methods are star, concatenation and union.  They take NFAs as parameters and return a new NFA which is the transform of its parameters. 

After the program creates the NFA, the NFA is run with a string.  While running through the string, a HashSet is used to keep track of the states that the NFA is currently in.  After reading the last character from the string, the NFA accepts if the set containing the states it is currently in contains the accept state and rejects otherwise.

 

## Testing Results

### Test 1

**Regular Expression:** `a*`

string  |   result
--------|----------
b       |   FALSE
aaaaa   |   TRUE
aε      |   TRUE
ε       |   TRUE

### Test 2

**Regular Expression:** `a(b|a)`

string  |   result
--------|----------
aa      |   TRUE
bb      |   FALSE
ab      |   TRUE
ε       |   FALSE

### Test 3

**Regular Expression:** `a*b*a(b|a)`

string          |   result
----------------|----------
aaabaab         |   FALSE
aaaabbbbbbbbaa  |   TRUE
aba             |   FALSE
aaab            |   TRUE
ab              |   TRUE
a               |   FALSE

### Test 4   

**Regular Expression:** `colo(u|ε)r`

string  |   result
--------|----------
pink    |   FALSE
color   |   TRUE
yellow  |    FALSE
ε       |   FALSE
colour  |   TRUE

### Test 5

**Regular Expression:** `(a*)((b)|a)`


string  |   result
--------|----------
aaaab   |   TRUE
aaaaa   |   TRUE
b       |   TRUE
a       |   TRUE
bab     |   FALSE
ba      |   FALSE

### Test 6

**Regular Expression:** `(((((a*)b)*)|c*)d)`


string          |   result
----------------|----------
aaababaabd      |   TRUE
bbbbbbabb       |   FALSE
ccccccccccccd   |   TRUE
abcd            |   FALSE
d               |   TRUE

### Test 7

**Regular Expression:** `(b|e)(ar)*`

string          |   result
----------------|----------
eararararar     |   TRUE
bararar         |   TRUE
bear            |   FALSE
b               |   TRUE
e               |   TRUE
ε               |   FALSE
eara            |   FALSE

### Test 8

**Regular Expression:** `ab*|c*`

string          |   result
----------------|----------
abbbbbbbbbccc   |   FALSE
a               |   TRUE
abb             |   TRUE
ε               |   TRUE
bb              |   FALSE
ccc             |   TRUE

### Test 9

**Regular Expression:** `((((a)(p))((p)(l)))((e)(s)))`


string  |   result
--------|----------
apples  |    TRUE
apple   |   FALSE
pple    |   FALSE
ε       |   FALSE

