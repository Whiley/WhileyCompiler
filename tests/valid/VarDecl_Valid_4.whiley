

function constantPool() -> int:
    return 12478623847120981

public export method test() -> void:
    int pool = constantPool()
    assume pool == 12478623847120981
