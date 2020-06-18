package cn.piesat.datastatisservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import springfox.documentation.swagger2.annotations.EnableSwagger2;



/**
 * @author LX
 * @date 2019/10/28 14:13
 * @ Description：
 */
@EnableScheduling
@SpringBootApplication
@ComponentScan(basePackages = {"cn.piesat"})
public class DataStatisServiceApplication implements WebMvcConfigurer{
	
	public static void main(String[] args) {
		SpringApplication.run(DataStatisServiceApplication.class, args);
	}

	
}
