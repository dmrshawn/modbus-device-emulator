package sim;

import emulation.RegisterMemory;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainSimulator {

    private final RegisterMemory registerMemory;
    private final ScheduledExecutorService executor;
    private static final int ROUNDING_MAGNITUDE = 100;

    public MainSimulator(RegisterMemory rm) 
    {
        registerMemory = rm;
        executor = Executors.newScheduledThreadPool(3);
    }

    public void addSimulator(SimTemplate simulator) 
    {
        executor.scheduleAtFixedRate(() -> update(simulator), 0, simulator.getUpdateFrequencyMs(), TimeUnit.MILLISECONDS);
    }

    private void update(SimTemplate simulator) 
    {

        double value = simulator.generate();

        int registerValue = (int) Math.round(value * ROUNDING_MAGNITUDE);

        registerMemory.setHoldingRegister(simulator.getRegisterAddress(), registerValue);

        System.out.printf("Register %d = %.2f%n", simulator.getRegisterAddress(), value);
    }

    public void stop() 
    {
        executor.shutdownNow();
    }
}