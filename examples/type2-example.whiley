define anat as int where $ >= 0
define bnat as int where 2*$ >= $
define posint as int where $ > 0
define dummy as int where $ > 0

posint f1(dummy x):
    return x // dummy <: posint

dummy f2(posint x):
    return x // posint <: dummy 

anat f3(bnat x):
    return x // bnat <: anat

bnat f4(anat x):
    return x // anat <: bnat

void System::main([string] args):
    y = 3
    
    f1(f2(y))    
    f3(f4(y))    
    
    print str(y)
    
