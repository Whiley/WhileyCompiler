type SupertypeRecord is {int a, ...}
type SubtypeRecord is {int a, int b, ...}

public export method test():
    SupertypeRecord x = (SupertypeRecord) {a:42, b:1337}
    if x is SubtypeRecord:
        int z = x.b
        assume z == 1337
