define int where $ >= 0 as anat
define int where 2*$ >= $ as bnat
define int where $ > 0 as posint
define int where $ > 0 as dummy

void System::main([string] args):
    anat a = 3
    bnat b = 4
    posint c = 1
    dummy d = 2
    
    c = d      // posint :> dummy 
    d = c      // posint <: dummy 
    a = c      // anat :> posint
    a = b      // anat :> bnat
    b = a      // bnat :> anat
    
    print str(a)
    