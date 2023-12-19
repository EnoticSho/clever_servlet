package clevertec.cache.impl;

import clevertec.cache.Cache;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Реализация кэша, использующего стратегию "Least Recently Used" (LRU).
 * В этой стратегии удаляются элементы, к которым обращались давнее всего.
 *
 * @param <K> тип ключей, поддерживаемых этим кэшем
 * @param <V> тип значений, хранящихся в кэше
 */
@Slf4j
public class LruCache<K, V> implements Cache<K, V> {
    private final int capacity;
    private final Map<K, V> map;
    private final List<K> linkedList;

    /**
     * Конструктор для создания кэша LRU с заданной вместимостью.
     *
     * @param capacity максимальное количество элементов, которое может хранить кэш
     */
    public LruCache(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();
        this.linkedList = new LinkedList<>();
        log.info("LRU Cache initialized with capacity: {}", capacity);
    }

    /**
     * Вставляет или обновляет значение, связанное с указанным ключом.
     * Если кэш заполнен, удаляется элемент, который использовался давно.
     *
     * @param key   ключ, с которым связано указанное значение
     * @param value значение, которое должно быть связано с указанным ключом
     */
    @Override
    public void put(K key, V value) {
        if (map.containsKey(key)) {
            linkedList.remove(key);
        }
        else if (map.size() == capacity) {
            K last = linkedList.removeLast();
            map.remove(last);
        }
        linkedList.addFirst(key);
        map.put(key, value);
        log.debug("Added new key: {}", key);
    }

    /**
     * Возвращает {@link Optional} значение, связанное с указанным ключом.
     * Если ключ не найден, возвращается пустой {@link Optional}.
     *
     * @param key ключ, значение которого нужно вернуть
     * @return {@link Optional} значение, связанное с указанным ключом
     */
    @Override
    public Optional<V> get(K key) {
        if (!map.containsKey(key)) {
            log.debug("Key not found: {}", key);
            return Optional.empty();
        }
        linkedList.remove(key);
        linkedList.addFirst(key);
        log.debug("Retrieved key: {}", key);
        return Optional.ofNullable(map.get(key));
    }

    /**
     * Удаляет значение для ключа из кэша, если оно присутствует.
     *
     * @param key ключ, значение которого должно быть удалено из кэша
     */
    @Override
    public void delete(K key) {
        if (map.containsKey(key)) {
            map.remove(key);
            linkedList.remove(key);
            log.debug("Deleted key: {}", key);
        }
    }
}
