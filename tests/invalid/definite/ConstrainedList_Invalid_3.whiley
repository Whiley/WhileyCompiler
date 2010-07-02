public [int] update(string str):
    return [-1]

public void f(char c):
    print [c]

public void System::main([string] args):
    string s1 = "Hello World"
    s1 = update(s1)
    if |s1| > 0:
        f(s1[0])