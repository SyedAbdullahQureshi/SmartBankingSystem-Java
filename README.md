# Smart Banking System

## Project Description
Smart Banking System is a Java project that demonstrates real-world banking operations using Object-Oriented Programming concepts. The project now includes both a console workflow and a modern Swing-based GUI workflow.

## Features
- User registration and login
- Account management (savings and current accounts)
- Deposit and withdrawal operations
- Balance checking
- Basic transaction tracking
- Input validation and exception handling
- GUI screens for login, registration, and dashboard

## Technologies Used
- Java
- Java Swing (GUI)
- Object-Oriented Programming principles

## OOP Concepts Applied
- Encapsulation
- Inheritance
- Polymorphism
- Abstraction

## Project Structure
- `src/main/java/com/banking/Main.java` (console entry)
- `src/main/java/com/banking/gui/BankingGUI.java` (GUI entry)
- `src/main/java/com/banking/models/*`
- `src/main/java/com/banking/services/*`
- `src/main/java/com/banking/exceptions/*`

## How to Run
1. Clone the repository.
2. Open the project in VS Code/IntelliJ/Eclipse.
3. Compile:
   ```bash
   javac -d target/classes -sourcepath src/main/java src/main/java/com/banking/exceptions/*.java src/main/java/com/banking/interfaces/*.java src/main/java/com/banking/models/*.java src/main/java/com/banking/utils/*.java src/main/java/com/banking/services/*.java src/main/java/com/banking/gui/*.java src/main/java/com/banking/Main.java
   ```
4. Run console version:
   ```bash
   java -cp target/classes com.banking.Main
   ```
5. Run GUI version:
   ```bash
   java -cp target/classes com.banking.gui.BankingGUI
   ```

## Author
Abdullah
