import whiley.lang.System

method main(System.Console console):
    [int] list = ((int)'a') .. ((int)'z')
    console.out.println(list)