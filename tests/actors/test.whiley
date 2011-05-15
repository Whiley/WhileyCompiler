define Empty as {}
define Actor as process Empty

void System::main([string] args):
    ps = spawn {}
    ps<->act(5)

void Actor::act(int x):
    sys.out<->println(x)
