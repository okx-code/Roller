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

        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            Type type = token.getType();
            if (type.arity() == 0) {
                outputQueue.add(token);
            } else if (type == Type.RIGHT_PARENTHESIS) {
                while (!operatorStack.isEmpty()
                        && operatorStack.peek().getType() != Type.LEFT_PARENTHESIS) {
                    outputQueue.add(operatorStack.pop());
                }
                if (!operatorStack.isEmpty()
                        && operatorStack.peek().getType() == Type.LEFT_PARENTHESIS) {
                    operatorStack.pop();
                }
            } else if (type.notation() == Notation.PREFIX) {
                operatorStack.push(token);
            } else {
                if (!operatorStack.isEmpty()) {
                    while (!operatorStack.isEmpty()
                            && operatorStack.peek().getType().precedence() > type.precedence()
                            && operatorStack.peek().getType() != Type.LEFT_PARENTHESIS) {
                        outputQueue.add(operatorStack.pop());
                    }
                }
                if (type == Type.DICE && (i == 0 || tokens.get(i - 1).getType() != Type.LITERAL)) {
                    // default dice count is 1
                    outputQueue.add(new Token(Type.LITERAL, "1"));
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
