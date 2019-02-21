XIC_BUILD=./xic-build
XTH_BIN=./xth/xth
XTH_BUILD_TEST_DIR=tests
XTH_TEST_DIRS=xth/tests/pa1 tests/pa1-fix-tests xth/tests/pa2 xth/tests/pa3
GRADLE_SETUP_FILES=build.gradle settings.gradle gradlew make_jar_executable.sh gradle

RED_COLOR='\033[0;31m'
NO_COLOR='\033[0m'

default: build

.PHONY: build
build:
	$(XIC_BUILD)

test: test-xth-build $(XTH_TEST_DIRS)

test-xth-build:
	$(eval SCRIPT:=$(addsuffix /xthScriptBuild, $(XTH_BUILD_TEST_DIR)))
	echo -e "$(RED_COLOR)xth on $(SCRIPT) $(NO_COLOR)"
	$(XTH_BIN) -testpath $(XTH_BUILD_TEST_DIR) $(SCRIPT)

.PHONY: $(XTH_TEST_DIRS)
$(XTH_TEST_DIRS):
	$(eval SCRIPT:=$(addsuffix /xthScript, $@))
	echo -e "$(RED_COLOR) xth on $(SCRIPT) $(NO_COLOR)"
	$(XTH_BIN) -testpath $@ $(SCRIPT)

zip: clean
	zip -r xic.zip $(GRADLE_SETUP_FILES) Makefile lib src xic-build xic tests/pa1-fix-tests -x *.results*

clean:
	rm -rf xic.zip build .gradle ~/bin/xic
	rm -f src/main/java/lexer/XiLexer.java*
	rm -f src/main/java/xi_parser/XiParser.java* 
	rm -f src/main/java/xi_parser/sym.java* 
	find . -name "*.lexed" -type f -delete
	find . -name "*.parsed" -type f -delete
