# FOPproject
My assignment was to write a ruby>java interpreter.
Since I am doing this project on my own, I have taken on all the group roles, so I won't mention them.
The MinimalInterpreter is basically a translator engine I built to run basic scripts with things like variable assignments, conditionals, loops and mathematic operations. The whole thing works by parsing the script line by line and figuring out what to do based on the type of command. For variables it has three separate maps to store integers, booleans, and strings. The handleAssignment method takes care of assignments. It splits the line into the variable name and whatever value\expression is being assigned, evaluates it and puts it in the right map based on the type. So for example, if its a number like x = 5 + 3, it evaluates the 5 + 3 part and stores the result in the integer map. 
The conditional stuff (if-else) is handled by a method called getIfElseBlock. It grabs the if block and the optional else block from the script. Then it uses evaluateCondition to check if the if condition is true. If the condition is true, it runs the if block. If it is false and there is an else, it runs that instead. 
Loops, like while, work similarly. The script extracts the loop body using getBlock and evaluates the condition with the same evaluateCondition method. It just keeps running the block over and over as long as the condition stays true.
Expressions are handled by evaluateSimpleExpression. This method takes care of math oprations like addition and subtraction or logical stuff like && and ||. It also makes sure variables in the expression are resolved properly, so you can do things like x + y if both x and y have values.
There’s also a handlePrint method, which does what it sounds like. It takes a print line, evaluates the expression or variable, and spits out the result. 
The main execution loop is eval2. It goes through each line of the script, figures out what type of statement it is (assignment, print, conditional, etc.), and calls the right method to handle it. It just keeps going like that unless there’s a conditional or loop that changes the flow. 
It can catch some errors too, like if you try to divide a number by 0, it will throw an arithmetic exception, or if some invalid comparison or opeator is used (syntax error) it will throw an Illegalargument exception. If while or if-else blocks are'nt closed properly it will throw a runtime exception. When variables aren't assigned correctly like "x=" or when they are used without being assigned (for example, puts x>5 but x isnt saved in the map variables) it will throw exceptions as well. When an expression is empty or null, it obvoiusly doesnt work and throws Illegalargument exception. If you try to add a string or a bool to an int it will throw an exception because you provided an invalid number format.
This is the User Guide:
My interpreter is quite easy to use, you just have to create an instance of the class MinimalInterpreter in the main method and then call on the eval2 method of the minimalinterpreter object and pass a Ruby code (as string) as an argument, like so:

           MinimalInterpreter interpreter = new MinimalInterpreter();
        String prog = """
       puts "Hello World"
              """;
     interpreter.eval2(prog);

Language Subset Specification with examples:

*  Variable assignment
•  Arithmetic operations (+, -, *, /, %, **)
•  Conditional statements (if, else)
•  Iterative control flow (while loop)
•  Basic output operations
*  Syntax error detection

   * It can print thing with puts, print, puts(...) and print(...):
     
         puts true
         print("hey")
     

   * It can store variables and overwrite them:
      
         x=3
         puts x
         x="hi"
         puts x
     

   * It can perform arithmetic operations:
      
         puts 4+3*5
     
   * It can understand > < = != >= <= == && || operations:
     
         n=3
         if n>=2 && n<4
         puts "Hello"
         end
         puts 4-1>=3
     
   * It implements If else blocks:
     
         x=6
         if x>5 || x==3
            puts "nice"
         else
            puts 0
         end
     
   * It implements while loops:
     
         n=5
         while n>0
          puts n
          n=n-1
         end
     
   * Basic error handling
   * Keep in mind that each assignment needs to be on a different line, as you can see in the code file.

Algorithm tests:

-SUM OF FIRST N NUMS-

  String prog = """
  
          n = 5
          sum = 0
         while n>0
          sum = sum + n
          n = n - 1
         end
         puts sum
         
 """;

-FACTORIAL OF N-

String prog = """

          n = 5
          fac = 1
         while n > 1
          fac = n * fac
          n = n - 1
         end
         puts fac
         
""";

-GCD-

 String prog = """ 
 
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
          
-SUM OF DIGITS-

String prog = """

     n=45
    sum = 0
    while n > 0
     sum = sum + n % 10
     n = n / 10
    end
    print sum
   
 """;

-MULTIPLICATION TABLE-

 String prog = """
 
          n = 5
          k = 1
         while k <= 10
          puts k*n
          k = k+1
         end
         
""";

-N-TH FIB NUM-

String prog = """

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

"""
-REVERSE NUM-

String prog = """

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

-PALINDROME-

String prog = """
 
    n = 123321
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
    
"""

-ISPRIME-

String prog = """

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

"""

-LARGEST DIGIT-

String prog= """

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

"""


    
