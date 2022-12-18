package org.fho.master.thesis.batch.ops.processor;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.fho.master.thesis.batch.domain.OpsCode;
import org.fho.master.thesis.batch.domain.model.VersionHelper;
import org.fho.master.thesis.batch.domain.model.generated.Opscode;
import org.joda.time.LocalDate;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.annotation.Scope;

import java.util.UUID;

@Slf4j
@Scope(value = "Step")
public class OpsCodeItemProcessor implements ItemProcessor<Opscode, OpsCode> {

    @Override
    public OpsCode process(@NonNull Opscode opscode) {
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

        return opCode;
    }

}
