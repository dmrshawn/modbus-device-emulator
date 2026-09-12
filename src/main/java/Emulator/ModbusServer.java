import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.procimg.SimpleProcessImage;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;
import com.ghgande.j2mod.modbus.slave.ModbusSlave;
import com.ghgande.j2mod.modbus.slave.ModbusSlaveFactory;

public class ModbusServer 
{
    private final int port;
    private final SimpleProcessImage processImage;
    private ModbusSlave slave;

    public ModbusServer(int p, int numberOfRegisters) 
    {
        port = p;

        processImage = new SimpleProcessImage();

        for (int i = 0; i < numberOfRegisters; i++)
            processImage.addRegister(new SimpleRegister(0));
    }

    public void start() throws ModbusException
    {

        // Create the Modbus TCP slave/server
        slave = ModbusSlaveFactory.createTCPSlave(port, 10, false);

        // Give the slave its register memory
        slave.addProcessImage(1, processImage);

        // Start listening for Modbus TCP clients
        slave.open();

        System.out.printf("Modbus TCP server started on port %d%n", port);
    }

    public void stop() 
    {
        if (slave == null) 
            return;
        
        slave.close();

        System.out.println("Modbus TCP server stopped.");
    }

    
    public void setHoldingRegister(int address, int value) 
    {
        checkRegisterAddress(address);

        processImage.getRegister(address).setValue(value);
    }

    
    public int getHoldingRegister(int address) 
    {
        checkRegisterAddress(address);

        return processImage.getRegister(address).getValue();
    }

    private void checkRegisterAddress(int address)
    {
        if (address < 0 || address >= processImage.getRegisterCount()) 
        {
            throw new IllegalArgumentException("Invalid register address: " + address);
        }
    }
}
