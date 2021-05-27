package org.example.service;

import org.example.annotion.Autowire;
import org.example.annotion.Component;
import org.example.config.BeanInit;

@Component("aservice")
public class Aservice implements BeanInit {
    @Autowire
    Bservice bservice;

    @Autowire
    Cservice cservice;

    @Override
    public void beanInit() {

    }
}
