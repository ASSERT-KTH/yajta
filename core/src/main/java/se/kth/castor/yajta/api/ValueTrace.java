package se.kth.castor.yajta.api;

import se.kth.castor.yajta.processor.util.MyList;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ValueTrace {
    public static boolean base64 = true;

    String clazz;
    String method;
    Object[] parameter;
    Object returnValue;
    ValueTrace parent;
    MyList<ValueTrace> children;

    public String getClazz() {
        return clazz;
    }

    public String getMethod() {
        return method;
    }

    public Object[] getParameter() {
        return parameter;
    }

    public Object getReturnValue() {
        return returnValue;
    }

    public MyList<ValueTrace> getChildren() {
        return children;
    }

    public static void setBase64(boolean base64) {
        ValueTrace.base64 = base64;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setParameter(Object[] parameter) {
        this.parameter = parameter;
    }

    public void setParent(ValueTrace parent) {
        this.parent = parent;
    }

    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
    }

    public ValueTrace getParent() {
        return parent;
    }

    public ValueTrace() {}

    public static ValueTrace getThreadDeclaration(String name) {
        ValueTrace root = new ValueTrace();
        root.clazz = "Thread";
        root.method = name;
        return root;
    }

    public ValueTrace addChild( String clazz, String method, Object[] parameter) {
        ValueTrace t = new ValueTrace();
        t.clazz = clazz;
        t.method = method;
        t.parameter = parameter;
        t.parent = this;
        if(children == null) children = new MyList<>();
        children.add(t);
        return t;
    }

    public static byte[] serialize(Serializable o) throws IOException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(baos);
            os.writeObject(o);
            os.flush();
            os.close();
            return baos.toByteArray();
        } catch (NotSerializableException e) {}
        return new byte[0];
    }

    String writeObject(Object o) {
        if(o == null) return "null";

        String retV = null;
        if(base64) {
            if(o instanceof Serializable) {
                Serializable serializable = (Serializable) o;
                try {
                    retV = java.util.Base64.getEncoder().encodeToString(serialize(serializable));
                } catch (IOException e) {}

            }
            if(retV == null) {
                retV = "UnSerializable";
            }
        } else {
            if(o instanceof String) {
                return (String) o;
            }
            retV = o.toString();
        }
        return retV;
    }

    public void print(BufferedWriter b) throws IOException {
        b.append("{\"class\":\"" + clazz + "\", \"method\":\"" + method + "\",");

        b.append(" \"parameters\":[");
        if(parameter != null) {
            for (int i = 0; i < parameter.length; i++) {
                if(i!=0) b.append(", ");
                b.append("\"" + writeObject(parameter[i]) + "\"");
            }
        }
        b.append("],");
        b.append(" \"returnValue\": \"" + writeObject(returnValue) + "\",");

        b.append(" \"children\":[");
        if(children != null) {
            boolean isFirst = true;
            for (ValueTrace t : children) {
                if (isFirst) isFirst = false;
                else b.append(",");
                t.print(b);
            }
        }
        b.append("]}");
    }
}
