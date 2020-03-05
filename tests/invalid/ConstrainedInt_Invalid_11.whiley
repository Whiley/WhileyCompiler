type nat is (int x) where x >= 0
type pos is (int x) where x > 0

function f(nat y) :
    debug "F(NAT)"

function f(pos x) :
    debug "F(INT)"

public export method test() :
    f(-1)
    f(1)
