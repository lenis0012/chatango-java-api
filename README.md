Chatango Java API
=================
Chatango-Java-API is a library written for simplified interaction with the chat service Chatango.com  
Version 1.1 aims to allow you to effortlessly retrieve and send data to chatango, providing data models for improved structure.  
As of right now, the API is in the process of being rewritten under the "develop" branch.

### Features
- WebSocket and flash support
- Room & PM's
- Powerful credentials system
- Create new accounts
- Logging
- Models for
  - Badges
  - Channels
  - Fonts
  - Friends
  - Messages
  - Colors
  - Users
- Event-based handler system
- Json config

### Installation
To install Chatango-Java-API v1.1, I suggest you use maven.  
```xml
<dependency>
    <groupId>com.lenis0012.chatango</groupId>
    <artifactId>chatango-java-api</artifactId>
    <version>1.1</version>
    <scope>system</scope>
    <systemPath>${project.baseDir}/lib/chatango-java-api.jar</systemPath>
</dependency>
```
Be sure to download the api to `lib/chatango-java-api.jar`  
The download can be found under the releases tab

### Usage
To connect to a room and interact with it, you should simple call ChatangoAPI.createBot
```java
import com.lenis0012.chatango.bot.engine.Engine;
import com.lenis0012.chatango.bot.ChatangoAPI;
import com.lenis0012.chatango.bot.api.Message;
import java.util.Arrays;

public class MyChatangoApp {
    public void main(String[] args) {
        // Configure
        Engine bot = ChatangoAPI.createBot("username", "password");
        bot.init(Arrays.asList("myroom"));
        
        // Connect
        bot.start();
        
        // Hello world
        Message hello = new Message("Hello world!");
        bot.getRoom("myroom").message(hello);
    }
}
```
Congratulations, you are now connected to chatango.  
Let's make a simple echo client, returning each message being sent.
```java
import com.lenis0012.chatango.bot.engine.Engine;
import com.lenis0012.chatango.bot.ChatangoAPI;
import com.lenis0012.chatango.bot.api.Message;
import com.lenis0012.chatango.bot.api.EventListener;
import com.lenis0012.chatango.bot.events.MessageReceiveEvent;
import java.util.Arrays;

public class MyEchoClient implements EventListener {
    public void setup(Engine engine) {
        ChatangoAPI.getLogger().info("Setting up echo client!");
        engine.getRoom("myroom").getEventManager().addListener(this);
    }
    
    public void onMessage(MessageReceiveEvent event) {
        Message response = new Message(event.getMessage().getText());
        event.getRoom().message(response);
    }
}
```
That's it, simply call EchoClient.setup from your MyChatangoApp class and you are all set.  
For the rest of the features, you'll have to play around and see for yourself.

### Contribute
Switch to "develop" branch for more information