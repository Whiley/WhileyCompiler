

function constantPool() -> int:
    return 12478623847120981

public export method test() :
    int pool = constantPool()
    assume pool == 12478623847120981
