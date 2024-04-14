import io.lettuce.core.KeyScanCursor;
import io.lettuce.core.RedisClient;
import io.lettuce.core.ScanArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SyncTest {

    public static final int MAX = 10;
    private RedisClient client;
    private StatefulRedisConnection<String, String> connection;
    private RedisCommands<String, String> commands;

    @BeforeEach
    public void init() {
        client = RedisClient.create("redis://localhost");
        connection = client.connect();
        commands = connection.sync();
        IntStream.range(0, MAX)
                .mapToObj(id -> new Object(){
                    String key = customerId(id);
                    int value = id;
                })
                .forEach(kv -> commands.set(kv.key, String.valueOf(kv.value)));
    }

    @Test
    public void whenNormal() {
        int count = 0;
        ScanArgs scanArgs = ScanArgs.Builder.matches(customerId("*"));
        KeyScanCursor<String> cursor = commands.scan(scanArgs);
        while (true) {
            List<String> keys = cursor.getKeys();
            count += keys.size();
            System.out.println(keys);
            if (cursor.isFinished()) {
                break;
            }
            cursor = commands.scan(cursor, scanArgs);
        }
        assertEquals(MAX, count);
    }

    @AfterEach
    public void close() {
        IntStream.range(0, MAX)
                .mapToObj(id -> customerId(id))
                .forEach(commands::unlink);
        connection.close();
        client.close();
    }

    String customerId(Object id) {
        return "customer:" + id;
    }

}
