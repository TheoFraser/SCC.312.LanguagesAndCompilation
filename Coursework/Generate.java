// Author: Theo Fraser

public class Generate extends AbstractGenerate {
    /**
     * This function calls the function insertTerminal in the super class
     */
    public void insertTerminal(Token token) {
        // Implement insertTerminal() method
        super.insertTerminal(token);
    }
    /**
     * This function calls the function commenceNonterminal in the super class
     */
    public void commenceNonterminal(String nonTerminalName) {
        // Implement commenceNonterminal() method
        super.commenceNonterminal(nonTerminalName);
    }
    /**
     * This function calls the function finishNonterminal in the super class
     */
    public void finishNonterminal(String nonTerminalName) {
        super.finishNonterminal(nonTerminalName);
        // Implement finishNonterminal() method
    }
    /**
     * This function calls the function reportSuccess in the super class
     */
    public void reportSuccess() {
        super.reportSuccess();
        // Implement reportSuccess() method
    }
    /**
     * This function prints out an Error which says what Token it expected what token it found and what line the error happened on.
     */
    public void reportError(Token token, String explanatoryMessage) throws CompilationException {
        System.out.println("ERROR: Expected " + explanatoryMessage + " Found " + Token.getName(token.symbol) + " at line " + token.lineNumber);
        // throw new CompilationException(explanatoryMessage);
        // Implement reportError() method
    }
}