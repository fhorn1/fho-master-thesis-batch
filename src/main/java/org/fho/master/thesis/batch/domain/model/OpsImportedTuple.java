package org.fho.master.thesis.batch.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fho.master.thesis.batch.domain.model.generated.EhdBodyTyp;
import org.fho.master.thesis.batch.domain.model.generated.EhdHeaderTyp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OpsImportedTuple {

    private EhdHeaderTyp ehdHeaderTyp;
    private EhdBodyTyp ehdBodyTyp;

}
