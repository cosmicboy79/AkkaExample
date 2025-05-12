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

package edu.akka.sample.finance.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import edu.akka.sample.finance.actor.CustomerActor.TransactionProcessed;
import edu.akka.sample.finance.data.definition.Transaction;
import edu.akka.sample.finance.utils.ColorfulOut;
import java.util.List;

public class FinanceTransactionsBulkActor extends AbstractActor {

  private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

  private int numberOfTransactionsToProcess;

  @Override
  public Receive createReceive() {

    return receiveBuilder()
        .match(
            List.class,
            this::sendToCustomers)
        .match(TransactionProcessed.class,
            this::acknowledgeProcessedTransaction)
        .matchAny(o -> log.error("Unknown message received in Bulk Actor! " + o.toString()))
        .build();
  }

  private void sendToCustomers(List<Transaction> transactions) {

    numberOfTransactionsToProcess = transactions.size();

    ColorfulOut.INSTANCE.yellow(
        "Number of transactions to process: " + numberOfTransactionsToProcess);

    transactions.forEach(transaction -> {

      ActorRef customerActor = getContext().actorOf(
          CustomerActor.getCustomerActor(transaction.customerId()));

      ColorfulOut.INSTANCE.purple("Sending message to " + customerActor.path());

      customerActor.tell(transaction, customerActor);
    });
  }

  private void acknowledgeProcessedTransaction(TransactionProcessed transactionProcessed) {

    numberOfTransactionsToProcess--;

    if (numberOfTransactionsToProcess == 0) {

      ColorfulOut.INSTANCE.yellow("All transactions processed!");

      getContext().getParent().tell(PoisonPill.getInstance(), getSelf());
    }
  }
}
