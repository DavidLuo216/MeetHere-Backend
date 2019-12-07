package cn.ecnuer996.meetHereBackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cn.ecnuer996.meetHereBackend.dao")
public class MeetHereBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(MeetHereBackendApplication.class, args);
    }

}
