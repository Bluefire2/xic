package kc875.cli;

enum OptimPhases {
    INITIAL,
    FINAL,

    // Analyses
    ASMLIVEVAR,
    ASMAVAILCOPY,

    // CFG after a stage
    ASMAFTERCOPY,
    ASMAFTERDCE,
}
