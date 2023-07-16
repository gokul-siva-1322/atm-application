package atm;

import java.io.*;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static atm.CustomerDAO.customerAuthentication;

public class App {
    // Required unique fields
    public static int uniqueTransactionNumber = 1000;

    // fields to store file paths
    public static final String currentDir = System.getProperty("user.dir");
    public static final String FILES_PATH = currentDir + "\\src\\files\\";
    public static final String CUSTOMERS_DATA_FILE_PATH = FILES_PATH + "atm-customers.txt";
    public static final String ATM_DENOMINATIONS_FILE_PATH = FILES_PATH + "atm-denominations.txt";
    public static final String LAST_TRANSACTION_NUMBER_FILE_PATH = FILES_PATH + "last-transaction-number.txt";
    public static final String CUSTOMER_MINI_STATEMENTS_FOLDER_PATH = FILES_PATH + "mini-statements\\";
    public static final String CUSTOMER_MINI_STATEMENT_FILE_SUFFIX = "_transactions.txt";

    //  Data Structures to Store data
    public static HashMap<Integer, Integer> atmDenominations = new HashMap<>();

    static {

        atmDenominations.put(2000, 0);
        atmDenominations.put(500, 0);
        atmDenominations.put(100, 0);
        System.out.println("In static block: " + atmDenominations);

    }
    public static List<Transaction> transactions = Collections.synchronizedList(new ArrayList<>());
    public static List<Customer> customers = Collections.synchronizedList(new ArrayList<>());

