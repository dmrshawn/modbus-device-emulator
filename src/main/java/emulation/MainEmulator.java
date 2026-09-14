package emulation;

import conf.Config;
import sim.MainSimulator;

public class MainEmulator 
{
    public static void main(String[] args) throws Exception 
    {
        Config config = Config.load("config.json");

        RegisterMemory memory = new RegisterMemory(config.getRequiredRegisterCount());

        ModbusServer server = new ModbusServer(config.network.port, memory);

        MainSimulator simulator = new MainSimulator(memory);

        server.start();

        for (Config.SimulatorSettings settings : config.simulators)
            simulator.addSimulator(settings);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {simulator.stop(); server.stop();}));
    }
}