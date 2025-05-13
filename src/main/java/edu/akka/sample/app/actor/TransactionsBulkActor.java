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

package edu.akka.sample.app.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Status;
import edu.akka.sample.app.actor.CustomerActor.TransactionProcessed;
import edu.akka.sample.app.data.definition.Customer;
import edu.akka.sample.app.data.definition.Transaction;
import edu.akka.sample.app.utils.CustomSystemOut;
import java.util.List;

/**
 * Bulk Actor that receives a list of financial transactions and sends each one of the to the
 * respective, related Customer Actor for processing.
 */
public class TransactionsBulkActor extends AbstractActor {

  private int numberOfTransactionsToProcess;
  private ActorRef parentActor;

  @Override
  public Receive createReceive() {

    return receiveBuilder()
        .match(
            List.class,
            this::sendToCustomers)
        .match(TransactionProcessed.class,
            this::acknowledgeProcessedTransaction)
        .matchAny(o -> CustomSystemOut.INSTANCE.red(
            "Unknown message received in Bulk Actor! " + o.toString()))
        .build();
  }

  /**
   * Operation called when the Actor receives financial transactions. Given the customer associated
   * to the transaction, this operation creates or finds the related Customer Actor that is
   * responsible for processing it.
   *
   * @param transactions Financial transactions to be processed
   */
  private void sendToCustomers(List<Transaction> transactions) {

    determineParentActor();

    numberOfTransactionsToProcess = transactions.size();

    CustomSystemOut.INSTANCE.yellow(
        "Number of received transactions to process: " + numberOfTransactionsToProcess);

    transactions.forEach(transaction -> {

      ActorRef customerActor = getActorRef(transaction.customer());

      CustomSystemOut.INSTANCE.printAsIs(
          "Sending message to actor for customer " + transaction.customer()
              .getColorfulCustomerId());
      customerActor.tell(transaction, customerActor);
    });
  }

  /**
   * Operation called when the Actor receives a message from the child actor signaling that the
   * financial transaction was processed.
   *
   * @param transactionProcessed Message about the processing of the transaction
   */
  private void acknowledgeProcessedTransaction(TransactionProcessed transactionProcessed) {

    numberOfTransactionsToProcess--;

    if (numberOfTransactionsToProcess == 0) {

      CustomSystemOut.INSTANCE.yellow("Informing the parent that all transactions were processed");
      parentActor.tell(new Status.Success("OK"), getSelf());

      return;
    }

    CustomSystemOut.INSTANCE.yellow("Still " + numberOfTransactionsToProcess + " to go...");
  }

  /**
   * Registers the Actor that has sent the financial transactions, so that it can be informed later
   * on about the completion of the processing.
   */
  private void determineParentActor() {

    parentActor = getSender();
    CustomSystemOut.INSTANCE.yellow("Parent is determined: " + parentActor.path());
  }

  /**
   * Finds or creates the Actor reference associated with the given Customer.
   *
   * @param customer Customer
   * @return Actor reference for the given Customer
   */
  private ActorRef getActorRef(Customer customer) {

    String actorName = "customer-" + customer.getCustomerId();

    if (!getContext().child(actorName).isEmpty()) {

      CustomSystemOut.INSTANCE.yellow("Child actor for " + actorName + " is found");
      return getContext().child(actorName).get();
    }

    CustomSystemOut.INSTANCE.yellow("Actor for " + actorName + " is created");
    return getContext().actorOf(CustomerActor.getCustomerActor(), actorName);

  }
}
