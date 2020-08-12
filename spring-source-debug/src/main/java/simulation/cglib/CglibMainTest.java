package simulation.cglib;

import simulation.cglib.config.AppConfig;
import simulation.cglib.utils.CglibUtil;

public class CglibMainTest {
	public static void main(String[] args) {
		CglibUtil cglibUtil = new CglibUtil(AppConfig.class);
	}
}
