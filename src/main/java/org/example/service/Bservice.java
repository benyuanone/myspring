package org.example.service;

import org.example.annotion.Autowire;
import org.example.annotion.Component;
import org.example.annotion.Scope;

@Component("bservice")
public class Bservice {
    @Autowire
    Aservice aservice;
}
