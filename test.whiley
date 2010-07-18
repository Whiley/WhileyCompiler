define nat as int where $ >= 0
define onion as [int]|nat

void f(onion x):
    if x ~= [int]:
        print "GOT LIST"
    else if x ~= nat:
        print "GOT NAT"

void System::main([string] args):
    f([1,2,3])
    f(1)
