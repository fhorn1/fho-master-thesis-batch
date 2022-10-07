package org.goafabric.spring.boot.examplebatch.ops;


import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Unmarshaller;
import lombok.extern.slf4j.Slf4j;
import org.goafabric.spring.boot.examplebatch.domain.model.OpsImportedTuple;
import org.goafabric.spring.boot.examplebatch.domain.model.generated.EhdBodyTyp;
import org.goafabric.spring.boot.examplebatch.domain.model.generated.EhdHeaderTyp;
import org.goafabric.spring.boot.examplebatch.domain.model.generated.ObjectFactory;
import org.goafabric.spring.boot.examplebatch.domain.model.generated.OpsRootTyp;
import org.goafabric.spring.boot.examplebatch.utilities.OpsCatalogXMLImporter;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;

@Slf4j
public class OpsCodeItemReader implements ItemReader<OpsImportedTuple> {
    final Resource resource;

    private static boolean ALREADY_READ = false;

    @Autowired
    private OpsCatalogXMLImporter opsCatalogXMLImporter;

    public OpsCodeItemReader(Resource resource) {
        this.resource = resource;
    }

    @Override
    public OpsImportedTuple read() throws Exception {
        if (ALREADY_READ) {
            return null;
        }
        ALREADY_READ = true;
        return opsCatalogXMLImporter.importXml(resource.getInputStream());
    }
}
