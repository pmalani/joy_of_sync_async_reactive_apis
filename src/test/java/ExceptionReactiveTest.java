import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExceptionReactiveTest {

    @Test
    public void whenOnErrorResume() {
        RedisClient client = RedisClient.create("redis://localhost");
        StatefulRedisConnection<String, String> connection = client.connect();
        RedisReactiveCommands<String, String> commands = connection.reactive();
        String key = "hello";
        String value = "world";
        commands.set(key, value)
                .then(commands.get(key))
                .doOnNext(x -> assertEquals(x, value))
                .then(commands.incr(key))
                .onErrorResume(e -> {
                    System.out.println(e);
                    return Mono.just(0L);
                })
                .then(commands.del(key))
                .block();
        connection.close();
        client.close();
    }

    @Test
    public void whenOnErrorReturn() {
        RedisClient client = RedisClient.create("redis://localhost");
        StatefulRedisConnection<String, String> connection = client.connect();
        RedisReactiveCommands<String, String> commands = connection.reactive();
        String key = "hello";
        String value = "world";
        commands.set(key, value)
                .then(commands.get(key))
                .doOnNext(x -> assertEquals(x, value))
                .then(commands.incr(key))
                .onErrorReturn(0L)
                .then(commands.del(key))
                .block();
        connection.close();
        client.close();
    }

}
