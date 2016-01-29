

type anat is int

type bnat is int

function atob(anat x) -> bnat:
    return x

function btoa(bnat x) -> anat:
    return x

public export method test() :
    int x = 1
    assume atob(x) == 1
    assume btoa(x) == 1
