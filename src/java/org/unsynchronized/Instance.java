package org.unsynchronized;
import java.io.*;
import java.util.*;

/**
 * Represents an instance of a non-enum, non-Class, non-ObjectStreamClass, 
 * non-array class, including the non-transient field values, for all classes in its
 * hierarchy and inner classes.
 */
public class Instance extends ContentBase {
    /**
     * Collection of field data, organized by class description.  
     */
    public Map<ClassDesc, Map<Field, Object>> fielddata;

    /**
     * Class description for this instance.
     */
    public ClassDesc classdesc;

    /**
     * Constructor.
     */
    public Instance() {
        super(ContentType.INSTANCE);
        this.fielddata = new HashMap<ClassDesc, Map<Field, Object>>();
    }
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(classdesc.name).append(' ').append("_h").append(JDeserialize.hex(handle))
            .append(" = r_").append(JDeserialize.hex(classdesc.handle)).append(";  ");
        //sb.append("// [instance " + jdeserialize.hex(handle) + ": " + jdeserialize.hex(classdesc.handle) + "/" + classdesc.name).append("]");
        return sb.toString();
    }
    /**
     * Object annotation data.
     */
    public Map<ClassDesc, List<Content>> annotations;
}
