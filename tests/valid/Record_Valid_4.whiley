type nat is (int x) where x >= 0
//
type structA is { bool r }
type structB is { !nat r } // => { void r } & { bool r }
//
function f(structA x) -> (structB y):
    return x

method test():
    structA x = { r: false }
    //
    assume f(x) == x