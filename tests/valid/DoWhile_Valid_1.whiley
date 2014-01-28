import whiley.lang.System

method f(System.Console sys, [int] args) => void:
    int i = 0
    do:
        i = i + 1
        sys.out.println(args[i])
    while (i + 1) < |args|

method main(System.Console sys) => void:
    f(sys, [1, 2, 3])
    f(sys, [1, 2])
    f(sys, [1, 2, 3, 4, 5, 6])
