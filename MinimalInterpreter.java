import java.util.HashMap;
import java.util.Map;
//test4

public class MinimalInterpreter {
    private final Map<String, Number> variables = new HashMap<>(); // stores numeric variables
    private final Map<String, Boolean> boolvar = new HashMap<>(); // stores boolean variables
    private final Map<String, String> stringvar = new HashMap<>(); // stores string variables

    public void eval(String code) {
        String[] lines = code.split("\n"); // splits code into individual lines
        boolean insideIf = false; // tracks whether inside an 'if' block
        boolean ifCondition = false; // stores the result of the 'if' condition
        boolean insideWhile = false; // tracks whether inside a 'while' loop
        StringBuilder block = new StringBuilder(); // collects code within blocks
        int whileStartIndex = -1; // remembers the starting index of a 'while' block

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim(); // removes leading and trailing spaces
            if (line.isEmpty()) continue; // skips empty lines

            if (line.startsWith("if")) {
                insideIf = true; // entering an 'if' block
                String condition = line.substring(2).trim(); // extracts the condition
                ifCondition = evaluateCondition(condition); // evaluates the condition
                continue;
            }

            if (line.startsWith("while")) {
                insideWhile = true; // entering a 'while' loop
                whileStartIndex = i; // remembers where the loop started
                String condition = line.substring(5).trim(); // extracts the loop condition
                if (evaluateCondition(condition)) {
                    continue; // proceed if condition is true
                } else {
                    while (i < lines.length && !lines[i].trim().equals("end")) {
                        i++; // skip loop if condition is false
                    }
                    insideWhile = false; // exit the loop
                    continue;
                }
            }

            if (insideWhile) {
                if (line.startsWith("end")) { // end of the 'while' block
                    evalBlock(block.toString()); // evaluate the collected block
                    block.setLength(0); // reset the block
                    String condition = lines[whileStartIndex].substring(5).trim(); // reevaluate condition
                    if (evaluateCondition(condition)) {
                        i = whileStartIndex; // restart loop if condition is true
                    } else {
                        insideWhile = false; // exit loop if condition is false
                    }
                    continue;
                }
                block.append(line).append("\n"); // add line to the 'while' block
            } else if (insideIf) {
                if (line.startsWith("else")) { // entering 'else' block
                    if (ifCondition) {
                        evalBlock(block.toString()); // execute 'if' block if condition was true
                    }
                    block.setLength(0); // reset block for 'else'
                    continue;
                } else if (line.startsWith("end")) { // end of 'if-else' block
                    if (!ifCondition) {
                        evalBlock(block.toString()); // execute 'else' block if 'if' condition was false
                    }
                    insideIf = false; // exit 'if'
                    block.setLength(0); // reset the block
                    continue;
                }
                block.append(line).append("\n"); // add line to 'if' or 'else' block
            } else {
                if (line.contains("=")) {
                    handleAssignment(line); // handle variable assignment
                } else if (line.startsWith("puts") || line.startsWith("print")) {
                    handlePrint(line); // handle print statements
                }
            }
        }
    }

    private void evalBlock(String block) {
        String[] lines = block.split("\n"); // splits block into lines
        for (String line : lines) {
            line = line.trim(); // trims each line
            if (!line.isEmpty()) {
                if (line.contains("=")) {
                    handleAssignment(line); // process assignments
                } else if (line.startsWith("puts") || line.startsWith("print")) {
                    handlePrint(line); // process print statements
                }
            }
        }
    }

    private boolean evaluateCondition(String condition) {
         if (condition.contains("&&")) {
            String[] parts = condition.split("&&");
            return evaluateCondition(parts[0].trim()) && evaluateCondition(parts[1].trim());
        }
        if (condition.contains("||")) {
            String[] parts = condition.split("\\|\\|");
            return evaluateCondition(parts[0].trim()) || evaluateCondition(parts[1].trim());
        }

        if (condition.contains("==")) { // handles equality checks
            String[] parts = condition.split("==");
            String varName = parts[0].trim(); // extract variable name
            String expectedValue = parts[1].trim(); // extract expected value

            if (boolvar.containsKey(varName)) { // checks if variable is boolean
                Boolean varValue = boolvar.get(varName);
                return varValue.toString().equals(expectedValue); // compares with expected
            } else if (variables.containsKey(varName)) { // checks if variable is numeric
                Number varValue = variables.get(varName);
                try {
                    double expected = Double.parseDouble(expectedValue);
                    return varValue.doubleValue() == expected; // compares values
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number format: " + expectedValue);
                    return false;
                }
            }
        } else if (condition.contains("<")) { // handles less-than conditions
            String[] parts = condition.split("<");
            String varName = parts[0].trim();
            String expectedValue = parts[1].trim();

            if (variables.containsKey(varName)) {
                Number varValue = variables.get(varName);
                try {
                    double expected = Double.parseDouble(expectedValue);
                    return varValue.doubleValue() < expected; // checks if less than expected
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number format: " + expectedValue);
                    return false;
                }
            }
        } else if (condition.contains(">")) { // handles greater-than conditions
        String[] parts = condition.split(">");
        String varName = parts[0].trim();
        String expectedValue = parts[1].trim();

        if (variables.containsKey(varName)) {
            Number varValue = variables.get(varName);
            try {
                double expected = Double.parseDouble(expectedValue);
                return varValue.doubleValue() > expected; // checks if greater than expected
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format: " + expectedValue);
                return false;
            }
        }
    } else if (condition.equals("true")) { // constant 'true'
            return true;
        } else if (condition.equals("false")) { // constant 'false'
            return false;
        }
        return false;
    }

    private void handleAssignment(String line) {
        String[] parts = line.split("="); // splits line into variable and expression

        if (parts.length != 2) {
            System.out.println("Invalid assignment syntax: " + line);
            return;
        }

        String varName = parts[0].trim(); // extracts variable name
        String expression = parts[1].trim(); // extracts assigned expression

        if (expression.startsWith("\"") && expression.endsWith("\"")) { // handles string assignments
            String value = expression.substring(1, expression.length() - 1);
            stringvar.put(varName, value);
        } else if (expression.equals("false") || expression.equals("true")) { // handles boolean assignments
            Boolean value = evaluateBool(expression);
            boolvar.put(varName, value);
        } else { // handles numeric assignments
            Number value = evaluateExpression(expression);
            variables.put(varName, value);
        }
    }

    private Boolean evaluateBool(String expression) {
        return expression.equals("true"); // evaluates boolean literals
    }

    private Number evaluateExpression(String expression) {
        expression = expression.replaceAll("\\s", ""); // removes spaces for evaluation

        while (expression.contains("(")) { // evaluates nested expressions
            int openIndex = expression.lastIndexOf("("); // find innermost opening parenthesis
            int closeIndex = expression.indexOf(")", openIndex); // find corresponding closing parenthesis
            String subExpression = expression.substring(openIndex + 1, closeIndex); // extract subexpression
            double subResult = evaluateSimpleExpression(subExpression); // calculate subexpression result
            expression = expression.substring(0, openIndex) + subResult + expression.substring(closeIndex + 1);
        }

        return evaluateSimpleExpression(expression); // evaluate simplified expression
    }

    private double evaluateSimpleExpression(String expression) {
        if (expression.contains("**")) { // handles exponentiation
            String[] parts = expression.split("\\*\\*");
            double leftOperand = parseOperand(parts[0].trim());
            double rightOperand = parseOperand(parts[1].trim());
            return Math.pow(leftOperand, rightOperand);
        }

        String[] terms = expression.split("(?=[-+*/%])|(?<=[-+*/%])"); // splits by operators
        double result = 0.0;
        String operator = "+"; // default operator

        for (String term : terms) {
            term = term.trim();
            if (term.isEmpty()) continue;

            if ("+-*/%".contains(String.valueOf(term.charAt(0)))) { // identifies operator
                operator = term;
            } else {
                double operand = parseOperand(term); // parses operand value
                switch (operator) { // applies operator
                    case "+":
                        result += operand;
                        break;
                    case "-":
                        result -= operand;
                        break;
                    case "*":
                        result *= operand;
                        break;
                    case "/":
                        if (operand != 0) {
                            result /= operand;
                        } else {
                            System.out.println("Error: Division by zero.");
                        }
                        break;
                    case "%":
                        result %= operand;
                        break;
                }
            }
        }

        return result; // returns calculated result
    }

    private double parseOperand(String term) {
        if (variables.containsKey(term)) { // retrieves variable value if exists
            return variables.get(term).doubleValue();
        } else {
            try {
                return Double.parseDouble(term); // parses numeric literal
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format: " + term);
                return 0; // returns default value on error
            }
        }
    }

