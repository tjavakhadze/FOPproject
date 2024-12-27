import java.util.HashMap;
import java.util.Map;

public class MinimalInterpreter {
    private final Map<String, Number> variables = new HashMap<>();
    private final Map<String, Boolean> boolvar = new HashMap<>();
    private final Map<String, String> stringvar = new HashMap<>();

    public void eval(String code) {
        String[] lines = code.split("\n");
        boolean insideIf = false;
        boolean ifCondition = false;
        boolean insideWhile = false;
        StringBuilder block = new StringBuilder();
        int whileStartIndex = -1;

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) continue;

            if (line.startsWith("if")) {
                insideIf = true;
                String condition = line.substring(2).trim();
                ifCondition = evaluateCondition(condition);
                continue;
            }

            if (line.startsWith("while")) {
                insideWhile = true;
                whileStartIndex = i;
                String condition = line.substring(5).trim();
                if (evaluateCondition(condition)) {
                    continue;
                } else {
                    while (i < lines.length && !lines[i].trim().equals("end")) {
                        i++;
                    }
                    insideWhile = false;
                    continue;
                }
            }

            if (insideWhile) {
                if (line.startsWith("end")) {
                    evalBlock(block.toString());
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
            }
            if (insideIf) {
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
                } else if (line.startsWith("return")) {
                    handleReturn(line); // handle print statements
                }
                else if (line.startsWith("puts") || line.startsWith("print")) {
                    handlePrint(line); // handle print statements
                }
            }
        }
    }

    private Object handleReturn(String line){
        return line;
    }

    private void evalBlock(String block) {
        String[] lines = block.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (!line.isEmpty()) {
                if (line.contains("=")) {
                    handleAssignment(line);
                } else if (line.startsWith("puts") || line.startsWith("print")) {
                    handlePrint(line);
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

        if (condition.contains("==")) {
            String[] parts = condition.split("==");
            String varName = parts[0].trim();
            String expectedValue = parts[1].trim();

            if (boolvar.containsKey(varName)) {
                Boolean varValue = boolvar.get(varName);
                return varValue.toString().equals(expectedValue);
            } else if (variables.containsKey(varName)) {
                Number varValue = variables.get(varName);
                try {
                    double expected = Double.parseDouble(expectedValue);
                    return varValue.doubleValue() == expected;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number format: " + expectedValue);
                    return false;
                }
            }
        }if (condition.contains("!=")) {
            String[] parts = condition.split("!=");
            String varName = parts[0].trim();
            String expectedValue = parts[1].trim();

            if (boolvar.containsKey(varName)) {
                Boolean varValue = boolvar.get(varName);
                return varValue.toString().equals(expectedValue);
            } else if (variables.containsKey(varName)) {
                Number varValue = variables.get(varName);
                try {
                    double expected = Double.parseDouble(expectedValue);
                    return varValue.doubleValue() != expected;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number format: " + expectedValue);
                    return false;
                }
            }
        } else if (condition.contains("<=")){
            String[] parts = condition.split("<=");
            String varName = parts[0].trim();
             String expectedValue = parts[1].trim();

            if (boolvar.containsKey(varName)) {
                Boolean varValue = boolvar.get(varName);
                return varValue.toString().equals(expectedValue);
            } else if (variables.containsKey(varName)) {
                Number varValue = variables.get(varName);
                try {
                    double expected = Double.parseDouble(expectedValue);
                    return (varValue.doubleValue() == expected) ||(varValue.doubleValue() < expected) ;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number format: " + expectedValue);
                    return false;
                }
            }
        }else if (condition.contains(">=")){
            String[] parts = condition.split(">=");
            String varName = parts[0].trim();
             String expectedValue = parts[1].trim();

            if (boolvar.containsKey(varName)) { // checks if variable is boolean
                Boolean varValue = boolvar.get(varName);
                return varValue.toString().equals(expectedValue);
            } else if (variables.containsKey(varName)) { // checks if variable is numeric
                Number varValue = variables.get(varName);
                try {
                    double expected = Double.parseDouble(expectedValue);
                    return (varValue.doubleValue() == expected) ||(varValue.doubleValue() > expected) ;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number format: " + expectedValue);
                    return false;
                }
            }
        }else if (condition.contains("<")) {
            String[] parts = condition.split("<");
            String varName = parts[0].trim();
            String expectedValue = parts[1].trim();

            if (variables.containsKey(varName)) {
                Number varValue = variables.get(varName);
                try {
                    double expected = Double.parseDouble(expectedValue);
                    return varValue.doubleValue() < expected;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number format: " + expectedValue);
                    return false;
                }
            }
        } else if (condition.contains(">")) {
        String[] parts = condition.split(">");
        String varName = parts[0].trim();
        String expectedValue = parts[1].trim();

        if (variables.containsKey(varName)) {
            Number varValue = variables.get(varName);
            try {
                double expected = Double.parseDouble(expectedValue);
                return varValue.doubleValue() > expected;
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format: " + expectedValue);
                return false;
            }
        }
    } else if (condition.equals("true")) {
            return true;
        } else if (condition.equals("false")) {
            return false;
        }
        return false;
    }

    private void handleAssignment(String line) {
        String[] parts = line.split("=");

        if (parts.length != 2) {
            System.out.println("Invalid assignment syntax: " + line);
            return;
        }

        String varName = parts[0].trim();
        String expression = parts[1].trim();

        if (expression.startsWith("\"") && expression.endsWith("\"")) { // handles string assignments
            if(variables.containsKey(varName)||boolvar.containsKey(varName)){
                variables.remove(varName);
                boolvar.remove(varName);
            }
            String value = expression.substring(1, expression.length() - 1);
            stringvar.put(varName, value);
        } else if (expression.equals("false") || expression.equals("true")) { // handles boolean assignments
             if(variables.containsKey(varName)||stringvar.containsKey(varName)){
                variables.remove(varName);
                stringvar.remove(varName);
            }
            Boolean value = evaluateBool(expression);
            boolvar.put(varName, value);
        } else { // handles numeric assignments
             if(stringvar.containsKey(varName)||boolvar.containsKey(varName)){
                stringvar.remove(varName);
                boolvar.remove(varName);
            }
            Number value = evaluateExpression(expression);
            variables.put(varName, value);
        }
    }

    private Boolean evaluateBool(String expression) {
        return expression.equals("true");
    }

    private Number evaluateExpression(String expression) {
        expression = expression.replaceAll("\\s", "");

        while (expression.contains("(")) { // evaluates nested expressions
            int openIndex = expression.lastIndexOf("("); // find innermost opening parenthesis
            int closeIndex = expression.indexOf(")", openIndex); // find corresponding closing parenthesis
            String subExpression = expression.substring(openIndex + 1, closeIndex);
            double subResult = evaluateSimpleExpression(subExpression);
            expression = expression.substring(0, openIndex) + subResult + expression.substring(closeIndex + 1);
        }

        return evaluateSimpleExpression(expression); // evaluate simplified expression
    }

    private double evaluateSimpleExpression(String expression) {
        if (expression.contains("**")) {
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

            if ("+-*/%".contains(String.valueOf(term.charAt(0)))) {
                operator = term;
            } else {
                double operand = parseOperand(term);
                switch (operator) {
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
                    case "+":
                        result += operand;
                        break;
                    case "-":
                        result -= operand;
                        break;
                }
            }
        }

        return result;
    }

    private double parseOperand(String term) {
        if (variables.containsKey(term)) {
            return variables.get(term).doubleValue();
        } else {
            try {
                return Double.parseDouble(term);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format: " + term);
                return 0;
            }
        }
    }

private void handlePrint(String line) {
    String expr;


    if (line.contains("(") && line.contains(")")) {
        expr = line.substring(line.indexOf('(') + 1, line.indexOf(')')).trim();
        if (expr.isEmpty()) {
            System.out.println();
            return;
        }
    } else {
        expr = line.substring(line.indexOf(' ') + 1).trim();
    }

    // Check if the expression is a string literal
    if (expr.startsWith("\"") && expr.endsWith("\"")) {
        System.out.println(expr.substring(1, expr.length() - 1));
    } else if (expr.equals("true") || expr.equals("false")) { // Check for boolean literals
        System.out.println(expr);
    } else if (boolvar.containsKey(expr)) { // Check if it's a boolean variable
        Boolean varValue = boolvar.get(expr);
        System.out.println(varValue);
    } else if (variables.containsKey(expr)) { // Check if it's a numeric variable
        Number varValue = variables.get(expr);
        if (varValue != null) {
            if (varValue.doubleValue() == (int) varValue.doubleValue()) {
                System.out.println((int) varValue.doubleValue());
            } else {
                System.out.println(varValue);
            }
        }
    } else if (stringvar.containsKey(expr)) { // Check if it's a string variable
        System.out.println(stringvar.get(expr));
    } else {
        // Evaluate the expression as a numeric one if it's not a string or boolean
        double result = evaluateExpression(expr).doubleValue();

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
                     a = true
                   
                     a = 5
                     puts a
          """;
        interpreter.eval(prog);
    }

}
