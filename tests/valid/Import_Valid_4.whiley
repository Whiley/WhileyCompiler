
function f(int x) -> int[]:
    return toString(x)

import toString from whiley.lang.Real

function g(real x) -> int[]:
    return toString(x)

public export method test() :
    assume f(1) == "1"
    assume g(1.2344) == "1.2344"
