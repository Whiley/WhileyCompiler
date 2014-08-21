package wycs.solver.smt;

/**
 * Roughly speaking, a logic is a collection of theorems that are required to run the {@link
 * wycs.solver.smt.Smt2File}. For details on a specific logic, see {@linktourl
 * http://smtlib.cs.uiowa.edu/logics.shtml}.
 *
 * @author Henry J. Wylde
 */
public enum Logic {

    AUFLIA, AUFLIRA, AUFNIRA, LRA, QF_ABV, QF_AUFBV, QF_AUFLIA, QF_AX, QF_BV, QF_IDL, QF_LIA,
    QF_LRA, QF_NIA, QF_NRA, QF_RDL, QF_UF, QF_UFBV, QF_UFIDL, QF_UFLIA, QF_UFLRA, QF_UFNRA, UFLRA,
    UFNIA;
}