    // Utilities
    public static final Scanner input = new Scanner(System.in);
    public static final NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("en", "in"));


    // To clear previous entries in the CMD
    public static void clearScreen(){
        System.out.println("\033[H\033[2J");
        System.out.flush();
    }

    /*
    Prints 'Invalid Choice!' in the console
    */
    public static void enterYourChoice(){
        System.out.print("""
                Enter Your Choice:\s""");
    }

    /*
    Prints 'Invalid Choice!' in the console
    */
    public static void invalidChoice(){
        System.out.println("""
                
                Invalid Choice!
                """);
    }

    /*
    Prints 'Press Enter To Continue...' in
    the console
    */
    public static void pressEnterToContinue(){
        System.out.print("""
                Press Enter To Continue...""");
        input.nextLine();
    }

    /*
    Method to load default user details
    * */
    public static void loadDefault() {
        List<?> tempCustomers = (List<?>) customDeSerialization(CUSTOMERS_DATA_FILE_PATH);
        if(tempCustomers == null) {
            // int accountNumber, String accountHolderName, String accountPin, double accountBalance
            Customer customer101 = new Customer(101, "Suresh", "2343", 25_234);
            Customer customer102 = new Customer(102, "Ganesh", "5432", 34_123);
            Customer customer103 = new Customer(103, "Magesh", "7854", 26_100);
            Customer customer104 = new Customer(104, "Naresh", "2345", 80_000);
            Customer customer105 = new Customer(105, "Harish", "1907", 1_03_400);
            customers.add(customer101);
            customers.add(customer102);
            customers.add(customer103);
            customers.add(customer104);
            customers.add(customer105);
            for (Customer customer: customers){
                customSerialization(CUSTOMER_MINI_STATEMENTS_FOLDER_PATH + customer.getAccountNumber() + CUSTOMER_MINI_STATEMENT_FILE_SUFFIX, null);
            }
            customSerialization(CUSTOMERS_DATA_FILE_PATH, customers);
        }
        else {
            for (Object object : tempCustomers) {
                if (object instanceof Customer) {
                    customers.add((Customer) object);
                }
            }
        }

        Integer lastTransactionNumber = (Integer) customDeSerialization(LAST_TRANSACTION_NUMBER_FILE_PATH);
        if(lastTransactionNumber == null){
            customSerialization(LAST_TRANSACTION_NUMBER_FILE_PATH, uniqueTransactionNumber);
        }
        else {
            uniqueTransactionNumber = lastTransactionNumber;
        }
    }

    /*
    Method to Load Previous Denomination count If any
    * */
    public static void loadPreviousDenominationIfAny() throws IOException, ClassNotFoundException {
        System.out.println("Loading Previous Denominations in the ATM....");
        HashMap<?, ?> tempAtmDenominations = (HashMap<?, ?>) customDeSerialization(ATM_DENOMINATIONS_FILE_PATH);
        if(tempAtmDenominations == null){
            System.out.println("No Previous Denominations Found!");
            customSerialization(ATM_DENOMINATIONS_FILE_PATH, atmDenominations);
        }
        else {
            System.out.println("Old Denominations File Found!");
            System.out.println("Adding Previous Denomination....");
            atmDenominations.replaceAll((key, value) -> value + (Integer) tempAtmDenominations.get(key));
            System.out.println("Added Successfully!");
        }

    }

    /*This Method check if all the necessary file directories are found
    if not it creates the required directories.
    * */
    public static void checkNeededDirectoriesPresentIfNotCreate(){
        File filesDirectory = new File(FILES_PATH);
        if (!filesDirectory.exists()){
            System.out.println("Files Dir Created: " + filesDirectory.mkdir());
        }
        File transactionsDirectory = new File(CUSTOMER_MINI_STATEMENTS_FOLDER_PATH);
        if(!transactionsDirectory.exists()){
            System.out.println("Mini Statements Dir Created: " + transactionsDirectory.mkdir());
        }
    }

    /*
    Function to handle all Serialization (File Writing) operations
    * */
    public static void customSerialization(String fileName, Object object) {
        FileOutputStream fileOutputStream;
        ObjectOutputStream objectOutputStream;

        try{
            fileOutputStream = new FileOutputStream(fileName);
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();

            objectOutputStream.close();
            fileOutputStream.close();
        }
        catch (IOException exception){
            System.out.println("Error Occurred, During Serialization: " + exception.getMessage());
        }
    }

    /*
    Function to handle all deserialization (File Reading) operations
    * */
    public static Object customDeSerialization(String fileName) {
        Object object = null;
        FileInputStream fileInputStream;
        ObjectInputStream objectInputStream;

        try{
            fileInputStream = new FileInputStream(fileName);
            objectInputStream = new ObjectInputStream(fileInputStream);
            object = objectInputStream.readObject();

            objectInputStream.close();
            fileInputStream.close();
        }
        catch (IOException | ClassNotFoundException exception){
            System.out.println("Error Occurred, During Deserialization: " + exception.getMessage());
        }

        return object;
    }

    /*
    Method to Load Cash to ATM
    1. It asks No. of Notes we need to add for every denomination.
       Once entered it adds to the ATM denomination count.
    * */
    public static void loadCashTotAtm() {
        ArrayList<Integer> keys = new ArrayList<>(atmDenominations.keySet());
        int keysLength = keys.size();
        int counter = 0;
        HashMap<Integer, Integer> inputDenominations = new HashMap<>();

        while (counter < keysLength){
            System.out.printf("Enter Number of %d: %n", keys.get(counter));
            int noOfNotes = 0;
            try {
                String stringNoOfNotes = input.nextLine().trim();
                noOfNotes = Integer.parseInt(stringNoOfNotes);
                inputDenominations.put(keys.get(counter), noOfNotes);
                counter ++;
            }
            catch (Exception exception){
                System.out.println("Invalid Input!");
                pressEnterToContinue();
            }
        }
        atmDenominations.replaceAll((key, value) -> value + (Integer) inputDenominations.get(key));
        customSerialization(ATM_DENOMINATIONS_FILE_PATH, atmDenominations);
        System.out.println("Cash Loaded into ATM...");
        checkAtmBalance();
    }

    /*
    Method to show Denomination, their count and subtotal along with,
    Total Balance available in a map.
    * */
    public static void showAndCalculateTotalDenomination(HashMap<Integer, Integer> denominations){
        int totalAmount = 0;
        System.out.println("_".repeat(50));
        System.out.printf("| %-15s | %-10s | %-15s |\n","Denominations","Number","Value");
        System.out.println("_".repeat(50));
        for (Integer key: denominations.keySet()){
            int denominationCount = denominations.get(key);
            int subTotal = key * denominationCount;
            System.out.printf("| %-15d | %-10d | %-15s |\n", key, denominationCount, numberFormat.format(subTotal));
            totalAmount += subTotal;

        }
        System.out.println("_".repeat(50));
        System.out.println("Total Amount = Rs. " + numberFormat.format(totalAmount));
    }

    /*
    Method to show ATM Balance
    (i.e) Each Denomination, their count and subtotal along with,
    Total Balance available in the ATM.
    * */
    public static void checkAtmBalance(){
        showAndCalculateTotalDenomination(atmDenominations);
        pressEnterToContinue();
    }

    /*
    This method returns a table that contains all the information about
    the customers.
    * */
    public static void showCustomerDetails(){
        System.out.println("_".repeat(57));
        System.out.printf("| %-10s | %-10s | %-10s | %-15s |\n","Acc No.","Acc Holder","PIN No.","Balance");
        System.out.println("_".repeat(57));
        for(Customer customer : customers){
            System.out.printf("| %-10d | %-10s | %-10s | %-15s |\n",
                    customer.getAccountNumber(),
                    customer.getAccountHolderName(),
                    customer.getAccountPin(),
                    numberFormat.format(customer.getAccountBalance()));
        }
        System.out.println("_".repeat(57));

        pressEnterToContinue();
    }

    /*
    Method to automate transaction Entry in the respective Customer File.
   (i.e) it Creates a scheduled thread that executes the transaction update
    Function for every 5 seconds
    * */
    public static void startTransactionTread(ScheduledExecutorService scheduledExecutorService ){
        // Updating for Every Customer
        for (Customer customer : customers){
            scheduledExecutorService.scheduleAtFixedRate(new MiniStatement(customer.getAccountNumber()), 0, 5, TimeUnit.SECONDS);
        }
    }

    /*
    Every Time the Thread executes it also updated the file related data structures
    also
    * */
    public static void updateEverythingInFile(){
        customSerialization(LAST_TRANSACTION_NUMBER_FILE_PATH, uniqueTransactionNumber);
        customSerialization(ATM_DENOMINATIONS_FILE_PATH, atmDenominations);
        customSerialization(CUSTOMERS_DATA_FILE_PATH, customers);
    }

    // Main Method
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        checkNeededDirectoriesPresentIfNotCreate();

        // Method call to load previous denomination count if any.
        loadPreviousDenominationIfAny();

        // Method call to load default customer detail.
        loadDefault();

        // Creating a Thread Pool
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        startTransactionTread(scheduledExecutorService);

        mainWhileLoop:
        while (true){
            System.out.println("""
                    1. Load Cash to ATM
                    2. Show Customer Details
                    3. Show ATM Operations
                    4.Exit
                    """);
            enterYourChoice();
            String choice = input.nextLine();
            switch (choice){
                case "1":
                    System.out.println("***In, Load Cash to ATM***");
                    loadCashTotAtm();
                    break;
                case "2":
                    System.out.println("***Show Customer Details***");
                    showCustomerDetails();
                    break;
                case "3":
                    System.out.println("***Show ATM Operations***");
                    customerAuthentication();
                    break;
                case "4":
                    System.out.println("***Thank You\3***");
                    // Breaking the loop using Loop Label
                    break mainWhileLoop;
                default:
                    invalidChoice();
                    pressEnterToContinue();
                    break;
            }
        }
        updateEverythingInFile();
        scheduledExecutorService.shutdown();
    }
}
