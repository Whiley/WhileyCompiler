type neg is (int x) where x < 0
//
type structA is { bool r }
type structB is { neg|bool r }
//
function f(structA x) -> (structB y):
    return x

public export method test():
    structA x = { r: false }
    //
    assume f(x) == x