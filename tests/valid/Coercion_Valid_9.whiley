import whiley.lang.System

type Link is {int data}
type BigLink is {int data, int code}

function sum(Link l) -> int:
    return l.data

function sum2(BigLink l) -> int:
  return sum((Link) l)

method main(System.Console console):
    BigLink l = {data: 1, code: 'c'}
    console.out.println(sum2(l))
