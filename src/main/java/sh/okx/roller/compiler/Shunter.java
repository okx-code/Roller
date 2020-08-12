package sh.okx.roller.compiler;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import sh.okx.roller.compiler.Token.Notation;
import sh.okx.roller.compiler.Token.Type;

public class Shunter {
    public List<Token> shunt(List<Token> tokens) {
        List<Token> outputQueue = new ArrayList<>();
        Deque<Token> operatorStack = new ArrayDeque<>();

        for (Token token : tokens) {
            Type type = token.getType();
            if (type.arity() == 0) {
                outputQueue.add(token);
            } else if (type.notation() == Notation.PREFIX) {
                operatorStack.push(token);
            } else {
                if (!operatorStack.isEmpty()) {
                    while (!operatorStack.isEmpty() && operatorStack.peek().getType().precedence() > type.precedence()) {
                        outputQueue.add(operatorStack.pop());
                    }
                }
                operatorStack.push(token);
            }
        }
        outputQueue.addAll(operatorStack);

        return outputQueue;
        /*
while there are tokens to be read:
    read a token.
    if the token is a number, then:
        push it to the output queue.
    else if the token is a function then:
        push it onto the operator stack
    else if the token is an operator then:
        while ((there is a operator at the top of the operator stack)
              and ((the operator at the top of the operator stack has greater precedence)
               or (the operator at the top of the operator stack has equal precedence and the token is left associative))
              and (the operator at the top of the operator stack is not a left parenthesis)):
            pop operators from the operator stack onto the output queue.
        push it onto the operator stack.
    else if the token is a left parenthesis (i.e. "("), then:
        push it onto the operator stack.
    else if the token is a right parenthesis (i.e. ")"), then:
        while the operator at the top of the operator stack is not a left parenthesis:
            pop the operator from the operator stack onto the output queue.
        /* If the stack runs out without finding a left parenthesis, then there are mismatched parentheses. *
        if there is a left parenthesis at the top of the operator stack, then:
        pop the operator from the operator stack and discard it
        /* After while loop, if operator stack not null, pop everything to output queue *
        if there are no more tokens to read then:
        while there are still operator tokens on the stack:
        /* If the operator token on the top of the stack is a parenthesis, then there are mismatched parentheses. *
        pop the operator from the operator stack onto the output queue.
                exit.
         */
    }
}
