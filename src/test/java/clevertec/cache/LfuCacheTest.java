package clevertec.cache;

import clevertec.cache.impl.LfuCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LfuCacheTest {

    private LfuCache<Integer, String> cache;

    @BeforeEach
    void setUp() {
        cache = new LfuCache<>(2);
    }

    @Test
    void testPutAndGet() {
        cache.put(1, "One");
        cache.put(2, "Two");

        assertAll("Verify put and get",
                () -> assertEquals(Optional.of("One"), cache.get(1)),
                () -> assertEquals(Optional.of("Two"), cache.get(2))
        );
    }

    @Test
    void testEvictionPolicy() {
        cache.put(1, "One");
        cache.put(2, "Two");
        cache.get(1);
        cache.put(3, "Three");

        assertAll("Verify eviction policy",
                () -> assertEquals(Optional.empty(), cache.get(2)),
                () -> assertEquals(Optional.of("One"), cache.get(1)),
                () -> assertEquals(Optional.of("Three"), cache.get(3))
        );
    }

    @Test
    void testDelete() {
        cache.put(1, "One");
        cache.delete(1);

        assertEquals(Optional.empty(), cache.get(1), "Key 1 should be deleted");
    }

    @Test
    void testCapacity() {
        cache.put(1, "One");
        cache.put(2, "Two");
        cache.put(3, "Three");

        assertEquals(Optional.empty(), cache.get(1), "Key 1 should be evicted due to capacity limits");
    }

    @Test
    void testGetNonExistentKey() {
        assertEquals(Optional.empty(), cache.get(99), "Getting a non-existent key should return empty Optional");
    }

    @Test
    void testLeastFrequentUsedEviction() {
        cache.put(1, "One");
        cache.put(2, "Two");
        cache.get(1);
        cache.get(1);
        cache.get(2);
        cache.get(2);
        cache.put(3, "Three");

        assertAll("Verify LFU eviction",
                () -> assertEquals(Optional.empty(), cache.get(1)),
                () -> assertEquals(Optional.of("Two"), cache.get(2)),
                () -> assertEquals(Optional.of("Three"), cache.get(3))
        );
    }
}
