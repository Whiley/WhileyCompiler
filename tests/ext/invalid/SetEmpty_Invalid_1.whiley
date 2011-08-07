void f({int} xs) requires xs != ∅:
    debug str(xs)

void System::main([string] args):
    f({1,4})
    f({})
