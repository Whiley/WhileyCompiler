define pos as int where $ > 0
define neg as int where $ < 0

neg negate(pos x):
    return -x

pos negate(neg x):
    return -x


void System::main([string] args):
    negate(0)
