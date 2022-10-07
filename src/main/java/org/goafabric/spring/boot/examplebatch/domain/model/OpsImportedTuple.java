package org.goafabric.spring.boot.examplebatch.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.goafabric.spring.boot.examplebatch.domain.model.generated.EhdBodyTyp;
import org.goafabric.spring.boot.examplebatch.domain.model.generated.EhdHeaderTyp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OpsImportedTuple {

    private EhdHeaderTyp ehdHeaderTyp;
    private EhdBodyTyp ehdBodyTyp;

}
