import whiley.lang.System

function f(int x) => int:
    return x * 12

function g(function(int)=>real func) => real:
    return func(1)

method main(System.Console sys) => void:
    sys.out.println(Any.toString(g((function(int)=>real) &f)))
