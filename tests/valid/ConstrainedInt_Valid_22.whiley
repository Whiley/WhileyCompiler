import whiley.lang.*

constant Days is ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]

type item is (int d) where (0 <= d) && (d < 7)

function inc(item i) -> item:
    return (i + 1) % 7

method print(System.Console console, item day, int count) -> void:
    if count > 0:
        console.out.println_s(Days[day])
        print(console, inc(day), count - 1)

public method main(System.Console console) -> void:
    print(console, 0, 15)
