package emulation;

import sim.MainSimulator;
import sim.TemperatureSim;

public class MainEmulator 
{
    public static void main(String[] args) throws Exception 
    {
        //REMOVE ONCE CONFIG.JAVA IS IMPLEMENTED
        int port = 1502;

        RegisterMemory memory = new RegisterMemory(10);

        ModbusServer server = new ModbusServer(port, memory);
        MainSimulator simManager = new MainSimulator(memory);

        simManager.addSimulator(new TemperatureSim(0, 1000, 20.0, 80.0));

        
        server.start();
        System.out.println("Simulators started.\nPress Ctrl+C to stop.");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> 
        {
            simManager.stop(); 
            server.stop();
        }));
        Thread.currentThread().join();
    }
}