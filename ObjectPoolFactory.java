import java.io.FileInputStream;
import java.util.*;

public class ObjectPoolFactory
{
    private Map<String, Object> objectPool = new HashMap<>();

    private Object createObject(String clazzName) throws Exception,
                                                        IllegalAccessException,
                                                        ClassNotFoundException
    {
        Class<?> clazz = Class.forName(clazzName);
        return clazz.getConstructor().newInstance();
    }

    public void initPool(String fileName) throws InstantiationException,
                                                 IllegalAccessException,
                                                 ClassNotFoundException
    {
        try (var fis = new FileInputStream(fileName))
        {
            var props = new Properties();
            props.load(fis);
            for (String name : props.stringPropertyNames())
            {
                objectPool.put(name, createObject(props.getProperty(name)));
            }
        }
        catch (Exception ex)
        {
            System.out.println("Error reading " + fileName);
        }
    }                                                 
    public Object getObject(String name)
    {
        return objectPool.get(name);
    }
    public static void main(String[] args) throws Exception
    {
        var pf = new ObjectPoolFactory();
        pf.initPool("obj.txt");
        System.out.println(pf.getObject("a"));
        System.out.println(pf.getObject("b"));
    }
}