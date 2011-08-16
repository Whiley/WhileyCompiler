// replace all occurrences of "old" with "new" in string "str".  TO BE
// DEPRECATED
public bool has(char c1, string str):
    for c2 in str:
        if c1 == c2:
            return true
    return false

void System::main([string] args):
    s = "Hello World"
    this.out.println(str(has('l',s)))
    this.out.println(str(has('e',s)))
    this.out.println(str(has('h',s)))
    this.out.println(str(has('z',s)))
    this.out.println(str(has('H',s)))
