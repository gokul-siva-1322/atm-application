package atm;

import java.io.Serializable;

public class Transaction implements Serializable {
    private int transactionNumber;
    private int accountNumber;
    private String description;
    private boolean isCredit;
    private int fromOrToAccountNumber;
    private boolean isWithdrawal;
    private int amount;
    private int closingBalance;

    // No argument Constructor for Serialization
    public Transaction(){

    }
    public Transaction(int transactionNumber, int accountNumber, String description, boolean isCredit, int fromOrToAccountNumber, boolean isWithdrawal,int amount, int closingBalance) {
        this.transactionNumber = transactionNumber;
        this.accountNumber = accountNumber;
        this.description = description;
        this.isCredit = isCredit;
        this.fromOrToAccountNumber = fromOrToAccountNumber;
        this.isWithdrawal = isWithdrawal;
        this.amount = amount;
        this.closingBalance = closingBalance;
    }


    public int getTransactionNumber() {
        return transactionNumber;
    }

    public void setTransactionNumber(int transactionNumber) {
        this.transactionNumber = transactionNumber;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCredit() {
        return isCredit;
    }

    public void setCredit(boolean credit) {
        isCredit = credit;
    }

    public int getFromOrToAccountNumber() {
        return fromOrToAccountNumber;
    }

    public void setFromOrToAccountNumber(int fromOrToAccountNumber) {
        this.fromOrToAccountNumber = fromOrToAccountNumber;
    }

    public boolean isWithdrawal() {
        return isWithdrawal;
    }

    public void setWithdrawal(boolean withdrawal) {
        isWithdrawal = withdrawal;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getClosingBalance() {
        return closingBalance;
    }

    public void setClosingBalance(int closingBalance) {
        this.closingBalance = closingBalance;
    }
}
