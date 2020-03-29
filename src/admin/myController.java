package admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class myController {
	@RequestMapping(value = "/testParam", method = RequestMethod.POST)
	@ResponseBody
	public String testParam(String IMSI){
		System.out.println(11);
	    System.out.println("IMSI: " + IMSI);//控制台输出：name:root，id:1001
	    return "true";
	}
}