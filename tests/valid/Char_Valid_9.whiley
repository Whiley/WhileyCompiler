import whiley.lang.System

method main(System.Console console):
    [int] list = ((int)'a') .. ((int)'z')
    [char] chars = ([char]) list
    console.out.println(chars)