package kafx.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputFilter;
import java.io.ObjectInputFilter.Status;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public abstract class FileIO {

	public boolean readFromFile(File file) throws Exception {
		return read(new FileInputStream(file));
	}

	protected final boolean read(InputStream inputStream) throws IOException, ClassNotFoundException {
		ObjectInputStream stream = new ObjectInputStream(inputStream);
		stream.setObjectInputFilter(getInputFilter());
		Data data = (Data) stream.readObject();
		stream.close();
		return handleData(data);
	}

	public List<Class<?>> getPermittedSerializableClasses() {
		return new ArrayList<>(Arrays.asList(new Class<?>[] { Data.class }));
	}

	public List<Class<?>> getPermittedDataObjectClasses() {
		return new ArrayList<>();
	}

	public List<String> getPermittedJavaPackages() {
		return new ArrayList<>(Arrays.asList(new String[] { "java.math", "java.lang", "java.time", "java.util" }));
	}

	public final ObjectInputFilter getInputFilter() {
		Predicate<Class<?>> classTest = c -> {
			if (this.getPermittedSerializableClasses().contains(c)) {
				return true;
			} else if (this.getPermittedJavaPackages().stream().anyMatch(s -> c.getPackageName().startsWith(s))) {
				return true;
			} else {
				for (Type i : c.getGenericInterfaces()) {
					ParameterizedType p = (ParameterizedType) i;
					if (p.getRawType().equals(DataObject.class)
							&& this.getPermittedDataObjectClasses().contains(p.getActualTypeArguments()[0])) {
						return true;
					}
				}
				return false;
			}
		};
		// TODO this does not support nested arrays
		return ObjectInputFilter.allowFilter(
				c -> c.isArray() ? classTest.test(c.getComponentType()) : classTest.test(c), Status.REJECTED);
	}

	public abstract boolean handleData(Data data);

	public boolean writeToFile(File file) throws Exception {
		return write(new FileOutputStream(file));
	}

	protected final boolean write(OutputStream outputStream) throws IOException {
		ObjectOutputStream stream = new ObjectOutputStream(outputStream);
		Data data = collectData();
		stream.writeObject(data);
		stream.close();
		DataObject.clear();
		return true;
	}

	public abstract Data collectData();

}
