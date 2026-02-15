package com.logistics.io.filter;

import com.logistics.io.dto.DeliveryOptionResult;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Класс ByTransportNameFilter — фильтр по названию транспорта (точное совпадение или по регулярному выражению).
 * GoF: Strategy — конкретная стратегия фильтрации.
 */

public class ByTransportNameFilter implements FilterStrategy {

    private final Pattern pattern;

    public ByTransportNameFilter(String nameOrRegex) {
        this.pattern = Pattern.compile(Objects.requireNonNull(nameOrRegex, "nameOrRegex"));
    }

    public static ByTransportNameFilter exact(String name) {
        return new ByTransportNameFilter(Pattern.quote(Objects.requireNonNull(name)));
    }

    @Override
    public boolean test(DeliveryOptionResult option) {
        return option != null && pattern.matcher(option.getTransportName() != null ? option.getTransportName() : "").find();
    }
}
