package cn.master.zeus.dto.response;

import lombok.Data;

import java.util.UUID;

/**
 * @author Created by 11's papa on 12/19/2023
 **/
@Data
public class DetailColumn {
    private String id;
    private boolean depthDff;
    private String columnTitle;
    private String columnName;
    private Object originalValue;
    private Object newValue;
    private Object diffValue;

    public DetailColumn() {

    }

    public DetailColumn(String columnTitle, String columnName, Object originalValue, Object newValue) {
        this.id = UUID.randomUUID().toString();
        this.columnTitle = columnTitle;
        this.columnName = columnName;
        this.originalValue = originalValue;
        this.newValue = newValue;
    }
}