private void handlePrint(String line) {
    String expr;

    // Check if the print statement contains parentheses with an expression
    if (line.contains("(") && line.contains(")")) {
        expr = line.substring(line.indexOf('(') + 1, line.indexOf(')')).trim();
        if (expr.isEmpty()) {
            System.out.println();
            return;
        }
    } else {
        expr = line.substring(line.indexOf(' ') + 1).trim(); // Extract expression after 'puts' or 'print'
    }

    // Check if the expression is a string literal (enclosed in quotes)
    if (expr.startsWith("\"") && expr.endsWith("\"")) {
        System.out.println(expr.substring(1, expr.length() - 1)); // Print the string without quotes
    } else if (expr.equals("true") || expr.equals("false")) { // Check for boolean literals
        System.out.println(expr); // Print the boolean value (true/false)
    } else if (boolvar.containsKey(expr)) { // Check if it's a boolean variable
        Boolean varValue = boolvar.get(expr);
        System.out.println(varValue); // Print the boolean value (true/false)
    } else if (variables.containsKey(expr)) { // Check if it's a numeric variable
        Number varValue = variables.get(expr);
        if (varValue != null) {
            // Print numeric value of the variable
            if (varValue.doubleValue() == (int) varValue.doubleValue()) {
                System.out.println((int) varValue.doubleValue());
            } else {
                System.out.println(varValue);
            }
        }
    } else if (stringvar.containsKey(expr)) { // Check if it's a string variable
        System.out.println(stringvar.get(expr)); // Print the string variable
    } else {
        // Evaluate the expression as a numeric one if it's not a string or boolean
        double result = evaluateExpression(expr).doubleValue();
        // Print the result of the evaluated expression
        if (result == (int) result) { // If result is an integer value, print as integer
            System.out.println((int) result);
        } else {
            System.out.println(result); // Print as double if not integer
        }
    }
}


    public static void main(String[] args) {
        MinimalInterpreter interpreter = new MinimalInterpreter();
        String prog = """
          n = "sum"
          puts n
          k = 5
          print k
          e = false
          puts (e)
          print 6*2
          """;
        interpreter.eval(prog); // executes the sample program
    }
}
