//test 2
import java.util.HashMap;
import java.util.Map;

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
        String varName;

        if (line.contains("(") && line.contains(")")) { // extracts variable from parentheses
            varName = line.substring(line.indexOf('(') + 1, line.indexOf(')')).trim();
            if (varName.isEmpty()) {
                System.out.println(); // prints empty line for 'puts'
                return;
            }
        } else {
            varName = line.substring(line.indexOf(' ') + 1).trim(); // extracts variable after space
        }

        if (variables.containsKey(varName)) { // prints numeric variables
            Number value = variables.get(varName);
            if (value instanceof Double && value.doubleValue() == value.intValue()) {
                System.out.println(value.intValue()); // avoids decimal point if unnecessary
            } else {
                System.out.println(value);
            }
        } else if (boolvar.containsKey(varName)) { // prints boolean variables
            System.out.println(boolvar.get(varName));
        } else if (stringvar.containsKey(varName)) { // prints string variables
            System.out.println(stringvar.get(varName));
        } else if ("true".equals(varName)) { // prints literal 'true'
            System.out.println(true);
        } else if ("false".equals(varName)) { // prints literal 'false'
            System.out.println(false);
        } else if (varName.startsWith("\"") && varName.endsWith("\"")) { // prints string literals
            System.out.println(varName.substring(1, varName.length() - 1));
        } else {
            try {
                double number = Double.parseDouble(varName); // parses and prints numeric literals
                if (number == (int) number) {
                    System.out.println((int) number);
                } else {
                    System.out.println(number);
                }
            } catch (NumberFormatException e) {
                System.out.println("Undefined variable: " + varName); // error for unknown variables
            }
        }
    }

    public static void main(String[] args) {
        MinimalInterpreter interpreter = new MinimalInterpreter();
        String prog = """
              t = 3
            if t > 2
              puts "yes"
            else
              puts "no"
            end
                """;
        interpreter.eval(prog); // executes the sample program
    }
}
