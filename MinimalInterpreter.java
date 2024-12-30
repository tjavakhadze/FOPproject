import java.util.HashMap;
import java.util.Map;
import java.util.*;
//final test
public class MinimalInterpreter {
    private final Map<String, Integer> variables = new HashMap<>();
    private final Map<String, Boolean> boolvar = new HashMap<>();
    private final Map<String, String> stringvar = new HashMap<>();

   class Block {
    public int ind; // stores index of block end
    public String content; // stores content of the block

    public Block(String content, int ind) { // constructor to initialize block
        this.ind = ind;
        this.content = content;
    }
}

public Block getBlock(String[] lines, int ind) {
    StringBuilder sb = new StringBuilder(); // builder to accumulate block content
    int count = 1; // count block level (if/while nested)
    while (ind < lines.length) { // loop through lines starting from index ind
        String line = lines[ind].trim(); // trim current line
        if (line.startsWith("if") || line.startsWith("while")) { // check if it's a new block
            count++; // increase block level for nested blocks
        } else if (line.startsWith("end")) { // check for end of block
            count--; // decrease block level
        }
        if (count == 0) break; // break when block is completed
        sb.append(line + "\n"); // append line to block content
        ind++; // move to next line
    }

    if (count != 0) { // check if block delimiters are mismatched
        throw new RuntimeException("Syntax Error: Mismatched block delimiters.");
    }
    return new Block(sb.toString(), ind); // return block with content and next index
}

public String getIfElseBlock(String code, boolean condition) {
    Boolean hasElse = false; // flag to track if 'else' is present
    String[] lines = code.split("\n"); // split code into lines
    int d = 1; // depth counter to track 'if' and 'else'
    int elseInd = -1; // index for 'else' block

    for (int i = 0; i < lines.length; i++) { // loop through lines
        String line = lines[i].trim(); // trim line
        if (line.startsWith("if")) d++; // increment depth for 'if'
        else if (line.startsWith("else")) d--; // decrement depth for 'else'

        if (d == 0) { // if depth is 0, we've found the 'else' block
            elseInd = i;
            break;
        }
    }

    StringBuilder ifBlock = new StringBuilder(elseInd == -1 ? code : ""); // initialize if block
    StringBuilder elseBlock = new StringBuilder(""); // initialize else block

    int k = 0;
    if (elseInd != -1) while (k != elseInd) ifBlock.append(lines[k++] + "\n"); // add lines to if block
    if (elseInd != -1) { // if 'else' exists, process else block
        int eI = elseInd + 1;
        while (eI < lines.length) { // add lines to else block
            elseBlock.append(lines[eI++] + "\n");
        }
    }

    return condition ? ifBlock.toString() : elseBlock.toString(); // return appropriate block based on condition
}

public void eval2(String code) {
    String[] lines = code.split("\n"); // split code into lines
    int i = 0;

    while (i < lines.length) { // loop through each line
        String line = lines[i].trim(); // trim line

        if (line.startsWith("while")) { // check for while loop
            Block block = getBlock(lines, i + 1); // get block inside while loop
            String content = block.content; // store block content

            i = block.ind; // update index to end of block
            String condition = line.substring(5).trim(); // get condition after 'while'

            while (evaluateCondition(condition)) { // evaluate condition and repeat loop
                eval2(content); // recursively evaluate the block
            }

        } else if (line.startsWith("if")) { // check for if statement
            Block block = getBlock(lines, i + 1); // get block inside if statement
            String content = block.content; // store block content

            i = block.ind; // update index to end of block
            String condition = line.substring(2).trim(); // get condition after 'if'
            Boolean ifCondition = evaluateCondition(condition); // evaluate if condition

            String ifElseBlock = getIfElseBlock(content, ifCondition); // get appropriate block based on condition

            eval2(ifElseBlock); // recursively evaluate the selected block
        } else { // for regular statements
            if (line.startsWith("puts") || line.startsWith("print")) {
                handlePrint(line); // handle print statements
            } else {
                evalBlock(line); // handle other types of statements
            }
        }
        i++; // move to next line
    }
}

private void evalBlock(String block) {
    String[] lines = block.split("\n"); // split block into lines
    for (String line : lines) { // loop through lines in block
        line = line.trim(); // trim each line
        if (!line.isEmpty()) { // ignore empty lines
            if (line.contains("=")) { // handle assignment statement
                handleAssignment(line);
            } else if (line.startsWith("puts") || line.startsWith("print")) { // handle print statement
                handlePrint(line);
            }
        }
    }
}

private boolean evaluateCondition(String condition) {
    if (condition.equals("true")) return true;
    if (condition.equals("false")) return false;
    if (condition.contains("&&")) { // handle AND condition
        String[] parts = condition.split("&&");
        String left = parts[0].trim();
        if (left.equals("true")) {
            return evaluateCondition(parts[1].trim());
        } else if (left.equals("false")){
            return false;
        }
        return evaluateCondition(parts[0].trim()) && evaluateCondition(parts[1].trim());
    }
    if (condition.contains("||")) { // handle OR condition
        String[] parts = condition.split("\\|\\|");
        String left = parts[0].trim();
        if (left.equals("true")) {
            return true;
        } else if (left.equals("false")){
            return evaluateCondition(parts[1].trim());
        }
        return evaluateCondition(parts[0].trim()) || evaluateCondition(parts[1].trim());
    }

    return (boolean) evaluateSimpleExpression(condition); // evaluate simple condition
}
private void handleAssignment(String line) {
    String[] parts = line.split("="); // split line by '=' to separate variable and expression
    if (parts.length < 2) { // check for invalid assignment syntax
        throw new RuntimeException("Syntax Error: Invalid assignment syntax in line - " + line);
    }
    String varName = parts[0].trim(); // variable name on the left side of '='
    String expression = parts[parts.length - 1].trim(); // expression on the right side of '='

    if (varName.isEmpty() || expression.isEmpty()) { // check if variable or expression is missing
        throw new RuntimeException("Syntax Error: Variable name or expression is missing in assignment.");
    }

    if (expression.startsWith("\"") && expression.endsWith("\"")) { // handles string assignments
        if(variables.containsKey(varName) || boolvar.containsKey(varName)) { // check if variable already exists
            variables.remove(varName); // remove existing variable
            boolvar.remove(varName); // remove existing boolean variable
        }
        String value = expression.substring(1, expression.length() - 1); // extract string value
        stringvar.put(varName, value); // assign string value to the variable
    } else if (expression.equals("false") || expression.equals("true")) { // handles boolean assignments
        if (variables.containsKey(varName) || stringvar.containsKey(varName)) { // check if variable exists
            variables.remove(varName); // remove existing numeric variable
            stringvar.remove(varName); // remove existing string variable
        }
        Boolean value = evaluateBool(expression); // evaluate boolean value
        boolvar.put(varName, value); // assign boolean value to the variable
    } else { // handle numeric assignments
        if (stringvar.containsKey(varName) || boolvar.containsKey(varName)) { // check if variable exists
            stringvar.remove(varName); // remove existing string variable
            boolvar.remove(varName); // remove existing boolean variable
        }
        Integer value = (Integer) evaluateSimpleExpression(expression); // evaluate numeric expression
        variables.put(varName, value); // assign numeric value to the variable
    }
}

private Boolean evaluateBool(String expression) {
    return expression.equals("true"); // return true if expression is 'true', otherwise false
}

private Object evaluateSimpleExpression(String expression) {
    if (expression == null || expression.trim().isEmpty()) { // check if expression is empty or null
        throw new IllegalArgumentException("Expression cannot be empty or null.");
    }

    expression = expression.replaceAll("\\s", ""); // remove spaces
    String operator = null;
    String leftPart = null;
    String rightPart = null;



    // Check for comparison operators and split the expression accordingly
    if (expression.contains("==")) {
        operator = "==";
        String[] parts = expression.split("==", 2);
        leftPart = parts[0].trim();
        rightPart = parts[1].trim();
    } else if (expression.contains("!=")) {
        operator = "!=";
        String[] parts = expression.split("!=", 2);
        leftPart = parts[0].trim();
        rightPart = parts[1].trim();
    } else if (expression.contains("<=")) {
        operator = "<=";
        String[] parts = expression.split("<=", 2);
        leftPart = parts[0].trim();
        rightPart = parts[1].trim();
    } else if (expression.contains(">=")) {
        operator = ">=";
        String[] parts = expression.split(">=", 2);
        leftPart = parts[0].trim();
        rightPart = parts[1].trim();
    } else if (expression.contains("<")) {
        operator = "<";
        String[] parts = expression.split("<", 2);
        leftPart = parts[0].trim();
        rightPart = parts[1].trim();
    } else if (expression.contains(">")) {
        operator = ">";
        String[] parts = expression.split(">", 2);
        leftPart = parts[0].trim();
        rightPart = parts[1].trim();
    }

    // If no comparison operator is found, treat the expression as a math expression
    if (operator == null) {
        leftPart = expression;
        rightPart = null;
    }

    // evaluate the mathematical expressions in both parts
    int leftResult = evaluateMath(leftPart);
    int rightResult = (rightPart != null) ? evaluateMath(rightPart) : 0; // handle right part if exists

    // if no comparison operator, return the result of the math expression
    if (operator == null) {
        return leftResult;
    }

    // compare the results based on the operator
    return switch (operator) {
        case "==" -> leftResult == rightResult;
        case "!=" -> leftResult != rightResult;
        case ">=" -> leftResult >= rightResult;
        case "<=" -> leftResult <= rightResult;
        case "<" -> leftResult < rightResult;
        case ">" -> leftResult > rightResult;
        default -> throw new IllegalArgumentException("Unknown comparison operator: " + operator);
    };
}

private int evaluateMath(String part) {
    String[] terms = part.split("(?=[-+*/%])|(?<=[-+*/%])"); // split expression into terms by operators
    List<String> termList = new ArrayList<>(Arrays.asList(terms));

    // Handle exponentiation **
    for (int i = 0; i < termList.size(); i++) {
        if (termList.get(i).equals("*") && i + 1 < termList.size() && termList.get(i + 1).equals("*")) {
            int leftOperand = parseOperand(termList.get(i - 1)); // get left operand
            int rightOperand = parseOperand(termList.get(i + 2)); // get right operand
            int result = (int) Math.pow(leftOperand, rightOperand); // calculate exponentiation
            termList.set(i - 1, String.valueOf(result)); // store result
            termList.remove(i); // remove first '*'
            termList.remove(i); // remove second '*'
            termList.remove(i); // remove right operand
            i--; // adjust index
        }
    }

    // Handle * / % operations
    for (int i = 0; i < termList.size(); i++) {
        String term = termList.get(i);
        if ("*/%".contains(term)) { // check for multiplication, division, or modulus
            int leftOperand = parseOperand(termList.get(i - 1)); // get left operand
            int rightOperand = parseOperand(termList.get(i + 1)); // get right operand
            int result;

            switch (term) {
                case "*" -> result = leftOperand * rightOperand; // multiplication
                case "/" -> { // division (check for division by zero)
                    if (rightOperand != 0) {
                        result = leftOperand / rightOperand;
                    } else {
                        throw new ArithmeticException("Division by zero.");
                    }
                }
                case "%" -> result = leftOperand % rightOperand; // modulus
                default -> throw new IllegalStateException("Unexpected operator: " + term);
            }

            termList.set(i - 1, String.valueOf(result)); // store result
            termList.remove(i); // remove operator
            termList.remove(i); // remove right operand
            i--; // adjust index
        }
    }

    // Handle + and - operations
    int result = parseOperand(termList.get(0)); // initialize result with the first operand
    for (int i = 1; i < termList.size(); i += 2) { // loop through remaining terms
        String operator = termList.get(i); // operator
        int operand = parseOperand(termList.get(i + 1)); // operand

        if (operator.equals("+")) {
            result += operand; // addition
        } else if (operator.equals("-")) {
            result -= operand; // subtraction
        }
    }

    return result; // return final result of the expression
}

private int parseOperand(String term) {
    if (variables.containsKey(term)) {
        return variables.get(term); // get variable value if it's already stored
    } else {
        try {
            return Integer.parseInt(term); // try to parse operand as an integer
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format: " + term); // handle invalid number format
        }
    }
}

private void handlePrint(String line) {
    String expr;

    if (line.contains("(") && line.contains(")")) { // check for parentheses
        expr = line.substring(line.indexOf('(') + 1, line.indexOf(')')).trim(); // extract expression inside parentheses
        if (expr.isEmpty()) {
            System.out.println(); // print empty line if expression is empty
            return;
        }
    } else {
        expr = line.substring(line.indexOf(' ') + 1).trim(); // extract expression after 'print' or 'puts'
    }

    if (expr.startsWith("\"") && expr.endsWith("\"")) { // handle string expressions
        System.out.println(expr.substring(1, expr.length() - 1)); // print string value without quotes
    } else if (expr.equals("true") || expr.equals("false")) { // handle boolean expressions
        System.out.println(expr); // print boolean value
    } else if (boolvar.containsKey(expr)) { // check if it's a boolean variable
        Boolean varValue = boolvar.get(expr);
        System.out.println(varValue); // print boolean value
    } else if (variables.containsKey(expr)) { // check if it's a numeric variable
        Integer varValue = variables.get(expr);
        if (varValue != null) {
            System.out.println(varValue); // print numeric value
        }
    } else if (expr.contains("||") || expr.contains("&&")) { // check if it's a string variable
        System.out.println(evaluateCondition(expr)); // print string value
    }
    else if (stringvar.containsKey(expr)) { // check if it's a string variable
        System.out.println(stringvar.get(expr)); // print string value
    } else {
        Object result = evaluateSimpleExpression(expr); // evaluate and print expression
        System.out.println(result);
    }
}

