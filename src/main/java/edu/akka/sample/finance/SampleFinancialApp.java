/*
 * MIT License
 *
 * Copyright (c) 2025 Cristiano Silva
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package edu.akka.sample.finance;

import static scala.concurrent.duration.Duration.Inf;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.util.Timeout;
import edu.akka.sample.finance.actor.FinanceTransactionsBulkActor;
import edu.akka.sample.finance.data.definition.Transaction;
import edu.akka.sample.finance.data.provider.TransactionProvider;
import edu.akka.sample.finance.utils.CustomSystemOut;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

/**
 * Main Application: it reads financial transactions repeatedly and sends them for processing to a
 * Bulk Actor via Actor System.
 */
public class SampleFinancialApp {

  private static final int NUMBER_OF_TRANSACTIONS_TO_READ = 5;

  public static void main(String[] args) throws InterruptedException, TimeoutException {

    // getting the Actor System for this application
    ActorSystem actorSystem = ActorSystem.create("ClassicAkkaSampleFinancialApp");

    // creating the instance of the Bulk Actor
    ActorRef bulkActor = actorSystem.actorOf(Props.create(FinanceTransactionsBulkActor.class));

    // reading first chunk of financial data
    List<Transaction> transactions = TransactionProvider.getInstance().readTransactions(
        NUMBER_OF_TRANSACTIONS_TO_READ);

    int count = 0;

    while (!transactions.isEmpty()) {

      CustomSystemOut.INSTANCE.blankLine();
      CustomSystemOut.INSTANCE.blueBackground(
          "- Sending batch of transactions no. " + ++count + " for processing -");

      // ask pattern is used
      // in this case, a future is returned, and the main thread will wait for its completion
      // or timeout, if nothing is received...
      Await.result(Patterns.ask(bulkActor, transactions, Timeout.apply(5, TimeUnit.MINUTES)),
          Duration.create(5, TimeUnit.MINUTES));

      CustomSystemOut.INSTANCE.blueBackground("- Batch of transactions no. " + count + " processed -");
      CustomSystemOut.INSTANCE.blueBackground("- Trying to read more now... -");

      // trying to read more transactions
      transactions = TransactionProvider.getInstance()
          .readTransactions(NUMBER_OF_TRANSACTIONS_TO_READ);

      if (transactions.isEmpty()) {

        // nothing more!
        CustomSystemOut.INSTANCE.blankLine();
        CustomSystemOut.INSTANCE.blueBackground("- Nothing more to process -");
        CustomSystemOut.INSTANCE.blueBackground("- Goodbye! -");
      }
    }

    // shutting things down
    actorSystem.terminate();
    Await.ready(actorSystem.whenTerminated(), Inf());
  }
}
