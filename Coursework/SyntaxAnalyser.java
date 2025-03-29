import java.io.IOException;
import java.io.PrintStream;

// Author: Theo Fraser

public class SyntaxAnalyser extends AbstractSyntaxAnalyser {
    int indentLevel = 0;
    boolean errorEncountered = false;
    String errorString = "";
    /**
     * 
     * @param fileName
     * @throws IOException
     */
    public SyntaxAnalyser(String fileName) throws IOException{
        this.lex = new LexicalAnalyser(fileName);
    }
    /**
     * This function prints indent level of tab spaces
     */
    private void _printIndent() {
        for (int i = 0; i < indentLevel; i++) {
            System.out.print("\t"); // Assuming each level of indentation is a tab
        }
    }
    /**
     * This function handles the grammar rule for statement part where <statement part> ::= begin <statement list> end. This means a statement part is made up of a begin symbol into a statement list into a end symbol.
     * The first thing this function does is call _printIndent(); which indents a print statement by the index level then it increments indent level by 1. After that it calls myGenerate.commenceNonterminal("StatementPart"); which prints that this is the start of a Statement Part.
     * Then the function does is acceptTerminal(Token.beginSymbol); which checks if the next symbol is equal to a begin symbol then it calls the function _statementList_(); which handles the grammar rule for a statement list. Then it calls the function acceptTerminal(Token.endSymbol);.
     * Then it decreases the indent level by 1 then calls _printIndent(); then it calls myGenerate.finishNonterminal("StatementPart"); which prints that this is the end of a Statement Part. After than it checks is errorEncountered is true if it is then it throws a new CompilationException.
     * 
     * @throws IOException If there is an error reading the file.
     * @throws CompilationException If there is a compilation error.
     */
    public void _statementPart_() throws IOException, CompilationException {
        _printIndent();
        myGenerate.commenceNonterminal("StatementPart");
        indentLevel = indentLevel + 1;
        acceptTerminal(Token.beginSymbol);
        _statementList_();
        acceptTerminal(Token.endSymbol);
        indentLevel = indentLevel - 1;
        _printIndent();
        myGenerate.finishNonterminal("StatementPart");
        if(errorEncountered == true){
            throw new CompilationException(errorString);
        }
    }
    /**
     * The Grammar rules for Statement List> is <statement list> ::= <statement> | <statement list> ; <statement>. This means a statement is list is made up of statement or a statement list then a semi colon symbol into a statement
     * The first thing this function does is call _printIndent(); which indents a print statement by the index level. Then it increments indent level by 1. After that it calls myGenerate.commenceNonterminal("StatementList"); which prints that this is the start of a Statement List.
     * Then it calls the function _statement_(); which handles the grammar rules for a statement. Then it enters a if statement to check if the next symbol is equal to a semi colon if it is true it will call acceptTerminal(Token.semicolonSymbol); and then the function
     * _statementList_(); which handles the grammar rule for a statement list. Then it decreases the indent level by 1 then calls _printIndent(); then lastly it calls myGenerate.finishNonterminal("AssignmentStatement"); which prints that this is the end of a Assignment Statement. 
     * 
     * @throws IOException 
     * @throws CompilationException
     */
    public void _statementList_()throws IOException, CompilationException{
        _printIndent();
        indentLevel = indentLevel + 1;
        myGenerate.commenceNonterminal("StatementList");
        _statement_();
        if(nextToken.symbol == Token.semicolonSymbol){
            acceptTerminal(Token.semicolonSymbol);
            _statementList_();
        }
        indentLevel = indentLevel - 1;
        _printIndent();
        myGenerate.finishNonterminal("StatementList");
    }
    /**
     * The grammar rule for statement is <statement> ::= <assignment statement> | <if statement> | <while statement> | <procedure statement> | <until statement> | <for statement>. This means a statement is made up of a assignment statement or a if statement or a while statement or a procedure statement 
     * or a until statement or a for statement. The first thing this function does is call _printIndent(); which indents a print statement by the index level. Then it increments indent level by 1. After that it calls myGenerate.commenceNonterminal("Statement"); which prints that this is the start of a Statement.
     * Then it enters a switch case for the next symbol if next symbol is equal to a identifier it is a assignment statement as a assignment statement starts with a Identifier and then it breaks out of the switch case. Then if the nextSymbol is equal to a if Symbol it is a if statement so it calls the function _ifStatement_() then breaks out of the switch case. 
     * Then if the next symbol is equal to a  while symbol it is a while Statement so it calls the function _whileStatement_() and breaks out of the switch case. Then it does that for the rest of the cases which are if next symbol is equal to a call symbol then it is a procedureStatement, if it is a do symbol which then its a _untilStatement_
     * and lastly a for symbol which is a _forStatement_. However if it is the default case e.g none of the other cases it will call the function handleError with the variable nextToken and the reason of the Error then break out of the switch statement.
     * Then outside the switch statement it decreases indent level by 1 then calls _printIndent(); then lastly it calls myGenerate.finishNonterminal("Statement"); which prints that this is the end of a Statement. 
     * 
     * @throws IOException
     * @throws CompilationException
     */
    public void _statement_() throws IOException, CompilationException{
        _printIndent();
        indentLevel = indentLevel + 1;
        myGenerate.commenceNonterminal("Statement");
        switch (nextToken.symbol) {
            case Token.identifier:
                _assignmentStatement_();
                break;
            case Token.ifSymbol:
                _ifStatement_();
                break;
            case Token.whileSymbol:
                _whileStatement_();
                break;
            case Token.callSymbol:
                _procedureStatement_();
                break;
            case Token.doSymbol:
                _untilStatement_();
                break;
            case Token.forSymbol:
                _forStatement_();
                break;
            default:
                // _printIndent();
                // myGenerate.reportError(nextToken, Token.getName(Token.identifier) + " or " + Token.getName(Token.ifSymbol) + " or " + Token.getName(Token.whileSymbol) + " or " + Token.getName(Token.callSymbol) + " or " + Token.getName(Token.doSymbol) + " or " + Token.getName(Token.forSymbol));
                // break;
                handleError(nextToken, Token.getName(Token.identifier) + " or " + Token.getName(Token.ifSymbol) + " or " + Token.getName(Token.whileSymbol) + " or " + Token.getName(Token.callSymbol) + " or " + Token.getName(Token.doSymbol) + " or " + Token.getName(Token.forSymbol));
                break;
        }
        indentLevel = indentLevel - 1;
        _printIndent();
        myGenerate.finishNonterminal("Statement");
    }
    /**
     * This functions handles the grammar rule for assignment statement where <assignment statement> ::= identifier := <expression> | identifier := stringConstant. This means a assignment statement is made up of a identifier a := (becomes symbol) and a expression or a string constant. 
     * The first thing this function does is call _printIndent(); which indents a print statement by the index level. Then it increments indent level by 1. After that it calls myGenerate.commenceNonterminal("AssignmentStatement"); which prints that this is the start of a AssignmentStatement.
     * Then it calls acceptTerminal(Token.identifier); as the first token it is expecting is a identifier. Then it calls acceptTerminal(Token.becomesSymbol);. After that it checks if the next token is equal to a string constant if it is the function calls acceptTerminal(Token.stringConstant);.
     * If it is not a string constant it calls the function _expression_(); which handles the grammar rule for a expression.     
     * Then it decreases the indent level by 1 then calls _printIndent(); then lastly it calls myGenerate.finishNonterminal("AssignmentStatement"); which prints that this is the end of a Assignment Statement. 
     * 
     * @throws IOException If there is an error reading the file.
     * @throws CompilationException If there is a compilation error.
     */
    public void _assignmentStatement_()throws IOException, CompilationException{
        _printIndent();
        indentLevel = indentLevel + 1;
        myGenerate.commenceNonterminal("AssignmentStatement");
        acceptTerminal(Token.identifier);  
        acceptTerminal(Token.becomesSymbol);
        if(nextToken.symbol == Token.stringConstant){
            acceptTerminal(Token.stringConstant);
        }
        else{
            _expression_();
        }
        indentLevel = indentLevel - 1;
        _printIndent();
        myGenerate.finishNonterminal("AssignmentStatement");
    }
    /**
     * This function handles the grammar rule for if statement where <if statement> ::= if <condition> then <statement list> end if | if <condition> then <statement list> else <statement list> end if. This means a if statement is made up of a if symbol then a condition then a then symbol into a 
     * statement list then into a end symbol into a if symbol or a if symbol then a condition then a then symbol into a statement list into a else symbol into a statement list then a end symbol into a if symbol.
     * The first thing this function does is call _printIndent(); which indents a print statement by the index level. Then it increments indent level by 1. After that it calls myGenerate.commenceNonterminal("IfStatement"); which prints that this is the start of a If Statement.
     * Then it calls acceptTerminal(Token.ifSymbol); as it expects a if symbol then it calls the function _condition_(); which handles the grammar rule for a condition. Then it calls acceptTerminal(Token.thenSymbol); after that it calls the function _statementList_(); which handles the grammar rule
     * for a _statementList_();. Then if the next symbol is equal to a else symbol then it calls acceptTerminal(Token.elseSymbol); and then calls the function _statementList_(); which handles the grammar rule for a statement list. Then outside the if statement it calls acceptTerminal(Token.endSymbol); and 
     * acceptTerminal(Token.ifSymbol);. Then it decreases the indent level by 1 then calls _printIndent(); then lastly it calls myGenerate.finishNonterminal("AssignmentStatement"); which prints that this is the end of a Assignment Statement. 
     * Then it decreases the indent level by 1 then calls _printIndent(); then lastly it calls myGenerate.finishNonterminal("AssignmentStatement"); which prints that this is the end of a Assignment Statement. 
     * 
     * @throws IOException If there is an error reading the file.
     * @throws CompilationException If there is a compilation error.
     */
    public void _ifStatement_() throws IOException, CompilationException{
        _printIndent();
        indentLevel = indentLevel + 1;
        myGenerate.commenceNonterminal("IfStatement");
        acceptTerminal(Token.ifSymbol);
        _condition_();
        acceptTerminal(Token.thenSymbol);
        _statementList_();
        if(nextToken.symbol == Token.elseSymbol){
            acceptTerminal(Token.elseSymbol);
            _statementList_();
        }
        acceptTerminal(Token.endSymbol);
        acceptTerminal(Token.ifSymbol);
        indentLevel = indentLevel - 1;
        _printIndent();
        myGenerate.finishNonterminal("IfStatement");
    }
    /**
     * This functions handles the grammar rule fo a while statement where <while statement> ::= while <condition> loop <statement list> end loop. This means a while statement consists of a while symbol then a condition then a statement list and then a end symbol and lastly a loop symbol.
     * The first thing this function does is call _printIndent(); which indents a print statement by the index level. Then it increments indent level by 1. After that it calls myGenerate.commenceNonterminal("WhileStatement"); which prints that this is the start of a While Statement.
     * Then the function calls acceptTerminal(Token.whileSymbol); as it expects next token to equal to a while symbol after that it calls the function _condition_(); which handles the grammar rule for a condition. Next the function calls acceptTerminal(Token.loopSymbol); and after that it calls 
     * _statementList_(); which handles the grammar rule for a statement list. Then it calls acceptTerminal(Token.endSymbol); and acceptTerminal(Token.loopSymbol);.      
     * Then it decreases the indent level by 1 then calls _printIndent(); then lastly it calls myGenerate.finishNonterminal("WhileStatement"); which prints that this is the end of a While Statement. 
     * 
     * @throws IOException If there is an error reading the file.
     * @throws CompilationException If there is a compilation error.
     */
    public void _whileStatement_() throws IOException, CompilationException{
        _printIndent();
        indentLevel = indentLevel + 1;
        myGenerate.commenceNonterminal("WhileStatement");
        acceptTerminal(Token.whileSymbol);  
        _condition_();
        acceptTerminal(Token.loopSymbol);
        _statementList_();
        acceptTerminal(Token.endSymbol);
        acceptTerminal(Token.loopSymbol);
        indentLevel = indentLevel - 1;
        _printIndent();
        myGenerate.finishNonterminal("WhileStatement");
    }
    /**
     * This function handles the grammar rule for a procedure statement where <procedure statement> ::= call identifier ( <argument list> ). This means a procedure statement is made up of a call symbol into a identifier symbol then a left Parenthesis statement. 
     * into a argument list lastly into a right Parenthesis symbol. The first thing this function does is call _printIndent(); which indents a print statement by the index level. Then it increments indent level by 1. After that it calls myGenerate.commenceNonterminal("ProcedureStatement"); which prints that this is the start of a Procedure Statement.
     * Then the function calls acceptTerminal(Token.callSymbol); as it expects next token to be equal to a call symbol after that it does acceptTerminal(Token.identifier); and acceptTerminal(Token.leftParenthesis); into calling the function _argumentList_(); which handles the grammar rule for a argument list.
     * Then it calls acceptTerminal(Token.rightParenthesis); as it expects next token to be a right Parenthesis. Then it decreases the indent level by 1 then calls _printIndent(); then lastly it calls myGenerate.finishNonterminal("ProcedureStatement"); which prints that this is the end of a Procedure Statement. 
     * 
     * @throws IOException If there is an error reading the file.
     * @throws CompilationException If there is a compilation error.
     */
    public void _procedureStatement_()throws IOException, CompilationException{
        _printIndent();
        indentLevel = indentLevel + 1;
        myGenerate.commenceNonterminal("ProcedureStatement");
        acceptTerminal(Token.callSymbol);  
        acceptTerminal(Token.identifier);
        acceptTerminal(Token.leftParenthesis);
        _argumentList_();
        acceptTerminal(Token.rightParenthesis);
        indentLevel = indentLevel - 1;
        _printIndent();
        myGenerate.finishNonterminal("ProcedureStatement");
    }
    /**
     * This function handles the grammar rule for a until statement where <until statement> ::= do <statement list> until <condition>. This means a until statement is made up of a do symbol into a statement list then into a until symbol into a condition.  
     * The first thing this function does is call _printIndent(); which indents a print statement by the index level. Then it increments indent level by 1. After that it calls myGenerate.commenceNonterminal("UntilStatement"); which prints that this is the start of a Until Statement.
     * Then the function calls acceptTerminal(Token.doSymbol); as it expects the next token to be equal to a do symbol and after that it calls the function _statementList_() then into acceptTerminal(Token.untilSymbol); and then into _condition_();.
     * Then it calls acceptTerminal(Token.rightParenthesis); as it expects next token to be a right Parenthesis. Then it decreases the indent level by 1 then calls _printIndent(); then lastly it calls myGenerate.finishNonterminal("UntilStatement"); which prints that this is the end of a Until Statement. 
     * 
     * @throws IOException If there is an error reading the file.
     * @throws CompilationException If there is a compilation error.
     */
    public void _untilStatement_() throws IOException, CompilationException{
        _printIndent();
        indentLevel = indentLevel + 1;
        myGenerate.commenceNonterminal("UntilStatement");
        acceptTerminal(Token.doSymbol);
        _statementList_();
        acceptTerminal(Token.untilSymbol);
        _condition_();
        indentLevel = indentLevel - 1;
        _printIndent();
        myGenerate.finishNonterminal("UntilStatement");
    }
    /**
     * This function handles the grammar rule for a for statement where <for statement> ::= for ( <assignment statement> ; <condition> ; <assignment statement> ) do <statement list> end loop. This means a for statement consists of a for symbol then a leftParenthesis symbol into a assignment statement
     * into a semi colon Symbol then a condition into a semi colon Symbol into a assignment statement then into a right Parenthesis symbol then a do symbol into a statement list then a end symbol and lastly into a loop symbol.
     * The first thing this function does is call _printIndent(); which indents a print statement by the index level. Then it increments indent level by 1. After that it calls myGenerate.commenceNonterminal("ForStatement"); which prints that this is the start of a For Statement.
     * Then the function calls acceptTerminal(Token.forSymbol); into acceptTerminal(Token.leftParenthesis); into the function _assignmentStatement_(); which handles the grammar rule for a assignment statement. Then the function calls acceptTerminal(Token.semicolonSymbol); into
     * _condition_(); and then into acceptTerminal(Token.semicolonSymbol); then the function _assignmentStatement_(); then afterward it calls acceptTerminal(Token.rightParenthesis);. Then it calls the functions acceptTerminal(Token.doSymbol); _statementList_(); acceptTerminal(Token.endSymbol); acceptTerminal(Token.loopSymbol);.
     * Then it decreases the indent level by 1 then calls _printIndent(); then lastly it calls myGenerate.finishNonterminal("UntilStatement"); which prints that this is the end of a Until Statement. 
     * 
     * @throws IOException If there is an error reading the file.
     * @throws CompilationException If there is a compilation error.
     */
    public void _forStatement_() throws IOException, CompilationException{
        _printIndent();
        indentLevel = indentLevel + 1;
        myGenerate.commenceNonterminal("ForStatement");
        acceptTerminal(Token.forSymbol);
        acceptTerminal(Token.leftParenthesis);
        _assignmentStatement_();
        acceptTerminal(Token.semicolonSymbol);
        _condition_();
        acceptTerminal(Token.semicolonSymbol);
        _assignmentStatement_();
        acceptTerminal(Token.rightParenthesis);
        acceptTerminal(Token.doSymbol);
        _statementList_();
        acceptTerminal(Token.endSymbol);
        acceptTerminal(Token.loopSymbol);
        indentLevel = indentLevel - 1;
        _printIndent();
        myGenerate.finishNonterminal("ForStatement");
    }
    /**
     * This function handles the grammar rule for a argument list where <argument list> ::= identifier | <argument list> , identifier. This means a argument list is made up of identifier or a argument list a (,)comma symbol and a identifier. 
     * The first thing this function does is call _printIndent(); which indents a print statement by the index level.  Then it increments indent level by 1. After that it calls myGenerate.commenceNonterminal("ArgumentList"); which prints that this is the start of a ArgumentList.
     * Then it calls acceptTerminal(Token.identifier);. Then it checks if the next symbol is equal to a comma symbol if it is then it will call acceptTerminal(Token.commaSymbol); and then the function _argumentList_(); which handles the grammar rule for a argument list. 
     * Then outside the if statement it decreases indent level by 1 then calls _printIndent(); then lastly it calls myGenerate.finishNonterminal("ArgumentList"); which prints that this is the end of a Argument List. 
     * 
     * @throws IOException If there is an error reading the file.
     * @throws CompilationException If there is a compilation error.
     */
    public void _argumentList_() throws IOException, CompilationException{
        _printIndent();
        indentLevel = indentLevel + 1;
        myGenerate.commenceNonterminal("ArgumentList");
        acceptTerminal(Token.identifier);
        if(nextToken.symbol == Token.commaSymbol) {
            acceptTerminal(Token.commaSymbol);
            _argumentList_();
        }
        indentLevel = indentLevel - 1;
        _printIndent();
        myGenerate.finishNonterminal("ArgumentList");
    }
    /**
     * This function handles the grammar rule for a condition where <condition> ::= identifier <conditional operator> identifier | identifier <conditional operator> numberConstant | identifier <conditional operator> stringConstant. This means 
     * a condition is made up of a identifier a condition operator and lastly either a identifier or a number constant or a string constant. The first thing this function does is call _printIndent(); which indents a print statement by the index level.
     * Then it increments indent level by 1. After that it calls myGenerate.commenceNonterminal("Condition"); which prints that this is the start of a Condition. Then it does acceptTerminal(Token.identifier); which checks if the next symbol is equal to a identifier then it calls the function _conditionOperator_()
     * which handles the grammar rule for a condition operator. Then it enters a switch statement for next token. If next token is equal to the identifier it calls acceptTerminal(Token.identifier); and then it breaks out of the switch statement. If the next symbol is equal to a
     * number constant it calls acceptTerminal(Token.numberConstant); then breaks out of the switch statement. If the next symbol is equal to a string Constant it calls acceptTerminal(Token.stringConstant); then breaks out of the switch statement.
     * However if the switch case call the default case e.g not a identifier or a string constant it will call the function handleError with the variable nextToken and the reason of the Error then break out of the switch statement.
     * Then outside the switch statement it decreases indent level by 1 then calls _printIndent(); then lastly it calls myGenerate.finishNonterminal("ConditionalOperator"); which prints that this is the end of a Conditional Operator. 
     * 
     * @throws IOException If there is an error reading the file.
     * @throws CompilationException If there is a compilation error.
     */
    public void _condition_()throws IOException, CompilationException{
        _printIndent();
        indentLevel = indentLevel + 1;
        myGenerate.commenceNonterminal("Condition");
        acceptTerminal(Token.identifier);
        _conditionOperator_();
        switch (nextToken.symbol) {
            case Token.identifier:
                acceptTerminal(Token.identifier);
                break;
            case Token.numberConstant:
                acceptTerminal(Token.numberConstant);
                break;
            case Token.stringConstant:
                acceptTerminal(Token.stringConstant);
                break;
            default:
                // _printIndent();
                // myGenerate.reportError(nextToken, Token.getName(Token.identifier) + " or " + Token.getName(Token.numberConstant) + " or " +  Token.getName(Token.stringConstant));
                // break;
                handleError(nextToken, Token.getName(Token.identifier) + " or " + Token.getName(Token.numberConstant) + " or " +  Token.getName(Token.stringConstant));
                break;
        }
        indentLevel = indentLevel - 1;
        _printIndent();
        myGenerate.finishNonterminal("Condition");
    }
    /**
     * This function handles the grammar rule for a conditional operator where <conditional operator> ::= > | >= | = | /= | < | <=. This means a conditional operator can either be >(greater than) or =>(greater than or equal to) or =(equal to) 
     * or /=(not equal to) or <(less than) or <=(less than or equal to). The first thing this function does is call _printIndent(); which indents a print statement by the index level 
     * Then it increments indent level by 1. After that it calls myGenerate.commenceNonterminal("ConditionalOperator"); which prints that this is the start of a ConditionalOperator. Then it enters a switch statement for next token. If next token is equal to the greaterThanSymbol 
     * it calls acceptTerminal(Token.greaterThanSymbol); which checks if the next symbol is equal to a greaterThanSymbol and then it breaks out of the switch statement. 
     * It does this for the rest of the conditional operators however if the switch case call the default case e.g not a conditional operator it will call the function handleError with the variable nextToken and the reason of the Error then break out of the switch statement 
     * Then outside the switch statement it decreases indent level by 1 then calls _printIndent(); then lastly it calls myGenerate.finishNonterminal("ConditionalOperator"); which prints that this is the end of a Conditional Operator.
     * 
     * @throws IOException If there is an error reading the file.
     * @throws CompilationException If there is a compilation error.
     */
    public void _conditionOperator_()throws IOException, CompilationException{
        _printIndent();
        indentLevel = indentLevel + 1;
        myGenerate.commenceNonterminal("ConditionalOperator");
        switch (nextToken.symbol) {
            case Token.greaterThanSymbol:
                acceptTerminal(Token.greaterThanSymbol);
                break;
            case Token.greaterEqualSymbol:
                acceptTerminal(Token.greaterEqualSymbol);
                break;
            case Token.equalSymbol:
                acceptTerminal(Token.equalSymbol);
                break;
            case Token.notEqualSymbol:
                acceptTerminal(Token.notEqualSymbol);
                break;
            case Token.lessThanSymbol:
                acceptTerminal(Token.lessThanSymbol);
                break;
            case Token.lessEqualSymbol:
                acceptTerminal(Token.lessEqualSymbol);
                break;
            default:
                // _printIndent();
                // myGenerate.reportError(nextToken, Token.getName(Token.greaterThanSymbol) + " or " + Token.getName(Token.greaterEqualSymbol) + " or " + Token.getName(Token.equalSymbol) + " or " + Token.getName(Token.notEqualSymbol) + " or " + Token.getName(Token.lessThanSymbol) + " or " + Token.getName(Token.lessEqualSymbol));
                // break;
                handleError(nextToken, Token.getName(Token.greaterThanSymbol) + " or " + Token.getName(Token.greaterEqualSymbol) + " or " + Token.getName(Token.equalSymbol) + " or " + Token.getName(Token.notEqualSymbol) + " or " + Token.getName(Token.lessThanSymbol) + " or " + Token.getName(Token.lessEqualSymbol));
                break;
        }
        indentLevel = indentLevel - 1;
        _printIndent();
        myGenerate.finishNonterminal("ConditionalOperator");
    }
    /**
     * This functions handles the grammar rule for a expression where <expression> ::= <term> | <expression> + <term> | <expression> - <term>. This means a expression is made up of a term or a expression and a +(plus symbol) and a term or a expression then a -(minus symbol) and a term
     * The first thing this function does is call _printIndent(); which indents a print statement by the index level then it increments indent level by 1. After that it calls myGenerate.commenceNonterminal("Term"); which prints that this is the start of a expression. 
     * After that it calls the function _term_() which handles the grammar rule for a term. Then it enters a switch statement for next token. If next token is equal to plus symbol it calls acceptTerminal(Token.plusSymbol); then calls the function _expression_(); which handles the grammar rule for a expression then it breaks out of the switch statement. 
     * If next token is equal to the minus Symbol then it calls acceptTerminal(Token.minusSymbol); then calls the function _expression_() which handles the grammar rule for a expression then it breaks out of the switch statement. Then outside the switch statement it decreases indent level by 1
     * then calls _printIndent(); then lastly it calls myGenerate.finishNonterminal("Expression"); which prints that this is the end of a Expression.
     * 
     * @throws IOException If there is an error reading the file.
     * @throws CompilationException If there is a compilation error.
     */
    public void _expression_()throws IOException, CompilationException{
        _printIndent();
        indentLevel = indentLevel + 1;
        myGenerate.commenceNonterminal("Expression");
        _term_();
        switch (nextToken.symbol) {
            case Token.plusSymbol:
                acceptTerminal(Token.plusSymbol);
                _expression_();
                break;
            case Token.minusSymbol:
                acceptTerminal(Token.minusSymbol);
                _expression_();
                break;
        }
        indentLevel = indentLevel - 1;
        _printIndent();
        myGenerate.finishNonterminal("Expression");
    }
    /**
     * This function handles the grammar rule for term where <term> ::= <factor> | <term> * <factor> | <term> / <factor>. This means a term is either a factor or a term then a * (multiply symbol) and a factor or a term then a / (division symbol) and a factor.
     * The first thing this function does is call _printIndent(); which indents a print statement by the index level then it increments indent level by 1. After that it calls myGenerate.commenceNonterminal("Term"); which prints that this is the start of a Term.
     * Then it calls the function _factor_() which calls the Grammar rule for a factor. Then it enters a switch statement for next token. If next token is equal to the divide symbol it calls acceptTerminal(Token.divideSymbol); which checks if next token is equal to a divide symbol then calls the function _term_() which handles the grammar rule for a term then it breaks out of the switch statement. 
     * If next token is equal to the times symbol then it calls acceptTerminal(Token.timesSymbol); then calls the function _term_() which handles the grammar rule for a term then it breaks out of the switch statement. Then outside the switch statement it decreases indent level by 1
     * then calls _printIndent(); then lastly it calls myGenerate.finishNonterminal("Term"); which prints that this is the end of a term.
     * 
     * @throws IOException If there is an error reading the file.
     * @throws CompilationException If there is a compilation error.
     */
    public void _term_()throws IOException, CompilationException{
        _printIndent();
        indentLevel = indentLevel + 1;
        myGenerate.commenceNonterminal("Term");
        _factor_();
        switch (nextToken.symbol) {
            case Token.divideSymbol:
                acceptTerminal(Token.divideSymbol);
                _term_();
                break;
            case Token.timesSymbol:
                acceptTerminal(Token.timesSymbol);
                _term_();
                break;
        }
        indentLevel = indentLevel - 1;
        _printIndent();
        myGenerate.finishNonterminal("Term");
    }
    /**
     * This function handles the grammar rule for factor which is <factor> ::= identifier | numberConstant | ( <expression> ).
     * So this means a factor is either a identifier a number constant or a expression in brackets. The first thing this function does is call _printIndent(); which indents a print statement by the index level  
     * then it increments ident level by 1 and after that it calls myGenerate.commenceNonterminal("Factor"); which prints it is the start of a factor.
     * Then it enters a switch statement for the next token in the switch statement if next token is equal to a identifier it then calls acceptTerminal(Token.identifier); which checks if the next token is equal to a identifier then breaks out the switch statement. 
     * If next token is equal to a number constant it calls acceptTerminal(Token.numberConstant); then breaks out the switch statement.
     * If the next token is a left Parenthesis it then calls acceptTerminal(Token.leftParenthesis); then it calls the function _expression_() then lastly it calls acceptTerminal(Token.rightParenthesis); and then breaks out the switch statement. 
     * For the default case e.g. not a identifier or a number constant it will call the function handleError with the variable nextToken and the reason of the Error then break out of the switch statement
     * Then outside the switch statement it decreases indent level by 1 and then calls _printIndent() then calls myGenerate.finishNonterminal("Factor"); which prints it has finished a factor.
     * 
     * @throws IOException If there is an error reading the file.
     * @throws CompilationException If there is a compilation error.
     */
    public void _factor_()throws IOException, CompilationException{
        _printIndent();
        indentLevel = indentLevel + 1;
        myGenerate.commenceNonterminal("Factor");
        switch (nextToken.symbol) {
            case Token.identifier:
                acceptTerminal(Token.identifier);
                break;
            case Token.numberConstant:
                acceptTerminal(Token.numberConstant);
                break;
            case Token.leftParenthesis:
                acceptTerminal(Token.leftParenthesis);
                _expression_();
                acceptTerminal(Token.rightParenthesis);
                break;
            default:
                // _printIndent();
                // myGenerate.reportError(nextToken, Token.getName(Token.identifier) + " or " + Token.getName(Token.numberConstant) + " or " + Token.getName(Token.leftParenthesis));
                // errorEncountered = true;
                handleError(nextToken, Token.getName(Token.identifier) + " or " + Token.getName(Token.numberConstant) + " or " + Token.getName(Token.leftParenthesis));
                break;
        }
        indentLevel = indentLevel - 1;
        _printIndent();
        myGenerate.finishNonterminal("Factor");
    }
    public void handleError(Token token, String explanatoryMessage) throws CompilationException, IOException{
        _printIndent();
        myGenerate.reportError(nextToken, explanatoryMessage);
        errorString = errorString + "ERROR: Expected " + explanatoryMessage + " Found " + Token.getName(token.symbol) + " at line " + token.lineNumber + "\n";
        findSynchronizingToken();
        errorEncountered = true;
    }
    /**
     * This function checks if the next token symbol is equal to symbol. If it is it will indent the print statement with _printIndent(); then calls myGenerate.insertTerminal(nextToken); which prints the token then 
     * set next token equal to the next token is the series. If the symbol is not equal to next symbol then it will call the function handleError with nextToken and the name of the symbol which will handle the error.
     * @throws IOException If there is an error reading the file.
     * @throws CompilationException If there is a compilation error.
     */
    public void acceptTerminal(int symbol) throws IOException, CompilationException {
        // Implement acceptTerminal() method
        if(nextToken.symbol == symbol){
            _printIndent();
            myGenerate.insertTerminal(nextToken);
            nextToken = lex.getNextToken();
        }
        else{
            handleError(nextToken, Token.getName(symbol));
        }
    }
    /**
     * This function finds the next synchronizing token in this case a semicolon in the list. It does this by looping through every token till it reaches a semicolon or a EOF token
     * 
     * @throws IOException If there is an error reading the file.
     * @throws CompilationException If there is a compilation error.
     */
    public void findSynchronizingToken() throws IOException{
        while (nextToken.symbol != Token.semicolonSymbol && nextToken.symbol != Token.eofSymbol) {
            nextToken = lex.getNextToken();
        }
    }
    /**
     * This function calls the function parse in the super class
     * 
     */
    public void parse(PrintStream ps) throws IOException {
        super.parse(ps);
    }
}
