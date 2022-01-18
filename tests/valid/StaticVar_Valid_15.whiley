int var = 0

method inc()
requires var >= 0
ensures old(var) < var:
    var = var + 1

public export method test():
    if var >= 0:
        inc()
        assert var > 0