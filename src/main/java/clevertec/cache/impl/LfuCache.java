package clevertec.cache.impl;

import clevertec.cache.Cache;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * Реализация стратегии кэширования "Least Frequently Used" (LFU).
 * Этот кэш удаляет элементы, которые используются наименее часто.
 *
 * @param <K> тип ключей, поддерживаемых этим кэшем
 * @param <V> тип значений, хранящихся в кэше
 */
@Slf4j
public class LfuCache<K, V> implements Cache<K, V> {

    private final int capacity;
    private final Map<K, V> mainMap;
    private final Map<K, Integer> freqMap;
    private final TreeMap<Integer, LinkedList<K>> freqList;

    /**
     * Конструктор для создания кэша LFU с заданной вместимостью.
     *
     * @param capacity максимальное количество элементов, которое может хранить кэш
     */
    public LfuCache(int capacity) {
        this.capacity = capacity;
        this.mainMap = new HashMap<>();
        this.freqMap = new HashMap<>();
        this.freqList = new TreeMap<>();
        log.info("LFU Cache initialized with capacity: {}", capacity);
    }

    /**
     * Вставляет или обновляет значение, связанное с указанным ключом.
     * Если кэш заполнен, удаляется элемент, который используется наименее часто.
     *
     * @param key   ключ, с которым связано указанное значение
     * @param value значение, которое должно быть связано с указанным ключом
     */
    @Override
    public void put(K key, V value) {
        if (key == null || value == null) {
            log.warn("Key or value cannot be null");
            return;
        }

        if (mainMap.containsKey(key)) {
            mainMap.put(key, value);
            updateFrequency(key);
        }
        else {
            if (mainMap.size() >= capacity) {
                K leastFreqKey = deleteLeastFrequentKey();
                log.debug("Removed least frequent key: {}", leastFreqKey);
            }
            mainMap.put(key, value);
            freqMap.put(key, 1);
            freqList.computeIfAbsent(1, k -> new LinkedList<>()).add(key);
        }
        log.debug("Key added or updated: {}", key);
    }

    /**
     * Возвращает значение, связанное с указанным ключом, если оно присутствует.
     * <p>
     * Этот метод возвращает {@link Optional} значение, связанное с ключом, если оно
     * присутствует в основной карте. Если ключ является null или значение не найдено,
     * метод возвращает пустой {@link Optional}.
     * <p>
     * Если значение присутствует, метод также обновляет частоту использования ключа.
     *
     * @param key Ключ, по которому будет произведен поиск значения.
     * @return {@link Optional} значение, связанное с ключом. Если ключ является null
     * или значение не найдено, возвращается пустой {@link Optional}.
     */
    @Override
    public Optional<V> get(K key) {
        if (key == null) {
            log.debug("Key is null");
            return Optional.empty();
        }

        V value = mainMap.get(key);
        if (value != null) {
            updateFrequency(key);
            log.debug("Value retrieved for key {}", key);
            return Optional.of(value);
        }
        else {
            log.debug("Key not found: {}", key);
            return Optional.empty();
        }
    }

    /**
     * Удаляет значение для ключа из кэша, если оно присутствует.
     *
     * @param key ключ, значение которого должно быть удалено из кэша
     */
    @Override
    public void delete(K key) {
        if (key != null && mainMap.containsKey(key)) {
            Integer freq = freqMap.remove(key);
            mainMap.remove(key);
            LinkedList<K> keys = freqList.get(freq);
            keys.remove(key);
            if (keys.isEmpty()) {
                freqList.remove(freq);
            }
            log.debug("Key deleted: {}", key);
        }
    }

    private void updateFrequency(K key) {
        Integer freq = freqMap.get(key);
        freqList.get(freq).remove(key);
        if (freqList.get(freq).isEmpty()) {
            freqList.remove(freq);
        }
        freqMap.put(key, freq + 1);
        freqList.computeIfAbsent(freq + 1, k -> new LinkedList<>()).add(key);
    }

    private K deleteLeastFrequentKey() {
        Integer leastFreq = freqList.firstKey();
        K key = freqList.get(leastFreq).removeFirst();
        if (freqList.get(leastFreq).isEmpty()) {
            freqList.remove(leastFreq);
        }
        mainMap.remove(key);
        freqMap.remove(key);
        return key;
    }
}
