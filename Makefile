XIC_BUILD=./xic-build
XTH_BIN=./xth/xth
XTH_BUILD_TEST_DIR=tests
XTH_VERBOSITY_LEVEL=2
XTH_TEST_DIRS=xth/tests/pa1 tests/pa1-fix-tests xth/tests/pa2 tests/pa2-staff-extracted-tests xth/tests/pa3 tests/pa3-tests tests/pa3-staff-examples-tests
GRADLE_SETUP_FILES=build.gradle settings.gradle gradlew make_jar_executable.sh gradle

.DEFAULT_GOAL := help

# Taken from https://marmelab.com/blog/2016/02/29/auto-documented-makefile.html
help:
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'

.PHONY: build
build:	## Build xic
	$(XIC_BUILD)

test: test-xth-build $(XTH_TEST_DIRS)	## Run xth quietly on all test directories

test-xth-build:	## Run xth quietly on xic builder
	$(eval SCRIPT:=$(addsuffix /xthScriptBuild, $(XTH_BUILD_TEST_DIR)))
	$(XTH_BIN) -v $(XTH_VERBOSITY_LEVEL) -testpath $(XTH_BUILD_TEST_DIR) $(SCRIPT)

.PHONY: $(XTH_TEST_DIRS)
$(XTH_TEST_DIRS):
	$(eval SCRIPT:=$(addsuffix /xthScript, $@))
	$(XTH_BIN) -v $(XTH_VERBOSITY_LEVEL) -testpath $@ $(SCRIPT)

test-verbose: XTH_VERBOSITY_LEVEL = 4	## Run xth on all test directories
test-verbose: test

test-custom:	## Run xth on a specific directory given by TESTPATH and with optional flags given by ARGS
	$(eval SCRIPT:=$(addsuffix /xthScript, $(TESTPATH)))
	$(XTH_BIN) $(ARGS) -testpath $(TESTPATH) $(SCRIPT)

zip: clean	## Zip Xi Compiler source files into xic.zip
	zip -r xic.zip $(GRADLE_SETUP_FILES) Makefile lib src xic-build xic tests/pa1-fix-tests -x *.results*

clean:	## Clean temporary build files from the directory
	rm -rf xic.zip build .gradle ~/bin/xic
	rm -f src/main/java/lexer/XiLexer.java*
	rm -f src/main/java/xi_parser/XiParser.java* 
	rm -f src/main/java/xi_parser/IxiParser.java* 
	rm -f src/main/java/xi_parser/sym.java* 
	find . -name "*.lexed" -type f -delete
	find . -name "*.parsed" -type f -delete
	find . -name "*.typed" -type f -delete
