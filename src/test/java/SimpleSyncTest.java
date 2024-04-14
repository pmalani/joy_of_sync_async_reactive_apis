import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SimpleSyncTest {

    @Test
    public void whenNormal() {
        RedisClient client = RedisClient.create("redis://localhost");
        StatefulRedisConnection<String, String> connection = client.connect();
        RedisCommands<String, String> commands = connection.sync();
        String key = "hello";
        String value = "world";
        commands.set(key, value);
        String ans = commands.get(key);
        assertEquals(value, ans);
        commands.del(key);
        connection.close();
        client.close();
    }

}
