type string is int[]
type item is (int d) where (0 <= d) && (d < 7)

string[] Days = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]

function inc(item i) -> item:
    return (i + 1) % 7

method get(item day, int count) -> int[]:
    if count > 0:
        return get(inc(day), count - 1)
    else:
        return Days[day]

public export method test() :
    int[] result
    result = get(0,0)
    assume result == "Monday"
    //
    result = get(0,1)
    assume result == "Tuesday"
    //
    result = get(0,2)
    assume result == "Wednesday"
    //
    result = get(0,3)
    assume result == "Thursday"
    //
    result = get(0,4)
    assume result == "Friday"
    //
    result = get(0,5)
    assume result == "Saturday"
    //
    result = get(0,6)
    assume result == "Sunday"
    //
    result = get(0,7)
    assume result == "Monday"
    //
    result = get(0,8)
    assume result == "Tuesday"
    //
    result = get(0,9)
    assume result == "Wednesday"
    //
    result = get(0,10)
    assume result == "Thursday"
    //
    result = get(0,11)
    assume result == "Friday"
    //
    result = get(0,12)
    assume result == "Saturday"
    //
    result = get(0,13)
    assume result == "Sunday"
    //
    result = get(0,14)
    assume result == "Monday"
    //
    result = get(0,15)
    assume result == "Tuesday"
