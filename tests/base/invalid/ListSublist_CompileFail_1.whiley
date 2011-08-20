import whiley.lang.*:*

void System::main([string] args):
    end = 1.2344
    list = [1,2,3]
    sublist = list[-1..end]
    this.out.println(str(list))
    this.out.println(str(sublist))
