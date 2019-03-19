XIC_BUILD=./xic-build
XTH_BIN=./xth/xth
XTH_BUILD_TEST_DIR=tests
XTH_VERBOSITY_LEVEL=2

SUBMIT_TEST_DIRS=tests/pa1-fix-tests tests/pa2-staff-extracted-tests tests/pa3-tests tests/pa3-staff-extracted-tests tests/pa4-tests
PA1_OTHER_TEST_DIRS=xth/tests/pa1
PA2_OTHER_TEST_DIRS=xth/tests/pa2
PA3_OTHER_TEST_DIRS=xth/tests/pa3 tests/pa3-staff-examples-tests
PA4_OTHER_TEST_DIRS=xth/tests/pa4
PA5_OTHER_TEST_DIRS=xth/tests/pa5
XTH_TEST_DIRS=$(SUBMIT_TEST_DIRS) $(PA1_OTHER_TEST_DIRS) $(PA2_OTHER_TEST_DIRS) $(PA3_OTHER_TEST_DIRS) $(PA4_OTHER_TEST_DIRS) $(PA5_OTHER_TEST_DIRS)

GRADLE_SETUP_FILES=build.gradle settings.gradle gradlew make_jar_executable.sh gradle gradle_exec.sh

.DEFAULT_GOAL := help

# Taken from https://marmelab.com/blog/2016/02/29/auto-documented-makefile.html
help:
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'

.PHONY: build
build:	## Build xic
	$(XIC_BUILD)

test: test-xth-build $(XTH_TEST_DIRS) test-unit	## Run unit tests and xth quietly on all test directories

test-xth-build:	## Run xth quietly on xic builder
	$(eval SCRIPT:=$(addsuffix /xthScriptBuild, $(XTH_BUILD_TEST_DIR)))
	$(XTH_BIN) -v $(XTH_VERBOSITY_LEVEL) -testpath $(XTH_BUILD_TEST_DIR) $(SCRIPT)

.PHONY: $(XTH_TEST_DIRS)
$(XTH_TEST_DIRS):
	$(eval SCRIPT:=$(addsuffix /xthScript, $@))
	$(XTH_BIN) -v $(XTH_VERBOSITY_LEVEL) -testpath $@ $(SCRIPT)

test-verbose: XTH_VERBOSITY_LEVEL = 4	## Run xth on all test directories
test-verbose: test

test-custom: build	## Run xth on a specific directory given by TESTPATH and with optional flags given by ARGS
	$(eval SCRIPT:=$(addsuffix /xthScript, $(TESTPATH)))
	$(XTH_BIN) $(ARGS) -testpath $(TESTPATH) $(SCRIPT)

test-unit:	## Run unit tests in the project
	./gradle_exec.sh --no-daemon test

zip: clean	## Zip Xi Compiler source files into xic.zip
	# netid of a random group member added to the zip file name
	zip -r xic-kc875.zip $(GRADLE_SETUP_FILES) Makefile lib src xic-build xic $(SUBMIT_TEST_DIRS) -x *.results*

clean:	## Clean temporary build files from the directory
	rm -rf xic.zip build .gradle ~/bin/xic
	rm -f src/main/java/lexer/XiLexer.java*
	rm -f src/main/java/xi_parser/XiParser.java* 
	rm -f src/main/java/xi_parser/IxiParser.java* 
	rm -f src/main/java/xi_parser/sym.java* 
	find . -name "*.lexed" -type f -delete
	find . -name "*.parsed" -type f -delete
	find . -name "*.typed" -type f -delete
	find . -name "*.ir.nml" -type f -delete
	find . -name "*.ir" -type f -delete
