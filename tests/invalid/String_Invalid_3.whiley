import whiley.lang.System

type nat is (int n) where n >= 0

function f(string str, int i) => char:
    return str[i]
