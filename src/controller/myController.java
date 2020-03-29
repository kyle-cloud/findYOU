package controller;


import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class myController {
	@RequestMapping("/testParam.do")
	@ResponseBody
	public void testParam(String IMSI, HttpServletResponse response) throws IOException{
		System.out.println(11);
	    System.out.println("IMSI: " + IMSI);//控制台输出：name:root，id:1001
	    response.getWriter().print("true");
	}
}