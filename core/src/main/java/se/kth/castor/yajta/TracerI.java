package se.kth.castor.yajta;

import se.kth.castor.yajta.api.FastTracking;
import se.kth.castor.yajta.api.MalformedTrackingClassException;
import se.kth.castor.yajta.api.Tracking;
import se.kth.castor.yajta.api.ValueTracking;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.NotFoundException;

import java.lang.instrument.ClassFileTransformer;

public interface TracerI extends ClassFileTransformer {

    void setTrackingClass(Class<? extends Tracking> trackingClass) throws MalformedTrackingClassException;
    void setValueTrackingClass(Class<? extends ValueTracking> trackingClass) throws MalformedTrackingClassException;
    void setFastTrackingClass(Class<? extends FastTracking> trackingClass) throws MalformedTrackingClassException;

    void doClass(CtClass cl, String name) throws NotFoundException, CannotCompileException;
}
