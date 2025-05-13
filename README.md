# AkkaExample

Sample application using Akka with Java.

Right now, this Sample application uses the Java Akka Classic API. However, I intend
to use this repository also to exercise the newest API of Akka, in the future.

Main entry point for this sample application is
[SampleApp](https://github.com/cosmicboy79/AkkaExample/blob/main/src/main/java/edu/akka/sample/app/SampleApp.java).
Financial data is provided by
[TransactionProvider](https://github.com/cosmicboy79/AkkaExample/blob/main/src/main/java/edu/akka/sample/app/data/provider/TransactionProvider.java)
and sent to a Bulk Actor, that in turn sends to the respective child Actors for processing.

This project is built with Maven. Java version is 21.

I recommend to simply import and run it by using an IDE.
