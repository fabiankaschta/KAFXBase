package kafx.io;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Data implements Serializable {

	private static final long serialVersionUID = 1212549241575978289L;

	private final Map<String, Serializable> serializables = new HashMap<>();

	public void put(String id, Serializable value) {
		this.serializables.put(id, value);
	}

	public Serializable get(String id) {
		return this.serializables.get(id);
	}

}
