import java.util.HashMap;
import java.util.Map;
import java.util.*;

public class MinimalInterpreter {
    private final Map<String, Integer> variables = new HashMap<>();
    private final Map<String, Boolean> boolvar = new HashMap<>();
    private final Map<String, String> stringvar = new HashMap<>();

    class Block {
        public int ind;
        public String content;

        public Block(String content, int ind) {
            this.ind = ind;
            this.content = content;
        }
    }


    public Block getBlock(String[] lines, int ind) {

        StringBuilder sb = new StringBuilder();
        int count = 1;
        while (ind < lines.length) {
            String line = lines[ind].trim();
            if (line.startsWith("if") || line.startsWith("while")) {
                count++;
            } else if (line.startsWith("end")) {
                count--;
            }
            if (count == 0) break;
            sb.append(line + "\n");
            ind++;
        }

        if (count != 0) {
        throw new RuntimeException("Syntax Error: Mismatched block delimiters.");
        }
        return new Block(sb.toString(), ind);
    }

    public String getIfElseBlock(String code, boolean condition) {
        Boolean hasElse = false;
        String[] lines = code.split("\n");
        int d = 1;
        int elseInd = -1;

        for (int i=0; i<lines.length; i++) {
            String line = lines[i].trim();
            if (line.startsWith("if")) d++;
            else if (line.startsWith("else")) d--;

            if (d == 0) {
                elseInd = i;
                break;
            }
        }

        StringBuilder ifBlock = new StringBuilder(elseInd == -1? code: "");
        StringBuilder elseBlock = new StringBuilder("");

        int k = 0;
        if (elseInd != -1) while (k != elseInd) ifBlock.append(lines[k++] + "\n");
        if (elseInd != -1) {
            int eI = elseInd + 1;
            while(eI < lines.length) {
                elseBlock.append(lines[eI++]+ "\n");
            }
        }

        return condition ? ifBlock.toString() : elseBlock.toString();
    }

    public void eval2(String code) {
        String[] lines = code.split("\n");
        int i = 0;

        while (i < lines.length) {
            String line = lines[i].trim();

            if (line.startsWith("while")) {
                Block block = getBlock(lines, i + 1);
                String content = block.content;

                i = block.ind;
                String condition = line.substring(5).trim();

                while (evaluateCondition(condition)) {
                    eval2(content);
                }

            } else if (line.startsWith("if")) {
                Block block = getBlock(lines, i + 1);
                String content = block.content;

                i = block.ind;
                String condition = line.substring(2).trim();
                Boolean ifCondition = evaluateCondition(condition);

                String ifElseBlock = getIfElseBlock(content, ifCondition);

                eval2(ifElseBlock);
            } else {
                if (line.startsWith("puts") || line.startsWith("print")) {
                    handlePrint(line); // handle print statements
                } else {
                    evalBlock(line);
                }
            }
            i++;
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

        return (boolean) evaluateSimpleExpression(condition);
    }


    private void handleAssignment(String line) {
        String[] parts = line.split("=");
        if (parts.length < 2) {
        throw new RuntimeException("Syntax Error: Invalid assignment syntax in line - " + line);
    }
        String varName = parts[0].trim();
        String expression = parts[parts.length-1].trim();

         if (varName.isEmpty() || expression.isEmpty()) {
        throw new RuntimeException("Syntax Error: Variable name or expression is missing in assignment.");
    }

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
            Integer value = (Integer) evaluateSimpleExpression(expression);
            variables.put(varName, value);
        }
    }

    private Boolean evaluateBool(String expression) {
        return expression.equals("true");
    }

    private Object evaluateSimpleExpression(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            throw new IllegalArgumentException("Expression cannot be empty or null.");
        }

        expression = expression.replaceAll("\\s", "");
        expression = expression.replaceAll("\\s", "");
        // Check if theres a comparison operator split based on the first operator found
        String operator = null;
        String leftPart = null;
        String rightPart = null;

        // Determine the operator and split the expression
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

        // If no comparison operator found treat the whole expression as a math expression
        if (operator == null) {
            leftPart = expression;
            rightPart = null;
        }

        // evaluate the mathematical expressions in both parts
        int leftResult = evaluateMath(leftPart);
        int rightResult = (rightPart != null) ? evaluateMath(rightPart) : 0; // Right part may be null for non-comparison cases

        // If there is no comparison operator return the result of the mathematical evaluation
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
    // Split the mathematical expression into terms
    String[] terms = part.split("(?=[-+*/%])|(?<=[-+*/%])");
    List<String> termList = new ArrayList<>(Arrays.asList(terms));

    // Handle **
    for (int i = 0; i < termList.size(); i++) {
        if (termList.get(i).equals("*") && i + 1 < termList.size() && termList.get(i + 1).equals("*")) {
            int leftOperand = parseOperand(termList.get(i - 1));
            int rightOperand = parseOperand(termList.get(i + 2));
            int result = (int) Math.pow(leftOperand, rightOperand);
            termList.set(i - 1, String.valueOf(result));
            termList.remove(i); // Remove first '*'
            termList.remove(i); // Remove second '*'
            termList.remove(i); // Remove right operand
            i--; // Adjust index
        }
    }

    // Handle * / %
    for (int i = 0; i < termList.size(); i++) {
        String term = termList.get(i);
        if ("*/%".contains(term)) {
            int leftOperand = parseOperand(termList.get(i - 1));
            int rightOperand = parseOperand(termList.get(i + 1));
            int result;

            switch (term) {
                case "*" -> result = leftOperand * rightOperand;
                case "/" -> {
                    if (rightOperand != 0) {
                        result = leftOperand / rightOperand;
                    } else {
                        throw new ArithmeticException("Division by zero.");
                    }
                }
                case "%" -> result = leftOperand % rightOperand;
                default -> throw new IllegalStateException("Unexpected operator: " + term);
            }

            termList.set(i - 1, String.valueOf(result));
            termList.remove(i); // Remove the operator
            termList.remove(i); // Remove the right operand
            i--; // Adjust index
        }
    }

    // Handle + and -
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
        }else if (expr.equals("true") || expr.equals("false")) {
            System.out.println(expr);
        } else if (boolvar.containsKey(expr)) { // Check if its a boolean
            Boolean varValue = boolvar.get(expr);
            System.out.println(varValue);
        } else if (variables.containsKey(expr)) { // or numeric
            Integer varValue = variables.get(expr);
            if (varValue != null) {
                System.out.println(varValue);
            }
        } else if (stringvar.containsKey(expr)) { //or string
            System.out.println(stringvar.get(expr));
        } else {
            Object result = evaluateSimpleExpression(expr);
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
                n = 5
          k = 1
         while k <= 10
          puts k*n
          k = k+1
         end
                """;

        String digitsum= """
                n=45
sum = 0
  while n > 0
    sum = sum + n % 10
    n = n / 10
  end
  print sum
                """;
        String largestdig= """
                n = 3947
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
     if n<1
     res= false
     end
     if n==1
     res= "neither"
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
         n=5
         while n>0
          puts n
          n=n-1
         end
             """;
           interpreter.eval2(prog);
    }
}