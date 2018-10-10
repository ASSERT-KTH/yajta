package fr.inria.yajta.api;

import fr.inria.yajta.processor.util.Dico;

public abstract class AbstractTracking implements Tracking {
	public Dico threads = new Dico();
	public Dico classes = new Dico();
	public Dico methods = new Dico();
}
