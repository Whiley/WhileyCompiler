

constant Days is ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]

type item is (int d) where (0 <= d) && (d < 7)

function inc(item i) -> item:
    return (i + 1) % 7

method get(item day, int count) -> int[]:
    if count > 0:
        return get(inc(day), count - 1)
    else:
        return Days[day]

public export method test() :
    assume get(0, 0) == "Monday"
    assume get(0, 1) == "Tuesday"
    assume get(0, 2) == "Wednesday"
    assume get(0, 3) == "Thursday"
    assume get(0, 4) == "Friday"
    assume get(0, 5) == "Saturday"
    assume get(0, 6) == "Sunday"
    assume get(0, 7) == "Monday"
    assume get(0, 8) == "Tuesday"
    assume get(0, 9) == "Wednesday"
    assume get(0, 10) == "Thursday"
    assume get(0, 11) == "Friday"
    assume get(0, 12) == "Saturday"
    assume get(0, 13) == "Sunday"
    assume get(0, 14) == "Monday"
    assume get(0, 15) == "Tuesday"
