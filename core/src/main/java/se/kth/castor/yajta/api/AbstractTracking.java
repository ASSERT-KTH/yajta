package se.kth.castor.yajta.api;

import se.kth.castor.yajta.processor.util.Dico;

public abstract class AbstractTracking implements Tracking {
	public Dico threads = new Dico();
	public Dico classes = new Dico();
	public Dico methods = new Dico();
}
