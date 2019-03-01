package fr.inria.yajta;

import fr.inria.yajta.api.FastTracking;
import fr.inria.yajta.api.MalformedTrackingClassException;
import fr.inria.yajta.api.Tracking;
import fr.inria.yajta.api.ValueTracking;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.NotFoundException;

import java.lang.instrument.ClassFileTransformer;

public interface TracerI extends ClassFileTransformer {

    void setTrackingClass(Class<? extends Tracking> trackingClass) throws MalformedTrackingClassException;
    void setValueTrackingClass(Class<? extends ValueTracking> trackingClass) throws MalformedTrackingClassException;
    public void setFastTrackingClass(Class<? extends FastTracking> trackingClass) throws MalformedTrackingClassException;

    void doClass(CtClass cl, String name) throws NotFoundException, CannotCompileException;
}
