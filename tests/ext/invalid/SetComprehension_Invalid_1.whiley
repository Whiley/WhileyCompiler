import whiley.lang.*:*

{int} pred({int} xs) ensures no { z in $ | z < 0 }:
    zs = { z | z in xs, z < 0 }
    return zs

void System::main([string] args):
    pred({-1,0,1})
