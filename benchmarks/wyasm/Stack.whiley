// =========== Stack Calculation ==============
int stackDiff_store(STORE store) ensures $==1 || $==2:
    jvmType t = jvmType(store.op)
    return -slotSize(t)

int stackDiff_load(LOAD load) ensures $==1 || $==2:
    return slotSize(jvmType(load.op))

int stackDiff_binop(BINOP store) ensures $==1 || $==2:
    return -slotSize(jvmType(store.op))

