import whiley.lang.System

function f({int=>any} xs) => int:
    if xs is {int=>string}:
        return 1
    else:
        return -1

method main(System.Console sys) => void:
    {int=>any} s1 = {0=>"Hello"}
    {int=>any} s2 = {1=>"Hello"}
    {int=>any} s3 = {0=>"Hello", 1=>"Hello"}
    {int=>any} s4 = {0=>"Hello", 1=>"Hello", 3=>"Hello"}
    sys.out.println(Any.toString(f(s1)))
    sys.out.println(Any.toString(f(s2)))
    sys.out.println(Any.toString(f(s3)))
    sys.out.println(Any.toString(f(s4)))
    {int=>int} t1 = {0=>0, 1=>1}
    sys.out.println(Any.toString(f(t1)))
