package emulation;

import com.ghgande.j2mod.modbus.procimg.*;

public class RegisterMemory 
{

    private final SimpleProcessImage processImage;

    public RegisterMemory(int numberOfRegisters) 
    {

        if (numberOfRegisters <= 0)
            throw new IllegalArgumentException("Number of registers must be greater than zero.");

        processImage = new SimpleProcessImage();

        for (int i = 0; i < numberOfRegisters; i++)
            processImage.addRegister(new SimpleRegister(0));
    }
    
    public SimpleProcessImage getProcessImage() 
    {
        return processImage;
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

    public int getRegisterCount() 
    {
        return processImage.getRegisterCount();
    }

    private void checkRegisterAddress(int address) 
    {
        if (address < 0 || address >= processImage.getRegisterCount()) 
            throw new IllegalArgumentException("Invalid register address: " + address);
    }
}
