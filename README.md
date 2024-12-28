# FOPproject
My assignment was to write a ruby>java interpreter.
Since I am doing this project on my own, I have taken on all the group roles, so I won't mention them.
My interpreter is quite easy to use, you just have to create an instance of the class MinimalInterpreter and then call on the eval method of the minimalinterpreter object and pass a Ruby code (as string) as an argument, like so:

MinimalInterpreter interpreter = new MinimalInterpreter();
String prog = """
           puts "Hello World"
              """;
interpreter.eval(prog);

Here are some of the things my code can do:
   * It can print thing with puts, print, puts(...) and print(...) 
   * It can store variables and overwrite them (x=3  puts x (output:3)  x="hi" puts x(output:hi) x=true ... )
   * It can perform arithmetic operations, both simple(4+2) and complicated (3+5*3**2)
   * It can understand > < = != >= <= == && || operations
   * It implements If else blocks
       if x>5 || x==3
            puts "nice"
         else
            puts 0
         end
   * It implements while loops
   * Basic error handling

Algorithm tests:

Sum of first n numbersGCD

  String prog = """
          n = 5
          sum = 0
         while n>0
          sum = sum + n
          n = n - 1
         end
         puts sum
          """;

Factorial of n

String prog = """
          n = 5
          fac = 1
         while n > 1
          fac = n * fac
          n = n - 1
         end
         puts fac
         """;

GCD

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
          
Sum of digits

String prog = """
n=45
sum = 0
  while n > 0
    sum = sum + n % 10
    n = n / 10
  end
  print sum
end
""";

Multiplication table

 String prog = """
          n = 5
          k = 1
         while k <= 10
          puts k*n
          k = k+1
         end
          """;

n-th fib num

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

""";


    
