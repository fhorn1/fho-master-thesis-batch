package org.goafabric.spring.boot.examplebatch.utilities;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Unmarshaller;
import org.goafabric.spring.boot.examplebatch.domain.model.OpsImportedTuple;
import org.goafabric.spring.boot.examplebatch.domain.model.generated.EhdBodyTyp;
import org.goafabric.spring.boot.examplebatch.domain.model.generated.EhdHeaderTyp;
import org.goafabric.spring.boot.examplebatch.domain.model.generated.ObjectFactory;
import org.goafabric.spring.boot.examplebatch.domain.model.generated.OpsRootTyp;
import org.springframework.stereotype.Component;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import java.io.InputStream;

@Component
public class OpsCatalogXMLImporter {

    public OpsImportedTuple importXml(InputStream inputStream) throws Exception {
        JAXBContext jc = JAXBContext.newInstance(OpsRootTyp.class, ObjectFactory.class);

        Unmarshaller unmarshaller = jc.createUnmarshaller();

        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLEventReader someSource = factory.createXMLEventReader(inputStream);

        JAXBElement<OpsRootTyp> rootElement = (JAXBElement<OpsRootTyp>) unmarshaller.unmarshal(someSource);
        OpsRootTyp opsRootType = rootElement.getValue();

        EhdHeaderTyp header = opsRootType.getHeader();
        EhdBodyTyp body = opsRootType.getBody();

        return OpsImportedTuple.builder()
                .ehdHeaderTyp(header)
                .ehdBodyTyp(body)
                .build();
    }
}
