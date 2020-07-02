# spring-oauth2
spring 整合的OAuth2 相关学习

## auth-server
使用如下命令生成keystore,使用的是RSA秘钥算法
```cmd
keytool -genkeypair -alias mytest 
                    -keyalg RSA 
                    -keypass passwd 
                    -keystore jwt-rsa.jks 
                    -storepass passwd
```


