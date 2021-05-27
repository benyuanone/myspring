package org.example.service;

import org.example.annotion.Autowire;
import org.example.annotion.Component;

@Component("cservice")
public class Cservice {
    @Autowire
    Aservice aservice;
    @Autowire
    Bservice bservice;
}
