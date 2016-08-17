package cryodex.xml;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XMLUtils {

	public static final String PLAYERS = "PLAYERS";
	public static final String PLAYER = "PLAYER";
	public static final String TOURNAMENTS = "TOURNAMENTS";
	public static final String TOURNAMENT = "TOURNAMENT";
	public static final String OPTIONS = "OPTIONS";

	public static StringBuilder appendObject(StringBuilder sb, String label,
			Object object) {

		if (object == null) {
			return sb;
		}

		sb.append("<" + label + ">");

		sb.append(object.toString());

		sb.append("</" + label + ">\n");

		return sb;
	}

	public static StringBuilder appendXMLObject(StringBuilder sb, String label,
			XMLObject object) {

		if (object == null) {
			return sb;
		}

		sb.append("<" + label + ">\n");

		object.appendXML(sb);

		sb.append("</" + label + ">\n");

		return sb;
	}

	public static StringBuilder appendList(StringBuilder sb, String listLabel,
			String label, List<? extends XMLObject> objectList) {

		sb.append("<" + listLabel + ">\n");
		for (XMLObject object : objectList) {
			sb.append("<" + label + ">\n");

			object.appendXML(sb);

			sb.append("</" + label + ">\n");
		}
		sb.append("</" + listLabel + ">\n");
		return sb;
	}

	public static Element getItem(BufferedReader r) throws IOException {
		String line = r.readLine();

		if (line == null || line.trim().isEmpty()) {
			return null;
		} else {
			line = line.trim();
			if (line.matches("<.*>.*</.*>")) {
				return new Element(r, line, true);
			} else if (line.indexOf("/") == 1) {
				return null;
			} else if (line.matches("<.*>")) {
				return new Element(r, line, false);
			}
		}

		return null;
	}

	public static class Element {

		private final String tag;
		private String data;
		private List<Element> children;

		public Element(BufferedReader r, String tagLine, boolean hasData)
				throws IOException {
			tag = tagLine.substring(1, tagLine.indexOf('>'));
			if (hasData) {
				data = tagLine.substring(tagLine.indexOf('>') + 1,
						tagLine.indexOf("</"));
			} else {
				Element o = getItem(r);

				while (o != null) {
					getChildren().add(o);
					o = getItem(r);
				}
			}
		}

		public List<Element> getChildren() {
			if (children == null) {
				children = new ArrayList<Element>();
			}
			return children;
		}

		public String getTag() {
			return tag;
		}

		public String getData() {
			return data;
		}

		public Element getChild(String tag) {
			for (Element e : getChildren()) {
				if (e.getTag().equals(tag)) {
					return e;
				}
			}
			return null;
		}

		public String getStringFromChild(String tag) {
			Element e = getChild(tag);
			if (e != null) {
				return e.getData();
			}
			return null;
		}

		public boolean getBooleanFromChild(String tag, boolean defaultValue) {
			Boolean value = getBooleanFromChild(tag);

			return value == null ? defaultValue : value;
		}

		public Boolean getBooleanFromChild(String tag) {
			String s = getStringFromChild(tag);

			if (s != null) {
				return new Boolean(s);
			}

			return null;
		}

		public Integer getIntegerFromChild(String tag) {
			String s = getStringFromChild(tag);

			if (s != null) {
				return new Integer(s);
			}

			return null;
		}

		@Override
		public String toString() {
			String s = "Tag: " + getTag();
			s += "\nData: " + data;
			s += "\nChildren: {";
			for (Element e : getChildren()) {
				s += e.getTag() + "(" + e.getData() + "), ";
			}
			return s;
		}
	}

}
