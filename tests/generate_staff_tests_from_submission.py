import argparse
import os

parser = argparse.ArgumentParser(
    description='Generate staff tests from the staff submission file.'
)
parser.add_argument('--submission-file', metavar='FILE', type=str,
                    required=True,
                    help='path of the pa.-submission-results.md file')
parser.add_argument('--destdir', metavar='D', type=str,
                    required=True,
                    help='path of the destination directory, will be created'
                    ' if non-existent')
parser.add_argument('--sol-extension', metavar='EXT', type=str,
                    required=True,
                    help='extension of the file that would contain staff\'s'
                    'expected solutions')

args = parser.parse_args()

def remove_extension(path):
    '''
    Removes the extension from the filename. Does not throw an error if the
    filename doesn't have an extension. Filenames with leading periods such as
    `.bashrc` don't have extensions, and so are returned as is.

    Args:
        path (str): path to the filename with or without an extension.

    Returns:
        (str): file name.
    '''
    fname, ext = os.path.splitext(path)
    return fname

def parse_error_test_case(list_details):
    '''
    Parses the test case extracted as a list of lines in list_details and writes
    to the required file.

    Args:
        list_details (str list): Lines between --- and --- in the submission
        file given by the staff.

    Returns:
        (str): test case file name.
    '''
    # Line with the name of the test case is like this:
    #   # xic-ref ...: fname
    first_line = list_details[0]
    test_type = first_line[ first_line.rfind('[')+1 : first_line.rfind(']')]
    test_fname = test_type.replace(' ', '-') + \
            '-' + first_line[ first_line.rfind(': ')+2 :]
    if args.sol_extension[0] != '.':
        args.sol_extension = '.' + args.sol_extension
    sol_test_fname = remove_extension(test_fname) + args.sol_extension

    start_test_case = False
    record_test_case = False
    start_expected_case = False
    record_expected_case = False

    test_case = []
    expected_case = []

    for line in list_details[1:]:
        if record_test_case and line == '```':
            record_test_case = False
            start_test_case = False
        elif record_test_case:
            test_case.append(line)
        elif line == '```' and start_test_case:
            record_test_case = True
        elif line.startswith('## Content'):
            start_test_case = True

        elif record_expected_case and line == '```':
            record_expected_case = False
            start_expected_case = False
        elif record_expected_case:
            expected_case.append(line)
        elif line == '```' and start_expected_case:
            record_expected_case = True
        elif line.startswith('## Expected'):
            start_expected_case = True

    with open(os.path.join(args.destdir, test_fname), 'w') as f:
        for line in test_case:
            f.write(line + '\n')

    with open(os.path.join(args.destdir, sol_test_fname), 'w') as f:
        for line in expected_case:
            f.write(line + '\n')

    return test_fname


fname_created_tests = []

with open(args.submission_file, 'r') as sub_f:
    if not os.path.exists(args.destdir):
        # Create destination directory
        os.makedirs(args.destdir, mode=0755)

    in_error_test_case = False
    record_test_case = []

    for line in sub_f.read().splitlines():
        if in_error_test_case and line == '---':
            # Need to change the flag
            in_error_test_case = False
            fname_created_tests.append(
                parse_error_test_case(record_test_case)
            )
            record_test_case = []

        elif in_error_test_case:
            record_test_case.append(line)

        elif not in_error_test_case and line == '---':
            # Need to change the flag and proceed to test case parsing hereafter
            in_error_test_case = True

# Write the files to a xthScript
with open(os.path.join(args.destdir, 'xthScript'), 'w') as f:
    for fname in sorted(fname_created_tests):
        f.write(fname + ';\n')
