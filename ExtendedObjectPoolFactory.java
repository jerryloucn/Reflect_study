import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.io.*;

public class ExtendedObjectPoolFactory
{
    private Map<String, Object> objectPool = new HashMap<>();
    private Properties config = new Properties();
    public void init(String fileName)
    {
        try (var fis = new FileInputStream(fileName))
        {
            config.load(fis);
        }
        catch (IOException ex)
        {
            System.out.println("Error reading " + fileName);
        }
    }

    private Object createObject(String clazzName)
                    throws Exception
    {
        Class<?> clazz = Class.forName(clazzName);
        return clazz.getConstructor().newInstance();
    }

    public void initPool() throws Exception
    {
        for (var name : config.stringPropertyNames())
        {
            if (!name.contains("%"))
            {
                objectPool.put(name, createObject(config.getProperty(name)));
            }
        }
    }

    public void initProperty() throws InvocationTargetException, 
                                    IllegalAccessException, NoSuchMethodException
    {
        for (var name : config.stringPropertyNames())
        {
            if (name.contains("%"))
            {
                String[] objAndProp = name.split("%");
                Object target = getObject(objAndProp[0]);
                String mtdName = "set" + objAndProp[1].substring(0,1).toUpperCase()
                                        + objAndProp[1].substring(1);
                Class<?> targetClass = target.getClass();
                Method mtd = targetClass.getMethod(mtdName, String.class);
                mtd.invoke(target, config.getProperty(name));
            }
        }
    }

    public Object getObject(String name)
    {
        return objectPool.get(name);
    }

    public static void main(String[] args)
    {
        var epf = new ExtendedObjectPoolFactory();
        epf.init("extObj.txt");
        try {
            epf.initPool();
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            epf.initProperty();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(epf.getObject("a"));
    }
}