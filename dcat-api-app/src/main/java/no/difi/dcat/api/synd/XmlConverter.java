package no.difi.dcat.api.synd;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;

public class XmlConverter {

	private Marshaller marshaller;
	private Unmarshaller unmarshaller;

	public void setMarshaller(Marshaller marshaller) {
		this.marshaller = marshaller;
	}

	public void setUnmarshaller(Unmarshaller unmarshaller) {
		this.unmarshaller = unmarshaller;
	}

	public String doMarshaling(Object graph) throws IOException {
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			doMarshaling(baos, graph);
			return baos.toString();
		} finally {
			baos.close();
		}
	}
	
	public void doMarshaling(OutputStream out, Object graph) throws IOException {
		marshaller.marshal(graph, new StreamResult(out));
	}

	public Object doUnMarshaling(InputStream in) throws IOException {
		try {
			return unmarshaller.unmarshal(new StreamSource(in));
		} finally {
			in.close();
		}
	}
}
