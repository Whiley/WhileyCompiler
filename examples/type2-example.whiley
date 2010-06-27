define anat as int where $ >= 0
define bnat as int where 2*$ >= $
define posint as int where $ > 0
define dummy as int where $ > 0

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
    