package org.example;

import org.example.config.AppConfig;
import org.example.factory.AnotionApplicationContext;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        AnotionApplicationContext applicationContext=new AnotionApplicationContext(AppConfig.class);
        Object o=applicationContext.getBean("aservice");
        System.out.println();
    }
}
