int var = -1

method inc()
requires var >= 0
ensures old(var) < var:
    var = var + 1

public export method test():
    inc()
