package com.mvc.springdatajdbc.util;

import com.mvc.springdatajdbc.annotation.Sortable;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SortableFieldUtil {
    private static final Map<Class<?>, SortableInfo> CACHE = new ConcurrentHashMap<>();

    @Getter
    public static class SortableInfo {
        private final Set<String> fieldNames;
        private final Map<String, String> columnMapping;
        private final String defaultSortField;
        private final Set<String> allowedColumns;

        public SortableInfo(Set<String> fieldNames,
                            Map<String, String> columnMapping,
                            String defaultSortField) {
            this.fieldNames = Collections.unmodifiableSet(new LinkedHashSet<>(fieldNames));
            this.columnMapping = Map.copyOf(columnMapping);
            this.defaultSortField = defaultSortField;
            this.allowedColumns = Set.copyOf(columnMapping.values());
        }

        public String getColumnName(String fieldName) {
            if (fieldName == null) {
                return defaultSortField != null ? columnMapping.getOrDefault(defaultSortField, defaultSortField) : "id";
            }
            return columnMapping.getOrDefault(fieldName, fieldName);
        }

        public boolean isSortable(String fieldName) {
            return fieldName != null && fieldNames.contains(fieldName);
        }
    }

    public static SortableInfo getSortableInfo(Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class не может быть null");
        }
        return CACHE.computeIfAbsent(clazz, SortableFieldUtil::extractSortableInfo);
    }

    private static SortableInfo extractSortableInfo(Class<?> clazz) {
        log.debug("Извлечение информации о сортируемых полях для класса: {}", clazz.getSimpleName());

        Set<String> fieldNames = new LinkedHashSet<>();
        Map<String, String> columnMapping = new HashMap<>();
        String defaultSortField = null;
        int highestPriority = -1;

        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            Sortable sortable = field.getAnnotation(Sortable.class);
            if (sortable != null && sortable.enabled()) {
                String fieldName = field.getName();
                fieldNames.add(fieldName);

                String columnName = sortable.columnName().isEmpty()
                        ? fieldName
                        : sortable.columnName();
                columnMapping.put(fieldName, columnName);

                log.debug("Найдено сортируемое поле: {} -> {} (priority={})",
                        fieldName, columnName, sortable.priority());

                if (sortable.priority() > highestPriority) {
                    highestPriority = sortable.priority();
                    defaultSortField = fieldName;
                }
            }
        }

        if (fieldNames.isEmpty()) {
            throw new IllegalStateException(
                    String.format("В классе %s не найдено ни одного поля с аннотацией @Sortable. " +
                                    "Добавьте аннотацию @Sortable к полям, по которым должна быть возможна сортировка.",
                            clazz.getSimpleName())
            );
        }

        if (defaultSortField == null) {
            defaultSortField = fieldNames.iterator().next();
            log.debug("Поле по умолчанию не указано (нет priority), используем: {}", defaultSortField);
        }

        log.info("Инициализирована информация о сортировке для {}: поля={}, по умолчанию={}",
                clazz.getSimpleName(), fieldNames, defaultSortField);

        return new SortableInfo(fieldNames, columnMapping, defaultSortField);
    }

    public static void clearCache() {
        CACHE.clear();
        log.debug("Кэш SortableFieldUtil очищен");
    }

    public static int getCacheSize() {
        return CACHE.size();
    }
}
