package expand.test.impl;

import expand.test.TestService;
import org.springframework.stereotype.Service;

/**
 * @author huangfu
 */
@Service
public class TestServiceImpl implements TestService {
	@Override
	public void testPrint() {
		System.out.println("测试方法打印....");
	}
}
