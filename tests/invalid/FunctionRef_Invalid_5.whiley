type Proc is &{int data}

method read(Proc this, int x) -> int:
    return x + 1

type Func is {
    function reader(int)->int
}

function id(int x) -> int:
    return x

function test(Func f, int arg) -> int:
    return f.read(arg)
