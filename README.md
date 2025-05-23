# AkkaExample

Sample application using Akka in Java.

For now it uses Java Akka Classic API only, but I intend to use this repository, soon, to learn about
the newest Akka API in Java as well. I plan also to include JUnit Tests for the Actors in the near future.

This sample application's main entry is class
[TransactionsProcessor](https://github.com/cosmicboy79/AkkaExample/blob/main/src/main/java/edu/akka/sample/app/classic/TransactionsProcessor.java).
The data to be processed is provided via class
[TransactionProvider](https://github.com/cosmicboy79/AkkaExample/blob/main/src/main/java/edu/akka/sample/app/classic/data/provider/TransactionProvider.java)
and sent to the Actor System, as follows:

1. [TransactionsActor](https://github.com/cosmicboy79/AkkaExample/blob/main/src/main/java/edu/akka/sample/app/classic/actor/TransactionsActor.java) - Actor
that receives a list of transactions to be processed. Based on the customer associated to each transaction, it will either find or create a related
child Actor (see next point).
2. [CustomerActor](https://github.com/cosmicboy79/AkkaExample/blob/main/src/main/java/edu/akka/sample/app/classic/actor/CustomerActor.java) - child Actor
(see previous point) that process the transaction for a customer.

This project can be built with either Maven or Gradle, and it was developed with Java 21. In both
cases, JAR file **sample-akka-app-all-<version>.jar** is built, so that the application can
be directly executed, in the command line, as per following example:

```
cd <location of JAR file>
java -jar sample-akka-app-all-1.0-SNAPSHOT.jar
```

In any case, I recommend to simply import this project and run it in the preferred IDE.
