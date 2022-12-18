package org.fho.master.thesis.batch.ops.processor;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.fho.master.thesis.batch.domain.OpsHead;
import org.fho.master.thesis.batch.domain.model.VersionHelper;
import org.fho.master.thesis.batch.domain.model.generated.EhdHeaderTyp;
import org.joda.time.LocalDate;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.annotation.Scope;

import javax.xml.bind.JAXBElement;
import java.util.UUID;

@Slf4j
@Scope(value = "Step")
public class OpsHeadItemProcessor implements ItemProcessor<JAXBElement<EhdHeaderTyp>, OpsHead> {

    @Override
    public OpsHead process(@NonNull JAXBElement<EhdHeaderTyp> rootElement) {
        EhdHeaderTyp header = rootElement.getValue();

        LocalDate validFrom = VersionHelper.extractStartDate(header.getServiceTmr().getV());
        LocalDate validTo = VersionHelper.extractEndDate(header.getServiceTmr().getV());

        return OpsHead.builder()
                .id(UUID.randomUUID().toString())
                .startDate(validFrom.toString())
                .endDate(validTo != null ? validTo.toString() : null)
                .internalVersion(VersionHelper.versionFromDate(validFrom))
                .kbvVersion(header.getInterface().getVersion().getV())
                .build();
    }

}
