# Smart Banking System

## Project Description
The Smart Banking System is a console-based application that simulates real-world banking operations. It allows users to register, log in, and perform various banking activities such as depositing, withdrawing, and checking account balances. The system is designed to provide a user-friendly interface for managing bank accounts and transactions.

## Features
- User registration and login
- Account management (savings and current accounts)
- Deposit and withdrawal operations
- Balance checking
- Transaction history
- Input validation for user inputs

## Technologies Used
- Java
- Maven for project management
- Object-Oriented Programming principles

## OOP Concepts Applied
- **Encapsulation**: Private variables in classes with public getters and setters to protect data.
- **Inheritance**: SavingsAccount and CurrentAccount classes inherit from the Account class.
- **Polymorphism**: Interface for transaction operations allows different implementations for various transaction types.
- **Abstraction**: Use of interfaces and abstract classes to define common behaviors without exposing implementation details.

## How to Run the Project
1. Clone the repository to your local machine.
2. Navigate to the project directory.
3. Use Maven to build the project:
   ```
   mvn clean install
   ```
4. Run the application:
   ```
   mvn exec:java -Dexec.mainClass="com.banking.Main"
   ```

Enjoy managing your banking operations with the Smart Banking System!