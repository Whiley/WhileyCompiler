import whiley.lang.*

type MyProc2 is {any data}

method set(&MyProc2 this, int d) -> void:
    this->data = d

method get(&MyProc2 this) -> any:
    return this->data

method create(any data) -> &MyProc2:
    return new {data: data}

method main(System.Console sys) -> void:
    &MyProc2 p2 = create(1.23)
    set(p2,1)
    sys.out.println(get(p2))
