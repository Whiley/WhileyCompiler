define Empty as {int x}
define Actor as process Empty

void Actor::act(int x, System sys):
    sys.out<->println(str(x))

void System::main([string] args):
    ps = spawn {x: 0}
    ps<->act(5, this)
