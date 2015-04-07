
type frf1nat is int x where x >= 0

function f(frf1nat y) -> void:
    debug "F(NAT)"

function f(int x) -> void:
    debug "F(INT)"

method main() -> void:
    f(-1)
    f(1)
