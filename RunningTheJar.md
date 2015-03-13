# Steps #

Download a myuw-cli JAR from the Downloads page.
  * Open a terminal window. (Windows users: Command Prompt)
  * Navigate to the folder you put the JAR in.
  * Execute: java -jar <jar name>
  * You'll be prompted to enter various commands.

Note: Most commands require you to login, which can be done using the login command.

A few examples:
  1. monitorsection -d MATH -l 390 -s A -q winter -y 2012
  1. login -u creative\_username -p password
  1. register -q winter -y 2012 -sln 12345
  1. register -q winter -y 2012 -sln 12345 -sln 67890 -sln 54321
  1. getcourses -q winter -y 2012
  1. drop -q winter -y 2012 -sln 12345
  1. drop -q winter -y 2012 -sln 12345 -sln 54321 -sln 17791
  1. autoregister -q winter -y 2012 -e Astr,211,A,10577 -e Geog,123,A,14056 -e CSE,417,A,12358

Eventually, a GUI might be made.  Just in time for Christmas.

# Notes #
  1. You need Java 1.6