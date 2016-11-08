Chatango Java API 2.0
=================
Chatango-Java-API is a library written for simplified interaction with the chat service Chatango.com  
Version 2.0 aims to provide a simplistic method for communicating with Chatango while maintaining a professional architecture.  
It is designed to follow modern api design architecture.

### Features
- Room and PM supported
- Fully WebSocket based
- Simplistic yet powerful configuration
- Multi-threaded
- Designed for fans of both Blocking & non-blocking API's
- Dependency Injection (through Guice)
- So beautiful it will make you cry

### Installation
To install Chatango-Java-API v2.0, I suggest you use maven.  
```xml
<dependency>
    <groupId>com.lenis0012.chatango</groupId>
    <artifactId>chatango-java-api</artifactId>
    <version>2.0-SNAPSHOT</version>
    <scope>system</scope>
    <systemPath>${project.baseDir}/lib/chatango-java-api.jar</systemPath>
</dependency>
```
As version 2.0 is still under development, you will have to compile it yourself using `mvn clean install`

### Usage
To be made...

### Contribute
1. Fork repository from github
2. Clone repository from git `git clone git@{your_repo}/chatango-java-api.git`
3. Get the development branch `git checkout origin/develop`  
4. _Optional:_ Setup git flow `git flow init -d` (note: requires [git flow](https://github.com/nvie/gitflow) to be installed.)
5. Make your changes and `git commit -m {description}`
6. Push your changes and make a PR `git push origin/develop`

Note: I prefer it when you use git flow and put your changes in a feature/hotifx branch, but it's not required.