# Q3 Customer Bank Details & ATM Process


## Steps To run the Application
  
- **Step 1:** Dowload or pull the code from master branch
- **Step 2:** Open the project (atm-application) in an IDE
- **Step 3:** Run the main method in ***atm-application\src\atm\APP.java***

(Note: The ATM already has some balance denominations and user transactions. If you want to start anew, you can delete the file directory(atm-application\src\files directory) and re-run the application again.)|
------------- |

### Default Denominations in the ATM (if the files directory is deleted),

Denominations | Number | Value           
------------- | ------ | ------
2000 | 0 | ₹0.00   
500  | 0 | ₹0.00 
100  | 0 | ₹0.00 

Total Amount = Rs. ₹0.00

### Default users,  
  
Acc No | Account Holder | Pin Number | Account Balance
------ | -------------- | ---------- | ---------------
101 | Suresh | 2343 | 25,234 ₹  
102 | Ganesh | 5432 | 34,123 ₹   
103 | Magesh | 7854 | 26,100 ₹   
104 | Naresh | 2345	| 80,000 ₹   
105 | Harish | 1907 | 1,03,400 ₹   


## Main menu will be,  

1. Load Cash to ATM  
2. Show Customer Details   
3. Show ATM Operations
   
### Load Cash to ATM  
This option is used feed money into the machine by entering number of denominations.

#### Input be like,
- Enter Number of 2000: 20
- Enter Number of 500 : 100
- Enter Number of 100 : 100

#### Output be like,  
Cash Loaded into ATM...

Denominations | Number | Value           
------------- | ------ | ------
2000 | 1 | ₹40,000.00   
500  | 2 | ₹50,000.00 
100  | 3 | ₹10,000.00 

Total Amount = Rs. ₹1,00,000.00
