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
import akka.actor.Props;
import edu.akka.sample.finance.data.definition.Transaction;
import edu.akka.sample.finance.utils.ColorfulOut;

class CustomerActor extends AbstractActor {

  public static Props getCustomerActor() {

    return Props.create(CustomerActor.class, CustomerActor::new);
  }

  @Override
  public Receive createReceive() {

    return receiveBuilder()
        .match(
            Transaction.class,
            this::processTransaction)
        .matchAny(o -> ColorfulOut.INSTANCE.red(
            "Unknown message received in Customer Actor! " + o.toString()))
        .build();
  }

  private void processTransaction(Transaction transaction) {

    ColorfulOut.INSTANCE.blue("Processing transaction in " + getSelf().path());

    String message =
        transaction.transactionType() + " of amount " + transaction.amount() + " for customer "
            + transaction.customerId();
    ColorfulOut.INSTANCE.green(message);

    ColorfulOut.INSTANCE.blue("Processing done in " + getSelf().path());

    getContext().getParent().tell(new TransactionProcessed(), getSelf());
  }

  public record TransactionProcessed() {

  }
}
