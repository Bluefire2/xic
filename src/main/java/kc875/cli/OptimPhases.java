package kc875.cli;

enum OptimPhases {
    INITIAL,
    FINAL,

    // Analyses
    IRAVAILEXPR,
    IRAVAILCOPY,
    IRLIVEVAR,
    ASMLIVEVAR,
    ASMAVAILCOPY,

    // CFG after a stage
    ASMAFTERCOPY,
    ASMAFTERDCE,
}
