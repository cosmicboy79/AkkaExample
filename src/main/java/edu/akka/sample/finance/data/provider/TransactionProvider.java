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

package edu.akka.sample.finance.data.provider;

import edu.akka.sample.finance.data.definition.Customer;
import edu.akka.sample.finance.data.definition.Transaction;
import edu.akka.sample.finance.data.definition.TransactionType;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Singleton that gives the financial transaction data to the main thread.
 * <p>
 * In a normal application this data would come from a database or message system, but for the
 * purposes of this exercise, it suffices.
 */
public class TransactionProvider {

  private static final TransactionProvider instance = new TransactionProvider();

  // the financial transaction data
  final List<Transaction> transactions = List.of(
      new Transaction(1, Customer.CUSTOMER_ID_1, 21.90,
          TransactionType.INVOICE),
      new Transaction(2, Customer.CUSTOMER_ID_2, 32.00,
          TransactionType.INVOICE),
      new Transaction(3, Customer.CUSTOMER_ID_3, 17.43,
          TransactionType.INVOICE),
      new Transaction(4, Customer.CUSTOMER_ID_1, 20.00,
          TransactionType.PAYMENT),
      new Transaction(5, Customer.CUSTOMER_ID_1, 2.00,
          TransactionType.PAYMENT),
      new Transaction(6, Customer.CUSTOMER_ID_3, 3.00,
          TransactionType.PAYMENT),
      new Transaction(7, Customer.CUSTOMER_ID_2, 10.00,
          TransactionType.PAYMENT),
      new Transaction(8, Customer.CUSTOMER_ID_2, 5.00,
          TransactionType.PAYMENT),
      new Transaction(9, Customer.CUSTOMER_ID_3, 2.00,
          TransactionType.REFUND),
      new Transaction(10, Customer.CUSTOMER_ID_2, 1.00,
          TransactionType.REFUND),
      new Transaction(11, Customer.CUSTOMER_ID_1, 7.50,
          TransactionType.PAYMENT)
  );

  // this is an offset pointing to the index to be used for a next read
  private int numberOfTransactionsRead = 0;

  public static TransactionProvider getInstance() {

    return instance;
  }

  /**
   * Reads a of financial transactions for the given input number.
   * <p>
   * Every time this operation is called, the offset shifts for the next read. If there is nothing
   * more to read, then this operation returns an empty list.
   *
   * @param numberOfTransactionsToRead How many transactions should be returned
   * @return Financial transactions as list of {@link Transaction}, or empty list, if there is
   * nothing more to read
   */
  public List<Transaction> readTransactions(int numberOfTransactionsToRead) {

    if (numberOfTransactionsRead >= transactions.size()) {

      return Collections.emptyList();
    }

    if ((numberOfTransactionsRead + numberOfTransactionsToRead) >= transactions.size()) {

      numberOfTransactionsToRead = transactions.size() - numberOfTransactionsRead;
    }

    List<Transaction> result = IntStream.range(numberOfTransactionsRead,
            (numberOfTransactionsRead + numberOfTransactionsToRead))
        .mapToObj(transactions::get)
        .toList();

    numberOfTransactionsRead += numberOfTransactionsToRead;

    return result;
  }

  /**
   * @return Total size of available financial transactions
   */
  int sizeOfAvailableData() {

    return transactions.size();
  }
}
