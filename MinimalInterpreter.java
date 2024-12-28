import java.util.HashMap;
import java.util.Map;
import java.util.*;
public class MinimalInterpreter {
    private final Map<String, Integer> variables = new HashMap<>();
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

    while (evaluateCondition(condition)) {
        evalBlock(block.toString());
        block.setLength(0);

        for (int j = whileStartIndex + 1; j < lines.length; j++) {
            String loopLine = lines[j].trim();
            if (loopLine.startsWith("end")) {
                break;
            }
            block.append(loopLine).append("\n");
        }
    }

    while (i < lines.length && !lines[i].trim().equals("end")) {
        i++;
    }
    insideWhile = false;
    block.setLength(0);
    continue;
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
                }
                else if (line.startsWith("puts") || line.startsWith("print")) {
                    handlePrint(line); // handle print statements
                }
            }
        }
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

            if (boolvar.containsKey(varName)&&boolvar.containsKey(expectedValue)) {
                Boolean varValue = boolvar.get(varName);
                Boolean right = boolvar.get(expectedValue);
                return varValue.toString().equals(right.toString());
            }else if (boolvar.containsKey(varName)) {
                Boolean varValue = boolvar.get(varName);
                return varValue.toString().equals(expectedValue);
            } else if (boolvar.containsKey(expectedValue)) {
                Boolean right = boolvar.get(expectedValue);
                return right.toString().equals((varName));
            } else if (variables.containsKey(varName)&&variables.containsKey(expectedValue)) {
                Integer varValue = variables.get(varName);
                Integer right = variables.get(expectedValue);
                return Objects.equals(varValue, right);
            } else if (variables.containsKey(varName)){
                 Integer varValue = variables.get(varName);
                 try {
                    return varValue == Integer.parseInt(expectedValue);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number format: " + expectedValue);
                    return false;
                }
            }else if (variables.containsKey(expectedValue)){
                Integer right = variables.get(expectedValue);
                 try {
                    return right == Integer.parseInt(varName);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number format: " + varName);
                    return false;
                }
            }
                else if (stringvar.containsKey(varName)&&stringvar.containsKey(expectedValue)) {
                String varValue = stringvar.get(varName);
                String right = stringvar.get(expectedValue);
                return varValue.equals(right);
            } else if(stringvar.containsKey(varName)){
                    String varValue = stringvar.get(varName);
                    return varValue.equals(expectedValue);
            }else if(stringvar.containsKey(expectedValue)){
                    String right = stringvar.get(expectedValue);
                    return right.equals(varName);
            }
        }if (condition.contains("!=")) {
            String[] parts = condition.split("!=");
            String varName = parts[0].trim();
            String expectedValue = parts[1].trim();
            if (boolvar.containsKey(varName)&&boolvar.containsKey(expectedValue)) {
                Boolean varValue = boolvar.get(varName);
                Boolean right = boolvar.get(expectedValue);
                return !(varValue.toString().equals(right.toString()));
            }else if (boolvar.containsKey(varName)) {
                Boolean varValue = boolvar.get(varName);
                return !(varValue.toString().equals(expectedValue));
            } else if (boolvar.containsKey(expectedValue)) {
                Boolean right = boolvar.get(expectedValue);
                return !(right.toString().equals(varName));
            } else if(variables.containsKey(varName)&&variables.containsKey(expectedValue)){
                 Integer varValue = variables.get(varName);
                 Integer right = variables.get(expectedValue);
                 return (!Objects.equals(varValue, right));
            } else if(variables.containsKey(expectedValue)){
                 Integer right = variables.get(expectedValue);
                 try {
                    return right != Integer.parseInt(varName);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number format: " + varName);
                    return false;
                }
            } else if (variables.containsKey(varName)) {
                Integer varValue = variables.get(varName);
                try {
                    int expected = Integer.parseInt(expectedValue);
                    return varValue != expected;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number format: " + expectedValue);
                    return false;
                }
            }
        } else if (condition.contains("<=")){
            String[] parts = condition.split("<=");
            String varName = parts[0].trim();
             String expectedValue = parts[1].trim();
             if(variables.containsKey(varName)&&variables.containsKey(expectedValue)){
                 Integer varValue = variables.get(varName);
                 Integer right = variables.get(expectedValue);
                 return (Objects.equals(varValue, right)) ||(varValue < right) ;
             } else if (variables.containsKey(varName)) {
                Integer varValue = variables.get(varName);
                try {
                    int expected = Integer.parseInt(expectedValue);
                    return (varValue == expected) ||(varValue < expected) ;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number format: " + expectedValue);
                    return false;
                }
            } else if (variables.containsKey(expectedValue)){
                 Integer right = variables.get(expectedValue);
                 try {
                    return (Integer.parseInt(varName)==right ) ||(Integer.parseInt(varName) < right) ;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number format: " + expectedValue);
                    return false;
                }
             }
        }else if (condition.contains(">=")){
            String[] parts = condition.split(">=");
            String varName = parts[0].trim();
            String expectedValue = parts[1].trim();
               if(variables.containsKey(varName)&&variables.containsKey(expectedValue)){
                 Integer varValue = variables.get(varName);
                 Integer right = variables.get(expectedValue);
                 return (Objects.equals(varValue, right)) ||(varValue > right) ;
             } else if (variables.containsKey(varName)) {
                Integer varValue = variables.get(varName);
                try {
                    int expected = Integer.parseInt(expectedValue);
                    return (varValue == expected) ||(varValue > expected) ;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number format: " + expectedValue);
                    return false;
                }
            } else if (variables.containsKey(expectedValue)){
                 Integer right = variables.get(expectedValue);
                 try {
                    return (Integer.parseInt(varName)==right ) ||(Integer.parseInt(varName) > right) ;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number format: " + varName);
                    return false;
                }

            }
        }else if (condition.contains("<")) {
            String[] parts = condition.split("<");
            String varName = parts[0].trim();
            String expectedValue = parts[1].trim();
            if(variables.containsKey(varName)&&variables.containsKey(expectedValue)){
                 Integer varValue = variables.get(varName);
                 Integer right = variables.get(expectedValue);
                 return varValue < right ;
             } else if (variables.containsKey(varName)) {
                Integer varValue = variables.get(varName);
                try {
                    int expected = Integer.parseInt(expectedValue);
                    return varValue < expected;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number format: " + expectedValue);
                    return false;
                }
            } else if (variables.containsKey(expectedValue)){
                 Integer right = variables.get(expectedValue);
                 try {
                    return Integer.parseInt(varName) < right ;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number format: " + varName);
                    return false;
                }
             }
        } else if (condition.contains(">")) {
        String[] parts = condition.split(">");
        String varName = parts[0].trim();
        String expectedValue = parts[1].trim();
        if(variables.containsKey(varName)&&variables.containsKey(expectedValue)){
                 Integer varValue = variables.get(varName);
                 Integer right = variables.get(expectedValue);
                 return varValue > right;
             } else if (variables.containsKey(varName)) {
                Integer varValue = variables.get(varName);
                try {
                    int expected = Integer.parseInt(expectedValue);
                    return varValue > expected;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number format: " + expectedValue);
                    return false;
                }
            } else if (variables.containsKey(expectedValue)){
                 Integer right = variables.get(expectedValue);
                 try {
                    return Integer.parseInt(varName) > right;
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
        } else {
             if(stringvar.containsKey(varName)||boolvar.containsKey(varName)){
                stringvar.remove(varName);
                boolvar.remove(varName);
            }
            Integer value = evaluateSimpleExpression(expression);
            variables.put(varName, value);
        }
    }

    private Boolean evaluateBool(String expression) {
        return expression.equals("true");
    }

  private int evaluateSimpleExpression(String expression) {
      expression = expression.replaceAll("\\s", ""); // Remove spaces
    while (expression.contains("**")) {
        int opIndex = expression.indexOf("**");
        int leftOperand = extractLeftOperand(expression, opIndex - 1);
        int rightOperand = extractRightOperand(expression, opIndex + 2);
        int result = (int) Math.pow(leftOperand, rightOperand);
        expression = replaceSubExpression(expression, opIndex, "**", result);
    }

    String[] terms = expression.split("(?=[-+*/%])|(?<=[-+*/%])");
    List<String> termList = new ArrayList<>(Arrays.asList(terms));

    // Handle '*' '/' '%'
    for (int i = 0; i < termList.size(); i++) {
        String term = termList.get(i);
        if ("*/%".contains(term)) {
            int leftOperand = parseOperand(termList.get(i - 1));
            int rightOperand = parseOperand(termList.get(i + 1));
            int result;

            switch (term) {
                case "*":
                    result = leftOperand * rightOperand;
                    break;
                case "/":
                    if (rightOperand != 0) {
                        result = leftOperand / rightOperand;

                    } else {
                        throw new ArithmeticException("Division by zero.");
                    }
                    break;
                case "%":
                    result = leftOperand % rightOperand;
                    break;
                default:
                    throw new IllegalStateException("Unexpected operator: " + term);
            }

            // Replace the operands and operator with the result
            termList.set(i - 1, String.valueOf(result));
            termList.remove(i); // Remove the operator
            termList.remove(i); // Remove the right operand
            i--; // Adjust index after removal
        }
    }

    // Handle '+' and '-'
    int result = parseOperand(termList.get(0));
    for (int i = 1; i < termList.size(); i += 2) {
        String operator = termList.get(i);
        int operand = parseOperand(termList.get(i + 1));

        if (operator.equals("+")) {
            result += operand;
        } else if (operator.equals("-")) {
            result -= operand;
        }
    }

    return result;
}

private int extractLeftOperand(String expression, int start) {
    int i = start;
    while (i >= 0 && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.' || expression.charAt(i) == '-')) {
        i--;
    }
    return parseOperand(expression.substring(i + 1, start + 1));
}

private int extractRightOperand(String expression, int start) {
    int i = start;
    while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
        i++;
    }
    return parseOperand(expression.substring(start, i));
}

