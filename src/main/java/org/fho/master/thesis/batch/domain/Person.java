package org.fho.master.thesis.batch.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Person {
    private String id;
    private String catalogVersion;

    private String lastName;
    private String firstName;
}
