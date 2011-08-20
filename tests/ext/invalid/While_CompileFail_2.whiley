import whiley.lang.*:*

void System::main([string] args):
    i=0
    r=0
    while i < |args| where j > 0:
        r = r + |args[i]|
    debug str(r)
