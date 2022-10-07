package org.goafabric.spring.boot.examplebatch.domain;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OpsHead {

    private String id;

    private String startDate;

    private String endDate;

    private String internalVersion;

    private String kbvVersion;

}
