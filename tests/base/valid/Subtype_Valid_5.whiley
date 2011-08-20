import whiley.lang.*:*

define sr5nat as int

void System::main([string] args):
    x = {f:1}
    x.f = 2
    this.out.println(str(x))
    
