import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SimpleAsyncTest {

    @Test
    public void whenNormal() throws ExecutionException, InterruptedException {
        RedisClient client = RedisClient.create("redis://localhost");
        StatefulRedisConnection<String, String> connection = client.connect();
        RedisAsyncCommands<String, String> commands = connection.async();
        String key = "hello";
        String value = "world";
        commands.set(key, value)
                .thenComposeAsync(x -> commands.get(key))
                .thenAccept(x -> assertEquals(x, value))
                .thenComposeAsync(x -> commands.del(key))
                .toCompletableFuture()
                .get();
        connection.close();
        client.close();
    }

}
