package conf;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class Config 
{

    public NetworkConfig network;
    public List<SimulatorSettings> simulators;

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
        
        if (simulators == null || simulators.isEmpty())
            throw new IllegalArgumentException("Missing 'simulators' configuration.");

        validateNetwork();
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

    private void validateSimulators() 
    {
        Set<Integer> usedRegisters = new HashSet<Integer>();
        for(SimulatorSettings simulator : simulators)
        {
            if(simulator == null)
                throw new IllegalArgumentException("Simulator configuration is null");
            
            validateSimulator(simulator);

            if (!usedRegisters.add(simulator.register))
                throw new IllegalArgumentException( "Duplicate register address: " + simulator.register );
        }
    }

    private void validateSimulator( SimulatorSettings simulator) 
    { 
        String name = simulator.name;
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Simulator name cannot be empty.");
        
        if (simulator.type == null || simulator.type.isBlank())
            throw new IllegalArgumentException("Simulator type cannot be empty: " + name);

        if (simulator.register < 0)
            throw new IllegalArgumentException("Simulator register cannot be negative: " + name);
        
        if (simulator.updateFrequencyMs <= 0)
            throw new IllegalArgumentException("Update frequency must be greater than zero: " + name);
        
        if (simulator.min >= simulator.max)
            throw new IllegalArgumentException("Minimum must be less than maximum: " + name);
    }

    public int getRequiredRegisterCount() 
    { 
        int highestAddress = simulators.stream() .mapToInt(simulator -> simulator.register) .max() .orElse(-1); 
        return highestAddress + 1; 
    }


    public static class NetworkConfig 
    {
        public String host;
        public int port;
        public int unitId;
    }

    public static class SimulatorSettings 
    {
        public String name;
        public String type;
        public int register;
        public boolean enabled;
        public long updateFrequencyMs;
        public double min;
        public double max;
    }
}
