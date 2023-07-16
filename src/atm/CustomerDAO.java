package atm;

import static atm.App.*;
import static atm.TransactionDAO.showMiniStatement;

public class CustomerDAO {

    /*
    Method to authenticate Customer using Acc No. & PIN
    * */
    public static void customerAuthentication(){
        System.out.println("\n\t***Welcome!***\n");
        System.out.print("Enter Your Acc No.: ");
        String stringCurrentAccNumber = input.nextLine();
        System.out.print("\nEnter Your PIN No.: ");
        String stringCurrentPinNumber = input.nextLine();
        int currentAccNumber = 0;
        try {
            currentAccNumber = Integer.parseInt(stringCurrentAccNumber);
            if(!stringCurrentPinNumber.matches("\\d{4}")){
                System.out.println("Sorry, Invalid Acc No. or PIN!");
                pressEnterToContinue();
                return;
            }
            boolean flag = false;

            for (int i = 0; i < customers.size(); i++){
                Customer currentCustomer = customers.get(i);
                if(currentCustomer.getAccountNumber() == currentAccNumber
                        &&
                        currentCustomer.getAccountPin().equals(stringCurrentPinNumber)){
                    customerLanding(currentCustomer);
                    flag = true;
                    break;
                }
            }
            if(!flag){
                System.out.println("Invalid Acc No. or PIN!");
                pressEnterToContinue();
            }
        }
        catch (Exception exception){
            System.out.println("Invalid Acc No. or PIN!");
            pressEnterToContinue();
        }
    }

    /*
    Show Customer Balance
    * */
    public static void showBalance(Customer currentCustomer){

        System.out.println("Acc No.    : " + currentCustomer.getAccountNumber());
        System.out.println("Holder Name: " + currentCustomer.getAccountHolderName());
        System.out.println("Acc Balance: " + numberFormat.format(currentCustomer.getAccountBalance()));

        pressEnterToContinue();
    }

    /*
    Customer Landing Page
    * */
    public static void customerLanding(Customer currentCustomer){
        customerLandingWhileLoop:
        while (true){
            System.out.println("\n***Welcome, "+ currentCustomer.getAccountHolderName()+"!***\n");
            System.out.println("""
                    1. Check Balance
                    2. Withdraw Money
                    3. Transfer Money
                    4. Check ATM Balance
                    5. Mini Statement
                    6. Back
                    """);
            enterYourChoice();
            String choice = input.nextLine();
            switch (choice){
                case "1":
                    System.out.println("\n\t***Acc Details***\n");
                    showBalance(currentCustomer);
                    break;
                case "2":
                    System.out.println("\n\t***Withdrawal***\n");
                    TransactionDAO.withdrawMoney(currentCustomer);
                    break;
                case "3":
                    System.out.println("\n\t***Money Transfer***\t");
                    TransactionDAO.transferMoney(currentCustomer);
                    break;
                case "4":
                    System.out.println("\n\t\t\t\t***ATM Balance***\n");
                    checkAtmBalance();
                    break;
                case "5":
                    System.out.println("\n\t\t\t\t\t\t\t\t\t***Mini Statement***\n");
                    showMiniStatement(currentCustomer);
                    break;
                case "6":
                    // Breaking the loop using Loop Label
                    break customerLandingWhileLoop;
                default:
                    invalidChoice();
                    pressEnterToContinue();
                    break;
            }
        }
    }
}
