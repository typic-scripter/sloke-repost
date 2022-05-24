package cc.sleek.client.util;

public interface Labeled {

	String getLabel();

	interface Mutable extends Labeled {
		void setLabel(String label);
	}

}
