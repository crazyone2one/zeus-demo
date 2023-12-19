package cn.master.zeus.util;

import cn.master.zeus.common.constants.StatusReference;
import cn.master.zeus.dto.response.DetailColumn;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Created by 11's papa on 12/19/2023
 **/
@Slf4j
public class ReflexObjectUtil {
    public static List<DetailColumn> getColumns(Object obj, Map<String, String> columns) {
        List<DetailColumn> columnList = new LinkedList<>();
        if (obj == null) {
            return columnList;
        }
        String dffValue = columns.get("ms-dff-col");
        List<String> dffColumns = new LinkedList<>();
        if (StringUtils.isNotEmpty(dffValue)) {
            dffColumns = Arrays.asList(dffValue.split(","));
        }
        // 得到类中的所有属性集合
        List<Field[]> fields = getFields(obj);
        try {
            for (Field[] fs : fields) {
                for (int i = 0; i < fs.length; i++) {
                    Field f = fs[i];
                    f.setAccessible(true);
                    if (columns.containsKey(f.getName())) {
                        Object val = f.get(obj);
                        if (StatusReference.statusMap.containsKey(String.valueOf(val))) {
                            val = StatusReference.statusMap.get(String.valueOf(val));
                        }
                        DetailColumn column = new DetailColumn(columns.get(f.getName()), f.getName(), val, StringUtils.EMPTY);
                        if (dffColumns.contains(f.getName())) {
                            column.setDepthDff(true);
                            column.setOriginalValue(JsonUtils.parseObject((String) val, Map.class));
                        }
                        columnList.add(column);
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        List<String> keys = columns.keySet().stream().collect(Collectors.toList());
        ReflexObjectUtil.order(keys, columnList);
        return columnList;
    }

    public static void order(List<String> orderRegulation, List<DetailColumn> targetList) {
        targetList.sort(((o1, o2) -> {
            int io1 = orderRegulation.indexOf(o1.getColumnName());
            int io2 = orderRegulation.indexOf(o2.getColumnName());
            return io1 - io2;
        }));
    }

    static List<Field[]> getFields(Object obj) {
        Class clazz = obj.getClass();
        // 得到类中的所有属性集合
        List<Field[]> fields = new LinkedList<>();
        // 遍历所有父类字节码对象
        while (clazz != null) {
            // 获取字节码对象的属性对象数组
            Field[] declaredFields = clazz.getDeclaredFields();
            fields.add(declaredFields);
            // 获得父类的字节码对象
            clazz = clazz.getSuperclass();
        }
        return fields;
    }
}
