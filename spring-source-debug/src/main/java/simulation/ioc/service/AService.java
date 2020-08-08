package simulation.ioc.service;


import simulation.ioc.annotations.MyAutowired;

public class AService {
    @MyAutowired
    private BService bService;
}
