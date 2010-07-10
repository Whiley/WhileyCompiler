define state as (string input, int pos) where pos >= 0 && pos <= |input|

state parseWhiteSpace(state st):
    return st

state parseTerm(state st):
    st = parseWhiteSpace(st)
    return st
