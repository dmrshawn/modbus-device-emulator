# modbus-device-emulator
An emulator that simulates field I/O devices without physical hardware and sends the data to a PLC client via Modbus.

After running target/modbus-device-emulator-1.0-SNAPSHOT-jar-with-dependencies.jar, the temperature simulator should slowly fluctuate. 
Using Ctrl + C should stop the simulator and the Modbus Server.


You can make your own simulator by creating a simulator that implements SimTemplate.java and adding it to src/main/java/sim
