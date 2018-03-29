package com.pomelo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TestController {
	
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView app() {
		ModelAndView mav = new ModelAndView("ok");
		mav.addObject("message", "Hello World!");
		return mav;
	}
	
	@RequestMapping(value = "/test1", method = RequestMethod.GET)
	@ResponseBody
	public Object map() {
		Map<String, String> map = new HashMap<String, String>();
		return map;
	}
}
