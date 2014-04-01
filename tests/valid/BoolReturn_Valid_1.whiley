import whiley.lang.System

function pred() => bool:
    return false

method main(System.Console sys) => void:
    sys.out.println(pred())
