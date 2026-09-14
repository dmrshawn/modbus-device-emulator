package sim;
import java.util.Random;

public class TemperatureSim implements SimTemplate
{
    private final int registerAddress;
    private final long updateFrequencyMs;
    private final double min;
    private final double max;

    private final Random random = new Random();

    private double value;

    public TemperatureSim(int ra, long ufms, double inputMin, double inputMax) 
    {
        registerAddress = ra;
        updateFrequencyMs = ufms;
        min = inputMin;
        max = inputMax;

        value = (min + max) / 2.0;
    }
    
    @Override
    public double generate() 
    {
        value += (random.nextDouble() - 0.5) * 2.0;

        value = Math.max(min, value);
        value = Math.min(max, value);

        return value;
    }

    @Override
    public long getUpdateFrequencyMs() 
    {
        return updateFrequencyMs;
    }

    @Override
    public int getRegisterAddress() 
    {
        return registerAddress;
    }
}