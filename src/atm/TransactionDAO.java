package atm;

import java.util.*;

import static atm.App.*;
import static atm.CustomerDAO.showBalance;

public class TransactionDAO {

    // withdrawal credentials min and max values
    public static final int MAX_WITHDRAWAL_LIMIT = 10_000;
    public static final int MIN_WITHDRAWAL_LIMIT = 100;
    public static final int MIN_DENOMINATIONS_CHANGE_LIMIT = 5_000;
    public static final int MIN_VENDED_2000_LESS_THAN_OR_EQUAL_5000 = 2;
    public static final int MIN_VENDED_500_LESS_THAN_OR_EQUAL_5000  = 4;
    public static final int MIN_VENDED_100_LESS_THAN_OR_EQUAL_5000  = 10;
    public static final int MAX_VENDED_100_LESS_THAN_OR_EQUAL_5000  = 15;

    public static final int MIN_VENDED_2000_GREATER_THAN_5000 = 2;
    public static final int MIN_VENDED_500_GREATER_THAN_5000  = 4;
    public static final int MIN_VENDED_100_GREATER_THAN_5000  = 10;
    public static final int MAX_VENDED_100_GREATER_THAN_5000  = 10;
    public static final Integer KEY_2000 = 2_000;
    public static final Integer KEY_500 = 500;
    public static final Integer KEY_100 = 100;
    public static final Integer MAX_TRANSACTION_TO_SHOWN = 10;

    /*
    This method updates the TransactionNumber to keep to unique
    * */
    public static void updateUniqueTransactionNumber(){
        uniqueTransactionNumber += 1;
    }
    /*
    Calculate the map total value by multiplying the key and value
    and summing the product
    * */
    public static int calculateTotal(HashMap<Integer, Integer> map){
        int totalAmount = 0;
        for (Integer key: map.keySet()){
            int denominationCount = map.get(key);
            int subTotal = key * denominationCount;
            totalAmount += subTotal;
        }
        return totalAmount;
    }
    public static boolean isRequiredDenominationsPossible(HashMap<Integer, Integer> map){
        for (Integer key : map.keySet()){
            if(!(atmDenominations.get(key) - map.get(key) >= 0)){
                return false;
            }
        }
        return true;
    }

    // validating the ATM PIN with regex and equals
    public static boolean isValidPIN(Customer currentCustomer){
        System.out.println("Enter Your PIN Number: ");
        String stringCurrentPinNumber = input.nextLine().trim();

        if(!stringCurrentPinNumber.matches("\\d{4}")){
            System.out.println("Sorry, Invalid PIN!");
            pressEnterToContinue();
            return false;
        }
        else {
            if(!currentCustomer.getAccountPin().equals(stringCurrentPinNumber)){
                System.out.println("Invalid PIN!");
                pressEnterToContinue();
                return false;
            }
        }
        return true;
    }

    /*
    Method to transfer money to another customer
    * */
    public static void transferMoney(Customer currentCustomer){
        showBalance(currentCustomer);

        System.out.println("Enter Receiver Acc No.: ");
        String stringCAccNumber = input.nextLine().trim();

        int transferAccNumber = 0;
        Customer transferCustomer = null;

        try {
            transferAccNumber = Integer.parseInt(stringCAccNumber);
            for(Customer customer : customers){
                if(transferAccNumber == customer.getAccountNumber()){
                    transferCustomer = customer;
                    break;
                }
            }
            if(transferCustomer == null){
                System.out.println("Invalid Acc No.!");
                pressEnterToContinue();
                return;
            }
        }
        catch (NumberFormatException numberFormatException){
            System.out.println("Invalid Acc No.!");
            pressEnterToContinue();
            return;
        }

        System.out.println("Enter Transfer Amount : ");
        String stringTransferAmount = input.nextLine().trim();
        int transferAmount = 0;

        try {
            transferAmount = Integer.parseInt(stringTransferAmount);
        }
        catch (NumberFormatException numberFormatException){
            System.out.println("Invalid Acc No.!");
            pressEnterToContinue();
            return;
        }
        if(transferAccNumber == currentCustomer.getAccountNumber()){
            System.out.println("You Can't Self Transfer!");
            pressEnterToContinue();
            return;
        }
        if(transferAmount < 1000 || transferAmount > 10_000){
            System.out.println("Enter Transfer Amount within the Range Rs. 1,000 - 10,000...");
            pressEnterToContinue();
            return;
        }
        else if(currentCustomer.getAccountBalance() < transferAmount){
            System.out.println("Not Enough Amount in Your Account...");
            pressEnterToContinue();
            return;
        }
        if(!isValidPIN(currentCustomer)){
            return;
        }
        currentCustomer.setAccountBalance(currentCustomer.getAccountBalance() - transferAmount);
        transferCustomer.setAccountBalance(transferCustomer.getAccountBalance() + transferAmount);

        System.out.println("Transfer was Successful!");

        String description1 = "Transfer to "+ transferAccNumber;
        Transaction transaction1 = new Transaction(
                uniqueTransactionNumber,
                currentCustomer.getAccountNumber(),
                description1,
                false,
                transferCustomer.getAccountNumber(),
                false,
                transferAmount,
                currentCustomer.getAccountBalance());
        updateUniqueTransactionNumber();
        transactions.add(transaction1);

        String description2 = "Transfer From "+ currentCustomer.getAccountNumber();
        Transaction transaction = new Transaction(
                uniqueTransactionNumber,
                transferAccNumber,
                description2,
                true,
                currentCustomer.getAccountNumber(),
                false,
                transferAmount,
                transferCustomer.getAccountBalance());
        updateUniqueTransactionNumber();
        transactions.add(transaction);

        System.out.println("Current Balance: " + numberFormat.format(currentCustomer.getAccountBalance()));
        pressEnterToContinue();
    }

