package gift.common.util;

import gift.common.code.CustomResponseCode;
import gift.common.exception.CustomException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.data.domain.Sort;

public class SortUtil {

    private static final String SORT_DELIMITER = ";";
    private static final String DIRECTION_ASC = "asc";
    private static final String DIRECTION_DESC = "desc";
    private static final String DEFAULT_SORT_FIELD = "id";

    public static Sort createSort(List<String> sortParams, Set<String> allowedFields) {
        Map<String, String> sortMap = getSortMap(sortParams, allowedFields);

        List<Sort.Order> orders = new ArrayList<>();
        for (Map.Entry<String, String> entry : sortMap.entrySet()) {
            orders.add(createOrder(entry));
        }

        if (!sortMap.containsKey(DEFAULT_SORT_FIELD)) {
            orders.add(new Sort.Order(Sort.Direction.DESC, DEFAULT_SORT_FIELD));
        }

        return Sort.by(orders);
    }

    private static Map<String, String> getSortMap(List<String> sortParams,
        Set<String> allowedFields) {
        Map<String, String> sortMap = new LinkedHashMap<>();

        if (sortParams == null || sortParams.isEmpty()) {
            return sortMap;
        }

        for (String sortParam : sortParams) {
            getSortParam(sortMap, sortParam, allowedFields);
        }

        return sortMap;
    }

    private static void getSortParam(Map<String, String> sortMap, String sortParam,
        Set<String> allowedFields) {
        String[] params = sortParam.split(SORT_DELIMITER);

        if (params.length < 2) {
            throw new CustomException(CustomResponseCode.VALIDATION_FAILED);
        }

        String property = params[0];
        validateSortField(property, allowedFields);

        String direction = params[1].toLowerCase();
        validateSortDirection(direction);

        sortMap.put(property, direction);
    }

    private static void validateSortField(String field, Set<String> allowedFields) {
        if (!allowedFields.contains(field)) {
            throw new CustomException(CustomResponseCode.INVALID_SORT_FIELD);
        }
    }

    private static void validateSortDirection(String direction) {
        if (!DIRECTION_ASC.equals(direction) && !DIRECTION_DESC.equals(direction)) {
            throw new CustomException(CustomResponseCode.INVALID_SORT_DIRECTION);
        }
    }

    private static Sort.Order createOrder(Map.Entry<String, String> entry) {
        String property = entry.getKey();
        Sort.Direction direction = Sort.Direction.fromString(entry.getValue());
        return new Sort.Order(direction, property);
    }
}
