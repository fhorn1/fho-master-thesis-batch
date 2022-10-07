package org.goafabric.spring.boot.examplebatch.ops;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.goafabric.spring.boot.examplebatch.domain.OpsHead;
import org.goafabric.spring.boot.examplebatch.domain.model.OpsImportedTuple;
import org.goafabric.spring.boot.examplebatch.domain.model.VersionHelper;
import org.goafabric.spring.boot.examplebatch.domain.model.generated.EhdHeaderTyp;
import org.joda.time.LocalDate;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.annotation.Scope;

import java.util.UUID;

@Slf4j
@Scope(value = "Step")
public class OpsHeadItemProcessor implements ItemProcessor<OpsImportedTuple, OpsHead> {

    @Override
    public OpsHead process(@NonNull OpsImportedTuple opsImportedTuple) {
        EhdHeaderTyp header = opsImportedTuple.getEhdHeaderTyp();

        LocalDate validFrom = VersionHelper.extractStartDate(header.getServiceTmr().getV());
        LocalDate validTo = VersionHelper.extractEndDate(header.getServiceTmr().getV());

        OpsHead opsHeadEo = OpsHead.builder()
                .id(UUID.randomUUID().toString())
                .startDate(validFrom.toString())
                .endDate(validTo != null ? validTo.toString() : null)
                .internalVersion(VersionHelper.versionFromDate(validFrom))
                .kbvVersion(header.getInterface().getVersion().getV())
                .build();

        return opsHeadEo;
    }

}
