import println from whiley.lang.System

type nat is int where $ >= 0

function f(string str, int i) => char:
    return str[i]
