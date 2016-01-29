
type frf1nat is (int x) where x >= 0

function f(frf1nat y) :
    debug "F(NAT)"

function f(int x) :
    debug "F(INT)"

method main() :
    f(-1)
    f(1)
