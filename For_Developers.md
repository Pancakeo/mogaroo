# Pre-Rex (Developer Guide) #

Things you need:

Maven (Tested on 2.2.1)

Subversion (Tested with command line svn, tortoise svn)

Java (Tested on 1.6)

Eclipse is handy.

Java CLI API: http://code.google.com/p/java-cli-api/

# Steps #

Firstly, do a svn checkout on Mogaroo. See source -> checkout.

Secondly, do a svn checkout on this: http://code.google.com/p/java-cli-api/

Next, do some Mavening.  'mvn eclipse:eclipse' for all projects.

Five! Import into Eclipse.  Add 'cli-api' as a required project for Mogaroo.

Zenly, see these notes if you want to run myuw-cli in Eclipse: http://code.google.com/p/java-cli-api/wiki/RunningInEclipse

And finally, once you start using myuw-cli you can run some commands and stuff.  Monitorclasses is probably the sought after one. (Maybe autoregister now)

# Notes #

myuw-api is the driving horse.

myuw-cli is a command line rider for the driving horse.

myuw-gui is an empty folder.