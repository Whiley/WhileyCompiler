define ClassFile as {
    int minor_version,
    int major_version,
    {ClassModifier} modifiers,
    class_t type, 
    class_t superClass    
}

define PUBLIC as 1
define FINAL as 2
define SUPER as 3
define INTERFACE as 4
define ABSTRACT as 5

define ClassModifier as {
    PUBLIC,
    FINAL,
    SUPER,
    INTERFACE,
    ABSTRACT
}
