package simulation.service;


import simulation.annotations.MyAutowired;

public class AService {
    @MyAutowired
    private BService bService;
}
