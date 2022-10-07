package org.goafabric.spring.boot.examplebatch.domain;

import lombok.*;
import org.goafabric.spring.boot.examplebatch.domain.model.generated.EhdBodyTyp;
import org.goafabric.spring.boot.examplebatch.domain.model.generated.EhdHeaderTyp;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OpsImportTuple {

    private OpsHead opsHead;
    private List<OpsCode> opsCodes;

}
