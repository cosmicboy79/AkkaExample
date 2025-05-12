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

import edu.akka.sample.finance.data.definition.Transaction;
import edu.akka.sample.finance.data.definition.TransactionType;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class TransactionProvider {

  private static final TransactionProvider instance = new TransactionProvider();

  final List<Transaction> transactions = List.of(
      new Transaction(Customer.CUSTOMER_ID_1.getCustomerId(), 21.90,
          TransactionType.INVOICE),
      new Transaction(Customer.CUSTOMER_ID_2.getCustomerId(), 32.00,
          TransactionType.INVOICE),
      new Transaction(Customer.CUSTOMER_ID_3.getCustomerId(), 17.43,
          TransactionType.INVOICE),
      new Transaction(Customer.CUSTOMER_ID_1.getCustomerId(), 20.00,
          TransactionType.PAYMENT),
      new Transaction(Customer.CUSTOMER_ID_1.getCustomerId(), 2.00,
          TransactionType.PAYMENT),
      new Transaction(Customer.CUSTOMER_ID_3.getCustomerId(), 3.00,
          TransactionType.PAYMENT),
      new Transaction(Customer.CUSTOMER_ID_2.getCustomerId(), 10.00,
          TransactionType.PAYMENT),
      new Transaction(Customer.CUSTOMER_ID_2.getCustomerId(), 5.00,
          TransactionType.PAYMENT),
      new Transaction(Customer.CUSTOMER_ID_3.getCustomerId(), 2.00,
          TransactionType.REFUND),
      new Transaction(Customer.CUSTOMER_ID_2.getCustomerId(), 1.00,
          TransactionType.REFUND),
      new Transaction(Customer.CUSTOMER_ID_1.getCustomerId(), 7.50,
          TransactionType.PAYMENT)
  );

  int numberOfTransactionsRead = 0;

  public static TransactionProvider getInstance() {

    return instance;
  }

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

  int sizeOfAvailableData() {

    return transactions.size();
  }

  private enum Customer {

    CUSTOMER_ID_1("1fd40c65-f596-45d8-9e0a-632c37ccb771"),
    CUSTOMER_ID_2("00221321-592f-49f7-933a-e6aebdc716a6"),
    CUSTOMER_ID_3("ed870e05-ac7a-4847-8d40-bb37f1fe4880");


    private final String customerId;

    Customer(String customerId) {

      this.customerId = customerId;
    }

    public String getCustomerId() {

      return customerId;
    }
  }
}
