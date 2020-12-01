package vn.vnpt.stc.enterprise.event.serialize;


import java.io.IOException;

public interface Serializer<T> {
    
    T deSerialize(Class type, String value) throws IOException;
    String serialize(T value) throws Exception;
    
    // Used if the field is a collection
    T deSerializeItem(String value) throws IOException;
    String serializeItem(T value) throws Exception;
}
