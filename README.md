# Xi Compiler (xic)

A compiler for the [Xi language](https://www.cs.cornell.edu/courses/cs4120/2019sp/) and its object-oriented extension Xi++, built by [Anmol Kabra](https://github.com/anmolkabra), [Cassandra Scarpa](https://github.com/cassandrascarpa), [Danny Yang](https://github.com/yangdanny97), [Kyrylo Chernyshov](https://github.com/BlueFire2).

## Installation

xic has been tested on Linux-64 environments.
A good place to get all requirements is to download the CS 4120 VM and the clone this repo inside.
If you don't want to use the VM, you should be able to build the compiler with only Java 11.

## Building

The Makefile provides several `make` commands for building, testing ("xth" is short for "xic test harness"), and running the compiler.
Running just `make` also prints this output:

| **Command**           | **Action**                                                                              |
| --------------------- | --------------------------------------------------------------------------------------- |
| `make build`          | Build xic                                                                               |
| `make clean`          | Clean temporary build files from the directory                                          |
| `make test-custom`    | Run xth on a specific directory given by TESTPATH and with optional flags given by ARGS |
| `make test`           | Run unit tests and xth quietly on all test directories                                  |
| `make test-unit`      | Run unit tests in the project                                                           |
| `make test-verbose`   | Run xth on all test directories                                                         |
| `make test-xth-build` | Run xth quietly on xic builder                                                          |
| `make zip`            | Zip Xi Compiler source files into xic.zip                                               |

## Usage

```
xic --flags a/b.xi
```

## Flags

| **Flag**                    | **Description**                                                                               | **Output**                                            |
| --------------------------- | --------------------------------------------------------------------------------------------- | ----------------------------------------------------- |
| _Miscellaneous_             |                                                                                               |                                                       |
| `-h`, `--help`              | Print a synopsis of options.                                                                  |                                                       |
| `--report-opts`             | Output the allowed compiler optimizations.                                                    |                                                       |
| _Compiler Phases_           |                                                                                               |                                                       |
| `-l`, `--lex`               | Generate output from lexical analysis.                                                        | `<src_path>/a/b.xi --> <diag_path>/b.lexed`           |
| `--parse`                   | Generate output from syntactic analysis.                                                      | `<src_path>/a/b.xi --> <diag_path>/b.parsed`          |
| `--typecheck`               | Generate output from semantic analysis.                                                       | `<src_path>/a/b.xi --> <diag_path>/b.typed`           |
| `--irgen`                   | Generate intermediate code.                                                                   | `<src_path>/a/b.xi --> <diag_path>/b.ir`              |
| `--irrun`                   | Generate and interpret intermediate code.                                                     | `<src_path>/a/b.xi --> <diag_path>/b.ir.nml`          |
| `--optir [initial|final]`:  | Report the intermediate code for each function `f` at the specified `phase` of optimization.  | `<src_path>/a/b.xi --> <diag_path>/b_<f>_<phase>.ir`  |
| `--optcfg [initial|final]`: | Report the control-flow graph for each function `f` at the specified `phase` of optimization. | `<src_path>/a/b.xi --> <diag_path>/b_<f>_<phase>.dot` |
| `-target <OS>`              | Specify the operating system for which to generate code. Supported OS: linux.                 | `<src_path>/a/b.xi --> <asm_path>/b.s`                |
| _Options_                   |                                                                                               |                                                       |
| `-sourcepath <src_path>`    | Specify where to find input source files. (default: `.`)                                      |                                                       |
| `-libpath <lib_path>`       | Specify where to find input library/interface files. (default: `.`)                           |                                                       |
| `-D <diag_path>`            | Specify where to place generated diagnostic files. (default: `.`)                             |                                                       |
| `-d <asm_path>`             | Specify where to place generated assembly files. (default: `.`)                               |                                                       |
| _Optimizations_             |                                                                                               |                                                       |
| `-O`                        | Disable optimizations.                                                                        |                                                       |
| `-O<opt>`                   | Enable optimization `opt`. Other optimizations are disabled, unless otherwise enabled.        |                                                       |
| `-O-no-<opt>`               | Disable only optimization `opt`. Others are enabled, unless otherwise disabled.               |                                                       |

**Note:** If none of the optimization flags are provided, all optimizations are enabled by default.

## Optimizations

| **Abbreviation** | **For Optimizations(s)**                                  |
| ---------------- | --------------------------------------------------------- |
| `reg`            | Register Allocation using graph coloring, move coalescing |
| `cse`            | Common Subexpression Elimination                          |
| `cf`             | Constant Folding                                          |
| `mc`             | Register Allocation using graph coloring, move coalescing |
| `copy`           | Copy Propagation                                          |
| `dce`            | Dead Code Elimination                                     |

## Acknowledgements
- [CS 4120](https://www.cs.cornell.edu/courses/cs4120/)
- [Xi Language Specification](https://www.cs.cornell.edu/courses/cs4120/2019sp/project/language.pdf)
- [Xi++ Language Specification](http://www.cs.cornell.edu/courses/cs4120/2019sp/project/oolang.pdf)
