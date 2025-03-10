@echo on
chcp 65001 >nul
mvn package && java -jar target/02180-Backgammon-1.0-SNAPSHOT.jar
pause