import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.slave.ModbusSlave;
import com.ghgande.j2mod.modbus.slave.ModbusSlaveFactory;
import emulator.RegisterMemory;

public class ModbusServer 
{
    private final int port;
    private final RegisterMemory registerMemory;
    private ModbusSlave slave;

    public ModbusServer(int p, RegisterMemory rm) 
    {
        port = p;
        registerMemory = rm;
    }

    public void start() throws ModbusException
    {

        slave = ModbusSlaveFactory.createTCPSlave(port, 10, false);
        slave.addProcessImage(1, registerMemory.getProcessImage());
        slave.open();
        System.out.printf("Modbus TCP server started on port %d%n", port);
    }

    public void stop() 
    {
        if (slave == null)
            return;

        slave.close();

        System.out.println("Modbus TCP server stopped");
    }
}