private String replaceSubExpression(String expression, int opIndex, String operator, int result) {
    int leftStart = opIndex - 1;
    while (leftStart >= 0 && (Character.isDigit(expression.charAt(leftStart)) || expression.charAt(leftStart) == '.' || expression.charAt(leftStart) == '-')) {
        leftStart--;
    }

    int rightEnd = opIndex + operator.length();
    while (rightEnd < expression.length() && (Character.isDigit(expression.charAt(rightEnd)) || expression.charAt(rightEnd) == '.')) {
        rightEnd++;
    }

    String subExpression = expression.substring(leftStart + 1, rightEnd);
    return expression.replace(subExpression, String.valueOf(result));
}

private int parseOperand(String term) {
    if (variables.containsKey(term)) {
        return variables.get(term);
    } else {
        try {
            return Integer.parseInt(term);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format: " + term);
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

    if (expr.startsWith("\"") && expr.endsWith("\"")) {
        System.out.println(expr.substring(1, expr.length() - 1));
    } else if (expr.equals("true") || expr.equals("false")) {
        System.out.println(expr);
    } else if (boolvar.containsKey(expr)) { // Check if it's a boolean variable
        Boolean varValue = boolvar.get(expr);
        System.out.println(varValue);
    } else if (variables.containsKey(expr)) { // Check if it's a numeric variable
        Integer varValue = variables.get(expr);
         if (varValue != null) {
          System.out.println(varValue);

        }
    } else if (stringvar.containsKey(expr)) { // Check if it's a string variable
        System.out.println(stringvar.get(expr));
    } else {
        // Evaluate the expression as a numeric one if it's not a string or boolean
        int result = evaluateSimpleExpression(expr);


            System.out.println(result); // Print as double if not integer

    }
}
public static void main(String[] args) {
        MinimalInterpreter interpreter = new MinimalInterpreter();
        String prog = """


    """;
        interpreter.eval(prog);
    }

}