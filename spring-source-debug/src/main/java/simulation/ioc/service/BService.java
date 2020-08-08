package simulation.ioc.service;


import simulation.ioc.annotations.MyAutowired;

public class BService {

    @MyAutowired
    private AService aService;
}
