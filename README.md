# blogSystem
我的个人博客系统

## application.properties
```

spring.thymeleaf.mode=LEGACYHTML5

spring.datasource.url=jdbc:mysql://localhost:3306/myblog?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true
spring.datasource.username=用户名
spring.datasource.password=密码
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

multipart.maxFileSize=100Mb
multipart.maxRequestSize=1000Mb
spring.servlet.multipart.max-file-size=100MB
spring.servlet.multipart.max-request-size=1000MB


#http.port=80
server.port = 80
```

## 项目结构
|位置|说明|
|---|---|
|(root)|工程启动文件|
|config|配置信息类|