    /*
    This Function sets the minimum required denominations prescribed in the rules
    * */
    public static HashMap<Integer, Integer> setMinimumDenomination(int withdrawAmount){
        HashMap<Integer, Integer> withdrawDenominations = new HashMap<>(atmDenominations);
        withdrawDenominations.replaceAll((key, value)-> 0);

        withdrawDenominations.replace(KEY_100 , 1);

        if (withdrawAmount > 1000){
            withdrawDenominations.replace(KEY_500 , 1);
        }
        if (withdrawAmount > 1500){
            withdrawDenominations.replace(KEY_100 , 10);
        }
        if(withdrawAmount > 3000){
            withdrawDenominations.replace(KEY_2000, 1);
        }
        if(withdrawAmount > 5000){
            withdrawDenominations.replace(KEY_500 , 2);
            withdrawDenominations.replace(KEY_2000, 2);
        }

        return withdrawDenominations;
    }
    /*
    This method fills the balance denomination once the minimum denomination is satisfied
    * */
    public static boolean fillUpBalanceDenomination(boolean flag, int withDrawAmount, int copyWithdrawAmount,HashMap<Integer, Integer> withdrawDenominations, HashMap<Integer, Integer> tempAtmDenominations){
        boolean canGivePossibleDenominations = true;
        for (int key : atmDenominations.keySet().stream().sorted(Comparator.reverseOrder()).toList()){
            if(copyWithdrawAmount >= key){

                int requiredKeyCount = Math.floorDiv(copyWithdrawAmount, key);

                if(tempAtmDenominations.get(key) == 0){
                    requiredKeyCount = 0;
                }
                else if (requiredKeyCount >= tempAtmDenominations.get(key)) {
                    requiredKeyCount = tempAtmDenominations.get(key);
                }
                if(flag){
                    int maxDenominationCountAllowed = 0;
                    if(key == KEY_100){
                        maxDenominationCountAllowed = withDrawAmount <= MIN_DENOMINATIONS_CHANGE_LIMIT ? MAX_VENDED_100_LESS_THAN_OR_EQUAL_5000 : MAX_VENDED_100_GREATER_THAN_5000;
                        if(requiredKeyCount + withdrawDenominations.get(KEY_100) > maxDenominationCountAllowed){
                            canGivePossibleDenominations = false;
                        }
                    }
                }
                withdrawDenominations.put(key, withdrawDenominations.get(key) + requiredKeyCount);
                tempAtmDenominations.put(key, tempAtmDenominations.get(key) - requiredKeyCount);
                copyWithdrawAmount -= (key * requiredKeyCount);
            }

        }
        return canGivePossibleDenominations;
    }
    /*
    Main withdraw method
    * */
    public static void withdrawMoney(Customer currentCustomer){
        HashMap<Integer, Integer> tempAtmDenominations = new HashMap<>(atmDenominations);

        System.out.println("Enter Withdraw Amount: ");
        String stringWithdrawAmount = input.nextLine().trim();

        int withdrawAmount = 0;
        int copyWithdrawAmount = 0;

        try {
            withdrawAmount = Integer.parseInt(stringWithdrawAmount);
            copyWithdrawAmount = withdrawAmount;
        }
        catch (NumberFormatException numberFormatException){
            System.out.println("Invalid Amount!");
            pressEnterToContinue();
            return;
        }

        if(!isValidPIN(currentCustomer)){
            return;
        }

        if(withdrawAmount < MIN_WITHDRAWAL_LIMIT || withdrawAmount > MAX_WITHDRAWAL_LIMIT){
            System.out.println("Enter Amount Within the Range  100 - 10,000");
            pressEnterToContinue();
            return;
        }
        if(withdrawAmount > currentCustomer.getAccountBalance()){
            System.out.println("Insufficient Balance in Your Account!");
            pressEnterToContinue();
            return;
        }
        if(withdrawAmount > calculateTotal(atmDenominations)){
            System.out.println("Insufficient Balance in ATM!");
            pressEnterToContinue();
            return;
        }

        HashMap<Integer, Integer> withdrawDenomination = setMinimumDenomination(withdrawAmount);

        if(!isRequiredDenominationsPossible(withdrawDenomination)){
            System.out.println("No, Money (Can't give proper Denominations)");
            pressEnterToContinue();
            return;
        }

        tempAtmDenominations.replaceAll((key, value)-> value - withdrawDenomination.get(key));
        copyWithdrawAmount -= calculateTotal(withdrawDenomination);

        if(copyWithdrawAmount < 0){
            System.out.println("No, Money (Can't give proper Denominations by rules)");
            pressEnterToContinue();
            return;
        }
        boolean isValidDenominations = fillUpBalanceDenomination(true, withdrawAmount, copyWithdrawAmount, withdrawDenomination, tempAtmDenominations);
        if(!isValidDenominations){
            System.out.println("Sorry!, Can't give possible denominations...");
            System.out.println(withdrawDenomination);
            pressEnterToContinue();
            return;
        }

        atmDenominations.replaceAll((key, value) -> atmDenominations.get(key) - withdrawDenomination.get(key));
        currentCustomer.setAccountBalance(currentCustomer.getAccountBalance() - withdrawAmount);

        String description = "Cash Withdrawal";
        Transaction transaction = new Transaction(
                uniqueTransactionNumber,
                currentCustomer.getAccountNumber(),
                description,
                false,
                0,
                true,
                withdrawAmount,
                currentCustomer.getAccountBalance());
        updateUniqueTransactionNumber();
        transactions.add(transaction);

        showAndCalculateTotalDenomination(withdrawDenomination);
        System.out.println("Collect Your Cash!");
        pressEnterToContinue();
    }
    /*
    Show the last 10 transaction made by the customer
    * */
    public static void showMiniStatement(Customer currentCustomer){

        String fileName = CUSTOMER_MINI_STATEMENTS_FOLDER_PATH + currentCustomer.getAccountNumber() + CUSTOMER_MINI_STATEMENT_FILE_SUFFIX;
        ArrayList<Transaction> miniStatement = (ArrayList<Transaction>) customDeSerialization(fileName);
        int count = 0;
        if (miniStatement == null || miniStatement.size() == 0){
            System.out.println("No Transaction Till Now...");
            System.out.println("In Case you Made an Transaction,\n" +
                               "but it is not show, then wait\n" +
                                "5 second and try Again!");
            pressEnterToContinue();
            return;
        }
        sort(miniStatement);
        System.out.println("_".repeat(101));
        System.out.printf("| %-15s | %-25s | %-15s | %-15s | %-15s |\n","Transaction No.","Description","Credit / Debit","Amount","Closing Balance");
        System.out.println("_".repeat(101));
        for (Transaction transaction : miniStatement){
            String creditOrDebit = transaction.isCredit() ? "Credit" : "Debit";
            if(count < MAX_TRANSACTION_TO_SHOWN){
                System.out.printf("| %-15d | %-25s | %-15s | %-15d | %-15s |\n",
                        transaction.getTransactionNumber(),
                        transaction.getDescription(),
                        creditOrDebit,
                        transaction.getAmount(),
                        numberFormat.format(transaction.getClosingBalance()));
            }
        }
        System.out.println("_".repeat(101));
        pressEnterToContinue();
    }
    /*
    To sort transaction ArrayList
    * */
    public static void sort(ArrayList<Transaction> list){

        Collections.sort(list, (obj1, obj2) -> {
            int cmp = obj1.getTransactionNumber() > (obj2.getTransactionNumber()) ? -1 : 1;
            return cmp;
        });
    }

}
