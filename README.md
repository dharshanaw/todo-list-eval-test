# todo-list-eval-test
Evaluation test suite

### System Requirements

Internet Connectivity and ability to connect maven central repository to download dependencies

Apache maven 3.6.0 or higher

Java SE Development Kit 17.0.6 or higher

Git version control support

### Installation and Running

Clone the repository https://github.com/dharshanaw/todo-list-eval-test git clone https://github.com/dharshanaw/todo-list-eval-test.git

Go to project directory evaluation_automation_test cd <git_Checkout_Directory>/todo-list-eval-test

Run execute test suite using maven <project_home>$ mvn clean install

If you are running the test directly by the IDE please set following system variable for API URL apiBackendUrl = http://localhost:3002/

Please Note: If you are running maven clean local repository it will take some time to get all maven plugins required. (Only for the first time)

### Evaluation of test results

This test module is configured with the Maven Surefire plugin therefore you can find all the reports under <Build_Home>/target/surefire-reports/ directory.