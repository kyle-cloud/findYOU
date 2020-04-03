package dao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.alibaba.fastjson.asm.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

public class SQLDateTypeAdapter {
	private final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
    public JsonElement serialize(java.sql.Date src, Type arg1, JsonSerializationContext arg2) {
        String dateFormatAsString = format.format(new java.sql.Date(src.getTime()));
        return new JsonPrimitive(dateFormatAsString);
    }
}
