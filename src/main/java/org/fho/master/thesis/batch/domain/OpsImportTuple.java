package org.fho.master.thesis.batch.domain;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OpsImportTuple {

    private OpsHead opsHead;
    private List<OpsCode> opsCodes;

}
