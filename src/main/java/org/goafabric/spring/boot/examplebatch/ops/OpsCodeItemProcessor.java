package org.goafabric.spring.boot.examplebatch.ops;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.goafabric.spring.boot.examplebatch.domain.OpsCode;
import org.goafabric.spring.boot.examplebatch.domain.model.OpsImportedTuple;
import org.goafabric.spring.boot.examplebatch.domain.model.VersionHelper;
import org.goafabric.spring.boot.examplebatch.domain.model.generated.EhdBodyTyp;
import org.goafabric.spring.boot.examplebatch.domain.model.generated.Opscode;
import org.goafabric.spring.boot.examplebatch.domain.model.generated.OpscodeListe;
import org.joda.time.LocalDate;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
public class OpsCodeItemProcessor implements ItemProcessor<OpsImportedTuple, List<OpsCode>> {

    @Override
    public List<OpsCode> process(@NonNull OpsImportedTuple opsImportedTuple) {
        EhdBodyTyp body = opsImportedTuple.getEhdBodyTyp();

        List<OpsCode> opsCodes = new ArrayList<>();

        for (OpscodeListe any :  body.getAny()) {
            for (Opscode opscode : any.getOpscode()) {
                OpsCode opCode = new OpsCode();
                opCode.setId(UUID.randomUUID().toString());

                opCode.setOpCode(opscode.getV());
                opCode.setDescription(opscode.getDN());

                opCode.setStartDate(VersionHelper.extractStartDate(opscode.getGueltigkeit().getV()).toString());
                LocalDate endDate = VersionHelper.extractEndDate(opscode.getGueltigkeit().getV());
                opCode.setEndDate(endDate != null ? endDate.toString() : null);

                if (opscode.getKzmedbegruendung() != null && "J".equals(opscode.getKzmedbegruendung().getV())) {
                    opCode.setMedicalMotivation(true);
                }

                opCode.setSideLocation("J".equals(opscode.getKzseite().getV()));

                if (opscode.getKz115B() != null) {
                    opCode.setCategory(opscode.getKz115B().getV());
                    if ("s".equals(opscode.getKz115B().getV()) && opscode.getKz115BInfo() != null) {
                        opCode.setCategoryInfo(opscode.getKz115BInfo().getV());
                    }
                }

                opsCodes.add(opCode);
            }
        }

        return opsCodes;
    }

}
