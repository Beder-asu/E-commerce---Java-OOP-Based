Getting Started

To compile and run the main application from the command line:

1. Open Command Prompt and navigate to your project directory:
   cd "<your-project-directory>"
2. Compile the Java source files (including dependencies in lib):
   javac -cp "lib/*;src" -d bin src\App.java src\test\TestSuite.java src\models\*.java src\services\*.java
3. Run the application:
   java -cp "lib/*;bin" App

This will execute the App.java file, which runs your test suite or main application logic. Replace <your-project-directory> with the path to your own project folder.

Folder Structure

- src/ — Java source code
  - models/ — domain classes (Product, Customer, Order, etc.)
  - services/ — business/database logic (DBManager, etc.)
  - test/ — test classes (TestSuite, etc.)
  - App.java — main entry point
- lib/ — external libraries (e.g., sqlite-jdbc)
- resources/ — database file and schema (e.g., ecommerce.db, schema.pdf)
- bin/ — compiled output (created after build)

PS
- The DBTestProgram.java file is a standalone test for database operations. It can be run separately and is not part of the main application flow so it is not included or invoked by App.java.
