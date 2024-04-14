import io.lettuce.core.KeyScanCursor;
import io.lettuce.core.RedisClient;
import io.lettuce.core.ScanArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReactiveTest {

    public static final int MAX = 10;
    private RedisClient client;
    private StatefulRedisConnection<String, String> connection;
    private RedisReactiveCommands<String, String> commands;

    @BeforeEach
    public void init() {
        client = RedisClient.create("redis://localhost");
        connection = client.connect();
        commands = connection.reactive();
        Flux.range(0, MAX)
                .flatMap(id -> commands.set(customerId(id), String.valueOf(id)))
                .blockLast();
    }

    @Test
    public void whenNormal() {
        ScanArgs scanArgs = ScanArgs.Builder.matches(customerId("*"));
        Mono<KeyScanCursor<String>> mono = commands.scan(scanArgs);
        Integer count = mono.expand(cursor -> cursor.isFinished() ? Mono.empty() : commands.scan(cursor, scanArgs))
                .doOnNext(cursor -> System.out.println(cursor.getKeys()))
                .map(cursor -> cursor.getKeys().size())
                .reduce((a, c) -> a + c)
                .block();
        assertEquals(MAX, count);
    }

    @AfterEach
    public void close() {
        Flux.range(0, MAX)
                .map(id -> customerId(id))
                .flatMap(commands::unlink)
                .blockLast();
        connection.close();
        client.close();
    }

    String customerId(Object id) {
        return "customer:" + id;
    }

}
