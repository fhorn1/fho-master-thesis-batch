package org.goafabric.spring.boot.examplebatch.ops;


import lombok.extern.slf4j.Slf4j;
import org.goafabric.spring.boot.examplebatch.domain.model.OpsImportedTuple;
import org.goafabric.spring.boot.examplebatch.utilities.OpsCatalogXMLImporter;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

@Slf4j
public class OpsHeadItemReader implements ItemReader<OpsImportedTuple> {
    final Resource resource;

    private static boolean ALREADY_READ = false;

    @Autowired
    private OpsCatalogXMLImporter opsCatalogXMLImporter;

    public OpsHeadItemReader(Resource resource) {
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
