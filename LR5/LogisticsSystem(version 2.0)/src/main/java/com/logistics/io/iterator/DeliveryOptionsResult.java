package com.logistics.io.iterator;

import com.logistics.io.dto.DeliveryOptionResult;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Класс DeliveryOptionsResult — контейнер результата вариантов доставки с обходом через итератор.
 * GoF: Iterator — предоставляет способ последовательного доступа к элементам без раскрытия внутреннего представления списка.
 */

public class DeliveryOptionsResult implements Iterable<DeliveryOptionResult> {

    private final List<DeliveryOptionResult> options;

    public DeliveryOptionsResult(List<DeliveryOptionResult> options) {
        this.options = options != null ? new ArrayList<>(options) : new ArrayList<>();
    }

    public List<DeliveryOptionResult> getOptions() {
        return new ArrayList<>(options);
    }

    public int size() {
        return options.size();
    }

    @Override
    public Iterator<DeliveryOptionResult> iterator() {
        return new DeliveryOptionIterator(options);
    }

    /**
     * Итератор по вариантам доставки — инкапсулирует состояние обхода.
     */
    private static class DeliveryOptionIterator implements Iterator<DeliveryOptionResult> {
        private final List<DeliveryOptionResult> list;
        private int index;

        DeliveryOptionIterator(List<DeliveryOptionResult> list) {
            this.list = list;
            this.index = 0;
        }

        @Override
        public boolean hasNext() {
            return index < list.size();
        }

        @Override
        public DeliveryOptionResult next() {
            return list.get(index++);
        }
    }
}
