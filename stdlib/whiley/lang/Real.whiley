package whiley.lang

import * from whiley.lang.Errors

// Convert a string into an integer
public real parse(string input) throws SyntaxError:
    r = 0
    dps = 0
    for i in 0..|input|:
        c = input[i]
        if c == '.' && dps == 0:
            dps = 1
        else if !Char.isDigit(c):
            throw SyntaxError("invalid number string",i,i)
        else:
            r = r * 10
            r = r + (c - '0')
            dps = dps * 10
    // finally, perform division
    r = (real) r
    if dps > 0:
        return r / dps
    else:
        return r

