# AkkaExample

Sample application using Akka in Java.

Right now, it uses the Java Akka Classic API only, but I intend
to use this repository, soon. also to learn about the newest API of Akka.

This sample application's main entry is class
[SampleApp](https://github.com/cosmicboy79/AkkaExample/blob/main/src/main/java/edu/akka/sample/app/SampleApp.java).
The data to be processed is provided via class
[TransactionProvider](https://github.com/cosmicboy79/AkkaExample/blob/main/src/main/java/edu/akka/sample/app/data/provider/TransactionProvider.java)
and sent to the Actor System, as follows:



This project is built with Maven. Java version is 21.

I recommend to simply import and run it by using an IDE.
