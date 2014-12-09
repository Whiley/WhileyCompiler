type Link is {
    int data,
    LinkedList next
}

type LinkedList is null | Link

function length(LinkedList ls) -> (int r)
// Return is non-negative
ensures r >= 0
// Return is positive then ls non-empty
ensures r > 0 <==> ls is Link:
    //
    if ls == null:
        return 0
    else:
        return 1 + length(ls.next)

function get(Link ls, int i) -> int
// Index i is within bounds
requires i >= 0 && i < length(ls):
    //
    if i == 0:
        return ls.data
    else:
        assert ls.next is Link
        return get(ls.next,i+1)



