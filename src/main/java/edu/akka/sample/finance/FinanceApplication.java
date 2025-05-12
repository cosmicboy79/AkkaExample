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
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

public class FinanceApplication {

  private static final int NUMBER_OF_TRANSACTIONS_TO_READ = 5;

  public static void main(String[] args) throws InterruptedException, TimeoutException {

    ActorSystem actorSystem = ActorSystem.create("FinanceAkkaSampleApp");

    ActorRef bulkActor = actorSystem.actorOf(Props.create(FinanceTransactionsBulkActor.class));

    List<Transaction> transactions = TransactionProvider.getInstance().readTransactions(
        NUMBER_OF_TRANSACTIONS_TO_READ);

    while (!transactions.isEmpty()) {

      bulkActor.tell(transactions, actorSystem.guardian());

      Await.result(Patterns.ask(bulkActor, transactions, Timeout.apply(5, TimeUnit.MINUTES)),
          Duration.create(5, TimeUnit.MINUTES));

      transactions = TransactionProvider.getInstance()
          .readTransactions(NUMBER_OF_TRANSACTIONS_TO_READ);
    }

    actorSystem.terminate();

    Await.ready(actorSystem.whenTerminated(), Inf());
  }
}
