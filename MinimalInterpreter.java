//for now this only works for reading and printing variables, arithmetic operations, booleans and if-else statements. I will be adding further changes later.
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
        StringBuilder block = new StringBuilder();

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            if (line.startsWith("if")) {
                insideIf = true;
                String condition = line.substring(2).trim();
                ifCondition = evaluateCondition(condition);
                continue;
            }

            if (insideIf) {
                if (line.startsWith("else")) {
                    if (ifCondition) {
                        evalBlock(block.toString());
                    }
                    block.setLength(0);
                    continue;
                } else if (line.startsWith("end")) {
                    if (!ifCondition) {
                        evalBlock(block.toString());
                    }
                    insideIf = false;
                    block.setLength(0);
                    continue;
                }
                block.append(line).append("\n");
            } else {
                if (line.contains("=")) {
                    handleAssignment(line);
                } else if (line.startsWith("puts") || line.startsWith("print")) {
                    handlePrint(line);
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

        if (expression.startsWith("\"") && expression.endsWith("\"")) {
            String value = expression.substring(1, expression.length() - 1);
            stringvar.put(varName, value);
        } else if (expression.equals("false") || expression.equals("true")) {
            Boolean value = evaluateBool(expression);
            boolvar.put(varName, value);
        } else {
            Number value = evaluateExpression(expression);
            variables.put(varName, value);
        }
    }

    private Boolean evaluateBool(String expression) {
        return expression.equals("true");
    }

    private Number evaluateExpression(String expression) {
        expression = expression.replaceAll("\\s", "");
        while (expression.contains("(")) {
            int openIndex = expression.lastIndexOf("(");
            int closeIndex = expression.indexOf(")", openIndex);
            String subExpression = expression.substring(openIndex + 1, closeIndex);
            double subResult = evaluateSimpleExpression(subExpression);
            expression = expression.substring(0, openIndex) + subResult + expression.substring(closeIndex + 1);
        }
        return evaluateSimpleExpression(expression);
    }

    private double evaluateSimpleExpression(String expression) {
        String[] terms = expression.split("(?=[-+*/%])|(?<=[-+*/%])");
        double result = 0.0;
        String operator = "+";

        for (String term : terms) {
            term = term.trim();
            if (term.isEmpty()) continue;

            // Handle operator
            if ("+-*/%".contains(String.valueOf(term.charAt(0)))) {
                operator = term;
            } else {
                // Handle operand
                double operand = parseOperand(term);
                switch (operator) {
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
                    default:
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
        String varName;

        if (line.contains("(") && line.contains(")")) {
            varName = line.substring(line.indexOf('(') + 1, line.indexOf(')')).trim();
            if (varName.isEmpty()) {
                System.out.println();
                return;
            }
        } else {
            varName = line.substring(line.indexOf(' ') + 1).trim();
        }

        if (variables.containsKey(varName)) {
            Number value = variables.get(varName);
            if (value instanceof Double && value.doubleValue() == value.intValue()) {
                System.out.println(value.intValue());
            } else {
                System.out.println(value);
            }
        } else if (boolvar.containsKey(varName)) {
            System.out.println(boolvar.get(varName));
        } else if (stringvar.containsKey(varName)) {
            System.out.println(stringvar.get(varName));
        } else if ("true".equals(varName)) {
            System.out.println(true);
        } else if ("false".equals(varName)) {
            System.out.println(false);
        } else if (varName.startsWith("\"") && varName.endsWith("\"")) {
            System.out.println(varName.substring(1, varName.length() - 1));
        } else {
            try {
                double number = Double.parseDouble(varName);
                if (number == (int) number) {
                    System.out.println((int) number);
                } else {
                    System.out.println(number);
                }
            } catch (NumberFormatException e) {
                System.out.println("Undefined variable: " + varName);
            }
        }
    }

    public static void main(String[] args) {
        MinimalInterpreter interpreter = new MinimalInterpreter();

        String program = """
            puts 3
            puts (3)
            print false
            print(false)
            print "wow"
            puts "wow"
            t = true
            if t == true
              puts "yes"
            else
              puts "no"
            end
            
            y = 3 + 4 * (2 - 1)
            puts y
            z = (2 + 3) * 5
            puts z
            a = 2 * 2 + 3
            puts a
           
     
        """;

        interpreter.eval(program);
    }
}
