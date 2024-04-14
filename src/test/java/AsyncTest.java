import io.lettuce.core.*;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AsyncTest {

    public static final int MAX = 10;
    private RedisClient client;
    private StatefulRedisConnection<String, String> connection;
    private RedisAsyncCommands<String, String> commands;

    @BeforeEach
    public void init() {
        client = RedisClient.create("redis://localhost");
        connection = client.connect();
        commands = connection.async();
        List<RedisFuture<String>> futures = IntStream.range(0, MAX)
                .mapToObj(id -> commands.set(customerId(id), String.valueOf(id)))
                .collect(Collectors.toList());
        LettuceFutures.awaitAll(Duration.ofSeconds(10), futures.toArray(new RedisFuture[0]));
    }

    @Test
    public void whenNormal() throws ExecutionException, InterruptedException {
        AtomicInteger count = new AtomicInteger();
        ScanArgs scanArgs = ScanArgs.Builder.matches(customerId("*"));
        RedisFuture<KeyScanCursor<String>> future = commands.scan(scanArgs);
        future.thenCompose(cursor -> process(cursor, scanArgs, count))
                .toCompletableFuture()
                .get();
        assertEquals(MAX,  count.get());
    }

    CompletionStage<KeyScanCursor<String>> process(
            KeyScanCursor<String> cursor, ScanArgs scanArgs, AtomicInteger count) {
        List<String> keys = cursor.getKeys();
        System.out.println(keys);
        count.addAndGet(keys.size());
        if (cursor.isFinished()) {
            return CompletableFuture.completedFuture(cursor);
        }
        return commands.scan(cursor, scanArgs).thenComposeAsync(next -> process(next, scanArgs, count));
    }

    @AfterEach
    public void close() {
        RedisFuture[] futures = IntStream.range(0, MAX)
                .mapToObj(id -> customerId(id))
                .map(commands::unlink)
                .toArray(RedisFuture[]::new);
        LettuceFutures.awaitAll(Duration.ofSeconds(10), futures);
        connection.close();
        client.close();
    }

    String customerId(Object id) {
        return "customer:" + id;
    }

}
