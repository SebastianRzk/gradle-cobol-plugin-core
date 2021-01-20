set -e

git clone https://github.com/SebastianRzk/gradle-cobol-plugin-example
cd gradle-cobol-plugin-example
sh ci/run_core_dev_test.sh
cd ..
