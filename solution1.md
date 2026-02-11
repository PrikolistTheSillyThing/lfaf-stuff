# Grammar thingy
## Creating strings and Finite Automata
The project was created in Java and follows the requirements of the task:
1. Create the grammar for my variant
VN={S, A, B, C},
VT={a, b}, 
P={
    S → bA     
    A → b    
    A → aB   
    B → bC    
    C → cA
    A → bA
    B → aB
}
2. Create 5 random strings that would follow the production rules
3. Create a method that would convert the grammar to finite automata
4. A method that checks if a string is valid via finite automata

## Main stuff
* *public Grammar()* - a constructor for Grammar that applies the production rules
* *generateString()* - self explanatory. chosenRule has random length which allows us to create Strings that are sets of terminal elements
* *toFiniteAutomaton()* - converts the grammar to the finite automata.
* *stringBelongToLanguage(final String inputString)* - checks if a string supports the finite automata. 
