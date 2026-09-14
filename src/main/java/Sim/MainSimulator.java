package sim;

import conf.Config;
import emulation.RegisterMemory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainSimulator {

    private static final int VALUE_SCALE = 100;

    private final RegisterMemory memory;

    private final ScheduledExecutorService executor;

    private final List<SimTemplate> simulators = new ArrayList<>();

    public MainSimulator(RegisterMemory registerMemory) 
    {
        if (registerMemory == null)
            throw new IllegalArgumentException("RegisterMemory cannot be null.");

        memory = registerMemory;

        executor = Executors.newScheduledThreadPool(4);
    }

    public void addSimulator(Config.SimulatorSettings settings) 
    {

        if (settings == null)
            throw new IllegalArgumentException("Simulator settings cannot be null.");

        if (!settings.enabled) 
        {
            System.out.printf("Simulator '%s' is disabled.%n", settings.name);
            return;
        }

        SimTemplate simulator = createSimulator(settings);

        simulators.add(simulator);

        executor.scheduleAtFixedRate(() -> updateSimulator(simulator), 0, simulator.getUpdateFrequencyMs(), TimeUnit.MILLISECONDS);

        System.out.printf("Started simulator '%s' -> register %d%n", simulator.getName(), simulator.getRegisterAddress());
    }

    private SimTemplate createSimulator(Config.SimulatorSettings settings) 
    {
        return switch (settings.type.toLowerCase())
        {
            case "temperature" ->
                new TemperatureSim(settings);
            default ->
                    throw new IllegalArgumentException("Unknown simulator type: " + settings.type);
        };
    }

    private void updateSimulator(SimTemplate simulator) 
    {
        try 
        {
            double value = simulator.generate();

            int registerAddress = simulator.getRegisterAddress();
            int registerValue =(int) Math.round(value * VALUE_SCALE);

            memory.setHoldingRegister(registerAddress, registerValue);

            System.out.printf("%s -> register %d = %.2f%n", simulator.getName(), registerAddress, value);
        } 
        catch (Exception e) 
        {
            System.err.printf("Simulator '%s' error: %s%n", simulator.getName(), e.getMessage());
        }
    }

    public void stop() 
    {
        executor.shutdownNow();

        try 
        {
            if (!executor.awaitTermination(2, TimeUnit.SECONDS)) 
                System.err.println("Simulator executor did not terminate.");
        } 
        catch (InterruptedException e) 
        {
            Thread.currentThread().interrupt();

            System.err.println("Interrupted while stopping simulators.");
        }

        simulators.clear();

        System.out.println("Simulators stopped.");
    }

    public int getSimulatorCount() 
    {
        return simulators.size();
    }
}