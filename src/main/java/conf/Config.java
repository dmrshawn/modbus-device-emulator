package conf;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Path;

public class Config 
{

    public NetworkConfig network;
    public RegisterConfig registers;
    public SimulatorConfig simulators;

    public static Config load(String path) throws IOException
    {
        return load(Path.of(path));
    }

    public static Config load(Path path) throws IOException 
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);

        Config config = mapper.readValue(path.toFile(), Config.class);
        config.validate();

        return config;
    }

    public void validate()
    {
        if (network == null)
            throw new IllegalArgumentException("Missing 'network' configuration.");
        
        if (registers == null)
            throw new IllegalArgumentException("Missing 'registers' configuration.");
        
        if (simulators == null)
            throw new IllegalArgumentException("Missing 'simulators' configuration.");

        validateNetwork();
        validateRegisters();
        validateSimulators();
    }

    private void validateNetwork() 
    {
        if (network.port < 1 || network.port > 65535)
            throw new IllegalArgumentException("Network port must be between 1 and 65535.");

        if (network.unitId < 1 || network.unitId > 247)
            throw new IllegalArgumentException("Modbus unit ID must be between 1 and 247.");

        if (network.host == null || network.host.isBlank())
            throw new IllegalArgumentException("Network host cannot be empty.");
    }

    private void validateRegisters() 
    {
        if (registers.temperature < 0)
            throw new IllegalArgumentException("Temperature register address cannot be negative.");
    }

    private void validateSimulators() 
    {
        validateSimulator("temperature", simulators.temperature);
    }

    private void validateSimulator(String name, SimulatorSettings settings) 
    {

        if (settings == null)
            throw new IllegalArgumentException("Missing '" + name + "' simulator configuration.");

        if (settings.updateFrequencyMs <= 0)
            throw new IllegalArgumentException(name + " update frequency must be greater than zero.");

        if (settings.min >= settings.max)
            throw new IllegalArgumentException(name + " minimum must be less than maximum.");
    }


    public static class NetworkConfig 
    {
        public String host;
        public int port;
        public int unitId;
    }

    public static class RegisterConfig 
    {
        //Add for each sim
        public int temperature;
    }

    public static class SimulatorConfig 
    {
        //Add for each sim
        public SimulatorSettings temperature;
    }

    public static class SimulatorSettings 
    {
        public boolean enabled;
        public long updateFrequencyMs;
        public double min;
        public double max;
    }
}
