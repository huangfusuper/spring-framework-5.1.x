package web.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * *********************************************************************
 * TODO
 * *********************************************************************
 *
 * @author huangfu
 * @date 2020/10/24 14:51
 */
@RestController
public class TestController {
	@RequestMapping("test/")
	public String test(){
		return "test";
	}
}
