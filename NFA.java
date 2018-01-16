import java.util.HashSet;
import java.util.HashMap;
/**
 * Write a description of class NFA here.
 * 
 * @author Matthew Yuen 
 * @version November 30, 2016
 */
public final class NFA
{
    //Formal Description of an NFA
    private HashSet<Integer> states;
    private HashSet<Character> alphabet;
    private HashMap<String,HashSet<Integer>> transition;
    private int startState;
    private int acceptState; //construction only involves one accept state
    
    //Ensures unique state numbers
    private static int newStateId = 0;
    /**
     * Constructor to create a new NFA
     */
    public NFA()
    {
        //just initialize all the instance variables
        states = new HashSet<Integer>();
        alphabet = new HashSet<Character>();
        transition = new HashMap<String,HashSet<Integer>>();
        startState = -1;
        acceptState = -2;
    }
    /**
     * Constructor to create a NFA with a transition from the start to accept state
     * on the given symbol
     * 
     * @param symbol the symbol that transitions from the start to accept state
     */
    public NFA(char symbol)
    {
        //initialoze instance variables
        this();
        
        //create new distinct start and accept states
        startState = newStateId++;
        acceptState = newStateId++;
        
        //add them to the set of states
        states.add(startState);
        states.add(acceptState);
        
        //epilons are not in the alphabet
        if (symbol != 'ε') {
            alphabet.add(symbol);
        }
        
        //add a transition from the start to the accept state on the symbol
        addTransition(startState, acceptState, symbol);
    }
    /**
     * addTransition
     * 
     * Adds a transition to the transition table given a initial state, transition symbol and state to
     *
     * @param stateFrom the initial state of the transition
     * @param stateTo state to transition to
     * @param symbol symbol of the transition
     */
    private void addTransition(int stateFrom, int stateTo, char symbol) {
        //if the list of transitions is not null then add the state to the the list
        if (transition.containsKey(Integer.toString(stateFrom)+'_'+symbol)) {
            transition.get(Integer.toString(stateFrom)+'_'+symbol).add(stateTo);
        }
        else{// else make a new set and add the transition to that
            HashSet<Integer> newTransition = new HashSet<Integer>();
            newTransition.add(stateTo);
            transition.put(Integer.toString(stateFrom)+'_'+symbol, newTransition);
        }
    }
    /**
     * concatenate
     * 
     * returns a new NFA which is the concatenation of the two NFA parameters
     * 
     * @param nfa1 NFA to concatenate
     * @param nfa2 NFA to concatenate
     * @return A new NFA which is the concatenation the two NFAs
     */
    public static NFA concatenate(final NFA nfa1, final NFA nfa2) {
        //create a new NFA
        NFA nfa = new NFA();
        
        //state state is the start state of the first NFA
        nfa.startState = nfa1.startState;
        
        //accept state is the accept state of the second NFA
        nfa.acceptState = nfa2.acceptState;
        
        //add the states from both NFAs into this one
        nfa.states.addAll(nfa1.states);
        nfa.states.addAll(nfa2.states);
        
        //do same thing with the alphebet
        nfa.alphabet.addAll(nfa1.alphabet);
        nfa.alphabet.addAll(nfa2.alphabet);
        
        //same thing with transitions
        nfa.transition.putAll(nfa1.transition);
        nfa.transition.putAll(nfa2.transition);
        
        //make an epsilon transition from the accept state of the first one to the start state of the second one
        nfa.addTransition(nfa1.acceptState, nfa2.startState, 'ε');
        
        //return the nfa just built
        return nfa;
    }
    /**
     * star
     *
     * returns a new NFA which is the star of the NFA parameter
     *
     * @param nfa1 NFA to star
     * @return A new NFA which is the stared version of the NFA
     */
    public static NFA star(final NFA nfa1) {
        //make a new NFA
        NFA nfa = new NFA();
        
        //make new start and accept state for it
        nfa.startState = newStateId++;
        nfa.acceptState = newStateId++;
        
        //add states from nfa1 into it and the new start and accept states
        nfa.states.addAll(nfa1.states);
        nfa.states.add(nfa.startState);
        nfa.states.add(nfa.acceptState);
        
        //add the alphabet from nfa1 into it
        nfa.alphabet.addAll(nfa1.alphabet);
        
        //same thing with the transitions
        nfa.transition.putAll(nfa1.transition);
        
        //make episilon transitions
        
        //new start to start of nfa1
        nfa.addTransition(nfa.startState, nfa1.startState, 'ε');
        //new start to new accept
        nfa.addTransition(nfa.startState, nfa.acceptState, 'ε');
        //nfa1 accept to nfa start
        nfa.addTransition(nfa1.acceptState, nfa1.startState, 'ε');
        //nfa1 accept to new accept
        nfa.addTransition(nfa1.acceptState, nfa.acceptState, 'ε');
        
        //return the NFA just built
        return nfa;
    }
    /**
     * union
     *
     * returns a new NFA which is the union of the NFA parameters
     *
     * @param nfa1 NFA to union
     * @param nfa2 NFA to union
     * @return a new NFA which is the union of the two NFAs
     */
    public static NFA union(final NFA nfa1, final NFA nfa2) {
        //make a new FNA
        NFA nfa = new NFA();
        
        //make new start and accept state for it
        nfa.startState = newStateId++;
        nfa.acceptState = newStateId++;
        
        //add states from both NFAs into it and the new start and accept states
        nfa.states.addAll(nfa1.states);
        nfa.states.addAll(nfa2.states);
        nfa.states.add(nfa.startState);
        nfa.states.add(nfa.acceptState);
        
        //add the alphabet from both NFAs into it
        nfa.alphabet.addAll(nfa1.alphabet);
        nfa.alphabet.addAll(nfa2.alphabet);
        
        //same thing with transitions
        nfa.transition.putAll(nfa1.transition);
        nfa.transition.putAll(nfa2.transition);
        
        //add epsilon transitions from the new start state to the start states of both NFAs
        nfa.addTransition(nfa.startState, nfa1.startState, 'ε');
        nfa.addTransition(nfa.startState, nfa2.startState, 'ε');
        
        
        //add epsilon transitions from the accept states of both NFAs to the new accept state
        nfa.addTransition(nfa1.acceptState, nfa.acceptState, 'ε');
        nfa.addTransition(nfa2.acceptState, nfa.acceptState, 'ε');
        
        //return the NFA just built
        return nfa;
    }
    /**
     * run
     * 
     * runs the NFA on a given string
     * 
     * @param string string to run the NFA
     * @return if the NFA accepts the string
     */
    public boolean run(String string) {
        //holds the states the NFA is in before a transition
        HashSet<Integer> currentStates = new HashSet<Integer>();
        //holds the states the NFA is in after the transition
        HashSet<Integer> nextStates = new HashSet<Integer>();
        //begin with the start state in the nextStates so it can do the epsilon transiions on it
        nextStates.add(startState);
        //indexes the string. -1 because starting with just epsilon transitions of the start state
        int i = -1;
        
        //do until end of string
        do {
            //follow transitions
            //don't follow transitions if just following epsilon transition from the start state
            if (i != -1) {
                //holds the character that the NFA is currently working on
                char character = string.charAt(i);
                //loop for all of the states that the NFA is currently in
                for (Integer state : currentStates) {
                    //ignore epsilons in the input string so just add them all into the post transition set
                    if (character == 'ε'){
                        nextStates.add(state);
                    } //follow transitions and add the state to the post transition set
                    else if (transition.containsKey(Integer.toString(state)+'_'+character)) {
                        nextStates.addAll(transition.get(Integer.toString(state)+'_'+character));
                    }
                }
            }
            //empty the set of pretransition states
            currentStates.clear();
            
            //do all epilson transitions
            //repeat until no more states are in the post transition set
            while (!nextStates.isEmpty()) {
                //iterate through all the states in the post transition set
                for (Integer state : nextStates.toArray(new Integer[0])) {
                    //follow the epsilon transitions if it exists and add it to the post transition set
                    if (transition.containsKey(Integer.toString(state)+'_'+'ε')) {
                        //makes sure the state isn't already in the pretransition set or else it may loop forever
                        if (!currentStates.contains(state)){
                            nextStates.addAll(transition.get(Integer.toString(state)+'_'+'ε'));
                        }
                    }
                    //add the state just checked into the pretransition set and remove it from the post transition set
                    currentStates.add(state);
                    nextStates.remove(state);
                }
            }
            //emty set of post transition staets
            nextStates.clear();
            
            //increase the index of the string to the next character
            i++;
        } while (i < string.length());
        
        //return whether the accept state is in the set of states that are currently in
        return currentStates.contains(acceptState);
    }
    /**
     * toString
     *
     * @return string containing the value of the stance variables
     */
    public String toString() {
        return "States: " + states.toString() +
        "\nAlphabet: " + alphabet.toString() + 
        "\nTransitions: " + transition.toString() + 
        "\nStart State: " + Integer.toString(startState) +
        "\nAccept State: " + Integer.toString(acceptState);
    }
}
