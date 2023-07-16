package atm;

import java.util.ArrayList;

import static atm.App.*;

public class MiniStatement implements Runnable{
    private int accountNumber;

    public MiniStatement(int accountNumber){
        this.accountNumber = accountNumber;
    }
    /*
    For Every customer the transactions are entered in different Files
    * */
    @Override
    public void run(){
        ArrayList<Transaction> tempTransactions = new ArrayList<>();
        // check any New transaction available
        if(transactions.size()!= 0){
            updateEverythingInFile();
            for (Transaction transaction: transactions){
                if(transaction.getAccountNumber() == this.accountNumber){
                    tempTransactions.add(transaction);
                }
            }
            transactions.removeAll(tempTransactions);
            if(tempTransactions.size() != 0){
                // Get Old transactions from the file if any
                String transactionFileName = CUSTOMER_MINI_STATEMENTS_FOLDER_PATH + this.accountNumber + CUSTOMER_MINI_STATEMENT_FILE_SUFFIX;
                ArrayList<?> oldTransactions = (ArrayList<?>) customDeSerialization(transactionFileName);
                if(oldTransactions != null){
                    tempTransactions.addAll((ArrayList<Transaction>)oldTransactions);
                }
                customSerialization(transactionFileName, tempTransactions);
            }
        }
    }
    public int getAccountNumber() {
        return accountNumber;
    }
    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }
}
