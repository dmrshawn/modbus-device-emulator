package sim;

public interface SimTemplate 
{
    double generate();
    long getUpdateFrequencyMs();
    int getRegisterAddress();
}