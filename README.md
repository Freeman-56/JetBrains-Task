# Задача для JetBrains (IDE Feature Suggester)
Запуск: 
  ```<>
    javac -d out -sourcepath src src/com/company/Main.java
    jar cvfm TextEditor.jar ./manifest.txt ./out/com/company/AstTree ^ ./out/com/company/Parser ^ ./out/com/company
    java -jar TextEditor.jar
  ```
