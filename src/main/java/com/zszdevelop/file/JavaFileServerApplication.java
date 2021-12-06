package com.zszdevelop.file;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class JavaFileServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(JavaFileServerApplication.class, args);
		log.info("(♥◠‿◠)ﾉﾞ  项目启动成功");
	}

}
