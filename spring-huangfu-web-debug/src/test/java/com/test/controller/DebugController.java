package com.test.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import web.mvc.entity.User;

@Controller
@RequestMapping("test")
public class DebugController {
	@RequestMapping("user")
	public ModelAndView modelAndView(Integer id, String name){
		System.out.println("-----被调用------");
		ModelAndView modelAndView = new ModelAndView("/userView");
		User user = new User(id, name);
		modelAndView.addObject("user",user);
		return modelAndView;
	}
}