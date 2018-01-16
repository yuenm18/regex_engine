import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

/**
 * Reads in a file or from stdin a regular expression and some strings
 * outputs if the regular expression recognizes the strings or not
 * 
 * @author Matthew Yuen 
 * @version November 30, 2016
 */
public class Regex
{
    /**
     * parseRegex
     *
     * recursively generate an equivalent NFA from the given regular expression
     *
     * @param regex the regular expression to parse
     * @return the NFA equivalent of the regular exprssion
     */
    private static NFA parseRegex(String regex) {
        //in case regex is the empty string
        if (regex.length() == 0) return new NFA();
        
        //A single character is turned into a new NFA containing a transition from the start state to the accept state on that character
        if (regex.length() == 1) return new NFA(regex.charAt(0));
        
        //check in order from lowest to highest precedence
        
        //look for an outer union
        //go through string looking for a | that is not in a parentheses statement
        //keeps track of what level of nested parentheses the character is in now
        int parenOpenNum = 0;
        for (int i = 0; i < regex.length(); i++) {
            if (regex.charAt(i) == '(') parenOpenNum++;
            else if (regex.charAt(i) == ')') parenOpenNum--;
            //recurse over the substrings between the | if the | is found in the other level of parentheses
            if (parenOpenNum == 0 && regex.charAt(i) == '|') {
                return NFA.union(parseRegex(regex.substring(0, i)), parseRegex(regex.substring(i + 1)));
            }
        }
        
        //look for an outer concat
        //go through the string looking for an outer concatenation
        //reset parentheses number even though should be zero if the string is valid
        parenOpenNum = 0;
        for (int i = 0; i < regex.length(); i++) {
            if (regex.charAt(i) == '(') parenOpenNum++;
            else if (regex.charAt(i) == ')') parenOpenNum--;
            //recurse if it is not in the form a* or (a)
            if (parenOpenNum == 0 && (regex.charAt(i) != '*' && regex.charAt(i) != ')') && i != 0) {
                return NFA.concatenate(parseRegex(regex.substring(0, i)), parseRegex(regex.substring(i)));
            }
            //recurse if it is in the form a(...
            if (parenOpenNum == 1 && regex.charAt(i) == '(' && i != 0) {
                return NFA.concatenate(parseRegex(regex.substring(0, i)), parseRegex(regex.substring(i)));
            }
        }
        
        //look for an outer star
        //go throught the string looking for an outer star
        //reset parentheses counter
        parenOpenNum = 0;
        for (int i = 0; i < regex.length(); i++) {
            if (regex.charAt(i) == '(') parenOpenNum++;
            else if (regex.charAt(i) == ')') parenOpenNum--;
            //recurse if in outer parenthesis level and the character is a *
            if (parenOpenNum == 0 && regex.charAt(i) == '*') {
                return NFA.star(parseRegex(regex.substring(0, i)));
            }
        }
        
        
        //remove outer parentheses
        return parseRegex(regex.substring(1, regex.length() - 1));
    }
    
    /**
     * main
     *
     * @param args files to check
     */
    public static void main(String[] args) {
        if (args.length != 0) {
            System.out.println("Testing " + args.length + " file" + ((args.length != 1) ? "s" : ""));
            //go through each file
            for (int i = 0; i < args.length; i++) {
                System.out.println();
                System.out.println();
                try {
                    //try reading the input file
                    System.out.println("File: " + args[i]);
                    Scanner scanner = new Scanner(new FileReader(args[i]));
        
                    //regex is the first line
                    String regex = scanner.nextLine();
                    
                    //try parse regex into NFA
                    NFA nfa = null;
                    try {
                        //check to make sure regex is valid
                        int openParenCount = 0;
                        for (int j = 0; j < regex.length(); j++) {
                            if (regex.charAt(j) == '(') openParenCount++;
                            else if (regex.charAt(j) == ')') openParenCount--;
                            //should never be more closed than open parentheses
                            if (openParenCount < 0) throw new Exception("Mismatched Parentheses");
                        }
                        //should be an equal number of open and closed parentheses
                        if (openParenCount != 0) throw new Exception("Mismatched Parentheses");
                        
                        //parse regex
                        nfa = parseRegex(regex);
                    }
                    catch (Exception e) { //if an error occurred in parsing, probably a result of an invalid regex
                        System.err.println("Could not parse Regular Expression: " + regex);
                        System.err.println(e.toString());
                        //close file stream
                        scanner.close();
                        //don't try parsing the rest of the file
                        continue;
                    }
                    
                    //System.out.println(nfa.toString());
                    
                    System.out.println();
                    System.out.println("Regular Expression: " + regex);
                    
                    //run each string through regular expression
                    //repeat until end of file
                    while (scanner.hasNextLine()) {
                        //holds current string
                        String string = scanner.nextLine();
                        System.out.println("\t"+string + ": " + nfa.run(string)); // run string through NFA
                    }
                    
                    //close file stream
                    scanner.close();
                }
                catch (FileNotFoundException e) { //error for an invalid file name
                    System.err.println("Invalid File: " + args[0]);
                    continue;
                }
            }
        }
        else {
            //scanner to read user input
            Scanner scanner = (System.console() != null) ? new Scanner(System.console().reader()) : new Scanner(System.in);
            //ask for a regular expression
            System.out.print("Regular Expression: ");
            String regex = scanner.nextLine();
            
            //try to convert the regular expression into an NFA
            NFA nfa = null;
            try {
                //check to make sure regex is valid
                int openParenCount = 0;
                for (int j = 0; j < regex.length(); j++) {
                    if (regex.charAt(j) == '(') openParenCount++;
                    else if (regex.charAt(j) == ')') openParenCount--;
                    //should never be more closed than open parentheses
                    if (openParenCount < 0) throw new Exception("Mismatched Parentheses");
                }
                //should be an equal number of open and closed parentheses
                if (openParenCount != 0) throw new Exception("Mismatched Parentheses");
                
                //parse regex
                nfa = parseRegex(regex);
            }
            catch (Exception e) { //there is probably an error with the regular expression
                System.err.println("Could not parse Regular Expression: " + regex);
                System.err.println(e.toString());
                //quit
                System.exit(-1);
            }
            
            //read strings to test against the NFA
            String string;
            System.out.print("String: ");
            while (!(string = scanner.nextLine()).equals("")) { // empty string means to quit
                //run the string against the NFA
                System.out.println(nfa.run(string));
                System.out.print("String: ");
            }
            
            //close the scanner
            scanner.close();
        }
    }
}