    public static void main(String[] args) {
        MinimalInterpreter interpreter = new MinimalInterpreter();

        String fib= """
            n = 10
            fib = 0
            fiba = 1
            count = 0 

            while count < n
              fib = fiba + fib 
              fiba = fib - fiba 
              count = count + 1  
            end

            puts fiba
        """;

        String multable= """
            n = 10
            k = 1
            while k <= 10
                puts k*n
                k = k+1
            end
        """;

        String digitsum= """
            n=451
            sum = 0
            while n > 0
                sum = sum + n % 10
                n = n / 10
            end
            print sum
        """;

        String largestdig= """
            n = 320937
            res = 0

            while n > 0
                digit = n % 10
                if digit > res
                    res = digit
                end
                n = n / 10
            end

            print res
        """;

   String palindrome= """
        n = 121
        original = n
        reversed = 0
        while n > 0
            reversed = reversed * 10 + n % 10
            n = n / 10
        end

        if original == reversed 
            puts "true"
        else
            puts "false"
        end
       """;

      String isprime= """
     n=7
     i=2
     res = true
     if n<=1
     res= false
     end
     while i>=2 && i<n 
       if n % i == 0
         res = false
         i = n
       end
        i = i + 1
     end
     
     puts res
              """  ;

    String reverse = """

     number = 1234
       digit = 0 
       reversed = 0 
      while number != 0 
       digit = number % 10 
       reversed = reversed * 10 + digit 
       number = number/10 
      end 
      
      puts reversed
        """;

      String gcd = """ 
       a = 48
       b = 18
       temp = 0
        while b != 0
            temp = b
            b = a % b
            a = temp
        end
       puts a
      """;


        String fac = """
          n = 5
          fac = 1
         while n > 1
          fac = n * fac
          n = n - 1
         end
         puts fac
          """;


     String sum = """ 
            n = 5
          sum = 0
         while n>0
          sum = sum + n
          n = n - 1
         end
         puts sum
          """;

     String prog = """
  
  """;
        interpreter.eval2(sum);
    }
}