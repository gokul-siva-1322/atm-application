package atm;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.io.Serializable;

public class Customer implements Serializable {

    private static final int ENCODE_VALUE = 1;
    private static final int DECODE_VALUE = 10 - 1;
    private int accountNumber;
    private transient String accountPin;
    private String accountHolderName;
    private int accountBalance;

    public Customer(){}

    public Customer(int accountNumber, String accountHolderName, String accountPin, int accountBalance) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.accountPin = accountPin;
        this.accountBalance = accountBalance;
    }
    @Serial
    private void writeObject(ObjectOutputStream objectOutputStream) throws Exception{

        objectOutputStream.defaultWriteObject();
        // encrypting the password which transient
        String encryptedPassword = encodeAndDecodePIN(this.accountPin, ENCODE_VALUE);
        objectOutputStream.writeObject(encryptedPassword);
    }

    @Serial
    private void readObject(ObjectInputStream objectInputStream) throws Exception{
        objectInputStream.defaultReadObject();
        String encryptedPassword = (String) objectInputStream.readObject();
        // decrypting the password
        accountPin = encodeAndDecodePIN(encryptedPassword, DECODE_VALUE);
    }

    // flag-> true encode flag->false decode
    private String encodeAndDecodePIN(String pin, int value){

        String result = "";
        for (int i = 0; i < pin.length(); i++){
            char current = pin.charAt(i);
            result += ((char) (((current - '0' + value) % 10) + '0'));
        }
        return result;
    }
    public int getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public String getAccountPin() {
        return accountPin;
    }

    public void setAccountPin(String accountPin) {
        this.accountPin = accountPin;
    }

    public int getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(int accountBalance) {
        this.accountBalance = accountBalance;
    }
}
